/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.dictionaryrepresentation.ds3;

/**
 * Implementation of the techniques described in the paper:
 *  E. Elhamifar, G. Sapiro and S. Sastry, Dissimilarity-based Sparse Subset Selection,
 * to appear in IEEE Transactions on Pattern Analysis and Machine Intelligence (TPAMI), 2015
 * Based on http://www.ccs.neu.edu/home/eelhami/codes.htm
 */

import br.com.methods.utils.Util;
import br.com.representative.dictionaryrepresentation.smrs.SMRS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Windows
 */
public class DS3 {
    
    private final double[][] D;
    private int[] representatives;
    
    public DS3(double[][] D) {
        this.D = D;
    }
   
    private void execute() {
        double max = Double.MIN_VALUE;
        
        for( int i = 0; i < D.length; ++i )
            for( int j = 0; j < D[0].length; ++j )
                D[i][j] = D[i][j]/max;
        
        int p = 2;
        double[] CFD = new double[D.length];
        Arrays.fill(CFD, 1.0);
        double[] rhov = computeRegularizer(D, p);
        double rho = rhov[1];
        double mu = 1*10e-1;
        int maxIter = 3000;
        double errThr = 1*10e-7;        
        
        double[][] Z = ds3(D, p, rho, mu, maxIter, CFD, errThr);
        formRepresentatives(Z);
    }
    
    private double[][] ds3(double[][] D, int p, double rho, double mu, int maxIter, double[] CFD, double errThr)
    {
        int nr = D.length, nc = D[0].length;
        boolean terminate = false;
        int k = 1;
        
        double[][] aux = Util.sum(D, 1);
        int idx = 0;        
        for( int i = 0; i < aux.length; ++i )
            if( aux[i][0] < aux[idx][0] )
                idx = i;
        double[][] C1 = Util.createMatrix(D.length, D[0].length, 0);
        for( int i = 0; i < C1[0].length; ++i )
            C1[idx][i] = 1;
        double[][] Lambda = Util.createMatrix(nr, nc, 0);
        
        for( int i = 0; i < CFD.length; ++i )
            CFD[i] = (rho/mu)*CFD[i];
        
        double[][] C2 = null;
        while( !terminate ) {
            double[][] Z = shrinkL1Lq(Util.minus(C1, Util.multiply(1.0/mu, Util.sum(Lambda, D))), CFD, p);
            C2 = bclsSolver(Util.sum(Z, Util.multiply(1.0/mu, Lambda)));
            
            Lambda = Util.sum(Lambda, Util.multiply(mu, Util.minus(Z, C2)));
            
            double err1 = errorCoef(Z, C2);
            double err2 = errorCoef(C1, C2);
            
            if( k >= maxIter || (err1 <= errThr && err2 <= errThr) ) {
                terminate = true;
                System.out.printf("Terminating: \n");
                System.out.printf("||Z-C||= %1.2f, ||C1-C2||= %1.2f, iteration = %.0f \n\n",err1,err2,k);
            } else {
                k++;
                if( k%100 == 0 ) 
                    System.out.printf("||Z-C||= %1.2f, ||C1-C2||= %1.2f, iteration = %.0f \n",err1,err2,k);                    
            }
            
            C1 = C2;
        }
        
        return C2;
    }
    
    private double[][] bclsSolver(double[][] U) {
        int m = U.length, n = U[0].length;
        
        double[][] V = Util.sort(U);
        
        List<Integer> activeSet = new ArrayList<>();
        for( int i = 0; i < n; ++i )
            activeSet.add(i);
        
        double[] theta = new double[n];
        Arrays.fill(theta, 0);
        
        int i = 1;
        while( !activeSet.isEmpty() && i <= m ) {
            List<Integer> idx = new ArrayList<>();
            
            double[][] auxSum = Util.sum(Util.select(Util.range(V, 0, i, 0, n), activeSet), 0);
            for( int j = 0; j < activeSet.size(); ++j )
                if( V[i][activeSet.get(j)] - (auxSum[0][j]-1.0)/(double)i <= 0 )
                    idx.add(activeSet.get(j));
            
            if( !idx.isEmpty() ) {
                double[][] filteredVsum = Util.sum(Util.select(Util.range(V, 0, i-1, 0, n), idx), 0);

                for( int j = 0; j < idx.size(); ++j )
                    theta[idx.get(j)] = (filteredVsum[0][j]-1.0)/(double)(i-1.0);

                activeSet.removeAll(idx); 
            }
            i++;
        }
        
        if( !activeSet.isEmpty() ) {
            double[][] filteredVsum = Util.sum(Util.select(Util.range(V, 0, m, 0, n), activeSet), 0);

            for( int j = 0; j < activeSet.size(); ++j )
                theta[activeSet.get(j)] = (filteredVsum[0][j]-1.0)/(double)m;
        }
        
        double[][] C = Util.minus(U, Util.repmatRow(theta, m));
        for( i = 0; i < C.length; ++i )
            for( int j = 0; j < C[0].length; ++j )
                C[i][j] = Math.max(C[i][j], 0);
        
        return C;
    }
    
    // we will use q == 2 since it's faster and produce acceptable results
    private double[] computeRegularizer(double[][] D, int q) {
        
        int nr = D.length, nc = D[0].length;
        double[][] s = Util.sum(D, 1);
        int idx = 0;
        for( int i = 0; i < s.length; ++i )
            idx = s[i][0] > s[idx][0] ? i : idx;
        
        double rhoMax = Double.MIN_VALUE, rhoMin;
        int[] idxC = new int[nr-1];
        for( int i = 0, j = 0; i < nr; ++i )
            if( i != idx )
                idxC[j++] = i;
        
        double[] v = new double[D[0].length];
        double sqrtNr = Math.sqrt(nr);
        for( int i = 0; i < idxC.length; ++i ) {
            
            for( int j = 0; j < v.length; ++j )
                v[j] = D[idxC[i]][j] - D[idx][j];
            
            double p = sqrtNr*(Util.norm(v)*Util.norm(v)) / (2*Arrays.stream(v).sum());
            rhoMax = Math.max(rhoMax, p);            
        }
        
        if( nr == nc )
            rhoMin = Util.min(Util.sum(D, Util.eye(nr, Math.pow(10, 10))));
        else
            rhoMin = Util.min(D);
        
        double[] minAndMaxRho = {rhoMin, rhoMax};
        return minAndMaxRho;
    }
    
    private double[][] shrinkL1Lq(double[][] C1, double[] lambda, int q) {
        
        int d = C1.length;
        int n = C1[0].length;
        
        double[] r = new double[d];

        double[][] sumAux = Util.sum(Util.multiply(C1, C1), 1);
        
        for( int i = 0; i < r.length; ++i )
            r[i] = Math.max(Math.sqrt(sumAux[i][0])-lambda[i], 0);

        for( int i = 0; i < r.length; ++i )
            r[i] = r[i]/(r[i]+lambda[i]);
        double[][] aux = Util.reapmat(r, n);

        double[][] C2 = new double[C1.length][C1[0].length];
        for( int i = 0; i < C1.length; ++i )
            for( int j = 0; j < C1[0].length; ++j )
                C2[i][j] = aux[i][j]*C1[i][j];
        return C2;
        
    }
    
    private double errorCoef(double[][] Z, double[][] C) {        
        double error = 0;        
        for( int i = 0; i < C.length; ++i )
            for( int j = 0; j < C[0].length; ++j )
                error += Math.abs(Z[i][j]-C[i][j]);
        
        return error/(C.length*C[0].length);
    }
    
    public int[] getRepresentatives() {
        return representatives;
    }

    private void formRepresentatives(double[][] C) {
        double ratio = 0.1;
        int n = C.length;
        
        double[] r = new double[n];        
        for( int i = 0; i < n; ++i ) 
            r[i] = Util.infNorm(C[i]);
        
        double rnorm = Util.infNorm(r);
        List<Integer> sInd = new ArrayList<>();
        for( int i = 0; i < r.length; ++i ) 
            if( r[i] >= ratio*rnorm ) 
                sInd.add(i);
        
        Item[] v = new Item[sInd.size()];
        for( int i = 0; i < v.length; ++i ) {
            v[i].value = Util.norm(C[sInd.get(i)]);
            v[i].index = sInd.get(i);
        }
        
        Arrays.sort(v, (Item a, Item b)->{ return (a.value < b.value ? 1 : (a.value > b.value ? -1 : 0)); });
        representatives = new int[v.length];
        for( int i = 0; i < v.length; ++i ) {
            representatives[i] = v[i].index;
        }
        
    }
    
    
      private class Item {
        public double value;
        public int index;
        
        public Item(double value, int index) {
            this.value = value;
            this.index = index;
        }
    }
}

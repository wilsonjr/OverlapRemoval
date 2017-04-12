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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Windows
 */
public class DS3 {
    
    private final double[][] items;
    private int[] representatives;
    
    public DS3(List<? extends List<Double>> items) {
        this.items = Util.elementMatrix(items);
    }
   
    private void execute() {
        
    }
    
    private double[][] ds3(double[][] D, int p, double rho, double mu, int maxIter, double[] CFD)
    {
        
        
        
        
        
        
        return null;
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
    
    private double[][] shrinkL1Lq(double[][] C1, double lambda, int q) {
        
        int d = C1.length;
        int n = C1[0].length;
        
        if( q == 1 ) {
            double[][] C2 = new double[C1.length][C1[0].length];
            
            for( int i = 0; i < C1.length; ++i )
                for( int j = 0; j < C1[0].length; ++j ) {
                    C2[i][j] = Math.abs(C1[i][j])-lambda;
                    C2[i][j] = Math.max(C2[i][j], 0);                    
                    C2[i][j] = C2[i][j]*Util.sign(C1[i][j]);                   
                }            
            return C2;
        } else {
            
            double[] r = new double[d];
            
            for( int i = 0; i < r.length; ++i )
                r[i] = Math.max(Util.norm(C1[i])-lambda, 0);
            
            for( int i = 0; i < r.length; ++i )
                r[i] = r[i]/(r[i]+lambda);
            double[][] aux = Util.reapmat(r, n);
            
            double[][] C2 = new double[C1.length][C1[0].length];
            for( int i = 0; i < C1.length; ++i )
                for( int j = 0; j < C1[0].length; ++j )
                    C2[i][j] = aux[i][j]*C1[i][j];
            return C2;
        }
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
}

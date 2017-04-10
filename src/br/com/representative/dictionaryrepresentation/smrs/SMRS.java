/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.dictionaryrepresentation.smrs;

import br.com.methods.utils.Util;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Windows
 */
public class SMRS {
    
    private double[][] items;
    
    public SMRS(List<? extends List<Double>> items) {
        this.items = Util.elementMatrix(items);
    }
    
    public void execute() {
        
        // setting up some parameters, using parameters proposed by the author of the technique
        double alpha = 5;
        double r = 0;
        double q = 2;
        
        double[] regParam = {alpha, alpha};
        boolean affine = true;
        double thr = Math.pow(10, -7);
        int maxIter = 5000;
        double thrS = 0.99, thrP = 0.95;
        int N = items[0].length;
        
        double[][] Y = Arrays.stream(items)
                .map((double[] row)->row.clone())
                .toArray((int length)->new double[length][]);
        
        double[] meanVector = Util.mean(Y, 1);
        // meanVector is treated as a column
        Y = Util.minus(Y, Util.reapmat(meanVector, N));
        
        double[][] C = almLasso(Y, affine, regParam, q, thr, maxIter);
        
        int[] indexes = findRep(C, thrS, q);
        
    }

    private double[][] almLasso(double[][] Y, boolean affine, double[] regParam, double q, double thr, int maxIter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private int[] findRep(double[][] C, double thr, double q) {
        
        int n = C.length;
        Item[] r = new Item[n];
        for( int i = 0; i < n; ++i ) 
            r[i] = new Item(Util.norm(C[i], q), i);
        
        Arrays.sort(r, (Item a, Item b)->{ return (a.value > b.value ? 1 : (a.value < b.value ? -1 : 0)); });
        double sumOfR = Arrays.stream(r).mapToDouble((Item i)->i.value).sum();
        
        double sum = 0;
        int i = 0;
        for( ; i < n; ++i ) {
            sum += r[i].value;
            if( sum/sumOfR > thr )
                break;
        }
        
        return Arrays.copyOfRange(Arrays.stream(r).mapToInt((Item item)->item.index).toArray(), 0, i+1);
    }
    
    private double computeLambda(double[][] Y, boolean affine) {
        
        int n = Y[0].length;
        double lambda = 0;
        
        if( !affine ) {
            double[] T = new double[n];
            double max = Double.MIN_VALUE;
            
            for( int i = 0; i < T.length; ++i ) {
                double[] yi = Util.copyColumn(Y, i);
                double[] yiY = Util.multiply(yi, Y);
                T[i] = Util.norm(yiY);    
                max = Math.max(max, T[i]);
            }
                
            lambda = max;
        } else {
            double[] T = new double[n];
            double max = Double.MIN_VALUE;
            
            // equivalent
            // ymean = mean(Y,2);
            // (ymean*ones(1,N)-Y))
            double[] ymean = Util.mean(Y, 1);
            double[][] aux = Util.reapmat(ymean, n);
            double[][] ymeanOnesMinusY = Util.minus(aux, Y);
            
            
            for( int i = 0; i < T.length; ++i ) {
                double[] yi = Util.copyColumn(Y, i);
                double[] yiY = Util.multiply(yi, ymeanOnesMinusY);
                T[i] = Util.norm(yiY);
                max = Math.max(max, T[i]);
            }
                
            lambda = max;
        }
        
        return lambda;
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
    
    private class Item {
        public double value;
        public int index;
        
        public Item(double value, int index) {
            this.value = value;
            this.index = index;
        }
    }
    
}

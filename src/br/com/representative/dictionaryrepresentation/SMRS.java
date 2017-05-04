/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.dictionaryrepresentation;

/**
 * Implementation of the techniques described in the paper:
 *  E. Elhamifar, G. Sapiro, and R. Vidal, See All by Looking at A Few: Sparse Modeling for Finding Representative Objects,
 * IEEE Conference on Computer Vision and Pattern Recognition (CVPR), 2012.
 * Based on http://www.ccs.neu.edu/home/eelhami/codes.htm
 */

import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Windows
 */
public class SMRS extends SparseRepresentation {
    private final double[][] items;
    
    public SMRS(List<? extends List<Double>> items) {
        super(5);
        this.items = Util.elementMatrix(items);
    }
    
    @Override
    public void execute() {
       
        // setting up some parameters, using parameters proposed by the author of the technique
        int q = 2;
        
        double[] regParam = {alpha, alpha};
        boolean affine = true;
        double thr = Math.pow(10, -7);
        int maxIter = 5000;
        double thrS = 0.99;
        int N = items[0].length;
        
        double[][] Y = Arrays.stream(items)
                .map((double[] row)->row.clone())
                .toArray((int length)->new double[length][]);
        
        double[] meanVector = Util.mean(Y, 1);
        // meanVector is treated as a column
        Y = Util.minus(Y, Util.reapmat(meanVector, N));        
        double[][] C = almLasso(Y, affine, regParam, q, thr, maxIter);
        
        representatives = findRep(C, thrS, q);
    }

    private double[][] almLasso(double[][] Y, boolean affine, double[] alpha, int q, double thr, int maxIter) {
        double alpha1 = alpha[0];
        double alpha2 = alpha[1];
        double thr1 = thr, thr2 = thr;
        
        int d = Y.length, n = Y[0].length;
        double mu1p = alpha1 * 1.0/computeLambda(Y, affine);
        double mu2p = alpha2 * 1.0;
        if( !affine ) {
            double mu1 = mu1p;
            double mu2 = mu2p;
            double[][] P = Util.multiply(Util.transposed(Y), Y);
            double[][] mu1P = Util.multiply(mu1, P);
            double[][] Atemp = Util.sum(mu1P, Util.multiply(mu2, Util.eye(n, 1)));
            double[][] A = Util.inverse(Atemp);
            
            double[][] C1 = Util.createMatrix(n, n, 0.0);
            double[][] Lambda2 = Util.createMatrix(n, n, 0.0);
            
            double err1 = 10*thr1;
            int i = 1;
            double[][] C2 = null;
            while( err1 > thr1 && i < maxIter ) {
                
                double[][] Z = Util.multiply(A, Util.sum(mu1P, Util.minus(Util.multiply(mu2, C1), Lambda2)));
                // same as Z+Lambda2./mu2
                C2 = shrinkL1Lq(Util.sum(Z, Util.multiply(1.0/mu2, Lambda2)), 1.0/mu2, q);
                
                Lambda2 = Util.sum(Lambda2, Util.multiply(mu2, Util.minus(Z, C2)));
                    
                err1 = errorCoef(Z, C2);
                
                C1 = C2;
                i++;
                if( i%100 == 0 )
                    System.out.printf("Iteration %5d, ||Z-C|| = %2.5f \n", i, err1);
            }
            
            System.out.printf("Terminating ADMM at iteration %5d, \n ||Z - C|| = %2.5f, \n",i,err1);
            
            
            return C2;
        } else {
            double mu1 = mu1p;
            double mu2 = mu2p;
          
            double[][] P = Util.multiply(Util.transposed(Y), Y);
            double[][] mu1P = Util.multiply(mu1, P);
            double[][] Atemp = Util.sum(Util.sum(mu1P, Util.multiply(mu2, Util.eye(n, 1))), Util.multiply(mu2, Util.createMatrix(n, n, 1.0)));
            double[][] A = Util.inverse(Atemp);
            
            double[][] C1 = Util.createMatrix(n, n, 0.0);   
            double[][] Lambda2 = Util.createMatrix(n, n, 0.0);
            double[] lambda3 = new double[n];
            Arrays.fill(lambda3, 0.0);
            
            double err1 = 10*thr1, err2 = 10*thr2;
            int i = 1;
            double[][] C2 = null;
            while( (err1 > thr1 || err2 > thr1) && i < maxIter ) {
                
                double[][] Z = Util.multiply(A, 
                                             Util.sum(
                                                      Util.multiply(mu1, P), 
                                                      Util.sum(
                                                               Util.multiply(
                                                                             mu2, 
                                                                             Util.minus(C1, Util.multiply(1.0/mu2, Lambda2))
                                                                             ), 
                                                               Util.sum(
                                                                        Util.multiply(mu2, Util.createMatrix(n, n, 1.0)), 
                                                                        Util.repmatRow(lambda3, n)
                                                                        )
                                                               )
                                                     ));
                C2 = shrinkL1Lq(Util.sum(Z, Util.multiply(1.0/mu2, Lambda2)), 1.0/mu2, q);
                
                Lambda2 = Util.sum(Lambda2, Util.multiply(mu2, Util.minus(Z, C2)));
                double[][] sumZ1 = Util.sum(Z, 0);
                for( int j = 0; j < sumZ1[0].length; ++j ) {
                    lambda3[j] = mu2 * (1-sumZ1[0][j]) + lambda3[j];
                }
                err1 = errorCoef(Z, C2);
                err2 = errorCoef(sumZ1, Util.createMatrix(1, n, 1.0));
                
                C1 = C2;
                i++;
                if( i%100 == 0 )
                    System.out.println("Iteration "+i+", ||Z - C|| = "+err1+", ||1 - C^T 1|| = "+err2);
            }
            
            System.out.println("Terminating ADMM at iteration "+i+",\n ||Z - C|| = "+err1+", ||1 - C^T 1|| = "+err2);
            return C2;
        }
    }

    private int[] findRep(double[][] C, double thr, double q) {
        
        int n = C.length;
        Item[] r = new Item[n];
        for( int i = 0; i < n; ++i ) 
            r[i] = new Item(Util.norm(C[i], q), i);
        
        Arrays.sort(r, (Item a, Item b)->{ return (a.value < b.value ? 1 : (a.value > b.value ? -1 : 0)); });
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
        double lambda;
        
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
    
    
        
    private class Item {
        public double value;
        public int index;
        
        public Item(double value, int index) {
            this.value = value;
            this.index = index;
        }
    }
    
}

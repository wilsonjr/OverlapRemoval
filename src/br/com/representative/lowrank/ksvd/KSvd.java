/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.lowrank.ksvd;

import Jama.Matrix;
import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;

/**
 *
 * @author wilson
 */
public class KSvd extends RepresentativeFinder {
    private final double[][] items;
    private final int dictsize;
    private double[][] D;
    private final double[][] itemsc;
    
    public KSvd(List<? extends List<Double>> items, int dictsize) {
        this.items = Util.elementMatrix(items);
        this.itemsc = new double[this.items.length][this.items[0].length];
        this.dictsize = dictsize;
    }
    
    @Override
    public void execute() {
        
        D = initialDict();        
        int maxiter = 30, n = D.length;
        
        double[] gamma;
        double[] y = new double[n];
        double previousError = 0, error = 0;
        int count;
        for( count = 0; count < maxiter; ++count ) {
            
            // sparse coding stage
            double[][] gammai = new double[dictsize][items[0].length];
            for( int i = 0; i < items[0].length; ++i ) {
                for( int k = 0; k < n; ++k )
                    y[k] = items[k][i];
                
                // notice that 'm' must be <= number of features, here we use m = n/2
                gamma = omp(D, y, 1e-5, D[0].length);
                
                for( int k = 0; k < dictsize; ++k )
                    gammai[k][i] = gamma[k];                
            }
            
            /** codebook update stage **/            
            // for each column k = 1, 2, ..., k, in D^(j-1)
            // note that D^(j-1) is the current dictionary
           
            for( int k = 0; k < D[0].length; ++k ) {
                
                // define the group of examples that use this atom
                List<Integer> wk = new ArrayList<>();
                for( int i = 0; i < gammai[0].length; ++i ) {
                    if( gammai[k][i] != 0.0 )
                        wk.add(i);
                }
                
              //  System.out.println("Finished step 1");
                
                if( !wk.isEmpty() ) {
                    // compute the overall representation error matrix, Ek = Y - sum(j!=k) d_jx^j_T
                    double[][] dict_temp = new double[D.length][D[0].length];                
                    removeColumn(dict_temp, k, D);
                    
                    error = computeError(Util.minus(items, Util.multiply(D, gammai)));
                   
                    double[][] items_temp = copyFromIndex(items, wk);                    
                    double[][] gammaiIndex = copyFromIndex(gammai, wk);
                                        
                    double[][] DGammaiIndex = Util.multiply(dict_temp, gammaiIndex);
                    
                    double[][] EkR = Util.minus(items_temp, DGammaiIndex);

                   // System.out.println("Finished step 2");

                    // Apply SVD decomposition EkR = USVT. Choose the updated dictionary column d'k to be the dist column of U. Update the coefficient
                    // vector xkr (the selected rows according to wk) to be the first column of V mulitiplied by S(1,1), the greatest singular value

                    DenseMatrix64F A = new DenseMatrix64F(EkR);
                    SingularValueDecomposition svd =  DecompositionFactory.svd(EkR.length, EkR[0].length, 
                                                                               true, true, false);
                    if( svd.decompose(A) ) {
                        double delta = ((DenseMatrix64F)svd.getW(null)).get(0, 0);

                        for( int i = 0; i < D.length; ++i ) 
                            D[i][k] = ((DenseMatrix64F)svd.getU(null, false)).get(i, 0);

                        //update gamma for the next iterations
                        for( int i = 0; i < wk.size(); ++i )
                            gammai[k][wk.get(i)] = ((DenseMatrix64F)svd.getV(null, false)).get(i, 0)*delta;
                        
                      //  System.out.println("Finished step 4");
                        
                    } else {
                        System.err.println("It doesn't perform SVD.");
                        return;
                    }
                 //   System.out.println("Finished step 4");
                }
                
            }
            
            if( Math.abs(error-previousError) <= 0.0000001 )
                break;
            previousError = error;
            
        }
               
        System.err.println("K-SVD execution finished with "+count+" iteration(s)");
        System.err.println("Selecting representatives...");
        formRepresentatives();
        System.err.println("Done.");
        
    }

    private void removeColumn(double[][] dict_temp, int k, double[][] D) {
        for( int i = 0; i < dict_temp.length; ++i )
            for( int j = 0; j < dict_temp[0].length; ++j )
                dict_temp[i][j] = (j == k ? 0 : D[i][j]); // "remove" column k
    }
    
    private double[][] initialDict() {
        
        int n = items.length;        
        double[][] dict = new double[n][dictsize];
        
        List<Integer> ids = new ArrayList<>();
        for( int i = 0; i < items[0].length; ++i )
            ids.add(i);
        
        Collections.shuffle(ids);
                
        for( int i = 0; i < n; ++i )            
            for( int j = 0; j < dictsize; ++j ) {
                dict[i][j] = items[i][ids.get(j)];                              
            }
        
        // normalize columns in l^2
        for( int j = 0; j < dictsize; ++j ) {
            double norm = 0;
            for( int i = 0; i < n; ++i )
                norm += dict[i][j]*dict[i][j];
            
            norm = 1.0/Math.sqrt(norm);
            for( int i = 0; i < n; ++i )
                dict[i][j] = dict[i][j]*norm;
        }
        
        for( int j = 0; j < itemsc[0].length; ++j ) {
            double norm = 0;
            for( int i = 0; i < items.length; ++i )
                norm += items[i][j]*items[i][j];
            
            norm = 1.0/Math.sqrt(norm);
            for( int i = 0; i < n; ++i )
                itemsc[i][j] = items[i][j]*norm;
        }
        
        return dict;
    }
    
    private double[] omp(double[][] D, double[] y, double eps, int m) {
        
        double x[] = Arrays.copyOf(y, y.length);
        
        double normx2 = Util.innerProduct(x, x);
        double normr2 = normx2;
        int natoms = m;
        double normtol2 = eps*normx2;
        
        double[] residual = Arrays.copyOf(x, x.length);
        List<Integer> indexes = new ArrayList<>();
        
        int k = 1;
        double[] stemp = null;
        while( normr2 > normtol2 && k++ <= natoms ) {
            
            double[][] dictT = Util.transposed(D);            
            double[] projections = Util.multiply(dictT, residual);
            for( int i = 0; i < projections.length; ++i )
                projections[i] = Math.pow(projections[i], 2.0);
            int index = Util.maxIndex(projections);
            
            indexes.add(index);
            double[][] H = copyFromIndex(D, indexes);
            
            Matrix HM = new Matrix(H);
            Matrix xM = new Matrix(x, x.length);
            Matrix stempM = HM.solve(xM);            
            stemp = new double[stempM.getRowDimension()];
            for( int i = 0; i < stemp.length; ++i )
                stemp[i] = stempM.get(i, 0);
            
            double[] Hstemp = Util.multiply(H, stemp);
            for( int i = 0; i < residual.length; ++i )
                residual[i] = x[i]-Hstemp[i];
            
            normr2 = Util.innerProduct(residual, residual);            
        }
        
        double[] gamma = new double[natoms];
        Arrays.fill(gamma, 0);
        for( int i = 0; i < stemp.length; ++i )
            gamma[indexes.get(i)] = stemp[i];
        
        
        return gamma;
    }

    private double[][] copyFromIndex(double[][] matrix, List<Integer> indexes) {
        double[][] copy = new double[matrix.length][indexes.size()];
        for( int i = 0; i < matrix.length; ++i )
            for( int j = 0; j < indexes.size(); ++j )
                copy[i][j] = matrix[i][indexes.get(j)];
        
        return copy;
    }
    
    private void formRepresentatives() {
        int count = 0;
        representatives = new int[D[0].length];
        
        for( int j = 0; j < D[0].length; ++j ) {
            double minDist = Double.MAX_VALUE;
            int index = 0;
            for( int k = 0; k < itemsc[0].length; ++k ) {
                double dist = 0;
                for( int i = 0; i < D.length; ++i ) {
                    dist += Math.pow(itemsc[i][k]-D[i][j], 2.0);
                }
                dist = Math.sqrt(dist);                
                
                if( dist < minDist ) {
                    minDist = dist;
                    index = k;
                }
            }
            representatives[count++] = index;            
        }
    }

    private double computeError(double[][] m) {
        
        double error = 0;
        for( int i = 0; i < m.length; ++i )
            for( int j = 0; j < m[0].length; ++j )
                error += m[i][j]*m[i][j];
        return Math.sqrt(error);
    }
    
         
}

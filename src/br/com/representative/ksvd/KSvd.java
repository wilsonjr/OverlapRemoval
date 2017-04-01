/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.ksvd;

import Jama.Matrix;
import br.com.methods.utils.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.ejml.interfaces.linsol.LinearSolver;

/**
 *
 * @author wilson
 */
public class KSvd {
    private double[][] items;
    private int dictsize;
    private double[] ans;
    
    public KSvd(List<? extends List<Double>> items, int dictsize) {
        this.items = Util.elementMatrix(items);
        this.dictsize = dictsize;
    }
    
    public void execute() {
        
        double D[][] = initialDict();        
        int maxiter = 1, n = D.length;
        List<Integer> ids = null;//new ArrayList<>();
        
        double[] gamma = null;
        double[] y = new double[n];
        for( int count = 0; count < maxiter; ++count ) {
            
            // sparse coding stage
            double[][] gammai = new double[dictsize][items[0].length];
            for( int i = 0; i < items[0].length; ++i ) {
                for( int k = 0; k < n; ++k )
                    y[k] = items[k][i];
                
                // notice that 'm' must be <= number of features, here we use m = n/2
                ids = new ArrayList<>();
                gamma = omp(D, y, 1e-5, 2);
                
                for( int k = 0; k < dictsize; ++k )
                    gammai[k][i] = k < gamma.length ? gamma[k] : 0;
                
            }
            
            System.out.println("dictsize: "+dictsize);
            System.out.println("Dimensions gammai: "+gammai.length+", "+gammai[0].length);
            
            
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
                
                System.out.println("Finished step 1");
                
                if( !wk.isEmpty() ) {
                    System.out.println("Not empty");
                    // compute the overall representation error matrix, Ek = Y - sum(j!=k) d_jx^j_T
                    double[][] dict_temp = new double[D.length][D[0].length];                
                    for( int i = 0; i < D.length; ++i )
                        for( int j = 0; j < D[0].length; ++j )
                            dict_temp[i][j] = (j == k ? 0 : D[i][j]); // "remove" column k

                    double[][] items_temp = new double[items.length][wk.size()];
                    for( int i = 0; i < items_temp.length; ++i )
                        for( int j = 0; j < wk.size(); ++j )
                            items_temp[i][j] = items[i][wk.get(j)];

                    double[][] gammaiIndex = new double[gammai.length][wk.size()];
                    for( int i = 0; i < gammaiIndex.length; ++i )
                        for( int j = 0; j < wk.size(); ++j )
                            gammaiIndex[i][j] = gammai[i][wk.get(j)];
                    
                    double[][] EkR = new double[D.length][wk.size()];
                    double[][] DGammaiIndex = mult(D, gammaiIndex);
                 
                    for( int i = 0; i < EkR.length; ++i )
                        for( int j = 0; j < EkR[0].length; ++j )
                            EkR[i][j] = items_temp[i][j] - DGammaiIndex[i][j];

                    System.out.println("Finished step 2");

                    System.out.println("EkR dimensions: "+EkR.length+", "+EkR[0].length);

                    // Apply SVD decomposition EkR = USVT. Choose the updated dictionary column d'k to be the dist column of U. Update the coefficient
                    // vector xkr (the selected rows according to wk) to be the first column of V mulitiplied by S(1,1), the greatest singular value

                    DenseMatrix64F A = new DenseMatrix64F(EkR);
                    SingularValueDecomposition svd =  DecompositionFactory.svd(EkR.length, EkR[0].length, true, true, false);
                    if( svd.decompose(A) ) {
                        System.out.println("FOi...");
                        double S11 = ((DenseMatrix64F)svd.getW(null)).get(0, 0);

                        for( int i = 0; i < D.length; ++i ) 
                            D[i][k] = ((DenseMatrix64F)svd.getU(null, false)).get(i, 0);

                        System.out.println("Finished step 3");

                        // the question is: Do I have to update gamma for the next iterations? I think so...
                        // well, he it is:
                        // add modifications to the other iterations
                        for( int i = 0; i < wk.size(); ++i )
                            gammai[k][wk.get(i)] = ((DenseMatrix64F)svd.getV(null, false)).get(i, 0)*S11;
                        
                        System.out.println("Finished step 4");
                        
                    } else {
                        System.out.println("NAO PASSOU PELA SVD");
                        return;
                    }
                    System.out.println("Finished step 4");
                }
                
            }
        }
        
        
        System.out.println("K-SVD execution finished");
        
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
        
        return dict;
    }
    
    private double[] omp(double[][] D, double[] y, double eps, int m) {
        
        double x[] = Arrays.copyOf(y, y.length);
        
        double normx2 = Util.innerProduct(x, x);
        double normr2 = normx2;
        int natoms = D[0].length;
        double normtol2 = eps*normx2;
        
        double[] residual = Arrays.copyOf(x, x.length);
        List<Integer> indexes = new ArrayList<>();
        
        int k = 1;
        double[] stemp = null;
        while( normr2 > normtol2 && k <= D[0].length ) {
            
            double[][] dictT = transposta(D);            
            double[] projections = prod(dictT, residual);
            for( int i = 0; i < projections.length; ++i )
                projections[i] = Math.pow(projections[i], 2.0);
            int index = getMaxIndex(projections);
            
            indexes.add(index);
            
            double[][] H = new double[D.length][indexes.size()];
            for( int i = 0; i < H.length; ++i )
                for( int j = 0; j < H[0].length; ++j )
                    H[i][j] = D[i][indexes.get(j)];
            
            Matrix HM = new Matrix(H);
            Matrix xM = new Matrix(x, x.length);
            Matrix stempM = HM.solve(xM);            
            stemp = new double[stempM.getRowDimension()];
            for( int i = 0; i < stemp.length; ++i )
                stemp[i] = stempM.get(i, 0);
            
            
            double[] Hstemp = prod(H, stemp);
            for( int i = 0; i < residual.length; ++i )
                residual[i] = x[i]-Hstemp[i];
            
            normr2 = Util.innerProduct(residual, residual);
            
            k++;
        }
        
        double[] gamma = new double[natoms];
        Arrays.fill(gamma, 0);
        for( int i = 0; i < stemp.length; ++i )
            gamma[indexes.get(i)] = stemp[i];
        
        
        return gamma;
    }
    
    private double[] prod(double[][] a, double[] b) {
        ans = new double[a.length];
        for( int i = 0; i < a.length; ++i ) {
            ans[i] = 0;
            for( int j = 0; j < a[0].length; ++j )
                ans[i] += a[i][j]*b[j];
        }
        
        return ans;
    }
    
    private int getMaxIndex(double[] elements) {
        int index = 0;
        double maior = elements[0];
        for( int i = 0; i < elements.length; ++i )
            if( elements[i] > maior ) {
                index = i;         
                maior = elements[i];
            }
        
        return index;
    }
    
    private double[][] mult(double[][] a, double[][] b) {
        double[][] r = new double[a.length][b[0].length];
        for(int i = 0;i < a.length;i++){
            for(int j = 0;j < b[0].length;j++){
               for(int k = 0;k < b.length;k++){
                  r[i][j] += a[i][k] * b[k][j];
               }
            }
            
        
        }
        
        return r;
    }
    
    private double[][] transposta(double[][] m) {
        
        double[][] mt = new double[m[0].length][m.length];
        
        for( int i = 0; i < m.length; ++i )
            for( int j =0; j < m[0].length; ++j )
                mt[j][i] = m[i][j];
        
        return mt;
    }
    
}

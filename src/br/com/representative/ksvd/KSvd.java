/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.ksvd;

import br.com.methods.utils.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.linsol.LinearSolver;

/**
 *
 * @author wilson
 */
public class KSvd {
    private double[][] items;
    private int dictsize;
    private double[] ans;
    
    public KSvd(List<List<Double>> items, int dictsize) {
        this.items = Util.elementMatrix(items);
    }
    
    public void execute() {
        
        double D[][] = initialDict();
        int j = 0;
        
        
        
    }
    
    
    
    
    private double[][] initialDict() {
        int n = items.length;        
        double[][] dict = new double[n][dictsize];
        
        List<Integer> ids = new ArrayList<>();
        for( int i = 0; i < n; ++i )
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
        
        int maxiter = 1;
        
        
        double[] gamma = new double[n];
        double[] y = new double[n];
        for( int count = 0; count < maxiter; ++count ) {
            
            // sparse coding stage
            double[][] gammai = new double[n][dictsize];
            for( int i = 0; i < items.length; ++i ) {
                for( int k = 0; k < n; ++k )
                    y[k] = items[k][i];
                
                omp(dict, y, 1e-6, 0, gamma, ids);
                
                for( int k = 0; k < n; ++k )
                    gammai[k][i] = gamma[k];
                
            }
            
            /** codebook update stage **/
            
            // for each column k = 1, 2, ..., k, in D^(j-1)
            // note that D^(j-1) is the current dictionary
            for( int k = 0; k < dict[0].length; ++k ) {
                
                // define the group of examples that use this atom
                List<Integer> wk = new ArrayList<>();
                for( int i = 0; i < gammai[0].length; ++i ) {
                    if( gammai[k][i] != 0.0 )
                        wk.add(i);
                }
                
                // compute the overall representation error matrix, Ek = Y - sum(j!=k) d_jx^j_T
                double[][] dict_temp = new double[dict.length][dict[0].length];                
                for( int i = 0; i < dict.length; ++i )
                    for( int j = 0; j < dict[0].length; ++j )
                        dict_temp[i][j] = (j == k ? 0 : dict[i][j]); // "remove" column k
                                
                double[][] items_temp = new double[items.length][wk.size()];
                for( int i = 0; i < items_temp.length; ++i )
                    for( int j = 0; j < wk.size(); ++j )
                        items_temp[i][j] = items[i][wk.get(j)];
                
                
                double[][] gT = transposta(gammai);
                double[][] gammaiT = new double[gT.length][wk.size()];
                for( int i = 0; i < gammaiT.length; ++i )
                    for( int j = 0; j < wk.size(); ++j )
                        gammaiT[i][j] = gT[i][wk.get(j)];
                
                
                double[][] EkR = mult(dict_temp, gammaiT);
                for( int i = 0; i < EkR.length; ++i )
                    for( int j = 0; j < EkR[0].length; ++j )
                        EkR[i][j] = items_temp[i][j] - EkR[i][j];
                
                
                
            
            }
            
            
            
            
            
            
            
        }
        
        
        return dict;
    }
    
    
    private void omp(double[][] D, double[] y, double eps, int m, double[] gamma, List<Integer> idx ) {
        /*  Orthogonal matching pursuit (OMP)
    
            Solves [1] min || D * gamma - x ||_2 subject to || gamma ||_0 <= m
            or     [2] min || gamma ||_0         subject to || D * gamma - x || <= eps

            Parameters
            ----------
                D, array of shape n_features x n_components
                y, vector of length n_features
                m, integer <= n_features
                eps, float (supersedes m)

        */
        
        int n = items.length;
        
        
        double[] residual = Arrays.copyOf(y, y.length);
        
        while( !topCondition(m, eps, idx, residual) ) {
            
            ans = new double[residual.length];
            double[] elements = prod(D, residual);
            for( int i = 0; i < elements.length; ++i )
                elements[i] = Math.abs(elements[i]);
            int index = getMaxIndex(elements);
            idx.add(index);
            
            LinearSolver<DenseMatrix64F> solver = LinearSolverFactory.leastSquares(n, dictsize);
            
            double[] Dindex = new double[y.length];
            gamma = new double[y.length];
            DenseMatrix64F Dden = new DenseMatrix64F(y.length, 1);
            DenseMatrix64F Yden = new DenseMatrix64F(y.length, 1);
            DenseMatrix64F gammaDen = new DenseMatrix64F(y.length, 1);
            for( int i = 0; i < Dden.numCols; ++i ) {
                Dindex[i] = D[i][index];
                Dden.set(i, 0, D[i][index]);
                Yden.set(i, 0, y[i]);
            }
                            
            solver.solve(Yden, gammaDen);
            for( int i = 0; i < gammaDen.numCols; ++i )
                gamma[i] = gammaDen.get(i, 0);
            
            
            double innerprod = Util.innerProduct(Dindex, gamma);
            for( int i = 0; i < residual.length; ++i ) {
                residual[i] = y[i] - innerprod;
            }
        }
    }

    private boolean topCondition(int m, double eps, List<Integer> idx, double[] residual) {        
        if( eps > 0 ) 
            return idx.size() == m;
        return Util.innerProduct(residual, residual) <= eps;        
    }
    
    private double[] prod(double[][] a, double[] b) {
        for( int i = 0; i < a.length; ++i ) {
            ans[i] = 0;
            for( int j = 0; j < a.length; ++j )
                ans[i] += a[i][j]*b[j];
        }
        
        return ans;
    }
    
    private int getMaxIndex(double[] elements) {
        int index = 0;
        
        for( int i = 0; i < elements.length; ++i )
            if( elements[i] > elements[index] ) 
                index = i;            
        
        return index;
    }
    
    private double[][] mult(double[][] a, double[][] b) {
        double[][] r = new double[a[0].length][b.length];
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

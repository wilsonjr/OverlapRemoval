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
    private List<List<Double>> items;
    private int dictsize;
    private double[] ans;
    
    public KSvd(List<List<Double>> items, int dictsize) {
        
    }
    
    public void execute() {
        
        double D[][] = initialDict();
        int j = 0;
        
        
        
    }
    
    
    
    
    private double[][] initialDict() {
        int n = items.size();        
        double[][] dict = new double[n][dictsize];
        
        List<Integer> ids = new ArrayList<>();
        for( int i = 0; i < n; ++i )
            ids.add(i);
        
        Collections.shuffle(ids);
                
        for( int i = 0; i < n; ++i )            
            for( int j = 0; j < dictsize; ++j ) {
                dict[i][j] = items.get(i).get(ids.get(j));                              
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
    
    
    private void omp(double[][] D, double[] y, double eps, int m) {
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
        
        int n = items.size();
        
        
        double[] residual = Arrays.copyOf(y, y.length);
        List<Integer> idx = new ArrayList<>();
        
        while( !topCondition(m, eps, idx, residual) ) {
            
            ans = new double[residual.length];
            double[] elements = prod(D, residual);
            for( int i = 0; i < elements.length; ++i )
                elements[i] = Math.abs(elements[i]);
            int index = getMaxIndex(elements);
            idx.add(index);
            
            LinearSolver<DenseMatrix64F> solver = LinearSolverFactory.leastSquares(n, dictsize);
            
            double[] Dindex = new double[y.length];
            double[] gamma = new double[y.length];
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
    
}

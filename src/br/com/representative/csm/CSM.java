/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.csm;

import br.com.methods.utils.Util;
import java.util.ArrayList;
import java.util.Arrays;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.Matrix;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;

/**
 *
 * @author wilson
 */
public class CSM {
    
    private ArrayList<ArrayList<Double>> items;
    private int c, k;
    private IndexPI[] columns;
    private int[] indexes;
    
    public CSM(ArrayList<ArrayList<Double>> items, int c, int k) {
        this.items = items;
        this.c = c;    
        this.k = k; // k == n give better results
    }
    
    public void execute() {
     
        Matrix vT = decomposeAndGetVt();        
        
        columns = new IndexPI[vT.getNumCols()];
        for( int j = 0; j < vT.getNumCols(); ++j ) { // para cada coluna de vT
            columns[j] = new IndexPI(j, 0);            
            for( int i = 0; i < k; ++i )
                columns[j].pi += ((DenseMatrix64F)vT).get(i, j) * ((DenseMatrix64F)vT).get(i, j);
        }
        
        createIndexes();  
    }

    private Matrix decomposeAndGetVt() {
        double[][] distanceMatrix =  Util.elementMatrix(items);
        DenseMatrix64F A = new DenseMatrix64F(distanceMatrix);
        SingularValueDecomposition svd =  DecompositionFactory.svd(distanceMatrix.length,
                distanceMatrix[0].length, true, true, false);
        svd.decompose(A);
        Matrix vT = svd.getV(null, true);
        return vT;
    }

    private void createIndexes() {
        Arrays.sort(columns);
        
        indexes = new int[c];
        for( int i = 0; i < c; ++i )
            indexes[i] = columns[i].index;
    }

    public int[] getRepresentatives() {
        return indexes;
    }
    
    private class IndexPI implements Comparable<IndexPI> {
        public int index;
        public double pi;
        
        public IndexPI(int index, double pi) {
            this.index = index;
            this.pi = pi;
        }        

        @Override
        public int compareTo(IndexPI o) {
            return new Double(o.pi).compareTo(pi);            
        }
        
        @Override
        public String toString() {
            return index+" "+pi;
        }
    }
    
    
}

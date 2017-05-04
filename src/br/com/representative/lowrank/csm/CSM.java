/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.lowrank.csm;

import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
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
public class CSM extends RepresentativeFinder {
    
    private final ArrayList<ArrayList<Double>> items;
    private int c, k;
    private IndexPI[] columns;
    
    public CSM(ArrayList<ArrayList<Double>> items, int c, int k) {
        this.items = items;
        this.c = c;    
       // this.k = Math.min(items.size(), items.get(0).size())/2+1; // k == n give better results
        this.k = k;
    }
    
    @Override
    public void execute() {     
        Matrix vT = decomposeAndGetVt();        
        System.out.println("size vT: "+vT.getNumRows()+" x "+vT.getNumCols());
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
        Matrix vT = svd.getV(null, false);
            
        return vT;
    }

    private void createIndexes() {
        Arrays.sort(columns);
        
        representatives = new int[c];
        for( int i = 0; i < c; ++i )
            representatives[i] = columns[i].index;
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

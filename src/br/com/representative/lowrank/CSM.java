/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.lowrank;

import java.util.Arrays;
import java.util.List;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.Matrix;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;

/**
 *
 * @author wilson
 */
public class CSM extends LowRank {
    private int c, k;
    private IndexPI[] columns;
    
    public CSM(List<? extends List<Double>> items, int c, int k) {
        super(items);
        if( c > items.size() )
            throw new RuntimeException("Number of representative greater than number of instances.");
        this.c = c;    
        this.k = k;
    }
        
    @Override
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
        DenseMatrix64F A = new DenseMatrix64F(items);
        SingularValueDecomposition svd =  DecompositionFactory.svd(items.length, items[0].length, true, true, false);
        
        svd.decompose(A);
        Matrix vT = svd.getV(null, false);
        
        System.out.println("Vt: "+vT.getNumRows()+", "+vT.getNumCols());
            
        return vT;
    }

    private void createIndexes() {
        Arrays.sort(columns);        
        representatives = new int[c];
        for( int i = 0; i < c; ++i )
            representatives[i] = columns[i].index;
    }
    
    @Override
    public void filterData(int[] indexes) {
        super.filterData(indexes);
        c = (int) (indexes.length*0.1);
        if( c == 0 )
            c = 1;
        k = indexes.length;        
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.csm;

import br.com.methods.utils.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

/**
 *
 * @author wilson
 */
public class CSM {
    
    private ArrayList<ArrayList<Double>> items;
    private int c;
    private IndexPI[] columns;
    
    public CSM(ArrayList<ArrayList<Double>> items, int c) {
        this.items = items;
        this.c = c;    
    }
    
    public void execute() {
        
        double[][] distanceMatrix = Util.elementMatrix(items);        
        RealMatrix matrix = new Array2DRowRealMatrix(distanceMatrix);
        
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
        double[][] vT = svd.getVT().getData();
        int n = svd.getU().getData().length;
        int m = vT[0].length;
        int k = Math.min(m, n)/2 + 1;
        columns = new IndexPI[vT[0].length];
        for( int j = 0; j < vT[0].length; ++j ) { // para cada coluna de vT
            columns[j] = new IndexPI(j, 0);            
            for( int i = 0; i < k; ++i )
                columns[j].pi += vT[i][j]*vT[i][j];
        }
        
        Arrays.sort(columns);        
    }

    public int[] getRepresentatives() {
        
        int[] indexes = new int[c];
        for( int i = 0; i < c; ++i )
            indexes[i] = columns[i].index;
        
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

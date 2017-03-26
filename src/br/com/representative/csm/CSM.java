/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.csm;

import br.com.methods.utils.Util;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.Matrix;
import org.ejml.factory.DecompositionFactory;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

/**
 *
 * @author wilson
 */
public class CSM {
    
    private ArrayList<ArrayList<Double>> items;
    private int c, k;
    private IndexPI[] columns;
    private double[][] truncatedU, truncatedS, truncatedVT, V;
    private double[][] d;
    
    public CSM(ArrayList<ArrayList<Double>> items, int c) {
        this.items = items;
        this.c = c;    
        this.k = items.size();//Math.min(items.size(), items.get(0).size())/2 +1;
    }
    public CSM(double[][] d, int c) {
        this.d = d;
        this.c = c;    
        this.k = 3;//d.length;//Math.min(items.size(), items.get(0).size())/2 +1;
    }
    
    public void execute() {
     
        double[][] distanceMatrix = null;
        
        if( items == null )
            distanceMatrix = d;
        else
            distanceMatrix = Util.elementMatrix(items);   
        //getTruncatedSVD(distanceMatrix, k);
//        columns = new IndexPI[truncatedVT[0].length];
//        System.out.println("Columns: "+truncatedVT[0].length);
//        System.out.println("k: "+k);
//        for( int j = 0; j < truncatedVT[0].length; ++j ) { // para cada coluna de vT
//            columns[j] = new IndexPI(j, 0);            
//            for( int i = 0; i < k; ++i )
//                columns[j].pi += truncatedVT[i][j]*truncatedVT[i][j];
//        }
//        
//        Arrays.sort(columns);        
        
        
        DenseMatrix64F A = new DenseMatrix64F(distanceMatrix);
        org.ejml.interfaces.decomposition.SingularValueDecomposition svd = 
                DecompositionFactory.svd(distanceMatrix.length, distanceMatrix[0].length, true, true, false);
        
        
        svd.decompose(A);
        Matrix U = svd.getU(null, false);
        Matrix S = svd.getW(null);
        Matrix vT = svd.getV(null, true);
        
        System.out.println("U1("+U.getNumRows()+", "+U.getNumCols()+")");
        System.out.println("S1("+S.getNumRows()+", "+S.getNumCols()+")");
        System.out.println("vT("+vT.getNumRows()+", "+vT.getNumCols()+")");
        System.out.println("k: "+k);
        columns = new IndexPI[vT.getNumCols()];
        for( int j = 0; j < vT.getNumCols(); ++j ) { // para cada coluna de vT
            columns[j] = new IndexPI(j, 0);            
            for( int i = 0; i < k; ++i )
                columns[j].pi += ((DenseMatrix64F)vT).get(i, j) * ((DenseMatrix64F)vT).get(i, j);
                //columns[j].pi += truncatedVT[i][j] * truncatedVT[i][j];
        }
        
        Arrays.sort(columns);  
        for( int i = 0 ;  i < c; ++i ) {
            System.err.println(columns[i]);
        }
        
        /*
        System.out.println("U1("+U1.getNumRows()+", "+U1.getNumCols()+")");
        System.out.println("S1("+S1.getNumRows()+", "+S1.getNumCols()+")");
        System.out.println("VT1("+VT1.getNumRows()+", "+VT1.getNumCols()+")");
                
        SimpleMatrix matA = new SimpleMatrix(distanceMatrix);
        
        SimpleSVD<SimpleMatrix> s = matA.svd();
        SimpleMatrix U2 = s.getU();
        SimpleMatrix S2 = s.getW();
        SimpleMatrix V2 = s.getV();
        Matrix VT2 = s.getSVD().getV(null, true);
        System.out.println("U("+s.getU().numRows()+", "+s.getU().numCols()+")");
        System.out.println("S("+s.getW().numRows()+", "+s.getW().numCols()+")");
        System.out.println("V("+s.getV().numRows()+", "+s.getV().numCols()+")");
        System.out.println("VT("+((DenseMatrix64F)s.getSVD().getV(null, true)).numRows+
                ", "+((DenseMatrix64F)s.getSVD().getV(null, true)).numCols+")");
        
        boolean flag = true;
        for( int i = 0; i < U1.getNumRows() && flag; ++i )
            for( int j = 0; j < U1.getNumCols() && flag; ++j )
                if( ((DenseMatrix64F)U1).get(i, j) != U2.get(i, j) ) {
                    flag = false;
                }
        if( !flag )
            System.out.println("U NOT EQUALS");
        else 
            System.out.println("U EQUALS");
        
        flag = true;
        for( int i = 0; i < S1.getNumRows() && flag; ++i )
            for( int j = 0; j < S1.getNumCols() && flag; ++j )
                if( ((DenseMatrix64F)S1).get(i, j) != S2.get(i, j) ) {
                    flag = false;
                }
        if( !flag )
            System.out.println("S NOT EQUALS");
        else 
            System.out.println("S EQUALS");
        
        flag = true;
        for( int i = 0; i < VT1.getNumRows() && flag; ++i )
            for( int j = 0; j < VT1.getNumCols() && flag; ++j ) {
                if( ((DenseMatrix64F)VT1).get(i, j) != V2.get(j, i) ) {
                    flag = false;
                }
            }
        if( !flag )
            System.out.println("VT NOT EQUALS");
        else 
            System.out.println("VT EQUALS");
        */
        
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
    
    
    public double[][] getTruncatedSVD(double[][] matrix, final int k) {
        SingularValueDecomposition svd = new SingularValueDecomposition(new Array2DRowRealMatrix(matrix));
        
        System.out.printf("U: %d x %d\n", svd.getU().getData().length, svd.getU().getData()[0].length);
        truncatedU = new double[svd.getU().getRowDimension()][k];
        svd.getU().copySubMatrix(0, truncatedU.length - 1, 0, k - 1, truncatedU);
            
        System.out.printf("S: %d x %d\n", svd.getS().getData().length, svd.getS().getData()[0].length);
        truncatedS = new double[k][k];
        svd.getS().copySubMatrix(0, k - 1, 0, k - 1, truncatedS);

         
        truncatedVT = new double[k][svd.getVT().getColumnDimension()];
        svd.getVT().copySubMatrix(0, k - 1, 0, truncatedVT[0].length - 1, truncatedVT);
        V = svd.getV().getData();
        System.out.printf("V: %d x %d\n", svd.getV().getData().length, svd.getV().getData()[0].length);
        

        RealMatrix approximatedSvdMatrix = (new Array2DRowRealMatrix(truncatedU)).multiply(new Array2DRowRealMatrix(truncatedS)).multiply(new Array2DRowRealMatrix(truncatedVT));

        return approximatedSvdMatrix.getData();
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.dictionaryrepresentation.ds3;

import br.com.methods.utils.Util;
import java.util.List;

/**
 *
 * @author Windows
 */
public class DS3 {
    
    private final double[][] items;
    private int[] representatives;
    
    public DS3(List<? extends List<Double>> items) {
        this.items = Util.elementMatrix(items);
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
    
    private double errorCoef(double[][] Z, double[][] C) {        
        double error = 0;        
        for( int i = 0; i < C.length; ++i )
            for( int j = 0; j < C[0].length; ++j )
                error += Math.abs(Z[i][j]-C[i][j]);
        
        return error/(C.length*C[0].length);
    }
    
    public int[] getRepresentatives() {
        return representatives;
    }
}

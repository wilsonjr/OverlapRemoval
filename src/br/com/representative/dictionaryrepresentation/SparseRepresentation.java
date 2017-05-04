/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.dictionaryrepresentation;

import br.com.representative.RepresentativeFinder;

/**
 *
 * @author Windows
 */
public abstract class SparseRepresentation extends RepresentativeFinder {
    
    protected double alpha; // regularization parameter;
    
    public SparseRepresentation(double alpha) {
        this.alpha = alpha;
    }
    
    protected double errorCoef(double[][] Z, double[][] C) {        
        double error = 0;        
        for( int i = 0; i < C.length; ++i )
            for( int j = 0; j < C[0].length; ++j )
                error += Math.abs(Z[i][j]-C[i][j]);
        
        return error/(C.length*C[0].length);
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.analysis;

/**
 *
 * @author wilson
 */
public class Redundancy {
 
    public static double calculate(int indexes[], double[][] similarity) {
        
        double sum = 0.0;
        
        for( int i = 0; i < indexes.length; ++i ) {
            
            double sumSimilarity = 0.0;
            for( int j = 0; j < indexes.length; ++j )
                sumSimilarity += similarity[indexes[j]][indexes[i]];            
            
            sum += (1.0 - 1.0/sumSimilarity);            
        }
        
        return sum/similarity.length;    
    }
    
}

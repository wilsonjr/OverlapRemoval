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
public class Redundancy implements RepresentativeAnalysis {
    
    private int[] indexes;
    private double[][] similarity;
    
    public Redundancy(int[] indexes, double[][] similarity) {
        this.indexes = indexes;
        this.similarity = similarity;                 
    }
    
    @Override
    public double init() {
        
        double sum = 0.0;
        
        for( int i = 0; i < indexes.length; ++i ) {
            
            double sumSimilarity = 0.0;
            for( int j = 0; j < indexes.length; ++j )
                sumSimilarity += similarity[indexes[j]][indexes[i]];            
            
            sum += (1.0 - 1.0/sumSimilarity);            
        }
        
        return sum/similarity.length;    
    }
    
    @Override
    public String toString() {
        return "Redundancy";
    }
    
}

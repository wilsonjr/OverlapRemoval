/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.analysis;

/**
 *
 * @author wilson
 */
public class Coverage implements RepresentativeAnalysis {
    
    private int[] indexes;
    private double[][] similarity;
    
    public Coverage(int indexes[], double[][] similarity) {
        this.indexes = indexes;
        this.similarity = similarity;
    }

    @Override
    public double init() {
        double sum = 0.0;
        
        for( int i = 0; i < similarity.length; ++i ) {
            
            double max = -1.0;
            for( int j = 0; j < indexes.length; ++j )
                max = Double.max(max, similarity[indexes[j]][i]);
            
            sum += max;
        }
        
        return sum/similarity.length;
    }
    
    @Override
    public String toString() {
        return "Coverage";
    }
    
}

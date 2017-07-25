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
public class Coverage {
    
    public static double calculate(int indexes[], double[][] similarity) {
        
        double sum = 0.0;
        
        for( int i = 0; i < similarity.length; ++i ) {
            
            double max = -1;
            for( int j = 0; j < indexes.length; ++j )
                max = Double.max(max, similarity[i][indexes[j]]);
            
            sum += max;
        }
        
        return sum/similarity.length;
    }
    
}

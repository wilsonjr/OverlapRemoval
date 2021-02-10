/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.overlayanalisys.neighorhoodpreservation;

import br.com.methods.utils.KNN;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Pair;
import br.com.overlayanalisys.definition.Metric;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author wilson
 */
public class NeighborhoodPreservation implements Metric {
    
    private int n_neighbors;
    private List<Double> np;
    
    public NeighborhoodPreservation(int n_neighbors) {
        
        this.n_neighbors = n_neighbors;
        this.np = new ArrayList<>();
    }

    @Override
    public double execute(ArrayList<OverlapRect> pts1, ArrayList<OverlapRect> pts2) {
        double mean_preservation = 0.0;
        
        for( int i = 1; i <= n_neighbors; ++i ) {
            
            
            KNN knn_pts1 = new KNN(i);
            KNN knn_pts2 = new KNN(i);
            
            Pair[][] neighbors_pts1 = knn_pts1.execute(pts1);
            Pair[][] neighbors_pts2 = knn_pts2.execute(pts2);
            
            
            
            
            
            double i_preservation = 0;
            for( int j = 0; j < neighbors_pts1.length; ++j ) {
                
                List<Integer> indices_pts1 = new ArrayList<>();
                List<Integer> indices_pts2 = new ArrayList<>();
                
                for( int k = 0; k < neighbors_pts1[j].length; ++k ) {
                    indices_pts1.add(neighbors_pts1[j][k].index);
                    indices_pts2.add(neighbors_pts2[j][k].index);
                }
               // System.out.println("Size before: "+indices_pts1.size()+", "+indices_pts2.size());
                
                List<Integer> intersection = indices_pts1.stream().filter(c -> indices_pts2.contains(c)).collect(Collectors.toList());
                // System.out.println("Size after: "+indices_pts1.size()+", "+indices_pts2.size()+", "+intersection.size());
                double preservation = (double)intersection.size() / (double) i;
                
                i_preservation += preservation;
                
            }
            
            i_preservation /= (double) neighbors_pts1.length; 
            np.add(i_preservation);
            
            mean_preservation += i_preservation;
            
        }
        
        
        return mean_preservation/n_neighbors;
    }
    
    public List<Double> getNP() {
        return np;
    }
    
}

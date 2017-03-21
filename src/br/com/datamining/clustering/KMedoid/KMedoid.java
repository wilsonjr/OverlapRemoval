/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.datamining.clustering.KMedoid;

import br.com.datamining.clustering.InitialMedoidApproach;
import br.com.methods.utils.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Windows
 */
public class KMedoid {
    
    private ArrayList<Point.Double> items;
    private InitialMedoidApproach initialGuessApproach;
    private final int K;
    private ArrayList<ArrayList<Integer>> clusters;
    
    public KMedoid(ArrayList<Point.Double> items, InitialMedoidApproach initialGuessApproach, int k) {
        if( items == null )
            throw new NullPointerException("Data cannot be null");
        
        this.initialGuessApproach = initialGuessApproach;
        this.K = k;
    }
    
    public void execute() {
        Point.Double[] newGuess = initialGuessApproach.getInitialGuess(items, K);
        Point.Double[] oldGuess = null;
        
        int maxIterations = 30, iter = 0;
        
        do {
            oldGuess = Arrays.copyOf(newGuess, newGuess.length);
            clusters = new ArrayList<>();
            for( int i = 0; i < K; ++i )
                clusters.add(new ArrayList<>());
            
            // assignment step: associate each data point to the closest medoid
            for( int i = 0; i < items.size(); ++i ) {
                double distance = Double.MAX_VALUE;
                int centroid = -1;
                
                for( int j = 0; j < newGuess.length; ++j ) {
                    double dij = Util.distanciaEuclideana(items.get(i).x, items.get(i).y, newGuess[j].x, newGuess[j].y);
                    if( distance > dij ) {
                        distance = dij;
                        centroid = j;
                    }
                }
                
                clusters.get(centroid).add(i);
            }
            
            //    update step: for each medoid m and each data point o associated to m, 
            // swap m and o and compute the total cost of the configuration (that is, the averagte dissimilarity
            // of o to all the data points associated to m). Select the medoid o with the lowest cost of the configuration.
            for( int i = 0; i < newGuess.length; ++i ) {
                double minCost = Double.MAX_VALUE;
                int index = 0;
                
                ArrayList<Integer> cluster = clusters.get(i);                                
                for( int j = 0; j < cluster.size(); ++j ) {
                    double cost = 0;
                    Point.Double centroid = items.get(cluster.get(j));
                    
                    for( int k = 0; k < cluster.size(); ++k ) {
                        if( k == j ) continue;                        
                        cost += Util.distanciaEuclideana(centroid.x, centroid.y, items.get(cluster.get(k)).x, items.get(cluster.get(k)).y);
                    }
                    
                    if( cost < minCost ) {
                        index = j;
                        minCost = cost;
                    }                    
                }
                
                newGuess[i] = items.get(cluster.get(index));
            }
            
            
        } while( !Arrays.equals(oldGuess, newGuess) && iter++ < maxIterations );
    }
    
}

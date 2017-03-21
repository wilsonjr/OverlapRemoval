/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.datamining.clustering.KMeans;

import br.com.datamining.clustering.InitialMedoidApproach;
import br.com.methods.utils.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Windows
 */
public class KMeans {
    
    private ArrayList<Point.Double> items;
    private InitialMedoidApproach initialGuessApproach;
    private final int K;
    private ArrayList<ArrayList<Integer>> clusters;
    
    
    public KMeans(ArrayList<Point.Double> items, InitialMedoidApproach initialGuessApproach, int k) {
        if( items == null )
            throw new NullPointerException("Data cannot be null");
        
        this.items = items;
        this.initialGuessApproach = initialGuessApproach;
        this.K = k;
    }
    
    
    public void execute() {        
        Point.Double[] newGuess = initialGuessApproach.getInitialGuess(items, K);
        Point.Double[] oldGuess = null;
        
        int maxIterations = 10, iter = 0;
        do {
             oldGuess = Arrays.copyOf(newGuess, newGuess.length);
             clusters = new ArrayList<>();
             for( int i = 0; i < K; ++i ) {
                 clusters.add(new ArrayList<>());
             }
             
             for( int i = 0; i < items.size(); ++i ) {
                 double distance = Double.MAX_VALUE;
                 int centroid = -1;
                 
                 
                 for( int j = 0; j < newGuess.length; ++j ) {
                     double dj = Util.distanciaEuclideana(items.get(i).x, items.get(i).y, newGuess[j].x, newGuess[j].y);
                     if( dj < distance ) {
                         distance = dj;
                         centroid = j;
                     }
                 }
                 
                 clusters.get(centroid).add(i);
             }
             
             
             for( int i = 0; i < clusters.size(); ++i ) {
                 ArrayList<Integer> cluster = clusters.get(i);
                 double x = 0, y = 0;
                 
                 for( int j = 0; j < cluster.size(); ++j ) {
                     x += items.get(cluster.get(j)).x;
                     y += items.get(cluster.get(j)).y;
                 }
                 
                 newGuess[i] = new Point.Double(x/cluster.size(), y/cluster.size());                 
             }
             
            
        } while( Arrays.equals(oldGuess, newGuess) && iter++ < maxIterations );
        
        
    }
    
    public ArrayList<ArrayList<Integer>> getClusters() {
        return clusters;
    }
    
    
    
}

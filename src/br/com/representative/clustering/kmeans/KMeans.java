/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering.kmeans;

import br.com.datamining.clustering.InitialMedoidApproach;
import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Windows
 */
public class KMeans extends RepresentativeFinder {
    
    private ArrayList<Point.Double> items;
    private InitialMedoidApproach initialGuessApproach;
    private final int K;
    private ArrayList<ArrayList<Integer>> clusters;
    private Point.Double[] centroids;
    private int maxIterations;
    
    public KMeans(ArrayList<Point.Double> items, InitialMedoidApproach initialGuessApproach, int k) {
        if( items == null )
            throw new NullPointerException("Data cannot be null");
        
        this.items = items;
        this.initialGuessApproach = initialGuessApproach;
        this.K = k;
        this.maxIterations = 30;
    }
    
    @Override
    public void execute() {        
        Point.Double[] newGuess = initialGuessApproach.getInitialGuess(items, K);
        Point.Double[] oldGuess = null;
        
        int iter = 0;
        do {
             oldGuess = Arrays.copyOf(newGuess, newGuess.length);
             clusters = new ArrayList<>();
             for( int i = 0; i < K; ++i ) {
                 clusters.add(new ArrayList<>());
             }
             
             // compute nearest cluster for each point
             for( int i = 0; i < items.size(); ++i ) {
                 double distance = Double.MAX_VALUE;
                 int centroid = -1;
                 
                 
                 for( int j = 0; j < newGuess.length; ++j ) {
                     double dj = Util.euclideanDistance(items.get(i).x, items.get(i).y, newGuess[j].x, newGuess[j].y);
                     if( dj < distance ) {
                         distance = dj;
                         centroid = j;
                     }
                 }
                 
                 clusters.get(centroid).add(i);
             }
             
             // compute de new centroids
             for( int i = 0; i < clusters.size(); ++i ) {
                 ArrayList<Integer> cluster = clusters.get(i);
                 
                 if( cluster.isEmpty() ) {
                     cluster.add((int) (Math.random() * (items.size() - 1)));
                 }
                 
                 double x = 0, y = 0;                 
                 for( int j = 0; j < cluster.size(); ++j ) {
                     x += items.get(cluster.get(j)).x;
                     y += items.get(cluster.get(j)).y;
                 }
                 
                 newGuess[i] = new Point.Double(x/cluster.size(), y/cluster.size());                 
             }
             
            
        } while( !Arrays.equals(oldGuess, newGuess) && iter++ < maxIterations );
        
        centroids = Arrays.copyOf(newGuess, newGuess.length);
        representatives = Util.selectRepresentatives(centroids, items);
    }
    
    public ArrayList<ArrayList<Integer>> getClusters() {
        return clusters;
    }
    
    public Point.Double[] getCentroids() {
        return centroids;
    }
    
    public void setMaxIterations(int max) {
        this.maxIterations = max;
    }
    
}
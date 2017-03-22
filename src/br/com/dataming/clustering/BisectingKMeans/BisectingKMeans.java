/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.dataming.clustering.BisectingKMeans;

import br.com.datamining.clustering.InitialMedoidApproach;
import br.com.datamining.clustering.KMeans.KMeans;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author wilson
 */
public class BisectingKMeans {
    
    private InitialMedoidApproach initialGuess;
    private ArrayList<Point.Double> items;
    private ArrayList<ArrayList<Integer>> clusters;
    private int K;
    private Point.Double[] centroids;
    
    
    public BisectingKMeans(ArrayList<Point.Double> items, InitialMedoidApproach initialGuess, int k) {
        if( items == null )
            throw new NullPointerException("Data cannot be null");
        
        this.items = items;
        this.initialGuess = initialGuess;
        this.K = k;
    }
    
    public void execute() {
        // we start with the whole data as a cluster 
        clusters = new ArrayList<>();
        clusters.add(new ArrayList<>());
        for( int i = 0; i < items.size(); ++i ) {
            clusters.get(0).add(i);
        }
        
        Point.Double[] centers = null;//initialGuess.getInitialGuess(items, 1);
        
        for( int i = 0; i < K-1; ++i ) { // K-1 is equal the number of times we have to split to create K clusters
            
            // get the greatest cluster
            int n = -1, index = -1;
            for( int j = 0; j < clusters.size(); ++j )
                if( clusters.get(j).size() > n ) {
                    n = clusters.get(j).size();
                    index = j;
                }
            
            ArrayList<Integer> cluster = clusters.get(index);
            
            ArrayList<Point.Double> clusterItems = new ArrayList<>();
            for( int j = 0; j < cluster.size(); ++j )
                clusterItems.add(items.get(cluster.get(j)));
            
            KMeans kmeans = new KMeans(clusterItems, initialGuess, 2);
            kmeans.execute();
            centers = kmeans.getCentroids();
            ArrayList<ArrayList<Integer>> splitCluster = kmeans.getClusters();
            
            clusters.remove(index);            
            clusters.add(splitCluster.get(0));
            clusters.add(splitCluster.get(1));
            
        }
        
        centroids = Arrays.copyOf(centers, centers.length);        
    }
    
    public Point.Double[] getCentroids() {
        return centroids;
  }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.clustering.bisectingkmeans;

import br.com.datamining.clustering.InitialMedoidApproach;
import br.com.representative.clustering.kmeans.KMeans;
import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wilson
 */
public class BisectingKMeans extends RepresentativeFinder {
    
    private InitialMedoidApproach initialGuess;
    private ArrayList<Point.Double> items;
    private ArrayList<ArrayList<Integer>> clusters;
    private int K;
    private Point.Double[] centroids;
    private final int ITER = 10;
    
    
    public BisectingKMeans(ArrayList<Point.Double> items, InitialMedoidApproach initialGuess, int k) {
        if( items == null )
            throw new NullPointerException("Data cannot be null");
        
        this.items = items;
        this.initialGuess = initialGuess;
        this.K = k;
    }
    
    @Override
    public void execute() {
        // we start with the whole data as a cluster 
        clusters = new ArrayList<>();
        clusters.add(new ArrayList<>());
        for( int i = 0; i < items.size(); ++i ) {
            clusters.get(0).add(i);
        }
        
        for( int i = 0; i < K-1; ++i ) { // K-1 is equal the number of times we have to split to create K clusters
            
            // get the greatest cluster
            int n = -1, index = -1;
            for( int j = 0; j < clusters.size(); ++j )
                if( clusters.get(j).size() > n ) {
                    n = clusters.get(j).size();
                    index = j;
                }
            
            ArrayList<Integer> cluster = clusters.get(index);
            
            // choose the best split 
            ArrayList<ArrayList<Integer>> chosenSplit = chooseSplit(cluster);
                        
            
            clusters.remove(index);            
            clusters.add(chosenSplit.get(0));
            clusters.add(chosenSplit.get(1));            
        }
        
        
        representatives = Util.selectRepresentatives(clusters, items);
    }
    
    public Point.Double[] getCentroids() {
        return centroids;
    }
    
    public ArrayList<ArrayList<Integer>> getClusters() {
        return clusters;
    }

    private ArrayList<ArrayList<Integer>> chooseSplit(ArrayList<Integer> cluster) {
        ArrayList<Point.Double> clusterItems = new ArrayList<>();
        Map<Integer, Integer> mapIndex = new HashMap<>();
        for( int j = 0; j < cluster.size(); ++j ) {
            clusterItems.add(items.get(cluster.get(j)));
            mapIndex.put(j, cluster.get(j));
        }
        
        ArrayList<ArrayList<Integer>> chosenSplit = null;
        double minEc = Double.MAX_VALUE;
            
        for( int x = 0; x < ITER; ++x ) {            
            KMeans kmeans = new KMeans(clusterItems, initialGuess, 2);
            kmeans.setMaxIterations(1);
            kmeans.execute();
            centroids = kmeans.getCentroids();
            ArrayList<ArrayList<Integer>> splitCluster = kmeans.getClusters();
            for( int j = 0; j < splitCluster.size(); ++j ) {
                for( int k = 0; k < splitCluster.get(j).size(); ++k ) 
                    splitCluster.get(j).set(k, mapIndex.get(splitCluster.get(j).get(k)));
            }

            double ec = 0;

            for( int j = 0; j < centroids.length; ++j ) {
                for( int k = 0; k < splitCluster.get(j).size(); ++k ) 
                    ec += Util.euclideanDistance(centroids[j].x, centroids[j].y, 
                            items.get(splitCluster.get(j).get(k)).x, items.get(splitCluster.get(j).get(k)).y);
            }

            if( ec < minEc ) {
                chosenSplit = splitCluster;
                minEc = ec;
            }
        }
        
        return chosenSplit;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.clustering.partitioning;

import br.com.representative.clustering.InitialMedoidApproach;
import br.com.methods.utils.Util;
import br.com.methods.utils.Vect;
import br.com.representative.RepresentativeRegistry;
import br.com.representative.clustering.KMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wilson
 */
public class BisectingKMeans extends KMethod {
    
    public BisectingKMeans(List<Vect> items, InitialMedoidApproach initialGuess, int k) {
        super(items, initialGuess, k);
    }
    
    static {
        RepresentativeRegistry.addClassPackage(BisectingKMeans.class.getName(), BisectingKMeans.class.getCanonicalName());
    }
    
    @Override
    public void execute() {
        List<Vect> tempCentroids = new ArrayList<>();
        //tempCentroids.add(new Point.Double(0, 0));
        tempCentroids.add(new Vect(items.get(0).vector().length));
        
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
            
            List<Integer> cluster = clusters.get(index);
            // choose the best split 
            List<List<Integer>> chosenSplit = chooseSplit(cluster);            
            tempCentroids.remove(index);
            tempCentroids.add(centroids[0]);
            tempCentroids.add(centroids[1]);
            
            clusters.remove(index);            
            clusters.add(chosenSplit.get(0));
            clusters.add(chosenSplit.get(1));            
        }
       
        centroids = tempCentroids.stream().toArray(Vect[]::new);
        representatives = Util.selectRepresentatives(centroids, items);
    }
    
    
    private List<List<Integer>> chooseSplit(List<Integer> cluster) {
        ArrayList<Vect> clusterItems = new ArrayList<>();
        Map<Integer, Integer> mapIndex = new HashMap<>();
        for( int j = 0; j < cluster.size(); ++j ) {
            clusterItems.add(items.get(cluster.get(j)));
            mapIndex.put(j, cluster.get(j));
        }
        
        List<List<Integer>> chosenSplit = null;
        double minEc = Double.MAX_VALUE;
            
        for( int x = 0; x < ITER; ++x ) {            
            KMeans kmeans = new KMeans(clusterItems, getInitialGuessApproach(), 2);
            kmeans.setMaxIterations(1);
            kmeans.execute();
            centroids = kmeans.getCentroids();
            
            List<List<Integer>> splitCluster = kmeans.getClusters();
            for( int j = 0; j < splitCluster.size(); ++j ) {
                for( int k = 0; k < splitCluster.get(j).size(); ++k ) 
                    splitCluster.get(j).set(k, mapIndex.get(splitCluster.get(j).get(k)));
            }

            double ec = 0;

            for( int j = 0; j < centroids.length; ++j ) {
                for( int k = 0; k < splitCluster.get(j).size(); ++k ) {
                    //ec += Util.euclideanDistance(centroids[j].x, centroids[j].y, 
                    //        items.get(splitCluster.get(j).get(k)).x, items.get(splitCluster.get(j).get(k)).y);
                    ec += centroids[j].distance(items.get(splitCluster.get(j).get(k)));
                    
                }
            }

            if( ec < minEc ) {
                chosenSplit = splitCluster;
                minEc = ec;
            }
        }
        
        return chosenSplit;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.datamining.clustering;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 *
 * @author wilson
 */
public class HierarchicalClustering {
    
    private ArrayList<Cluster> clusters;
    private ArrayList<Rectangle2D.Double> items;
    private PriorityQueue<Linkage> linkages;
    
    public HierarchicalClustering(ArrayList<Rectangle2D.Double> items) {                
        if( items == null )
            throw new NullPointerException("Data cannot be null");
        
        this.items = items;
    }
    
    public void execute() {
        // 1. create one cluster for each element
        createClusters(items);
        
        // 2. compute distance between each par of elements
        computeDistances();
        
        
        
    }
    
    private void createClusters(ArrayList<Rectangle2D.Double> items) {
        clusters = new ArrayList<>();        
        items.stream().forEach((e)-> {
            clusters.add(new Cluster(e));
        });
    }

    private void computeDistances() {        
        for( int i = 0; i < clusters.size(); ++i ) 
            for( int j = i+1; j < clusters.size(); ++j ) {                
                double distance = clusters.get(i).distanceTo(clusters.get(j));                
                Linkage linkage = new Linkage(clusters.get(i), clusters.get(j), distance);
                linkages.add(linkage);
            }
    }
    
    
    
    
    
}

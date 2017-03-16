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
        
        
        
        
    }
    
    
    // 1. Create one cluster for each element
    private void createClusters(ArrayList<Rectangle2D.Double> items) {
        clusters = new ArrayList<>();        
        items.stream().forEach((e)-> {
            clusters.add(new Cluster(e));
        });
    }
    
    
    
    
    
}

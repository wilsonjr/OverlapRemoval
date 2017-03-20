/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.datamining.clustering;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 *
 * @author wilson
 */
public class HierarchicalClustering {
    
    private ArrayList<Cluster> clusters;
    private ArrayList<? extends Rectangle2D.Double> items;
    private PriorityQueue<Linkage> linkages;
    private Map<String, Linkage> linkageMap;
    private LinkageStrategy linkageStrategy;
    
    public HierarchicalClustering(ArrayList<? extends Rectangle2D.Double> items, LinkageStrategy linkageStrategy) {                
        if( items == null )
            throw new NullPointerException("Data cannot be null");
        
        this.items = items;
        this.linkageStrategy = linkageStrategy;
    }
    
    public void execute() {
        // 1. create one cluster for each element
        createClusters(items);
        
        // 2. compute distance between each par of elements
        computeDistances();
        
        // 3. extract a pair of clusters with the smallest distance
        while( clusters.size() > 1 ) {
            
            Linkage top = linkages.poll();
            
            Cluster uv = new Cluster();
            Cluster u = top.getU();
            Cluster v = top.getV();            
            
            uv.addPoints(u.getPoints());
            uv.addPoints(v.getPoints());
            uv.setId(u.getId()+"."+v.getId());  
            uv.addSons(u);
            uv.addSons(v);
            
            System.out.println("Unindo clusters: "+u.getId()+" <-> "+v.getId());
            
            clusters.remove(u);
            clusters.remove(v);
            
            for( Cluster c: clusters ) {
                Linkage firstLink = findLink(c, u);
                Linkage secondLink = findLink(c, v);
                List<Double> distances = new ArrayList<>();
                if( firstLink != null ) {
                    distances.add(firstLink.getDistance());
                    linkages.remove(firstLink);
                }
                if( secondLink != null ) {
                    distances.add(secondLink.getDistance());
                    linkages.remove(secondLink);
                }
           
                double linkageDistance = linkageStrategy.distance(distances);
                Linkage uvC = new Linkage(uv, c, linkageDistance);
                linkages.add(uvC);
                linkageMap.put(createKey(uv, c), uvC);
            } 
            
            clusters.add(uv);
        }
    }
    
    private void createClusters(ArrayList<? extends Rectangle2D.Double> items) {
        clusters = new ArrayList<>();      
        
        for( int i = 0; i < items.size(); ++i ) {
            clusters.add(new Cluster(items.get(i), i, this));
        }
    }

    private void computeDistances() {  
        linkages = new PriorityQueue<>();
        linkageMap = new HashMap<>();
        for( int i = 0; i < clusters.size(); ++i ) 
            for( int j = i+1; j < clusters.size(); ++j ) {                
                double distance = clusters.get(i).distanceTo(clusters.get(j));                
                Linkage linkage = new Linkage(clusters.get(i), clusters.get(j), distance);
                linkages.add(linkage);
                linkageMap.put(createKey(linkage.getU(), linkage.getV()), linkage);
            }
    }

    private Linkage findLink(Cluster c, Cluster u) {
        Linkage link = linkageMap.get(createKey(c, u));
        if( link == null ) 
            link = linkageMap.get(createKey(u, c));
        return link;
    }

    private String createKey(Cluster u, Cluster v) {        
        return u.getId()+"<->"+v.getId();
    }
    
    public ArrayList<Cluster> getClusters() {
        return clusters;
    }
    
    
    
}

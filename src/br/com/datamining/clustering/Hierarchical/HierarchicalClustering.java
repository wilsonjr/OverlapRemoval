/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.datamining.clustering.Hierarchical;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author wilson
 */
public class HierarchicalClustering {
    
    private ArrayList<Cluster> clusters, backup;
    private ArrayList<? extends Rectangle2D.Double> items;
    private PriorityQueue<Linkage> linkages;
    private Map<String, Linkage> linkageMap;
    private LinkageStrategy linkageStrategy;
    private ArrayList<ArrayList<ArrayList<Integer>>> clusterHierarchy;
            
    
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
        agglomerate();
        Collections.reverse(clusterHierarchy);
    }

    private void agglomerate() {
        
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
            u.setParent(uv);
            v.setParent(uv);
            
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
            addLevel();            
        }
    }

    private void addLevel() throws NumberFormatException {
        clusterHierarchy.add(new ArrayList<>());
        int index = clusterHierarchy.size()-1;
        for( int i = 0; i < clusters.size(); ++i ) {
            ArrayList<Integer> items = new ArrayList<>();
            String[] indexes = clusters.get(i).getId().split("\\.");
            for( int j = 0; j < indexes.length; ++j )
                items.add(Integer.parseInt(indexes[j]));
            clusterHierarchy.get(index).add(items);
        }
    }

    
    private void createClusters(ArrayList<? extends Rectangle2D.Double> items) {
        clusters = new ArrayList<>();      
        clusterHierarchy = new ArrayList<>();
        clusterHierarchy.add(new ArrayList<>());
        
        for( int i = 0; i < items.size(); ++i ) {
            clusters.add(new Cluster(items.get(i), i, this));
            ArrayList<Integer> c = new ArrayList<>();
            c.add(i);
            clusterHierarchy.get(0).add(c);
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
    
    public void printHierarchy() {
        clusters.get(0).print("");
    }
    
    public ArrayList<ArrayList<ArrayList<Integer>>> getClusterHierarchy() {
        
        return clusterHierarchy;
    }
    
}
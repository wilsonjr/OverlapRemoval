/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.projection.spacereduction.seamcarving;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author Windows
 */
public class RectGraphCache {
    
    private List<Rectangle2D.Double> rects;
    private Map<SeamPair<Rectangle2D.Double, Rectangle2D.Double>, Double> similarity;
    private Map<SeamPair<Rectangle2D.Double, Rectangle2D.Double>, Double> distance;
    
    private Map<SeamPair<Rectangle2D.Double, Rectangle2D.Double>, Double> shortestPaths = new HashMap<>();
    private Map<Rectangle2D.Double, Double> weightedDegree = new HashMap<>();
    private Map<Rectangle2D.Double, Integer[]> nonZeroAdjacency = new HashMap<>();
    
    public RectGraphCache(List<Rectangle2D.Double> rects, 
            Map<SeamPair<Rectangle2D.Double, Rectangle2D.Double>, Double> similarity, 
            Map<SeamPair<Rectangle2D.Double, Rectangle2D.Double>, Double> distance){
                
        this.rects = rects;
        this.similarity = similarity;
        this.distance = distance;
    }
    
    public double shortestPath(Rectangle2D.Double r1, Rectangle2D.Double r2) {
        SeamPair<Rectangle2D.Double, Rectangle2D.Double> rp = new SeamPair<>(r1, r2);
        
        if( !shortestPaths.containsKey(rp) )
            initShortestPaths(r1);
        
        return shortestPaths.get(rp);
    }
    
    public double weightedDegree(Rectangle2D.Double r) {
        if( !weightedDegree.containsKey(r) )
            initWeightedDegree(r);
        
        return weightedDegree.get(r);
    }
    
    public Integer[] nonZeroAdjacency(Rectangle2D.Double r) {
        if( !nonZeroAdjacency.containsKey(r) )
            initNonZeroAdjacency(r);
        
        return nonZeroAdjacency.get(r);
    }
    
    private void initShortestPaths(Rectangle2D.Double s) {
        Map<Rectangle2D.Double, Integer> rIndex = new HashMap<>();
        for( int i = 0; i < rects.size(); ++i )
            rIndex.put(rects.get(i), i);
        
        double[] dist = new double[rects.size()];
        Arrays.fill(dist, Double.MAX_VALUE);
        dist[rIndex.get(s)] = 0;
        
        PriorityQueue<Rectangle2D.Double> q = new PriorityQueue<>();
        q.add(s);
        
        while( !q.isEmpty() ) {            
            Rectangle2D.Double now = q.poll();
            int v = rIndex.get(now);
            
            for( int i = 0; i < rects.size(); ++i ) {                
                Rectangle2D.Double next = rects.get(i);
                SeamPair<Rectangle2D.Double, Rectangle2D.Double> rp = new SeamPair<>(now, next);
                if( distance.containsKey(rp) ) {                    
                    double len = distance.get(rp);
                    if( dist[i] > dist[v]+len ) {
                        dist[i] = dist[v]+len;
                        q.add(next);
                    }                    
                }                
            }            
        }
        
        for( int i = 0; i < rects.size(); ++i ) {
            SeamPair<Rectangle2D.Double, Rectangle2D.Double> rp = new SeamPair<>(s, rects.get(i));
            shortestPaths.put(rp, dist[i]);
        }
        
    }
    
    private void initWeightedDegree(Rectangle2D.Double s) {
        double rd = 0;
        
        for( int j = 0; j < rects.size(); ++j ) {
            Rectangle2D.Double t = rects.get(j);
            if( s.equals(t) )
                continue;
            
            SeamPair<Rectangle2D.Double, Rectangle2D.Double> rp = new SeamPair<>(s, t);
            rd += similarity.get(rp);
        }
        
        weightedDegree.put(s, rd);
    }
    
    private void initNonZeroAdjacency(Rectangle2D.Double s) {
        List<Integer> adj = new ArrayList<>();
        
        for( int i = 0; i < rects.size(); ++i ) {
            Rectangle2D.Double t = rects.get(i);
            if( s.equals(t) )
                continue;
            
            SeamPair<Rectangle2D.Double, Rectangle2D.Double> rp = new SeamPair<>(s, t);
            if( similarity.get(rp) > 0 )
                adj.add(i);
        }
        
        nonZeroAdjacency.put(s, adj.toArray(new Integer[adj.size()]));
    }
    
    
    
}

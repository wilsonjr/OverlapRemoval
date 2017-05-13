/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.utils;


import java.awt.geom.Point2D;
import java.util.PriorityQueue;
/**
 *
 * @author wilson
 */
public class KNearestNeighbors {
    
    private int k;
    private Point2D.Double[] points;
    private PriorityQueue<Pair>[] nearestNeighbors;
    
    public KNearestNeighbors(int k, Point2D.Double[] points) {
        this.k = k;
        this.points = points;
        
    }
    
    public void execute() {
        nearestNeighbors = new PriorityQueue[points.length];
        
        for( int i = 0; i < nearestNeighbors.length; ++i ) {
            
            for( int j = 0; j < points.length; ++j )
                if( i != j )
                    nearestNeighbors[i].add(new Pair(j, (float)Util.euclideanDistance(points[i].x, points[i].y, points[j].x, points[j].y)));
        }
    }
    
    public PriorityQueue<Pair>[] getNearestNeighbors() {
        return nearestNeighbors;
    }
    
}

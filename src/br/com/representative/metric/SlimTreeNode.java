/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.metric;

import br.com.methods.utils.Util;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilson
 */
public class SlimTreeNode implements Comparable<SlimTreeNode> {
    private List<Instance> points;
    
    public SlimTreeNode(List<Point2D.Double> points) {
        this.points = new ArrayList<>();
        for( int i = 0; i < points.size(); ++i )
            this.points.add(new Instance(points.get(i), i));
    }
    
    public SlimTreeNode() {
        points = new ArrayList<>();
    }
    
    public void add(Point2D.Double p, int index) {
        points.add(new Instance(p, index));
    }
    
    public int size() {
        return points.size();
    }

    @Override
    public int compareTo(SlimTreeNode o) {
        return Integer.compare(o.size(), size());
    }

    public Point2D.Double get(int i) {
        return points.get(i).p;
    }
    
    public int index(int i) {
        return points.get(i).i;
    }

    public int medoid() {
        
        Point2D.Double centroid = new Point2D.Double(0, 0);
        for( int i = 0; i < points.size(); ++i ) {
            centroid.x += points.get(i).p.x;
            centroid.y += points.get(i).p.y;
        }
        
        centroid.x /= points.size();
        centroid.y /= points.size();
        
        int indexCentroid = -1;
        double minDist = Double.MAX_VALUE;
        for( int i = 0; i < points.size(); ++i ) {
            double d = Util.euclideanDistance(centroid.x, centroid.y, points.get(i).p.x, points.get(i).p.y);
            if( d < minDist ) {
                minDist = d;
                indexCentroid = i;
            }
        }        
        
        return points.get(indexCentroid).i;
    }
    
    
    private class Instance {
        private Point2D.Double p;
        private int i;
        
        public Instance(Point2D.Double p, int index) {
            this.p = p;
            this.i = index;
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.expadingnode;

import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author wilson
 */
public class OverlapNode {
    
    
    private List<OverlapNode> instances;
    public OverlapRect boundingBox;
    public OverlapRect finalBoundingBox;
    private int id;
    private boolean leaf;
    private final double MIN_DISTANCE = 0.0000001;
    
    public OverlapNode(OverlapRect boundingBox) {
        this.boundingBox = boundingBox;
        this.finalBoundingBox = new OverlapRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        this.id = boundingBox.getId();
        leaf = true;
    }
    
    public OverlapNode(List<OverlapNode> instances) {
        this.instances = instances;        
        leaf = false;
    }
    
    
    public void calculateBoundingBox() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
                
        
        for( int i = 0; i < instances.size(); i++ ) {
            double x1 = instances.get(i).boundingBox.x;
            if (maxX < x1)
                maxX = x1;
            if (minX > x1)
                minX = x1;
            
            double x2 = instances.get(i).boundingBox.x+instances.get(i).boundingBox.width;
            if (maxX < x2)
                maxX = x2;
            if (minX > x2)
                minX = x2;

            double y1 = instances.get(i).boundingBox.y;
            if (maxY < y1)
                maxY = y1;
            if (minY > y1)
                minY = y1;
            
            double y2 = instances.get(i).boundingBox.y+instances.get(i).boundingBox.height;
            if (maxY < y2)
                maxY = y2;
            if (minY > y2)
                minY = y2;
        }
        
        double space = 0;
        double width = (maxX-minX) + space;
        double height = (maxY-minY) + space;
        
        
        finalBoundingBox = new OverlapRect(minX, minY, width, height);
        boundingBox = new OverlapRect(minX, minY, width, height);
        
    }
    
    public void removeOverlap(int representative) {
        
        List<OverlapNode> subset = instances;
        Collections.sort(subset, OverlapNodeComparator.getInstance(subset.get(representative)));
        
        System.out.println("Removing overlaps, please wait...");
        for( int i = 0; i < subset.size(); ++i ) {
          
            OverlapNode r1 = subset.get(i);
            for( int j = i+1; j < subset.size(); ++j ) {
                OverlapNode r2 = subset.get(j);
                
                if( r1.boundingBox.intersects(r2.boundingBox) ) {
                    double inter = intersection(r1.boundingBox, r2.boundingBox);
                    
                    double ax = r2.boundingBox.x;
                    double ay = r2.boundingBox.y;
                    double bx = r1.boundingBox.x;
                    double by = r1.boundingBox.y;

                    double lenAB = Util.euclideanDistance(ax, ay, bx, by);
                    
                    if( lenAB <= MIN_DISTANCE ) {
                        ax += 0.5;
                        ay += 0.5;
                        lenAB = Util.euclideanDistance(ax, ay, bx, by);
                        inter = intersection(r1.boundingBox, new OverlapRect(ax, ay, r2.boundingBox.width, r2.boundingBox.height));
                    }

                    double ammountx = (bx-ax)/lenAB * (inter*lenAB - lenAB);
                    double ammounty = (by-ay)/lenAB * (inter*lenAB - lenAB);
                    
                    r1.boundingBox.x = bx+ammountx;
                    r1.boundingBox.y = by+ammounty;
                     
                    for( int o = i; o >= 0; --o ) {                    
                        OverlapNode p = subset.get(o);

                        List<OverlapNode> first = new ArrayList<>();

                        for( int k = o-1; k >= 0; --k ) {
                            first.add(subset.get(k));
                        }

                        if( !first.isEmpty() ) {

                            Collections.sort(first, OverlapNodeComparator.getInstance(p));

                            for( int k = 0; k < first.size(); ++k ) {
                                OverlapNode r3 = first.get(k);
                                
                                if( p.boundingBox.intersects(r3.boundingBox) ) {
                                    inter = intersection(p.boundingBox, r3.boundingBox);
                                    ax = p.boundingBox.x;
                                    ay = p.boundingBox.y;
                                    bx = r3.boundingBox.x;
                                    by = r3.boundingBox.y;

                                    lenAB = Util.euclideanDistance(ax, ay, bx, by);
                                    
                                    if( lenAB <= MIN_DISTANCE ) {
                                        ax += 0.5;
                                        ay += 0.5;
                                        lenAB = Util.euclideanDistance(ax, ay, bx, by);
                                        inter = intersection(p.boundingBox, new OverlapRect(ax, ay, r3.boundingBox.width, r3.boundingBox.height));
                                    }

                                    ammountx = (bx-ax)/lenAB *(inter*lenAB - lenAB);
                                    ammounty = (by-ay)/lenAB *(inter*lenAB - lenAB);

                                    r3.boundingBox.x = bx + ammountx;
                                    r3.boundingBox.y = by + ammounty;
                                }
                            }

                        }
                    }
                    
                    
                } 
            }            
        }
        System.out.println("Removing overlaps, done...");
        
        instances = subset;
        calculateBoundingBox();
    }
    
    private double intersection(OverlapRect u, OverlapRect v) {
        return Math.max(
            Math.min(
               (u.getWidth()/2. + v.getWidth()/2.)/Math.abs(u.getCenterX() - v.getCenterX()), 
               (u.getHeight()/2. + v.getHeight()/2.)/Math.abs(u.getCenterY() - v.getCenterY())
            ), 1);
    }

    public List<OverlapNode> getInstances() {
        return instances;
    }    

    public void updatePositions(double deltax, double deltay) {
        
        for( int i = 0; i < instances.size(); ++i ) {
            instances.get(i).boundingBox.x += deltax;
            instances.get(i).boundingBox.y += deltay;
        }
        
        calculateBoundingBox();
    }

    public void updateInstances() {
        
        List<OverlapNode> nodes = new ArrayList<>();
        
        for( int i = 0; i < instances.size(); ++i ) {
            for( int j = 0; j < instances.get(i).getInstances().size(); ++j )
                nodes.add(instances.get(i).getInstances().get(j));
        }
        
        instances = nodes;                 
    }

    public int getId() {
        return id;
    }
    
    public boolean isLeaf() {
        return leaf;
    }

    
}

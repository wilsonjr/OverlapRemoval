/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.metric;

import br.com.methods.utils.Vect;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilson
 */
public class SlimTreeNode implements Comparable<SlimTreeNode> {
    private List<Instance> points;
    
    public SlimTreeNode(List<Vect> points) {
        this.points = new ArrayList<>();
        for( int i = 0; i < points.size(); ++i )
            this.points.add(new Instance(points.get(i), i));
    }
    
    public SlimTreeNode() {
        points = new ArrayList<>();
    }
    
    public void add(Vect p, int index) {
        points.add(new Instance(p, index));
    }
    
    public int size() {
        return points.size();
    }

    @Override
    public int compareTo(SlimTreeNode o) {
        return Integer.compare(o.size(), size());
    }

    public Vect get(int i) {
        return points.get(i).p;
    }
    
    public int index(int i) {
        return points.get(i).i;
    }

    public int medoid() {        
        Vect centroid = new Vect(points.get(0).p.vector().length);
        for( int i = 0; i < points.size(); ++i ) {
            centroid.add(points.get(i).p);
        }
        centroid.divide(points.size());
        
        int indexCentroid = -1;
        double minDist = Double.MAX_VALUE;
        for( int i = 0; i < points.size(); ++i ) {
            double d = centroid.distance(points.get(i).p);
            if( d < minDist ) {
                minDist = d;
                indexCentroid = i;
            }
        }        
        
        return points.get(indexCentroid).i;
    }
    
    
    private class Instance {
        private Vect p;
        private int i;
        
        public Instance(Vect p, int index) {
            this.p = p;
            this.i = index;
        }
    }
}

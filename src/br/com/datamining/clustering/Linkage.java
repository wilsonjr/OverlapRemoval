/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.datamining.clustering;

/**
 *
 * @author wilson
 */
public class Linkage implements Comparable<Linkage> {
    
    private Cluster u;
    private Cluster v;
    private double distance;
        
    private Linkage(Cluster u, Cluster v, double distance) {
        this.u = u;
        this.v = v;
        this.distance = distance;
    }
    
    public Cluster getU() {
        return u;
    }
    
    public Cluster getV() {
        return v;
    }
    
    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(Linkage o) {
        double dist = distance-o.getDistance();
        if( dist > 0 )
            return 1;
        else if( dist < 0 )
            return -1;
        return 0;        
    }
    
}

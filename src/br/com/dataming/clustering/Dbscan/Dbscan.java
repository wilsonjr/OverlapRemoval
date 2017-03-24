/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.dataming.clustering.Dbscan;

import br.com.methods.utils.Util;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author wilson
 */
public class Dbscan {
    
    private ArrayList<Point.Double> items;
    private ArrayList<ArrayList<Integer>> clusters;
    private ArrayList<DbscanPoint> points;
    private int currentCluster;
    private double epsilon;
    private int minPts;
    
    public Dbscan(ArrayList<Point.Double> items) {
        if( items == null )
            throw new NullPointerException("Data cannot be null");
        
        this.items = items;        
        this.points = new ArrayList<>();
        for( int i = 0; i < this.items.size(); ++i )
            this.points.add(new DbscanPoint(this.items.get(i)));        
        this.currentCluster = 0;
    }
    
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
    
    public void setMinPts(int minPts) {
        this.minPts = minPts;
    }
    
    public void execute() {
       
        for( int i = 0; i < points.size(); ++i ) {

            DbscanPoint p = points.get(i);

            if( !p.processed() ) {
                tryExpand(p);
            }
        }

        clusters = new ArrayList<>();
        for( int i = 0; i < currentCluster; ++i ) {
            clusters.add(new ArrayList<>());
        }
       
        for( int i = 0; i < points.size(); ++i )
            if( points.get(i).processed() )
                clusters.get(points.get(i).cluster).add(i);       
    }

    private void tryExpand(DbscanPoint p) {
        
        ArrayList<DbscanPoint> seeds = new ArrayList<>();
        for( int i = 0; i < points.size(); ++i ) {
            double d = Util.distanciaEuclideana(p.point.x, p.point.y, points.get(i).point.x, points.get(i).point.y);
            if( d < epsilon ) 
                seeds.add(points.get(i));
        }
        
        if( seeds.size() >= minPts ) {
            p.cluster = currentCluster;
            seeds.remove(p);
            
            while( !seeds.isEmpty() ) {
                DbscanPoint first = seeds.get(0);
                ArrayList<DbscanPoint> nseeds = new ArrayList<>();
                for( int i = 0; i < points.size(); ++i ) {
                    double d = Util.distanciaEuclideana(first.point.x, first.point.y, points.get(i).point.x, points.get(i).point.y);
                    if( d < epsilon ) 
                        nseeds.add(points.get(i));
                }
                
                if( nseeds.size() >= minPts ) {                    
                    for( int i = 0; i < nseeds.size(); ++i ) {                        
                        if( !nseeds.get(i).processed() ) {
                            seeds.add(nseeds.get(i));
                        }                        
                        nseeds.get(i).cluster = currentCluster;
                    }
                }
                seeds.remove(first);
            }
            
            currentCluster++;
        } 
    }
    
    public ArrayList<ArrayList<Integer>> getClusters() {
        return clusters;
    }
    
    private class DbscanPoint {
        
        public Point.Double point;
        public int cluster;
        
        public DbscanPoint(Point.Double point) {
            this.point = point;
            this.cluster = -1;
        }
        
        public boolean processed() {
            return cluster != -1;
        }
        
    }
    
}
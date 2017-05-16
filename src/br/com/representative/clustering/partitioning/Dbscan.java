/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.clustering.partitioning;

import br.com.methods.utils.Util;
import br.com.representative.clustering.Partitioning;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilson
 */
public class Dbscan extends Partitioning {
    
    private ArrayList<DbscanPoint> points;
    private int currentCluster;
    private double epsilon;
    private int minPts;
    
    public Dbscan(List<Point.Double> items, double epsilon, int minPts) {
        super(items);
               
        this.currentCluster = 0;
        this.epsilon = epsilon;
        this.minPts = minPts;
    }
    
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
    
    public void setMinPts(int minPts) {
        this.minPts = minPts;
    }
    
    @Override
    public void execute() {
        
        this.points = new ArrayList<>();
        for( int i = 0; i < this.items.size(); ++i )
            this.points.add(new DbscanPoint(this.items.get(i))); 
       
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
        
        representatives = Util.selectRepresentatives(clusters, items);
    }

    private void tryExpand(DbscanPoint p) {
        
        ArrayList<DbscanPoint> seeds = new ArrayList<>();
        for( int i = 0; i < points.size(); ++i ) {
            double d = Util.euclideanDistance(p.point.x, p.point.y, points.get(i).point.x, points.get(i).point.y);
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
                    double d = Util.euclideanDistance(first.point.x, first.point.y, points.get(i).point.x, points.get(i).point.y);
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

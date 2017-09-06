/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.analysis;

import br.com.methods.utils.Util;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilson
 */
public class NearestNeighborAnalysis implements RepresentativeAnalysis {
    
    private int[] representative;
    private Point2D.Double[] points;
    
    public NearestNeighborAnalysis(int[] representative, Point2D.Double[] points) {
        this.representative = representative;
        this.points = points;
    }
    
    
    @Override
    public double init() {
        double mean = 0.0;
        List<Double> distances = new ArrayList<>();
        
        for( int i = 0; i < representative.length; ++i ) {
            
            Point2D.Double u = points[representative[i]];            
            double minDist = Double.MAX_VALUE;
            
            for( int j = 0; j < representative.length; ++j ) {
                if( i == j )
                    continue;
                
                Point2D.Double v = points[representative[j]];               
                
                double dist = Util.euclideanDistance(u.x, u.y, v.x, v.y);
                minDist = Math.min(minDist, dist);                
            }
                       
            
            distances.add(minDist);            
            mean += minDist;
        }
        
        
        
        
        
        
        
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

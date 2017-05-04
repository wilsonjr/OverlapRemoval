/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering;

import java.awt.geom.Point2D;
import java.util.List;

/**
 *
 * @author Windows
 */
public class FirstPointsMedoidApproach implements InitialMedoidApproach {

    @Override
    public Point2D.Double[] getInitialGuess(List<Point2D.Double> items, int k) {
        
        Point2D.Double[] medoids = new Point2D.Double[k];        
        for( int i = 0; i < k; ++i )
            medoids[i] = items.get(i);
        
        return medoids;
    }
    
}

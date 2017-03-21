/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.datamining.clustering;

import br.com.methods.utils.Util;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Windows
 */
public class FarPointsMedoidApproach implements InitialMedoidApproach {

    @Override
    public Point.Double[] getInitialGuess(ArrayList<Point.Double> items, int k) {
        Point.Double[] medoids = new Point.Double[k];
        int count = 0, index = 0;
        
        Point.Double first = items.get(0);
        double d = Double.MIN_VALUE;
        for( int i = 0; i < items.size(); ++i ) {
            double di = Util.distanciaEuclideana(first.x, first.y, items.get(i).x, items.get(i).y);
            if( d < di ) {
                d = di;
                index = i;
            }
        }
        
        medoids[count++] = items.get(index);
        
        while( count < k ) {
            double max = Double.MIN_VALUE;
            for( int i = 0; i < items.size(); ++i ) {
                
                double min = Double.MAX_VALUE;                
                for( int j = 0; j < count; ++j ) {
                    double dij = Util.distanciaEuclideana(items.get(i).x, items.get(i).y, medoids[j].x, medoids[j].y);
                    if( dij < min ) {
                        min = dij;
                    }
                }
                
                if( min > max ) {
                    max = min;
                    index = i;
                }
            }
            
            medoids[count++] = items.get(index);
        }
        
        return medoids;
    }
    
}

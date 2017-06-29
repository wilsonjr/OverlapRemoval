/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering;

import br.com.methods.utils.Vect;
import java.util.List;

/**
 *
 * @author Windows
 */
public class FarPointsMedoidApproach implements InitialMedoidApproach {

    @Override
    public Vect[] getInitialGuess(List<Vect> items, int k) {
        Vect[] medoids = new Vect[k];
        int count = 0, index = 0;
        
        Vect first = items.get(0);
        double d = Double.MIN_VALUE;
        for( int i = 0; i < items.size(); ++i ) {
            //double di = Util.euclideanDistance(first.x, first.y, items.get(i).x, items.get(i).y);
            double di = first.distance(items.get(i));
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
                    //double dij = Util.euclideanDistance(items.get(i).x, items.get(i).y, medoids[j].x, medoids[j].y);
                    double dij = items.get(i).distance(medoids[j]);
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

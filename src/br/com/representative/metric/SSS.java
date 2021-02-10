/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
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
public class SSS extends AccessMetric {   
    private double alpha;
    private double maxDistance;
    
    public SSS(List<Vect> items, double alpha, double maxDistance) {
        super(items);
        
        if( items.isEmpty() )
            throw new IllegalArgumentException("The set must not be empty.");
        
        this.alpha = alpha;
        this.maxDistance = maxDistance;
    }
    
    @Override
    public void execute() {
        List<Integer> pivots = new ArrayList<>();
        pivots.add(0);        
        
        for( int i = 1; i < items.size(); ++i ) {
            boolean pivot = true;
            
            for( Integer p: pivots ) {
                //double d = Util.euclideanDistance(items.get(p).x, items.get(p).y, items.get(i).x, items.get(i).y);
                double d = items.get(p).distance(items.get(i));
                if( d < maxDistance*alpha ) {
                    pivot = false;
                    break;
                }
            }
            
            if( pivot ) {
                pivots.add(i);                       
            }
        }
        
        representatives = pivots.stream().mapToInt((e)->e).toArray();
    }

    @Override
    public void filterData(int[] indexes) {
        super.filterData(indexes);
        
        double distance = -1;
        for( int i = 0; i < items.size(); ++i )
            for( int j = i+1; j < items.size(); ++j ) {
                //double d = Util.euclideanDistance(items.get(i).x, items.get(i).y, items.get(j).x, items.get(j).y);
                double d = items.get(i).distance(items.get(j));
                if( distance < d ) 
                    distance = d;
            }
        
        maxDistance = distance;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }
    
}

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

import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilson
 */
public class SSS extends RepresentativeFinder {   
    private List<Point2D.Double> finalItems;
    private List<Point2D.Double> items;
    private double alpha;
    private double maxDistance;
    
    public SSS(List<Point2D.Double> items, double alpha, double maxDistance) {
        if( items.isEmpty() )
            throw new IllegalArgumentException("The set must not be empty.");
        
        this.finalItems = items;
        this.items = items;
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
                double d = Util.euclideanDistance(items.get(p).x, items.get(p).y, items.get(i).x, items.get(i).y);
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
        items.clear();
        
        for( Integer i: indexes )
            items.add(finalItems.get(i));   
    }
    
}

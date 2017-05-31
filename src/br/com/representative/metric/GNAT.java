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
import java.util.Random;

/**
 *
 * @author wilson
 */
public class GNAT extends RepresentativeFinder {
    private List<Point2D.Double> finalItems;
    private List<Point2D.Double> items;
    private int k;
    
    public GNAT(List<Point2D.Double> items, int k) {
        if( items.isEmpty() )
            throw new IllegalArgumentException("The set must not be empty.");
        
        this.finalItems = items;
        this.items = items;
        this.k = k;
    }
    
    @Override
    public void execute() {
        
        List<Integer> pivots = new ArrayList<>();
        
        int qtdp = 3*k < items.size() ? 3*k : items.size();
        
        if( k >= items.size() ) {
            for( int i = 0; i < items.size(); ++i ) 
                pivots.add(i);
            
        } else {
            
            int randomIdx = new Random().nextInt(qtdp+1);
            pivots.add(randomIdx);
            --k;
            
            while( k > 0 ) {
                findBestPartPoint(pivots, items);
                k--;
            }        
        } 
    }
    
    private void findBestPartPoint(List<Integer> pivots, List<Point2D.Double> set) {
        double dist = Double.MIN_VALUE;
        int pivot = -1;
        
        for( int i = 0; i < set.size(); ++i ) {
            
            if( pivots.contains(i) )
                continue;
            
            double menor = Double.MAX_VALUE;
            for( int j = 0; j < pivots.size(); ++j ) {
                double d = Util.euclideanDistance(set.get(i).x, set.get(i).y, set.get(pivots.get(j)).x, set.get(pivots.get(j)).y);
                menor = Math.min(d, menor);
            }
            
            if( menor > dist ) {
                pivot = i;
                dist = menor;
            }            
        }
        
        if( pivot != -1 ) {
            
            pivots.add(pivot);
        }
    }

    @Override
    public void filterData(int[] indexes) {
        items.clear();
        
        for( Integer i: indexes )
            items.add(finalItems.get(i));   
    }
    
}

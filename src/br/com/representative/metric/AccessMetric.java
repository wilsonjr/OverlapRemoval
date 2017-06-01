/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.metric;

import br.com.representative.RepresentativeFinder;
import java.awt.geom.Point2D;
import java.util.List;

/**
 *
 * @author Windows
 */
public abstract class AccessMetric extends RepresentativeFinder {
    protected List<Point2D.Double> finalItems;
    protected List<Point2D.Double> items;
    
    public AccessMetric(List<Point2D.Double> items) {
        this.finalItems = items;
        this.items = items;
    }
    
    @Override
    public void filterData(int[] indexes) {
        items.clear();
        
        for( Integer i: indexes )
            items.add(finalItems.get(i));   
    }
    
}

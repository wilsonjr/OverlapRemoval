/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering;

import br.com.representative.RepresentativeFinder;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Windows
 */
public abstract class Clustering extends RepresentativeFinder {
    protected List<Point.Double> items;
    
    public Clustering(List<Point.Double> items) {
        super();
        if( items == null )
            throw new NullPointerException("Data cannot be null");
        this.items = items;
    }

    public Clustering() {}
    
    public List<Point.Double> getItems() {
        return items;
    }
    
    @Override
    public void filterData(int[] indexes) {
        List<Point.Double> temp = new ArrayList<>();
        items.forEach((p) -> { temp.add(new Point.Double(p.x, p.y)); });
        items.clear();
        
        for( Integer i: indexes )
            items.add(temp.get(i));        
    }
}

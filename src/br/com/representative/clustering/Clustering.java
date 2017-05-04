/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering;

import java.awt.Point;
import java.util.List;

/**
 *
 * @author Windows
 */
public abstract class Clustering {
    protected List<Point.Double> items;
    
    public Clustering(List<Point.Double> items) {
        if( items == null )
            throw new NullPointerException("Data cannot be null");
        this.items = items;
    }

    public Clustering() {}
    
    public List<Point.Double> getItems() {
        return items;
    }
}

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
public abstract class Partitioning extends Clustering {
    protected List<List<Integer>> clusters;
    
    public Partitioning(List<Point.Double> items) {
        super(items);
    }
    
    public Partitioning() {
        super();
    }    
    
    public List<List<Integer>> getClusters() {
        return clusters;
    }
    
}

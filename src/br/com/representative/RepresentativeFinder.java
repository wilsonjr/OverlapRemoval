/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative;

import br.com.representative.clustering.Clustering;
import java.awt.Point;
import java.util.List;

/**
 *
 * @author Windows
 */
public abstract class RepresentativeFinder extends Clustering {
    protected int[] representatives;
    
    public RepresentativeFinder() {
        super();
    }
    
    public RepresentativeFinder(List<Point.Double> items) {
        super(items);
    }
    
    public abstract void execute();
    
    public int[] getRepresentatives() {
        return representatives;
    }
    
    
}

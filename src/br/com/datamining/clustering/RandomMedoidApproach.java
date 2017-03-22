/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.datamining.clustering;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author Windows
 */
public class RandomMedoidApproach implements InitialMedoidApproach {

    @Override
    public Point2D.Double[] getInitialGuess(ArrayList<Point2D.Double> items, int k) {
        
        ArrayList<Integer> selected = new ArrayList<>();
        Point2D.Double[] medoids = new Point2D.Double[k];
        int count = 0;
        
        while( count < k ) {
            int i = (int) (Math.random() * (items.size() - 1));
            if( !selected.contains(i) ) {
                medoids[count++] = items.get(i);
                selected.add(i);
            }
        }
        
        return medoids;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.datamining.clustering;

import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Windows
 */
public interface InitialMedoidApproach {    
    
    Point.Double[] getInitialGuess(ArrayList<Point.Double> items, int k);
    
}

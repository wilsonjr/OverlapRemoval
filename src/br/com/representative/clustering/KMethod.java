/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering;

import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Windows
 */
public abstract class KMethod extends Partitioning {
    
    protected final int K;
    protected int ITER;
    protected Point.Double[] centroids;    
    protected InitialMedoidApproach initialGuessApproach;    
        
    public KMethod(ArrayList<Point.Double> items, InitialMedoidApproach initialGuessApproach, int k) {
        super(items);
        this.initialGuessApproach = initialGuessApproach;
        this.K = k;
        this.ITER = 30;
    }
    
    public Point.Double[] getCentroids() {
        return centroids;
    }

    public InitialMedoidApproach getInitialGuessApproach() {
        return initialGuessApproach;
    }
    
    public void setMaxIterations(int max) {
        ITER = max;
    }
}

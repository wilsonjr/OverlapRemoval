/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.analysis;

import br.com.methods.utils.Util;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wilson
 */
public class ClusterAnalysis implements RepresentativeAnalysis {

    private int[] representative;
    private Point2D.Double[] points;
    
    public ClusterAnalysis(int[] representative, Point2D.Double[] points) {
        this.representative = representative;
        this.points = points;
    }
    
    
    @Override
    public double init() {
        
        double variance = 0.0, mean = (double)points.length/(double)representative.length;
        
        Map<Integer, List<Integer>> indexes = Util.createIndex(representative, points);
        
        variance = indexes.entrySet().stream().map(
                (v) -> (v.getValue().size()-mean)*(v.getValue().size()-mean)
        ).reduce(
                variance, 
                (accumulator, _item) -> accumulator + _item        
        );
        
        variance /= representative.length;
        
        
        indexes.entrySet().stream().forEach((v)->System.out.println(">> "+v.getValue().size()));
        
        System.out.printf("Value for Cluster Median metric: %f\n", mean);
        System.out.printf("Value for Cluster Standard Deviation metric: %f\n", Math.sqrt(variance));
        return variance;
    }
    
    @Override
    public String toString() {
        return "Cluster Variance";
    }
    
}

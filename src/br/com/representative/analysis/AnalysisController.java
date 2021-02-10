/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.analysis;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wilson
 */
public class AnalysisController {
    
    public static void execute(int[] indexes, double[][] similarity, Point2D.Double[] points) {
        
        Redundancy redundancy = new Redundancy(indexes, similarity);
        Coverage coverage = new Coverage(indexes, similarity);
        ClusterAnalysis cAnalysis = new ClusterAnalysis(indexes, points);
        
        
        Arrays.asList(redundancy, coverage, cAnalysis).stream().forEach((v)->execute(v));       
        
    }
    
    public static void execute(RepresentativeAnalysis representativeAnalysis) {
        
        double value = representativeAnalysis.init();
        
        
        System.out.printf("Value for %s metric: %f\n",representativeAnalysis.toString(), value);     
    }
    
    
}

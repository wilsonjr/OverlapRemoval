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
    
    public static void execute(int[] indexes, double[][] similarity, Point2D.Double[] pts) {
        
        Redundancy redundancy = new Redundancy(indexes, similarity);
        Coverage coverage = new Coverage(indexes, similarity);
        Entropy entropy = new Entropy(pts);
        
        Arrays.asList(redundancy, coverage, entropy).stream().forEach((v)->execute(v));       
        
    }
    
    public static void execute(RepresentativeAnalysis representativeAnalysis) {
        
        long start = System.currentTimeMillis();
        representativeAnalysis.init();
        long finish = System.currentTimeMillis();
        Logger.getLogger(AnalysisController.class.getName()).log(Level.INFO, "Execution time "+representativeAnalysis.toString()+
                                                                             ": {0}s ",((finish-start)/1000.0));
    }
    
    
}

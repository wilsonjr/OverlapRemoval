/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.overlayanalisys.euclideandistance;

import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import br.com.overlayanalisys.definition.Metric;
import java.util.ArrayList;

/**
 *
 * @author wilson
 */
public class EuclideanDistance implements Metric {
    
    @Override
    public double execute(ArrayList<OverlapRect> pts1, ArrayList<OverlapRect> pts2) {
        double sum = 0.0;
        
        for( int i = 0; i < pts1.size(); ++i )        
            sum += Util.euclideanDistance(pts1.get(i).getCenterX(), pts1.get(i).getCenterY(), 
                                            pts2.get(i).getCenterX(), pts2.get(i).getCenterY());
        
        return sum/(double)pts1.size();
    }
    
    public double[] executeInstanceByInstance(ArrayList<OverlapRect> pts1, ArrayList<OverlapRect> pts2) {
        double[] distances = new double[pts1.size()];
        
        for( int i = 0; i < pts1.size(); ++i )
            distances[i] = Util.euclideanDistance(pts1.get(i).getCenterX(), pts1.get(i).getCenterY(), 
                                            pts2.get(i).getCenterX(), pts2.get(i).getCenterY());
        
        return distances;
    }
    
}

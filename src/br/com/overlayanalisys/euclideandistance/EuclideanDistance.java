/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.overlayanalisys.euclideandistance;

import br.com.metodos.utils.Retangulo;
import br.com.metodos.utils.Util;
import br.com.overlayanalisys.definition.Metric;
import java.util.ArrayList;

/**
 *
 * @author wilson
 */
public class EuclideanDistance implements Metric {
    
    @Override
    public double execute(ArrayList<Retangulo> pts1, ArrayList<Retangulo> pts2) {
        double sum = 0.0;
        
        for( int i = 0; i < pts1.size(); ++i )        
            sum += Util.distanciaEuclideana(pts1.get(i).getCenterX(), pts1.get(i).getCenterY(), 
                                            pts2.get(i).getCenterX(), pts2.get(i).getCenterY());
        
        return sum/(double)pts1.size();
    }
}

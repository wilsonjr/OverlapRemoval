/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.datamining.clustering;

import java.util.List;

/**
 *
 * @author wilson
 */
public class SingleLinkageStrategy implements LinkageStrategy {
    
    @Override
    public double distance(List<Double> d) {        
        return d.stream().min(Double::compareTo).get();       
    }
    
    
}

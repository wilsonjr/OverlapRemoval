/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.datamining.clustering;

import java.util.List;

/**
 *
 * @author Windows
 */
public class CompleteLinkageStrategy implements LinkageStrategy {
    
    @Override
    public double distance(List<Double> d) {
        return d.stream().max(Double::compareTo).get();
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.datamining.clustering.Hierarchical;

import java.util.List;

/**
 *
 * @author Windows
 */
public interface LinkageStrategy {
    
    double distance(List<Double> d);
    
}

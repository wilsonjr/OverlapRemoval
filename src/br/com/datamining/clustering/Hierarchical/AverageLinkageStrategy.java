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
public class AverageLinkageStrategy implements LinkageStrategy {
    
    @Override
    public double distance(List<Double> d) {    
        double sum = 0;
        return d.stream().map((dlb) -> dlb).reduce(sum, (accumulator, _item) -> accumulator + _item)/d.size();
    }
    
}

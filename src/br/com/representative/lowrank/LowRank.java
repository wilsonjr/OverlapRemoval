/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.lowrank;

import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import java.util.List;

/**
 *
 * @author Windows
 */
public abstract class LowRank extends RepresentativeFinder {
    
    protected final double[][] items;
    
    public LowRank(List<? extends List<Double>> items) {
        super();
        this.items = Util.elementMatrix(items);
    }
    
    
}

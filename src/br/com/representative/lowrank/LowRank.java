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
    
    protected double[][] items;
    protected final double[][] itemsFinal;
    
    public LowRank(List<? extends List<Double>> items) {
        super();
        this.itemsFinal = Util.elementMatrix(items);
        this.items = itemsFinal;
    }
    
    @Override
    public void filterData(int[] indexes) {
        double[][] newItems = new double[itemsFinal.length][indexes.length];
        for( int j = 0; j < indexes.length; ++j )
            for( int i = 0; i < itemsFinal.length; ++i ) 
                newItems[i][j] = itemsFinal[i][indexes[j]];
        items = newItems;        
    }
}

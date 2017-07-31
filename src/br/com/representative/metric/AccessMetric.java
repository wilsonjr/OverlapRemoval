/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.metric;

import br.com.methods.utils.Vect;
import br.com.representative.RepresentativeFinder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Windows
 */
public abstract class AccessMetric extends RepresentativeFinder {
    protected List<Vect> finalItems;
    protected List<Vect> items;
    
    public AccessMetric(List<Vect> items) {       
        if( items.isEmpty() )
            throw new IllegalArgumentException("The set must not be empty.");
        
        this.finalItems = items;
        this.items = new ArrayList<>();
        
        items.forEach((v)->this.items.add(v));
    }
    
    @Override
    public void filterData(int[] indexes) {
        items.clear();
        
        for( Integer i: indexes ) 
            items.add(finalItems.get(i));   
    }
    
}

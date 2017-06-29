/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering;

import br.com.methods.utils.Vect;
import br.com.representative.RepresentativeFinder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Windows
 */
public abstract class Clustering extends RepresentativeFinder {
    protected List<Vect> items;
    protected final List<Vect> itemsFinal;
    
    public Clustering(List<Vect> items) {
        super();
        if( items == null )
            throw new NullPointerException("Data cannot be null");
        
        this.itemsFinal = new ArrayList<>(items);
        this.items = new ArrayList<>();
        this.itemsFinal.stream().forEach((e)->this.items.add(e));
    }

    
    public List<Vect> getItems() {
        return items;
    }
    
    @Override
    public void filterData(int[] indexes) {
        items.clear();
        
        for( Integer i: indexes )
            items.add(itemsFinal.get(i));        
    }
}

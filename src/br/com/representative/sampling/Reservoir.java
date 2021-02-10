/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.sampling;

import br.com.methods.utils.Vect;
import br.com.representative.clustering.Partitioning;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Windows
 */
public class Reservoir extends Partitioning {
    
    private int k = 0;

    public Reservoir(List<Vect> items, int k) {
        super(items);
        
        this.k = k;
    }

    @Override
    public void execute() {
        
        int[] R = new int[k];        
        
        for( int i = 0; i < k; ++i )
            R[i] = i; 
        
        for( int i = k; i < items.size(); ++i ) {
            
            int j = (int) Math.floor(Math.random() * i);
            if( j < k )
                R[j] = i;
        }
                
        representatives = R;
    }
    
    
    @Override
    public void filterData(int[] indexes) {
        super.filterData(indexes);
        
        k = (int)(indexes.length*0.1);
        if( k == 0 )
            k = 1;        
    }
 
}

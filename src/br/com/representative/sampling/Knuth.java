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
public class Knuth extends Partitioning {
    
    private int k = 0;

    public Knuth(List<Vect> items, int k) {
        super(items);
        
        this.k = k;
    }

    @Override
    public void execute() {
        
        int n = k;
        List<Integer> sample = new ArrayList<>();
        int i = 0;
        
        for( int index = 0; index < items.size(); ++index ) {
            process(sample, index, ++i, n);
        }
        
        List<Integer> selected = process(sample, items.size()-1, ++i, n);
        int[] R = new int[selected.size()];
        
        for( int index = 0; index < selected.size(); ++index ) {
            R[index] = selected.get(index);
        }
        
        representatives = R;
    }

    private List<Integer> process(List<Integer> sample, int item, int i, int n) {
        if( i <= n ) {
            sample.add(item);
        } else if( Math.floor(Math.random() * i) < n ) {
            sample.set((int)Math.floor(Math.random() * n), item);
        }
        
        return sample;
    }
    
    @Override
    public void filterData(int[] indexes) {
        super.filterData(indexes);
        
        k = (int) (indexes.length*0.1);
        if( k == 0 )
            k = 1;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.metric;

import br.com.methods.utils.Vect;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author wilson
 */
public class GNAT extends AccessMetric {
    private int k;
    
    public GNAT(List<Vect> items, int k) {
        super(items);
        
        this.k = k;
    }
    
    
    @Override
    public void execute() {
        
        List<Integer> pivots = new ArrayList<>();
        
        int qtdp = 3*k < items.size() ? 3*k : items.size();
        
        if( k >= items.size() ) {
            for( int i = 0; i < items.size(); ++i ) 
                pivots.add(i);
            
        } else {
            
            int randomIdx = new Random().nextInt(qtdp+1);
            pivots.add(randomIdx);
            --k;
            
            while( k > 0 ) {
                findBestPartPoint(pivots, items);
                k--;
            }        
        } 
        
        representatives = pivots.stream().mapToInt((e)->e).toArray();
    }
    
    private void findBestPartPoint(List<Integer> pivots, List<Vect> set) {
        double dist = Double.MIN_VALUE;
        int pivot = -1;
        
        for( int i = 0; i < set.size(); ++i ) {
            
            if( pivots.contains(i) )
                continue;
            
            double menor = Double.MAX_VALUE;
            for( int j = 0; j < pivots.size(); ++j ) {
                //double d = Util.euclideanDistance(set.get(i).x, set.get(i).y, set.get(pivots.get(j)).x, set.get(pivots.get(j)).y);
                double d = set.get(i).distance(set.get(pivots.get(j)));
                menor = Math.min(d, menor);
            }
            
            if( menor > dist ) {
                pivot = i;
                dist = menor;
            }            
        }
        
        if( pivot != -1 ) {
            
            pivots.add(pivot);
        }
    }

    @Override
    public void filterData(int[] indexes) {
        super.filterData(indexes);   
        k = (int)(indexes.length*0.1);
        if( k == 0 )
            k = 1;
    }

    public void setK(int k) {
        this.k = k;
    }
        
}

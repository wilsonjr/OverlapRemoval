/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.ksvd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author wilson
 */
public class KSvd {
    private List<List<Double>> items;
    private int dictsize;
    
    public KSvd(List<List<Double>> items, int dictsize) {
        
    }
    
    public void execute() {
        
        
    }
    
    
    
    
    private double[][] initialDict() {
        int n = items.size();        
        double[][] dict = new double[n][dictsize];
        
        List<Integer> ids = new ArrayList<>();
        for( int i = 0; i < n; ++i )
            ids.add(i);
        
        Collections.shuffle(ids);
        
        for( int i = 0; i < n; ++i ) 
            for( int j = 0; j < dictsize; ++j )
                dict[i][j] = items.get(i).get(ids.get(j));        
        
        return dict;
    }
    
}

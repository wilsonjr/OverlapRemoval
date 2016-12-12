/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.pivot;

import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author wilson
 */
public class GNAT {
    
    
    public void selectPivots(ArrayList<OverlapRect> conjunto, int k) {
        
        if( conjunto.isEmpty() )
            throw new IllegalArgumentException("É necessário um conjunto com ao menos um elemento.");
        
        int qtdp = 3*k < conjunto.size() ? 3*k : conjunto.size();
        ArrayList<OverlapRect> pivots = new ArrayList<>();
        
        if( k >= conjunto.size() ) {
            for( int i = 0; i < conjunto.size(); ++i ) {
                conjunto.get(i).setPivot(true);
                pivots.add(conjunto.get(i));
            }
        } else {
            
            int randomIdx = new Random().nextInt(qtdp+1);
            conjunto.get(randomIdx).setPivot(true);
            pivots.add(conjunto.get(randomIdx));
            --k;
            
            while( k > 0 ) {
                findBestPartPoint(pivots, conjunto);
                k--;
            }        
        }       
    }
    
    private void findBestPartPoint(ArrayList<OverlapRect> pivots, ArrayList<OverlapRect> conjunto) {
        double dist = Double.MIN_VALUE;
        int pivot = -1;
        
        for( int i = 0; i < conjunto.size(); ++i ) {
            
            if( conjunto.get(i).isPivot() )
                continue;
            
            double menor = Double.MAX_VALUE;
            for( int j = 0; j < pivots.size(); ++j ) {
                double d = Util.distanciaEuclideana(conjunto.get(i).getCenterX(), conjunto.get(i).getCenterY(), 
                                                    pivots.get(j).getCenterX(), pivots.get(j).getCenterY());
                menor = Math.min(d, menor);
            }
            
            if( menor > dist ) {
                pivot = i;
                dist = menor;
            }            
        }
        
        if( pivot != -1 ) {
            conjunto.get(pivot).setPivot(true);            
            pivots.add(conjunto.get(pivot));
        }
    }
    
}

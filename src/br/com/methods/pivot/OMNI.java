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
public class OMNI {    
    
    public void selectPivots(ArrayList<OverlapRect> conjunto, int cardinalidade) {
        if( conjunto.size() < 2 ) 
            throw new IllegalArgumentException("É necessário um conjunto com ao menos dois elementos.");
                
        int s1 = new Random().nextInt(conjunto.size()+1), f1 = -1, f2 = -1;
        double dist = Double.MIN_VALUE;
        
        for( int i = 0; i < conjunto.size(); ++i ) {
            if( i == s1 )
                continue;
            
            double d = Util.distanciaEuclideana(conjunto.get(i).getCenterX(), conjunto.get(i).getCenterY(), 
                                                conjunto.get(s1).getCenterX(), conjunto.get(s1).getCenterY());
            if( d > dist ) {
                dist = d;
                f1 = i;
            }
        }
        
        dist = Double.MIN_VALUE;
        for( int i = 0; i < conjunto.size(); ++i ) {
            if( i == f1 )
                continue;
            
            double d = Util.distanciaEuclideana(conjunto.get(i).getCenterX(), conjunto.get(i).getCenterY(), 
                                                conjunto.get(f1).getCenterX(), conjunto.get(f1).getCenterY());
            if( d > dist ) {
                dist = d;
                f2 = i;
            }
        }
        
        double edge = Util.distanciaEuclideana(conjunto.get(f1).getCenterX(), conjunto.get(f1).getCenterY(), 
                                               conjunto.get(f2).getCenterX(), conjunto.get(f2).getCenterY());
        
        cardinalidade -= 2;
        
        findOtherPivots(f1, f2, conjunto, cardinalidade, edge);
    }

    private void findOtherPivots(int f1, int f2, ArrayList<OverlapRect> conjunto, int cardinalidade, double edge) {
        ArrayList<OverlapRect> pivots = new ArrayList<>();
        conjunto.get(f1).setPivot(true);
        conjunto.get(f2).setPivot(true);
        pivots.add(conjunto.get(f1));
        pivots.add(conjunto.get(f2));
        
        while( cardinalidade > 0 ) {
            double error = Double.MAX_VALUE;
            int idx = -1;
            
            for( int i = 0; i < conjunto.size(); ++i ) {
                if( conjunto.get(i).isPivot() )
                    continue;
                
                double sum = 0.0;
                
                for( int k = 0; k < pivots.size(); ++k ) {
                    double d = Util.distanciaEuclideana(pivots.get(k).getCenterX(), pivots.get(k).getCenterY(), 
                                                        conjunto.get(i).getCenterX(), conjunto.get(i).getCenterY());
                    sum += Math.abs(edge-d);
                }
                
                if( sum < error ) {
                    error = sum;
                    idx = i;
                }                
            }
            
            if( idx != -1 ) {
                conjunto.get(idx).setPivot(true);
                pivots.add(conjunto.get(idx));
            }
            
            cardinalidade--;
        }
    }
    
}

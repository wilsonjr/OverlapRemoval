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

/**
 *
 * @author wilson
 */
public class SSS {   
    
    public void selectPivots(ArrayList<OverlapRect> conjunto, double alpha, double maxDistance) {
        
        if( conjunto.isEmpty() )
            throw new IllegalArgumentException("É necessário um conjunto com ao menos um elemento.");
        
        ArrayList<OverlapRect> pivots = new ArrayList<>();
        
        conjunto.get(0).setPivot(true);
        pivots.add(conjunto.get(0));
        
        for( int i = 1; i < conjunto.size(); ++i ) {
            boolean pivot = true;
            
            for( OverlapRect r: pivots ) {
                double d = Util.euclideanDistance(r.getCenterX(), r.getCenterY(), 
                                                    conjunto.get(i).getCenterX(), conjunto.get(i).getCenterY());
                if( d < maxDistance*alpha ) {
                    pivot = false;
                    break;
                }
            }
            
            if( pivot ) {
                conjunto.get(i).setPivot(true);
                pivots.add(conjunto.get(i));                        
            }
        }
        
    }
    
}

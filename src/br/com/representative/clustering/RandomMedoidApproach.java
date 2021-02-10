/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering;

import br.com.methods.utils.Vect;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Windows
 */
public class RandomMedoidApproach implements InitialMedoidApproach {

    @Override
    public Vect[] getInitialGuess(List<Vect> items, int k) {
        
        ArrayList<Integer> selected = new ArrayList<>();
        Vect[] medoids = new Vect[k];
        int count = 0;
        
        while( count < k ) {
            int i = (int) (Math.random() * (items.size() - 1));
            if( !selected.contains(i) ) {
                medoids[count++] = items.get(i);
                selected.add(i);
            }
        }
        
        return medoids;
    }
    
}

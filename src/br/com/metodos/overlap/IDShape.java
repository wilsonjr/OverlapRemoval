/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap;

import br.com.metodos.utils.Retangulo;

/**
 *
 * @author Thaís
 */
public class IDShape {
    private Retangulo rect;
    private int distance;
        
    public IDShape(Retangulo rect, int distance) {
        this.rect = rect;
        this.distance = distance;
    }
    
    public Retangulo getRect() {
        return rect;
    }
    
    public int getDistance() {
        return distance;
    }   
            
            
            
            
   
}

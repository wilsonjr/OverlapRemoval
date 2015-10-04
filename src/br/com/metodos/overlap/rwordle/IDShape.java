/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.rwordle;

import br.com.metodos.utils.Retangulo;

/**
 *
 * @author Thaís
 */
public class IDShape {
    private Retangulo rect;
    private double distance;
        
    public IDShape(Retangulo rect, double distance) {
        this.rect = rect;
        this.distance = distance;
    }
    
    public Retangulo getRect() {
        return rect;
    }
    
    public double getDistance() {
        return distance;
    }       
   
}

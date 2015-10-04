/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.utils;

import java.awt.Color;
import java.awt.Rectangle;

/**
 *
 * @author wilson
 */
public class Retangulo extends Rectangle.Double {    
    public Color cor;
    public int numero;
    private double epsX, epsY;
    
    public Retangulo() {
        epsX = epsY = 0;
    }
        
    public Retangulo(double xMin, double yMin, double width, double height, Color cor, int i) {        
        super(xMin, yMin, width, height);        
        this.cor = cor;
        this.numero = i;
        epsX = epsY = 0;       
    }
    
    public void moveX(double step) {
        setRect(step-width/2, y, (width+(step-width/2))-(step-width/2), height);
    }
    
    public void moveY(double step) {
        setRect(x, step-height/2, width, (height+(step-width/2))-(step-height/2));
    }
    
    @Override
    public double getMaxX() { 
        return super.getMaxX()+epsX; 
    }
    
    @Override
    public double getMaxY() { 
        return super.getMaxY()+epsY; 
    }
        
    public double olapX(Retangulo r) {
        return (width + r.width)/2. - Math.abs(getCenterX() - r.getCenterX());
    }    
    
    public double olapY(Retangulo r) {
        return (height + r.height)/2 - Math.abs(getCenterY() - r.getCenterY());
    }
    
    @Override
    public String toString() {
        return "<"+getMinX()+", "+getMaxX()+", "+getMinY()+", "+getMaxY()+">";
    }
    
    
}

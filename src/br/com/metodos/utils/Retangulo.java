/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.utils;

/**
 *
 * @author wilson
 */
public class Retangulo  {    
    private double ux, uy, lx, ly;    
    private double epsX = 0, epsY = 0;
    private int id;
    
    public Retangulo(double minX, double minY, double width, double height, int id) {
        ux = minX;
        lx = minX+width;
        uy = minY;
        ly = minY+height;
        this.id = id;
    }    
        
    public Retangulo(double minX, double minY, double width, double height) {
        this(minX, minY, width, height, -1);
    }    
    
    public int getId() {
        return id;
    }
    
    public void setLX(double x) {
        lx = x;
    }
    
    public void setUX(double x) {
        double w = getWidth();
        ux = x;
        lx = ux+w;
    }
    
    public void setLY(double y) {
        ly = y;
    }
    
    public void setUY(double y) {
        double h = getHeight();
        uy = y;
        ly = uy+h;
    }
    
    public void setWidth(double w) {
        setLX(ux+w);
    }
    
    public void setHeight(double h)  {
        setLY(uy+h);
    }
    
    
    public double getLX() {
        return lx+epsX; 
    }
    
    public double getLY() { 
        return ly+epsY; 
    }
    
    public double getUX() { 
        return ux; 
    }
    
    public double getUY() { 
        return uy; 
    }
    
    public double getCenterX() { 
        return ux+getWidth()/2.0; 
    }
    
    public double getCenterY() { 
        return uy+getHeight()/2.0; 
    }
    
    public double getWidth()  { 
        return lx-ux; 
    }
    
    public double getHeight()  { 
        return ly-uy; 
    }
    
    public void moveX(double x) {
        double q = (x-getWidth()/2.);
        lx = q+getWidth();
        ux = q;        
    }
    
    public void moveY(double y) {
        double q = (y-getHeight()/2.);
        ly = q+getHeight();
        uy = q;
    }
    
    public double olapX(Retangulo r) {
        return (getWidth() + r.getWidth())/2. - Math.abs(getCenterX() - r.getCenterX());
    }    
    
    public double olapY(Retangulo r) {
        return (getHeight() + r.getHeight())/2 - Math.abs(getCenterY() - r.getCenterY());
    }
    
    @Override
    public String toString() {
        return "<"+ux+", "+lx+", "+uy+", "+ly+">";
    }

    @Override
    public boolean equals(Object obj) {
        Retangulo robj = (Retangulo) obj;
        return getCenterX() == robj.getCenterX() && getCenterY() == robj.getCenterY();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.ux) ^ (Double.doubleToLongBits(this.ux) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.uy) ^ (Double.doubleToLongBits(this.uy) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.lx) ^ (Double.doubleToLongBits(this.lx) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.ly) ^ (Double.doubleToLongBits(this.ly) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.epsX) ^ (Double.doubleToLongBits(this.epsX) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.epsY) ^ (Double.doubleToLongBits(this.epsY) >>> 32));
        hash = 97 * hash + this.id;
        return hash;
    }
    
    public void setId(int id) {
        this.id = id;
    }
      
    
    
}

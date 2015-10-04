/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

/**
 *
 * @author Thaís
 */
public class RetanguloVPSC {
    private double minX, maxX, minY, maxY;    
    private double xGap = 0, yGap = 0;
        
    public RetanguloVPSC(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }    
    
    public double getMaxX() { return maxX+xGap; }
    public double getMaxY() { return maxY+yGap; }
    public double getMinX() { return minX; }
    public double getMinY() { return minY; }
    
    public double getCenterX()  { return minX+getWidth()/2.0; }
    public double getCenterY()  { return minY+getHeight()/2.0; }
    public double getWidth()  { return getMaxX()-minX; }
    public double getHeight()  { return getMaxY()-minY; }
    
    public void moveMinX(double x) {
        maxX = x+getWidth();
        minX = x;    
    }
    
    public void moveMinY(double y) {
        maxY = y+getHeight();
        minY = y;       
    }
    
    public void moveCentreX(double x) {
        moveMinX(x-getWidth()/2.);    
    }
    
    public void moveCentreY(double y) {
        moveMinY(y-getHeight()/2.);    
    }
    
    
    public double olapx(RetanguloVPSC r) {
        /*if( getCenterX() <= r.getCenterX() && r.getMinX() <getMaxX() )
            return getMaxX()-r.getMinX();
        if( r.getCenterX() <= getCenterX() && getMinX() < r.getMaxX() )
           return r.getMaxX()-getMinX();
        return 0;*/
        return (getWidth()+r.getWidth())/2 - Math.abs(getCenterX()-r.getCenterX());
    }
    
    
    public double olapy(RetanguloVPSC r) {
       /* if( getCenterY() <= r.getCenterY() && r.getMinY() < getMaxY() )
            return getMaxY()-r.getMinY();
        if( r.getCenterY() <= getCenterY() && getMinY() < r.getMaxY() )
            return r.getMaxY()-getMinY();
        return 0;*/
        return (getHeight()+r.getHeight())/2 - Math.abs(getCenterY()-r.getCenterY());
    }
    
    @Override
    public String toString() {
        //return "X: "+getMinX()+", Y: "+getMinY()+", WIDTH: "+getWidth()+", HEIGHT: "+getHeight();
        return "<"+getMinX()+", "+getMaxX()+", "+getMinY()+", "+getMaxY()+">";
    }
    
    
}

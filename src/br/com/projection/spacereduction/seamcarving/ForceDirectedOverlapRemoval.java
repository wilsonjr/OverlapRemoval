/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.projection.spacereduction.seamcarving;

import br.com.methods.utils.Util;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 *
 * @author Windows
 */
public class ForceDirectedOverlapRemoval {
    
    private static double KR = 0.25;
    private int LIMIT = 1000;
    
    public ForceDirectedOverlapRemoval() {}
    
    public ForceDirectedOverlapRemoval(int limit) {
        this.LIMIT = limit;
    }
    
    public void run(Rectangle2D.Double[] elems) {
        
        for( int count = 0; thereAreOverlaps(elems) && count < LIMIT; ++count ) {
            
            for( int i = 0; i < elems.length; ++i ) {
                Point2D dxy = new Point2D.Double();
                double cnt = 0;
                
                for( int j = 0; j < elems.length; ++j ) {
                    if( i != j ) {
                        
                        // TODO check if this piece of code is equivalent to intersects()
                        
                        if( elems[i].getMaxX()-1 < elems[j].getMinX() )
                            continue;
                        
                        if( elems[i].getMinX()+1 > elems[j].getMaxX() )
                            continue;
                        
                        if( elems[i].getMaxY()-1 < elems[j].getMinY() )
                            continue;
                        
                        if( elems[i].getMinY()+1 > elems[j].getMaxY() )
                            continue;
                        
                        Point2D dir = computeForceIJ(elems[i], elems[j]);
                        dxy.setLocation(dxy.getX()-dir.getX(), dxy.getY()-dir.getY());
                        cnt++;
                    }
                }
                if( cnt == 0 )
                    continue;
                
                dxy.setLocation(dxy.getX()*(1.0/cnt), dxy.getY()*(1.0/cnt));
                
                elems[i].setRect(elems[i].getX() + dxy.getX(), elems[i].getY() + dxy.getY(), 
                        elems[i].width, elems[i].height);
            }
        }
        
    }
    
    private Point2D computeForceIJ(Rectangle2D.Double rectI, Rectangle2D.Double rectJ) {
        
        double hix = Math.min(rectI.getMaxX(), rectJ.getMaxX());
        double lox = Math.max(rectI.getMinX(), rectJ.getMinX());
        double hiy = Math.min(rectI.getMaxY(), rectJ.getMaxY());
        double loy = Math.max(rectI.getMinY(), rectJ.getMinY());
        
        double dx = (hix-lox) * 1.1;
        double dy = (hiy-loy) * 1.1;
        
        // f(a,b) = kr*min{dx, dy}
        double f = KR*Math.min(dx, dy);
        Point2D dir = new Point2D.Double(rectJ.getCenterX()-rectI.getCenterX(), rectJ.getCenterY()-rectI.getCenterY());
        double distanceToOrigin = Util.distanciaEuclideana(dir.getX(), dir.getY(), 0, 0);
        if( distanceToOrigin < 1e-5 ) {
            Random rand = new Random(2);
            dir = new Point2D.Double(-1 + 2*rand.nextDouble(), -1 + 2*rand.nextDouble());
        } else
            dir.setLocation(dir.getX()*(1.0/distanceToOrigin), dir.getY()*(1.0/distanceToOrigin));
            
        dir.setLocation(dir.getX()*f, dir.getY()*f);
        
        return dir;       
    }
    
    public boolean thereAreOverlaps(Rectangle2D.Double[] elems) {
        for( int i = 0; i < elems.length; ++i )
            for( int j = 0; j < elems.length; ++j )
                if( elems[i].intersects(elems[j]) )
                    return true;
        
        return false;
    }
    
    

    
    
    
}

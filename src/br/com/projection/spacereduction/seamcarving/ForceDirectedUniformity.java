/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.projection.spacereduction.seamcarving;

import br.com.methods.utils.Util;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Windows
 */
public class ForceDirectedUniformity {
    
    private static double SCALE_FACTOR = 0.1;
    private static double KR = 0.3;
    private int LIMIT = 500;
    
    public ForceDirectedUniformity() {}
    
    public ForceDirectedUniformity(int limit) {
        this.LIMIT = limit;
    }
    
    public void run(Rectangle2D.Double[] elems) {
        Rectangle2D.Double[] rect = new Rectangle2D.Double[elems.length];
        double maxHeight = Arrays.stream(elems).mapToDouble(x->x.height).max().orElse(0.0);
        double delta = maxHeight*SCALE_FACTOR;
        
        for( int i = 0; i < elems.length; ++i )
            rect[i] = new Rectangle2D.Double(elems[i].x, elems[i].y, elems[i].width+delta, elems[i].height+delta);
       
        for( int count = 0; thereAreOverlaps(elems) && count < LIMIT; ++count ) {
            
            for( int i = 0; i < rect.length; ++i ) {
                Point2D dxy = new Point2D.Double();
                double cnt = 0;
                
                for( int j = 0; j < rect.length; ++j ) {
                    if( i != j ) {
                        
                        // TODO check if this piece of code is equivalent to intersects()
                        
                        if( rect[i].getMaxX()-1 < rect[j].getMinX() )
                            continue;
                        
                        if( rect[i].getMinX()+1 > rect[j].getMaxX() )
                            continue;
                        
                        if( rect[i].getMaxY()-1 < rect[j].getMinY() )
                            continue;
                        
                        if( rect[i].getMinY()+1 > rect[j].getMaxY() )
                            continue;
                        
                        Point2D dir = computeForceIJ(rect[i], rect[j]);
                        dxy.setLocation(dxy.getX()-dir.getX(), dxy.getY()-dir.getY());
                        cnt++;
                    }
                }
                if( cnt == 0 )
                    continue;
                
                dxy.setLocation(dxy.getX()*(1.0/cnt), dxy.getY()*(1.0/cnt));
                
                rect[i].setRect(rect[i].getX() + dxy.getX(), rect[i].getY() + dxy.getY(), rect[i].width, rect[i].height);
            }
        }
        
        for( int i = 0; i < elems.length; ++i ) {
            elems[i].setRect(rect[i].x, rect[i].y, elems[i].width, elems[i].height);
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

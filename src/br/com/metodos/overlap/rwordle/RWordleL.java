/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.rwordle;

import br.com.metodos.utils.Retangulo;
import br.com.metodos.utils.Util;
import static br.com.metodos.utils.Util.distanciaEuclideana;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author wilson
 */
public class RWordleL {
    
    
    public static ArrayList<Retangulo> apply(ArrayList<Retangulo> rectangles, double alpha, boolean centralize) {
        
        /**
         * Finds the min and max x and y coordinates in rectangles
         */
        double xmin = rectangles.get(0).getMinX(), xmax = rectangles.get(0).getMaxX(), 
               ymin = rectangles.get(0).getMinY(), ymax = rectangles.get(0).getMaxY();
               
        for( Retangulo r: rectangles ) {
            if( r.getMinX() < xmin ) 
                xmin = r.getMinX();
            if( r.getMaxX() > xmax )
                xmax = r.getMaxX();
            if( r.getMinY() < ymin )
                ymin = r.getMinY();
            if( r.getMaxY() > ymax )
                ymax = r.getMaxY();
        }
        
         /**
         * Compute the center of mass of set 
         */ 
        double initialCenterX = (xmin+xmax)/2;
        double initialCenterY = (ymin+ymax)/2;
        
        
        /**
         * o perform a translation 
         */         
        ArrayList<Retangulo> novos = new ArrayList<>();
        for( int i = 0; i < rectangles.size(); ++i ) {
            double distance = Util.distanciaEuclideana(0, 0, rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY());
                        
            novos.add(new Retangulo((rectangles.get(i).x-distance), (rectangles.get(i).y-distance), rectangles.get(i).width, 
                         rectangles.get(i).height, rectangles.get(i).cor, rectangles.get(i).numero));            
        }
        
        /**
         * perform a rotation in alpha degrees 
         */         
        for( int i = 0; i < novos.size(); ++i ) {
            double x = novos.get(i).x;
            double y = novos.get(i).y;
            
            novos.get(i).x =  (x*Math.cos(Math.toRadians(alpha)) - y*Math.sin(Math.toRadians(alpha)));
            novos.get(i).y =  (x*Math.sin(Math.toRadians(alpha)) + y*Math.cos(Math.toRadians(alpha)));
        }
        
        /**
         * we perform another translation to bring the elements of their position
         */
        for( int i = 0; i < novos.size(); ++i ) {
            double aux = distanciaEuclideana(0, 0, rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY());
            novos.get(i).x += aux;
            novos.get(i).y += aux;
        }
        
        /**
         * Now, we sort the elements according with a scanline defined by angle alpha
         */        
        Collections.sort(novos, new Comparator<Retangulo>() {
            @Override
            public int compare(Retangulo o1, Retangulo o2) {
                return new Double(o1.x).compareTo(o2.x);
            }            
        });
        
        
        Rectangle.Double r = new Rectangle.Double();        
        xmin = 99999; xmax = -1; ymin = 99999; ymax = -1;
       
        double xlinha = 0, ylinha = 0;
        ArrayList<Retangulo> projected = new ArrayList<>();
        for( int i = 0; i < novos.size(); ++i ) {
            double angle = 3;
            double adjust = .5;
            
            boolean flag;  
            
            Area areaS = null;
            do {
                
                flag = true;
                
                /**
                 * Moves elements according with a spiral while has overlaps
                 */    
                double x = (novos.get(i).getCenterX()+xlinha) + Math.sin(angle)*angle*adjust;
                double y = (novos.get(i).getCenterY()+ylinha) + Math.cos(angle)*angle*adjust;                
                
                 /**
                 * creates a area object for simple check for overlaps
                 */    
                Shape s = new Rectangle.Double(x, y, novos.get(i).width, novos.get(i).height);
                areaS = new Area(s);
                
                /**
                 * searches for overlaps in projected set
                 */    
                for( Retangulo rect: projected ) {
                    Shape s1 = new Rectangle.Double(rect.x, rect.y, rect.width, rect.height);
                    Area areaS1 = new Area(s1);
                    areaS1.intersect(areaS);
                    if( !areaS1.isEmpty() ) {
                        flag = false;
                        break;
                    }                        
                } 
                angle += (0.5/angle); 
            } while( !flag );
                        
            projected.add(new Retangulo(areaS.getBounds().x, areaS.getBounds().y, 
                                        areaS.getBounds().width, areaS.getBounds().height,
                                        novos.get(i).cor, novos.get(i).numero));
                        
            /**
             * here, we discover the x and y amount to translate the position of elements
             * in case of user wants to recenter elements after each iteration
             */
            if( centralize ) {
                if( areaS.getBounds().getMinX() < xmin || areaS.getBounds().getMaxX() > xmax || 
                    areaS.getBounds().getMinY() < ymin || areaS.getBounds().getMaxY() > ymax ) {
                    
                    /**
                     * its not necessary search throw all elements at each time
                     */
                    xmin = Math.min(xmin, areaS.getBounds().getMinX());
                    xmax = Math.max(xmax, areaS.getBounds().getMaxX());
                    ymin = Math.min(ymin, areaS.getBounds().getMinY());
                    ymax = Math.max(ymax, areaS.getBounds().getMaxY());

                    
                    r.setRect(xmin, ymin, Util.distanciaEuclideana(xmin, ymin, xmax, ymin), 
                                          Util.distanciaEuclideana(xmin, ymin, xmin, ymax));

                    xlinha = initialCenterX - r.getCenterX();
                    ylinha = initialCenterY - r.getCenterY();
                }
            }   
            
        }
        
        return projected;
                
    }
    
}

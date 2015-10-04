/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.rwordle;

import br.com.metodos.utils.Retangulo;
import br.com.metodos.utils.Util;
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
public class RWordleC {
    
    
    public static ArrayList<Retangulo> apply(ArrayList<Retangulo> rectangles) {
        
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
        double centerX = (xmin+xmax)/2;
        double centerY = (ymin+ymax)/2;
        
        ArrayList<IDShape> shapes = new ArrayList<>();
        for( int i = 0; i < rectangles.size(); ++i ) {
            shapes.add(new IDShape(new Retangulo(rectangles.get(i).x, rectangles.get(i).y, rectangles.get(i).width, rectangles.get(i).height,
                                                 rectangles.get(i).cor, rectangles.get(i).numero),
                                                 Util.distanciaEuclideana(centerX, centerY, rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY())));            
        }        
        
        /**
         * arranges the elements according to the mass center
         */
        Collections.sort(shapes, new Comparator<IDShape>() {
            @Override
            public int compare(IDShape o1, IDShape o2) {
                return new Double(o1.getDistance()).compareTo(o2.getDistance());
            }            
        });
            
        
        ArrayList<Retangulo> projected = new ArrayList<>();
        for( int i = 0; i < shapes.size(); ++i ) {
            /* this is a assumption */
            double angle = 2.;            
            boolean flag;  
            
            Area areaS = null;
            do {
                
                flag = true;
                
                /**
                 * Moves elements according with a spiral while has overlaps
                 */                 
                double x = shapes.get(i).getRect().getCenterX() + Math.sin(angle)*angle*0.5;
                double y = shapes.get(i).getRect().getCenterY() + Math.cos(angle)*angle*0.5;               
                                
                /**
                 * creates a area object for simple check for overlaps
                 */                 
                Shape s = new Rectangle.Double(x, y, shapes.get(i).getRect().width, shapes.get(i).getRect().height);
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
                
                /**
                 * we think that reduce the growth rate at each iteration is a good guess
                 */
                angle += (0.5/angle); 
            } while( !flag );
                        
            projected.add(new Retangulo(areaS.getBounds().x, areaS.getBounds().y, areaS.getBounds().getWidth(), 
                                        areaS.getBounds().getHeight(), shapes.get(i).getRect().cor, shapes.get(i).getRect().numero));
        }
        
        return projected;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.rwordle;

import br.com.methods.overlap.OverlapRemoval;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Classe de implementação do método RWordleC
 * @author wilson
 */
public class RWordleC implements OverlapRemoval {
    
    /**
     * Aplica o método RWordleC (Ordena os elementos de acordo com o centro para projeção)
     * @param rectangles Projeção inicial.
     * @return Projeção sem sobreposição.
     */
    @Override
    public Map<OverlapRect, OverlapRect> apply(List<OverlapRect> rectangles) {
        
        /**
         * Finds the min and max x and y coordinates in rectangles
         */
        double xmin = rectangles.get(0).getUX(), xmax = rectangles.get(0).getLX(), 
               ymin = rectangles.get(0).getUY(), ymax = rectangles.get(0).getLY();                      
        
        for( OverlapRect r: rectangles ) {
            if( r.getUX() < xmin ) 
                xmin = r.getUX();
            if( r.getLX() > xmax )
                xmax = r.getLX();
            if( r.getUY() < ymin )
                ymin = r.getUY();
            if( r.getLY() > ymax )
                ymax = r.getLY();
        }
        
        /**
         * Compute the center of mass of set 
         */        
        double centerX = (xmin+xmax)/2;
        double centerY = (ymin+ymax)/2;
        
        ArrayList<IDShape> shapes = new ArrayList<>();
        for( int i = 0; i < rectangles.size(); ++i ) {
            shapes.add(new IDShape(rectangles.get(i),
                                                 Util.euclideanDistance(centerX, centerY, 
                                                     rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY()), 
                                                    rectangles.get(i).getId()) );            
        }        
        
        /**
         * arranges the elements according to the mass center
         */
        Collections.sort(shapes, (IDShape o1, IDShape o2)->new Double(o1.getDistance()).compareTo(o2.getDistance()));
           
        ArrayList<OverlapRect> projected = new ArrayList<>();
        for( int i = 0; i < shapes.size(); ++i ) {
            /* this is a assumption */
            double angle = 3.;            
            
            boolean flag;  
            
            int current_try = 1;
            int max_tries = 5000;
            
            Area areaS = null;
            do {
                
                flag = true;
                
                /**
                 * Moves elements according with a spiral while has overlaps
                 */  
                
                
                double x = shapes.get(i).getRect().getCenterX() + Math.sin(angle)*angle*0.5;
                double y = shapes.get(i).getRect().getCenterY() + Math.cos(angle)*angle*0.5;  
                
                
                if( Util.bounding_box != null ) {
                    x = Util.checkBoundX((float) x);
                    y = Util.checkBoundY((float) y);
                } 
                                
                /**
                 * creates a area object for simple check for overlaps
                 */                 
                Shape s = new Rectangle.Double(x, y, shapes.get(i).getRect().getWidth(), shapes.get(i).getRect().getHeight());
                areaS = new Area(s);
                
                /**
                 * searches for overlaps in projected set
                 */                 
                for( OverlapRect rect: projected ) {                    
                    
                    Shape s1 = new Rectangle.Double(rect.getUX(), rect.getUY(), rect.getWidth(), rect.getHeight());
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
                angle += 0.3; 
            } while( !flag && current_try++ < max_tries);
                        
            
            projected.add(new OverlapRect(areaS.getBounds().x, areaS.getBounds().y, areaS.getBounds().getWidth(), 
                                        areaS.getBounds().getHeight(), shapes.get(i).getOriginalId()));
            
        }
        
        
        Map<OverlapRect, OverlapRect> projectedToReprojected = new HashMap<>();
        IntStream.range(0, rectangles.size()).forEach(i->projectedToReprojected.put(
                rectangles.get(projected.get(i).getId()), projected.get(i)));
        
        return projectedToReprojected;
    }
    
    @Override
    public String toString() {
        return "RWordleC";
    }
    
}

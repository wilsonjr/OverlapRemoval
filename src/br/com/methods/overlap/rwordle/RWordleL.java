    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.rwordle;

import br.com.methods.overlap.OverlapRemoval;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import static br.com.methods.utils.Util.euclideanDistance;
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
 * Classe de implementação do método RWordleL
 * @author wilson
 */
public class RWordleL implements OverlapRemoval {
    
    private double alpha;
    private boolean centralize;
    
    public RWordleL(double alpha, boolean centralize) {
        this.alpha = alpha;
        this.centralize = centralize;
    }
    
    
    /**
     * Aplica o método RWordleL (Ordena os elementos de acordo uma linha de varredura com orientação especificada pelo parâmetro 'alpha')
     * @param rectangles Projeção inicial.
     * @return Retângulos sem sobreposição.
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
        double initialCenterX = (xmin+xmax)/2;
        double initialCenterY = (ymin+ymax)/2;
        
        
        /**
         * o perform a translation 
         */         
        ArrayList<OverlapRect> novos = new ArrayList<>();
        for( int i = 0; i < rectangles.size(); ++i ) {
            double distance = Util.euclideanDistance(0, 0, rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY());
                        
            novos.add(new OverlapRect((rectangles.get(i).getUX()-distance), (rectangles.get(i).getUY()-distance), 
                                    rectangles.get(i).getWidth(), rectangles.get(i).getHeight(), rectangles.get(i).getId()));            
        }
        
        /**
         * perform a rotation in alpha degrees 
         */         
        for( int i = 0; i < novos.size(); ++i ) {
            double x = novos.get(i).getUX();
            double y = novos.get(i).getUY();
            
            novos.get(i).setUX((x*Math.cos(Math.toRadians(alpha)) - y*Math.sin(Math.toRadians(alpha))));
            novos.get(i).setUY((x*Math.sin(Math.toRadians(alpha)) + y*Math.cos(Math.toRadians(alpha))));
        }
        
        /**
         * we perform another translation to bring the elements of their position
         */
        for( int i = 0; i < novos.size(); ++i ) {
            double aux = euclideanDistance(0, 0, rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY());
            novos.get(i).setUX(novos.get(i).getUX()+aux);
            novos.get(i).setUY(novos.get(i).getUY()+aux);
        }
        
        /**
         * Now, we sort the elements according with a scanline defined by angle alpha
         */        
        Collections.sort(novos, (OverlapRect o1, OverlapRect o2)->new Double(o1.getUX()).compareTo(o2.getUX()));
        
        
        Rectangle.Double r = new Rectangle.Double();        
        xmin = 99999; xmax = -1; ymin = 99999; ymax = -1;
       
        double xlinha = 0, ylinha = 0;
        ArrayList<OverlapRect> projected = new ArrayList<>();
        for( int i = 0; i < novos.size(); ++i ) {
            double angle = 3;
            double adjust = .5;
            
            boolean flag;  
            int max_tries = 5000;
            int current_try = 1;
            Area areaS = null;
            do {
                
                flag = true;
                
                /**
                 * Moves elements according with a spiral while has overlaps
                 */    
                double x = (novos.get(i).getCenterX()+xlinha) + Math.sin(angle)*angle*adjust;
                double y = (novos.get(i).getCenterY()+ylinha) + Math.cos(angle)*angle*adjust;                
                
                
                if( Util.bounding_box != null ) {
                    x = Util.checkBoundX((float) x);
                    y = Util.checkBoundY((float) y);
                } 
                                
                 /**
                 * creates a area object for simple check for overlaps
                 */    
                Shape s = new Rectangle.Double(x, y, novos.get(i).getWidth(), novos.get(i).getHeight());
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
                angle += (0.3); 
            } while( !flag  &&  current_try++ < max_tries );
                        
            projected.add(new OverlapRect(areaS.getBounds().x, areaS.getBounds().y, 
                                        areaS.getBounds().width, areaS.getBounds().height, novos.get(i).getId()));
                        
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

                    
                    r.setRect(xmin, ymin, Util.euclideanDistance(xmin, ymin, xmax, ymin), 
                                          Util.euclideanDistance(xmin, ymin, xmin, ymax));

                    xlinha = initialCenterX - r.getCenterX();
                    ylinha = initialCenterY - r.getCenterY();
                }
            }               
        }
        
        Map<OverlapRect, OverlapRect> projectedToReprojected = new HashMap<>();
        IntStream.range(0, rectangles.size()).forEach(
            i->projectedToReprojected.put(rectangles.get(projected.get(i).getId()), projected.get(i))
        );
        
        return projectedToReprojected;                
    }
    
    @Override
    public String toString() {
        return "RWordleL";
    }
}

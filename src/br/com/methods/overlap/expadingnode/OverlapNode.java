/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.expadingnode;

import br.com.methods.utils.OverlapRect;
import java.util.List;

/**
 *
 * @author wilson
 */
public class OverlapNode {
    
    
    private List<OverlapRect> instances;
    private OverlapRect boundingBox;
    
    public OverlapNode(List<OverlapRect> instances) {
        this.instances = instances;        
    }
    
    
    private void calculateBoundingBox() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -1.0, maxY = -1.0;
        
        
        for( OverlapRect e: instances ) {
            
            double x = e.getUX();
            double y = e.getUY();
            
            if( x < minX ) 
                minX = x;
            if( y < minY )
                minY = y;
            
            if( x > maxX )
                maxX = x;
            
            if( y > maxY )
                maxY = y;            
        }
        
        boundingBox = new OverlapRect(minX, minY, maxX-minX, maxY-minY);
    }
    
    
    
    
}

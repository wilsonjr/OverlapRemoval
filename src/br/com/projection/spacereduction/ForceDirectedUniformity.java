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
public class ForceDirectedUniformity extends ForceDirectedOverlapRemoval {
    
    private static double SCALE_FACTOR = 0.1;
    
    public ForceDirectedUniformity() {
        super(500);
    }
    
    public ForceDirectedUniformity(int limit) {
        super(limit);
        ForceDirectedOverlapRemoval.KR = 0.3;
    }
    
    @Override
    public void run(Rectangle2D.Double[] elems) {
        Rectangle2D.Double[] rect = new Rectangle2D.Double[elems.length];
        double maxHeight = Arrays.stream(elems).mapToDouble(x->x.height).max().orElse(0.0);
        double delta = maxHeight*SCALE_FACTOR;
        
        for( int i = 0; i < elems.length; ++i )
            rect[i] = new Rectangle2D.Double(elems[i].x, elems[i].y, elems[i].width+delta, elems[i].height+delta);
                
        super.run(rect);
        
        for( int i = 0; i < elems.length; ++i ) 
            elems[i].setRect(rect[i].x, rect[i].y, elems[i].width, elems[i].height);       
        
    }
        
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.explore.explorertree.util;

import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.util.List;

/**
 *
 * @author wilson
 */
public interface RepulsiveNode {
    
    
    default public double maxDistance(List<OverlapRect> elems, int representative) {
        double maxDistance = -1;
        for( int i = 0; i < elems.size(); ++i ) {
            double d = Util.euclideanDistance(elems.get(i).x, elems.get(i).y, 
                                              elems.get(representative).x, elems.get(representative).y);
            if( d > maxDistance )
                maxDistance = d;
        }
        
        return maxDistance;        
    }
    
    public List<OverlapRect> repulsive(List<OverlapRect> elems, int representative, double minWeight, double maxWeight);
    
}

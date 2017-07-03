/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.explore.explorertree.util;

import br.com.explorer.explorertree.ExplorerTreeController;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilson
 */
public class ForceSegment implements RepulsiveNode {

    @Override
    public List<OverlapRect> repulsive(List<OverlapRect> elems, int representative, double minWeight, double maxWeight) {
        List<OverlapRect> after = new ArrayList<>();
        
        double maxDist = maxDistance(elems, representative);
        
        for( int i = 0; i < elems.size(); ++i ) {
            if( representative == i ) { 
                after.add(new OverlapRect(elems.get(representative).x, elems.get(representative).y, 
                        elems.get(representative).width, elems.get(representative).height, elems.get(representative).getId()));
                
                continue;
            }
            double ax = elems.get(representative).x;
            double ay = elems.get(representative).y;
            double bx = elems.get(i).x;
            double by = elems.get(i).y;
            
            double lenAB = Util.euclideanDistance(ax, ay, bx, by);
            double weight = ExplorerTreeController.calculateWeight(maxWeight, minWeight, maxDist, lenAB);
            
            double cx = bx + (bx-ax)/lenAB * weight;
            double cy = by + (by-ay)/lenAB * weight;
                        
            after.add(new OverlapRect(cx, cy, elems.get(i).width, elems.get(i).height, elems.get(i).getId()));
        }
        
        return after;
    }
    
}

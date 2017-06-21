/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.methods.overlap.expadingnode;

import br.com.methods.utils.Util;
import java.util.Comparator;

/**
 *
 * @author Windows
 */
public class OverlapNodeComparator implements Comparator<OverlapNode> {
    private static OverlapNodeComparator instance;
    
    private static OverlapNode node;
    
    private OverlapNodeComparator() {}
        
    public static synchronized OverlapNodeComparator getInstance(OverlapNode representative) {
        node = representative;
        if( instance == null ) {
            instance = new OverlapNodeComparator();
        }
        
        return instance;
    }
    
    
    @Override
    public int compare(OverlapNode o1, OverlapNode o2) {
        double d1 = Util.euclideanDistance(o2.boundingBox.x, o2.boundingBox.y, node.boundingBox.x, node.boundingBox.y);
        double d2 = Util.euclideanDistance(o1.boundingBox.x, o1.boundingBox.y, node.boundingBox.x, node.boundingBox.y);
        
        return Double.compare(d1, d2);
    }
    
}

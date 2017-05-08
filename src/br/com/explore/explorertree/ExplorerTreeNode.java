/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.explore.explorertree;

import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Windows
 */
public class ExplorerTreeNode {
    
    private List<ExplorerTreeNode> _children;
    
    private Point2D.Double[] _subprojection;
    
    private int[] _indexes;
    
    private int _distinctionDistance;
    private int _routing;
    private int _minChildren;
   
    private RepresentativeFinder _representativeAlgorithm;
    
    // to implement further
    // private int[] dissimilar
    // private int[] similar
    
    public ExplorerTreeNode(int minChildren, int distinctionDistance, int routing, Point2D.Double[] subprojection, int[] indexes, RepresentativeFinder representativeAlgorithm) {
        _indexes = indexes;
        _minChildren = minChildren;
        _routing = routing;
        _subprojection = subprojection;
        _distinctionDistance = distinctionDistance;
        _representativeAlgorithm = representativeAlgorithm;
    }
    
    public void createSubTree() {
        // in order to apply the representative selection to the subprojection, we need to filter the elements so that the
        // algorithm can be applied on the subprojection
        System.out.println("SIZE: "+_indexes.length);
        for( int i = 0; i < _indexes.length; ++i )
            System.out.print(_indexes[i]+" ");
        System.out.println();
        _representativeAlgorithm.filterData(_indexes);
        
        // execute algorithm and retrieve representative
        _representativeAlgorithm.execute();
        int[] nthLevelRepresentatives = _representativeAlgorithm.getRepresentatives();
        
        // apply distinction algorithm
        nthLevelRepresentatives = Util.distinct(nthLevelRepresentatives, _subprojection, _distinctionDistance);
        Map<Integer, List<Integer>> map = Util.createIndex(nthLevelRepresentatives, _subprojection);
        
        Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Routing: {0} - Number of representatives before {1}", new Object[]{_routing, map.size()});
        // remove representatives which represent only < _minChildren
        Util.removeDummyRepresentive(map, _minChildren);
        Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Routing: {0} - Number of representatives after  {1}", new Object[]{_routing, map.size()});
        System.out.println("................................");
        
        // for each representative
        _children = new ArrayList<>();
        map.entrySet().forEach((item)-> {
            int representative = item.getKey();
            // get the indexes of the elements that it represents
            List<Integer> indexesChildren = item.getValue();
            Point2D.Double[] points = new Point2D.Double[indexesChildren.size()];
            int routing = -1;
            
            // create subprojection 
            for( int j = 0; j < points.length; ++j ) {
                if( indexesChildren.get(j) == representative ) // store the routing index in the 'subprojection'
                    routing = j;
                points[j] = new Point2D.Double(_subprojection[indexesChildren.get(j)].x, _subprojection[indexesChildren.get(j)].y);
            }
            
            // continue further
            _children.add(new ExplorerTreeNode(_distinctionDistance, _minChildren, routing, points, 
                    indexesChildren.stream().mapToInt((Integer value)->value).toArray(), _representativeAlgorithm)); 
        });
        
    }
    
}

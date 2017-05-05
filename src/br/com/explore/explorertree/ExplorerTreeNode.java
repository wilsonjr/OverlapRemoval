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

/**
 *
 * @author Windows
 */
public class ExplorerTreeNode {
    
    private List<ExplorerTreeNode> _children;
    
    private Point2D.Double[] _subprojection;
    
    private int[] indexes;
    
    private int _distinctionDistance;
    private int _routing;
    private int _minChildren;
   
    private RepresentativeFinder _representativeAlgorithm;
    
    // to implement further
    // private int[] dissimilar
    // private int[] similar
    
    public ExplorerTreeNode(int minChildren, int distinctionDistance, int routing, Point2D.Double[] subprojection, int[] indexes, RepresentativeFinder representativeAlgorithm) {
        _minChildren = minChildren;
        _routing = routing;
        _subprojection = subprojection;
        _distinctionDistance = distinctionDistance;
        _representativeAlgorithm = representativeAlgorithm;
    }
    
    public void createSubTree() {
        _representativeAlgorithm.filterData(indexes);
        _representativeAlgorithm.execute();
        int[] nthLevelRepresentatives = _representativeAlgorithm.getRepresentatives();
        nthLevelRepresentatives = Util.distinct(nthLevelRepresentatives, _subprojection, _distinctionDistance);
        Map<Integer, List<Integer>> map = Util.createIndex(nthLevelRepresentatives, _subprojection);
        Util.removeDummyRepresentive(map, _minChildren);
        
        _children = new ArrayList<>();
        for( int i = 0; i < nthLevelRepresentatives.length; ++i ) {
            List<Integer> indexesChildren = map.get(nthLevelRepresentatives[i]);
            Point2D.Double[] points = new Point2D.Double[indexesChildren.size()];
            int routing = -1;

            for( int j = 0; j < points.length; ++j ) {
                if( indexesChildren.get(j) == nthLevelRepresentatives[i] ) // store the routing index in the 'subprojection'
                    routing = j;
                points[j] = new Point2D.Double(_subprojection[indexesChildren.get(j)].x, _subprojection[indexesChildren.get(j)].y);
            }

            _children.add(new ExplorerTreeNode(_distinctionDistance, _minChildren, routing, points, 
                    indexesChildren.stream().toArray(Integer[]::new), _representativeAlgorithm));            
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.explore.explorertree;

import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Windows
 */
public class ExplorerTree {
    
    private List<ExplorerTreeNode> _topNodes;
    private List<Polygon> _topTesselation;
    private Point2D.Double[] _projection;
    
    private RepresentativeFinder _representativeAlgorithm;
    
    private int _distinctionDistance;
    private int _minChildren;
    
    public ExplorerTree(Point2D.Double[] projection, RepresentativeFinder representativeAlgorithm, 
                        int distinctionDistance, int minChildren) {
        _projection = projection;
        _representativeAlgorithm = representativeAlgorithm;
        _distinctionDistance = distinctionDistance;
        _minChildren = minChildren;
    }
    
    public void build() {
        createLevelOne();
        createSubTree();
    }
    
        
    public void createSubTree() {
        _topNodes.stream().forEach(ExplorerTreeNode::createSubTree);
    }
    
    private void createLevelOne() {
        
        _representativeAlgorithm.execute();
        int[] levelOneRepresentatives = _representativeAlgorithm.getRepresentatives();
        levelOneRepresentatives = Util.distinct(levelOneRepresentatives, _projection, _distinctionDistance);
        Map<Integer, List<Integer>> map = Util.createIndex(levelOneRepresentatives, _projection);
        Util.removeDummyRepresentive(map, _minChildren);
        
        _topNodes = new ArrayList<>();
        for( int i = 0; i < levelOneRepresentatives.length; ++i ) {
            List<Integer> indexes = map.get(levelOneRepresentatives[i]);
            Point2D.Double[] points = new Point2D.Double[indexes.size()];
            int routing = -1;

            for( int j = 0; j < points.length; ++j ) {
                if( indexes.get(j) == levelOneRepresentatives[i] ) // store the routing index in the 'subprojection'
                    routing = j;
                points[j] = new Point2D.Double(_projection[indexes.get(j)].x, _projection[indexes.get(j)].y);
            }

            _topNodes.add(new ExplorerTreeNode(_distinctionDistance, _minChildren, routing, points, 
                    indexes.stream().toArray(Integer[]::new), _representativeAlgorithm));            
        }
    }
    
}

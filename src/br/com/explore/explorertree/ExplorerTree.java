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
import java.util.logging.Level;
import java.util.logging.Logger;

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
        Logger.getLogger(ExplorerTree.class.getName()).log(Level.INFO, "Creating level one.");
        createLevelOne();
        Logger.getLogger(ExplorerTree.class.getName()).log(Level.INFO, "Finish creating level one.");
        Logger.getLogger(ExplorerTree.class.getName()).log(Level.INFO, "Now creating subtrees");
        createSubTree();
        Logger.getLogger(ExplorerTree.class.getName()).log(Level.INFO, "Finish creating subtrees");
    }
    
        
    public void createSubTree() {
        _topNodes.stream().forEach(ExplorerTreeNode::createSubTree);
    }
    
    private void createLevelOne() {
        
        // execute algorithm and retrieve representative
        _representativeAlgorithm.execute();
        int[] levelOneRepresentatives = _representativeAlgorithm.getRepresentatives();
        
        // apply distinction algorithm
        levelOneRepresentatives = Util.distinct(levelOneRepresentatives, _projection, _distinctionDistance);
        Map<Integer, List<Integer>> map = Util.createIndex(levelOneRepresentatives, _projection);
        
        Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Number of representatives before {0}", map.size());
        // remove representatives which represent only < _minChildren
        Util.removeDummyRepresentive(map, _minChildren);
        Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Number of representatives after  {0}", map.size());
        System.out.println("................................");
        
        // for each representative
        _topNodes = new ArrayList<>();
        
        map.entrySet().forEach((item) -> {
            int representative = item.getKey();
            // get the indexes of the elements that it represents
            List<Integer> indexes = item.getValue();
            Point2D.Double[] points = new Point2D.Double[indexes.size()];
            int routing = -1;
            
            // create subprojection 
            for( int j = 0; j < points.length; ++j ) {
                if( indexes.get(j) == representative ) // store the routing index in the 'subprojection'
                    routing = j;
                points[j] = new Point2D.Double(_projection[indexes.get(j)].x, _projection[indexes.get(j)].y);
            }
            
            // do the same to each subprojection
            _topNodes.add(new ExplorerTreeNode(_distinctionDistance, _minChildren, routing, points,
                    indexes.stream().mapToInt((Integer value)->value).toArray(), _representativeAlgorithm));
        });        
    }
    
    public void print() {
        
        System.out.println("Quantidade de instâncias: "+_projection.length);
        System.out.println("Quantidade de representativos: "+_topNodes.size());
        for( int i = 0; i < _topNodes.size(); ++i )
            _topNodes.get(i).print("\t");
        
        
        
        
    }
    
}

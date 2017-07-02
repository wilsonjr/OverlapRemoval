/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.explorer.explorertree;

import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
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
    
    private Map<Integer, ExplorerTreeNode> _activeNodes;
    
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
        //levelOneRepresentatives = selectMedoid(levelOneRepresentatives);
       
        Map<Integer, List<Integer>> map = Util.createIndex(levelOneRepresentatives, _projection);
        
        Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Number of representatives before {0}", map.size());
        // remove representatives which represent only < _minChildren
        Util.removeDummyRepresentive(map, _minChildren);
        Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Number of representatives after  {0}", map.size());
        System.out.println("................................");
       
        levelOneRepresentatives = map.entrySet().stream().mapToInt((value)->value.getKey()).toArray();
        levelOneRepresentatives = selectMedoid(levelOneRepresentatives);
        map = Util.createIndex(levelOneRepresentatives, _projection);
        
        
        // for each representative
        _topNodes = new ArrayList<>();
        
        map.entrySet().forEach((item) -> {
            int representative = item.getKey();
            // get the indexes of the elements that it represents
            List<Integer> indexes = item.getValue();
            System.out.println(representative+" >> "+indexes.size());
            
            
            Point2D.Double[] points = new Point2D.Double[indexes.size()];

            // create subprojection 
            for( int j = 0; j < points.length; ++j )
                points[j] = new Point2D.Double(_projection[indexes.get(j)].x, _projection[indexes.get(j)].y);           

            // do the same to each subprojection
            _topNodes.add(new ExplorerTreeNode(_minChildren, _distinctionDistance, representative, points,
                    indexes.stream().mapToInt((Integer value)->value).toArray(), _representativeAlgorithm, null));
        });        
    }
    
    public void print() {
        
        System.out.println("Quantidade de instâncias: "+_projection.length);
        System.out.println("Quantidade de representativos: "+_topNodes.size());
        for( int i = 0; i < _topNodes.size(); ++i )
            _topNodes.get(i).print("\t");
        
    }
    
    public void buildActiveNodes() {
        _activeNodes = new HashMap<>();
        for( int i = 0; i < _topNodes.size(); ++i )
            _activeNodes.put(_topNodes.get(i).routing(), _topNodes.get(i));
        
    }
    
    public void expandNode(int index, Polygon polygon) {
        ExplorerTreeNode node = _activeNodes.get(index);
        node.setPolygon(polygon);
        _activeNodes.remove(index);
        
        node.children().stream().forEach((ExplorerTreeNode value) -> { _activeNodes.put(value.routing(), value); });        
    }
    
    public Map<Integer, ExplorerTreeNode> activeNodes() {
        return _activeNodes;
    }
    
    public List<ExplorerTreeNode> topNodes() {
        return _topNodes;
    }
    
    private int[] selectMedoid(int[] indexes) {
        int[] temp = new int[indexes.length];
        Map<Integer, List<Integer>> mapTemp = Util.createIndex(indexes, _projection);
        int k = 0;
        
        for( Map.Entry<Integer, List<Integer>> v: mapTemp.entrySet() ) {
            
            List<Integer> list = v.getValue();
            Point2D.Double p = new Point2D.Double(0,0);
            for( int i = 0; i < list.size(); ++i ) {
                p.x += _projection[list.get(i)].x;
                p.y += _projection[list.get(i)].y;
            }
            
            p.x /= list.size();
            p.y /= list.size();
            
            int medoid = list.get(0);
            double dist = Double.MAX_VALUE;
            for( int i = 0; i < list.size(); ++i ) {
                double d = Util.euclideanDistance(p.x, p.y, _projection[list.get(i)].x, _projection[list.get(i)].y);
                if( d < dist ) {
                    dist = d;
                    medoid = list.get(i);
                }
            }
            
            temp[k++] = medoid;
        }
        
        return temp;
    }

    public List<Integer> filterNodes(ExplorerTreeNode parent) {
        
       List<Integer> toRemove = new ArrayList<>();
       _activeNodes.entrySet().stream().filter((value) -> ( value.getValue().isChild(parent) )).forEachOrdered((value) -> {
           toRemove.add(value.getKey());
        });
       
       toRemove.stream().forEach((Integer value)->_activeNodes.remove(value));
        
       _activeNodes.put(parent.routing(), parent);
       
       return toRemove;

    }
    
    
    public Point2D.Double[] projection() {
        return projection();
    }
    
}

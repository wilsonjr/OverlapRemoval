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
        
        // execute algorithm and retrieve getRepresentative
        _representativeAlgorithm.execute();
        int[] levelOneRepresentatives = _representativeAlgorithm.getRepresentatives();
        
        
        // apply distinction algorithm
        levelOneRepresentatives = Util.distinct2(levelOneRepresentatives, _projection, _distinctionDistance);
        //levelOneRepresentatives = selectMedoid(levelOneRepresentatives);
       
        Map<Integer, List<Integer>> map = Util.createIndex2(levelOneRepresentatives, _projection);
        
        Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Number of representatives before {0}", map.size());
        // remove representatives which represent only < _minChildren
        Util.removeDummyRepresentive2(map, _minChildren);
        Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Number of representatives after  {0}", map.size());
        System.out.println("................................");
       
        levelOneRepresentatives = map.entrySet().stream().mapToInt((value)->value.getKey()).toArray();
        levelOneRepresentatives = selectMedoid(levelOneRepresentatives);
        map = Util.createIndex2(levelOneRepresentatives, _projection);
        
        
        // for each getRepresentative
        _topNodes = new ArrayList<>();
        
        map.entrySet().forEach((item) -> {
            int representative = item.getKey();
            // get the getIndexes of the elements that it represents
            List<Integer> indexes = item.getValue();
            System.out.println(representative+" >> "+indexes.size());
            
            
            Point2D.Double[] points = new Point2D.Double[indexes.size()];

            // create getSubprojection 
            for( int j = 0; j < points.length; ++j )
                points[j] = new Point2D.Double(_projection[indexes.get(j)].x, _projection[indexes.get(j)].y);           

            // do the same to each getSubprojection
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
            _activeNodes.put(_topNodes.get(i).getRouting(), _topNodes.get(i));
        
    }
    
    public void expandNode(int index, Polygon polygon) {
        ExplorerTreeNode node = _activeNodes.get(index);
        node.setPolygon(polygon);
        _activeNodes.remove(index);
        
        node.getChildren().stream().forEach((ExplorerTreeNode value) -> { _activeNodes.put(value.getRouting(), value); });        
    }
    
    public Map<Integer, ExplorerTreeNode> getActiveNodes() {
        return _activeNodes;
    }
    
    public List<ExplorerTreeNode> getTopNodes() {
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
        
       _activeNodes.put(parent.getRouting(), parent);
       
       return toRemove;

    }
    
    
    public Point2D.Double[] getProjection() {
        return _projection;
    }

    public List<Polygon> getTopTesselation() {
        return _topTesselation;
    }

    public void setTopTesselation(List<Polygon> _topTesselation) {
        this._topTesselation = _topTesselation;
    }

    public RepresentativeFinder representativeAlgorithm() {
        return _representativeAlgorithm;
    }

    public int getDistinctionDistance() {
        return _distinctionDistance;
    }

    public void setDistinctionDistance(int _distinctionDistance) {
        this._distinctionDistance = _distinctionDistance;
    }

    public int getMinChildren() {
        return _minChildren;
    }

    public void setMinChildren(int _minChildren) {
        this._minChildren = _minChildren;
    }

    public void setTopNodes(List<ExplorerTreeNode> _topNodes) {
        this._topNodes = _topNodes;
    }

    public void setProjection(Point2D.Double[] _projection) {
        this._projection = _projection;
    }

    public void setActiveNodes(Map<Integer, ExplorerTreeNode> _activeNodes) {
        this._activeNodes = _activeNodes;
    }
    
    
    
}

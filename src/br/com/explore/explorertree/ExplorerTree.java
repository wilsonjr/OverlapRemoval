/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.explore.explorertree;

import br.com.methods.utils.Util;
import br.com.methods.utils.Vect;
import br.com.representative.RepresentativeFinder;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private Vect[] _projection;
    
    private RepresentativeFinder _representativeAlgorithm;
    
    private int _distinctionDistance;
    private int _minChildren;
    
    private Map<Integer, ExplorerTreeNode> _activeNodes;
    
    private Point2D.Double[] _indexesProjection;
    
    public ExplorerTree(Vect[] projection, RepresentativeFinder representativeAlgorithm, 
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
        
        _indexesProjection = Util.projectData(levelOneRepresentatives, _projection);
        for( int i = 0; i < _indexesProjection.length; ++i ) {
            System.out.println("1: "+levelOneRepresentatives[i]+ " *** "+_indexesProjection[i].x+" "+_indexesProjection[i].y);
        }       
        
        // apply distinction algorithm
        int[] newIndexes = Util.distinct(levelOneRepresentatives, _indexesProjection, _distinctionDistance);
        
        Point2D.Double[] newProjection = new Point2D.Double[newIndexes.length];
        for( int i = 0, j = 0; i < newIndexes.length; ) {
            if( i < newIndexes.length && j < levelOneRepresentatives.length ) {
                if( newIndexes[i] == levelOneRepresentatives[j] ) 
                    newProjection[i++] = _indexesProjection[j++];
                else
                    j++;
            }
        }
        
        levelOneRepresentatives = newIndexes;
        _indexesProjection = newProjection;
        
        for( int i = 0; i < _indexesProjection.length; ++i ) {
            System.out.println("2: "+levelOneRepresentatives[i]+ " *** "+_indexesProjection[i].x+" "+_indexesProjection[i].y);
        }       
        
        //levelOneRepresentatives = selectMedoid(levelOneRepresentatives);
//        for( int i = 0; i < _indexesProjection.length; ++i ) {
//            System.out.println("*** 2 "+_indexesProjection[i].x+" "+_indexesProjection[i].y);
//        }
         
         
        Map<Integer, List<Integer>> map = Util.createIndex(levelOneRepresentatives, _projection);
//        for( int i = 0; i < _indexesProjection.length; ++i ) {
//            System.out.println("*** 3 "+_indexesProjection[i].x+" "+_indexesProjection[i].y);
//        }
        Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Number of representatives before {0}", map.size());
        
        // remove representatives which represent only < _minChildren
        Util.removeDummyRepresentive(map, _minChildren);
//        for( int i = 0; i < _indexesProjection.length; ++i ) {
//            System.out.println("*** 4 "+_indexesProjection[i].x+" "+_indexesProjection[i].y);
//        }
        
        Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Number of representatives after  {0}", map.size());
        System.out.println("................................");
        
        
        List<Item> items = new ArrayList<>();
        for( int i = 0; i < levelOneRepresentatives.length; ++i ) {
            items.add(new Item(levelOneRepresentatives[i], _indexesProjection[i]));
        }        
        Collections.sort(items);
        
        
        newIndexes = map.entrySet().stream().mapToInt((value)->value.getKey()).toArray();
        Arrays.sort(newIndexes);
        
        newProjection = new Point2D.Double[newIndexes.length];
        for( int i = 0, j = 0; i < newIndexes.length; ) {
            if( i < newIndexes.length && j < items.size() ) {
                if( newIndexes[i] == items.get(j).i ) 
                    newProjection[i++] = items.get(j++).p;
                else
                    j++;
            }
        }
        
        levelOneRepresentatives = newIndexes;
        _indexesProjection = newProjection;
        
        for( int i = 0; i < _indexesProjection.length; ++i ) {
            System.out.println("3: "+levelOneRepresentatives[i]+ " *** "+_indexesProjection[i].x+" "+_indexesProjection[i].y);
        }  
        
        Map<Integer, Point2D.Double> indexToPoint = new HashMap<>();
        for( int i = 0; i < levelOneRepresentatives.length; ++i ) {
            indexToPoint.put(levelOneRepresentatives[i], _indexesProjection[i]);
            System.out.println("index: "+levelOneRepresentatives[i]+", point: "+_indexesProjection[i].x+", "+_indexesProjection[i].y);
        }
        //levelOneRepresentatives = selectMedoid(levelOneRepresentatives);
        //_indexesProjection = Util.projectData(levelOneRepresentatives, _projection);
        
        map = Util.createIndex(levelOneRepresentatives, _projection);
        
        
        // for each representative
        _topNodes = new ArrayList<>();
        
        map.entrySet().forEach((item) -> {
            int representative = item.getKey();
            // get the indexes of the elements that it represents
            List<Integer> indexes = item.getValue();
            
            int elem = -1;
            // create subprojection 
            Vect[] points = new Vect[indexes.size()];
            for( int j = 0; j < points.length; ++j ) {
                points[j] = new Vect(_projection[indexes.get(j)].vector());            
                if( representative == indexes.get(j) )
                    elem = j;
            }
            
            System.out.println("Olha o elem: "+elem+": "+indexes.get(elem));
            System.out.println("ExplorerTree::representative: "+representative+" size: "+points.length);
            // do the same to each subprojection
            ExplorerTreeNode node = new ExplorerTreeNode(_minChildren, _distinctionDistance, representative, points,
                    indexes.stream().mapToInt((Integer value)->value).toArray(), _representativeAlgorithm, null, elem, 
                    indexToPoint.get(representative));
            node.addEntry(representative, indexToPoint.get(representative));
            _topNodes.add(node);
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
    
    public Point2D.Double[] indexesProjection() {
        return _indexesProjection;
    }
    
    private int[] selectMedoid(int[] indexes) {
        int[] temp = new int[indexes.length];
        Map<Integer, List<Integer>> mapTemp = Util.createIndex(indexes, _projection);
        int k = 0;
        
        for( Map.Entry<Integer, List<Integer>> v: mapTemp.entrySet() ) {
            
            List<Integer> list = v.getValue();            
            
            Vect p = new Vect(_projection[0].vector().length);
            for( int i = 0; i < list.size(); ++i ) {
                p.add(_projection[list.get(i)]);
            }
            p.divide(list.size());
            
            
            int medoid = list.get(0);
            double dist = Double.MAX_VALUE;
            for( int i = 0; i < list.size(); ++i ) {
                //double d = Util.euclideanDistance(p.x, p.y, _projection[list.get(i)].x, _projection[list.get(i)].y);
                double d = p.distance(_projection[list.get(i)]);
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
        return _indexesProjection;
    }
    
    public static class Item implements Comparable<Item>{
        private int i;
        private Point2D.Double p;
        
        public Item(int i, Point2D.Double p) {
            this.i = i;
            this.p = p;
        }


        @Override
        public int compareTo(Item o) {
            return Integer.compare(i, o.i);
        }
        
        public int getIndex() {
            return i;
        }
        
        public Point2D.Double getPoint() {
            return p;
        }
    
    }
    
}

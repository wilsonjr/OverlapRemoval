/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.explore.explorertree;

import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import br.com.representative.clustering.hierarchical.Cluster;
import br.com.representative.clustering.hierarchical.Linkage;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
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
    
    public ExplorerTreeNode(int minChildren, int distinctionDistance, int routing, Point2D.Double[] subprojection, 
                            int[] indexes, RepresentativeFinder representativeAlgorithm) {
        _indexes = indexes;
        _minChildren = minChildren;
        _routing = routing;
        _subprojection = subprojection;
        _distinctionDistance = distinctionDistance;
        _representativeAlgorithm = representativeAlgorithm;
        _children = new ArrayList<>();
    }
    
    public void createSubTree() {
        // in order to apply the representative selection to the subprojection, we need to filter the elements so that the
        // algorithm can be applied on the subprojection
        _representativeAlgorithm.filterData(_indexes);
        
        // execute algorithm and retrieve representative
        _representativeAlgorithm.execute();
        int[] nthLevelRepresentatives = _representativeAlgorithm.getRepresentatives();
        if( nthLevelRepresentatives.length != 0 ) {
        
            System.out.println("Number of instances: "+_indexes.length+", number of representatives: "+nthLevelRepresentatives.length);
            // apply distinction algorithm
            nthLevelRepresentatives = Util.distinct(nthLevelRepresentatives, _subprojection, _distinctionDistance);
            Map<Integer, List<Integer>> map = Util.createIndex(nthLevelRepresentatives, _subprojection);
            
            
            Map<Integer, List<Integer>> map2 = agglomerateRepresentative(map);
            
            System.out.println("---------------------------------------MAP1-----------------------------------------");
            for( Map.Entry<Integer, List<Integer>> v: map.entrySet() ) {
                System.out.print(v.getKey()+":  ");
                for( Integer e: v.getValue() )
                    System.out.print(e+" ");
                System.out.println();
            }            
            
            System.out.println("\n*****************************************");
            for( Map.Entry<Integer, List<Integer>> v: map2.entrySet() ) {
                System.out.print(v.getKey()+":  ");
                for( Integer e: v.getValue() )
                    System.out.print(e+" ");
                System.out.println();
            }            
            
            System.out.println("---------------------------------------MAP2-----------------------------------------");
            
            

            Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Routing: {0} - Number of representatives before {1}", new Object[]{_routing, map.size()});
            // remove representatives which represent only < _minChildren
            Util.removeDummyRepresentive(map, _minChildren);
            Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Routing: {0} - Number of representatives after  {1}", new Object[]{_routing, map.size()});
            System.out.println("................................");

            Map<Integer, Integer> mapIndexes = new HashMap<>();

            map.values().stream().forEach((values) -> {

                values.stream().forEach((value)-> {
                    mapIndexes.put(value, _indexes[value]);
                });
            });


            // for each representative
            
            map.entrySet().forEach((item)-> {
                int representative = item.getKey();
                // get the indexes of the elements that it represents
                List<Integer> indexesChildren = item.getValue();
                System.out.println(indexesChildren.size()+" >= "+(2*_minChildren));
                if( indexesChildren.size() >= _minChildren ) {

                    Point2D.Double[] points = new Point2D.Double[indexesChildren.size()];

                    // create subprojection 
                    for( int j = 0; j < points.length; ++j )
                        points[j] = new Point2D.Double(_subprojection[indexesChildren.get(j)].x, _subprojection[indexesChildren.get(j)].y);

                    // continue to the further children, we must always pass original indexes
                    _children.add(new ExplorerTreeNode(_distinctionDistance, _minChildren, mapIndexes.get(representative), points, 
                            //indexesChildren.stream().mapToInt((Integer value)->value).toArray(), 
                            indexesChildren.stream().mapToInt((Integer i)->mapIndexes.get(i)).toArray(),
                            _representativeAlgorithm)); 
                }
            });

            _children.stream().forEach(ExplorerTreeNode::createSubTree);
        }
    }

    public void print(String identation) {
        System.out.println(identation+"Routing: ("+_routing+")");
        System.out.println(identation+"Quantidade de instâncias: "+_subprojection.length);
        System.out.println(identation+"Quantidade de representativos: "+_children.size());
        for( int i = 0; i < _children.size(); ++i )
            _children.get(i).print("\t"+identation);
    }
    
    public int routing() {
        return _routing;
    }
    
    public List<ExplorerTreeNode> children() {
        return _children;
    }
    
    private Map<Integer, List<Integer>> agglomerateRepresentative(Map<Integer, List<Integer>> map) {
    
        Map<String, LinkageRepresentative> linkageMap = new HashMap<>();
        PriorityQueue<LinkageRepresentative> queue = new PriorityQueue<>();
        Map<Integer, List<Integer>> newMap = new HashMap<>();
        
        // create "clusters"
        List<Representative> reps = new ArrayList<>();
        map.keySet().stream().forEach((value)->reps.add(new Representative(value, String.valueOf(value), map.get(value))));
        
        for( int i = 0; i < reps.size(); ++i ) 
            for( int j = i+1; j < reps.size(); ++j ) {
                LinkageRepresentative linkage = new LinkageRepresentative(reps.get(i), reps.get(j),
                                                      Util.euclideanDistance(_subprojection[reps.get(i).idx].x, _subprojection[reps.get(i).idx].y, 
                                                                             _subprojection[reps.get(j).idx].x, _subprojection[reps.get(j).idx].y));
                queue.add(linkage);
                linkageMap.put(createKey(linkage.u, linkage.v), linkage);
            }
        
        List<Point2D.Double> projection = Arrays.asList(_subprojection);
                
        while( reps.size() > 1 ) {
            
            LinkageRepresentative top = queue.poll();
            List<Integer> neighbors = new ArrayList<>();            
            
            top.u.list.stream().forEach((e)->neighbors.add(e));
            top.v.list.stream().forEach((e)->neighbors.add(e));
            
            Point2D.Double p = new Point2D.Double(0, 0);
            neighbors.stream().forEach((e)->{
                p.x += _subprojection[e].x;
                p.y += _subprojection[e].y;
            });
            
            p.x /= (double)_subprojection.length;
            p.y /= (double)_subprojection.length;
            
            int[] idx = Util.selectRepresentatives(new Point2D.Double[]{p}, projection);
            
            reps.remove(top.u);
            reps.remove(top.v);
            
            if( neighbors.size() >= _minChildren ) {
                newMap.put(idx[0], neighbors);
                
                for( Representative c: reps ) {
                    LinkageRepresentative firstLink = findLink(linkageMap, c, top.u);
                    LinkageRepresentative secondLink = findLink(linkageMap, c, top.v);
                    List<Double> distances = new ArrayList<>();
                    if( firstLink != null ) {
                        distances.add(firstLink.distance);
                        queue.remove(firstLink);
                    }
                    if( secondLink != null ) {
                        distances.add(secondLink.distance);
                        queue.remove(secondLink);
                    }
                } 
                
            } else {
                Representative newCluster = new Representative(idx[0], top.u.id+"."+top.v.id, neighbors);
                
                for( Representative c: reps ) {
                    LinkageRepresentative firstLink = findLink(linkageMap, c, top.u);
                    LinkageRepresentative secondLink = findLink(linkageMap, c, top.v);
                    List<Double> distances = new ArrayList<>();
                    if( firstLink != null ) {
                        distances.add(firstLink.distance);
                        queue.remove(firstLink);
                    }
                    if( secondLink != null ) {
                        distances.add(secondLink.distance);
                        queue.remove(secondLink);
                    }

                    double linkageDistance = Util.euclideanDistance(_subprojection[idx[0]].x, _subprojection[idx[0]].y, 
                                                                    _subprojection[c.idx].x, _subprojection[c.idx].y);
                    LinkageRepresentative uvC = new LinkageRepresentative(newCluster, c, linkageDistance);
                    queue.add(uvC);
                    linkageMap.put(createKey(newCluster, c), uvC);
                } 

                reps.add(newCluster);  
            }
        }
        
        return newMap;
    }
    
    private LinkageRepresentative findLink(Map<String, LinkageRepresentative> linkageMap, Representative c, Representative u) {
        LinkageRepresentative link = linkageMap.get(createKey(c, u));
        if( link == null ) 
            link = linkageMap.get(createKey(u, c));
        return link;
    }
    
    private String createKey(Representative u, Representative v) {
        return u.id+"<->"+v.id;
    }
    
    private class Representative {
        private int idx;        
        private String id;
        private List<Integer> list;
        
        private double _distance;
        
        public Representative(int idx, String id, List<Integer> list) {
            this.idx = idx;
            this.list = list;
            this.id = id;
        }                
    }
    
    private class LinkageRepresentative implements Comparable<Object> {
        private Representative u;
        private Representative v;        
        private double distance;
        
        public LinkageRepresentative(Representative u, Representative v, double distance) {
            this.u = u;
            this.v = v;
            this.distance = distance;
        }
        
        @Override
        public int compareTo(Object o) {
            LinkageRepresentative or = (LinkageRepresentative)o;
            return new Double(distance).compareTo(or.distance);            
        }
        
    }
    
}

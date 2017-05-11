/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.explore.explorertree;

import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import br.com.representative.clustering.FarPointsMedoidApproach;
import br.com.representative.clustering.partitioning.KMeans;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
        
        if( _indexes.length < 2*_minChildren ) // this node can represent its set of instances
            return;
        
        // in order to apply the representative selection to the subprojection, we need to filter the elements so that the
        // algorithm can be applied on the subprojection
        _representativeAlgorithm.filterData(_indexes);
        
        // execute algorithm and retrieve representative
        _representativeAlgorithm.execute();
        int[] nthLevelRepresentatives = _representativeAlgorithm.getRepresentatives();
        if( nthLevelRepresentatives.length >= 0 ) {
        
            System.out.println("Number of instances: "+_indexes.length+", number of representatives: "+nthLevelRepresentatives.length);
            // apply distinction algorithm
            nthLevelRepresentatives = Util.distinct(nthLevelRepresentatives, _subprojection, _distinctionDistance);
            if( nthLevelRepresentatives.length <= 1 )
                return;
            
            
            Map<Integer, List<Integer>> map = Util.createIndex(nthLevelRepresentatives, _subprojection);
            
            
            Map<Integer, List<Integer>> copyMap = new HashMap<>();
            for( Map.Entry<Integer, List<Integer>> v: map.entrySet() ) {
                copyMap.put(v.getKey(), v.getValue());
            }
            

            Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Routing: {0} - Number of representatives before {1}", new Object[]{_routing, map.size()});
            // remove representatives which represent only < _minChildren
            Util.removeDummyRepresentive(map, _minChildren);
            Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Routing: {0} - Number of representatives after  {1}", new Object[]{_routing, map.size()});
            if( map.isEmpty() ) {
                System.out.println("---------------------------------------MAP1-----------------------------------------");
                
                for( Map.Entry<Integer, List<Integer>> v: copyMap.entrySet() ) {

                    System.out.print(v.getKey()+":  ");
                    for( Integer e: v.getValue() )
                        System.out.print(e+" ");
                    System.out.println();
                }            
            }
            
//            System.out.println("................................");

            
           
            
            if( map.isEmpty() || map.size() == 1  ) {
                System.out.println("\n*****************************************");
                Map<Integer, List<Integer>> agglomerateMap = agglomerateRepresentative(copyMap);
                Map<Integer, Integer> mapIndexes2 = new HashMap<>();

                agglomerateMap.values().stream().forEach((values) -> {
                    values.stream().forEach((value)-> {
                        mapIndexes2.put(value, _indexes[value]);
                    });
                });
                
                
                for( Map.Entry<Integer, List<Integer>> v: agglomerateMap.entrySet() ) {
                    System.out.print("Rounting: "+_routing+" // "+v.getKey()+"("+mapIndexes2.get(v.getKey())+"):  ");
                    for( Integer e: v.getValue() )
                        System.out.print(e+" ");
                    System.out.println();
                }            
                map = agglomerateMap;
                System.out.println("---------------------------------------MAP2-----------------------------------------");
            }
            System.out.println("MAP1");
            for( Map.Entry<Integer, List<Integer>> v: map.entrySet() ) {
                System.out.print(v.getKey()+":  ");
                for( Integer e: v.getValue() )
                    System.out.print(e+" ");
                System.out.println();
            }  
            System.out.println("****************************************************");
            // recria o conjunto de representativos para conter todos elementos
            nthLevelRepresentatives = map.entrySet().stream().mapToInt((value)->value.getKey()).toArray();
            if( nthLevelRepresentatives.length == 1 ) // this will lead to a infinite loop...
                return;
            for( int i = 0; i < nthLevelRepresentatives.length; ++i )
               System.out.print(nthLevelRepresentatives[i]+" ");
            System.out.println("****************************************************");
            System.out.println("MAP2");
            map = Util.createIndex(nthLevelRepresentatives, _subprojection); 
            Map<Integer, Integer> mapIndexes = new HashMap<>();
            map.values().stream().forEach((values) -> {
                values.stream().forEach((value)-> {
                    mapIndexes.put(value, _indexes[value]);                    
                });
            });
            for( Map.Entry<Integer, List<Integer>> v: map.entrySet() ) {
                System.out.print(v.getKey()+"("+mapIndexes.get(v.getKey())+"):  ");
                for( Integer e: v.getValue() )
                    System.out.print(e+" ");
                System.out.println();
            }  
            
            
            
            // for each representative
            
            map.entrySet().forEach((item)-> {
                int representative = item.getKey();
                // get the indexes of the elements that it represents
                List<Integer> indexesChildren = item.getValue();
               
                Point2D.Double[] points = new Point2D.Double[indexesChildren.size()];

                // create subprojection 
                for( int j = 0; j < points.length; ++j )
                    points[j] = new Point2D.Double(_subprojection[indexesChildren.get(j)].x, _subprojection[indexesChildren.get(j)].y);

                // continue to the further children, we must always pass original indexes
                _children.add(new ExplorerTreeNode(_minChildren, _distinctionDistance, mapIndexes.get(representative), points, 
                        //indexesChildren.stream().mapToInt((Integer value)->value).toArray(), 
                        indexesChildren.stream().mapToInt((Integer i)->mapIndexes.get(i)).toArray(),
                        _representativeAlgorithm)); 
               
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
    
    public int[] indexes() {
        return _indexes;
    }
    
    
    private Map<Integer, List<Integer>> agglomerateRepresentative(Map<Integer, List<Integer>> map) {
        Map<Integer, List<Integer>> newMap = new HashMap<>();
        
        
//        KMeans kmeans = new KMeans(Arrays.asList(_subprojection), new FarPointsMedoidApproach(), _indexes.length/_minChildren);        
//        kmeans.execute();
//        int[] representative = kmeans.getRepresentatives();
//        
//        List<Representative> reps = new ArrayList<>();
//        for( int i = 0; i < representative.length; ++i ) {
//            //newMap.put(representative[i], kmeans.getClusters().get(i));
//            reps.add(new Representative(representative[i], String.valueOf(representative[i]), kmeans.getClusters().get(i)));   
//            System.out.println("Representative: "+representative[i]+", qtd: "+kmeans.getClusters().get(i).size());
//        }
//        
//        
//        while( true && reps.size() != 1 ) {
//            reps.sort((Representative o1, Representative o2) -> new Integer(o1.list.size()).compareTo(o2.list.size()));
//            
//            System.out.println("**************************"+reps.get(0).list.size()+" < "+_minChildren+"************************");
//            if( reps.get(0).list.size() < _minChildren ) {
//                
//                double dist = Double.MAX_VALUE;
//                int index = -1;
//                int u = reps.get(0).idx;
//                
//                for( int i = 1; i < reps.size(); ++i ) {
//                    int v = reps.get(i).idx;
//                    double d = Util.euclideanDistance(_subprojection[u].x, _subprojection[u].y, _subprojection[v].x, _subprojection[v].y);
//                    if( d < dist ) {
//                        dist = d;
//                        index = i;
//                    }
//                }
//                index = 1;
//                List<Integer> list = new ArrayList<>();
//                
//                reps.get(0).list.stream().forEach((v)->list.add(v));
//                reps.get(index).list.stream().forEach((v)->list.add(v));             
//                
//                Point2D.Double p = new Point2D.Double(0, 0);
//                list.stream().forEach((e)->{
//                    p.x += _subprojection[e].x;
//                    p.y += _subprojection[e].y;
//                });
//
//                p.x /= (double)list.size();
//                p.y /= (double)list.size();
//
//                int index2 = -1;
//                double distance = Double.MAX_VALUE;
//                for( int i = 0; i < list.size(); ++i ) {
//                    double d = Util.euclideanDistance(p.x, p.y, _subprojection[list.get(i)].x, _subprojection[list.get(i)].y);
//                    
//                    if( distance > d ) {
//                        distance = d;
//                        index2 = list.get(i);
//                    }
//                }
//                if( index2 == 15 ) 
//                    System.out.println(p.x+" - "+p.y);
//                System.out.println("Fundindo "+reps.get(0).idx+" ("+reps.get(0).list.size()+ ") e "+reps.get(index).idx+" ("+reps.get(index).list.size()+ ")");
//                
//                
//                //reps.add(new Representative(index2, String.valueOf(index2), list));    
//                   
//                
//                List<Representative> temp = new ArrayList<>();
//                for( int i = 1; i < reps.size(); ++i )
//                    if( i != index  )
//                        temp.add(reps.get(i));
//                temp.add(new Representative(index2, String.valueOf(index2), list));
//                
//                //reps.remove(0);     
//                //reps.remove(index);
//                
//                reps = temp;
//                System.out.println("REPS SIZE: "+reps.size());
//            } else
//                break;
//            
//            
//        }
//        
//        reps.stream().forEach((v)->newMap.put(v.idx, v.list));
        
        
        
        
        
    
        Map<String, LinkageRepresentative> linkageMap = new HashMap<>();
        PriorityQueue<LinkageRepresentative> queue = new PriorityQueue<>();        
        
        // create "clusters"
        List<Representative> reps = new ArrayList<>();
        map.entrySet().stream().forEach((value)->{
            
            // force representatives to be the medoid
            Point2D.Double p = new Point2D.Double(0, 0);
            value.getValue().stream().forEach((e)->{
                p.x += _subprojection[e].x;
                p.y += _subprojection[e].y;
            });
            
            p.x /= (double)value.getValue().size();
            p.y /= (double)value.getValue().size();
                        
            int index = -1;
            double distance = Double.MAX_VALUE;
            for( int i = 0; i < value.getValue().size(); ++i ) {
                double d = Util.euclideanDistance(p.x, p.y, _subprojection[value.getValue().get(i)].x, 
                                                            _subprojection[value.getValue().get(i)].y);
                if( distance > d ) {
                    distance = d;
                    index = value.getValue().get(i);
                }
            }
            
            reps.add(new Representative(index, String.valueOf(index), value.getValue()));       
        });
        
        for( int i = 0; i < reps.size(); ++i ) 
            for( int j = i+1; j < reps.size(); ++j ) {
                LinkageRepresentative linkage = new LinkageRepresentative(reps.get(i), reps.get(j),
                                                      Util.euclideanDistance(_subprojection[reps.get(i).idx].x, _subprojection[reps.get(i).idx].y, 
                                                                             _subprojection[reps.get(j).idx].x, _subprojection[reps.get(j).idx].y));
                queue.add(linkage);
                linkageMap.put(createKey(linkage.u, linkage.v), linkage);
            }
        
        //        System.out.println("MIN_CHILDREN: "+_minChildren);
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
            
            p.x /= (double)neighbors.size();
            p.y /= (double)neighbors.size();
                        
            int index = -1;
            double distance = Double.MAX_VALUE;
            for( int i = 0; i < neighbors.size(); ++i ) {
                double d = Util.euclideanDistance(p.x, p.y, _subprojection[neighbors.get(i)].x, _subprojection[neighbors.get(i)].y);
                if( distance > d ) {
                    distance = d;
                    index = neighbors.get(i);
                }
            }
            
            reps.remove(top.u);
            reps.remove(top.v);
            
            System.out.println("Merging: "+top.u.idx+" - "+top.v.idx+", centroid: "+index);
            for( int i = 0; i < neighbors.size(); ++i )
                System.out.print(neighbors.get(i)+" ");
            System.out.println();
            System.out.println(neighbors.size()+" >= "+_minChildren);
            if( neighbors.size() >= _minChildren ) {
                System.out.println("Removing these nodes");
                newMap.put(index, neighbors);
                
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
                System.out.println("Merging these nodes");
                
                Representative newCluster = new Representative(index, top.u.id+"."+top.v.id, neighbors);
                
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

                    double linkageDistance = Util.euclideanDistance(_subprojection[index].x, _subprojection[index].y, 
                                                                    _subprojection[c.idx].x, _subprojection[c.idx].y);
                    LinkageRepresentative uvC = new LinkageRepresentative(newCluster, c, linkageDistance);
                    queue.add(uvC);
                    linkageMap.put(createKey(newCluster, c), uvC);
                } 

                reps.add(newCluster);  
            }
        }
        
        if( !reps.isEmpty() ) {
            Representative lastOne = reps.get(0);
            List<Integer> neighbors = lastOne.list;
            System.out.println("Add "+lastOne.idx+" as a final node");
            Point2D.Double p = new Point2D.Double(0, 0);
            neighbors.stream().forEach((e)->{
                p.x += _subprojection[e].x;
                p.y += _subprojection[e].y;
            });

            p.x /= (double)neighbors.size();
            p.y /= (double)neighbors.size();

            int index = -1;
            double distance = Double.MAX_VALUE;
            for( int i = 0; i < neighbors.size(); ++i ) {
                double d = Util.euclideanDistance(p.x, p.y, _subprojection[neighbors.get(i)].x, _subprojection[neighbors.get(i)].y);
                if( distance > d ) {
                    distance = d;
                    index = neighbors.get(i);
                }
            }

            newMap.put(index, neighbors);
        }
        
        List<Representative> reps2 = new ArrayList<>();
        for( Map.Entry<Integer, List<Integer>> value: newMap.entrySet() ) 
            reps2.add(new Representative(value.getKey(), String.valueOf(value.getKey()), value.getValue()));
                
        
        
        while( true && reps2.size() != 1 ) {
            reps2.sort((Representative o1, Representative o2) -> new Integer(o1.list.size()).compareTo(o2.list.size()));
            
            System.out.println("**************************"+reps2.get(0).list.size()+" < "+_minChildren+"************************");
            if( reps2.get(0).list.size() < _minChildren ) {
                
                double dist = Double.MAX_VALUE;
                int index = -1;
                int u = reps2.get(0).idx;
                
                for( int i = 1; i < reps2.size(); ++i ) {
                    int v = reps2.get(i).idx;
                    double d = Util.euclideanDistance(_subprojection[u].x, _subprojection[u].y, _subprojection[v].x, _subprojection[v].y);
                    if( d < dist ) {
                        dist = d;
                        index = i;
                    }
                }
                index = 1;
                List<Integer> list = new ArrayList<>();
                
                reps2.get(0).list.stream().forEach((v)->list.add(v));
                reps2.get(index).list.stream().forEach((v)->list.add(v));             
                
                Point2D.Double p = new Point2D.Double(0, 0);
                list.stream().forEach((e)->{
                    p.x += _subprojection[e].x;
                    p.y += _subprojection[e].y;
                });

                p.x /= (double)list.size();
                p.y /= (double)list.size();

                int index2 = -1;
                double distance = Double.MAX_VALUE;
                for( int i = 0; i < list.size(); ++i ) {
                    double d = Util.euclideanDistance(p.x, p.y, _subprojection[list.get(i)].x, _subprojection[list.get(i)].y);
                    
                    if( distance > d ) {
                        distance = d;
                        index2 = list.get(i);
                    }
                }
                System.out.println("Fundindo "+reps2.get(0).idx+" ("+reps2.get(0).list.size()+ ") e "+reps2.get(index).idx+" ("+reps2.get(index).list.size()+ ")");
                
                
                //reps.add(new Representative(index2, String.valueOf(index2), list));    
                   
                
                List<Representative> temp = new ArrayList<>();
                for( int i = 1; i < reps2.size(); ++i )
                    if( i != index  )
                        temp.add(reps2.get(i));
                temp.add(new Representative(index2, String.valueOf(index2), list));
                
                //reps.remove(0);     
                //reps.remove(index);
                
                reps2 = temp;
                System.out.println("REPS SIZE: "+reps2.size());
            } else
                break;
            
            
        }
        Map<Integer, List<Integer>> newMap2 =new HashMap<>();
        reps.stream().forEach((v)->newMap2.put(v.idx, v.list));
        
        
        
        return newMap2;
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
            //return new Integer(u.list.size()+v.list.size()).compareTo(or.u.list.size()+or.v.list.size());
            return new Double(distance).compareTo(or.distance);            
        }
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.explore.explorertree;

import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import br.com.representative.clustering.FarPointsMedoidApproach;
import br.com.representative.clustering.partitioning.BisectingKMeans;
import br.com.representative.clustering.partitioning.KMeans;
import br.com.representative.clustering.partitioning.KMedoid;
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
    
    private double _lowerBound = 60.0/100.0;
   
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
        
        System.out.println("ROUTING OBJECT: "+_routing+", objects passed: ");
            for( int i = 0; i < _indexes.length; ++i )
                System.out.print(_indexes[i]+" ");
            System.out.println("\n");
        
        
        if( _indexes.length < 2.*_lowerBound*_minChildren ) // this node can represent its set of instances
            return;
        
        // in order to apply the representative selection to the subprojection, we need to filter the elements so that the
        // algorithm can be applied on the subprojection
        _representativeAlgorithm.filterData(_indexes);
        
        // execute algorithm and retrieve representative
        _representativeAlgorithm.execute();
        int[] nthLevelRepresentatives = _representativeAlgorithm.getRepresentatives();
        if( nthLevelRepresentatives.length > 0 ) {
        
            System.out.println("Number of instances: "+_indexes.length+", number of representatives: "+nthLevelRepresentatives.length);
            
            
            // apply distinction algorithm
            nthLevelRepresentatives = Util.distinct(nthLevelRepresentatives, _subprojection, _distinctionDistance);
            if( nthLevelRepresentatives.length < 1 )
                return;
            
            
            Map<Integer, List<Integer>> map = Util.createIndex(nthLevelRepresentatives, _subprojection);
            
            
            Map<Integer, List<Integer>> copyMap = new HashMap<>();
            for( Map.Entry<Integer, List<Integer>> v: map.entrySet() ) {
                copyMap.put(v.getKey(), v.getValue());
            }
            

            //Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Routing: {0} - Number of representatives before {1}", new Object[]{_routing, map.size()});
            // remove representatives which represent only < _minChildren
            Util.removeDummyRepresentive(map, (int) (_lowerBound*_minChildren));
            //Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Routing: {0} - Number of representatives after  {1}", new Object[]{_routing, map.size()});
            if( map.isEmpty() ) {
                System.out.println("---------------------------------------MAP1-----------------------------------------");
                
                for( Map.Entry<Integer, List<Integer>> v: copyMap.entrySet() ) {

                    System.out.print(_indexes[v.getKey()]+":  ");
                    for( Integer e: v.getValue() )
                        System.out.print(_indexes[e]+" ");
                    System.out.println();
                }            
            }
            
//            System.out.println("................................");

            
           
            
            if( map.isEmpty() || map.size() == 1  ) {
                System.out.println("\n*****************************************");
                Map<Integer, List<Integer>> agglomerateMap =agglomerateRepresentative(copyMap);
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
            nthLevelRepresentatives = Util.distinct(nthLevelRepresentatives, _subprojection, _distinctionDistance);
            if( nthLevelRepresentatives.length == 1 )  {// this will lead to a infinite loop...
             
                if( _indexes.length < 2*_minChildren )
                    return;
                
                map = applyKMeans();
                nthLevelRepresentatives = map.entrySet().stream().mapToInt((value)->value.getKey()).toArray(); 
                System.out.println(nthLevelRepresentatives.length+" elementos após kmedoid");
                nthLevelRepresentatives = Util.distinct(nthLevelRepresentatives, _subprojection, _distinctionDistance);
                if( nthLevelRepresentatives.length == 1 ) {
                    System.out.println("Um elemento após distinct");
                    return;
                }
            }
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
        } else {
            System.out.println("NAO consegui encontrar nenhum representativo");
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
            System.out.println("Valores: ");
            
            for( int i = 0; i < value.getValue().size(); ++i ) {
                System.out.print(_indexes[value.getValue().get(i)]+" ");
                double d = Util.euclideanDistance(p.x, p.y, _subprojection[value.getValue().get(i)].x, 
                                                            _subprojection[value.getValue().get(i)].y);
                if( distance > d ) {
                    distance = d;
                    index = value.getValue().get(i);
                }
            }
            
            System.out.println(" - CENTROID: "+_indexes[index]);
            
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
            
            if( top == null )
                System.out.println("TOP é NULL");
            if( top.u == null )
                System.out.println("TOP.u é NULL");
            if( top.v == null )
                System.out.println("TOP.v é NULL");
            
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
            
            
            int idxu = -1;
            int idxv = -1;
            double distu = top.distance;
            double distv = top.distance;
            
            for( Map.Entry<Integer, List<Integer>> v: newMap.entrySet() ) {
                double du = Util.euclideanDistance(_subprojection[top.u.idx].x, _subprojection[top.u.idx].y, 
                                                   _subprojection[v.getKey()].x, _subprojection[v.getKey()].y);
                if( du < distu ) {
                    distu = du;
                    idxu = v.getKey();
                }
                
                double dv = Util.euclideanDistance(_subprojection[top.v.idx].x, _subprojection[top.v.idx].y, 
                                                   _subprojection[v.getKey()].x, _subprojection[v.getKey()].y);
                
                if( dv < distv ) {
                    distv = dv;
                    idxv = v.getKey();
                }
            }

            if( idxu != idxv && (distu < top.distance || distv < top.distance) ) {
                
                System.out.println("Entramos aqui 1");
            
                if( distu < top.distance ) {
                    if( top.u.list.size() <= _lowerBound*_minChildren ) {
                        
                        System.out.println("u: Merging the nodes "+_indexes[top.u.idx]+" and "+_indexes[idxu]);
                        
                        
                        // augment list of neighbors
                        List<Integer> nList = newMap.get(idxu);
                        top.u.list.stream().forEach((e)->nList.add(e));
                        newMap.put(idxu, nList);
                        
                        reps.remove(top.u);
                        for( Representative c: reps ) {
                            LinkageRepresentative firstLink = findLink(linkageMap, c, top.u);
                            if( firstLink != null ) 
                                queue.remove(firstLink);                            
                        } 
                        
                        int medoid = idxu;
                        double dMedoid = Double.MAX_VALUE;
                        Point2D.Double pMedoid = new Point2D.Double(0,0);
                        for( int i = 0; i < nList.size(); ++i ) {
                            
                            pMedoid.x += _subprojection[nList.get(i)].x;
                            pMedoid.y += _subprojection[nList.get(i)].y;
                            
                        }
                        
                        pMedoid.x /= (double)nList.size();
                        pMedoid.y /= (double)nList.size();
                        
                        for( int i = 0; i < nList.size(); ++i ) {
                            double d = Util.euclideanDistance(pMedoid.x, pMedoid.y,
                                                              _subprojection[nList.get(i)].x, _subprojection[nList.get(i)].y);
                            if( dMedoid > d ) {
                                dMedoid = d;
                                medoid = nList.get(i);
                            }                           
                        }
                        
                        newMap.remove(idxu);
                        newMap.put(medoid, nList);
                        
                        
                        
                        
                        
                    } else {
                        
                        System.out.println("u: Forming a new node "+_indexes[top.u.idx]);
                        
                        newMap.put(top.u.idx, top.u.list);                        
                        
                        reps.remove(top.u);                                            
                        for( Representative c: reps ) {
                            LinkageRepresentative firstLink = findLink(linkageMap, c, top.u);
                            if( firstLink != null ) 
                                queue.remove(firstLink);                            
                        }
                    }
                }
                
                
                if( distv < top.distance ) {
                    if( top.v.list.size() <= _lowerBound*_minChildren ) {
                        
                        System.out.println("v: Merging the nodes "+_indexes[top.v.idx]+" and "+_indexes[idxv]);
                        
                        // augment list of neighbors
                        List<Integer> nList = newMap.get(idxv);
                        top.v.list.stream().forEach((e)->nList.add(e));
                        newMap.put(idxv, nList);
                        
                        reps.remove(top.v);
                        for( Representative c: reps ) {
                            LinkageRepresentative firstLink = findLink(linkageMap, c, top.v);
                            if( firstLink != null ) 
                                queue.remove(firstLink);                            
                        } 
                        
                        int medoid = idxv;
                        double dMedoid = Double.MAX_VALUE;
                        Point2D.Double pMedoid = new Point2D.Double(0,0);
                        for( int i = 0; i < nList.size(); ++i ) {
                            
                            pMedoid.x += _subprojection[nList.get(i)].x;
                            pMedoid.y += _subprojection[nList.get(i)].y;
                            
                        }
                        
                        pMedoid.x /= (double)nList.size();
                        pMedoid.y /= (double)nList.size();
                        
                        for( int i = 0; i < nList.size(); ++i ) {
                            double d = Util.euclideanDistance(pMedoid.x, pMedoid.y,
                                                              _subprojection[nList.get(i)].x, _subprojection[nList.get(i)].y);
                            if( dMedoid > d ) {
                                dMedoid = d;
                                medoid = nList.get(i);
                            }                           
                        }
                        
                        newMap.remove(idxv);
                        newMap.put(medoid, nList);
                        
                        
                    } else {
                        
                        System.out.println("v: Forming a new node "+_indexes[top.v.idx]);
                        
                        newMap.put(top.v.idx, top.v.list);                        
                        
                        reps.remove(top.v);                                            
                        for( Representative c: reps ) {
                            LinkageRepresentative firstLink = findLink(linkageMap, c, top.v);
                            if( firstLink != null ) 
                                queue.remove(firstLink);                            
                        }
                    }
                }
                
            
            } else if( idxu != -1 ) { // idxu == idxv but not equals to -1, so that there is a node in 'newMap' which is nearest 
                
                System.out.println("Entramos aqui 2");
                
                if( distu < distv ) {
                    
                    if( top.u.list.size() <= _lowerBound*_minChildren ) {
                        
                        System.out.println("u: Merging the nodes "+_indexes[top.u.idx]+" and "+_indexes[idxu]);
                        
                        
                        // augment list of neighbors
                        List<Integer> nList = newMap.get(idxu);
                        top.u.list.stream().forEach((e)->nList.add(e));
                        newMap.put(idxu, nList);
                        
                        reps.remove(top.u);
                        for( Representative c: reps ) {
                            LinkageRepresentative firstLink = findLink(linkageMap, c, top.u);
                            if( firstLink != null ) 
                                queue.remove(firstLink);                            
                        } 
                        
                        int medoid = idxu;
                        double dMedoid = Double.MAX_VALUE;
                        Point2D.Double pMedoid = new Point2D.Double(0,0);
                        for( int i = 0; i < nList.size(); ++i ) {
                            
                            pMedoid.x += _subprojection[nList.get(i)].x;
                            pMedoid.y += _subprojection[nList.get(i)].y;
                            
                        }
                        
                        pMedoid.x /= (double)nList.size();
                        pMedoid.y /= (double)nList.size();
                        
                        for( int i = 0; i < nList.size(); ++i ) {
                            double d = Util.euclideanDistance(pMedoid.x, pMedoid.y,
                                                              _subprojection[nList.get(i)].x, _subprojection[nList.get(i)].y);
                            if( dMedoid > d ) {
                                dMedoid = d;
                                medoid = nList.get(i);
                            }                           
                        }
                        
                        newMap.remove(idxu);
                        newMap.put(medoid, nList);
                        
                        
                        
                    } else {
                        
                        System.out.println("u: Forming a new node "+_indexes[top.u.idx]);
                        
                        newMap.put(top.u.idx, top.u.list);                        
                        
                        reps.remove(top.u);                                            
                        for( Representative c: reps ) {
                            LinkageRepresentative firstLink = findLink(linkageMap, c, top.u);
                            if( firstLink != null ) 
                                queue.remove(firstLink);                            
                        }
                    }
                    
                } else {
                    
                    if( top.v.list.size() <= _lowerBound*_minChildren ) {
                        
                        System.out.println("v: Merging the nodes "+_indexes[top.v.idx]+" and "+_indexes[idxv]);
                        
                        // augment list of neighbors
                        List<Integer> nList = newMap.get(idxv);
                        top.v.list.stream().forEach((e)->nList.add(e));
                        newMap.put(idxv, nList);
                        
                        reps.remove(top.v);
                        for( Representative c: reps ) {
                            LinkageRepresentative firstLink = findLink(linkageMap, c, top.v);
                            if( firstLink != null ) 
                                queue.remove(firstLink);                            
                        } 
                        
                        int medoid = idxv;
                        double dMedoid = Double.MAX_VALUE;
                        Point2D.Double pMedoid = new Point2D.Double(0,0);
                        for( int i = 0; i < nList.size(); ++i ) {
                            
                            pMedoid.x += _subprojection[nList.get(i)].x;
                            pMedoid.y += _subprojection[nList.get(i)].y;
                            
                        }
                        
                        pMedoid.x /= (double)nList.size();
                        pMedoid.y /= (double)nList.size();
                        
                        for( int i = 0; i < nList.size(); ++i ) {
                            double d = Util.euclideanDistance(pMedoid.x, pMedoid.y,
                                                              _subprojection[nList.get(i)].x, _subprojection[nList.get(i)].y);
                            if( dMedoid > d ) {
                                dMedoid = d;
                                medoid = nList.get(i);
                            }                           
                        }
                        
                        newMap.remove(idxv);
                        newMap.put(medoid, nList);
                        
                        
                    } else {
                        
                        System.out.println("v: Forming a new node "+_indexes[top.v.idx]);
                        
                        newMap.put(top.v.idx, top.v.list);                        
                        
                        reps.remove(top.v);                                            
                        for( Representative c: reps ) {
                            LinkageRepresentative firstLink = findLink(linkageMap, c, top.v);
                            if( firstLink != null ) 
                                queue.remove(firstLink);                            
                        }
                    }
                    
                }
            } else {
            
            
                reps.remove(top.u);
                reps.remove(top.v);

                System.out.println("Merging: "+_indexes[top.u.idx]+" - "+_indexes[top.v.idx]+", centroid: "+_indexes[index]);
                for( int i = 0; i < neighbors.size(); ++i )
                    System.out.print(_indexes[neighbors.get(i)]+" ");
                System.out.println();
                System.out.println(neighbors.size()+" >= "+_minChildren);



                if( neighbors.size() >= _lowerBound*_minChildren ) {
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
        }
        
        if( !reps.isEmpty() ) {
            Representative lastOne = reps.get(0);
            List<Integer> neighbors = lastOne.list;
            System.out.println("Add "+_indexes[lastOne.idx]+" as a final node");
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
            
            if( neighbors.size() <= _lowerBound*_minChildren ) {
                
            
                int idx = -1;
                double dist = Double.MAX_VALUE;

                for( Map.Entry<Integer, List<Integer>> v: newMap.entrySet() ) {
                    double d = Util.euclideanDistance(_subprojection[index].x, _subprojection[index].y, 
                                                      _subprojection[v.getKey()].x, _subprojection[v.getKey()].y);
                    if( dist > d ) {
                        dist = d;
                        idx = v.getKey();
                    }
                }
                System.out.println("Concatenating "+_indexes[lastOne.idx]+" and "+idx);
                List<Integer> nList = newMap.get(idx);
                neighbors.stream().forEach((e)->nList.add(e));
                newMap.put(idx, nList);
            } else {
                System.out.println(_indexes[lastOne.idx]+" is the new node");
                newMap.put(index, neighbors);
            }

            
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
//        
        Map<Integer, List<Integer>> newMap2 =new HashMap<>();
//        for( int i = 0; i < reps2.size(); ++i ) {
//            
//            List<Integer> list = reps2.get(i).list;
//            
//            Point2D.Double p = new Point2D.Double(0, 0);
//            list.stream().forEach((e)->{
//                p.x += _subprojection[e].x;
//                p.y += _subprojection[e].y;
//            });
//
//            p.x /= (double)list.size();
//            p.y /= (double)list.size();
//
//            int index = -1;
//            double distance = Double.MAX_VALUE;
//            for( int j = 0; j < list.size(); ++j ) {
//                double d = Util.euclideanDistance(p.x, p.y, _subprojection[list.get(j)].x, _subprojection[list.get(j)].y);
//
//                if( distance > d ) {
//                    distance = d;
//                    index = list.get(j);
//                }
//            }
//            
//            newMap2.put(index, list);
//            
//            
//            
//            
//        }
        
        
        
        
        reps2.stream().forEach((v)->newMap2.put(v.idx, v.list));
        
        
        
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

    private Map<Integer, List<Integer>> applyKMeans() {
        Map<Integer, List<Integer>> newMap = new HashMap<>();
        
        /*
        double dist = -1;
        int idx1 = 0;
        Point2D.Double first = _subprojection[0];
        for( int i = 0; i < _subprojection.length; ++i ) {
            double d = Util.euclideanDistance(first.x, first.y, _subprojection[i].x, _subprojection[i].y);
            if( d > dist ) {
                dist = d;
                idx1 = i;
            }       
        }
        
        first = _subprojection[idx1];
        Point2D.Double second = null;
        
        dist = -1;
        int idx2 = 0;
        for( int i = 0; i < _subprojection.length; ++i ) {
            double d = Util.euclideanDistance(first.x, first.y, _subprojection[i].x, _subprojection[i].y);
            if( d > dist ) {
                dist = d;
                idx2 = i;
            }                 
        }
        
        second = _subprojection[idx2];
        
        List<Integer> listFirst = new ArrayList<>();
        List<Integer> listSecond = new ArrayList<>();
        
        for( int i = 0; i < _subprojection.length; ++i ) {
            if( i != idx1 && i != idx2 ) {
                double dFirst = Util.euclideanDistance(first.x, first.y, _subprojection[i].x, _subprojection[i].y);
                double dSecond = Util.euclideanDistance(second.x, second.y, _subprojection[i].x, _subprojection[i].y);
                if( dFirst < dSecond ) {
                    listFirst.add(i);
                } else {
                    listSecond.add(i);
                }                
            }                
        }
        
        //newMap.put(idx1, listFirst);
        //newMap.put(idx2, listSecond);
        
        
        for( int i = 0; i < listFirst.size(); ++i ) {
            
            List<Integer> list = listFirst;
            
            Point2D.Double p = new Point2D.Double(0, 0);
            list.stream().forEach((e)->{
                p.x += _subprojection[e].x;
                p.y += _subprojection[e].y;
            });

            p.x /= (double)list.size();
            p.y /= (double)list.size();

            int index = -1;
            double distance = Double.MAX_VALUE;
            for( int j = 0; j < list.size(); ++j ) {
                double d = Util.euclideanDistance(p.x, p.y, _subprojection[list.get(j)].x, _subprojection[list.get(j)].y);

                if( distance > d ) {
                    distance = d;
                    index = list.get(j);
                }
            }
            
            newMap.put(index, list);
            
        }
        
        for( int i = 0; i < listSecond.size(); ++i ) {
            
            List<Integer> list = listSecond;
            
            Point2D.Double p = new Point2D.Double(0, 0);
            list.stream().forEach((e)->{
                p.x += _subprojection[e].x;
                p.y += _subprojection[e].y;
            });

            p.x /= (double)list.size();
            p.y /= (double)list.size();

            int index = -1;
            double distance = Double.MAX_VALUE;
            for( int j = 0; j < list.size(); ++j ) {
                double d = Util.euclideanDistance(p.x, p.y, _subprojection[list.get(j)].x, _subprojection[list.get(j)].y);

                if( distance > d ) {
                    distance = d;
                    index = list.get(j);
                }
            }
            
            newMap.put(index, list);
            
        }*/
        
        
        KMedoid kmeans = new KMedoid(Arrays.asList(_subprojection), new FarPointsMedoidApproach(), _indexes.length/_minChildren);        
        kmeans.execute();
        
        int[] representative = kmeans.getRepresentatives();
        
        for( int i = 0; i < representative.length; ++i ) {
            newMap.put(representative[i], kmeans.getClusters().get(i));
        }
        
        
        return newMap;
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

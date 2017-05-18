/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.explore.explorertree;

import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import br.com.representative.clustering.FarPointsMedoidApproach;
import br.com.representative.clustering.partitioning.KMedoid;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Stream;

/**
 *
 * @author Windows
 */
public class ExplorerTreeNode {
    
    public static int nodeCount = 0;
    
    private final ExplorerTreeNode _parent;
    
    private final List<ExplorerTreeNode> _children;
    
    private final Point2D.Double[] _subprojection;
    
    private final int[] _indexes;
    
    private final int _distinctionDistance;
    private final int _routing;
    private final int _minChildren;
    
    private final double _lowerBound = 60.0/100.0;
   
    private final RepresentativeFinder _representativeAlgorithm;
    
    private Polygon _polygon;
    
    // to implement further
    // private int[] dissimilar
    // private int[] similar
    
    public ExplorerTreeNode(int minChildren, int distinctionDistance, int routing, Point2D.Double[] subprojection, 
                            int[] indexes, RepresentativeFinder representativeAlgorithm, ExplorerTreeNode parent) {
        _indexes = indexes;
        _minChildren = minChildren;
        _routing = routing;
        _subprojection = subprojection;
        _distinctionDistance = distinctionDistance;
        _representativeAlgorithm = representativeAlgorithm;
        _children = new ArrayList<>();
        _parent = parent;
        _polygon = null;
    }
    
    public void createSubTree() {
        
        
        if( _indexes.length < 2.*_lowerBound*_minChildren ) // this node can represent its set of instances
            return;
        
        // in order to apply the representative selection to the subprojection, we need to filter the elements so that the
        // algorithm can be applied on the subprojection
        _representativeAlgorithm.filterData(_indexes);
        
        // execute algorithm and retrieve representative
        _representativeAlgorithm.execute();
        int[] nthLevelRepresentatives = _representativeAlgorithm.getRepresentatives();
        if( nthLevelRepresentatives.length > 0 ) {
        
            // select medoids since the selected representatives must have high chances to overlap
            nthLevelRepresentatives = selectMedoid(nthLevelRepresentatives);
            
            // apply distinction algorithm
            nthLevelRepresentatives = Util.distinct(nthLevelRepresentatives, _subprojection, _distinctionDistance);
            
            Map<Integer, List<Integer>> map = Util.createIndex(nthLevelRepresentatives, _subprojection);            
            Map<Integer, List<Integer>> copyMap = new HashMap<>();
            for( Map.Entry<Integer, List<Integer>> v: map.entrySet() ) {
                copyMap.put(v.getKey(), v.getValue());
            }
            
            //Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Routing: {0} - Number of representatives before {1}", new Object[]{_routing, map.size()});
            // remove representatives which represent only < _minChildren
            Util.removeDummyRepresentive(map, (int) (_lowerBound*_minChildren));
            //Logger.getLogger(ExplorerTreeNode.class.getName()).log(Level.INFO, "Routing: {0} - Number of representatives after  {1}", new Object[]{_routing, map.size()});
        
            if( map.size() <= 1  ) {
                Map<Integer, List<Integer>> agglomerateMap = agglomerateRepresentative(copyMap);
                map = agglomerateMap;                
            }
            
            nthLevelRepresentatives = map.entrySet().stream().mapToInt((value)->value.getKey()).toArray();
            //nthLevelRepresentatives = selectMedoid(nthLevelRepresentatives);
            nthLevelRepresentatives = Util.distinct(nthLevelRepresentatives, _subprojection, _distinctionDistance);            
            
            if( nthLevelRepresentatives.length == 1 ) {// this will lead to a infinite loop...             
                if( _indexes.length < 2*_minChildren )
                    return;
                
                map = tryToDivideCluster();
                nthLevelRepresentatives = map.entrySet().stream().mapToInt((value)->value.getKey()).toArray(); 
                nthLevelRepresentatives = Util.distinct(nthLevelRepresentatives, _subprojection, _distinctionDistance);
                if( nthLevelRepresentatives.length == 1 )  // we don't propagate again since it will cause a infinite loop
                    return;                
            }
            
            
            // store the nearest neighbors for each representative
            map = Util.createIndex(nthLevelRepresentatives, _subprojection);             
            
            map.entrySet().forEach((item)-> {
                int representative = item.getKey();
                // get the indexes of the elements that it represents
                List<Integer> indexesChildren = item.getValue();
               
                Point2D.Double[] points = new Point2D.Double[indexesChildren.size()];

                // create subprojection 
                for( int j = 0; j < points.length; ++j )
                    points[j] = new Point2D.Double(_subprojection[indexesChildren.get(j)].x, _subprojection[indexesChildren.get(j)].y);

                // continue to the further children, we must always pass original indexes
                _children.add(new ExplorerTreeNode(_minChildren, _distinctionDistance, _indexes[representative], points, 
                        indexesChildren.stream().mapToInt((Integer i)->_indexes[i]).toArray(),
                        _representativeAlgorithm, this)); 
               
            });
            
            _children.stream().forEach(ExplorerTreeNode::createSubTree);
        } else {
            System.out.println("No representative were found");
        }
    }

    private int[] selectMedoid(int[] indexes) {
        int[] temp = new int[indexes.length];
        Map<Integer, List<Integer>> mapTemp = Util.createIndex(indexes, _subprojection);
        int k = 0;
        
        for( Map.Entry<Integer, List<Integer>> v: mapTemp.entrySet() ) {
            
            List<Integer> list = v.getValue();
            Point2D.Double p = new Point2D.Double(0,0);
            for( int i = 0; i < list.size(); ++i ) {
                p.x += _subprojection[list.get(i)].x;
                p.y += _subprojection[list.get(i)].y;
            }
            
            p.x /= list.size();
            p.y /= list.size();
            
            int medoid = list.get(0);
            double dist = Double.MAX_VALUE;
            for( int i = 0; i < list.size(); ++i ) {
                double d = Util.euclideanDistance(p.x, p.y, _subprojection[list.get(i)].x, _subprojection[list.get(i)].y);
                if( d < dist ) {
                    dist = d;
                    medoid = list.get(i);
                }
            }
            
            temp[k++] = medoid;
        }
        
        return temp;
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
            reps.add(new Representative(value.getKey(), String.valueOf(value.getKey()), value.getValue()));       
        });
        
        for( int i = 0; i < reps.size(); ++i ) 
            for( int j = i+1; j < reps.size(); ++j ) {
                double distance = //getSingleLinkage(reps.get(i).list, reps.get(j).list);
                Util.euclideanDistance(_subprojection[reps.get(i).idx].x, _subprojection[reps.get(i).idx].y, 
                                                       _subprojection[reps.get(j).idx].x, _subprojection[reps.get(j).idx].y);
                LinkageRepresentative linkage = new LinkageRepresentative(reps.get(i), reps.get(j), distance);
                queue.add(linkage);
                linkageMap.put(createKey(linkage.u, linkage.v), linkage);
            }        
        
        while( reps.size() > 1 ) {
            
            LinkageRepresentative top = queue.poll();
            
            int idxu = -1;
            int idxv = -1;
            double distu = top.distance;
            double distv = top.distance;
            
            for( Map.Entry<Integer, List<Integer>> v: newMap.entrySet() ) {
                double du = //getSingleLinkage(top.u.list, v.getValue());
                        
                           Util.euclideanDistance(_subprojection[top.u.idx].x, _subprojection[top.u.idx].y, 
                                                   _subprojection[v.getKey()].x, _subprojection[v.getKey()].y);
                if( du < distu ) {
                    distu = du;
                    idxu = v.getKey();
                }
                
                double dv = //getSingleLinkage(top.v.list, v.getValue());
                            Util.euclideanDistance(_subprojection[top.v.idx].x, _subprojection[top.v.idx].y, 
                                                   _subprojection[v.getKey()].x, _subprojection[v.getKey()].y);
                
                if( dv < distv ) {
                    distv = dv;
                    idxv = v.getKey();
                }
            }

            if( idxu != idxv && (distu < top.distance || distv < top.distance) ) {
                if( distu < top.distance ) {
                    if( top.u.list.size() <= _lowerBound*_minChildren )
                        augmentNeighbors(idxu, newMap, top.u, reps, linkageMap, queue);                        
                    else
                        addNewCluster(top.u.idx, top.u.list, newMap, top.u, reps, linkageMap, queue);                    
                }               
                
                if( distv < top.distance ) {
                    if( top.v.list.size() <= _lowerBound*_minChildren )    
                        augmentNeighbors(idxv, newMap, top.v, reps, linkageMap, queue);                        
                    else 
                        addNewCluster(top.v.idx, top.v.list, newMap, top.v, reps, linkageMap, queue);                        
                }                
            
            } else if( idxu != -1 ) { // idxu == idxv but not equals to -1, so that there is a node in 'newMap' which is nearest 
                if( distu < distv ) {
                    
                    if( top.u.list.size() <= _lowerBound*_minChildren )
                        augmentNeighbors(idxu, newMap, top.u, reps, linkageMap, queue);                                                
                    else 
                        addNewCluster(top.u.idx, top.u.list, newMap, top.u, reps, linkageMap, queue);
                    
                } else {
                    
                    if( top.v.list.size() <= _lowerBound*_minChildren )
                        augmentNeighbors(idxv, newMap, top.v, reps, linkageMap, queue);                        
                    else 
                        addNewCluster(top.v.idx, top.v.list, newMap, top.v, reps, linkageMap, queue);
                    
                }
            } else {
                
                List<Integer> neighbors = new ArrayList<>();        
            
                top.u.list.stream().forEach((e)->neighbors.add(e));
                top.v.list.stream().forEach((e)->neighbors.add(e));
                
                int medoid = findMedoid(-1, neighbors);
                        
                reps.remove(top.u);
                reps.remove(top.v);

                if( neighbors.size() >= _lowerBound*_minChildren ) {
                    newMap.put(medoid, neighbors);
                    for( Representative c: reps ) {
                        LinkageRepresentative firstLink = findLink(linkageMap, c, top.u);
                        LinkageRepresentative secondLink = findLink(linkageMap, c, top.v);
                        
                        if( firstLink != null ) 
                            queue.remove(firstLink);
                        
                        if( secondLink != null ) 
                            queue.remove(secondLink);                        
                    } 

                } else {
                    
                    Representative newCluster = new Representative(medoid, top.u.id+"."+top.v.id, neighbors);
                    for( Representative c: reps ) {
                        LinkageRepresentative firstLink = findLink(linkageMap, c, top.u);
                        LinkageRepresentative secondLink = findLink(linkageMap, c, top.v);
                        if( firstLink != null )
                            queue.remove(firstLink);                        
                        if( secondLink != null )
                            queue.remove(secondLink);
                        

                        double linkageDistance = //getSingleLinkage(neighbors, c.list);
                        Util.euclideanDistance(_subprojection[medoid].x, _subprojection[medoid].y, 
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
            int medoid = findMedoid(-1, neighbors);
            
            if( neighbors.size() <= _lowerBound*_minChildren ) { // merge with another cluster
                
            
                int idx = -1;
                double dist = Double.MAX_VALUE;

                for( Map.Entry<Integer, List<Integer>> v: newMap.entrySet() ) {
                    double d = //getSingleLinkage(neighbors, v.getValue());
                            Util.euclideanDistance(_subprojection[medoid].x, _subprojection[medoid].y, 
                                                      _subprojection[v.getKey()].x, _subprojection[v.getKey()].y);
                    if( dist > d ) {
                        dist = d;
                        idx = v.getKey();
                    }
                }
                
                
                List<Integer> nList = newMap.get(idx);
                neighbors.stream().forEach((e)->nList.add(e));
                //idx = findMedoid(idx, nList);
                newMap.put(idx, nList);
            } else
                newMap.put(medoid, neighbors); // create its own cluster
        }
        
        //Map<Integer, List<Integer>> newMap2 = agglomerateDummyClusters(newMap);
        
        return newMap;
    }

    private void addNewCluster(int idx, List<Integer> list,
                               Map<Integer, List<Integer>> newMap, Representative element, 
                               List<Representative> reps, Map<String, LinkageRepresentative> linkageMap, 
                               PriorityQueue<LinkageRepresentative> queue) {
        newMap.put(idx, list);
        
        reps.remove(element);
        for( Representative c: reps ) {
            LinkageRepresentative firstLink = findLink(linkageMap, c, element);
            if( firstLink != null )
                queue.remove(firstLink);
        }
    }

    private void augmentNeighbors(int idx, 
                                  Map<Integer, List<Integer>> newMap, 
                                  Representative topElement, 
                                  List<Representative> reps, 
                                  Map<String, LinkageRepresentative> linkageMap, 
                                  PriorityQueue<LinkageRepresentative> queue) {
        
        List<Integer> nList = newMap.get(idx);
        topElement.list.stream().forEach((e)->nList.add(e));
        
        addNewCluster(idx, nList, newMap, topElement, reps, linkageMap, queue);
        
        int medoid = findMedoid(idx, nList);        
        newMap.remove(idx);
        newMap.put(medoid, nList);
    }

    private int findMedoid(int idx, List<Integer> nList) {
        int medoid = idx;
        
        double dMedoid = Double.MAX_VALUE;
        Point2D.Double pMedoid = new Point2D.Double(0,0);        
        for( int i = 0; i < nList.size(); ++i ) {            
            pMedoid.x += _subprojection[nList.get(i)].x;
            pMedoid.y += _subprojection[nList.get(i)].y;            
        }
        
        pMedoid.x /= (double)nList.size();
        pMedoid.y /= (double)nList.size();
        
        for( int i = 0; i < nList.size(); ++i ) {
            double d = Util.euclideanDistance(pMedoid.x, pMedoid.y, _subprojection[nList.get(i)].x, _subprojection[nList.get(i)].y);
            if( dMedoid > d ) {
                dMedoid = d;
                medoid = nList.get(i);
            }
        }
        
        return medoid;
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

    private Map<Integer, List<Integer>> tryToDivideCluster() {
        System.out.println("Entrei aqui pela "+(nodeCount++)+"-ésima vez.");
        
        KMedoid kmedoid = new KMedoid(Arrays.asList(_subprojection), new FarPointsMedoidApproach(), _indexes.length/_minChildren);        
        kmedoid.execute();
        
        int[] representative = kmedoid.getRepresentatives();
        
        Map<Integer, List<Integer>> newMap = new HashMap<>();
        for( int i = 0; i < representative.length; ++i ) {
            newMap.put(representative[i], kmedoid.getClusters().get(i));
        }
        
        Map<Integer, List<Integer>> mapMedoid = agglomerateDummyClusters(newMap);
        return mapMedoid;
    }

    private Map<Integer, List<Integer>> agglomerateDummyClusters(Map<Integer, List<Integer>> clusters) {
        List<Representative> presentativeMedoid = new ArrayList<>();
        for( Map.Entry<Integer, List<Integer>> value: clusters.entrySet() ) 
            presentativeMedoid.add(new Representative(value.getKey(), String.valueOf(value.getKey()), value.getValue()));
        
        while( presentativeMedoid.size() != 1 ) {
            presentativeMedoid.sort((Representative o1, Representative o2) -> new Integer(o1.list.size()).compareTo(o2.list.size()));
            
            if( presentativeMedoid.get(0).list.size() < _lowerBound*_minChildren ) {
                
                double dist = Double.MAX_VALUE;
                int index = -1;
                for( int i = 1; i < presentativeMedoid.size(); ++i )  {                    
                    double d = getSingleLinkage(presentativeMedoid.get(0).list, presentativeMedoid.get(i).list);
                    if( d < dist ) {
                        index = i;
                        dist = d;
                    }
                }
                
                List<Integer> list = new ArrayList<>();                
                presentativeMedoid.get(0).list.stream().forEach((v)->list.add(v));
                presentativeMedoid.get(index).list.stream().forEach((v)->list.add(v));                  
                
                int medoid = findMedoid(-1, list);                
                List<Representative> temp = new ArrayList<>();
                for( int i = 1; i < presentativeMedoid.size(); ++i )
                    if( i != index  )
                        temp.add(presentativeMedoid.get(i));
                temp.add(new Representative(medoid, String.valueOf(medoid), list));
                
                presentativeMedoid = temp;
            } else
                break;
        }
        
        Map<Integer, List<Integer>> mapMedoid = new HashMap<>();
        for( int i = 0; i < presentativeMedoid.size(); ++i ) {            
            List<Integer> list = presentativeMedoid.get(i).list;
            int medoid = findMedoid(i, list);
            mapMedoid.put(medoid, list);
        }
        
        
        presentativeMedoid.stream().forEach((v)->mapMedoid.put(v.idx, v.list));
        return mapMedoid;
    }

    private double getSingleLinkage(List<Integer> u, List<Integer> v) {
        List<Double> distances = new ArrayList<>();
        for( int j = 0; j < u.size(); ++j )
            for( int k = 0; k < v.size(); ++k ) {
                double d = Util.euclideanDistance(_subprojection[u.get(j)].x, _subprojection[u.get(j)].y,
                                                  _subprojection[v.get(k)].x, _subprojection[v.get(k)].y);
                distances.add(d);
            }
        
        return distances.stream().min(Double::compareTo).get();
    }

    public ExplorerTreeNode parent() {
        return _parent;
    }
    
    public Polygon polygon() {
        return _polygon;
    }

    public void setPolygon(Polygon polygon) {
        _polygon = polygon;
    }

    public boolean isChildren(ExplorerTreeNode parent) {
        ExplorerTreeNode node = _parent;
        
        while( node != null ) {            
            if( parent == node )
                return true;
            node = node.parent();
        }
        
        return false;
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

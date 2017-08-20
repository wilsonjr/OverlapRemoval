/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.metric;

import br.com.methods.utils.Util;
import br.com.methods.utils.Vect;
import br.com.representative.clustering.affinitypropagation.AffinityPropagation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

/**
 *
 * @author wilson
 */
public class MST extends AccessMetric {
    
    private PriorityQueue<SlimTreeNode> nodes;
    private List<SlimTreeNode> clusters;
    
    private int k;    
    private int maxNodes;
    
    public MST(List<Vect> items, int maxNodes, int k) {
        super(items);
        this.maxNodes = maxNodes;
        this.k = k;
    }
    
    @Override
    public void execute() {        
        clusters = new ArrayList<>();
        int i = 0;
        for( ; i < items.size(); ++i ) {
            SlimTreeNode node = insert(items.get(i), i);
            node.computeMedoid();
            if( node.size() > maxNodes ) {
                
                
                List<SlimTreeNode> splitted = solveOverflow(node);
                
                clusters.remove(node);
                splitted.stream().forEach((v) -> {                    
                    v.computeMedoid();
                    clusters.add(v);
                });
                
            }
                        
        }
                
        
        int[] repsIndexes = clusters.stream().mapToInt((e)->{
            e.computeMedoid();
            return e.getMedoid().getI();
        }).toArray();
                
        
        Map<Integer, List<Integer>> index = Util.createIndex(repsIndexes, items.stream().map((v)->v).toArray(Vect[]::new));

        List<AffinityPropagation.RepresentativeIndexes> representativeIndexes = new ArrayList<>();
        final AffinityPropagation ap = new AffinityPropagation(items, k);
        index.entrySet().stream().forEach((Map.Entry<Integer, List<Integer>> v) -> {    
            
            
            representativeIndexes.add(ap.new RepresentativeIndexes(v.getKey(), v.getValue()));
        });

        Collections.sort(representativeIndexes);
        
        representatives = new int[k];
        for( i = 0; i < k; ++i ) 
            representatives[i] = representativeIndexes.get(i).getId();
        
  
    }

    @Override
    public void filterData(int[] indexes) {
        super.filterData(indexes);
        
        k = (int)(indexes.length*0.1);
        if( k == 0 )
            k = 1;
    }
    
    
    
    
    
    private SlimTreeNode insert(Vect point, int index) {
        SlimTreeNode node = null;
        
        if( clusters.isEmpty() ) {
            node = new SlimTreeNode(point, index);
            clusters.add(node);
        } else {
            
            List<SlimTreeNode> possibleNodes = new ArrayList<>();
            for( int i = 0; i < clusters.size(); ++i ) {

                if( clusters.get(i).qualify(point) ) 
                    possibleNodes.add(clusters.get(i));
            }
            
            
            if( possibleNodes.isEmpty() ) {
                  
                double minDist = clusters.get(0).getMedoid().getP().distance(point);
                node = clusters.get(0);

                for( int i = 0; i < clusters.size(); ++i )
                    if( minDist > clusters.get(i).getMedoid().getP().distance(point) ) {
                        minDist = clusters.get(i).getMedoid().getP().distance(point);
                        node = clusters.get(i);
                    }
                
                
            } else {
            
        
                String mode = "minoccup";

                switch( mode ) {

                    case "random":
                        int randint = new Random().nextInt(possibleNodes.size());
                        node = possibleNodes.get(randint);
                        break;

                    case "mindist":
                        double minDist = possibleNodes.get(0).getMedoid().getP().distance(point);
                        node = possibleNodes.get(0);

                        for( int i = 0; i < possibleNodes.size(); ++i )
                            if( minDist > possibleNodes.get(i).getMedoid().getP().distance(point) ) {
                                minDist = possibleNodes.get(i).getMedoid().getP().distance(point);
                                node = possibleNodes.get(i);
                            }
                        break;

                    default:
                        int minSize = possibleNodes.get(0).size();
                        node = possibleNodes.get(0);

                        for( int i = 0; i < possibleNodes.size(); ++i )
                            if( minSize > possibleNodes.get(i).size() ) {
                                minSize = possibleNodes.get(i).size();
                                node = possibleNodes.get(i);
                            }

                        break;
                }
            }
            
            node.add(point, index);
        }
        
        return node;
    }
    
    private List<SlimTreeNode> solveOverflow(SlimTreeNode node) {
        
        List<Vertice> vertices = new ArrayList<>();
        for( int i = 0; i < node.size(); ++i )
            vertices.add(new Vertice(node.get(i),i));
        
        List<Edge> edges = new ArrayList<>();
        for( int i = 0; i < vertices.size(); ++i )
            for( int j = i+1; j < vertices.size(); ++j ) {
                double d = vertices.get(i).point().distance(vertices.get(j).point());                        
                edges.add(new Edge(vertices.get(i), vertices.get(j), d));
                edges.add(new Edge(vertices.get(j), vertices.get(i), d));
            }
        
        List<Edge> mst = new ArrayList<>();
        List<DisjointSet<Vertice>> sets = new ArrayList<>();
        
        for( int i = 0; i < vertices.size(); ++i )
            sets.add(new DisjointSet<>(vertices.get(i)));
        
        Collections.sort(edges, (Edge o1, Edge o2) -> Double.compare(o1.distance(), o2.distance()));       
        
        
        Map<Integer, List<Integer>> map = new HashMap<>();
        
        for( int i = 0; i < edges.size(); ++i )  {
            
            if( sets.get(edges.get(i).u().id()).findSet() != sets.get(edges.get(i).v().id()).findSet() ) {
                mst.add(edges.get(i));
                sets.get(edges.get(i).u().id()).union(sets.get(edges.get(i).v().id()));
                
                if( map.get(edges.get(i).v().id()) == null )
                    map.put(edges.get(i).v().id(), new ArrayList<>());
                if( map.get(edges.get(i).u().id()) == null )
                    map.put(edges.get(i).u().id(), new ArrayList<>());
                
                map.get(edges.get(i).v().id()).add(edges.get(i).u().id());
                map.get(edges.get(i).u().id()).add(edges.get(i).v().id());
                
            }            
        }
        
        Collections.sort(mst);  
        
        int unode = mst.get(0).u().id();
        int vnode = mst.get(0).v().id();
        
        vertices.get(unode).setVisited(true);
        vertices.get(vnode).setVisited(true);
        
        Queue<Integer> queue = new LinkedList<>();
        SlimTreeNode firstNode = new SlimTreeNode();
        SlimTreeNode secondNode = new SlimTreeNode();
        
        queue.add(unode);
        while( !queue.isEmpty() ) {
            int top = queue.poll();
            vertices.get(top).setVisited(true);
            
            firstNode.add(vertices.get(top).point(), node.index(vertices.get(top).id()));
            
            List<Integer> adj = map.get(top);
            for( int i = 0; i < adj.size(); ++i )
                if( !vertices.get(adj.get(i)).visited() )
                    queue.add(adj.get(i));            
        }
        
        queue.add(vnode);
        while( !queue.isEmpty() ) {
            int top = queue.poll();
            vertices.get(top).setVisited(true);
            
            secondNode.add(vertices.get(top).point(), node.index(vertices.get(top).id()));
            
            List<Integer> adj = map.get(top);
            for( int i = 0; i < adj.size(); ++i )
                if( !vertices.get(adj.get(i)).visited() )
                    queue.add(adj.get(i));            
        }
        
        
        return new ArrayList<>(Arrays.asList(firstNode, secondNode));
        
    }
    
    
    public void setK(int k) {
        this.k = k;
    }

    public void setMaxNodes(int maxNodes) {
        this.maxNodes = maxNodes;
    }
      
}

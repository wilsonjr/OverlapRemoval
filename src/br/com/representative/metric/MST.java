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
import br.com.representative.RepresentativeFinder;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author wilson
 */
public class MST extends RepresentativeFinder {
    
    private PriorityQueue<SlimTreeNode> nodes;
    
    private List<Point2D.Double> finalItems;
    private List<Point2D.Double> items;
    
    private List<SlimTreeNode> clusters;
    
    private int k;
    
    
    public MST(List<Point2D.Double> items, int k) {
        this.finalItems = items;
        this.items = items;
        this.k = k;
    }
    

    @Override
    public void execute() {
        
        SlimTreeNode node = new SlimTreeNode(items);
        nodes = new PriorityQueue<>();
        nodes.add(node);
        
        while( !nodes.isEmpty() ) {
            
            SlimTreeNode u = nodes.peek();
            if( u.size() <= k )
                break;
            
            nodes.poll();
            List<SlimTreeNode> splitted = applyMST(u);
            nodes.add(splitted.get(0));
            nodes.add(splitted.get(1));
            
        }
        
        
        clusters = new ArrayList<>();
        for( SlimTreeNode n: nodes )
            clusters.add(n);
    }

    @Override
    public void filterData(int[] indexes) {
        items.clear();
        
        for( Integer i: indexes )
            items.add(finalItems.get(i));   
    }
    
    private List<SlimTreeNode> applyMST(SlimTreeNode u) {
        
        List<Vertice> vertices = new ArrayList<>();
        for( int i = 0; i < u.size(); ++i )
            vertices.add(new Vertice(u.get(i), i));
        
        List<Edge> edges = new ArrayList<>();
        for( int i = 0; i < vertices.size(); ++i )
            for( int j = i+1; i < vertices.size(); ++j ) {
                double d = Util.euclideanDistance(vertices.get(i).point().x, vertices.get(i).point().y,
                                                  vertices.get(j).point().x, vertices.get(j).point().y);
                edges.add(new Edge(vertices.get(i), vertices.get(j), d));
                edges.add(new Edge(vertices.get(j), vertices.get(i), d));
            }
        
        List<Edge> mst = new ArrayList<>();
        List<DisjointSet<Vertice>> sets = new ArrayList<>();
        
        for( int i = 0; i < vertices.size(); ++i )
            sets.add(new DisjointSet<>(vertices.get(i)));
        
        Collections.sort(edges);
        Map<Integer, List<Integer>> map = new HashMap<>();
        
        for( int i = 0; i < edges.size(); ++i )  {
            
            if( sets.get(edges.get(i).u().id()).findSet() != sets.get(edges.get(i).v().id()).findSet() ) {
                mst.add(edges.get(i));
                sets.get(edges.get(i).u().id()).union(sets.get(edges.get(i).v().id()));
                
                if( map.get(edges.get(i).v().id()) != null )
                    map.put(edges.get(i).v().id(), new ArrayList<>());
                if( map.get(edges.get(i).u().id()) != null )
                    map.put(edges.get(i).u().id(), new ArrayList<>());
                
                map.get(edges.get(i).v().id()).add(edges.get(i).u().id());
                map.get(edges.get(i).u().id()).add(edges.get(i).v().id());
                
            }            
        }
        
        Collections.sort(mst);
        
        int unode = mst.get(0).u().id();
        int vnode = mst.get(0).v().id();
        
        Queue<Integer> queue = new LinkedList<>();
        SlimTreeNode firstNode = new SlimTreeNode();
        SlimTreeNode secondNode = new SlimTreeNode();
        
        queue.add(unode);
        while( !queue.isEmpty() ) {
            int top = queue.poll();
            vertices.get(top).setVisited(true);
            
            firstNode.add(vertices.get(top).point());
            
            List<Integer> adj = map.get(top);
            for( int i = 0; i < adj.size(); ++i )
                if( !vertices.get(adj.get(i)).visited() )
                    queue.add(adj.get(i));            
        }
        
        queue.add(vnode);
        while( !queue.isEmpty() ) {
            int top = queue.poll();
            vertices.get(top).setVisited(true);
            
            secondNode.add(vertices.get(top).point());
            
            List<Integer> adj = map.get(top);
            for( int i = 0; i < adj.size(); ++i )
                if( !vertices.get(adj.get(i)).visited() )
                    queue.add(adj.get(i));            
        }
        
        
        return new ArrayList<>(Arrays.asList(firstNode, secondNode));
    }
      
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering.furs;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Windows
 */
public class Node implements Comparable<Node> {
    
    private double centralityDegree;
    private List<Node> adj;
    private boolean active;
    private int id;
    
    public Node(int id) {
        this.id = id;
        centralityDegree = 0.0;
        adj = new ArrayList<>();
        active = true;
    }     
    
    public void add(Node node, double distance) {
        adj.add(node);
        centralityDegree += distance;
    }
    
    public int getId() {
        return id;
    }
    
    public void activate() {
        active = true;
    }
    
    public void deactivate() {
        active = false;
    }
    
    public double getCentralityDegree() {
        return centralityDegree;
    }

    public boolean state() {
        return active;
    }
    
    public List<Node> neighbors() {
        return adj;
    }
    
    @Override
    public int compareTo(Node o) { 
        return Double.compare(centralityDegree, o.getCentralityDegree());        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering.furs;

import java.util.List;

/**
 *
 * @author Windows
 */
public class Node implements Comparable<Node> {
    
    private int centralityDegree;
    private List<Node> adj;
    private boolean active;
    
    
    public void activate() {
        active = true;
    }
    
    public void deactivate() {
        active = false;
    }
    
    public int getCentralityDegree() {
        return centralityDegree;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(centralityDegree, o.getCentralityDegree());
    }
    
}

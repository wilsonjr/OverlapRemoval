/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.metric;

/**
 *
 * @author wilson
 */
public class Edge implements Comparable<Edge> {
    
    private Vertice _u;
    private Vertice _v;
    
    private double distance;
        
    public Edge(Vertice u, Vertice v, double distance) {
        this._u = u;
        this._v = v;
        this.distance = distance;
    }
    
    public Vertice u() {
        return _u;
    }
    
    public Vertice v() {
        return _v;
    }

    @Override
    public int compareTo(Edge o) {
        return Double.compare(distance, o.distance);
    }
    
    
}

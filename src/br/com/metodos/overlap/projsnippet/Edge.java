/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.projsnippet;

/**
 *
 * @author wilson
 */
public class Edge {
    private long u;
    private long v;
    private double peso;
    
    public Edge(long u, long v) {
        this(u, v, 1);
    }
    
    public Edge(long u, long v, double peso) {
        this.u = u;
        this.v = v;
        this.peso = peso;
    }
    
    public long getU() {
        return u;
    }
    
    public long getV() {
        return v;
    }
    
    public void setU(long u) {
        this.u = u;
    }
    
    public void setV(long v) {
        this.v = v;
    }
    
    public void setPeso(double peso) {
        this.peso = peso;
    }
    
    public double getPeso() {
        return peso;
    }
    
    @Override
    public String toString() {
        return u+" "+v+": "+peso;
    }
    
    
}

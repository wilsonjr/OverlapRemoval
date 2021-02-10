/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.projsnippet;

import java.util.LinkedList;

/**
 * Representação de um vértice para aplicação do algoritmo PRIM
 * @author wilson
 */
public class Vertex implements HeapElement {
    private long id;
    private long pai;
    private boolean visitado;
    private LinkedList<Edge> adj;
    private int state;
    private double key;
    private int componente;
    
    public Vertex(long id) {
        this.id = id;
        this.visitado = false;
        adj = new LinkedList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isVisitado() {
        return visitado;
    }

    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }

    public LinkedList<Edge> getAdj() {
        return adj;
    }
    
    public void add(Edge v) {
        for( Edge e: adj )
            if( e.getV() == v.getV() )
                return;
        
        adj.add(v);
    }
    
    @Override
    public String toString() {
        String q = id+"("+componente+"): ";
        
        for( Edge l: adj )
            q += l.getV()+", ";
        
        return q;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void setKey(double key) {
        this.key = key;
    }

    @Override
    public double getKey() {
        return key;
    }

    public long getPai() {
        return pai;
    }

    public void setPai(long pai) {
        this.pai = pai;
    }

    public int getComponente() {
        return componente;
    }

    public void setComponente(int componente) {
        this.componente = componente;
    }
    
    
    public boolean has(long v) {
        for( Edge q: adj )
            if( q.getV() == v )
                return true;
        
        return false;
    }
    
}

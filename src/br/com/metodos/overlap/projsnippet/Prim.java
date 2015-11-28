/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.projsnippet;

import java.util.ArrayList;

/**
 *
 * @author wilson
 */
public class Prim {
    
    private ArrayList<Edge> edges = null;
    
    public void execute(Vertice[] grafo) {
        edges = prim(grafo, 0);
    }
    
    public ArrayList<Edge> getEdges() {
        return edges;
    }
    
    
    
    public static ArrayList<Edge> prim(Vertice[] grafo, int inicio) {
        ArrayList<Edge> mst = new ArrayList<>();
        MinHeap priorityQueue = new MinHeap(new HeapElementComparator());
        int[] has = new int[grafo.length];
        
        for( int i = 0; i < grafo.length; ++i ) {
            has[i] = 0;
            grafo[i].setId(i);
            grafo[i].setState(0);
            grafo[i].setPai(-1);
            grafo[i].setKey(Double.MAX_VALUE);
            priorityQueue.push(grafo[i]);
        }        
        grafo[inicio].setKey(0);
        
        priorityQueue.makeHeap();
        while( !priorityQueue.isEmpty() ) {
            Vertice top = (Vertice) priorityQueue.pop();
            
            has[(int)top.getId()] = 1;
            if( top.getId() != inicio )
                mst.add(new Edge(grafo[(int)top.getId()].getPai(), top.getId(), top.getKey()));
            
            
            for( Edge e: grafo[(int)top.getId()].getAdj() ) {
                if( has[(int)e.getV()] == 0 && e.getPeso() < grafo[(int)e.getV()].getKey() ) {                    
                    grafo[(int)e.getV()].setPai(top.getId());
                    priorityQueue.decreaseKey(grafo[(int)e.getV()], e.getPeso());
                }
            }           
        }
        
        return mst;
    }
}

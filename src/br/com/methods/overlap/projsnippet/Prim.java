/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.projsnippet;

import java.util.ArrayList;

/**
 * Algoritmo PRIM
 * @author wilson
 */
public class Prim {
    
    private ArrayList<Edge> edges = null;
    
    /**
     * Executa o algoritmo com base no grafo passado
     * @param grafo Grafo em lista de adjacências.
     */
    public void execute(Vertex[] grafo) {
        edges = prim(grafo, 0);
    }
    
    /**
     * Retorna as arestas pertencentes à árvore geradora mínima
     * @return Arestas da árvore
     */
    public ArrayList<Edge> getEdges() {
        return edges;
    }
    
    /**
     * Executa o algoritmo com vértice inicial especificado.
     * @param grafo Grafo em lista de adjacências
     * @param inicio Vértice inicial
     * @return Arestas da árvore
     */
    private static ArrayList<Edge> prim(Vertex[] grafo, int inicio) {
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
            Vertex top = (Vertex) priorityQueue.pop();
            
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

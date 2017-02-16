/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative;

import br.com.methods.overlap.projsnippet.Edge;
import br.com.methods.overlap.projsnippet.HeapElementComparator;
import br.com.methods.overlap.projsnippet.MinHeap;
import br.com.methods.overlap.projsnippet.Vertex;
import br.com.methods.utils.OverlapRect;

/**
 *
 * @author Windows
 */
public class Dijsktra {
    
    private final Vertex[] grafo;
    
    public Dijsktra(Vertex[] grafo) {
        this.grafo = grafo;
    }
    
    public void exec(int init, int fim, OverlapRect[] rect) {
        
        MinHeap priorityQueue = new MinHeap(new HeapElementComparator());
        for( int i = 0; i < grafo.length; ++i ) {
            grafo[i].setId(i);
            grafo[i].setState(0);
            grafo[i].setPai(-1);
            grafo[i].setKey(Double.MAX_VALUE);
            priorityQueue.push(grafo[i]);
        }
        
        grafo[init].setKey(0);        
        priorityQueue.makeHeap();
        
        while( !priorityQueue.isEmpty() ) {
            Vertex top = (Vertex) priorityQueue.pop();            
            rect[(int)top.getId()].addHealth();
            if( (int)top.getId() == fim ) 
                break;
            
            grafo[(int)top.getId()].getAdj().stream().
                    filter((e) -> ( e.getPeso()+grafo[(int)e.getU()].getKey() < grafo[(int)e.getV()].getKey() )).
                        forEachOrdered((e) -> {
                            grafo[(int)e.getV()].setKey(e.getPeso()+grafo[(int)e.getU()].getKey());
                            grafo[(int)e.getU()].setPai(top.getId());
                            priorityQueue.decreaseKey(grafo[(int)e.getV()], e.getPeso()+grafo[(int)e.getU()].getKey());
                                    
            });
        }
    }
    
}

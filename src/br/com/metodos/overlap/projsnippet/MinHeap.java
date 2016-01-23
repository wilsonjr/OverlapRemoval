/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.projsnippet;

import java.util.ArrayList;

/**
 * Heap Binário.
 * Baseado em Cormen et al.
 * @author wilson
 */
public class MinHeap {
    private ArrayList<HeapElement> elems = null;
    private HeapComp<HeapElement> comp = null;
    
    public MinHeap(HeapComp comp) {
        elems = new ArrayList<>();
        this.comp = comp;
    }
    
    public void push(HeapElement h) {
        elems.add(h);
    }
    
    private int pai(int i) {
        return (i-1)/2;
    }
    
    private int esquerdo(int i) {
        return i*2 + 1;
    }
    
    private int direito(int i) {
        return i*2 + 2;
    }
    
    private void heapify(int i) {
        int esq = esquerdo(i);
        int dir = direito(i);
        int menor = 0;
        
        if( esq < elems.size() && comp.compare(elems.get(esq), elems.get(i)) < 0 )
            menor = esq;
        else
            menor = i;
        if( dir < elems.size() && comp.compare(elems.get(dir), elems.get(menor)) < 0 )
            menor = dir;
        
        if( menor != i ) {
            HeapElement aux = elems.get(i);
            elems.set(i, elems.get(menor));
            elems.set(menor, aux);
            heapify(menor);
        }        
    }
    
    public void makeHeap() {
        for( int i = (elems.size()/2+1); i >= 0; --i )
            heapify(i);
    }
    
    public HeapElement top() {
        return elems.get(0);
    }
    
    public HeapElement pop() {
        HeapElement min = elems.get(0);
        elems.set(0, elems.get(elems.size()-1));
        elems.remove(elems.size()-1);
        heapify(0);
        return min;
    }
    
    public boolean decreaseKey(HeapElement he, double key) {
        int i = elems.indexOf(he);
        if( i >= 0 ) {
            elems.get(i).setKey(key);
            while( i > 0 && comp.compare(elems.get(pai(i)), elems.get(i)) > 0 ) {
                HeapElement aux = elems.get(i);
                elems.set(i, elems.get(pai(i)));
                elems.set(pai(i), aux);
                i = pai(i);
            }
            return true;
        } else
            return false;
    }
    
    public boolean isEmpty() {
        return elems.isEmpty();
    }
    
}

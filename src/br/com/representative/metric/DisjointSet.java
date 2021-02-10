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
public class DisjointSet<T> {
    private T chave;
    private DisjointSet pai;
    private int ordem;
    
    public DisjointSet() {}
    public DisjointSet(T elem) {
        makeSet(elem);
    }
    
    public DisjointSet findSet() {
        return findSet(this);
    }
    
    private DisjointSet findSet(DisjointSet elem) {
        if( elem != elem.getPai() )
            elem.setPai(findSet(elem.getPai()));
        return elem.getPai();
    }
    
    
    private void link(DisjointSet elem) {
        DisjointSet p = findSet(this);
        if( p.getOrdem() > elem.getOrdem() )
            elem.setPai(p);
        else {
            p.setPai(elem);
            if( p.getOrdem() == elem.getOrdem() )
                elem.setOrdem(elem.getOrdem()+1);
        }
                
    }
    
    
    public void union(DisjointSet elem) {
        link(findSet(elem));
    }
    
    
    private void makeSet(T elem) {
        setPai(this);
        setElem(elem);
        setOrdem(0);
    }
    
    private void setOrdem(int ordem) {
        this.ordem = ordem;
    }
    
    private void setElem(T elem) {
        this.chave = elem;        
    }
    
    private void setPai(DisjointSet elem) {
        pai = elem;
    }

    private int getOrdem() {
        return ordem;
    }

    private DisjointSet getPai() {
        return pai;
    }
}

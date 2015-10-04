/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

import java.util.ArrayList;

/**
 *
 * @author wilson
 */
public class PairingHeap {
    private PairingNode raiz;
    private int size;
    
    public PairingHeap() {
        raiz = null;
        size = 0;        
    }
    
    public PairingNode getRaiz() {        
        return raiz;
    }
    
    public int getSize() {
        return size;
    }
    
    public PairingNode insert(Restricao r) {
        ++size;
        PairingNode n = new PairingNode(r);
        if( raiz == null )
            raiz = n;
        else
            juntaHeap(raiz, n);
        
        return n;
    }
    
    public Restricao getMin() {
        if( raiz != null )
            return raiz.getElem();
        return null;
    }
    
    public void eraseHeap() {
        raiz = null;
        size = 0;
    }
    
    public void removeMin() {
        if( raiz != null ) {
            
            if( raiz.getFilhoEsquerdo() == null ) {
                raiz = null;
            } else 
                raiz = juntaIrmaos(raiz.getFilhoEsquerdo());
            --size;
        }
    }
    
    public void merge(PairingHeap p) {
        PairingNode r = p.getRaiz();
        if( raiz == null && r != null ) {
            raiz = r;
        } else
            juntaHeap(raiz, r);
        size += p.getSize();
    }

    private void juntaHeap(PairingNode q, PairingNode r) {
        if( r != null ) {
            if( compara(r.getElem(), q.getElem()) ) {
                r.setAnterior(q.getAnterior());
                q.setAnterior(r);
                q.setProxIrmao(r.getFilhoEsquerdo());
                if( q.getProxIrmao() != null )
                    q.getProxIrmao().setAnterior(q);
                r.setFilhoEsquerdo(q);
                q = r;
            } else {
                r.setAnterior(q);
                q.setProxIrmao(r.getProxIrmao());
                if( q.getProxIrmao() != null )
                    q.getProxIrmao().setAnterior(q);
                r.setProxIrmao(q.getFilhoEsquerdo());
                if( r.getProxIrmao() != null )
                    r.getProxIrmao().setAnterior(r);
                q.setFilhoEsquerdo(r);
            }
        }
    }
    
    private boolean compara(Restricao a, Restricao b) {
        double sa = a.getLeft().getBloco().getTimeStamp() > a.getTimeStamp()
                || a.getLeft().getBloco() == a.getRight().getBloco()
                ? Double.MIN_VALUE : a.getSlack();
        double sb = b.getLeft().getBloco().getTimeStamp() > b.getTimeStamp()
                || b.getLeft().getBloco() == b.getRight().getBloco()
                ? Double.MIN_VALUE : b.getSlack();
        if( sa == sb ) {
            if( a.getLeft().getId() == b.getLeft().getId() ) {
                return a.getRight().getId() < b.getRight().getId();
            }
            return a.getLeft().getId() < b.getLeft().getId();
        }
        return sa < sb;
    }   

    private PairingNode juntaIrmaos(PairingNode filhoEsquerdo) {
        if( filhoEsquerdo.getProxIrmao() == null )
            return filhoEsquerdo;
        ArrayList<PairingNode> q = new ArrayList<>();
        int n = 0;
                
        while( filhoEsquerdo != null ) {
            q.add(filhoEsquerdo);
            filhoEsquerdo.getAnterior().setProxIrmao(null);
            filhoEsquerdo = filhoEsquerdo.getProxIrmao();
            n++;
        }
        q.add(null);
        
        int i = 0;
        for( ; i+1 < n; i += 2 )
            juntaHeap(q.get(i), q.get(i+1));
        int j = i-2;
        if( j == n-3 )
            juntaHeap(q.get(j), q.get(j+2));
        for( ; j >= 2; j -= 2 )
            juntaHeap(q.get(j-2), q.get(j));
        return q.get(0);
    }
    
    public boolean isEmpty() {
        return raiz == null;
    }
    
}

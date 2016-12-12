/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.prism;

import java.util.Objects;

/**
 * Representa uma aresta para o método PRISM.
 * Esta poderá ser "esticada" de acordo com a execução do método.
 * @author wilson
 */
public class PRISMEdge {
    public static double SMAX = 1.5;
    private PRISMPoint u;
    private PRISMPoint v;
    
    public PRISMEdge(PRISMPoint u, PRISMPoint v) {
        this.u = u;
        this.v = v;
    }

    public PRISMPoint getU() {
        return u;
    }

    public PRISMPoint getV() {
        return v;
    }

    /**
     * Verifica se duas arestas são iguais.
     * Neste caso é a mesma aresta se u == v || v == u.
     * @param obj 
     * @return True se as arestas são iguais, False caso contrário.
     */
    @Override
    public boolean equals(Object obj) {
        PRISMEdge o = (PRISMEdge)obj;
        
        // simplesmente verifica se u == v  || v == u
        return getU().x() == o.getU().x() &&  getU().y() == o.getU().y() && getV().x() == o.getV().x() &&  getV().y() == o.getV().y() ||
               getU().x() == o.getV().x() &&  getU().y() == o.getV().y() && getV().x() == o.getU().x() &&  getV().y() == o.getU().y();
        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.u);
        hash = 71 * hash + Objects.hashCode(this.v);
        return hash;
    }
    
    @Override
    public String toString() {
        return u+" --> "+v;
    }
    
    public void swap() {
        if( u.getIdx() > v.getIdx() ) {
            PRISMPoint aux = u;
            u = v;
            v = aux;
        }
    }
    
    
    
}

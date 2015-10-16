/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.prism;

import java.util.Objects;

/**
 *
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

    @Override
    public boolean equals(Object obj) {
        PRISMEdge o = (PRISMEdge)obj;
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
    
    
    
    
}

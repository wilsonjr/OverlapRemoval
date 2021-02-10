/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.metric;

import br.com.methods.utils.Vect;

/**
 *
 * @author wilson
 */
public class Vertice {
    
    private int _id;
    private Vect _point;
    private boolean _visited;
    
    public Vertice(Vect point, int id) {
        this._point = point;
        this._id = id;
        this._visited = false;
    }
    
    public boolean visited() {
        return _visited;
    }
    
    public void setVisited(boolean visited) {
        _visited = visited;
    }
    
    public Vect point() {
        return _point;
    }
    
    public int id() {
        return _id;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.metric;

import java.awt.geom.Point2D;

/**
 *
 * @author wilson
 */
public class Vertice {
    
    private int _id;
    private Point2D.Double _point;
    private boolean _visited;
    
    public Vertice(Point2D.Double point, int id) {
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
    
    public Point2D.Double point() {
        return _point;
    }
    
    public int id() {
        return _id;
    }
    
}

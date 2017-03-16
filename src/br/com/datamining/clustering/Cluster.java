/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.datamining.clustering;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author wilson
 */
public class Cluster {
    private String id;
    private ArrayList<Rectangle2D.Double> points;
    
    public Cluster(Rectangle2D.Double r, int id) {
        points = new ArrayList<>();
        points.add(r);
        this.id = String.valueOf(id);
    }
    
    public Cluster() {
        points = new ArrayList<>();
    }
    
    public void addPoint(Rectangle2D.Double p) {
        points.add(p);
    }
    
    public Cluster(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public double distanceTo(Cluster get) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

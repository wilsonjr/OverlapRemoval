/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.datamining.clustering;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilson
 */
public class Cluster {
    private String id;
    private ArrayList<Rectangle2D.Double> points = new ArrayList<>();
    private ArrayList<String> sons = new ArrayList<>();
    
    public Cluster(Rectangle2D.Double r, int id) {
        points.add(r);
        this.id = String.valueOf(id);
    }
    
    public Cluster() { }
    
    public void addSons(String sonId) {
        sons.add(sonId);
    }
    
    public void addPoints(List<Rectangle2D.Double> p) {        
        points.addAll(p);
    }
    
    public List<Rectangle2D.Double> getPoints() {
        return points;
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

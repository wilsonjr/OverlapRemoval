/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.datamining.clustering;

import br.com.methods.utils.Util;
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
    private ArrayList<Cluster> sons = new ArrayList<>();
    private HierarchicalClustering hc;
    
    public Cluster(Rectangle2D.Double r, int id) {
        points.add(r);
        this.id = String.valueOf(id);
    }
    
    public Cluster(Rectangle2D.Double r, int id, HierarchicalClustering hc) {
        this(r, id);
        this.hc = hc;
    }
    
    public Cluster() { }
    
    public void addSons(Cluster son) {
        sons.add(son);
    }
    
    public ArrayList<Cluster> getSons() {
        return sons;
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

    public double distanceTo(Cluster c) {
        return Util.distanciaEuclideana(points.get(0).x, points.get(0).y, c.getPoints().get(0).x, c.getPoints().get(0).y);
    }
    
    @Override
    public String toString() {
        return id;
    }
    
    public void printHierarchy(String space) {
        System.out.println(space+this+" has "+sons.size()+" children");        
        for( int i = 0; i < sons.size(); ++i ) {
            sons.get(i).printHierarchy(space+"\t");
        }
    }
    
}

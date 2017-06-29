/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.clustering.hierarchical;

import br.com.methods.utils.Vect;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilson
 */
public class Cluster {
    private String id;
    private ArrayList<Vect> points = new ArrayList<>();
    private ArrayList<Cluster> sons = new ArrayList<>();
    private Cluster parent;
    private HierarchicalClustering hc;
    
    public Cluster(Vect r, int id) {
        parent = null;
        points.add(r);
        this.id = String.valueOf(id);
    }
    
    public Cluster(Vect r, int id, HierarchicalClustering hc) {
        this(r, id);
        this.hc = hc;
    }
    
    public Cluster() { }
    
    public Cluster getParent() {
        return parent;
    }
    
    public void setParent(Cluster parent) {
        this.parent = parent;
    }
    
    public void addSons(Cluster son) {
        sons.add(son);
    }
    
    public ArrayList<Cluster> getSons() {
        return sons;
    }
    
    public void addPoints(List<Vect> p) {        
        points.addAll(p);
    }
    
    public List<Vect> getPoints() {
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
        //return Util.euclideanDistance(points.get(0).x, points.get(0).y, c.getPoints().get(0).x, c.getPoints().get(0).y);
        return points.get(0).distance(c.getPoints().get(0));
    }
    
    @Override
    public String toString() {
        return id;
    }
    
    public void print(String space) {
        System.out.println(space+this+" has "+sons.size()+" children");        
        for( int i = 0; i < sons.size(); ++i ) {
            sons.get(i).print(space+"\t");
        }
    }
    
    
}

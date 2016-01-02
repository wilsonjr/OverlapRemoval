/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

import br.com.metodos.utils.Retangulo;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author wilson
 */
public class No {
    private Variavel var;
    private Retangulo rect;
    private double position;
    private No belowNeighbour, aboveNeighbour;
    private TreeSet<No> leftNeighbours = new TreeSet<>(new NoComparator());
    private TreeSet<No> rightNeighbours = new TreeSet<>(new NoComparator());
    private boolean deleted;
    
    public No(Variavel var, Retangulo rect, double position) {
        this.var = var;
        this.rect = rect;
        this.position = position;
        deleted = false;
    }
    
    public Variavel getVar() {
        return var;
    }
    
    public Retangulo getRect() {
        return rect;
    }
    
    public double getPosition() {
        return position;
    }
    
    public void addLeftNeighbour(No v) {
        leftNeighbours.add(v);
    }
    
    public void addRightNeighbour(No v) {
        rightNeighbours.add(v);
    }    
    
    public void setLeftNeighbours(TreeSet<No> neighbours) {
        leftNeighbours = neighbours;
        
        Iterator<No> it = neighbours.iterator();
        while( it.hasNext() ) {
            No no = it.next();
            if( !deleted && !no.getDeleted() ) 
                no.addRightNeighbour(this);            
        }
    }
    
    public void setRightNeighbours(TreeSet<No> neighbours) {
        rightNeighbours = neighbours;
        
        Iterator<No> it = neighbours.iterator();
        while( it.hasNext() ) {
            No no = it.next();
            if( !deleted && !no.getDeleted() ) 
                no.addLeftNeighbour(this);            
        }        
    }
    
    public TreeSet<No> getRightNeighbours() {
        return rightNeighbours;
    }
    
    public TreeSet<No> getLeftNeighbours() {
        return leftNeighbours;
    }   
    
    public No getBelowNeighbour() {
        return belowNeighbour;
    }
    
    public No getAboveNeighbour() {
        return aboveNeighbour;
    }
    
    public void setBelowNeighbour(No v) {
        belowNeighbour = v;
    }
    
    public void setAboveNeighbour(No v) {
        aboveNeighbour = v;
    }
    
    public boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void removeRightNeighbour(No v) {
        Iterator<No> it = rightNeighbours.iterator();
        No toRemove = null;
        while( it.hasNext() ) {
            toRemove = it.next();
            if( toRemove == v )
                break;
        }
        
        if( toRemove != null ) {
            toRemove.setDeleted(true);            
        }
    }

    public void removeLeftNeighbour(No v) {
        Iterator<No> it = leftNeighbours.iterator();
        No toRemove = null;
        while( it.hasNext() ) {
            toRemove = it.next();
            if( toRemove == v )
                break;
        }
        
        if( toRemove != null ) {
            toRemove.setDeleted(true);            
        }
    }
    
}

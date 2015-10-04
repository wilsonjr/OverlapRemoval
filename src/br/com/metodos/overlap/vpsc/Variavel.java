/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

import java.util.ArrayList;

/**
 *
 * @author Thaís
 */
public class Variavel {
    private int id;
    private double des;
    private double weight;
    private ArrayList<Restricao> in;
    private ArrayList<Restricao> out;
    private Bloco bloco; // bloco ao qual a var pertence...
    private double offset;
    private boolean visitado;
    private boolean deletado;
    
    public Variavel(int id) {
        init(id);
    }
    
    public void init(int id) {
        this.id = id;
        des = 0;
        weight = 1;
        offset = 0;
        visitado = false;
        
        in = new ArrayList<>();
        out = new ArrayList<>();
    }
    
    
    public void setOffset(double offset) {
        this.offset = offset;
    }
    
    public double getPosition() {
        return bloco.getPosn() + offset;
    }
    
    public void setDesiredPosition(double des) {
        this.des = des;
    }
    
    public int getId() {
        return id;
    }
    
    
    public ArrayList<Restricao> getIn() {
        return in;
    }
    
    public ArrayList<Restricao> getOut() {
        return out;
    }    
    
    public Bloco getBloco() {
        return bloco;
    }
    
    public void setBloco(Bloco b) {
        bloco = b;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public double getDes() {
        return des;
    }
    
    public double getOffset() {
        return offset;
    }
    
    public void setVisitado(boolean v) {
        visitado = v;
    }
    
    public boolean getVisitado() {
        return visitado;
    }

    public boolean getDeletado() {
        return deletado;
    }
    
    public void setDeletado(boolean d) {
        deletado = d;
    }
}

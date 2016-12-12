/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.prism;

/**
 * Utilizada para não repetir pontos.
 * @author wilson
 */
public class SetPoint  {
    private int i;
    private int j;
    private double value;
    
    public SetPoint(int i, int j, double value) {
        this.i = i;
        this.j = j;
        this.value = value;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return i+", "+j+": "+value;
    }
}

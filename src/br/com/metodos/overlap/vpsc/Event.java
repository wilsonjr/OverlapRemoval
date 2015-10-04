/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

/**
 *
 * @author wilson
 */
public class Event {
    private String tipo;
    private No no;
    private double position;
    
    public Event(String tipo, No no, double position) {
        this.tipo = tipo;
        this.no = no;
        this.position = position;
    }
    
    
    public No getNo() {
        return no;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public double getPosition() {
        return position;
    }
}

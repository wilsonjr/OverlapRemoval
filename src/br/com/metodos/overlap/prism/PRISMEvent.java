/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.prism;

import br.com.metodos.utils.Retangulo;

/**
 *
 * @author wilson
 */
public class PRISMEvent {
    private String tipo;
    private Retangulo r;
    
    public PRISMEvent(String tipo, Retangulo r) {
        this.tipo = tipo;
        this.r = r;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Retangulo getR() {
        return r;
    }

    public void setR(Retangulo r) {
        this.r = r;
    }
    
    
    
}

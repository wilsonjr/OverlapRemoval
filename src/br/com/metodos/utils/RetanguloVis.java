/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.utils;

import java.awt.Color;

/**
 *
 * @author wilson
 */
public class RetanguloVis extends Retangulo {
    public Color cor;
    public int numero;
    
    public RetanguloVis(double xMin, double yMin, double width, double height, Color cor, int numero) {
        super(xMin, yMin, width, height);
        this.cor = cor;
        this.numero = numero;
    }
    
}

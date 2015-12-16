/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.utils;

import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author wilson
 */
public class RetanguloVis extends Retangulo {
    public Color cor;
    public int numero;
    public boolean isHexBoard;
    public Point p;
    
    public RetanguloVis(double xMin, double yMin, double width, double height, Color cor, int numero) {
        super(xMin, yMin, width, height);
        this.cor = cor;
        this.numero = numero;
    }

    public boolean isIsHexBoard() {
        return isHexBoard;
    }

    public void setIsHexBoard(boolean isHexBoard) {
        this.isHexBoard = isHexBoard;
    }

    public Point getP() {
        return p;
    }

    public void setP(Point p) {
        this.p = p;
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.incboard;

import br.com.metodos.utils.Retangulo;
import br.com.metodos.utils.Util;
import incboard.api.DataItemInterface;

/**
 *
 * @author wilson
 */
public class RetanguloItem implements DataItemInterface {
    private String uRI;
    private int x;
    private int y;
    private Retangulo rect;
    
    public RetanguloItem(Retangulo rect, String uRI) {
        this.rect = rect;
        this.uRI = uRI;
    }    
    
    public Retangulo getRect() {
        return rect;
    }           
    
    @Override
    public String getURI() {
        return uRI;
    }

    @Override
    public int getCol() {
        return x;
    }

    @Override
    public int getRow() {
        return y;
    }

    @Override
    public void setCol(int i) {
        x = i;
    }

    @Override
    public void setRow(int i) {
        y = i;
    }

    @Override
    public Double getDistance(DataItemInterface dii) {
        RetanguloItem r = (RetanguloItem) dii;
        Retangulo rect2 = r.getRect();
        
        return Util.distanciaEuclideana(rect.getCenterX(), rect.getCenterY(), rect2.getCenterX(), rect2.getCenterY());
    }
    
}

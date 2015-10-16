/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.prism;

import br.com.metodos.utils.Retangulo;
import delaunay_triangulation.Point_dt;

/**
 *
 * @author wilson
 */
public class PRISMPoint extends Point_dt {
    private Retangulo rect;
    private int idx;
    
    
    public PRISMPoint(double x, double y, Retangulo rect, int idx) {
        super(x,y);
        this.rect = rect;
        this.idx = idx;
    }
    
    @Override
    public String toString() {
        return rect.getUX()+" "+rect.getUY()+" "+rect.getWidth()+" "+rect.getHeight()+" |||";
    }

    public Retangulo getRect() {
        return rect;
    }
    
    public int getIdx() {
        return idx;
    }
    
    
}

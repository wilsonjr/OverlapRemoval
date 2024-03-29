/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.prism;

import br.com.methods.utils.OverlapRect;
import delaunay_triangulation.Point_dt;

/**
 * Classe que representa um vértice para o método.
 * Pode depender de qual método/API utiliza-se para triangulação. Neste caso é uma especialização de Point_dt.
 * @author wilson
 */
public class PRISMPoint extends Point_dt {
    private OverlapRect rect;
    private int idx;
    
    
    public PRISMPoint(double x, double y, OverlapRect rect, int idx) {
        super(x,y);
        this.rect = rect;
        this.idx = idx;
    }
    
    @Override
    public String toString() {
        //return (int)rect.getUX()+" "+(int)rect.getUY()+" "+(int)rect.getWidth()+" "+(int)rect.getHeight()+" ("+idx +") |||";
        return String.valueOf(idx);
    }

    public OverlapRect getRect() {
        return rect;
    }
    
    public int getIdx() {
        return idx;
    }    
}

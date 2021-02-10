/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.test.draw.color;

import java.awt.Color;

/**
 *
 * @author wilson
 */
public abstract class ColorScale {
    protected Color[] colors;
        
    public Color getColor(int index) {
        if( index > 255 ) index = 255;
        if( index < 0 ) index = 0;
        return colors[index];        
    }
    
    public abstract String getName();
}

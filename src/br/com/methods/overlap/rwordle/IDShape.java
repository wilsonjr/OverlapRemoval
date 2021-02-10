/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.rwordle;

import br.com.methods.utils.OverlapRect;

/**
 * Utilizado para fazer um backup dos elementos quando aplicados aos métodos RWordleC e RWordleL.
 * @author Wilson
 */
public class IDShape {
    private OverlapRect rect;
    private double distance;
    private int originalId;
        
    public IDShape(OverlapRect rect, double distance, int originalId) {
        this.rect = rect;
        this.distance = distance;
        this.originalId = originalId;
    }
    
    public OverlapRect getRect() {
        return rect;
    }
    
    public double getDistance() {
        return distance;
    }       
   
    public int getOriginalId() {
        return originalId;
    }
}

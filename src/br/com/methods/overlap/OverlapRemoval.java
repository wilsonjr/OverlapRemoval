/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap;

import br.com.methods.utils.OverlapRect;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wilson
 */
public interface OverlapRemoval {
    public ArrayList<OverlapRect> apply(ArrayList<OverlapRect> rects);
    
    default public ArrayList<OverlapRect> applyAndShowTime(ArrayList<OverlapRect> rects) {
        
        long startTime = System.currentTimeMillis();
        ArrayList<OverlapRect> returned = apply(rects);
        long endTime = System.currentTimeMillis();
        Logger.getLogger(OverlapRemoval.class.getName()).log(Level.INFO, "Execution time "+toString()+": {0}", ((endTime-startTime)/1000));
        
        return returned;                
    }
}

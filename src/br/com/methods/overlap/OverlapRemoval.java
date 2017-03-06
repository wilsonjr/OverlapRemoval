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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wilson
 */
public interface OverlapRemoval {
    public Map<OverlapRect, OverlapRect> apply(ArrayList<OverlapRect> rects);
    
    default public Map<OverlapRect, OverlapRect> applyAndShowTime(ArrayList<OverlapRect> rects) {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ENTRADA@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        for( int i = 0; i < rects.size(); ++i ) {
            System.out.println(">>> "+i+": "+rects.get(i).x+"; "+rects.get(i).y);
            rects.get(i).setId(i);
        } 
        long startTime = System.currentTimeMillis();
        Map<OverlapRect, OverlapRect> returned = apply(rects);
        long endTime = System.currentTimeMillis();
        Logger.getLogger(OverlapRemoval.class.getName()).log(Level.INFO, "Execution time "+toString()+": {0}", ((endTime-startTime)/1000.0));
        
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@SAIDA@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        returned.entrySet().forEach((e) -> {
            System.out.println(">>> "+e.getKey().getId()+": "+e.getValue().x+"; "+e.getValue().y);
        });
        
        return returned;                
    }
}

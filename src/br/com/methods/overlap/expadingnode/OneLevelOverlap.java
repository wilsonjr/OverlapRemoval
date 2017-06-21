/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.expadingnode;

import br.com.methods.overlap.OverlapRemoval;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wilson
 */
public class OneLevelOverlap implements OverlapRemoval {
    private int rep;
    
    public OneLevelOverlap(int rep) {
        this.rep = rep;
    }

    @Override
    public Map<OverlapRect, OverlapRect> apply(ArrayList<OverlapRect> rects) {
                
        ArrayList<OverlapRect> reprojected = removeOverlap(rects);        
        Collections.sort(reprojected, (a, b)->{
            return Integer.compare(a.getId(), b.getId());
        });
        
        
        Map<OverlapRect, OverlapRect> map = new HashMap<>();
        for( int i = 0; i < rects.size(); ++i )
            map.put(rects.get(i), reprojected.get(rects.get(i).getId()));
        
        return map;
    }
    
    
    private ArrayList<OverlapRect> removeOverlap(ArrayList<OverlapRect> rect) {
        ArrayList<OverlapRect> rects = new ArrayList<>();
        for( int i = 0; i < rect.size(); ++i ) {
            rects.add(new OverlapRect(rect.get(i).x, rect.get(i).y, rect.get(i).width, rect.get(i).height, rect.get(i).getId()));
        }
        
        Collections.sort(rects, (a, b) -> {
            return Double.compare(Util.euclideanDistance(b.x, b.y, rect.get(rep).x, rect.get(rep).y), 
                                  Util.euclideanDistance(a.x, a.y, rect.get(rep).x, rect.get(rep).y));
        });
        
        //System.out.println("Representative "+rect.get(rep).getId());
        
                
        for( int i = 0; i < rects.size(); ++i ) {
            OverlapRect r1 = rects.get(i);
            for( int j = i+1; j < rects.size(); ++j ) {
                OverlapRect r2 = rects.get(j);
                if( r1.intersects(r2) ) {
                    //System.out.println("Removendo sobreposição de "+r1.getId()+" e "+r2.getId());
                    double inter = intersection(r1, r2);
                    
                    double ax = r2.x;
                    double ay = r2.y;
                    double bx = r1.x;
                    double by = r1.y;

                    double lenAB = Util.euclideanDistance(ax, ay, bx, by);
                    
                    if( lenAB == 0.0 ) {
                        ax += 0.5;
                        ay += 0.5;
                        lenAB = Util.euclideanDistance(ax, ay, bx, by);
                        inter = intersection(r1, new OverlapRect(ax, ay, r2.width, r2.height));
                    }

                    //System.out.println("len: "+lenAB+" --  inter: "+inter);
                    double ammountx = (bx-ax)/lenAB * (inter*lenAB - lenAB);
                    double ammounty = (by-ay)/lenAB * (inter*lenAB - lenAB);
                    
                    r1.x = bx+ammountx;
                    r1.y = by+ammounty;
                    
                    
                    for( int o = i; o >= 0; --o ) {                    
                        OverlapRect p = rects.get(o);

                        List<OverlapRect> first = new ArrayList<>();

                        for( int k = o-1; k >= 0; --k ) {
                            first.add(rects.get(k));
                        }

                        if( !first.isEmpty() ) {

                            Collections.sort(first, (a, b) -> {
                                return Double.compare(Util.euclideanDistance(a.x, a.y, p.x, p.y), 
                                                      Util.euclideanDistance(b.x, b.y, p.x, p.y));
                            });


                            for( int k = 0; k < first.size(); ++k ) {
                                OverlapRect r3 = first.get(k);
                                if( p.intersects(r3) ) {
                                   // System.out.println("Atualizando posições: "+r3.getId());
                                    inter = intersection(p, r3);
                                    ax = p.x;
                                    ay = p.y;
                                    bx = r3.x;
                                    by = r3.y;

                                    lenAB = Util.euclideanDistance(ax, ay, bx, by);

                                    ammountx = (bx-ax)/lenAB * (inter*lenAB - lenAB);
                                    ammounty = (by-ay)/lenAB * (inter*lenAB - lenAB);

                                    r3.x = bx + ammountx;
                                    r3.y = by + ammounty;
                                }
                            }

                        }
                    }
                    
                    
                } 
            }
            
        }
        
        return rects;        
    }
    
    private double intersection(OverlapRect u, OverlapRect v) {
        return Math.max(
            Math.min(
               (u.getWidth()/2. + v.getWidth()/2.)/Math.abs(u.getCenterX() - v.getCenterX()), 
               (u.getHeight()/2. + v.getHeight()/2.)/Math.abs(u.getCenterY() - v.getCenterY())
            ), 1);
    }
    
}

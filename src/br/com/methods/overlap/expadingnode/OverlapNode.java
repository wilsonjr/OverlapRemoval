/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.expadingnode;

import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author wilson
 */
public class OverlapNode {
    
    
    private List<OverlapRect> instances;
    private OverlapRect boundingBox;
    private int representative;
    
    public OverlapNode(List<OverlapRect> instances, int representative) {
        this.instances = instances;        
        this.representative = representative;
    }
    
    
    private void calculateBoundingBox() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -1.0, maxY = -1.0;
        
        
        for( OverlapRect e: instances ) {
            
            double x = e.getUX();
            double y = e.getUY();
            
            if( x < minX ) 
                minX = x;
            if( y < minY )
                minY = y;
            
            if( x > maxX )
                maxX = x;
            
            if( y > maxY )
                maxY = y;            
        }
        
        boundingBox = new OverlapRect(minX, minY, maxX-minX, maxY-minY);
    }
    
    public void removeOverlap() {
        
        List<OverlapRect> subset = new ArrayList<>();
        for( int i = 0; i < instances.size(); ++i ) {
            subset.add(new OverlapRect(instances.get(i).x, instances.get(i).y, instances.get(i).width, instances.get(i).height, instances.get(i).getId()));
        }
        
        Collections.sort(subset, (a, b) -> {
            return Double.compare(Util.euclideanDistance(b.x, b.y, instances.get(representative).x, instances.get(representative).y), 
                                  Util.euclideanDistance(a.x, a.y, instances.get(representative).x, instances.get(representative).y));
        });
        
        System.out.println("Representative "+instances.get(representative).getId());
        
                
        for( int i = 0; i < subset.size(); ++i ) {
            OverlapRect r1 = subset.get(i);
            for( int j = i+1; j < subset.size(); ++j ) {
                OverlapRect r2 = subset.get(j);
                if( r1.intersects(r2) ) {
                    System.out.println("Removendo sobrepresentativeosição de "+r1.getId()+" e "+r2.getId());
                    double inter = intersection(r1, r2);
                    
                    double ax = r2.x;
                    double ay = r2.y;
                    double bx = r1.x;
                    double by = r1.y;

                    double lenAB = Util.euclideanDistance(ax, ay, bx, by);
                    
                    if( lenAB == 0.0 ) {
                        ax += 0.5;
                        //ay += 0.;
                        lenAB = Util.euclideanDistance(ax, ay, bx, by);
                        inter = intersection(r1, new OverlapRect(ax, ay, r2.width, r2.height));
                    }

                    System.out.println("len: "+lenAB+" --  inter: "+inter);
                    double ammountx = (bx-ax)/lenAB * (inter*lenAB - lenAB);
                    double ammounty = (by-ay)/lenAB * (inter*lenAB - lenAB);
                    
                    r1.x = bx+ammountx;
                    r1.y = by+ammounty;
                    
                    
                    for( int o = i; o >= 0; --o ) {                    
                        OverlapRect p = subset.get(o);

                        List<OverlapRect> first = new ArrayList<>();

                        for( int k = o-1; k >= 0; --k ) {
                            first.add(subset.get(k));
                        }

                        if( !first.isEmpty() ) {

                            Collections.sort(first, (a, b) -> {
                                return Double.compare(Util.euclideanDistance(a.x, a.y, p.x, p.y), 
                                                      Util.euclideanDistance(b.x, b.y, p.x, p.y));
                            });


                            for( int k = 0; k < first.size(); ++k ) {
                                OverlapRect r3 = first.get(k);
                                if( p.intersects(r3) ) {
                                    System.out.println("Atualizando posições: "+r3.getId());
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
        
        instances = subset;
        calculateBoundingBox();
    }
    
    private double intersection(OverlapRect u, OverlapRect v) {
        return Math.max(
            Math.min(
               (u.getWidth()/2. + v.getWidth()/2.)/Math.abs(u.getCenterX() - v.getCenterX()), 
               (u.getHeight()/2. + v.getHeight()/2.)/Math.abs(u.getCenterY() - v.getCenterY())
            ), 1);
    }
    
    
    
    
    
    
}

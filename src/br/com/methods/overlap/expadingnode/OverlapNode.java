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
    
    
    private List<OverlapNode> instances;
    private OverlapRect boundingBox;
    private OverlapRect finalBoundingBox;
    private int representative;
    private int id;
    
    public OverlapNode(OverlapRect boundingBox) {
        this.boundingBox = boundingBox;
        this.id = boundingBox.getId();
    }
    
    public OverlapNode(List<OverlapNode> instances, int representative) {
        this.instances = instances;        
        this.representative = representative;
    }
    
    public OverlapRect getBoundingBox() {
        return boundingBox;
    }
    
    private void calculateBoundingBox() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -1.0, maxY = -1.0;
        
        
        for( OverlapNode e: instances ) {
            
            double x = e.getBoundingBox().getUX();
            double y = e.getBoundingBox().getUY();
            
            if( x < minX ) 
                minX = x;
            if( y < minY )
                minY = y;
            
            if( x > maxX )
                maxX = x;
            
            if( y > maxY )
                maxY = y;            
        }
        
        finalBoundingBox = new OverlapRect(minX, minY, maxX-minX, maxY-minY);
        boundingBox = new OverlapRect(minX, minY, maxX-minX, maxY-minY);
    }
    
    public void removeOverlap() {
        
        List<OverlapNode> subset = new ArrayList<>();
        for( int i = 0; i < instances.size(); ++i ) {
            subset.add(new OverlapNode(new OverlapRect(instances.get(i).getBoundingBox().x, instances.get(i).getBoundingBox().y, 
                                       instances.get(i).getBoundingBox().width, instances.get(i).getBoundingBox().height, 
                                       instances.get(i).getBoundingBox().getId())));
        }
        
        Collections.sort(subset, (a, b) -> {
            return Double.compare(Util.euclideanDistance(b.getBoundingBox().x, b.getBoundingBox().y, 
                                                         instances.get(representative).getBoundingBox().x, 
                                                         instances.get(representative).getBoundingBox().y), 
                                  Util.euclideanDistance(a.getBoundingBox().x, a.getBoundingBox().y, 
                                                         instances.get(representative).getBoundingBox().x, 
                                                         instances.get(representative).getBoundingBox().y));
        });
        
        System.out.println("Representative "+instances.get(representative).getBoundingBox().getId());
        
                
        for( int i = 0; i < subset.size(); ++i ) {
            OverlapNode r1 = subset.get(i);
            for( int j = i+1; j < subset.size(); ++j ) {
                OverlapNode r2 = subset.get(j);
                if( r1.getBoundingBox().intersects(r2.getBoundingBox()) ) {
                    System.out.println("Removendo sobrepresentativeosição de "+r1.getBoundingBox().getId()+" e "+r2.getBoundingBox().getId());
                    double inter = intersection(r1.getBoundingBox(), r2.getBoundingBox());
                    
                    double ax = r2.getBoundingBox().x;
                    double ay = r2.getBoundingBox().y;
                    double bx = r1.getBoundingBox().x;
                    double by = r1.getBoundingBox().y;

                    double lenAB = Util.euclideanDistance(ax, ay, bx, by);
                    
                    if( lenAB == 0.0 ) {
                        ax += 0.5;
                        //ay += 0.;
                        lenAB = Util.euclideanDistance(ax, ay, bx, by);
                        inter = intersection(r1.getBoundingBox(), new OverlapRect(ax, ay, r2.getBoundingBox().width, r2.getBoundingBox().height));
                    }

                    System.out.println("len: "+lenAB+" --  inter: "+inter);
                    double ammountx = (bx-ax)/lenAB * (inter*lenAB - lenAB);
                    double ammounty = (by-ay)/lenAB * (inter*lenAB - lenAB);
                    
                    r1.getBoundingBox().x = bx+ammountx;
                    r1.getBoundingBox().y = by+ammounty;
                    
                    
                    for( int o = i; o >= 0; --o ) {                    
                        OverlapNode p = subset.get(o);

                        List<OverlapNode> first = new ArrayList<>();

                        for( int k = o-1; k >= 0; --k ) {
                            first.add(subset.get(k));
                        }

                        if( !first.isEmpty() ) {

                            Collections.sort(first, (aa, bb) -> {
                                OverlapRect a = aa.boundingBox;
                                OverlapRect b = bb.boundingBox;
                                
                                return Double.compare(Util.euclideanDistance(a.x, a.y, p.getBoundingBox().x, p.getBoundingBox().y), 
                                                      Util.euclideanDistance(b.x, b.y, p.getBoundingBox().x, p.getBoundingBox().y));
                            });


                            for( int k = 0; k < first.size(); ++k ) {
                                OverlapNode r3 = first.get(k);
                                if( p.getBoundingBox().intersects(r3.getBoundingBox()) ) {
                                    System.out.println("Atualizando posições: "+r3.getBoundingBox().getId());
                                    inter = intersection(p.getBoundingBox(), r3.getBoundingBox());
                                    ax = p.getBoundingBox().x;
                                    ay = p.getBoundingBox().y;
                                    bx = r3.getBoundingBox().x;
                                    by = r3.getBoundingBox().y;

                                    lenAB = Util.euclideanDistance(ax, ay, bx, by);

                                    ammountx = (bx-ax)/lenAB * (inter*lenAB - lenAB);
                                    ammounty = (by-ay)/lenAB * (inter*lenAB - lenAB);

                                    r3.getBoundingBox().x = bx + ammountx;
                                    r3.getBoundingBox().y = by + ammounty;
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

    public List<OverlapNode> getInstances() {
        return instances;
    }

    public OverlapRect getFinalBoundingBox() {
        return finalBoundingBox;
    }

    public void updatePositions(double deltax, double deltay) {
        
        for( int i = 0; i < instances.size(); ++i ) {
            instances.get(i).boundingBox.moveX(deltax);
            instances.get(i).boundingBox.moveY(deltay);
        }
        
        
    }

    public void updateInstances() {
        
        List<OverlapNode> nodes = new ArrayList<>();
        
        for( int i = 0; i < instances.size(); ++i ) {
            for( int j = 0; j < instances.get(i).getInstances().size(); ++j )
                nodes.add(instances.get(i).getInstances().get(j));
        }
        
        instances = nodes;                 
    }

    public int getId() {
        return id;
    }
    
    
    
    
    
    
    
    
}

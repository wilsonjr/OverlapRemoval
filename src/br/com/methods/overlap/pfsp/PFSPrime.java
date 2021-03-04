/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.methods.overlap.pfsp;

import br.com.methods.overlap.OverlapRemoval;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wilson
 */
public class PFSPrime implements OverlapRemoval {

    @Override
    public Map<OverlapRect, OverlapRect> apply(List<OverlapRect> rects) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public Graph apply(Graph graph) {
            
        graph.nodes.stream().forEach(n -> {
            n.up_x = n.x;
            n.up_y = n.y;
            n.up_gamma = 0.0;
        });
        
        horizontalScan(graph.nodes);
        
        graph.nodes.stream().forEach(n -> {
            if( n.up_gamma == Double.MIN_VALUE )
                n.up_gamma = Double.MIN_VALUE;
            else 
                n.up_gamma = 0.0;
        });
        
        if( graph.nodes.get(0).up_gamma == Double.MIN_VALUE ) {
            return null;
        }
        
        verticalScan(graph.nodes);
        
        graph.nodes.stream().forEach(n -> {
            if( n.up_gamma == Double.MIN_VALUE ) {
                n.up_gamma = Double.MIN_VALUE;
            } else {
                n.x = n.up_x + n.width/2.0;
                n.y = n.up_y + n.height/2.0;
            }
            
        });
        
        if( graph.nodes.get(0).up_gamma == Double.MIN_VALUE ) {
            return null;
        }
        
        
        return graph;
    } 
    
    private int sameX(List<Node> nodes, int i) {
        int index = i;
        for( ; index < nodes.size()-1; ++index ) {
            if( nodes.get(index).x != nodes.get(index+1).x )
                return index;
        }
            
        return index;
    }
    
    private int sameY(List<Node> nodes, int i) {
        int index = i;
        for( ; index < nodes.size()-1; ++index ) {
            if( nodes.get(index).y != nodes.get(index+1).y )
                return index;
        }
            
        return index;
    }

    private void horizontalScan(List<Node> nodes) {
        nodes.sort((Node a, Node b) -> Double.compare(a.x, b.x));
        
        int i = 0;
        double sigma = 0;
        
        Node lmin = nodes.get(0);
        
        while( i < nodes.size() ) {
            int k = sameX(nodes, i);
            double gamma = 0;            
            Node u = nodes.get(i);
            
            if( u.x > lmin.x ) {
                
                for( int m = i; m <= k; m++ ) {
                    Node v = nodes.get(i);
                    // gamma'en peu plus
                    double gamma_pp = 0;

                    for( int j = 0; j < i; j++ ) {
                        Node nodeJ = nodes.get(j);
                        if( nodeJ.up_gamma == Double.MIN_VALUE )
                            throw new RuntimeException("Cannot set undefined updated position for nodeJ");
                        gamma_pp = Math.max(nodeJ.up_gamma + force(nodeJ, v).x, gamma_pp);
                        
                    }

                    // gangplanck
                    double gamma_p = minX(v) + gamma_pp < minX(lmin) ? sigma : gamma_pp;

                    gamma = Math.max(gamma, gamma_p);
                }
                
            }
            
            
            for( int m = i; m <= k; m++ ) {
                Node r = nodes.get(m);
                if( r.up_gamma == Double.MIN_VALUE )
                    throw new RuntimeException("Cannot set undefined updated position for nodeJ");
                r.up_gamma = gamma;
                r.up_x = minX(r) + gamma;
                
                if( Util.bounding_box != null ) {
                    r.up_x = Util.checkBoundX((float) r.up_x);
                } 
                
                if( minX(r) < minX(lmin) ) {
                  lmin = r;
                }
            }

            double delta = 0;

            for (int m = i; m <= k; m++) {
                for (int j = k + 1; j < nodes.size(); j++) {
                    Node f = force(nodes.get(m), nodes.get(j));
                    if (f.x > delta) {
                        delta = f.x;
                    }
                }
            }

            sigma += delta;
            i = k + 1;
            
        }

    }

    private void verticalScan(List<Node> nodes) {
        nodes.sort((Node a, Node b) -> Double.compare(a.y, b.y));

        int i = 0;
        Node lmin = nodes.get(0);
        double sigma = 0;

        while (i < nodes.size()) {
            Node u = nodes.get(i);
            int k = sameY(nodes, i);

            double gamma = 0;
            if (u.y > lmin.y) {
                for (int m = i; m <= k; m++) {
                    double gamma_pp = 0;
                    Node v = nodes.get(m);
                    for (int j = 0; j < i; j++) {
                        Node nodeJ = nodes.get(j);
                        if (nodeJ.up_gamma == Double.MIN_VALUE)
                            throw new RuntimeException("Cannot set undefined updated position for nodeJ");

                        gamma_pp = Math.max(nodeJ.up_gamma + force(nodeJ, v).y, gamma_pp);
                    }

                    double gamma_p = minY(v) + gamma_pp < minY(lmin) ? sigma : gamma_pp;

                    gamma = Math.max(gamma, gamma_p);
                }
            }
            
            for (int m = i; m <= k; m++) {
                Node r = nodes.get(m);
                if (r.up_gamma == Double.MIN_VALUE)
                    throw new RuntimeException("Cannot set undefined updated position for r");

                r.up_gamma = gamma;
                r.up_y = minY(r) + gamma;
                if( Util.bounding_box != null ) {
                    r.up_y = Util.checkBoundY((float) r.up_y);
                } 

                if (minY(r) < minY(lmin)) {
                  lmin = r;
                }
            }

            double delta = 0;
            for (int m = i; m <= k; m++) {
                for (int j = k + 1; j < nodes.size(); j++) {
                    Node f = force(nodes.get(m), nodes.get(j));
                    if (f.y > delta) {
                      delta = f.y;
                    }
                }
            }
            
            sigma += delta;
            i = k + 1;
        }
    }
    
    
    
    
    
    private Node force(Node vi, Node vj) {
        Node f = new Node();
        f.x = 0;
        f.y = 0;
        
        double delta_x = DX(vi, vj);
        double delta_y = DY(vi, vj);
        double adelta_x = Math.abs(delta_x);
        double adelta_y = Math.abs(delta_y);
        
        
        double gij = delta_y / delta_x;

        double Gij = (vi.height + vj.height) / (vi.width + vj.width);

        if ((Gij >= gij && gij > 0) || (-Gij <= gij && gij < 0) || gij == 0.0) {
          f.x = (delta_x / adelta_x) * ((vi.width + vj.width) / 2 - adelta_x);
          f.y = f.x * gij;
        }
        if ((Gij < gij && gij > 0) || (-Gij > gij && gij < 0)) {
          f.y = (delta_y / adelta_y) * ((vi.height + vj.height) / 2 - adelta_y);
          f.x = f.y / gij;
        }

        return f;
    }
    
    
    private double DX(Node p1, Node p2) {
        return p2.x - p1.x;
    }
    
    private double DY(Node p1, Node p2) {
        return p2.y - p1.y;
    }

    private double minX(Node v) {
        return v.x - v.width/2;
    }
    
    private double maxX(Node v) {
        return v.x + v.width/2;
    }
    
    private double minY(Node v) {
        return v.y - v.height/2;
    }
    
    private double maxY(Node v) {
        return v.y + v.height/2;
    }
}

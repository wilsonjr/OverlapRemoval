/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.projection.spacereduction;

import br.com.methods.utils.Util;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 *
 * @author wilson
 */
public class ContextPreserving {
    
    private static final double EPS = 1e-6;
    private static final double KA = 15;
    private static final double KR = 1000;
    private static final double MAX_IT = 1000;
    private double T = 1;
    
    private final Rectangle2D.Double[] _initialPositions;
    private Rectangle2D.Double[] _rects;
    private Rectangle2D.Double[] _rectPositions;
    
    private int[][] delaunayEdges;
            
    public ContextPreserving(Rectangle2D.Double[] initialPositions) {
        _initialPositions = initialPositions;
    }
    
    public Map<Rectangle2D.Double, Rectangle2D.Double> reduceSpace(Rectangle2D.Double[] projection) {
        _rects = projection;
        _rectPositions = new Rectangle2D.Double[_rects.length];
        
        applyAlgorithm();
        
        Map<Rectangle2D.Double, Rectangle2D.Double> positions = new HashMap<>();        
        IntStream.range(0, _rects.length).forEach(i->positions.put(_rects[i], _rectPositions[i]));

        return positions;
        
    }
    
    
    public void applyAlgorithm() {
        
        delaunayEdges = computeDelaunay();
        
        applyForceAlgorithm();
    }

    private void applyForceAlgorithm() {
        
        for( int i = 0; i < MAX_IT; ++i ) {
            if( !doIteration() )
                break;
            
            if( i % 5 == 0 )
                T *= 0.95;
        }
        
        new ForceDirectedOverlapRemoval().run(_initialPositions);
        new ForceDirectedUniformity().run(_initialPositions);
    }

    private int[][] computeDelaunay() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean doIteration() {
        double maxWeight = computeMaxWeight();
        Rectangle2D.Double bb = computeBoundingBox();
        
        double avgStep = 0;
        
        for( int i = 0; i < _rects.length; ++i ) {
            Rectangle2D.Double rect = _initialPositions[i];
            Point2D.Double dxy = new Point2D.Double(0, 0);
            
            if( !overlap(i) ) {
                // attractive force (compact principle)
                Point2D.Double p = computeAttractiveForce(bb, i, rect, maxWeight);
                dxy.setLocation(dxy.x+p.x, dxy.y+p.y);
                
                // resulsion force (planar principle)
                p = computePlanarForce(bb, i, rect);
                dxy.setLocation(dxy.x+p.x, dxy.y+p.y);
            } else {
                // repulsion force (removing overlaps)
                Point2D.Double p = computeRepulsiveForce(bb, i, rect);
                dxy.setLocation(dxy.x+p.x, dxy.y+p.y);
                
                // repulsion force (planar principle)
                p = computePlanarForce(bb, i, rect);
                dxy.setLocation(dxy.x+p.x, dxy.y+p.y);
            }
            
            // move the rectangle
            dxy = normalize(dxy, bb);
            rect.setRect(rect.x + dxy.x, rect.y + dxy.y, rect.width, rect.height);
            
            avgStep += Util.distanciaEuclideana(dxy.x, dxy.y, 0, 0);
        }
        
        avgStep /= _rects.length;
        return avgStep > Math.max(bb.width, bb.height) / 10000.0;        
    }

    private Rectangle2D.Double computeBoundingBox() {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        
        for( Rectangle2D.Double r: _initialPositions ) {
            minX = Math.min(minX, r.getMinX());
            maxX = Math.max(maxX, r.getMaxX());
            minY = Math.min(minY, r.getMinY());
            maxY = Math.max(maxY, r.getMaxY());
        }
        
        return new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY);
    }

    private double computeMaxWeight() {
        double maxWeight = 0.;
        
        for( int i = 0; i < _rects.length; ++i )
            maxWeight = Math.max(maxWeight, _rects[i].getWeight());
        
        return maxWeight;
    }

    private boolean overlap(int i) {
        for( int j = 0; j < _rects.length; ++j )
            if( i != j && _initialPositions[i].intersects(_initialPositions[j]) )
                return true;
        
        return false;
    }

    private Point2D.Double computeAttractiveForce(Rectangle2D.Double bb, int i, Rectangle2D.Double recti, double maxWeight) {
            
        Point2D.Double dxy = new Point2D.Double(0, 0);
        
        double cnt = 0;
        for( int j = 0; j < _rects.length; ++j ) {
            if( i != j ) {
                
                Rectangle2D.Double rectj = _initialPositions[j];
                
                double wi = Math.max(_rects[i].getWeight(), maxWeight/5.);
                double wj = Math.max(_rects[j].getWeight(), maxWeight/5.);
                double dist = Util.rectToRectDistance(recti, rectj);
                double force = wi * wj * dist / (maxWeight*maxWeight);                
                if( T < 0.5 )
                    force *= T;
                
                Point2D.Double dir = new Point2D.Double(rectj.getCenterX()-recti.getCenterX(), rectj.getCenterY()-recti.getCenterY());
                        
                double len = Util.distanciaEuclideana(dir.x, dir.y, 0, 0);
                if( len < EPS )
                    continue;
                
                // normalize
                dir.setLocation(dir.x/len, dir.y/len);
                dir.setLocation(dir.x*force, dir.y*force);
                
                dxy.setLocation(dxy.x+dir.x, dxy.y+dir.y);
                cnt++;
            }
        }
        
        if( cnt == 0.0 )
            cnt = 1;
        dxy.setLocation(dxy.x*(1./cnt), dxy.y*(1./cnt));
        
        return dxy;
    }

    private Point2D.Double computeRepulsiveForce(Rectangle2D.Double bb, int i, Rectangle2D.Double recti) {
        Point2D.Double dxy = new Point2D.Double(0, 0);
        
        double cnt = 0.0;
        for( int j = 0; j < _rects.length; ++j ) {
            if( i != j ) {
                
                Rectangle2D.Double rectj = _initialPositions[j];
                
                if( recti.intersects(rectj) ) {
                    
                    double hix = Math.min(recti.getMaxX(), rectj.getMaxX());
                    double lox = Math.max(recti.getMinX(), rectj.getMinX());
                    double hiy = Math.min(recti.getMaxY(), rectj.getMaxY());
                    double loy = Math.max(recti.getMinY(), rectj.getMinY());
                    double dx = hix - lox;
                    double dy = hiy - loy;
                    
                    double force = KR * Math.min(dx, dy);
                    
                    Point2D.Double dir = new Point2D.Double(rectj.getCenterX()-recti.getCenterX(), 
                            rectj.getCenterX()-recti.getCenterX());
                    
                    double len = Util.distanciaEuclideana(dir.x, dir.y, 0, 0);
                    if( len < EPS )
                        continue;
                    
                    dir.setLocation(dir.x/len, dir.y/len);
                    dir.setLocation(dir.x*force, dir.y*force);
                    
                    dxy.setLocation(dxy.x-dir.x, dxy.y-dir.y);
                    cnt++;                    
                }
            }    
        }
        
        if( cnt == 0.0 )
            cnt = 1;
        dxy.setLocation(dxy.x*(1./cnt), dxy.y*(1./cnt));
        return dxy;       
    }

    private Point2D.Double computePlanarForce(Rectangle2D.Double bb, int i, Rectangle2D.Double recti) {
        Point2D.Double dxy = new Point2D.Double(0, 0);
        
        double cnt = 0.0;
        for( int t = 0; t < delaunayEdges[i].length; t += 2 ) {
            int j = delaunayEdges[i][t];
            int k = delaunayEdges[i][t+1];
            
            Point2D.Double pi = new Point2D.Double(_initialPositions[i].getCenterX(), _initialPositions[i].getCenterY());
            Point2D.Double pj = new Point2D.Double(_initialPositions[j].getCenterX(), _initialPositions[j].getCenterY());
            Point2D.Double pk = new Point2D.Double(_initialPositions[k].getCenterX(), _initialPositions[k].getCenterY());
            
            if( orientation(pi, pj, pk) < 0 ) {
                Point2D.Double force = planarForce(pi, pj, pk);
                force.setLocation(force.x*KA, force.y*KA);
                
                dxy.setLocation(dxy.x+force.x, dxy.y+force.y);
                cnt++;
            }
        }
        if( cnt > 0.0 )
            dxy.setLocation(dxy.x*(1./cnt), dxy.y*(1./cnt));
        return dxy;
    }

    private int orientation(Point2D.Double pi, Point2D.Double pj, Point2D.Double pk) {
        // cross product
        double cr = (pj.x-pi.x) * (pk.y-pi.y) - (pk.x-pi.x) * (pj.y-pi.y);
        if( cr > 1e-8 )
            return 1;
        else if( cr < -1e-8 )
            return -1;
        return 0;
    }

    private Point2D.Double planarForce(Point2D.Double pi, Point2D.Double pj, Point2D.Double pk) {
        double dx = pk.x - pj.x;
        double dy = pk.y - pj.y;
        
        double dist = Util.pointToLineDistance(new Point2D.Double(pj.x, pj.y), new Point2D.Double(pk.x, pk.y), 
                new Point2D.Double(pi.x, pi.y));
        
        Point2D.Double norm = new Point2D.Double(-dy, dx);
        norm.setLocation(norm.x*dist, norm.y*dist);
        
        return norm;
    }
    
    
    
    
}

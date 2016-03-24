/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.overlayanalisys.sizeincrease;

import br.com.metodos.utils.Retangulo;
import br.com.metodos.utils.Util;
import br.com.overlayanalisys.definition.Metric;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author wilson
 */
public class SizeIncrease implements Metric {
    private Polygon polygon1 = new Polygon();
    private Polygon polygon2 = new Polygon();
    
    @Override
    public double execute(ArrayList<Retangulo> pts1, ArrayList<Retangulo> pts2) {
        
        Point2D[] p1 = new Point2D[pts1.size()];
        for( int i = 0; i < p1.length; ++i )
            p1[i] = new Point2D(pts1.get(i).getCenterX(), pts1.get(i).getCenterY());
        GrahamScan gs1 = new GrahamScan(p1);
        
        Iterator<Point2D> it1 = gs1.hull().iterator();
        while( it1.hasNext() ) {
            Point2D pp = it1.next();
            polygon1.addPoint((int)pp.x(), (int)pp.y());
        }
        
        Point2D[] p2 = new Point2D[pts2.size()];
        for( int i = 0; i < p2.length; ++i )
            p2[i] = new Point2D(pts2.get(i).getCenterX(), pts2.get(i).getCenterY());
        GrahamScan gs2 = new GrahamScan(p2);
        
        Iterator<Point2D> it2 = gs2.hull().iterator();
        while( it2.hasNext() ) {
            Point2D pp = it2.next();
            polygon2.addPoint((int)pp.x(), (int)pp.y());
        }
        
        
        return (double)Util.polygonArea(polygon1)/(double)Util.polygonArea(polygon2);
    }

    public Polygon getPolygon1() {
        return polygon1;
    }

    public Polygon getPolygon2() {
        return polygon2;
    }
       
    
    
    
}

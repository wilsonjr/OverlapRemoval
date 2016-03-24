/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.overlayanalisys.layoutsimilarity;

import br.com.metodos.overlap.prism.PRISMEdge;
import br.com.metodos.overlap.prism.PRISMPoint;
import br.com.metodos.utils.Retangulo;
import br.com.metodos.utils.Util;
import br.com.overlayanalisys.definition.Metric;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Triangle_dt;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author wilson
 */
public class LayoutSimilarity implements Metric {

    @Override
    public double execute(ArrayList<Retangulo> pts1, ArrayList<Retangulo> pts2) {
        double sum = 0.0;
        
        PRISMPoint[] points = new PRISMPoint[pts1.size()];
        
        for( Retangulo r:  pts1 )
            points[r.getId()] = new PRISMPoint(r.getCenterX(), r.getCenterY(), r, r.getId());
        
        ArrayList<PRISMEdge> edges = new ArrayList<>();        
        Delaunay_Triangulation dt = new Delaunay_Triangulation(points);
        
        Iterator<Triangle_dt> triIt = dt.trianglesIterator();
        while( triIt.hasNext() ) {
            Triangle_dt t = triIt.next();
            if( !t.isHalfplane() ) {
                PRISMEdge e = new PRISMEdge((PRISMPoint)t.p1(), (PRISMPoint)t.p2());
                if( !edges.contains(e) )
                    edges.add(e);

                e = new PRISMEdge((PRISMPoint)t.p1(), (PRISMPoint)t.p3());
                if( !edges.contains(e) )
                    edges.add(e);

                e = new PRISMEdge((PRISMPoint)t.p2(), (PRISMPoint)t.p3());
                if( !edges.contains(e) )
                    edges.add(e);
            }
        }
        
        double rBarra = 0.0;
        ArrayList<Double> rij = new ArrayList<>();
        for( int i = 0; i < edges.size(); ++i ) {
            double dOri = Util.distanciaEuclideana(pts1.get(edges.get(i).getU().getIdx()).getCenterX(), 
                                                   pts1.get(edges.get(i).getU().getIdx()).getCenterY(), 
                                                   pts1.get(edges.get(i).getV().getIdx()).getCenterX(), 
                                                   pts1.get(edges.get(i).getV().getIdx()).getCenterY());
            double dMod = Util.distanciaEuclideana(pts2.get(edges.get(i).getU().getIdx()).getCenterX(), 
                                                   pts2.get(edges.get(i).getU().getIdx()).getCenterY(), 
                                                   pts2.get(edges.get(i).getV().getIdx()).getCenterX(), 
                                                   pts2.get(edges.get(i).getV().getIdx()).getCenterY());
            
            
            rij.add(dMod/dOri);
            rBarra += (dMod/dOri);
        }
        
        rBarra /= edges.size();
        
        double variancia = 0.0;
        for( Double d: rij ) 
            variancia += Math.pow(d - rBarra, 2.0);
        
        variancia /= edges.size();
        double desvioPadraoNorm = Math.sqrt(variancia)/rBarra;
        
        return desvioPadraoNorm;
    }
    
}

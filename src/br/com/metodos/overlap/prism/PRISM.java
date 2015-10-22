/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.prism;

import br.com.metodos.overlap.vpsc.Restricao;
import br.com.metodos.overlap.vpsc.VPSC;
import br.com.metodos.overlap.vpsc.Variavel;
import br.com.metodos.utils.Retangulo;
import br.com.metodos.utils.Util;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Triangle_dt;
import java.util.ArrayList;
import java.util.Iterator;


/**
 *
 * @author wilson
 */
public class PRISM {
    
    public static PRISMEdge[] findRestOverlaps(ArrayList<Retangulo> retangulos) {
        /**
         * copy rectangles 
         */         
        ArrayList<Retangulo> projected = new ArrayList<>();
        for( Retangulo r: retangulos )  
            projected.add(new Retangulo(r.getUX(), r.getUY(), r.getWidth(), r.getHeight()));
        
        /**
         *  init all variables
         */         
        ArrayList<Variavel> varsx = new ArrayList<>();
        ArrayList<Variavel> varsy = new ArrayList<>();
        for( int i = 0; i < projected.size(); ++i ) {
            varsx.add(new Variavel(i));
            varsy.add(new Variavel(i));
        }
        
        /**
         * generate horizontal constraints and solve vpsc for x
         */
        ArrayList<Restricao> restricoesx = new ArrayList<>();
        ArrayList<Restricao> restricoesy = new ArrayList<>();
        VPSC.generateCx(projected, varsx, restricoesx);
        VPSC.generateCy(projected, varsy, restricoesy);        
        
        PRISMEdge[] newOverlaps = new PRISMEdge[restricoesx.size()+restricoesy.size()];
        for( int i = 0; i < restricoesx.size(); ++i ) {
            int u = restricoesx.get(i).getLeft().getId();
            int v = restricoesx.get(i).getRight().getId();
            newOverlaps[i] = new PRISMEdge(new PRISMPoint(retangulos.get(u).getCenterX(), retangulos.get(u).getCenterY(), 
                                                          retangulos.get(u), u),
                                           new PRISMPoint(retangulos.get(v).getCenterX(), retangulos.get(v).getCenterY(), 
                                                          retangulos.get(v), v));
        }
        
        for( int j = restricoesx.size(), i = 0; i < restricoesy.size(); ++i, ++j ) {
            int u = restricoesy.get(i).getLeft().getId();
            int v = restricoesy.get(i).getRight().getId();
            newOverlaps[j] = new PRISMEdge(new PRISMPoint(retangulos.get(u).getCenterX(), retangulos.get(u).getCenterY(), 
                                                          retangulos.get(u), u),
                                           new PRISMPoint(retangulos.get(v).getCenterX(), retangulos.get(v).getCenterY(), 
                                                          retangulos.get(v), v));
        }
        
        return newOverlaps;
    }
    
    private static ArrayList<Retangulo> apply(ArrayList<Retangulo> rects, boolean augmentGp) {
        ArrayList<Retangulo> projected = new ArrayList<>();
        
        for( int i = 0; i < rects.size(); ++i )
            projected.add(new Retangulo(rects.get(i).getUX(), rects.get(i).getUY(), rects.get(i).getWidth(), rects.get(i).getHeight()));
                
        // form a proximity graph Gp of x0 by Delaunay triangulation        
        PRISMPoint[] points = new PRISMPoint[projected.size()];
       
        int maxIterations = 10000;
        
        do {
            for( int i = 0; i < projected.size(); ++i )
                points[i] = new PRISMPoint(projected.get(i).getCenterX(), projected.get(i).getCenterY(), projected.get(i), i);
            
            Delaunay_Triangulation dt = new Delaunay_Triangulation(points);
            ArrayList<PRISMEdge> edges = new ArrayList<>();
           
            Iterator<Triangle_dt> trianglesIterator = dt.trianglesIterator();
            while( trianglesIterator.hasNext() ) {
                Triangle_dt t = trianglesIterator.next();
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
            
            if( augmentGp ) {
                PRISMEdge[] restEdge = findRestOverlaps(projected);
                for( int i = 0; i < restEdge.length; ++i ) {
                    if( !edges.contains(restEdge[i]) ) {
//                        System.out.println("NAO CONTEM");
                        edges.add(restEdge[i]);
                        
                    } 
                }
            }
//            System.out.println("EDGES SIZE - ARRAYLIST: "+edges.size());
            
            PRISMEdge[] arestas = new PRISMEdge[edges.size()];
            boolean flag = false;
            for( int i = 0; i < edges.size(); ++i ) {
                arestas[i] = edges.get(i);
                
                if( Util.tij(arestas[i].getU().getRect(), arestas[i].getV().getRect()) != 1.0000000 )
                    flag = true;
            }
            if( flag ) {
                PRISMPoint[] pontos = Util.stressMajorization(arestas, points);
                if( pontos != null ) {
                    for( int i = 0; i < pontos.length; ++i ) {
                        points[i] = pontos[i];
                        projected.get(i).setUX(pontos[i].getRect().getCenterX()-pontos[i].getRect().getWidth()/2.);
                        projected.get(i).setUY(pontos[i].getRect().getCenterY()-pontos[i].getRect().getHeight()/2.);
                    }
//                    System.out.println("Novos pontos:");
//                    for( int i = 0; i < points.length; ++i ) {
//                        System.out.println(points[i].getRect().getCenterX()+", "+points[i].getRect().getCenterY());
//                    }
                    if( Util.getFinished() )                         
                        break;                    
                }                
            } else
                break;
            
        } while( --maxIterations > 0 );
        
        if( maxIterations != 0 ) 
            return projected;
        
        return null;
    }
    
    public static ArrayList<Retangulo> apply(ArrayList<Retangulo> rects) {
        ArrayList<Retangulo> firstPass = apply(rects, false);
        ArrayList<Retangulo> secondPass = apply(firstPass, true);
        return secondPass;
    }
       
   
    public static void main(String... args) {
        
        ArrayList<Retangulo> projected = new ArrayList<>();
        projected.add(new Retangulo(2, 10, 1, 1)); // 0
        projected.add(new Retangulo(4, 8, 3, 5)); // 1
        projected.add(new Retangulo(8, 10, 1, 1)); // 2
        projected.add(new Retangulo(6, 7, 9, 2)); // 3
        projected.add(new Retangulo(10, 4, 1, 1)); // 4
        projected.add(new Retangulo(8, 1, 1, 1)); // 5
        projected.add(new Retangulo(5, 2, 1, 1)); // 6
        projected.add(new Retangulo(2, 1, 1, 1)); // 7
        projected.add(new Retangulo(0, 4, 1, 1)); // 8
        projected.add(new Retangulo(0, 7, 1, 1)); // 9
        projected.add(new Retangulo(5, 5, 1, 1)); // 10

        ArrayList<Retangulo> reprojected = apply(projected);
        System.out.println("REPROJECTED ELEMENTS: ");
        for( Retangulo r: reprojected ) {
            System.out.println((r.getUX())+" "+(r.getUY()));
        }
//        PRISMEdge[] e = findRestOverlaps(projected);
//        for( PRISMEdge edge: e ) {
//            System.out.println(edge.getU()+" "+edge.getV());
//        }
    }   
    
    
    
}

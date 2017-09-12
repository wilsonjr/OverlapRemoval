/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.prism;

import br.com.methods.overlap.OverlapRemoval;
import br.com.methods.overlap.vpsc.Constraint;
import br.com.methods.overlap.vpsc.VPSC;
import br.com.methods.overlap.vpsc.Variable;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Triangle_dt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;


/**
 *
 * @author wilson
 */
public class PRISM implements OverlapRemoval {
    
    private int structure;
    
    /**
     * @param structure Estrutura de dados utilizada para armazenar a matriz:
     *                  0 - Matriz normal;
     *                  1 - Estrutura de Yale para matrizes esparsas.
     */
    public PRISM(int structure) {
        this.structure = structure;
    }
    
    /**
     * O método PRISM propõe utilizar a rotina do método VPSC para encontrar restrições de sobreposição.
     * Assim, as sobreposições "escondidas" são encontradas, e adiciona-se arestas para esses vértices sobrepostos.
     * @param retangulos Composição atual dos retângulos na projeção.
     * @return Array de arestas que precisam ser adicionadas.
     */
    private static PRISMEdge[] findRestOverlaps(ArrayList<OverlapRect> retangulos) {
        /**
         * copy rectangles 
         */         
        ArrayList<OverlapRect> projected = new ArrayList<>();
        for( OverlapRect r: retangulos )  
            projected.add(new OverlapRect(r.getUX(), r.getUY(), r.getWidth(), r.getHeight()));
        
        /**
         *  init all variables
         */         
        ArrayList<Variable> varsx = new ArrayList<>();
        ArrayList<Variable> varsy = new ArrayList<>();
        for( int i = 0; i < projected.size(); ++i ) {
            varsx.add(new Variable(i));
            varsy.add(new Variable(i));
        }
        
        /**
         * generate horizontal constraints and solve vpsc for x
         */
        ArrayList<Constraint> restricoesx = new ArrayList<>();
        ArrayList<Constraint> restricoesy = new ArrayList<>();
        VPSC.generateCx(projected, varsx, restricoesx);
        VPSC.generateCy(projected, varsy, restricoesy);        
        
        // aumenta o grafo de proximidade com as arestas encontradas pelo VPSC
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
    
    /**
     * Aplica o método PRISM
     * @param rects Composição dos retângulos na projeção multidimensional.
     * @param augmentGp Indicador que controla a adição de arestas encontradas pela rotina do VPSC.
     * @param algorithm Estrutura de dados utilizada para armazenar a matriz:
     *                  0 - Matriz normal;
     *                  1 - Estrutura de Yale para matrizes esparsas.
     * @return Retângulos sem sobreposição.
     */
    private static List<OverlapRect> apply(List<OverlapRect> rects, boolean augmentGp, int algorithm) {
        ArrayList<OverlapRect> projected = new ArrayList<>();
        
        for( int i = 0; i < rects.size(); ++i )
            projected.add(new OverlapRect(rects.get(i).getUX(), rects.get(i).getUY(), rects.get(i).getWidth(), rects.get(i).getHeight()));
                
        // form a proximity graph Gp of x0 by Delaunay triangulation        
        PRISMPoint[] points = new PRISMPoint[projected.size()];
       
        int maxIterations = 10000;
        
        do {
            for( int i = 0; i < projected.size(); ++i )
                points[i] = new PRISMPoint(projected.get(i).getCenterX(), projected.get(i).getCenterY(), projected.get(i), i);
            
            System.out.println("Applying Triangulation");
            
            ArrayList<PRISMEdge> edges = new ArrayList<>();
            if( rects.size() > 2 ) {
                Delaunay_Triangulation dt = new Delaunay_Triangulation(points);
                Iterator<Triangle_dt> trianglesIterator = dt.trianglesIterator();
                
                while( trianglesIterator.hasNext() ) {
                    Triangle_dt t = trianglesIterator.next();
                    if( !t.isHalfplane() ) {            

                        // adiciona as arestas de cada triangulo ao grafo de proximidade
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
            }
            
            // na segunda parte do algoritmo deve-se adicionar as arestas encontradas pelo VPSC
            if( augmentGp ) {
                
                System.out.println("Augmenting Proximity Graph");
                
                PRISMEdge[] restEdge = findRestOverlaps(projected);
                for( int i = 0; i < restEdge.length; ++i ) 
                    if( !edges.contains(restEdge[i]) ) 
                        edges.add(restEdge[i]);
            }
            
            PRISMEdge[] arestas = new PRISMEdge[edges.size()];
            boolean flag = false;
            for( int i = 0; i < edges.size(); ++i ) {
                arestas[i] = edges.get(i);                
                if( Util.tij(arestas[i].getU().getRect(), arestas[i].getV().getRect()) != 1.0000000 )
                    flag = true;
            }
            
            if( flag ) {
                // chama stress majorization somente se há sobreposição entre dois nós
                System.out.println("Stress Majorization");
                PRISMPoint[] pontos;
                if( algorithm == 0 )
                    pontos = Util.stressMajorization(arestas, points);
                else 
                    pontos = Util.stressMajorizationYale(arestas, points);
                
                if( pontos != null ) {
                    for( int i = 0; i < pontos.length; ++i ) {
                        points[i] = pontos[i];
                        projected.get(i).setUX(pontos[i].getRect().getCenterX()-pontos[i].getRect().getWidth()/2.);
                        projected.get(i).setUY(pontos[i].getRect().getCenterY()-pontos[i].getRect().getHeight()/2.);
                    }
                    
                    if( Util.getFinished() )                         
                        break;                    
                }               
            } else
                break;
            
            System.out.println("Finished 'Iteration' number "+maxIterations);
            
        } while( --maxIterations > 0 );
        
        return projected;
    }
    
    /**
     * Calcula a quantidade de sobreposição em relação ao eixo X
     * @param s Retângulo referência
     * @param r Retângulo com qual irá se comparar
     * @return Sobreposição no eixo X
     */
    private static double olapx(OverlapRect s, OverlapRect r) {
        return (s.getWidth()+r.getWidth())/2 - Math.abs(s.getCenterX()-r.getCenterX());
    }
    
    /**
     * Calcula a quantidade de sobreposição em relação ao eixo Y
     * @param s Retângulo referência
     * @param r Retângulo com qual irá se comparar
     * @return Sobreposição no eixo Y
     */
    private static double olapy(OverlapRect s, OverlapRect r) {
        return (s.getHeight()+r.getHeight())/2 - Math.abs(s.getCenterY()-r.getCenterY());
    }
    
    /**
     * Método utilizado quando existe somente 2 retângulos (não é possível formar uma triangulação).
     * @param rects Os 2 retângulos
     * @return 2 retângulos não sobrepostos.
     */
    private static ArrayList<OverlapRect> naivePRISM(List<OverlapRect> rects) {
        
         ArrayList<OverlapRect> projected = new ArrayList<>();
        
        for( int i = 0; i < rects.size(); ++i )
            projected.add(new OverlapRect(rects.get(i).getUX(), rects.get(i).getUY(), rects.get(i).getWidth(), rects.get(i).getHeight()));
        
        double x = Math.abs(olapx(projected.get(0), projected.get(1)));
        double y = Math.abs(olapy(projected.get(0), projected.get(1)));        
        
        if( x < y )
            projected.get(0).setUX(projected.get(0).getUX()-x);
        else
            projected.get(0).setUY(projected.get(0).getUY()-y);
        return projected;
    }
    
    /**
     * Rotina principal do método.
     * Verifica as "condições" do conjunto e realiza as "passadas" no método PRISM.
     * @param rects Projeção original
     * @return Retângulos sem sobreposição.
     */
    @Override
    public Map<OverlapRect, OverlapRect> apply(List<OverlapRect> rects) {
        Map<OverlapRect, OverlapRect> projectedToReprojected = new HashMap<>();

        // para um nó apenas não há o que fazer
        if( rects.size() <= 1 ) {
            IntStream.range(0, rects.size()).forEach(i->projectedToReprojected.put(rects.get(i), rects.get(i)));
            return projectedToReprojected;
        }
        // para dois nós a remoção é trivial
        if( rects.size() == 2 ) {
            ArrayList<OverlapRect> naiveReprojected = naivePRISM(rects);
            IntStream.range(0, rects.size()).forEach(
                i->projectedToReprojected.put(rects.get(i), naiveReprojected.get(i))
            );
            return projectedToReprojected;
        }
        // remove a sobreposição por meio de uma visão local
        List<OverlapRect> firstPass = apply(rects, false, structure);
        
        // remove o restante da sobreposição por meio de uma visão global
        List<OverlapRect> secondPass = apply(firstPass, true, structure);
        
        IntStream.range(0, rects.size()).forEach(i->projectedToReprojected.put(rects.get(i), secondPass.get(i)));
        
        return projectedToReprojected;
    }
    
    
    @Override
    public String toString() {
        return "PRISM";
    }
}

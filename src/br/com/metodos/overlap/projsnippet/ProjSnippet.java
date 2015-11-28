/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.projsnippet;

import br.com.grafos.ui.Menu;
import br.com.metodos.utils.Retangulo;
import br.com.metodos.utils.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.thiele.mllib.classifiers.NearestNeighbour;
import nu.thiele.mllib.data.Data;



/**
 *
 * @author wilson
 */
public class ProjSnippet {
    
    
     public static ArrayList<Retangulo> e_o(ArrayList<Retangulo> retangulos, double alpha) {
        
        double[][] l = formGraph(retangulos);
        ArrayList<Retangulo> projected = new ArrayList<>();        
        try {
            File file = new File("points.rect");        
            if( !file.exists() )
                file.createNewFile();
            
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            try( BufferedWriter bw = new BufferedWriter(fw) ) {
                bw.write(retangulos.size()+"\n");
                for( Retangulo r: retangulos )
                    bw.write(r.getUX()+" "+r.getLY()+" "+r.getWidth()+" "+r.getHeight()+"\n");
                bw.write("0.5\n");
                bw.write(String.valueOf(alpha));
            }
            
            File fileMat = new File("matrixL.matrix");
            if( !fileMat.exists() )  
                fileMat.createNewFile();
            
            fw = new FileWriter(fileMat.getAbsoluteFile());
            try( BufferedWriter bw = new BufferedWriter(fw) ) {
                bw.write(l.length+"\n");
                for( int i = 0; i < l.length; ++i ) {
                    for( int j = 0; j < l.length; ++j ) 
                        bw.write(l[i][j]+" ");
                    bw.write("\n");
                }
            }
            
            
            
            System.out.println("Chamando rotina C++");
            
            Process p = null;
            try {
                //p = Runtime.getRuntime().exec("C:\\Python27\\python.exe teste_minimization.py");
                //p = Runtime.getRuntime().exec("cmd /c teste_nlopt.exe");
                p = Runtime.getRuntime().exec("cmd /c energia.exe "+alpha);
            } catch (IOException ex) {
                Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            int waitFor = 1;
            try {  
                System.out.println("Esperando rotina C++");
                waitFor = p.waitFor();
                System.out.println(waitFor);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProjSnippet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println("Recuperando resultados...");
            if( waitFor == 0 ) {
                
                Scanner scn = new Scanner(new File("point_solve.rect"));
                int idx = 0;
                if( scn.hasNext() ) {
                    scn.nextInt();
                    while( scn.hasNext() ) {
                    
                        double ux = Double.parseDouble(scn.next());
                        if( scn.hasNext() ) {
                            double ly = Double.parseDouble(scn.next());
                            System.out.println(ux+" <<-->> "+ly);
                            double uy = ly-retangulos.get(idx).getHeight();
                            projected.add(new Retangulo(ux, uy, retangulos.get(idx).getWidth(), retangulos.get(idx).getHeight()));
                            scn.next(); scn.next();
                        }
                    }
                }
                System.out.println("size: "+projected.size());
                return projected;
            } 
            
            
            
            
        } catch( IOException e ) {
            
        }
        
        
        
        return null;
       
     }
     
     
     public static double[][] formGraph(ArrayList<Retangulo> retangulos) {
         ArrayList<Data.DataEntry> data = new ArrayList<>();
         for( Retangulo r: retangulos )
             data.add(new Data.DataEntry(new double[]{r.getUX(), r.getLY()}, (long)r.getId()));
         Vertice[] grafo = new Vertice[retangulos.size()];
         
         for (int i = 0; i < data.size(); ++i) {
            Data.DataEntry[] e = NearestNeighbour.getKNearestNeighbours(data, data.get(i).getX(), 5);
            Vertice v = new Vertice(i);
            System.out.println("i: "+i);
            for( int j = 0; j < e.length; ++j ) {
                System.out.println(e[j].getX()[0]+" "+e[j].getX()[1]+" "+e[j].getY());
                if( v.getId() != (long)e[j].getY() )
                    v.add(new Edge(i, (Long)e[j].getY(), Util.distanciaEuclideana(data.get(i).getX()[0], 
                                                                                  data.get(i).getX()[1], 
                                                                                  e[j].getX()[0],
                                                                                  e[j].getX()[1])));
            }
            System.out.println();
            grafo[i] = v;
            
         }
         
         findComponents(grafo);
         
         Vertice[] c = completedGraph(retangulos);
         Prim p = new Prim();
         p.execute(c);
         ArrayList<Edge> edges = p.getEdges();
         
         // aumenta o grafo
         for( Edge e: edges ) {
             int i = (int)e.getU();
             int j = (int)e.getV();
             if( grafo[i].getComponente() != grafo[j].getComponente() ) {
                 grafo[i].add(new Edge(i, j,
                         Util.distanciaEuclideana(retangulos.get(i).getUX(), retangulos.get(i).getLY(), 
                                                  retangulos.get(j).getUX(), retangulos.get(j).getLY())));
                 
             }
         }
         
         for( Vertice v: grafo )
             System.out.println(v);
         
         
         double[][] l = new double[grafo.length][grafo.length];
         for( Vertice v: grafo ) {
             for( Edge e: v.getAdj() ) 
                 l[(int)e.getU()][(int)e.getV()] = -1.0/((double)v.getAdj().size());
         }
         for( int i = 0; i < l.length; ++i ) {
             for( int j = 0; j < l.length; ++j )
                 l[i][i] = 0.0;
             l[i][i] = 1.0;
         }
             
         
         return l;         
     }
     
     public static Vertice[] completedGraph(ArrayList<Retangulo> retangulos) {
         Vertice[] grafo = new Vertice[retangulos.size()];
         
         for( int i = 0; i < grafo.length; ++i ) {
             Vertice v = new Vertice(i);
             for( int j = 0; j < grafo.length; ++j ) {
                 if( i == j )
                     continue;
                 
                 v.add(new Edge(i, j, Util.distanciaEuclideana(retangulos.get(i).getUX(), 
                                                               retangulos.get(i).getLY(), 
                                                               retangulos.get(j).getUX(), 
                                                               retangulos.get(j).getLY())));
             }
             grafo[i] = v;
         }
         
         return grafo;
     }
              
     private static void visita(long i, Vertice[] grafo, int comp) {
         grafo[(int)i].setVisitado(true);
         grafo[(int)i].setComponente(comp);
         
         
         for( Edge e: grafo[(int)i].getAdj() )
             if( !grafo[(int)e.getV()].isVisitado() ) {
                 visita(e.getV(), grafo, comp);
             }
     }
             
     public static void findComponents(Vertice[] grafo) {
         for( int i = 0; i < grafo.length; ++i ) {
             grafo[i].setVisitado(false);
             grafo[i].setComponente(-1);
         }
         
         int comp = 0;
         for( int i = 0; i < grafo.length; ++i )
             if( !grafo[i].isVisitado() ) {
                 visita(i, grafo, comp);
                 comp++;
             }         
     }
     
     
     
     
     public static void main(String[] args) {
         System.out.println("ola");
     }

}

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
 * Classe que prepara os elementos paraexecutar o método ProjSnippet
 * @author wilson
 */
public class ProjSnippet {
     private static int resultado;
     private static int kNeighbours;
     
     /**
      * Forma o grafo e elementos necessário para execução do algoritmo ProjSnippet.
      * Este algoritmo não está implementado em Java, mas sim em C++. 
      * A escolha foi feita para usar a mesma biblioteca que o autor do método.
      * @param retangulos Projeção inicial 
      * @param alpha Parâmetro que controla a contribuição das energias de sobreposição e vizinhança
      * @param kNeighbours Número de vizinhos mais próximas para formação do grafo e, consequentemente, a matriz L.
      * @return 
      */
     public static ArrayList<Retangulo> apply(ArrayList<Retangulo> retangulos, double alpha, int kNeighbours) {
        ProjSnippet.resultado = -1;
        ProjSnippet.kNeighbours = kNeighbours;
        
        double[][] l = formGraph(retangulos);
        ArrayList<Retangulo> projected = new ArrayList<>();        
        
        try {
            File file = new File("points.rect");        
            if( !file.exists() )
                file.createNewFile();
 
            /**
             *  salva as coordenadas e as dimensões dos retângulos
             */
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            try( BufferedWriter bw = new BufferedWriter(fw) ) {
                bw.write(retangulos.size()+"\n");
                for( Retangulo r: retangulos )
                    bw.write(r.getUX()+" "+r.getLY()+" "+r.getWidth()+" "+r.getHeight()+"\n");
                bw.write("1\n"); // w inicial
                bw.write(String.valueOf(alpha));
            }
            
            /**
             * salva a matriz usada para a energia de vizinhança
             */
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
            
            try {
                final Process p = Runtime.getRuntime().exec("cmd /c energia.exe");
                System.out.println("Esperando rotina C++");
                new Runnable() {

                    @Override
                    public void run() {
                        try {  
                            resultado = p.waitFor();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ProjSnippet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }.run();
               
            } catch (IOException ex) {
                Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println("Recuperando resultados...");
            if( resultado == 0 ) {
                Scanner scn = new Scanner(new File("point_solve.rect"));
                int idx = 0;
                
                if( scn.hasNext() ) {
                    scn.nextInt();                
                    while( scn.hasNext() ) {
                    
                        double ux = Double.parseDouble(scn.next());
                        if( scn.hasNext() ) {
                            double ly = Double.parseDouble(scn.next());
                            double uy = ly-retangulos.get(idx).getHeight();
                            projected.add(new Retangulo(ux, uy, retangulos.get(idx).getWidth(), retangulos.get(idx).getHeight()));
                            scn.next(); scn.next();
                            idx++;
                        }
                    }                    
                }
                return projected;
            } 
            
        } catch( IOException e ) {
            
        }
        
        return null;       
     }
     
     /**
      * Forma o grafo de vizinhos mais próximos para gerar a matriz L usada na energia de vizinhança
      * @param retangulos Projeção inicial
      * @return Matriz L
      */
     private static double[][] formGraph(ArrayList<Retangulo> retangulos) {
         // "converte" os retângulos para usar a biblioteca kNN
         ArrayList<Data.DataEntry> data = new ArrayList<>();
         for( Retangulo r: retangulos )
             data.add(new Data.DataEntry(new double[]{r.getUX(), r.getLY()}, (long)r.getId()));
         
         
         // Para cada vértice, encontra seus vizinhos mais próximos
         Vertice[] grafo = new Vertice[retangulos.size()];
         for (int i = 0; i < data.size(); ++i) {
            Data.DataEntry[] e = NearestNeighbour.getKNearestNeighbours(data, data.get(i).getX(), 
                    ProjSnippet.kNeighbours > retangulos.size() ? retangulos.size() : ProjSnippet.kNeighbours);
            
            // forma o grafo de acordo com os vizinhos mais próximos
            Vertice v = new Vertice(i);
            for( int j = 0; j < e.length; ++j ) {
                if( v.getId() != (long)e[j].getY() )
                    v.add(new Edge(i, (Long)e[j].getY(), Util.distanciaEuclideana(data.get(i).getX()[0], 
                                                                                  data.get(i).getX()[1], 
                                                                                  e[j].getX()[0],
                                                                                  e[j].getX()[1])));
            }
            grafo[i] = v;
         }         
         
         // encontra as componentes resultante do kNN
         findComponents(grafo);
         
         // "aumenta" o número de arestas ligando possíveis componentes desconectadas
         Vertice[] c = completedGraph(retangulos);
         
         // encontra a árvore geradora mínima
         Prim p = new Prim();
         p.execute(c);
         ArrayList<Edge> edges = p.getEdges();
         
         // utiliza a menor aresta ligando os componentes para aumentar o grafo original
         for( Edge e: edges ) {
             int i = (int)e.getU();
             int j = (int)e.getV();
             if( grafo[i].getComponente() != grafo[j].getComponente() ) {
                 grafo[i].add(new Edge(i, j,
                         Util.distanciaEuclideana(retangulos.get(i).getUX(), retangulos.get(i).getLY(), 
                                                  retangulos.get(j).getUX(), retangulos.get(j).getLY())));
                 
             }
         }
         
         for( Vertice v: grafo ) {
             for( Edge e: v.getAdj() ) {
                 if( !grafo[(int)e.getV()].has(e.getU()) )
                     grafo[(int)e.getV()].add(new Edge(e.getV(), e.getU(), e.getPeso()));
             }
         }
                  
         /** cria a matriz L, definida da seguinte forma:
          * L_ii = 1
          * L_ij = -1/|i|, se ij é uma aresta no grafo
          * L_ij = 0, caso contrário
          * onde |i| é a valência do nó i.
          **/
         double[][] l = new double[grafo.length][grafo.length];
         for( Vertice v: grafo ) {
             for( Edge e: v.getAdj() ) 
                 l[(int)e.getU()][(int)e.getV()] = -1.0/((double)v.getAdj().size()+2);             
         }                  
         for( int i = 0; i < l.length; ++i ) 
             l[i][i] = 1.0;
         
         return l;         
     }
     
     /**
      * Cria um grafo completo
      * @param retangulos Projeção inicial
      * @return Grafo completo sem loop
      */
     private static Vertice[] completedGraph(ArrayList<Retangulo> retangulos) {
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
             
     /**
      * Método principal de busca de profundidade
      * @param i Vértice corrente na busca em profundidade
      * @param grafo Grafo em lista de adjacências
      * @param comp Componentes ao qual o vértice pertence
      */
     private static void visita(long i, Vertice[] grafo, int comp) {
         grafo[(int)i].setVisitado(true);
         grafo[(int)i].setComponente(comp);
         
         
         for( Edge e: grafo[(int)i].getAdj() )
             if( !grafo[(int)e.getV()].isVisitado() ) {
                 visita(e.getV(), grafo, comp);
             }
     }
            
     /**
      * Busca em profundidade para encontrar componentes do grafo.
      * Necessário para conectar os componentes desconectados após encontrar os 'k-Neighbours'
      * @param grafo Grafo em lista de adjacências
      */
     private static void findComponents(Vertice[] grafo) {
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
}

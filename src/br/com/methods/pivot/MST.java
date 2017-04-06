/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.pivot;

import br.com.methods.pivot.MST.Kruskal.EDGE;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wilson
 */
public class MST {
    
    private ArrayList<OverlapRect> C;
    private int[] vertices;
    
    public void selectPivots(ArrayList<OverlapRect> conjunto, int qtdPagina) {
        this.C = conjunto;
        vertices = new int[C.size()];
        
        for( int i = 0; i < vertices.length; ++i ) {
            
            vertices[i] = i;
        }
         
        ArrayList<OverlapRect> representativos = new ArrayList<>();
        ArrayList<ArrayList<OverlapRect>> grupos = new ArrayList<>();
        
        C.get(0).setPivot(true);
        C.get(0).setCluster(0);
        
        representativos.add(C.get(0));
        grupos.add(new ArrayList<OverlapRect>());
        grupos.get(0).add(C.get(0));
        
        for( int i = 1; i < C.size(); ++i ) {
            
            int[] idx = findNearest(representativos, C.get(i));
            idx[0]=idx[1];
            if( grupos.get(idx[0]).size() >= qtdPagina ) {
                C.get(i).setHeight(idx[0]);
                C.get(i).setCluster(idx[0]);
                grupos.get(idx[0]).add(C.get(i));
                promoteRepresentative(representativos, grupos, idx[0], idx[1]);

            } else {
                C.get(i).setHeight(idx[0]);
                C.get(i).setCluster(idx[0]);
                grupos.get(idx[0]).add(C.get(i));
            }
            
        }
        
        int count = 0;
        for( int i = 0; i < representativos.size();++i)
            if( representativos.get(i).isPivot()  )
                count++;
        
        for( ArrayList<OverlapRect> r: grupos )
            System.out.println("size: "+r.size());
        
        System.out.println("Quantidade de representativos: "+count);
    }

    private int[] findNearest(ArrayList<OverlapRect> representativos, OverlapRect r) {
        int idx = -1;
        double menor = Double.MAX_VALUE;
        
        for( int i = 0; i < representativos.size(); ++i ) {
            if( representativos.get(i).isPivot() ) {
                double d = Util.euclideanDistance(representativos.get(i).getCenterX(), representativos.get(i).getCenterY(), 
                                                     r.getCenterX(), r.getCenterY());
                if( menor > d ) {
                    menor = d;
                    idx = i;
                }
            } 
        }
        
        return  new int[]{representativos.get(idx).getCluster(), idx};
    }

    private void promoteRepresentative(ArrayList<OverlapRect> representativos, ArrayList<ArrayList<OverlapRect>> grupos, int idx, int idx2) {
        representativos.get(idx2).setPivot(false);
        representativos.get(idx2).setHeight(idx);
        
        ArrayList<EDGE> edges = new ArrayList<>();
        for( int i = 0; i < grupos.get(idx).size(); ++i )
            for( int j = 0; j < grupos.get(idx).size(); ++j ) {
                
                    edges.add(new EDGE(grupos.get(idx).get(i).getId(), grupos.get(idx).get(j).getId(), 
                                       Util.euclideanDistance(grupos.get(idx).get(i).getCenterX(), 
                                                                grupos.get(idx).get(i).getCenterY(), 
                                                                grupos.get(idx).get(j).getCenterX(), 
                                                                grupos.get(idx).get(j).getCenterY())));
                
            }
        //System.out.println("Quantidade de edges: "+edges.size());
        EDGE[] edgesV = new EDGE[edges.size()];
        edges.toArray(edgesV);
        
        ArrayList<EDGE> mst = Kruskal.kruskal(vertices, edgesV);
        
        int[] components = deleteGreatestEdge(mst);
        
        if( components != null ) {
            representativos.remove(idx2);
            grupos.remove(idx);
            selectRepresentive(components, representativos, grupos, 1);
            selectRepresentive(components, representativos, grupos, 2);
        } else
            System.out.println("NAO ACHAMOS A MAIOR...");
                
    }

    private int[] deleteGreatestEdge(ArrayList<EDGE> mst) {
        int idx = -1;
        double maiorPeso = Double.MIN_VALUE;
        
        for( int i = 0; i < mst.size(); ++i ) 
            if( maiorPeso < mst.get(i).weight ) {
                maiorPeso = mst.get(i).weight;
                idx = i;
            }
        
        if( idx != -1 ) {
            int uu = (int) mst.get(idx).from;
            int vv = (int) mst.get(idx).to;
            
            int maxIdx = vertices.length;
            
            mst.remove(idx);
            
            int[] elems = new int[maxIdx];
            for( int i = 0; i < elems.length; ++i )
                elems[i] = -1;
            
            elems[(int)mst.get(0).from] = elems[(int)mst.get(0).to] = 1;
            
            for( int i = 1; i < mst.size(); ++i ) {
                int u = (int) mst.get(i).from;
                int v = (int) mst.get(i).to;
                
                if( elems[u] == -1 && elems[v] != -1 ) 
                    elems[u] = elems[v];
                else if( elems[u] != -1 && elems[v] == -1 )
                    elems[v] = elems[u];
                else if( elems[u] == -1 && elems[v] == -1 )
                    elems[v] = elems[u] = 2;
            }
            
            elems[uu] = elems[uu] == -1 ? 2 : elems[uu];
            elems[vv] = elems[vv] == -1 ? 2 : elems[vv];
                
            
            return elems;
        }
            
        return null;
    }

    private void selectRepresentive(int[] components, ArrayList<OverlapRect> representativos, 
                                    ArrayList<ArrayList<OverlapRect>> grupos, int key) {
        
        double xmin = Double.MAX_VALUE, xmax = Double.MIN_VALUE, 
               ymin = Double.MAX_VALUE, ymax = Double.MIN_VALUE;   
        
        for( int i = 0; i < components.length; ++i ) {
            if( components[i] == key ) {
                if( C.get(i).getCenterX() < xmin )
                    xmin = C.get(i).getCenterX();
                if( C.get(i).getCenterY() < ymin )
                    ymin = C.get(i).getCenterY();
                if( C.get(i).getCenterX() > xmax )
                    xmax = C.get(i).getCenterX();
                if( C.get(i).getCenterY() > ymax )
                    ymax = C.get(i).getCenterY();
            }
        }
        
        
        /**
         * Compute the center of mass of set 
         */        
        double centerX = (xmin+xmax)/2;
        double centerY = (ymin+ymax)/2;
        
        int idx = -1;
        double dist = Double.MAX_VALUE;
        
        for( int i = 0; i < components.length; ++i ) {
            if( components[i] == key ) {
                double d = Util.euclideanDistance(centerX, centerY, C.get(i).getCenterX(), C.get(i).getCenterY());
                if( dist > d ) {
                    dist = d;
                    idx = i;
                }
            }
        }
        
        //System.out.println("mais central : "+idx+" ("+key+")");
        C.get(idx).setPivot(true);        
        C.get(idx).setCluster(grupos.size());
        representativos.add(C.get(idx));
        grupos.add(new ArrayList<OverlapRect>());
        
        
        for( int i = 0; i < components.length; ++i )
            if( components[i] == key ) {
                C.get(i).setHeight(grupos.size()-1);
                C.get(i).setCluster(grupos.size()-1);
                grupos.get(grupos.size()-1).add(C.get(i));
            }
    }
    
    
    public static class Kruskal {
        static class EDGE implements Comparable<EDGE>{
            int from, to;
            double weight;
            EDGE(int f, int t, double w){
                from = f;
                to = t;
                weight = w; 
            }  

            @Override
            public int compareTo(EDGE o) {
                return weight<o.weight?-1:(weight>o.weight?1:0);
            } 

            @Override
            public String toString(){
                return "["+from+", "+to+"]";
            }
        }

        private static Map<Integer, Integer> PARENT;
        private static Map<Integer, Integer> RANKS; //to store the depths

        public static void initialize(int[] universe){ 
            PARENT = new HashMap<>();
            RANKS = new HashMap<>();
            for(int x:universe){
                PARENT.put(x, x);
                RANKS.put(x, 1);
            } 
        }

        public static int FindSet(int item){
            int parent = PARENT.get(item); 
            if(parent==item)return item;
            else return FindSet(parent);
        }

        public static void Union(int setA, int setB){
            int pA, pB;
            while((pA = PARENT.get(setA))!=setA){setA = pA;}
            while((pB = PARENT.get(setB))!=setB){setB = pB;}

            int rankFirst = RANKS.get(setA), rankSecond = RANKS.get(setB);
            if(rankFirst>rankSecond){
                PARENT.put(setB, setA);  
                updateRanksUpward(setB);
            }else if(rankSecond>rankFirst){
                PARENT.put(setA, setB);  
                updateRanksUpward(setA);
            }else{
                PARENT.put(setB, setA); 
                updateRanksUpward(setB);
            }
        }

        public static void updateRanksUpward(int current){
            int currentDepth = RANKS.get(current);
            int currentsParent = PARENT.get(current);
            int parentsDepth = RANKS.get(currentsParent);
            if(!(currentDepth<parentsDepth || currentsParent == current)){ 
                RANKS.put(currentsParent, currentDepth+1);
                updateRanksUpward(currentsParent);
            }
        } 

        //CLSR p631 Algorithm
        public static ArrayList<EDGE> kruskal(int[] vertices, EDGE[] edges){  
                //Initialize A = empty set
                ArrayList<EDGE> mst = new ArrayList<>();

                //for each vertex v belongs to G.V MAKE-SET(v)
                initialize(vertices);

                //sort the edges of G.E into non decreasing order by weight w
                Arrays.sort(edges);

                //For each edge (u,v) belongs to G.E taken in non decreasing order by weight
                for(EDGE edge:edges){
                    //If (find-set(u)!=find-set(v)
                    if(FindSet(edge.from)!=FindSet(edge.to)){
                        //A = A union (u, v)
                        mst.add(edge);
                        //UNION(u, v)
                        Union(edge.from, edge.to);
                    }
                }             
                //Display contents
               // System.out.println("MST contains the edges: "+mst);
                return mst;
        }
    }
    
    
    
    
    

   
    
}

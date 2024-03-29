/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering.furs;

import br.com.methods.utils.KNN;
import br.com.methods.utils.Pair;
import br.com.methods.utils.Vect;
import br.com.representative.clustering.Partitioning;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Windows
 */
public class FURS extends Partitioning {
    private int k;
    private int s;
    private float deltaS;
    private float deltaK;

    public FURS(List<Vect> items, int s, int k, float deltaS, float deltaK) {
        super(items);
        this.s = s;
        this.k = k;
        this.deltaK = deltaK;
        this.deltaS = deltaS;
    }
    
    @Override
    public void execute() {
        
        KNN knn = new KNN(k);
        Pair[][] knnPoints = knn.executeVect(items);
        
        List<Node> nodes = new ArrayList<>();
        for( int i = 0; i < knnPoints.length; ++i ) 
            nodes.add(new Node(i));
        for( int i = 0; i < knnPoints.length; ++i ) {
            for( int j = 0; j < knnPoints[i].length; ++j )
                if( i != knnPoints[i][j].index )
                    nodes.get(i).add(nodes.get(knnPoints[i][j].index), knnPoints[i][j].value);
            
            for( int j = 0; j < knnPoints.length; ++j )
                for( int k = 0; k < knnPoints[j].length; ++k )
                    if( i == knnPoints[j][k].index )
                        nodes.get(i).addAdjIn(nodes.get(knnPoints[j][k].index));
        }
        
        List<Node> L = new ArrayList<>(nodes);
        Collections.sort(L);
        
        List<Node> representative = new ArrayList<>();
        
        while( representative.size() < s ) {            
            // reactivation step
            if( L.isEmpty() ) {     
               nodes.stream().filter((n)-> !n.state() && notInRepresentative(n, representative) ).forEachOrdered((n)->{
                   n.activate();
                   L.add(n);
               });
               Collections.sort(L);
            }
            
            // hub selection
            Node v = L.remove(0);
            v.deactivate();
            representative.add(v);              
            List<Node> Nb = v.neighbors();
            Nb.stream().forEach((n)->{ 
                n.deactivate(); 
                L.remove(n);
            });            
        }
        representative.stream().forEach((v)->System.out.println(">> "+v.getId()));
        representatives = representative.stream().mapToInt((n)->n.getId()).toArray();
    }
    
    
    public int[] execute(List<Vect> items, int k, int s) {
        
        KNN knn = new KNN(k);
        Pair[][] knnPoints = knn.executeVect(items);
        
        List<Node> nodes = new ArrayList<>();
        for( int i = 0; i < knnPoints.length; ++i ) 
            nodes.add(new Node(i));
        
        for( int i = 0; i < knnPoints.length; ++i ) {
            for( int j = 0; j < knnPoints[i].length; ++j )
                if( i != knnPoints[i][j].index )
                    nodes.get(i).add(nodes.get(knnPoints[i][j].index), knnPoints[i][j].value);
        }
        
        List<Node> L = new ArrayList<>(nodes);
        Collections.sort(L);
        
        List<Node> representative = new ArrayList<>();
        
        while( representative.size() < s ) {            
            // reactivation step
            if( L.isEmpty() ) {                 
               nodes.stream().filter((n)-> !n.state() ).forEachOrdered((n)->L.add(n));
               Collections.sort(L);
            }
            
            // hub selection
            Node v = L.remove(0);
            representative.add(v);          
            List<Node> Nb = v.neighbors();
            Nb.stream().forEach((n)->{ 
                n.deactivate(); 
                L.remove(n);
            });            
                    
        }

        int[] reps = representative.stream().mapToInt((n)->n.getId()).toArray();
        
        return reps;
    }
    
    @Override
    public void filterData(int[] indexes) {
        super.filterData(indexes);
        
        k = (int)(deltaK*k > 1 ? deltaK*k : 1);
        s = (int)(deltaS*s > 1 ? deltaS*s : 1);
    }

    public void setK(int k) {
        this.k = k;
    }

    private boolean notInRepresentative(Node n, List<Node> representative) {
        
        for( int i = 0; i < representative.size(); ++i )
            if( representative.get(i).getId() == n.getId() )
                return false;
        
        return true;
    }
    
}

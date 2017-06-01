/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering.furs;

import br.com.methods.utils.KNN;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Pair;
import br.com.representative.clustering.Partitioning;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Windows
 */
public class FURS extends Partitioning {
    private int k;
    private int s;

    public FURS(List<Point2D.Double> items, int s, int k) {
        super(items);
        this.s = s;
        this.k = k;
    }

    @Override
    public void execute() {
        
        
        List<OverlapRect> points = items.stream().map((v)->new OverlapRect(v.x, v.y, 0, 0)).collect(Collectors.toList());
        KNN knn = new KNN(k);
        Pair[][] knnPoints = knn.execute(points);
        
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

        representatives = representative.stream().mapToInt((n)->n.getId()).toArray();
    }
    
}
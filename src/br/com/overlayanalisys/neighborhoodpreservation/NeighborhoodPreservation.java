/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.overlayanalisys.neighborhoodpreservation;

import br.com.methods.utils.OverlapRect;
import br.com.overlayanalisys.definition.Metric;
import java.util.ArrayList;
import nu.thiele.mllib.classifiers.NearestNeighbour;
import nu.thiele.mllib.data.Data;

/**
 *
 * @author wilson
 */
public class NeighborhoodPreservation implements Metric {
    private int kNeighbours;
    
    public NeighborhoodPreservation(int kNeighbours) {
        this.kNeighbours = kNeighbours;
    }

    @Override
    public double execute(ArrayList<OverlapRect> pts1, ArrayList<OverlapRect> pts2) {
        if( pts1.size() != pts2.size() )
            throw new RuntimeException("A quantidade de elementos na projeção deve ser a mesma.");
        
        double taxa = 0;
        
        ArrayList<Data.DataEntry> dataPts1 = new ArrayList<>();
        for( OverlapRect r: pts1 )
            dataPts1.add(new Data.DataEntry(new double[]{r.getCenterX(), r.getCenterY()}, (long)r.getId()));
        
        ArrayList<Data.DataEntry> dataPts2 = new ArrayList<>();
        for( OverlapRect r: pts2 )
            dataPts2.add(new Data.DataEntry(new double[]{r.getCenterX(), r.getCenterY()}, (long)r.getId()));
        
        
        for( int i = 0; i < pts1.size(); ++i ) {
            Data.DataEntry[] nearPts1 = NearestNeighbour.getKNearestNeighbours(dataPts1, dataPts1.get(i).getX(), kNeighbours+1);
            Data.DataEntry[] nearPts2 = NearestNeighbour.getKNearestNeighbours(dataPts2, dataPts2.get(i).getX(), kNeighbours+1);
            
            double count = 0.0;
            for( int j = 0; j < nearPts1.length; ++j ) {
                if( i != (long)nearPts1[j].getY() ) {
                    
                    for( int k = 0; k < nearPts2.length; ++k )
                        if( (long)nearPts1[j].getY() == (long)nearPts2[k].getY() ) {
                            count++;
                        }
                }
            }
            System.out.println("porcentagem: "+(count/kNeighbours));
            
            taxa += (count/kNeighbours);
        }
        
        
        
        return taxa/pts1.size();
    }
    
}

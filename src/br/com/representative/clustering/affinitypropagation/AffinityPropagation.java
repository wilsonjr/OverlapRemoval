/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering.affinitypropagation;

import br.com.methods.utils.Util;
import br.com.representative.clustering.Partitioning;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Windows
 */
public class AffinityPropagation extends Partitioning {

    private double alpha;
    
    public AffinityPropagation(List<Point2D.Double> items) {
        super(items);
        alpha = 0.5;
    }

    @Override
    public void execute() {
        
        double[][] a = new double[items.size()][items.size()];
        double[][] r = new double[items.size()][items.size()];
        double[][] s = new double[items.size()][items.size()];
        List<Double> similatities = new ArrayList<>();
        
        for( int i = 0; i < s.length-1; ++i ) 
            for( int j = i+1; j < s.length; ++j ) {
                //s[i][j] = -((items.get(i).x-items.get(j).x)*(items.get(i).x-items.get(j).x)+(items.get(i).y-items.get(j).y)*(items.get(i).y-items.get(j).y));
                s[i][j] = -Util.euclideanDistance(items.get(i).x, items.get(i).y, items.get(j).x, items.get(j).y);
                s[j][i] = s[i][j];
                similatities.add(s[i][j]);
            }
        
        Collections.sort(similatities);
        int size = similatities.size();
        double median = size%2 == 0 ? (similatities.get(size/2)+similatities.get(size/2-1))/2 : similatities.get(size/2);
        System.out.println("median: "+median);
        for( int i = 0; i < s.length; ++i ) {
            s[i][i] = median;
            for( int j = 0; j < s.length; ++j ) {
                r[i][j] = 0;
                a[i][j] = 0;
            }
        }
        
        int maxIterations = 230;
        
        for( int iter = 0; iter < maxIterations; ++iter ) {
            
            // update responsabilities
            for( int i = 0; i < items.size(); ++i ) {                
                for( int k = 0; k < items.size(); ++k ) {
                    
                    double max = -1e100;
                    for( int j = 0; j < k; ++j ) 
                        if( a[i][j]+s[i][j] > max )
                            max = a[i][j]+s[i][j];               
                    for( int j = k+1; j < items.size(); ++j )
                        if( a[i][j]+s[i][j] > max )
                            max = a[i][j]+s[i][j];       
                    
                    r[i][k] = (1.0-alpha)*(s[i][k] - max) + alpha*r[i][k];
                    
                }
            }            
            // update availabilities
            for( int i = 0; i < items.size(); ++i ) {                
                for( int k = 0; k < items.size(); ++k ) {
                    
                    if( i == k ) { // update auto availabilities
                        double sum = 0;
                        for( int j = 0; j < k; ++j )
                            sum += Math.max(0, r[j][k]);
                        for( int j = k+1; j < items.size(); ++j )
                            sum += Math.max(0, r[j][k]);

                        a[k][k] = (1.0-alpha)*sum + alpha*a[k][k];
                        
                    } else {
                        
                        double sum = 0;                    
                        for( int j = 0; j < items.size(); ++j ) 
                            if( j != i && j != k )
                                sum += Math.max(0, r[j][k]);

                        a[i][k] = (1.0-alpha)*Math.min(0, r[k][k] + sum) + alpha*a[i][k];
                        
                    }
                }
            }             
        }
        
        List<Integer> indexes = new ArrayList<>();        
        for( int i = 0; i < items.size(); ++i ) {
            if( r[i][i]+a[i][i] > 0 ) 
                indexes.add(i);
        }
                
        representatives = indexes.stream().mapToInt((e)->e).toArray();        
    }
    
}

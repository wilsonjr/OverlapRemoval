/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering.affinitypropagation;

import br.com.methods.utils.Util;
import br.com.methods.utils.Vect;
import br.com.representative.clustering.Partitioning;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Windows
 */
public class AffinityPropagation extends Partitioning {

    private double alpha;
    private int size;
    
    public AffinityPropagation(List<Vect> items, int size) {
        super(items);
        alpha = 0.5;
        this.size = size;
    }
    
    @Override
    public void execute() {
        
        double[][] a = new double[items.size()][items.size()];
        double[][] r = new double[items.size()][items.size()];
        double[][] s = new double[items.size()][items.size()];
        List<Double> similatities = new ArrayList<>();
        
        for( int i = 0; i < s.length-1; ++i ) 
            for( int j = i+1; j < s.length; ++j ) {
                s[i][j] = -items.get(i).distance(items.get(j));
                s[j][i] = s[i][j];
                similatities.add(s[i][j]);
            }
        
        Collections.sort(similatities);
        int simSize = similatities.size();
        double median = simSize%2 == 0 ? (similatities.get(simSize/2)+similatities.get(simSize/2-1))/2 : similatities.get(simSize/2);
        System.out.println("median: "+median);
        for( int i = 0; i < s.length; ++i ) {
            s[i][i] = median;
            for( int j = 0; j < s.length; ++j ) {
                r[i][j] = 0;
                a[i][j] = 0;
            }
        }
        
        int maxIterations = 100;
        
        for( int iter = 0; iter < maxIterations; ++iter ) {
            System.out.println("Iteration number "+(iter+1));
            
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
        List<item> its = new ArrayList<>();
        for( int i = 0; i < items.size(); ++i ) {
            if( r[i][i]+a[i][i] > 0 )  {
                its.add(new item((a[i][i]), i));
                indexes.add(i);
            }
        }
        
        if( size != 0 ) {
            int[] repsIndexes = indexes.stream().mapToInt((e)->e).toArray();

            Map<Integer, List<Integer>> index = Util.createIndex(repsIndexes, items.stream().map((v)->v).toArray(Vect[]::new));

            List<RepresentativeIndexes> representativeIndexes = new ArrayList<>();
            index.entrySet().stream().forEach((v) -> {             
                double availability = 0.0;
                for( int i = 0; i < its.size(); ++i ) 
                    if( its.get(i).i == v.getKey() ) {
                        availability = its.get(i).v;
                        break;
                    }
                representativeIndexes.add(new RepresentativeIndexes(v.getKey(), v.getValue(), availability));        
            });



            Collections.sort(representativeIndexes);
            Collections.sort(representativeIndexes, (RepresentativeIndexes aa, RepresentativeIndexes bb) -> {            
                return Double.compare(bb.availability, aa.availability);        
            });

            representatives = new int[size];
            for( int i = 0; i < size; ++i )
                representatives[i] = representativeIndexes.get(i).id;
        
        } else {
            representatives = indexes.stream().mapToInt((e)->e).toArray();        
        }
    }
    
    private class item implements Comparable<item>{
        double v;
        int i;
        
        item(double vv, int ii) {
            v = vv;
            i = ii;
        }

        @Override
        public int compareTo(item o) {
            return Double.compare(v, o.v);
        }    
    }
    
    
    private class RepresentativeIndexes implements Comparable<RepresentativeIndexes> {
        
        private Integer id;
        private List<Integer> indexes;
        private double availability;
        
        public RepresentativeIndexes(Integer id, List<Integer> indexes, double availability) {
            this.id = id;
            this.indexes = indexes;
            this.availability = availability;
        }
        
        @Override
        public int compareTo(RepresentativeIndexes o) {
            return Integer.compare(o.indexes.size(), indexes.size());
        }
        
    }
    
    
        
}

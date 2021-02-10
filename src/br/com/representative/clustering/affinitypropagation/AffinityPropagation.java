/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering.affinitypropagation;

import br.com.methods.utils.Util;
import br.com.methods.utils.Vect;
import br.com.representative.clustering.Partitioning;
import com.clust4j.algo.AffinityPropagationParameters;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

/**
 *
 * @author Windows
 */
public class AffinityPropagation extends Partitioning {

    private double alpha;
    private int size;
    
    private Array2DRowRealMatrix mat = null;
    
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
        
        for( int i = 0; i < s.length; ++i ) 
            for( int j = i+1; j < s.length; ++j ) {
                s[i][j] = -items.get(i).distance(items.get(j));
                s[j][i] = s[i][j];
                similatities.add(s[i][j]);
            }
        
        mat = new Array2DRowRealMatrix(s);        
        AffinityPropagationParameters app = new AffinityPropagationParameters();
        app.setDampingFactor(0.9);
        
        com.clust4j.algo.AffinityPropagation ap = app.fitNewModel(mat);
        representatives = ap.getLabels();
        
        
        int maxCluster = -1;
        for( int i = 0; i < representatives.length; ++i )
            maxCluster = Math.max(maxCluster, representatives[i]);
        
        Point2D.Double[] points = new Point2D.Double[maxCluster+1];
        for( int i = 0; i < points.length; ++i ) {
            points[i] = new Point2D.Double(0,0);
        }
            
        int[] sizes = new int[maxCluster+1];
        Arrays.fill(sizes, 0);
        
        for( int i = 0; i < representatives.length; ++i ) {
            
            points[representatives[i]].x += items.get(i).get(0);
            points[representatives[i]].y += items.get(i).get(1);
            sizes[representatives[i]]++;
        }
        
        for( int i = 0; i < points.length; ++i ) {
            points[i].x /= (double)sizes[i];
            points[i].y /= (double)sizes[i];
        }
        List<Integer> medoids = new ArrayList<>();
        for( int i = 0; i < points.length; ++i ) {
            double d = Double.MAX_VALUE;
            int medoid = 0;
            for( int j = 0; j < representatives.length; ++j ) {
                if( i == representatives[j] ) {
                    double d2 = Util.euclideanDistance(points[i].x, points[i].y, 
                            items.get(j).get(0), items.get(j).get(1));
                    if( d2 < d ) {
                        medoid = j;
                        d = d2;
                    }
                }
            }
            
            medoids.add(medoid);
        }
                
        representatives = medoids.stream().mapToInt((v)->v).toArray();
        
       // Arrays.sort(representatives);
        
//        for( int i = 0; i < representatives.length; ++i )
//            System.out.println(">> "+representatives[i]);
        
        
//        Collections.sort(similatities);
//        int simSize = similatities.size();
//        double median = simSize%2 == 0 ? (similatities.get(simSize/2)+similatities.get(simSize/2-1))/2 : similatities.get(simSize/2);
//        for( int i = 0; i < s.length; ++i ) {
//            s[i][i] = median;
//            for( int j = 0; j < s.length; ++j ) {
//                r[i][j] = 0;
//                a[i][j] = 0;
//            }
//        }
//        
//        int maxIterations = 100;
//        
//        for( int iter = 0; iter < maxIterations; ++iter ) {
//            System.out.println("Iteration number "+(iter+1));
//            
//            // update responsabilities
//            for( int i = 0; i < items.size(); ++i ) {                
//                for( int k = 0; k < items.size(); ++k ) {
//                    
//                    double max = -1e100;
//                    for( int j = 0; j < k; ++j ) 
//                        if( a[i][j]+s[i][j] > max )
//                            max = a[i][j]+s[i][j];               
//                    for( int j = k+1; j < items.size(); ++j )
//                        if( a[i][j]+s[i][j] > max )
//                            max = a[i][j]+s[i][j];       
//                    
//                    r[i][k] = (1.0-alpha)*(s[i][k] - max) + alpha*r[i][k];
//                    
//                }
//            }            
//            // update availabilities
//            for( int i = 0; i < items.size(); ++i ) {                
//                for( int k = 0; k < items.size(); ++k ) {
//                    
//                    if( i == k ) { // update auto availabilities
//                        double sum = 0;
//                        for( int j = 0; j < k; ++j )
//                            sum += Math.max(0, r[j][k]);
//                        for( int j = k+1; j < items.size(); ++j )
//                            sum += Math.max(0, r[j][k]);
//
//                        a[k][k] = (1.0-alpha)*sum + alpha*a[k][k];
//                        
//                    } else {
//                        
//                        double sum = 0;                    
//                        for( int j = 0; j < items.size(); ++j ) 
//                            if( j != i && j != k )
//                                sum += Math.max(0, r[j][k]);
//
//                        a[i][k] = (1.0-alpha)*Math.min(0, r[k][k] + sum) + alpha*a[i][k];
//                        
//                    }
//                }
//            }    
//            
//        }
//        
//        List<Integer> indexes = new ArrayList<>();        
//        Map<Integer, Double> map = new HashMap<>();        
//        
//        for( int i = 0; i < items.size(); ++i ) {
//            if( r[i][i]+a[i][i] > 0 )  {
//                indexes.add(i);                
//                map.put(i, a[i][i]);
//            }
//        }
//        
//        if( size != 0 ) {
//            int[] repsIndexes = indexes.stream().mapToInt((e)->e).toArray();
//
//            Map<Integer, List<Integer>> index = Util.createIndex(repsIndexes, items.stream().map((v)->v).toArray(Vect[]::new));
//
//            List<RepresentativeIndexes> representativeIndexes = new ArrayList<>();
//            index.entrySet().stream().forEach((v) -> {             
//                double availability = map.get(v.getKey());                
//                representativeIndexes.add(new RepresentativeIndexes(v.getKey(), v.getValue(), availability));        
//            });
//
//
//
//            Collections.sort(representativeIndexes);
//            Collections.sort(representativeIndexes, (RepresentativeIndexes aa, RepresentativeIndexes bb) -> {            
//                return Double.compare(bb.availability, aa.availability);        
//            });
//
//            representatives = new int[size];
//            for( int i = 0; i < size; ++i )
//                representatives[i] = representativeIndexes.get(i).id;
//        
//        } else {
//            representatives = indexes.stream().mapToInt((e)->e).toArray();        
//        }
    }
    
    public class RepresentativeIndexes implements Comparable<RepresentativeIndexes> {
        
        private Integer id;
        private List<Integer> indexes;
        private double availability;
        
        public RepresentativeIndexes(Integer id, List<Integer> indexes, double availability) {
            this.id = id;
            this.indexes = indexes;
            this.availability = availability;
        }
        
        public RepresentativeIndexes(Integer id, List<Integer> indexes) {
            this(id, indexes, 0);
        }
        
        @Override
        public int compareTo(RepresentativeIndexes o) {
            return Integer.compare(o.indexes.size(), indexes.size());
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public List<Integer> getIndexes() {
            return indexes;
        }

        public void setIndexes(List<Integer> indexes) {
            this.indexes = indexes;
        }
        
        
        
        
    }
    
    
        
}

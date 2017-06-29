/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.clustering;

import br.com.methods.utils.Vect;
import java.util.List;

/**
 *
 * @author Windows
 */
public abstract class Partitioning extends Clustering {
    protected List<List<Integer>> clusters;
    
    public Partitioning(List<Vect> items) {
        super(items);
    }
    
    public List<List<Integer>> getClusters() {
        return clusters;
    }
    
            
    
}

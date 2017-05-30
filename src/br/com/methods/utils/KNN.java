/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.utils;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wilson
 */
public class KNN {

    public KNN(int nrNeighbors) {
        this.nrNeighbors = nrNeighbors;
    }
    
    public Pair[][] execute(List<OverlapRect> rects) {
        long start = System.currentTimeMillis();
        Pair[][] neighbors = null;

        if (this.nrNeighbors > rects.size() - 1) {
            throw new RuntimeException("Number of neighbors bigger than the number of " +
                    "elements minus one (an element is not computed as a neighbor " +
                    "of itself)!");
        }

        //init the neighbors list
        neighbors = new Pair[rects.size()][];
        for (int i = 0; i < neighbors.length; i++) {
            neighbors[i] = new Pair[this.nrNeighbors];

            for (int j = 0; j < neighbors[i].length; j++) {
                neighbors[i][j] = new Pair(-1, Float.MAX_VALUE);
            }
        }

        for (int i = 0; i < rects.size(); i++) {
            for (int j = 0; j < rects.size(); j++) {
                if (i == j) {
                    continue;
                }                
                float dist = (float) br.com.methods.utils.Util.euclideanDistance(rects.get(i).getCenterX(), rects.get(i).getCenterY(), 
                                                                           rects.get(j).getCenterX(), rects.get(j).getCenterY());

                if (dist < neighbors[i][neighbors[i].length - 1].value) {
                    for (int k = 0; k < neighbors[i].length; k++) {
                        if (neighbors[i][k].value > dist) {
                            for (int n = neighbors[i].length - 1; n > k; n--) {
                                neighbors[i][n].index = neighbors[i][n - 1].index;
                                neighbors[i][n].value = neighbors[i][n - 1].value;
                            }

                            neighbors[i][k].index = j;
                            neighbors[i][k].value = dist;
                            break;
                        }
                    }
                }
            }
        }

        long finish = System.currentTimeMillis();

        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                "KNN time: " + (finish - start) / 1000.0f + "s");

        return neighbors;
    }
    
    
    
    

    private int nrNeighbors = 5;
}

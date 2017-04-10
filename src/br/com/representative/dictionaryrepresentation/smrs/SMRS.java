/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.dictionaryrepresentation.smrs;

import br.com.methods.utils.Util;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Windows
 */
public class SMRS {
    
    private double[][] items;
    
    public SMRS(List<? extends List<Double>> items) {
        this.items = Util.elementMatrix(items);
    }
    
    public void execute() {
        
        // setting up some parameters, using parameters proposed by the author of the technique
        double alpha = 5;
        double r = 0;
        double q = 2;
        
        double[] regParam = {alpha, alpha};
        boolean affine = true;
        double thr = Math.pow(10, -7);
        int maxIter = 5000;
        double thrS = 0.99, thrP = 0.95;
        int N = items[0].length;
        
        double[][] Y = Arrays.stream(items)
                .map((double[] row)->row.clone())
                .toArray((int length)->new double[length][]);
        
        double[] meanVector = Util.mean(Y, 1);
        // meanVector is treated as a column
        Y = Util.minus(Y, Util.reapmat(meanVector, N));
        
        double[][] C = almLasso(Y, affine, regParam, q, thr, maxIter);
        
        int[] indexes = findRep(C, thrS, q);
        
    }

    private double[][] almLasso(double[][] Y, boolean affine, double[] regParam, double q, double thr, int maxIter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private int[] findRep(double[][] C, double thrS, double q) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

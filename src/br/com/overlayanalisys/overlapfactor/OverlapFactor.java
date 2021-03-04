/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.overlayanalisys.overlapfactor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilson
 */
public class OverlapFactor {
    
    private double[] widths;
    private double[] heights;
    private double[] x;
    private double[] y;
    
    private List<Double> factors;
    private double overlapFactor;
    
    
    public OverlapFactor(double[] x, double[] y, double[] widths, double[] heights) 
    {
        this.widths = widths;
        this.heights = heights;
        this.x = x;
        this.y = y;
    }
    
    
    public double fit_transform()
    {
        overlapFactor = 0.0f;
        factors = new ArrayList<>();
        
        for( int i = 0; i < widths.length; ++i ) {            
            for( int j = i+1; j < widths.length; ++j ) {
                double wi = widths[i]/2.0f;
                double wj = widths[j]/2.0f;
                
                double hi = heights[i]/2.0f;
                double hj = heights[j]/2.0f;
                
                double ow = (wi+wj)/Math.abs(x[i]-x[j]);
                double oh = (hi+hj)/Math.abs(y[i]-y[j]);
                
                
                double tij = Math.max(Math.min(ow, oh), 1.0f);
                
                overlapFactor += (tij);
                factors.add(overlapFactor);
            }
        }
        
        overlapFactor = overlapFactor / factors.size();
        
        
        return overlapFactor;
    }
    
    public double getOverlapFactor()
    {
        return overlapFactor;
    }
    
    public List<Double> getFactors() 
    {
        return factors;
    }
    
}

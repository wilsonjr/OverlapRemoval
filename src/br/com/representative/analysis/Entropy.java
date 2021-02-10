/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.analysis;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * Rserve, DescTools need to be installed.
 * @author Windows
 */
public class Entropy implements RepresentativeAnalysis {
    private Point2D.Double[] pts;
    
    public Entropy(Point2D.Double[] pts) {
        this.pts = pts;
    }
    
    @Override
    public double init() {
        try {
             
            double[] values = new double[pts.length*2];
            for( int i = 0; i < pts.length; ++i )
                values[i] = pts[i].x;
            for( int i = pts.length; i < values.length; ++i )
                values[i] = pts[i-pts.length].y;
            
            String list = "c("+String.valueOf(values[0]);
            for( int i = 1; i < values.length; ++i )
                list += (","+String.valueOf(values[i]));
            list += ")";
            
            
            RConnection connection = ConnectionSingleton.getInstance();   
            String script = "m <- matrix("+list+", nrow="+String.valueOf(pts.length)+", ncol=2);" +
                            "require(DescTools);"+
                            "Entropy(table(m));";
            
            REXP x = connection.eval(script);
            
            return Math.abs(x.asDouble());            
            
        } catch( REXPMismatchException | RserveException e ) {
            System.err.println("Exception when computing Entropy: "+e.toString());
        } 
        return -1.0;
    }
    
    
    @Override
    public String toString() {
        return "Entropy";
    }
    
}

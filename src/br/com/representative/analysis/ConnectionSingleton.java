/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.analysis;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 *
 * @author wilson
 */
public class ConnectionSingleton {
    
    private static RConnection rConnection = null;
        
    private ConnectionSingleton() {}

    public static RConnection getInstance() throws RserveException {
        if( rConnection == null ) {
            rConnection = new RConnection();
        }                

        return rConnection;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.util.registry;

import br.com.representative.RepresentativeRegistry;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Windows
 */
public abstract class TechniquesRegistry {
    
    protected static HashMap<String, Object> map = new HashMap<>();    
    
    protected TechniquesRegistry() {}
    
    public synchronized static Object getInstance(Class classObject, Object... initArgs) {
    
        String canonicalName = classObject.getCanonicalName();
        
        Object classInstantiation = map.get(canonicalName);
        if( classInstantiation == null ) {
            
            try {
                Class<?>[] parameterTypes = Class.forName(canonicalName).getConstructors()[0].getParameterTypes();
                Constructor classConstructor = Class.forName(canonicalName).getConstructor(parameterTypes);
                classInstantiation = classConstructor.newInstance(initArgs);                
                
                map.put(canonicalName, classInstantiation);
            } catch( ClassNotFoundException | InstantiationException | IllegalAccessException | 
                     IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e ) {
                 Logger.getLogger(RepresentativeRegistry.class.getName()).log(Level.SEVERE, null, e);
            }
            
        }
        
        return classInstantiation;
    }
    
}

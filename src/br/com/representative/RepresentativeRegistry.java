/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Windows
 */
public class RepresentativeRegistry {
        
    private static HashMap<String, Object> map = new HashMap<>();    
    private static HashMap<String, String> classPackage = new HashMap<>();
    
    protected RepresentativeRegistry() {}
    
    public synchronized static Object getInstance(String className, Object... initArgs) {
        
        Object classInstantiation = map.get(className);
        if( classInstantiation == null ) {
            
            try {
                
                Class<?>[] parameterTypes = Class.forName(className).getConstructors()[0].getParameterTypes();
                Constructor classConstructor = Class.forName(className).getConstructor(parameterTypes);
                classInstantiation = classConstructor.newInstance(initArgs);                
                
                map.put(className, classInstantiation);
            } catch( ClassNotFoundException | InstantiationException | IllegalAccessException | 
                     IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e ) {
                 Logger.getLogger(RepresentativeRegistry.class.getName()).log(Level.SEVERE, null, e);
            }
            
        }
        
        return classInstantiation;
    }
    
    public static void addClassPackage(String key, String value) {        
        classPackage.put(key, value);
    }
        
    
    
}

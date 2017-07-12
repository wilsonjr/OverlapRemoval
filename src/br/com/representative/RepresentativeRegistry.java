/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative;

import br.com.representative.clustering.affinitypropagation.AffinityPropagation;
import br.com.representative.clustering.furs.FURS;
import br.com.representative.clustering.hierarchical.HierarchicalClustering;
import br.com.representative.clustering.partitioning.BisectingKMeans;
import br.com.representative.clustering.partitioning.Dbscan;
import br.com.representative.clustering.partitioning.KMeans;
import br.com.representative.clustering.partitioning.KMedoid;
import br.com.representative.dictionaryrepresentation.DS3;
import br.com.representative.dictionaryrepresentation.SMRS;
import br.com.representative.lowrank.CSM;
import br.com.representative.lowrank.KSvd;
import br.com.representative.metric.GNAT;
import br.com.representative.metric.MST;
import br.com.representative.metric.SSS;
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
        
        if( classPackage.isEmpty() ) {
            RepresentativeRegistry.addClassPackage("AffinityPropagation", AffinityPropagation.class.getCanonicalName());
        RepresentativeRegistry.addClassPackage("SSS", SSS.class.getName());
        RepresentativeRegistry.addClassPackage("MST", MST.class.getCanonicalName());
        RepresentativeRegistry.addClassPackage("GNAT", GNAT.class.getCanonicalName());
        RepresentativeRegistry.addClassPackage("KSvd", KSvd.class.getCanonicalName());
        RepresentativeRegistry.addClassPackage("CSM", CSM.class.getCanonicalName());
        RepresentativeRegistry.addClassPackage("SMRS", SMRS.class.getCanonicalName());
        RepresentativeRegistry.addClassPackage("DS3", DS3.class.getCanonicalName());
        RepresentativeRegistry.addClassPackage("KMedoid", KMedoid.class.getCanonicalName());
        RepresentativeRegistry.addClassPackage("KMeans", KMeans.class.getCanonicalName());
        RepresentativeRegistry.addClassPackage("Dbscan", Dbscan.class.getCanonicalName());
        RepresentativeRegistry.addClassPackage("BisectingKMeans", BisectingKMeans.class.getCanonicalName());
        RepresentativeRegistry.addClassPackage("HierarchicalClustering", HierarchicalClustering.class.getCanonicalName());
        RepresentativeRegistry.addClassPackage("FURS", FURS.class.getCanonicalName());
        }
        
    
        String canonicalName = classPackage.get(className);
        
        Object classInstantiation = map.get(canonicalName);
        if( classInstantiation == null ) {
            
            try {
                System.out.println("name: "+canonicalName);
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
    
    public synchronized static void addClassPackage(String key, String value) {        
        System.out.println(key+" >> "+value);
        classPackage.put(key, value);
    }
        
    
    
}

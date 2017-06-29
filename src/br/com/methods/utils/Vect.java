/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.methods.utils;

/**
 *
 * @author Windows
 */
public class Vect {
    
    private double[] _vector;
    
    public Vect(double[] vector) {
        _vector = vector;
    }
    
    public double[] vector() {
        return _vector;
    }
    
    public double get(int i) {
        return _vector[i];
    }
    
    
    public double distance(Vect v) {
        
        double sum = 0;
        
        for( int i = 0; i < _vector.length; ++i ) {
            sum += (_vector[i]-v.get(i))*(_vector[i]-v.get(i));
        }
        
        return Math.sqrt(sum);
    }
    
}

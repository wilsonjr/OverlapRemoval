/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.methods.utils;

import java.util.Arrays;

/**
 *
 * @author Windows
 */
public class Vect {
    
    private double[] _vector;
    
    public Vect(double[] vector) {
        _vector = vector;
    }
    
    public Vect(int n) {
        _vector = new double[n];
        Arrays.fill(_vector, 0);
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

    public void add(Vect v) {
        for( int i = 0; i < _vector.length; ++i ) {
            _vector[i] += v.get(i);
        }
    }

    public void divide(double n) {
        for( int i = 0; i < _vector.length; ++i )
            _vector[i] /= n;
    }
    
}

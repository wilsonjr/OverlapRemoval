/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.utils;

import java.util.List;

/**
 * Estrutuda para armazenar uma matriz esparsa
 * @author wilson
 */
public class YaleMatrix {
    public int[] ia;
    public int[] ja;
    public double[] a;
    
    /**
     * Cria uma Matriz de Yale
     * @param a
     * @param ja
     * @param ia 
     */
    public YaleMatrix(List<Double> a, List<Integer> ja, List<Integer> ia) {
        this.ia = new int[ia.size()];
        this.ja = new int[ja.size()];
        this.a = new double[a.size()];
        for( int i = 0; i < ia.size(); ++i  )
            this.ia[i] = ia.get(i);
        for( int i = 0; i < ja.size(); ++i  )
            this.ja[i] = ja.get(i);
        for( int i = 0; i < a.size(); ++i  )
            this.a[i] = a.get(i);                
    }
    
    /**
     * Cria uma Matriz de Yale
     * @param a
     * @param ja
     * @param ia 
     */
    public YaleMatrix(double[] a, int[] ja, int[] ia) {
        this.ia = new int[ia.length];
        this.ja = new int[ja.length];
        this.a = new double[a.length];
        for( int i = 0; i < ia.length; ++i  )
            this.ia[i] = ia[i];
        for( int i = 0; i < ja.length; ++i  )
            this.ja[i] = ja[i];
        for( int i = 0; i < a.length; ++i  )
            this.a[i] = a[i];                
    }
    
}

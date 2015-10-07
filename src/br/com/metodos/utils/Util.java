/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.utils;

import br.com.metodos.overlap.vpsc.Event;
import java.util.ArrayList;

/**
 *
 * @author wilson
 */
public class Util {
    
    /**
     * Euclidean distance between two points
     * @param p1x x coordinate of first point
     * @param p1y y coordinate of first point
     * @param p2x x coordinate of second point
     * @param p2y y coordinate of second point
     * @return distance between two points
     */
    public static double distanciaEuclideana(double p1x, double p1y, double p2x, double p2y) {
        return Math.sqrt(Math.pow(p1x-p2x, 2) + Math.pow(p1y-p2y, 2));
    }
    
    
    public static ArrayList<Retangulo> toRetangulo(ArrayList<RetanguloVis> rects) {
        ArrayList<Retangulo> rs = new ArrayList<>();
        
        for( RetanguloVis r: rects ) 
            rs.add(new Retangulo(r.getUX(), r.getUY(), r.getWidth(), r.getHeight()));
        
        return rs;
    }
        
    public static void toRetanguloVis(ArrayList<RetanguloVis> ori, ArrayList<Retangulo> rects) {
        
        for( int i = 0; i < rects.size(); ++i ) {
            ori.get(i).setUX(rects.get(i).getUX());
            ori.get(i).setUY(rects.get(i).getUY());
            ori.get(i).setWidth(rects.get(i).getWidth());
            ori.get(i).setHeight(rects.get(i).getHeight());
        }   
    }
        
    public static void quickSort(Event array[], int start, int end) {
        int i = start;                          
        int k = end;                            

        if( end - start >= 1 ) {
            Event pivot = array[start];       

            while( k > i ) {
                while( array[i].getPosition() <= pivot.getPosition() && i <= end && k > i )  
                    i++;                                    
                while( array[k].getPosition() > pivot.getPosition() && k >= start && k >= i ) 
                    k--;                                        
                if( k > i)                                      
                     swap(array, i, k);                                    
            }
            swap(array, start, k);          
            quickSort(array, start, k - 1);
            quickSort(array, k + 1, end);  
        }
        
    }

    private static void swap(Event array[], int index1, int index2)  {
            Event temp = array[index1];           
            array[index1] = array[index2];      
            array[index2] = temp;               
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.utils;

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
}

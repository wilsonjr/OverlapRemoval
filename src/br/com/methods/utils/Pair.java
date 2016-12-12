/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.utils;

/**
 *
 * @author wilson
 */
public class Pair implements Comparable {

    public Pair(int index, float value) {
        this.index = index;
        this.value = value;
    }

    public int compareTo(Object o) {
        if (o instanceof Pair) {
            if (this.value - ((Pair) o).value == EPSILON) {
                return 0;
            } else if (this.value - ((Pair) o).value > EPSILON) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static final float EPSILON = 0.00001f;
    public int index;
    public float value;
}
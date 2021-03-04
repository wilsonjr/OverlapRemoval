/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.methods.overlap.pfsp;

/**
 *
 * @author wilson
 */
public class Node {
    public double x;
    public double y;
    public double width;
    public double height;
    public int id;
    
    public double up_x;
    public double up_y;
    public double up_gamma;
    
    public Node() {}
    
    public Node(double x, double y, double width, double height, int id) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;
        
        this.up_x = 0.0;
        this.up_y = 0.0;
        this.up_gamma = Double.MIN_VALUE;
    }
}

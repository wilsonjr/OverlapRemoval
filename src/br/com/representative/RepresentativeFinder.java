/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative;

/**
 *
 * @author Windows
 */
public abstract class RepresentativeFinder {    
    protected int[] representatives;
    
    public abstract void execute();
    public abstract void filterData(int[] indexes);
    
    public int[] getRepresentatives() {
        return representatives;
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

import java.util.Comparator;

/**
 *
 * @author wilson
 */
public class NoComparator implements Comparator<No> {

    @Override
    public int compare(No o1, No o2) {
        
        if( o1.getPosition() < o2.getPosition() )
            return -1;
        else if( o1.getPosition() > o2.getPosition() )
            return 1;
        return 1;
    }
    
}

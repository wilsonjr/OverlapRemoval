/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.metodos.overlap.projsnippet;
/**
 *
 * @author wilson
 */
public class HeapElementComparator implements HeapComp<HeapElement> {
    @Override
    public double compare(HeapElement e1, HeapElement e2) {
        return e1.getKey() - e2.getKey();
    }
    
}

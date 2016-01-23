/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.metodos.overlap.projsnippet;

/**
 * Classe que realiza a comparação de 'Elementos Heap'
 * @author wilson
 */
public class HeapElementComparator implements HeapComp<HeapElement> {
    
    /**
     * Realiza a comparação de HeapElements
     * @param e1 HeapElement referência
     * @param e2 HeapElement a ser verificado
     * @return Retorna um double:
     *             >  0 se e1 é maior que e2;
     *             <  0 se e1 é menor que e2;
     *             == 0  se são iguais.
     */
    @Override
    public double compare(HeapElement e1, HeapElement e2) {
        return e1.getKey() - e2.getKey();
    }
    
}

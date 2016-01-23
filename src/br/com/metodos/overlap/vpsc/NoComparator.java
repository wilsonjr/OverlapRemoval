/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

import java.util.Comparator;

/**
 * Realiza a comparação de dois nós
 * @author wilson
 */
public class NoComparator implements Comparator<No> {

    /**
     * Compara dois nós
     * @param o1 Nó referência
     * @param o2 Nó a ser verificado
     * @return -1 se o1 < o2
     *         1  se o1 > o2
     *         1  se o1 == o2 (Não se pode descartar nós, para que não se descarte elementos da projeção)
     */
    @Override
    public int compare(No o1, No o2) {
        
        if( o1.getPosition() < o2.getPosition() )
            return -1;
        else if( o1.getPosition() > o2.getPosition() )
            return 1;
        
        // se for igual (0) a coleção descartaria, mas neste caso temos que guardar
        // caso contrário, não poderíamos remover sobreposição de retângulos coincidentes
        return 1;
    }
    
}

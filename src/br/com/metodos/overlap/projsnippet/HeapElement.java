/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.projsnippet;

/**
 * Para utilizar o Hesp Binário, é necessário implementar essa interface.
 * 'key' representa o valor corrente da instância, que determinará a ordenação do Heap.
 * @author wilson
 */
public interface HeapElement {
    
    /**
     * Atualiza o valor da instância.
     * @param key 
     */
    void setKey(double key);
    
    /**
     * Retorna o valor corrente da instância no Heap.
     * @return 
     */
    double getKey();
}

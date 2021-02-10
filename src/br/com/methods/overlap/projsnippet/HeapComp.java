/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.projsnippet;

/**
 * Interface para utilização da comparação no Heap Binário
 * @author wilson
 * @param <T>
 */
public interface HeapComp<T> {
    double compare(T e1, T e2);
}

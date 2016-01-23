/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

import br.com.metodos.utils.Retangulo;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Representa a estrutura Nó, definida pelo método VPSC
 * @author wilson
 */
public class No {
    private Variavel var;
    private Retangulo rect;
    private double position;
    private No belowNeighbour, aboveNeighbour;
    private TreeSet<No> leftNeighbours = new TreeSet<>(new NoComparator());
    private TreeSet<No> rightNeighbours = new TreeSet<>(new NoComparator());
    private boolean deleted;
    
    public No(Variavel var, Retangulo rect, double position) {
        this.var = var;
        this.rect = rect;
        this.position = position;
        deleted = false;
    }
    
    /**
     * Retorna a variavel contida no nó
     * @return Variavel
     */
    public Variavel getVar() {
        return var;
    }
    
    /**
     * Retorna o retângulo que envolve o nó
     * @return Retângulo envolvente do nó.
     */
    public Retangulo getRect() {
        return rect;
    }
    
    /**
     * Posição do nó considerando aspectos 1D
     * @return Posição do nós em relação uma coordenada.
     */
    public double getPosition() {
        return position;
    }
    
    /**
     * Adiciona elementos que estão à esquerda do nó
     * @param v No
     */
    public void addLeftNeighbour(No v) {
        leftNeighbours.add(v);
    }
    
    /**
     * Adiciona elementos que estão à direita do nó
     * @param v No
     */
    public void addRightNeighbour(No v) {
        rightNeighbours.add(v);
    }    
    
    /**
     * Adiciona coleção de elementos à esquerda do nó corrente
     * @param neighbours Elementos a serem inseridos
     */
    public void setLeftNeighbours(TreeSet<No> neighbours) {
        leftNeighbours = neighbours;
        
        Iterator<No> it = neighbours.iterator();
        while( it.hasNext() ) {
            No no = it.next();
            if( !deleted && !no.getDeleted() ) 
                no.addRightNeighbour(this);            
        }
    }
    
    /**
     * Adiciona coleção de elementos à direita do nó corrente
     * @param neighbours Elementos a serem inseridos
     */
    public void setRightNeighbours(TreeSet<No> neighbours) {
        rightNeighbours = neighbours;
        
        Iterator<No> it = neighbours.iterator();
        while( it.hasNext() ) {
            No no = it.next();
            if( !deleted && !no.getDeleted() ) 
                no.addLeftNeighbour(this);            
        }        
    }
    
    /**
     * Retorna os vizinhos da direita do nó
     * @return TreeSet de vizinhos da direita
     */
    public TreeSet<No> getRightNeighbours() {
        return rightNeighbours;
    }
    
    /**
     * Retorna os vizinhos da direita do nó
     * @return TreeSet de vizinhos da direita
     */
    public TreeSet<No> getLeftNeighbours() {
        return leftNeighbours;
    }   
    
    /**
     * Retorna o primeiro vizinho abaixo do nó
     * @return No
     */
    public No getBelowNeighbour() {
        return belowNeighbour;
    }
    
    /**
     * Retorna o primeiro vizinho acima do nó
     * @return No
     */
    public No getAboveNeighbour() {
        return aboveNeighbour;
    }
    
    /**
     * Define o primeiro vizinho abaixo do nó
     * @param v No
     */
    public void setBelowNeighbour(No v) {
        belowNeighbour = v;
    }
    
    /**
     * Define o primeiro vizinho acima do nó
     * @param v No
     */
    public void setAboveNeighbour(No v) {
        aboveNeighbour = v;
    }
    
    /**
     * Verifica se o nó está marcado para ser removido
     * @return true se será removido,
     *         false caso contrário.
     */
    public boolean getDeleted() {
        return deleted;
    }
    
    /**
     * Define se o nó deverá ser removido
     * @param deleted true se deve ser removido posteriormente,
     *                false caso contrário.
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Remove o vizinho a esquerda passado como parâmetro.
     * Essa remoção é preguiçosa, no sentido de que um flag para remoção posterior é utilizado
     * @param v 
     */
    public void removeRightNeighbour(No v) {
        Iterator<No> it = rightNeighbours.iterator();
        No toRemove = null;
        
        while( it.hasNext() ) {
            toRemove = it.next();
            if( toRemove == v )
                break;
        }
        
        if( toRemove != null ) {
            toRemove.setDeleted(true);            
        }
    }

    /**
     * Remove o vizinho a esquerda passado como parâmetro.
     * Essa remoção é preguiçosa, no sentido de que um flag para remoção posterior é utilizado
     * @param v 
     */
    public void removeLeftNeighbour(No v) {
        Iterator<No> it = leftNeighbours.iterator();
        No toRemove = null;
        
        while( it.hasNext() ) {
            toRemove = it.next();
            if( toRemove == v )
                break;
        }
        
        if( toRemove != null ) {
            toRemove.setDeleted(true);            
        }
    }
    
}

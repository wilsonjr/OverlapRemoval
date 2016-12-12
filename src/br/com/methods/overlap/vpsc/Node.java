/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.vpsc;

import br.com.methods.utils.OverlapRect;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Representa a estrutura Nó, definida pelo método VPSC
 * @author wilson
 */
public class Node {
    private Variable var;
    private OverlapRect rect;
    private double position;
    private Node belowNeighbour, aboveNeighbour;
    private TreeSet<Node> leftNeighbours = new TreeSet<>(new NodeComparator());
    private TreeSet<Node> rightNeighbours = new TreeSet<>(new NodeComparator());
    private boolean deleted;
    
    public Node(Variable var, OverlapRect rect, double position) {
        this.var = var;
        this.rect = rect;
        this.position = position;
        deleted = false;
    }
    
    /**
     * Retorna a variavel contida no nó
     * @return Variable
     */
    public Variable getVar() {
        return var;
    }
    
    /**
     * Retorna o retângulo que envolve o nó
     * @return Retângulo envolvente do nó.
     */
    public OverlapRect getRect() {
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
     * @param v Node
     */
    public void addLeftNeighbour(Node v) {
        leftNeighbours.add(v);
    }
    
    /**
     * Adiciona elementos que estão à direita do nó
     * @param v Node
     */
    public void addRightNeighbour(Node v) {
        rightNeighbours.add(v);
    }    
    
    /**
     * Adiciona coleção de elementos à esquerda do nó corrente
     * @param neighbours Elementos a serem inseridos
     */
    public void setLeftNeighbours(TreeSet<Node> neighbours) {
        leftNeighbours = neighbours;
        
        Iterator<Node> it = neighbours.iterator();
        while( it.hasNext() ) {
            Node no = it.next();
            if( !deleted && !no.getDeleted() ) 
                no.addRightNeighbour(this);            
        }
    }
    
    /**
     * Adiciona coleção de elementos à direita do nó corrente
     * @param neighbours Elementos a serem inseridos
     */
    public void setRightNeighbours(TreeSet<Node> neighbours) {
        rightNeighbours = neighbours;
        
        Iterator<Node> it = neighbours.iterator();
        while( it.hasNext() ) {
            Node no = it.next();
            if( !deleted && !no.getDeleted() ) 
                no.addLeftNeighbour(this);            
        }        
    }
    
    /**
     * Retorna os vizinhos da direita do nó
     * @return TreeSet de vizinhos da direita
     */
    public TreeSet<Node> getRightNeighbours() {
        return rightNeighbours;
    }
    
    /**
     * Retorna os vizinhos da direita do nó
     * @return TreeSet de vizinhos da direita
     */
    public TreeSet<Node> getLeftNeighbours() {
        return leftNeighbours;
    }   
    
    /**
     * Retorna o primeiro vizinho abaixo do nó
     * @return Node
     */
    public Node getBelowNeighbour() {
        return belowNeighbour;
    }
    
    /**
     * Retorna o primeiro vizinho acima do nó
     * @return Node
     */
    public Node getAboveNeighbour() {
        return aboveNeighbour;
    }
    
    /**
     * Define o primeiro vizinho abaixo do nó
     * @param v Node
     */
    public void setBelowNeighbour(Node v) {
        belowNeighbour = v;
    }
    
    /**
     * Define o primeiro vizinho acima do nó
     * @param v Node
     */
    public void setAboveNeighbour(Node v) {
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
    public void removeRightNeighbour(Node v) {
        Iterator<Node> it = rightNeighbours.iterator();
        Node toRemove = null;
        
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
    public void removeLeftNeighbour(Node v) {
        Iterator<Node> it = leftNeighbours.iterator();
        Node toRemove = null;
        
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

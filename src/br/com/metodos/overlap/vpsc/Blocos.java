/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

import java.util.ArrayList;

/**
 *
 * @author wilson
 */
public class Blocos  {
    private ArrayList<Variavel> vars;
    private ArrayList<Bloco> blocos;    
    
    /**
     * Cria um Bloco para cada Variavel em vars
     * @param vars Variaveis que representam cada elemento na projeção
     */
    public Blocos(ArrayList<Variavel> vars) {
        
        this.vars = vars;
        blocos = new ArrayList<>();
        
        for( int i = 0; i < vars.size(); ++i ) 
            blocos.add(new Bloco(vars.get(i)));       
    }    
    
    /**
     * Realiza a ordenação topológica das Variaveis
     * @return Ordenação topológica das Variaveis
     */
    public ArrayList<Variavel> totalOrder() {
        ArrayList<Variavel> order = new ArrayList<>();
        for( int i = 0; i < vars.size(); ++i )
            vars.get(i).setVisitado(false);
        
        for( int i = 0; i < vars.size(); ++i )
            if( vars.get(i).getIn().isEmpty() ) // ou seja, tem grau de entrada == 0?
                visita(vars.get(i), order);
        
        return order;
    }
    
    /**
     * Estabelece a ordenação topológica
     * @param v Variavel sendo analisada
     * @param order Pilha contendo a ordenação
     */
    private void visita(Variavel v, ArrayList<Variavel> order) {
        v.setVisitado(true);
        
        for( int j = 0; j < v.getOut().size(); ++j ) {
            if( !v.getOut().get(j).getRight().getVisitado() )
                visita(v.getOut().get(j).getRight(), order);
        }
        
        order.add(0, v);
    }
    
    /**
     * Mescla este bloco com o bloco do lado esquerdo.
     * @param b Bloco na posição esquerda
     */
    public void mergeLeft(Bloco b) {
        // inicializa as in constraints e recupera a restrição que possui maior violação
        b.heapifyInConstraints();     
        Restricao r = b.getMinInConstraint();        
        
        // caso essa violação seja maior que 0 é necessário mesclar os blocos
        while( r != null && r.getViolationLeft() > 0 ) {
            
            b.removeMinInConstraint();           
            Bloco bl = r.getLeft().getBloco();            
            if( bl.getIn() == null )
                bl.heapifyInConstraints();
            
            double distbltob = r.getRight().getOffset()-r.getLeft().getOffset()-r.getGap();                        
            if( b.getVars().size() > bl.getVars().size() ) {           
                b.mergeBlockLeft(bl, r, distbltob);
                r = b.getMinInConstraint();
            } else {
                bl.mergeBlockLeft(b, r, -distbltob);
                r = bl.getMinInConstraint();
                b = bl;
            }
        }
        
    }
    
    /**
     * Mescla este bloco com o bloco do lado direito.
     * @param b Bloco na posição direita
     */
    public void mergeRight(Bloco b) {
        b.heapifyOutConstraints();
        Restricao r = b.getMinOutConstraint();
        
        // caso essa violação seja maior que 0 é necessário mesclar os blocos
        while( r != null && r.getViolationRight() < 0 ) {
            b.removeMinOutConstraint();
            
            Bloco rightBlock = r.getRight().getBloco();
            
            if( rightBlock.getOut() == null )
                rightBlock.heapifyOutConstraints();
            
            double distancia =  r.getLeft().getOffset()+r.getGap()-r.getRight().getOffset();
           
            if( b.getVars().size() > rightBlock.getVars().size() ) {
                b.mergeBlockRight(rightBlock, r, distancia);
                r = b.getMinOutConstraint();
            } else {
                rightBlock.mergeBlockRight(b, r, -distancia);
                r = rightBlock.getMinOutConstraint();
                b = rightBlock;
            }            
        }
    }
        
    /**
     * Quebra o Bloco 'b' em dois Blocos 'lb' (left) e rb (right) segundo a restrição 'c'.
     * Isso ocorre porque o coeficiente de 'c' é negativo e, portanto, a solução não é ótima.
     * @param b Bloco que será quebrado
     * @param lb Bloco do lado esquerdo
     * @param rb Bloco do lado direito
     * @param c Restrição sendo analisada
     */
    public void restrictBlock(Bloco b, Bloco lb, Bloco rb, Restricao c) {
        c.setAtiva(false);
        /**
         * We define left(b,c) to be the nodes in b.vars connected by a path of constraints from b.active \ {c} to left(c),
         * i.e. the variables which are in the left sub-block of b if b is split by removing c.
         */
        b.addInSplitBlock(lb, c.getLeft());
        b.addInSplitBlock(rb, c.getRight());
    }    
    
    /**
     * Retorna os blocos criados
     * @return ArrayList de Blocos
     */
    public ArrayList<Bloco> getBlocos() {
        return blocos;
    }
}

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

import java.util.ArrayList;

/**
 *
 * @author wilson
 */
public class Blocks  {
    private ArrayList<Variable> vars;
    private ArrayList<Block> blocos;    
    
    /**
     * Cria um Block para cada Variable em vars
     * @param vars Variaveis que representam cada elemento na projeção
     */
    public Blocks(ArrayList<Variable> vars) {
        
        this.vars = vars;
        blocos = new ArrayList<>();
        
        for( int i = 0; i < vars.size(); ++i ) 
            blocos.add(new Block(vars.get(i)));       
    }    
    
    /**
     * Realiza a ordenação topológica das Variaveis
     * @return Ordenação topológica das Variaveis
     */
    public ArrayList<Variable> totalOrder() {
        ArrayList<Variable> order = new ArrayList<>();
        for( int i = 0; i < vars.size(); ++i )
            vars.get(i).setVisitado(false);
        
        for( int i = 0; i < vars.size(); ++i )
            if( vars.get(i).getIn().isEmpty() ) // ou seja, tem grau de entrada == 0?
                visita(vars.get(i), order);
        
        return order;
    }
    
    /**
     * Estabelece a ordenação topológica
     * @param v Variable sendo analisada
     * @param order Pilha contendo a ordenação
     */
    private void visita(Variable v, ArrayList<Variable> order) {
        v.setVisitado(true);
        
        for( int j = 0; j < v.getOut().size(); ++j ) {
            if( !v.getOut().get(j).getRight().getVisitado() )
                visita(v.getOut().get(j).getRight(), order);
        }
        
        order.add(0, v);
    }
    
    /**
     * Mescla este bloco com o bloco do lado esquerdo.
     * @param b Block na posição esquerda
     */
    public void mergeLeft(Block b) {
        // inicializa as in constraints e recupera a restrição que possui maior violação
        b.heapifyInConstraints();     
        Constraint r = b.getMinInConstraint();        
        
        // caso essa violação seja maior que 0 é necessário mesclar os blocos
        while( r != null && r.getViolationLeft() > 0 ) {
            
            b.removeMinInConstraint();           
            Block bl = r.getLeft().getBloco();            
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
     * @param b Block na posição direita
     */
    public void mergeRight(Block b) {
        b.heapifyOutConstraints();
        Constraint r = b.getMinOutConstraint();
        
        // caso essa violação seja maior que 0 é necessário mesclar os blocos
        while( r != null && r.getViolationRight() < 0 ) {
            b.removeMinOutConstraint();
            
            Block rightBlock = r.getRight().getBloco();
            
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
     * Quebra o Block 'b' em dois Blocks 'lb' (left) e rb (right) segundo a restrição 'c'.
     * Isso ocorre porque o coeficiente de 'c' é negativo e, portanto, a solução não é ótima.
     * @param b Block que será quebrado
     * @param lb Block do lado esquerdo
     * @param rb Block do lado direito
     * @param c Restrição sendo analisada
     */
    public void restrictBlock(Block b, Block lb, Block rb, Constraint c) {
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
     * @return ArrayList de Blocks
     */
    public ArrayList<Block> getBlocos() {
        return blocos;
    }
}

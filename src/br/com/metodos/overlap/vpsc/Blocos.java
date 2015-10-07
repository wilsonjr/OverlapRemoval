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
public class Blocos extends ArrayList<Bloco> {
    private ArrayList<Variavel> vars;
    private long timeBlock;
    
    public Blocos(ArrayList<Variavel> vars) {
        timeBlock = 0;
        this.vars = vars;
        
        for( int i = 0; i < vars.size(); ++i ) 
            add(new Bloco(vars.get(i)));
       
    }
    
    public long getTimeBlock() {
        return timeBlock;
    }
    
    public void incrementTimeBlock() {
        ++timeBlock;
    }
    
    public ArrayList<Variavel> totalOrder() {
        ArrayList<Variavel> order = new ArrayList<>();
        for( int i = 0; i < vars.size(); ++i )
            vars.get(i).setVisitado(false);
        
        for( int i = 0; i < vars.size(); ++i )
            if( vars.get(i).getIn().isEmpty() ) // ou seja, tem grau de entrada == 0?
                visita(vars.get(i), order);
        
        return order;
    }
    
    private void visita(Variavel v, ArrayList<Variavel> order) {
        v.setVisitado(true);
        
        for( int j = 0; j < v.getOut().size(); ++j ) {
            if( !v.getOut().get(j).getRight().getVisitado() )
                visita(v.getOut().get(j).getRight(), order);
        }
        
        order.add(0, v);
    }
    
    public void mergeLeft(Bloco b) {
        incrementTimeBlock();
        b.setTimeStamp(timeBlock);
        b.heapifyInConstraints();
     
        Restricao r = b.getMinInConstraint();        
        while( r != null && r.getViolationLeft() > 0 ) {
            
            b.removeMinInConstraint();           
            
            
            Bloco bl = r.getLeft().getBloco();
            
            if( bl.getIn() == null )
                bl.heapifyInConstraints();
            double distbltob = r.getRight().getOffset()-r.getLeft().getOffset()-r.getGap();
            
            
            if( b.getVars().size() > bl.getVars().size() ) {
                incrementTimeBlock();
                b.mergeBlockLeft(bl, r, distbltob);
                b.setTimeStamp(timeBlock);              
                r = b.getMinInConstraint();
            } else {
                incrementTimeBlock();
                bl.mergeBlockLeft(b, r, -distbltob);
                bl.setTimeStamp(timeBlock);
                r = bl.getMinInConstraint();
                b = bl;
            }
        }
        
    }
    
    public void mergeRight(Bloco b) {
        b.heapifyOutConstraints();
        Restricao r = b.getMinOutConstraint();
        
        while( r != null && r.getViolationRight() < 0 ) {
            b.removeMinOutConstraint();
            
            Bloco rightBlock = r.getRight().getBloco();
            
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
    
    
    public void restrict_block(Bloco b, Bloco lb, Bloco rb, Restricao c) {
        b.split(lb, rb, c);
        
        rb.setPosn(b.getPosn());
        rb.setWPosn(rb.getPosn()*rb.getWeight());
        mergeLeft(lb);
        
        rb = c.getRight().getBloco();
        
        double wposn = 0;        
        for( int i = 0; i < rb.getVars().size(); ++i ) 
            wposn += (rb.getVars().get(i).getWeight()*(rb.getVars().get(i).getDes() - rb.getVars().get(i).getOffset()));
        
        rb.setWPosn(wposn);
        rb.setPosn(rb.getWPosn()/rb.getWeight());
        
        mergeRight(rb);        
        
        b.setDeleted(true);
        add(lb);
        add(rb);
    }
    
}

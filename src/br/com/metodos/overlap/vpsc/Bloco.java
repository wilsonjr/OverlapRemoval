/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author Wilson
 */
public class Bloco {
    private ArrayList<Variavel> vars; // variáveis do bloco
    private double posn; // posição de referência do bloco
    private double weight; // soma dos pesos das variaveis nesse bloco
    private double wposn;  // soma ponderada das posições desejadas das variaveis nesse bloco
    private PriorityQueue<Restricao> in; // in constraints
    private PriorityQueue<Restricao> out; // out constraints
    private boolean deleted; // usamos uma estratégia para nao ter problema de ponteiro nulo
    private Restricao menor = null;
    
    public Bloco() {
        vars = new ArrayList<>();
          
        weight = 0;
        wposn = 0;
        in = null;
        out = null;
        deleted =false;
    }
    
    public Bloco(Variavel v) { 
        vars = new ArrayList<>();        
        weight = 0;
        wposn = 0;
        in = null;
        out = null;
        deleted =false;
        if( v != null ) {
            v.setOffset(0);
            addVar(v);
        }
    }
    
    public void addVar(Variavel v) {
        v.setBloco(this);
        vars.add(v);
        weight += v.getWeight();
        wposn += v.getWeight()*(v.getDes()-v.getOffset());
        posn = wposn/weight;
    }
    
    public void setPosn(double posn) {
        this.posn = posn;
    }
    
    public double getPosn() {
        return posn;
    }    
    
    public PriorityQueue<Restricao> heapifyInConstraints() {
        in = new PriorityQueue<>(vars.size(), new Comparator<Restricao>() {
           @Override
           public int compare(Restricao a, Restricao b) {
               if( a.getViolationLeft() < b.getViolationLeft() )
                   return 1;
               else if( a.getViolationLeft() > b.getViolationLeft() )
                   return -1;
               return 0;
           }
        });
        
        for( int i = 0; i < vars.size(); ++i ) {
            ArrayList<Restricao> rest = vars.get(i).getIn();
            for( int j = 0; j < rest.size(); ++j )  {
                // evita que restrições redundantes sejam inseridas
                if( rest.get(j).getLeft().getBloco() != this  )
                    in.add(rest.get(j));
            }
                
        }
        return in;
    }
    
    public PriorityQueue<Restricao> heapifyOutConstraints() {
        out = new PriorityQueue<>(vars.size(), new Comparator<Restricao>() {
           @Override
           public int compare(Restricao a, Restricao b) {
               if( a.getViolationRight()< b.getViolationRight() )
                   return -1;
               else if( a.getViolationRight() > b.getViolationRight() )
                   return 1;
               return 0;
               
           }
        });
        
        for( int i = 0; i < vars.size(); ++i ) {
            ArrayList<Restricao> rest = vars.get(i).getOut();
            for( int j = 0; j < rest.size(); ++j )  {
                // evita que restrições redundantes sejam inseridas
                if( rest.get(j).getLeft().getBloco() != this )
                    out.add(rest.get(j));
            }
                
        }
        return in;
    }
    
    
    public Restricao getMinOutConstraint() {
        Restricao constraint = out.peek();
        
        // o autor diz que é importante reduzir restrições redundantes quando encontra-las:
        /**
         * The only slight catch is that some of the constraints in b.in may be internal constraints, i.e. constraints
         * which are between variables in the same block. Such internal constraints are removed from the queue when encountered.
         */
        while( constraint != null && constraint.getLeft().getBloco() == constraint.getRight().getBloco() ) {
            out.poll();
            constraint = out.peek();
        }       
        
        return constraint;
    }

    public Restricao getMinInConstraint() {
        Restricao constraint = in.peek();
        
        // o autor diz que é importante reduzir restrições redundantes quando encontra-las
        while( constraint != null && constraint.getLeft().getBloco() == constraint.getRight().getBloco() ) {
            in.poll();            
            constraint = in.peek();
        }        
        
        return constraint;
    }

    public void removeMinInConstraint() {
        in.poll();
    }
    public void removeMinOutConstraint() {
        out.poll();
    }
    
    public PriorityQueue<Restricao> getIn() {
        return in;
    }
    
    public PriorityQueue<Restricao> getOut() {
        return out;
    }

    public ArrayList<Variavel> getVars() {
        return vars;
    }

    public void setDeleted(boolean b) {
        deleted = b;
    }
    
    public boolean getDeleted() {
        return deleted;
    }

    
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
    
    public void setWPosn(double wposn) {
        this.wposn = wposn;
    }

    public double getWPosn() {
        return wposn;
    }

    
    public void addInSplitBlock(Bloco b, Variavel v) {
        b.addVar(v);
        
        // visita a subarvore da esquerda
        for( int i = 0; i < v.getIn().size(); ++i ) {
            if( v.getIn().get(i).getLeft().getBloco() == this && v.getIn().get(i).getAtiva() )
                addInSplitBlock(b, v.getIn().get(i).getLeft());            
        }
        
        // visita a subarvore da direita
        for( int i = 0; i < v.getOut().size(); ++i ) {
            if( v.getOut().get(i).getRight().getBloco() == this && v.getOut().get(i).getAtiva() )
                addInSplitBlock(b, v.getOut().get(i).getRight());            
        }
    }
    
    /**
     * compDfDv - Busca em profundidade nas restrições ativas do bloco
     * somando v.weight * (posn(v)-v.des)
     * @param v
     * @param u
     * @return 
     */
    private double compDfDv(Variavel v, Variavel u) {
        double dfdv = v.getWeight() * (v.getPosition()-v.getDes());
        
        for( int i = 0; i < v.getOut().size(); ++i ) {
            if( v.getOut().get(i).getRight().getBloco() == this && v.getOut().get(i).getAtiva() &&
                v.getOut().get(i).getRight() != u ) {
                v.getOut().get(i).setLm(compDfDv(v.getOut().get(i).getRight(), v));
                dfdv += v.getOut().get(i).getLm();
                if( menor == null ) {
                    menor = v.getOut().get(i);                    
                }
                if( menor.getLm() > v.getOut().get(i).getLm() ) {
                    menor = v.getOut().get(i);                    
                }
            }
        }
        
        for( int i = 0; i < v.getIn().size(); ++i ) {
            if( v.getIn().get(i).getLeft().getBloco() == this && v.getIn().get(i).getAtiva() && 
                v.getIn().get(i).getLeft() != u ) {
                v.getIn().get(i).setLm(-compDfDv(v.getIn().get(i).getLeft(), v));
                dfdv -= v.getIn().get(i).getLm();
                if( menor == null ) {
                    menor = v.getIn().get(i);                    
                }
                if( menor.getLm() > v.getIn().get(i).getLm() ) {
                    menor = v.getIn().get(i);
                    
                }
            }
        }        
        
        return dfdv;
    }
    
   

    public Restricao findMinLM() {
       
        // inicializa o lm com 0
        for( int i = 0; i < vars.size(); ++i ) {
            for( int j  = 0; j < vars.get(i).getIn().size(); ++j )
                vars.get(i).getIn().get(j).setLm(0);
            for( int j  = 0; j < vars.get(i).getOut().size(); ++j )
                vars.get(i).getOut().get(j).setLm(0);
        }        
        
        menor = null;
        // computa o diferencial para cada bloco.
        compDfDv(vars.get(0), null);
        return menor;
    }

    public void mergeBlockLeft(Bloco bloco, Restricao r, double distancia) {
        // como agora u + a = v, essa restrição torna-se ativa        
        r.setAtiva(true);
        
        wposn = wposn + bloco.getWPosn() - distancia*bloco.getWeight();
        weight = weight + bloco.getWeight();
        posn = wposn/weight;
        
        for( int i = 0; i < bloco.getVars().size(); ++i ) {
            bloco.getVars().get(i).setBloco(this);
            bloco.getVars().get(i).setOffset(bloco.getVars().get(i).getOffset()+distancia);
            vars.add(bloco.getVars().get(i));
        }
        
        // remove possíveis restrições redundantes
        getMinInConstraint();
        bloco.getMinInConstraint();
        in.addAll(bloco.getIn());       
        
        bloco.setDeleted(true);
    }
    
    public void mergeBlockRight(Bloco bloco, Restricao r, double distancia) {
        // como agora u + a = v, essa restrição torna-se ativa
        r.setAtiva(true);
        wposn = wposn + bloco.getWPosn() - distancia*bloco.getWeight();
        weight = wposn + bloco.getWeight();
        posn = wposn/weight;
        
        for( int i = 0; i < bloco.getVars().size(); ++i ) {
            bloco.getVars().get(i).setBloco(this);
            bloco.getVars().get(i).setOffset(bloco.getVars().get(i).getOffset()+distancia);
            vars.add(bloco.getVars().get(i));
        }        
        
        // remove possiveis restrições redundantes
        getMinOutConstraint();
        bloco.getMinOutConstraint();
        out.addAll(bloco.getOut());
        
        bloco.setDeleted(true);
    }

}

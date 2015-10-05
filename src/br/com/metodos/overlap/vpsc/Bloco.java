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
    private ArrayList<Variavel> vars;
    private double posn;
    private double weight;
    private double wposn;
    private long timeStamp;
    private PriorityQueue<Restricao> in;
    private PriorityQueue<Restricao> out;
    private boolean deleted;
    private Restricao menor = null;
    
    public Bloco() {
        vars = new ArrayList<>();
          timeStamp = 0;
        weight = 0;
        wposn = 0;
        in = null;
        out = null;
        deleted =false;
    }
    
    public Bloco(Variavel v) { 
        vars = new ArrayList<>();
        timeStamp = 0;
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
    
    public long getTimeStamp() {
        return timeStamp;
    }
    
    public void setTimeStamp(long time) {
        timeStamp = time;
    }    
    
    public PriorityQueue<Restricao> heapifyInConstraints() {
        //in = new PairingHeap();
        in = new PriorityQueue<>(vars.size(), new Comparator<Restricao>() {
           @Override
           public int compare(Restricao a, Restricao b) {
               if( a.getViolation() < b.getViolation() )
                   return 1;
               else if( a.getViolation() > b.getViolation() )
                   return -1;
               return 0;
               
               
               
           }
        });
        
        for( int i = 0; i < vars.size(); ++i ) {
            ArrayList<Restricao> rest = vars.get(i).getIn();
            for( int j = 0; j < rest.size(); ++j )  {
                Restricao r = rest.get(j);
                r.setTimeStamp(timeStamp);
                if( r.getLeft().getBloco() != this && in != null || r.getRight().getBloco() != this && in == null )
                    in.add(r);
            }
                
        }
        return in;
    }
    
    public PriorityQueue<Restricao> heapifyOutConstraints() {
        //in = new PairingHeap();
        out = new PriorityQueue<>(vars.size(), new Comparator<Restricao>() {
           @Override
           public int compare(Restricao a, Restricao b) {
               if( a.getViolation() < b.getViolation() )
                   return -1;
               else if( a.getViolation() > b.getViolation() )
                   return 1;
               return 0;
               
           }
        });
        
        for( int i = 0; i < vars.size(); ++i ) {
            ArrayList<Restricao> rest = vars.get(i).getOut();
            for( int j = 0; j < rest.size(); ++j )  {
                Restricao r = rest.get(j);
                r.setTimeStamp(timeStamp);
                if( r.getLeft().getBloco() != this && out != null || r.getRight().getBloco() != this && out == null )
                    out.add(r);
            }
                
        }
        return in;
    }
    
    
    
    
    public Restricao getMinOutConstraint() {
        if( out.isEmpty() ) 
            return null;
        Restricao r = out.peek();
        while( r.getLeft().getBloco() == r.getRight().getBloco() ) {
            out.poll();
            if( out.isEmpty() )
                return null;
            r = out.peek();
        }
        return r;
    }

    public Restricao getMinInConstraint() {
        Restricao rest = null;
        ArrayList<Restricao> forDelete = new ArrayList<>();
        
        while( !in.isEmpty() ) {
            rest = in.peek();
            System.out.println(rest.getLeft().getId()+"  "+rest.getRight().getId());
            Bloco lb = rest.getLeft().getBloco();
            Bloco rb = rest.getRight().getBloco();
            
            if( lb == rb )  {// restrição foi juntada no mesmo bloco... 
                in.poll();
            } else if( rest.getTimeStamp() < lb.getTimeStamp() ) {
                in.poll();
                forDelete.add(rest);
            } else 
                break;
        }
                
        for( int i = 0; i < forDelete.size(); ++i ) {
            rest = forDelete.get(i);
            rest.setTimeStamp(timeStamp);
            
            in.add(rest);
        }
        
        if( in.isEmpty() )
            return null;
        return in.peek();
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

    public void merge(Bloco bloco, Restricao r, double distancia) {
        r.setAtiva(true);
        wposn += bloco.getWPosn() - distancia*bloco.getWeight();
        weight += bloco.getWeight();
        posn = wposn/weight;
        
        for( int i = 0; i < bloco.getVars().size(); ++i ) {
            bloco.getVars().get(i).setBloco(this);
            bloco.getVars().get(i).setOffset(bloco.getVars().get(i).getOffset()+distancia);
            vars.add(bloco.getVars().get(i));
        }
        bloco.setDeleted(true);
    }

    public void mergeIn(Bloco bloco) {
        getMinInConstraint();
        bloco.getMinInConstraint();
        in.addAll(bloco.getIn());               
    }
    
    public void mergeOut(Bloco bloco) {
        getMinOutConstraint();
        bloco.getMinOutConstraint();
        out.addAll(bloco.getOut());
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

    
    public void populate(Bloco b, Variavel v, Variavel u) {
        b.addVar(v);
        for( int i = 0; i < v.getIn().size(); ++i ) {
            if( v.getIn().get(i).getLeft().getBloco() == this && v.getIn().get(i).getAtiva() && u != v.getIn().get(i).getLeft() )
                populate(b, v.getIn().get(i).getLeft(), v);            
        }
        
        for( int i = 0; i < v.getOut().size(); ++i ) {
            if( v.getOut().get(i).getRight().getBloco() == this && v.getOut().get(i).getAtiva() && u != v.getOut().get(i).getRight() )
                populate(b, v.getOut().get(i).getRight(), v);            
        }
    }

    public void split(Bloco lb, Bloco rb, Restricao c) {
        c.setAtiva(false);
        populate(lb, c.getLeft(), c.getRight());
        populate(rb, c.getRight(), c.getLeft());
        
    }
    
    private void resetActiveLm(Variavel v, Variavel u) {
        for( int i = 0; i < v.getOut().size(); ++i ) {
            if( v.getOut().get(i).getRight().getBloco() == this && v.getOut().get(i).getAtiva() &&
                v.getOut().get(i).getRight() != u ) {
                
                v.getOut().get(i).setLm(0);
                resetActiveLm(v.getOut().get(i).getRight(), v);
            }
        }
        
        for( int i = 0; i < v.getIn().size(); ++i ) {
            if( v.getIn().get(i).getLeft().getBloco() == this && v.getIn().get(i).getAtiva() && 
                v.getIn().get(i).getLeft() != u ) {
                
                v.getIn().get(i).setLm(0);
                resetActiveLm(v.getIn().get(i).getLeft(), v);
            }
        }    
    }
    
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
       
        resetActiveLm(vars.get(0), null);
        
        menor = null;
        compDfDv(vars.get(0), null);
        if( menor != null )
            System.out.println("ATENCAO, ESTOU RETORNANDO...");
        
        return menor;
    }

}

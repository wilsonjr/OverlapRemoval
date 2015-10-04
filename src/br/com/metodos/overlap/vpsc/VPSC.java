/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

import br.com.metodos.utils.Retangulo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author Wilson
 */
public class VPSC {
    
    public static ArrayList<Retangulo> apply(ArrayList<Retangulo> rectangles, double epsX, double epsY) {
        ArrayList<Retangulo> projected = new ArrayList<>();
        
        
        return projected;        
    }
    
    public static void solveVPSC(ArrayList<Variavel> vars, ArrayList<Restricao> res) {
        Blocos blocos = new Blocos(vars);
        
        satisfyVPSC(blocos, vars, res);
        do {
            
            for( int i = 0; i < blocos.size(); ++i ) {
                blocos.get(i).heapifyInConstraints();
                blocos.get(i).heapifyOutConstraints();
            }
            
            Restricao r1 = blocos.get(0).findMinLM();
            int idx = 0;
            for( int i = 1; i < blocos.size(); ++i ) {
                Restricao r2 = blocos.get(i).findMinLM();
                if( r1 == null || r2 != null && r1.getLm() > r2.getLm() )  {
                    r1 = r2;                
                    idx = i;
                }
            }
            if( r1 == null || r1.getLm() >= 0 )
                break;
            
            Bloco lb = new Bloco();
            Bloco rb = new Bloco();
            blocos.restrict_block(blocos.get(idx), lb, rb, r1);

            for( int j = blocos.size()-1; j >= 0; --j )
                if( blocos.get(j).getDeleted() )
                    blocos.remove(j);
            
            
        } while( true );
        
    }
    
    
    
    private static void satisfyVPSC(Blocos blocos, ArrayList<Variavel> vars, ArrayList<Restricao> res) {
        for( int i = 0; i < res.size(); ++i )
            res.get(i).setAtiva(false);
    
        ArrayList<Variavel> varsOrdered = blocos.totalOrder();
        
        for( int i = 0; i < varsOrdered.size(); ++i )
            if( !varsOrdered.get(i).getBloco().getDeleted()) 
                blocos.mergeLeft(varsOrdered.get(i).getBloco());                
                             
        
        for( int i = blocos.size()-1; i >= 0; --i )
                if( blocos.get(i).getDeleted() )
                    blocos.remove(i);
    }
    
    
    private static int geraRestricoesVerticais(ArrayList<Retangulo> retangulos, ArrayList<Variavel> vars, ArrayList<Restricao> restricoes) {
        ArrayList<Event> eventos = new ArrayList<>();
        
        for( int i = 0; i < retangulos.size(); ++i ) {
            vars.get(i).setDesiredPosition(retangulos.get(i).getCenterY());            
            System.out.println(">> "+retangulos.get(i).getCenterY());
            No no = new No(vars.get(i), retangulos.get(i), retangulos.get(i).getCenterY());
            
            eventos.add(new Event("OPEN", no, retangulos.get(i).getMinX()));
            eventos.add(new Event("CLOSE", no, retangulos.get(i).getMaxX()));
        }
          
        Collections.sort(eventos, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                if( o1.getPosition() > o2.getPosition() ) 
                    return 1;
                else if( o1.getPosition() < o2.getPosition() )
                    return -1;
                else if( o1.getNo().getRect() == o2.getNo().getRect() ) {
                    
                    if( o1.getTipo().equals("OPEN") )
                        return -1;
                    return 1;  
                } else
                
                return 0;
            }
        });
        
        
      
          
        TreeSet<No> scanline = new TreeSet<>(new NoComparator());        
        for( int i = 0; i < eventos.size(); ++i ) {
            No v = eventos.get(i).getNo();
            
            if( eventos.get(i).getTipo().equals("OPEN") ) {
                // só preciso olhar um elemento para cima e para baixo...
                scanline.add(v);
                               
                No lower = null, higher = null;
                Iterator<No> it = scanline.iterator();
                while( it.hasNext() ) {
                    No no = it.next();
                    if( no == v ) {
                        if( it.hasNext() )
                            higher = it.next();                        
                        break;
                    }
                    lower = no;
                }
                
                No u =  lower;///scanline.lower(v);
                if( u != null ) {                    
                    v.setAboveNeighbour(u);
                    u.setBelowNeighbour(v);
                } 
                u = higher;//scanline.higher(v);
                if( u != null ) {
                    v.setBelowNeighbour(u);
                    u.setAboveNeighbour(v);
                } 
                System.out.println();
            } else {
                
                No a = v.getAboveNeighbour();
                if( a != null ) {
                    double separation = (v.getRect().getHeight()+a.getRect().getHeight())/2.;
                    restricoes.add(new Restricao(a.getVar(), v.getVar(), separation));
                    a.setBelowNeighbour(v.getBelowNeighbour()); //  PRA QUE???
                }
                
                No b = v.getBelowNeighbour();
                if( b != null ) {
                    double separation = (v.getRect().getHeight()+b.getRect().getHeight())/2.;
                    restricoes.add(new Restricao(v.getVar(), b.getVar(), separation));
                    b.setAboveNeighbour(v.getAboveNeighbour());
                }
                
                
                scanline.remove(v);
            }
        }
        
        
        return restricoes.size();
    }
    
    public static int geraRestricoesHorizontais(ArrayList<Retangulo> retangulos, ArrayList<Variavel> vars, ArrayList<Restricao> restricoes) {
        ArrayList<Event> eventos = new ArrayList<>();
        
        for( int i = 0; i < retangulos.size(); ++i ) {
            vars.get(i).setDesiredPosition(retangulos.get(i).getCenterX());            
            No no = new No(vars.get(i), retangulos.get(i), retangulos.get(i).getCenterX());
            eventos.add( (new Event("OPEN", no, retangulos.get(i).getMinY())));
            eventos.add( (new Event("CLOSE", no, retangulos.get(i).getMaxY())));
        }
        
        
        
        Collections.sort(eventos, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                if( o1.getNo().getRect() == o2.getNo().getRect() ) {
                    if( o1.getTipo().equals("OPEN") )
                        return -1;
                    return 1;
                } else 
                    return new Double(o1.getPosition()).compareTo(o2.getPosition());
            }
        });
        
        
        
        TreeSet<No> scanline = new TreeSet<>(new NoComparator());
        for( int i = 0; i < eventos.size(); ++i ) {
            
            No v = eventos.get(i).getNo();            
            if( eventos.get(i).getTipo().equals("OPEN") ) {
                
                scanline.add(v);
                
                TreeSet<No> leftNeighbours = getLeftNeighbours(scanline, v);                
                TreeSet<No> rightNeighbours = getRightNeighbours(scanline, v);               
                
                
                v.setLeftNeighbours(leftNeighbours);
                v.setRightNeighbours(rightNeighbours);
                
            } else { /* ei.kind == close*/
                
                Iterator<No> it = v.getLeftNeighbours().iterator();
                while( it.hasNext() ) {
                    
                    No u = it.next();                    
                    double separation = (v.getRect().getWidth()+u.getRect().getWidth())/2.;
                    
                    if( canAddConstraint(restricoes, u.getVar(), v.getVar(), separation) ) {                    
                        restricoes.add(new Restricao(u.getVar(), v.getVar(), separation));
                        u.getRightNeighbours().remove(v);                    
                    }
                }
                
                it = v.getRightNeighbours().iterator();
                while( it.hasNext() ) {
                    No u = it.next();                    
                    double separation = (v.getRect().getWidth()+u.getRect().getWidth())/2.;
                    if( canAddConstraint(restricoes, v.getVar(), u.getVar(), separation) )  {
                        restricoes.add(new Restricao(v.getVar(), u.getVar(), separation));
                        u.getLeftNeighbours().remove(v);
                    }
                }
                
                scanline.remove(v);
                removeScanline(scanline, v);
            }
        }
        
        return restricoes.size();
    }
    
    
    private static void removeScanline(TreeSet<No> scanline, No v) {
        Iterator<No> it = scanline.iterator();
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
    
    private static TreeSet<No> getLeftNeighbours(TreeSet<No> scanline, No v) {
        TreeSet<No> leftv = new TreeSet<>(new NoComparator());
        
        No lower = null;
        Iterator<No> it = scanline.descendingIterator();
        
        while( it.hasNext() ) {
            No no = it.next();
            if( no == v ) {
                break;
            }
            
        }
        No u = null;
        while( it.hasNext() ) {
            u = it.next();//scanline.floor(v);//lower;//scanline.lower(v);
            
            if( !u.getDeleted() )
                break;
            u = null;
        }
        while( u != null ) {      
            
            if( u.getRect().olapX(v.getRect()) <= 0 ) {
                leftv.add(u);
                return leftv;
            }            
            if( u.getRect().olapX(v.getRect()) <= u.getRect().olapY(v.getRect()) ) 
                leftv.add(u);
            
            u = null;
            while( it.hasNext() ) {
                u = it.next();//scanline.floor(u);//lower;//scanline.lower(u);
                if( !u.getDeleted() ) 
                    break;
                u = null;
                
            }
        }
        
        return leftv;
    }

    private static TreeSet<No> getRightNeighbours(TreeSet<No> scanline, No v) {
        TreeSet<No> rightv = new TreeSet<>(new NoComparator());
        
        No higher = null;
        Iterator<No> it = scanline.iterator();
        while( it.hasNext() ) {
            No no = it.next();
            if( no == v ) {
                break;
            }            
        }
        
        No u = null;
        while( it.hasNext() ) {
            u = it.next();//scanline.ceiling(v);//higher;//scanline.higher(v);
            if( !u.getDeleted() )
                break;
            u = null;
        }
        while( u != null ) {
            if( u.getRect().olapY(v.getRect()) <= 0 ) {
                rightv.add(u);
                return rightv;
            }            
            
            if( u.getRect().olapX(v.getRect()) <= u.getRect().olapY(v.getRect()) ) 
                rightv.add(u);            
             
            u = null;
            while( it.hasNext() ) {
                u = it.next();//scanline.ceiling(u);//higher; //scanline.higher(u);
                
                if( !u.getDeleted() )
                    break;
                u = null;
            }
        }
                
        return rightv;
    }

    private static boolean canAddConstraint(ArrayList<Restricao> restricoes, Variavel v1, Variavel v2, double separation) {
        
        for( int i = 0; i < restricoes.size(); ++i )
            if( restricoes.get(i).getLeft() == v1 && restricoes.get(i).getRight() == v2 && restricoes.get(i).getGap() == separation )
                return false;
        
        
        return true;
    }
}

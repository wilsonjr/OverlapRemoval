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
import br.com.metodos.utils.Util;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author Wilson
 */
public class VPSC {
    
    public static ArrayList<Retangulo> apply(ArrayList<Retangulo> rectangles, double epsX, double epsY) {
        /**
         * copy rectangles 
         */         
        ArrayList<Retangulo> projected = new ArrayList<>();
        for( Retangulo r: rectangles )  
            projected.add(new Retangulo(r.getUX(), r.getUY(), r.getWidth(), r.getHeight()));
        
        /**
         *  init all variables
         */         
        ArrayList<Variavel> vars = new ArrayList<>();
        for( int i = 0; i < projected.size(); ++i ) 
            vars.add(new Variavel(i));
                
        /**
         * generate horizontal constraints and solve vpsc for x
         */
        ArrayList<Restricao> restricoes = new ArrayList<>();
        int count = VPSC.geraRestricoesHorizontais(projected, vars, restricoes);
        
        // solve vpsc for x
        VPSC.solveVPSC(vars, restricoes);
        
        // move rectangles in x coordinate
        for( int i = 0; i < projected.size(); ++i ) 
            projected.get(i).moveX(vars.get(i).getPosition());
              
        // init all variables         
        for( int i = 0; i < vars.size(); ++i ) 
            vars.get(i).init(i);
        
        // generate constraint for y coordinate
        restricoes = new ArrayList<>();
        count = VPSC.geraRestricoesVerticais(projected, vars, restricoes);
        
        // solve vpsc for y and move rectangles in y direction to remove all overlap remaining
        VPSC.solveVPSC(vars, restricoes);
        for( int i = 0; i < projected.size(); ++i )  
            projected.get(i).moveY(vars.get(i).getPosition());           
        
        return projected;      
        
        
        
           
    }
    
    public static void solveVPSC(ArrayList<Variavel> vars, ArrayList<Restricao> res) {
        Blocos blocos = new Blocos(vars);
        
        satisfyVPSC(blocos, vars, res);
        int limit = 0;
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
            
            
        } while( limit++ < 1000 );
        
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
    
    
    
    
    public static int geraRestricoesVerticais(ArrayList<Retangulo> retangulos, ArrayList<Variavel> vars, ArrayList<Restricao> restricoes) {
        Event eventos[] = new Event[2*retangulos.size()];
        int j = 0;
        for( int i = 0; i < retangulos.size(); ++i ) {
            vars.get(i).setDesiredPosition(retangulos.get(i).getCenterY());            
            No no = new No(vars.get(i), retangulos.get(i), retangulos.get(i).getCenterY());
            
            eventos[j++] = (new Event("OPEN", no, retangulos.get(i).getUX()));
            eventos[j++] = (new Event("CLOSE", no, retangulos.get(i).getLX()));
        }
          
       Util.quickSort(eventos, 0, eventos.length-1);

        for( int i = 1; i < eventos.length; ++i )
            if( eventos[i-1].getNo().getRect() == eventos[i].getNo().getRect() && eventos[i-1].getTipo().equals("OPEN") ) {
                Event temp = eventos[i-1];           
                eventos[i-1] = eventos[i];      
                eventos[i] = temp;               
            }
        
          
        TreeSet<No> scanline = new TreeSet<>(new NoComparator());        
        for( int i = 0; i < eventos.length; ++i ) {
            No v = eventos[i].getNo();
            
            if( eventos[i].getTipo().equals("OPEN") ) {
                // só preciso olhar um elemento para cima e para baixo...
                scanline.add(v);
                
                /**
                 * Finds the first lower element than v
                 */
                Iterator<No> descendingIt = scanline.descendingIterator();        
                while( descendingIt.hasNext() ) {
                    No no = descendingIt.next();
                    if( no == v ) 
                        break;
                }
                No lower = null;
                while( descendingIt.hasNext() ) {
                    lower = descendingIt.next();
                    if( !lower.getDeleted() )
                        break;
                    lower = null;
                }
                
                No u =  lower;
                if( u != null ) {                    
                    v.setAboveNeighbour(u);
                    u.setBelowNeighbour(v);
                }
                
                /**
                 * Finds the first higher element than v
                 */
                
                Iterator<No> ascendingIt = scanline.iterator();
                while( ascendingIt.hasNext() ) {
                    No no = ascendingIt.next();
                    if( no == v )
                        break;
                }

                No higher = null;
                while( ascendingIt.hasNext() ) {
                    higher = ascendingIt.next();
                    if( !higher.getDeleted() )
                        break;
                    higher = null;
                }
                u = higher;
                if( u != null ) {
                    v.setBelowNeighbour(u);
                    u.setAboveNeighbour(v);
                } 
                
            } else {
                
                No a = v.getAboveNeighbour();
                if( a != null ) {
                    double separation = (v.getRect().getHeight()+a.getRect().getHeight())/2.;
                    restricoes.add(new Restricao(a.getVar(), v.getVar(), separation));
                    a.setBelowNeighbour(v.getBelowNeighbour());
                }
                
                No b = v.getBelowNeighbour();
                if( b != null ) {
                    double separation = (v.getRect().getHeight()+b.getRect().getHeight())/2.;
                    restricoes.add(new Restricao(v.getVar(), b.getVar(), separation));
                    b.setAboveNeighbour(v.getAboveNeighbour());
                }
                
                
                //scanline.remove(v);
                removeScanline(scanline, v);
            }
        }
        
        
        return restricoes.size();
    }
    
    
    
    
    public static int geraRestricoesHorizontais(ArrayList<Retangulo> retangulos, ArrayList<Variavel> vars, ArrayList<Restricao> restricoes) {
        /**
         * Adiciona os eventos de abertura e fechamento dos retangulos 
         */
        Event[] eventos = new Event[retangulos.size()*2];
        for( int i = 0, j = 0; i < retangulos.size(); ++i ) {
            vars.get(i).setDesiredPosition(retangulos.get(i).getCenterX());            
            No no = new No(vars.get(i), retangulos.get(i), retangulos.get(i).getCenterX());
            eventos[j++] = new Event("OPEN", no, retangulos.get(i).getUY());
            eventos[j++] = new Event("CLOSE", no, retangulos.get(i).getLY());            
        }
        
        // [e1,..., e2n] := events sorted by posn
        Util.quickSort(eventos, 0, eventos.length-1);
        
        // elementos consecutivos que fazem parte do mesmo retangulo, o evento OPEN deve vir primeiro
        for( int i = 1; i < eventos.length; ++i ) 
            if( eventos[i-1].getNo().getRect() == eventos[i].getNo().getRect() && eventos[i-1].getTipo().equals("OPEN") ) {
                Event temp = eventos[i-1];           
                eventos[i-1] = eventos[i];      
                eventos[i] = temp;               
            }        
        
        /**
         * Essa linha de varredura tem um problema.
         * Nao conseguimos remover um elemento com o método remove, pois seu comparator nunca retorna 0.
         * Ou seja, é como se não houvesse elemento igual ao procurado, assim é necessário fazer uma busca ad-hoc e utilizar
         * um flag para elementos removidos.
         */
        TreeSet<No> scanline = new TreeSet<>(new NoComparator());
        for( int i = 0; i < eventos.length; ++i ) {
            
            No v = eventos[i].getNo();            
            if( eventos[i].getTipo().equals("OPEN") ) {
                
                scanline.add(v);
                
                System.out.print("elementos: ");
                
                Iterator<No> it = scanline.iterator();
                while( it.hasNext() ) {
                    
                    No u = it.next();             
                    if( !u.getDeleted() )
                        System.out.print(u.getPosition()+" ");
                    
                }
                System.out.println();
                
                TreeSet<No> leftNeighbours = getLeftNeighbours(scanline, v);                
                TreeSet<No> rightNeighbours = getRightNeighbours(scanline, v);               
                
                System.out.println(v.getVar().getId()+": Founded "+leftNeighbours.size()+" in left and "+rightNeighbours.size()+" in right");
                
                v.setLeftNeighbours(leftNeighbours);
                v.setRightNeighbours(rightNeighbours);
                
            } else { /* ei.kind == close*/
                System.out.println("Close: "+v.getVar().getId());
                Iterator<No> it = v.getLeftNeighbours().iterator();
                while( it.hasNext() ) {
                    
                    No u = it.next();             
                    double separation = (v.getRect().getWidth()+u.getRect().getWidth())/2.;

                    if( !u.getDeleted() && canAddConstraint(restricoes, u.getVar(), v.getVar(), separation) ) {                    
                        restricoes.add(new Restricao(u.getVar(), v.getVar(), separation));
                        u.removeRightNeighbour(v);
                    }
                    
                }
                
                it = v.getRightNeighbours().iterator();
                while( it.hasNext() ) {
                    No u = it.next();         
                    double separation = (v.getRect().getWidth()+u.getRect().getWidth())/2.;
                    if( !u.getDeleted() && canAddConstraint(restricoes, v.getVar(), u.getVar(), separation) )  {
                        restricoes.add(new Restricao(v.getVar(), u.getVar(), separation));
                        u.removeLeftNeighbour(v);
                        //removeScanline(u.getLeftNeighbours(), v);
                    }
                    
                }
                
                //scanline.remove(v);
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
        
        
        Iterator<No> it = scanline.descendingIterator();        
        while( it.hasNext() ) {
            No no = it.next();
            if( no == v )
                break;
        }
        
        No u = null;
        while( it.hasNext() ) {
            u = it.next();
            
            if( !u.getDeleted() )
                break;
            u = null;
        }
        
        while( u != null ) {      
            
            if( u.getRect().olapX(v.getRect()) <= 0 ) {
                leftv.add(u);
                System.out.println("left inseriu 1");
                return leftv;
            }            
            if( u.getRect().olapX(v.getRect()) <= u.getRect().olapY(v.getRect()) )  {
                leftv.add(u);
                System.out.println("left inseriu 2");
            }
            
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
        System.out.println("origin pos: "+v.getPosition());
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
            System.out.println("pos : "+u.getPosition());
            System.out.println(">> "+u.getRect().olapX(v.getRect())+" "+u.getRect().olapY(v.getRect()));
            if( u.getRect().olapX(v.getRect()) <= 0.000 ) {
                rightv.add(u);
                System.out.println("right inseriu 1");
                return rightv;
            }            
            
            
            if( u.getRect().olapX(v.getRect()) <= u.getRect().olapY(v.getRect()) )  {
                rightv.add(u);            
                System.out.println("right inseriu 2");
            }
             
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
        
    //    for( int i = 0; i < restricoes.size(); ++i )
    //        if( restricoes.get(i).getLeft() == v1 && restricoes.get(i).getRight() == v2 && restricoes.get(i).getGap() == separation )
    //            return false;
        
        
        return true;
    }
}

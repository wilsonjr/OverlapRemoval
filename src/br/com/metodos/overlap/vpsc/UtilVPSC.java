/*
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
 * @author Thaís
 */
public class UtilVPSC {
  
    
    
    public static void solveVPSC(ArrayList<Variavel> vars, ArrayList<Restricao> res) {
        for( int i = 0; i < res.size(); ++i ) {
            res.get(i).setAtiva(false);
        }
        
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
    
    
    
    public static void satisfyVPSC(Blocos blocos, ArrayList<Variavel> vars, ArrayList<Restricao> res) {
        for( int i = 0; i < res.size(); ++i ) {
            res.get(i).setAtiva(false);
        }
        
        
        
        ArrayList<Variavel> varsOrdered = blocos.totalOrder();
        
        for( int i = 0; i < varsOrdered.size(); ++i ) {
            if( !varsOrdered.get(i).getBloco().getDeleted()) {                
                blocos.mergeLeft(varsOrdered.get(i).getBloco());                
            } 
                
        }
        for( int i = blocos.size()-1; i >= 0; --i )
                if( blocos.get(i).getDeleted() )
                    blocos.remove(i);
    }
    
    
    public static int geraRestricoesVerticais(ArrayList<Retangulo> retangulos, ArrayList<Variavel> vars, ArrayList<Restricao> restricoes) {
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
    
    
    public static void mquickSort(Event array[], int start, int end)
    {
            int i = start;                          // index of left-to-right scan
            int k = end;                            // index of right-to-left scan

            if (end - start >= 1)                   // check that there are at least two elements to sort
            {
                    Event pivot = array[start];       // set the pivot as the first element in the partition

                    while (k > i)                   // while the scan indices from left and right have not met,
                    {
                            while (array[i].getPosition() <= pivot.getPosition() && i <= end && k > i)  // from the left, look for the first
                                    i++;                                    // element greater than the pivot
                            while (array[k].getPosition() > pivot.getPosition() && k >= start && k >= i) // from the right, look for the first
                                k--;                                        // element not greater than the pivot
                            if (k > i)                                       // if the left seekindex is still smaller than
                                    swap(array, i, k);                      // the right index, swap the corresponding elements
                    }
                    swap(array, start, k);          // after the indices have crossed, swap the last element in
                                                    // the left partition with the pivot 
                    mquickSort(array, start, k - 1); // quicksort the left partition
                    mquickSort(array, k + 1, end);   // quicksort the right partition
            }
            else    // if there is only one element in the partition, do not do any sorting
            {
                    return;                     // the array is sorted, so exit
            }
    }

    public static void swap(Event array[], int index1, int index2) 
    // pre: array is full and index1, index2 < array.length
    // post: the values at indices 1 and 2 have been swapped
    {
            Event temp = array[index1];           // store the first value in a temp
            array[index1] = array[index2];      // copy the value of the second into the first
            array[index2] = temp;               // copy the value of the temp into the second
    }
    
    
    public static int geraRestricoesHorizontais(ArrayList<Retangulo> retangulos, ArrayList<Variavel> vars, ArrayList<Restricao> restricoes) {
      //  ArrayList<Event> eventos = new ArrayList<>();
        Event eventos[] = new Event[retangulos.size()*2];
        int c = 0;
        for( int i = 0; i < retangulos.size(); ++i ) {
            vars.get(i).setDesiredPosition(retangulos.get(i).getCenterX());
            
            No no = new No(vars.get(i), retangulos.get(i), retangulos.get(i).getCenterX());
            
            eventos[c++] = (new Event("OPEN", no, retangulos.get(i).getMinY()));
            eventos[c++] = (new Event("CLOSE", no, retangulos.get(i).getMaxY()));
        }
        
        
        
        /*Collections.sort(eventos, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                if( o1.getNo().getRect() == o2.getNo().getRect() ) {
                    if( o1.getTipo().equals("OPEN") )
                        return -1;
                    return 1;
                } else 
                    return new Double(o1.getPosition()).compareTo(new Double(o2.getPosition()));
            }
        });*/
        mquickSort(eventos, 0, eventos.length-1);
        
        
        TreeSet<No> scanline = new TreeSet<>(new NoComparator());
        for( int i = 0; i < eventos.length; ++i ) {
            
            No v = eventos[i].getNo();            
            if( eventos[i].getTipo().equals("OPEN") ) {
                
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

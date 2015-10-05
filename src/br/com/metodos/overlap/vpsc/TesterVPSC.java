/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;


import br.com.metodos.utils.Retangulo;
import java.util.ArrayList;

/*
 *
 * @author wilson
 */
public class TesterVPSC {
    public static void main(String... args) {
        ArrayList<Retangulo> rects = new ArrayList<>();
//        rects.add(new Retangulo(1, 2, 3-1, 4-2));
//        rects.add(new Retangulo(2, 3, 5-2, 5-3));
//        rects.add(new Retangulo(9, 3, 11-9, 5-3));
//        rects.add(new Retangulo(4, 4, 8-4, 8-4));
//        rects.add(new Retangulo(7, 6, 9-7, 8-6));
//        rects.add(new Retangulo(3, 7, 5-3, 10-7));
//        rects.add(new Retangulo(11, 7, 13-11, 9-7));
//        rects.add(new Retangulo(7, 9, 9-7, 11-9));
//        rects.add(new Retangulo(12, 9, 14-12, 11-9));
        
        rects.add(new Retangulo(250,  201, 50, 50));
    rects.add(new Retangulo(235,  213, 50, 50));
    rects.add(new Retangulo(234,  190, 50, 50));
        
        
        ArrayList<Variavel> vars = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i ) 
            vars.add(new Variavel(i));
        
        ArrayList<Restricao> restricoes = new ArrayList<>();
        int m = VPSC.geraRestricoesHorizontais(rects, vars, restricoes);
        /*double oldx[] = new double[rects.size()];
        for( int i = 0; i < oldx.length; ++i ) 
            oldx[i] = vars.get(i).getDes();                    */
        System.out.println("Numero de restricoes horizontais: "+m);
        for( Restricao r: restricoes )
            System.out.println(r);        
        VPSC.solveVPSC(vars, restricoes);      
        //UtilVPSC.satisfyVPSC(new Blocos(vars), vars, restricoes);
        for( int i = 0; i < vars.size(); ++i ) {
          System.out.println(vars.get(i).getPosition());
           rects.get(i).moveX(vars.get(i).getPosition());
        }
       
      
        for( int i = 0; i < vars.size(); ++i ) 
            vars.get(i).init(i);
        restricoes = new ArrayList<>();
        m = VPSC.geraRestricoesVerticais(rects, vars, restricoes);
        System.out.println("NUMERO DE RESTRICOES VERTICAIS: "+m);
        for( Restricao r: restricoes )
            System.out.println(r);
        VPSC.solveVPSC(vars, restricoes);
        for( int i = 0; i < rects.size(); ++i )  {
            rects.get(i).moveY(vars.get(i).getPosition());            
            System.out.println(vars.get(i).getPosition());
            
        }
       
        
        for( Retangulo r: rects )
            System.out.println(r);
    }
}

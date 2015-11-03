/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.projsnippet;

import br.com.grafos.ui.Menu;
import br.com.metodos.utils.Retangulo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author wilson
 */
public class ProjSnippet {
     public static ArrayList<Retangulo> e_o(ArrayList<Retangulo> retangulos) {
        ArrayList<Retangulo> projected = new ArrayList<>();        
        try {
            File file = new File("points.rect");
        
            if( !file.exists() )
                file.createNewFile();
            
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for( Retangulo r: retangulos )
                bw.write(r.getUX()+" "+r.getLY()+" "+r.getWidth()+" "+r.getHeight()+"\n");
            bw.close();
            
            
            Process p = null;
            try {
                //p = Runtime.getRuntime().exec("C:\\Python27\\python.exe teste_minimization.py");
                //p = Runtime.getRuntime().exec("cmd /c teste_nlopt.exe");
                p = Runtime.getRuntime().exec("cmd /c energia_sobreposicao.exe");
            } catch (IOException ex) {
                Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            int waitFor = 1;
            try {  
                waitFor = p.waitFor();
                System.out.println(waitFor);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProjSnippet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if( waitFor == 0 ) {
                
                Scanner scn = new Scanner(new File("point_solve.rect"));
                int idx = 0;
                while( scn.hasNext() ) {
                    double ux = Double.parseDouble(scn.nextLine());
                    if( scn.hasNext() ) {
                        double ly = Double.parseDouble(scn.nextLine());
                        double uy = ly-retangulos.get(idx).getHeight();
                        projected.add(new Retangulo(ux, uy, retangulos.get(idx).getWidth(), retangulos.get(idx).getHeight()));
                    }
                }
                System.out.println("size: "+projected.size());
                 return projected;
            }
            
            
            
            
        } catch( IOException e ) {
            
        }
        
        
        
        return null;
       
     }
     
     public static void main(String[] args) {
         System.out.println("ola");
     }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.utils;

import br.com.metodos.overlap.prism.PRISMEdge;
import br.com.metodos.overlap.prism.PRISMPoint;
import br.com.metodos.overlap.vpsc.Event;
import java.util.ArrayList;

/**
 *
 * @author wilson
 */
public class Util {
    private static double E = 10e-6;
    private static double EPS = 10e-4;
    private static int LIMIT = 10000;
    public static final double ZERO = 0.0000000;
    private static boolean finished;
    
    
    public static boolean getFinished() {
        return finished;
    }
    
    /**
     * Euclidean distance between two points
     * @param p1x x coordinate of first point
     * @param p1y y coordinate of first point
     * @param p2x x coordinate of second point
     * @param p2y y coordinate of second point
     * @return distance between two points
     */
    public static double distanciaEuclideana(double p1x, double p1y, double p2x, double p2y) {
        return Math.sqrt(Math.pow(p1x-p2x, 2.) + Math.pow(p1y-p2y, 2.));
    }
    
    public static ArrayList<Retangulo> toRetangulo(ArrayList<RetanguloVis> rects) {
        ArrayList<Retangulo> rs = new ArrayList<>();
        
        for( RetanguloVis r: rects ) 
            rs.add(new Retangulo(r.getUX(), r.getUY(), r.getWidth(), r.getHeight()));
        
        return rs;
    }
        
    public static void toRetanguloVis(ArrayList<RetanguloVis> ori, ArrayList<Retangulo> rects) {
        
        for( int i = 0; i < rects.size(); ++i ) {
            ori.get(rects.get(i).getId()).setUX(rects.get(i).getUX());
            ori.get(rects.get(i).getId()).setUY(rects.get(i).getUY());
            ori.get(rects.get(i).getId()).setWidth(rects.get(i).getWidth());
            ori.get(rects.get(i).getId()).setHeight(rects.get(i).getHeight());
        }   
    }
        
    public static void quickSort(Event array[], int start, int end) {
        int i = start;                          
        int k = end;                            

        if( end - start >= 1 ) {
            Event pivot = array[start];       

            while( k > i ) {
                while( array[i].getPosition() <= pivot.getPosition() && i <= end && k > i )  
                    i++;                                    
                while( array[k].getPosition() > pivot.getPosition() && k >= start && k >= i ) 
                    k--;                                        
                if( k > i)                                      
                     swap(array, i, k);                                    
            }
            swap(array, start, k);          
            quickSort(array, start, k - 1);
            quickSort(array, k + 1, end);  
        }
        
    }

    private static void swap(Event array[], int index1, int index2)  {
            Event temp = array[index1];           
            array[index1] = array[index2];      
            array[index2] = temp;               
    }
    
    
     public static double tij(Retangulo u, Retangulo v) {
         
        return 
            Math.max(
            Math.min(
               (u.getWidth()/2. + v.getWidth()/2.)/Math.abs(u.getCenterX() - v.getCenterX()), 
               (u.getHeight()/2. + v.getHeight()/2.)/Math.abs(u.getCenterY() - v.getCenterY())
            ), 1);
    }
    
    public static double sij(Retangulo u, Retangulo v) {
        
        return Math.min(tij(u, v), 1.5);
    }
    
    public static double dij(PRISMEdge e) {
        return sij(e.getU().getRect(), e.getV().getRect())*
                Util.distanciaEuclideana(e.getU().getRect().getCenterX(), e.getU().getRect().getCenterY(), 
                                         e.getV().getRect().getCenterX(), e.getV().getRect().getCenterY());
    }
    
    public static double wij(PRISMEdge e) {
        return Math.pow(dij(e), -2.);
    }
    
    
//    private static boolean isSolution(double[][] v) {
//        double norma1 = Math.abs(v[2][0] - v[1][0]), norma2 = Math.abs(v[2][0]);
//        
//        for( int j = 1; j < v[0].length; ++j ) {
//            if( Math.abs(v[2][j] - v[1][j]) > norma1 )
//                norma1 = Math.abs(v[2][j]-v[1][j]);
//            if( Math.abs(v[2][j]) > norma2 )
//                norma2 = Math.abs(v[2][j]);
//        }
//        
//        double res = norma1/norma2;
//        return res <= E;
//    }
    
    private static boolean isSolution(double[] residuo) {
        double maior = Math.abs(residuo[0]);
        for( int i = 1; i < residuo.length; ++i )
            if( maior < Math.abs(residuo[i]) )
                maior = Math.abs(residuo[i]);
        return maior <= E;
    }
    
    
    private static void multMatrizVetor(double[][] A, double[] b, double[] v, double[] r) {
        for( int i = 0; i < A.length; ++i ) {
            r[i] = b[i];
            for( int j = 0; j < A.length; ++j )
                r[i] += A[i][j]*v[j];
        }
    }
    
    private static void multMatrizVetor(double[][] A, double[] v, double[] r) {
        for( int i = 0; i < A.length; ++i ) {
            r[i] = 0;
            for( int j = 0; j < A.length; ++j )
                r[i] += A[i][j]*v[j];        
        }    
    }
    
    private static double innerProduct(double[] r1, double[] r2) {
        double prod = 0;
        for( int i = 0; i < r1.length; ++i )
            prod += (r1[i]*r2[i]);
        return prod;
    }
    private static void atribuiVector(double[] des, double[] ori) {
        for( int i = 0; i < ori.length; ++i )
            des[i] = ori[i];
    }
             
    public static boolean gradConjugados(double[][] A, double[][] r, double[][] v, double[][] p, double[] b) {
        
        multMatrizVetor(A, b, v[0], r[0]);
        
        for( int i = 0; i < r[0].length; ++i )
            p[0][i] = -r[0][i];
        
        double[] r0product = new double[r[0].length];
        atribuiVector(r0product, r[0]);
        
        double r0r0 = innerProduct(r[0], r0product);
        
        double[] Ar0 = new double[A.length];
        multMatrizVetor(A, r[0], Ar0);
        
        double Ar0r0 = innerProduct(Ar0, r[0]);
        
        double q0 = r0r0/Ar0r0;
        
        double[] Ap0 = new double[A.length];
        
        multMatrizVetor(A, p[0], Ap0);
        for( int i = 0; i < A.length; ++i ) {
            v[1][i] = v[0][i] + q0*p[0][i];
            r[1][i] = r[0][i] + q0*Ap0[i];
        }
        
        int count = 0;
        
        double[] r1 = new double[r[1].length];
        double[] r0 = new double[r[0].length];
        double[] Ap1 = new double[A.length];
        
        do {
            atribuiVector(r1, r[1]);            
            atribuiVector(r0, r[0]);
            
            double r1r1 = innerProduct(r1, r[1]);
            r0r0 = innerProduct(r0, r[0]);            
            double alpha = r1r1/r0r0;
            
            for( int i = 0; i < A.length; ++i )
                p[1][i] = -r[1][i] + alpha*p[0][i];
            
            multMatrizVetor(A, p[1], Ap1);
            double Ap1p1 = innerProduct(Ap1, p[1]);
            double qk = r1r1/Ap1p1;
            
            for( int i = 0; i < A.length; ++i ) {
                v[2][i] = v[1][i] + qk*p[1][i];
                r[2][i] = r[1][i] + qk*Ap1[i];
            }
            
            if( isSolution(r[2]) )
                return true;
                    
            for( int i = 0; i < A.length; ++i ) {
                r[0][i] = r[1][i];
                r[1][i] = r[2][i];
                
                v[0][i] = v[1][i];
                v[1][i] = v[2][i];
                
                p[0][i] = p[1][i];
            }
            
            
        } while( ++count < LIMIT );
        
        
        return false;
    }
    
    
    public static PRISMPoint[] stressMajorization(PRISMEdge[] edges, PRISMPoint[] layout) {
        finished = false;
        double[][] lw = generateLw(edges, layout.length);
        double[] x = new double[layout.length];
        double[] y = new double[layout.length];
       
        double stress0 = stress(edges);
        System.out.println("old stress: "+stress0);
        int limit = 10000;
        
        do {
            double[][] lz = generateLz(edges, layout.length);
            
            for( int i = 0; i < layout.length; ++i ) {
                x[i] = layout[i].getRect().getCenterX();
                y[i] = layout[i].getRect().getCenterY();                        
            }
            
            /** solve for x **/
            double[] bx = LzX(lz, x);
            double[][] matrizx = new double[lw.length][lw.length];
            for( int i = 0; i < lw.length; ++i )
                for( int j = 0; j < lw.length; ++j )
                    matrizx[i][j] = lw[i][j];
            
            double[][] rx = new double[3][lw.length];
            double[][] vx = new double[3][lw.length];
            double[][] px = new double[3][lw.length];
                        
            for( int i = 0; i < bx.length; ++i )
                bx[i] = -bx[i];
            
            boolean retorno = Util.gradConjugados(matrizx, rx, vx, px, bx);
            if( retorno )
                System.out.println("Gradiente Conjugado para X convergiu");
            else
                System.out.println("Gradiente Conjugado para X não convergiu");
            
            
            /** solve for y **/
           double[] by = LzX(lz, y);
           double[][] matrizy = new double[lw.length][lw.length];
           for( int i = 0; i < lw.length; ++i )
               for( int j = 0; j < lw.length; ++j )
                   matrizy[i][j] = lw[i][j];
           
            double[][] ry = new double[3][lw.length];
            double[][] vy = new double[3][lw.length];
            double[][] py = new double[3][lw.length];
            
            for( int i = 0; i < by.length; ++i )
                by[i] = -by[i];
            
            retorno = Util.gradConjugados(matrizy, ry, vy, py, by);
            if( retorno )
                System.out.println("Gradiente Conjugado para Y convergiu");
            else
                System.out.println("Gradiente Conjugado para Y não convergiu");
            
            for( int i = 0; i < by.length; ++i ) {
                layout[i].getRect().setUX(vx[2][i]-layout[i].getRect().getWidth()/2.);
                layout[i].getRect().setUY(vy[2][i]-layout[i].getRect().getHeight()/2.);
            }
            
            double stress1 = stress(edges);
            System.out.println("new stress: "+stress1);
            if( Math.abs(stress0-stress1) < stress1*EPS )
                return layout;
            stress0 = stress1;
            
            
            boolean flag = true;
            for( int i = 0; i < edges.length && flag; ++i ) 
                if( Util.tij(edges[i].getU().getRect(), edges[i].getV().getRect()) != 1.00000000000 )
                    flag = false;
            if( flag ) {
                finished = true;
                return layout;
            }
        } while( limit-- > 0 );
        
        return null;
        
    }
    
    
    private static double[] LzX(double[][] lz, double[] xy) {
        double[] r = new double[xy.length];        
        for( int i = 0; i < lz.length; ++i ) {
            r[i] = 0;
            for( int j = 0; j < lz.length; ++j )
                r[i] += lz[i][j]*xy[j];
        }
        
        return r;            
    }
    
    private static double[][] generateLw(PRISMEdge[] edges, int n) {
        double[][] temp = new double[n][n];        
        double[][] lw = new double[n][n];
        for( int i = 0; i < temp.length; ++i )
            for( int j = 0; j < temp.length; ++j )
                temp[i][j] = lw[i][j] = 0.0;
        
        for( int i = 0; i < edges.length; ++i ) {
            temp[edges[i].getU().getIdx()][edges[i].getV().getIdx()] = Util.wij(edges[i]);
            temp[edges[i].getV().getIdx()][edges[i].getU().getIdx()] = temp[edges[i].getU().getIdx()][edges[i].getV().getIdx()];
        }
        
        for( int i = 0;i < lw.length; ++i ) {
            double w_ik = 0;
            for( int j = 0; j < lw.length; ++j ) {
                if( i != j ) {
                    double w_ij = temp[i][j];
                    if( w_ij != Util.ZERO ) {
                        w_ik += w_ij;
                        lw[i][j] = -w_ij;
                    }
                }
            }
            
            lw[i][i] = w_ik;
        }
        
        return lw;
    }
    
    
    private static double[][] generateLz(PRISMEdge[] edges, int n) {
        double[][] temp = new double[n][n];        
        double[][] lz = new double[n][n];
        for( int i = 0; i < temp.length; ++i )
            for( int j = 0; j < temp.length; ++j )
                temp[i][j] = lz[i][j] = 0.0;
        
        for( int i = 0; i < edges.length; ++i ) {
            double a = Math.abs(edges[i].getU().getRect().getCenterX() - edges[i].getV().getRect().getCenterX());
            double b = Math.abs(edges[i].getU().getRect().getCenterY() - edges[i].getV().getRect().getCenterY());
            
            double c = //Util.distanciaEuclideana(edges[i].getU().getRect().getCenterX(), 
                       //                         edges[i].getU().getRect().getCenterY(), 
                       //                         edges[i].getV().getRect().getCenterX(), 
                       //                         edges[i].getV().getRect().getCenterY());
                    Math.max(a, b);
            temp[edges[i].getU().getIdx()][edges[i].getV().getIdx()] = -Util.wij(edges[i])*Util.dij(edges[i])*( c == 0 ? 0.0 : 1./c);
            temp[edges[i].getV().getIdx()][edges[i].getU().getIdx()] = temp[edges[i].getU().getIdx()][edges[i].getV().getIdx()];
        }
        
        for( int i = 0; i < lz.length; ++i ) {
            double Lz_ii = 0;
            for( int j = 0; j < lz.length; ++j ) {
                if( i != j ) {
                    double delta_ij_inv = temp[i][j];
                    if( delta_ij_inv != Util.ZERO ) {
                        Lz_ii += delta_ij_inv;
                        lz[i][j] = delta_ij_inv;
                    }
                }
            }            
            lz[i][i] = -Lz_ii;
        }
        
        return lz;
    }
    
    private static double stress(PRISMEdge[] v) {
        double s = 0;
        
        for( int i = 0; i < v.length; ++i ) {
            double a = Math.abs(v[i].getU().getRect().getCenterX() - v[i].getV().getRect().getCenterX());
            double b = Math.abs(v[i].getU().getRect().getCenterY() - v[i].getV().getRect().getCenterY());
            double c = Math.max(a, b);
            System.out.println(Util.tij(v[i].getU().getRect(), v[i].getV().getRect()));
            s += Util.wij(v[i]) * Math.pow(c - Util.dij(v[i]), 2);
        }
        
        return s;
    }
    
}

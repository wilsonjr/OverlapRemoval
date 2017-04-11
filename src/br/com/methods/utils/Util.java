package br.com.methods.utils;

import Jama.Matrix;
import br.com.methods.overlap.prism.PRISMEdge;
import br.com.methods.overlap.prism.PRISMPoint;
import br.com.methods.overlap.prism.SetPoint;
import br.com.methods.overlap.vpsc.Event;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Classe utilizada para auxiliar nos métodos de remoção de sobreposição.
 * @author wilson
 */
public class Util {
    private static final double E = 10e-6;
    private static final double EPS = 10e-4;
    private static final int LIMIT = 10000;
    public  static final double ZERO = 0.0000000;
    private static boolean finished;
    private static TreeSet<Integer> lwZeroRow;
    private static TreeSet<Integer> lzZeroRow;
    
    /**
     * Verifica se o método PRISM já pode ser parado
     * @return true se sim,
     *         falso caso contrário.
     */
    public static boolean getFinished() {
        return finished;
    }
    
    /**
     * Calcula a distância euclideana entre dois pontos
     * @param p1x coordenada x do primeiro ponto
     * @param p1y coordenada y do primeiro ponto
     * @param p2x coordenada x do segundo ponto
     * @param p2y coordenada y do segundo ponto
     * @return distance between two points
     */
    public static double euclideanDistance(double p1x, double p1y, double p2x, double p2y) {
        return Math.sqrt(Math.pow(p1x-p2x, 2.) + Math.pow(p1y-p2y, 2.));
    }
    
    /**
     * Converte uma lista de objetos RetanguloVis em uma lista de OverlapRect.
     * @param rects RetangulosVis a ser convertido
     * @return Lista de Retângulos
     */
    public static ArrayList<OverlapRect> toRectangle(ArrayList<RetanguloVis> rects) {
        ArrayList<OverlapRect> rs = new ArrayList<>();
        rects.forEach(r-> {
            rs.add(new OverlapRect(r.getUX(), r.getUY(), r.getWidth(), r.getHeight(), r.isPivot(), r.getLevel(), r.getCluster(), r.getHealth(), r.numero));
        });
        
        return rs;
    }
        
    /**
     * Converte uma lista de objetos OverlapRect em uma lista de RetanguloVis.
     * @param ori Lista de RetanguloVis original
     * @param rects Lista de OverlapRect com as novas coordenadas
     */
    public static void toRectangleVis(ArrayList<RetanguloVis> ori, ArrayList<OverlapRect> rects) {
        
        for( int i = 0; i < rects.size(); ++i ) {    
            ori.get(i).setUX(rects.get(i).getUX());
            ori.get(i).setUY(rects.get(i).getUY());
            ori.get(i).setWidth(rects.get(i).getWidth());
            ori.get(i).setHeight(rects.get(i).getHeight());
            ori.get(i).setPivot(rects.get(i).isPivot());
            ori.get(i).setLevel(rects.get(i).getLevel());
            ori.get(i).numero = rects.get(i).getId();
            ori.get(i).setHealth(rects.get(i).getHealth());
        }   
    }
     
    /**
     * Quicksort dos eventos a ser ordenada por sua posição
     * @param array Coleção de eventos a ser ordenada
     * @param start Índice inicial
     * @param end Índice final
     */
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

    /**
     * Função para troca de posições
     * @param array Coleção de eventos
     * @param index1 Índice do primeiro elemento
     * @param index2 Índice do segundo elemento
     */
    private static void swap(Event array[], int index1, int index2)  {
        Event temp = array[index1];           
        array[index1] = array[index2];      
        array[index2] = temp;               
    }
        
    /**
     * Calcula o fator de sobreposição entre dois retângulos.
     * @param u Retângulo 1
     * @param v Retângulo 2
     * @return double
     */
    public static double tij(OverlapRect u, OverlapRect v) {
         
        return 
            Math.max(
            Math.min(
               (u.getWidth()/2. + v.getWidth()/2.)/Math.abs(u.getCenterX() - v.getCenterX()), 
               (u.getHeight()/2. + v.getHeight()/2.)/Math.abs(u.getCenterY() - v.getCenterY())
            ), 1);
    }
    
    /**
     * Controla o fator de sobreposição entre dois Retangulos.
     * Isso é necessário para que em cada iteração do método PRISM, a sobreposição não seja removida
     * abruptamente.
     * @param u
     * @param v
     * @return 
     */
    public static double sij(OverlapRect u, OverlapRect v) {        
        return Math.min(tij(u, v), 1.5);
    }
    
    /**
     * dij definida em Stress Majorization:
     * 
     * @param e Aresta PRISM
     * @return double
     */
    public static double dij(PRISMEdge e) {
        return sij(e.getU().getRect(), e.getV().getRect())*
                Util.euclideanDistance(e.getU().getRect().getCenterX(), e.getU().getRect().getCenterY(), 
                                         e.getV().getRect().getCenterX(), e.getV().getRect().getCenterY());
    }
    
    /**
     * wij definida em Stress Majorization:
     * 
     * @param e Aresta PRISM
     * @return double
     */
    public static double wij(PRISMEdge e) {
        return Math.pow(dij(e), -2.);
    }
        
    /**
     * Verifica se a iteração do método Gradiente Conjudado é uma solução.
     * @param residuo Resíduo gerado pela iteração
     * @return true se é solução,
     *         false caso contrário.
     */
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
    
    private static void multMatrixVector(YaleMatrix A, double[] b, double[] v, double[] r) {
        for( int i = 0, j = 0; i < b.length; ++i ) {
            r[i] = b[i];
            if( !lwZeroRow.contains(i) ) { // verificacao necessaria, uma linha inteira pode ser nula
                for( int k = A.ia[j]; k < A.ia[j+1]; k++ )
                    r[i] += A.a[k]*v[A.ja[k]];
                j++;
            }
        }
    }
    
    private static void multMatrixVector(double[][] A, double[] v, double[] r) {
        for( int i = 0; i < A.length; ++i ) {
            r[i] = 0;
            for( int j = 0; j < A.length; ++j )
                r[i] += A[i][j]*v[j];        
        }    
    }
    
    private static void multMatrixVector(YaleMatrix A, double[] v, double[] r) {
        for( int i = 0, j = 0; i < r.length; ++i ) {
            r[i] = 0.0;
            if( !lwZeroRow.contains(i) ) {
                for( int k = A.ia[j]; k < A.ia[j+1]; k++ ) 
                    r[i] += A.a[k]*v[A.ja[k]];               
                j++;
            }
        }    
    }
    
    public static double innerProduct(double[] r1, double[] r2) {
        double prod = 0;
        for( int i = 0; i < r1.length; ++i )
            prod += (r1[i]*r2[i]);
        return prod;
    }
    
    private static void setVector(double[] des, double[] ori) {
        for( int i = 0; i < ori.length; ++i )
            des[i] = ori[i];
    }
             
    /**
     * Método dos Gradientes Conjugados para solução de sistema linear
     * @param A Matriz A de ordem nxn
     * @param r Vetor auxiliar 1 de ordem 3xn
     * @param v Vetor auxiliar 2 de ordem 3xn
     * @param p Vetor auxiliar 3 de ordem 3xn
     * @param b Vetor solução de ordem n
     * @return 
     */
    public static boolean conjugateGradient(double[][] A, double[][] r, double[][] v, double[][] p, double[] b) {
        multMatrizVetor(A, b, v[0], r[0]);
        
        for( int i = 0; i < r[0].length; ++i )
            p[0][i] = -r[0][i];
        
        double[] r0product = new double[r[0].length];
        setVector(r0product, r[0]);
        
        double r0r0 = innerProduct(r[0], r0product);
        
        double[] Ar0 = new double[A.length];
        Util.multMatrixVector(A, r[0], Ar0);

        double Ar0r0 = innerProduct(Ar0, r[0]);
        
        double q0 = r0r0/Ar0r0;
        
        double[] Ap0 = new double[A.length];
        
        Util.multMatrixVector(A, p[0], Ap0);
            
        for( int i = 0; i < A.length; ++i ) {
            v[1][i] = v[0][i] + q0*p[0][i];
            r[1][i] = r[0][i] + q0*Ap0[i];
        }
                
        int count = 0;
        
        double[] r1 = new double[r[1].length];
        double[] r0 = new double[r[0].length];
        double[] Ap1 = new double[A.length];
        
        do {
            setVector(r1, r[1]);            
            setVector(r0, r[0]);
            
            double r1r1 = innerProduct(r1, r[1]);
            r0r0 = innerProduct(r0, r[0]);            
            double alpha = r1r1/r0r0;

            for( int i = 0; i < A.length; ++i )
                p[1][i] = -r[1][i] + alpha*p[0][i];

            Util.multMatrixVector(A, p[1], Ap1);

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
    
    /**
     * Método dos Gradientes Conjugados para solução de sistema linear usando Matriz de Yale
     * @param A Matriz A de com estrutura de Yale
     * @param r Vetor auxiliar 1 de ordem 3xn
     * @param v Vetor auxiliar 2 de ordem 3xn
     * @param p Vetor auxiliar 3 de ordem 3xn
     * @param b Vetor solução de ordem n
     * @return 
     */
    public static boolean conjugateGradient(YaleMatrix A, double[][] r, double[][] v, double[][] p, double[] b) {
        int n = b.length;
        Util.multMatrixVector(A, b, v[0], r[0]);
        
        for( int i = 0; i < n; ++i )
            p[0][i] = -r[0][i];
                
        double[] r0product = new double[n];
        setVector(r0product, r[0]);
        
        double r0r0 = innerProduct(r[0], r0product);
        
        double[] Ar0 = new double[n];
        multMatrixVector(A, r[0], Ar0);
                
        double Ar0r0 = innerProduct(Ar0, r[0]);
        
        double q0 = r0r0/Ar0r0;
        
        double[] Ap0 = new double[n];
        
        multMatrixVector(A, p[0], Ap0);
        
        for( int i = 0; i < n; ++i ) {
            v[1][i] = v[0][i] + q0*p[0][i];
            r[1][i] = r[0][i] + q0*Ap0[i];
        }
        
        int count = 0;
        
        double[] r1 = new double[n];
        double[] r0 = new double[n];
        double[] Ap1 = new double[n];
        
        do {
            setVector(r1, r[1]);            
            setVector(r0, r[0]);
            
            double r1r1 = innerProduct(r1, r[1]);
            r0r0 = innerProduct(r0, r[0]);            
            double alpha = r1r1/r0r0;

            for( int i = 0; i < n; ++i )
                p[1][i] = -r[1][i] + alpha*p[0][i];
            
            multMatrixVector(A, p[1], Ap1);            
            
            double Ap1p1 = innerProduct(Ap1, p[1]);
            double qk = r1r1/Ap1p1;
                        
            for( int i = 0; i < n; ++i ) {
                v[2][i] = v[1][i] + qk*p[1][i];
                r[2][i] = r[1][i] + qk*Ap1[i];
            }
                        
            if( isSolution(r[2]) )
                return true;
                    
            for( int i = 0; i < n; ++i ) {
                r[0][i] = r[1][i];
                r[1][i] = r[2][i];
                
                v[0][i] = v[1][i];
                v[1][i] = v[2][i];
                
                p[0][i] = p[1][i];
            }
            
            
        } while( ++count < LIMIT );
        
        
        return false;
    }
        
    /**
     * Aplica Stress Majorization nas arestas passadas
     * @param edges Arestas formadas pelo método PRISM
     * @param layout Projeção corrente da iteração do método PRISM
     * @return 
     */
    public static PRISMPoint[] stressMajorization(PRISMEdge[] edges, PRISMPoint[] layout) {
        finished = false;
        double[][] lw = generateLw(edges, layout.length);
        double[] x = new double[layout.length];
        double[] y = new double[layout.length];
       
        double stress0 = stress(edges);
        int limit = layout.length;
        
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
                        
            for( int i = 0; i < bx.length; ++i ) {
                bx[i] = -bx[i];
            }

            boolean retorno = Util.conjugateGradient(matrizx, rx, vx, px, bx);            
            
            /** solve for y **/
           double[] by = LzX(lz, y);
           double[][] matrizy = new double[lw.length][lw.length];
           for( int i = 0; i < lw.length; ++i )
               for( int j = 0; j < lw.length; ++j )
                   matrizy[i][j] = lw[i][j];
           
            double[][] ry = new double[3][lw.length];
            double[][] vy = new double[3][lw.length];
            double[][] py = new double[3][lw.length];
            
            for( int i = 0; i < by.length; ++i ) {
                by[i] = -by[i];
            }
        
            retorno = Util.conjugateGradient(matrizy, ry, vy, py, by);
            for( int i = 0; i < by.length; ++i ) {
                layout[i].getRect().setUX(vx[2][i]-layout[i].getRect().getWidth()/2.);
                layout[i].getRect().setUY(vy[2][i]-layout[i].getRect().getHeight()/2.);
            }
            
            double stress1 = stress(edges);
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
        
    private static double[] LzX(YaleMatrix lz, double[] xy) {
        double[] r = new double[xy.length];        
        for( int i = 0, j = 0; i < xy.length; ++i ) {
            r[i] = 0;
            if( !lzZeroRow.contains(i)  ) {
                for( int k = lz.ia[j]; k < lz.ia[j+1]; ++k )
                    r[i] += lz.a[k]*xy[lz.ja[k]];
                j++;
            }
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
            double value = Util.wij(edges[i]);
            temp[edges[i].getU().getIdx()][edges[i].getV().getIdx()] = value;
            temp[edges[i].getV().getIdx()][edges[i].getU().getIdx()] = value;
        }
        
        for( int i = 0; i < lw.length; ++i ) {
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
    
    private static YaleMatrix generateLwYale(PRISMEdge[] edges, int n) {
        TreeSet<SetPoint> setPoint = new TreeSet<>((SetPoint o1, SetPoint o2) -> {
            if( o1.getI() < o2.getI() )
                return -1;
            else if( o1.getI() > o2.getI() ) {
                return 1;
            } else {
                if( o1.getJ() < o2.getJ() )
                    return -1;
                else if( o1.getJ() > o2.getJ() )
                    return 1;
                
                return 0;
            }
        });
        
        for( int i = 0; i < edges.length; ++i ) {
            double value = Util.wij(edges[i]);
            setPoint.add(new SetPoint(edges[i].getU().getIdx(), edges[i].getV().getIdx(), value));
            setPoint.add(new SetPoint(edges[i].getV().getIdx(), edges[i].getU().getIdx(), value));
        }
        
        return formLwYaleMatrix(setPoint, n);
    }
        
    private static YaleMatrix formLwYaleMatrix(TreeSet<SetPoint> setPoint, int n) {
        lwZeroRow = new TreeSet<>();
        ArrayList<Double> A = new ArrayList<>();
        ArrayList<Integer> JA = new ArrayList<>();
        ArrayList<Integer> IA = new ArrayList<>();
        
        double[] diagonal = new double[n];  
        for( int i = 0; i < diagonal.length; ++i )
            diagonal[i] = 0.0;
        boolean[] flag = new boolean[n];
        for( int i = 0; i < flag.length; ++i )
            flag[i] = false;
        boolean[] first = new boolean[n];
        for( int i = 0; i < first.length; ++i )
            first[i] = false;
        
        SetPoint[] points = setPoint.toArray(new SetPoint[setPoint.size()]);
        for( int i = 0; i < points.length; ++i ) 
            diagonal[points[i].getI()] += points[i].getValue();
        
        if( points.length > 0 && points[0].getI() != 0 ) {
            for( int j = 0; j < points[0].getI(); ++j ) {
                lwZeroRow.add(j);
            }
        }
        
        for( int i = 0; i < points.length; ++i ) {
            
            if( i > 0 ) {
                int linhaInicial = points[i-1].getI();
                int linhaFinal = points[i].getI();
                
                for( int j = linhaInicial+1; j < linhaFinal; ++j ) {
                    lwZeroRow.add(j);
                }
            }            
            
            SetPoint p = points[i];
            int linha = p.getI();
            int coluna = p.getJ();
            double w_ij = p.getValue();
            
            if( i > 0 && diagonal[points[i-1].getI()] != 0.0 && !flag[points[i-1].getI()] &&  points[i-1].getI() != linha ) {
                A.add(diagonal[points[i-1].getI()]);
                JA.add(points[i-1].getI());
                flag[points[i-1].getI()] = true;
            }
            
            if( w_ij != Util.ZERO ) {
                if( coluna >= linha && diagonal[linha] != 0.0 && !flag[linha] ) {
                    A.add(diagonal[linha]);
                    JA.add(linha);
                    
                    if( !first[linha] && (i == 0 || points[i-1].getI() != linha) ) {
                        IA.add(A.size()-1);
                        first[linha] = true;
                    }
                    
                    flag[linha] = true;
                }
                
                A.add(-w_ij);
                JA.add(coluna);
                if( !first[linha] && (i == 0 || points[i-1].getI() != linha) ) {
                    IA.add(A.size()-1);
                    first[linha] = true;
                }
            }
        }
        
        if( diagonal[points[points.length-1].getI()] != 0.0 ) {
            A.add(diagonal[points[points.length-1].getI()]);
            JA.add(points[points.length-1].getI());
        }
        
        if( points.length > 0 && points[points.length-1].getI() != n-1 ) {
            int linhaInicial = points[points.length-1].getI();
            for( int j = linhaInicial+1; j < n; ++j )
                lwZeroRow.add(j);
            
        }
        IA.add(A.size());
        
        return new YaleMatrix(A, JA, IA);
    }
    
    private static YaleMatrix generateLzYale(PRISMEdge[] edges, int n) {        
        TreeSet<SetPoint> setPoint = new TreeSet<>((SetPoint o1, SetPoint o2) -> {
            if( o1.getI() < o2.getI() )
                return -1;
            else if( o1.getI() > o2.getI() ) {
                return 1;
            } else {
                if( o1.getJ() < o2.getJ() )
                    return -1;
                else if( o1.getJ() > o2.getJ() )
                    return 1;
                
                return 0;
            }
        });
        
        for( int i = 0; i < edges.length; ++i ) {
            double a = Math.abs(edges[i].getU().getRect().getCenterX() - edges[i].getV().getRect().getCenterX());
            double b = Math.abs(edges[i].getU().getRect().getCenterY() - edges[i].getV().getRect().getCenterY());            
            double c = Math.max(a, b);
            double value = -Util.wij(edges[i]) * Util.dij(edges[i]) * ( c == 0 ? 0.0 : 1./c);
            setPoint.add(new SetPoint(edges[i].getU().getIdx(), edges[i].getV().getIdx(), value));
            setPoint.add(new SetPoint(edges[i].getV().getIdx(), edges[i].getU().getIdx(), value));
        }
        
        return formLzYaleMatrix(setPoint, n);
    }
      
    private static YaleMatrix formLzYaleMatrix(TreeSet<SetPoint> setPoint, int n) {
        lzZeroRow = new TreeSet<>();
        ArrayList<Double> A = new ArrayList<>();
        ArrayList<Integer> JA = new ArrayList<>();
        ArrayList<Integer> IA = new ArrayList<>();
        
        double[] diagonal = new double[n];  
        for( int i = 0; i < diagonal.length; ++i )
            diagonal[i] = 0.0;
        boolean[] flag = new boolean[n];
        for( int i = 0; i < flag.length; ++i )
            flag[i] = false;
        boolean[] first = new boolean[n];
        for( int i = 0; i < first.length; ++i )
            first[i] = false;
        
        SetPoint[] points = setPoint.toArray(new SetPoint[setPoint.size()]);
        for( int i = 0; i < points.length; ++i ) 
            diagonal[points[i].getI()] += points[i].getValue();
        
        if( points.length > 0 && points[0].getI() != 0 ) {
            for( int j = 0; j < points[0].getI(); ++j ) {
                lzZeroRow.add(j);
            }
        }
        
        for( int i = 0; i < points.length; ++i ) {
            
            if( i > 0 ) {
                int linhaInicial = points[i-1].getI();
                int linhaFinal = points[i].getI();
                
                for( int j = linhaInicial+1; j < linhaFinal; ++j )
                    lzZeroRow.add(j);                
            }
            
            
            SetPoint p = points[i];
            int linha = p.getI();
            int coluna = p.getJ();
            double w_ij = p.getValue();
            
            if( i > 0 && diagonal[points[i-1].getI()] != 0.0 && !flag[points[i-1].getI()] &&  points[i-1].getI() != linha ) {
                A.add(-diagonal[points[i-1].getI()]);
                JA.add(points[i-1].getI());
                flag[points[i-1].getI()] = true;
            }
            
            if( w_ij != Util.ZERO ) {
                if( coluna >= linha && diagonal[linha] != 0.0 && !flag[linha] ) {
                    A.add(-diagonal[linha]);
                    JA.add(linha);
                    
                    if( !first[linha] && (i == 0 || points[i-1].getI() != linha) ) {
                        IA.add(A.size()-1);
                        first[linha] = true;
                    }
                    
                    flag[linha] = true;
                }
                
                A.add(w_ij);
                JA.add(coluna);
                if( !first[linha] && (i == 0 || points[i-1].getI() != linha) ) {
                    IA.add(A.size()-1);
                    first[linha] = true;
                }
            }
        }
        
        if( diagonal[points[points.length-1].getI()] != 0.0 ) {
            A.add(-diagonal[points[points.length-1].getI()]);
            JA.add(points[points.length-1].getI());
        }
        
        if( points.length > 0 && points[points.length-1].getI() != n-1 ) {
            int linhaInicial = points[points.length-1].getI();
            for( int j = linhaInicial+1; j < n; ++j ) 
                lzZeroRow.add(j);            
        }
        
        IA.add(A.size());        
        
        return new YaleMatrix(A, JA, IA);
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
            double c = Math.max(a, b);
            
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
            s += Util.wij(v[i]) * Math.pow(c - Util.dij(v[i]), 2);
        }
        
        return s;
    }
    
    
    /**
     * Retorna a menor coordenada X da projeção.
     * @param rects Projeção
     * @return double
     */
    public static double getMinX(ArrayList<OverlapRect> rects) {
        double min = rects.get(0).getUX();
        
        for( int i = 1; i < rects.size(); ++i )
            min = Math.min(min, rects.get(i).getUX());
        
        return min;
    }
    
    /**
     * Retorna a menor coordenada Y da projeção.
     * @param rects Projeção
     * @return double
     */
    public static double getMinY(ArrayList<OverlapRect> rects) {
        double min = rects.get(0).getUY();
                
        for( int i = 1; i < rects.size(); ++i )
            min = Math.min(min, rects.get(i).getUY());
       
        return min;
    }
    
    /**
     * Faz com que nenhuma coordenada tenha valores negativos.
     * @param rects Projeção com coordenadas negativas.
     */
    public static void normalize(ArrayList<OverlapRect> rects) {
        double minX = getMinX(rects);
        double minY = getMinY(rects);
        if( minX < 0 || minY < 0 ) {
            for( int i = 0; i < rects.size(); ++i ) {
                if( minX < 0 )
                    rects.get(i).setUX(rects.get(i).getUX()-minX);
                if( minY < 0 )
                    rects.get(i).setUY(rects.get(i).getUY()-minY);
            }
        }
    }
        
    /**
     * Recupera a coordenada central da projeção
     * @param rects Projeção
     * @return Coordenadas {x, y}
     */
    public static double[] getCenter(ArrayList<OverlapRect> rects) {
        double xmin = rects.get(0).getUX(), xmax = rects.get(0).getLX(), 
               ymin = rects.get(0).getUY(), ymax = rects.get(0).getLY();                      
        
        for( OverlapRect r: rects ) {
            if( r.getUX() < xmin ) 
                xmin = r.getUX();
            if( r.getLX() > xmax )
                xmax = r.getLX();
            if( r.getUY() < ymin )
                ymin = r.getUY();
            if( r.getLY() > ymax )
                ymax = r.getLY();
        }
        
        /**
         * Compute the center of mass of set 
         */        
        double centerX = (xmin+xmax)/2;
        double centerY = (ymin+ymax)/2;
        
        
        double[] center = {centerX, centerY};
        return center;
    }
    
    /**
     * Realiza a translação da projeção.
     * @param rects Projeção corrente
     * @param ammountX Deslocamento em X
     * @param ammountY Deslocamento em Y
     */
    public static void translate(ArrayList<OverlapRect> rects, double ammountX, double ammountY) {
        for( int i = 0; i < rects.size(); ++i ) {
            rects.get(i).setUX(rects.get(i).getUX()+ammountX);
            rects.get(i).setUY(rects.get(i).getUY()+ammountY);
        }
    }
    
    /**
     * Aplica Stress Majorization nas arestas passadas utilizando a estrutura de Yale
     * @param edges Arestas formadas pelo método PRISM
     * @param layout Projeção corrente da iteração do método PRISM
     * @return 
     */
    public static PRISMPoint[] stressMajorizationYale(PRISMEdge[] edges, PRISMPoint[] layout) {
        finished = false;
        YaleMatrix lw = generateLwYale(edges, layout.length);
        
        double[] x = new double[layout.length];
        double[] y = new double[layout.length];
       
        double stress0 = stress(edges);
        int limit = layout.length;
        
        do {            
            YaleMatrix lz = generateLzYale(edges, layout.length);

            for( int i = 0; i < layout.length; ++i ) {
                x[i] = layout[i].getRect().getCenterX();
                y[i] = layout[i].getRect().getCenterY();                        
            }
            
            /** solve for x **/
            double[] bx = LzX(lz, x);
            
            YaleMatrix matrizx = new YaleMatrix(lw.a, lw.ja, lw.ia);
            
            double[][] rx = new double[3][layout.length];
            double[][] vx = new double[3][layout.length];
            double[][] px = new double[3][layout.length];
                        
            for( int i = 0; i < bx.length; ++i ) 
                bx[i] = -bx[i];
            
            boolean retorno = Util.conjugateGradient(matrizx, rx, vx, px, bx);            
            
            /** solve for y **/
            double[] by = LzX(lz, y);
           
            double[][] ry = new double[3][layout.length];
            double[][] vy = new double[3][layout.length];
            double[][] py = new double[3][layout.length];
            
            for( int i = 0; i < by.length; ++i ) 
                by[i] = -by[i];            
            
            YaleMatrix matrizy = new YaleMatrix(lw.a, lw.ja, lw.ia);
            retorno = Util.conjugateGradient(matrizy, ry, vy, py, by);
            
            for( int i = 0; i < by.length; ++i ) {
                layout[i].getRect().setUX(vx[2][i]-layout[i].getRect().getWidth()/2.);
                layout[i].getRect().setUY(vy[2][i]-layout[i].getRect().getHeight()/2.);
            }

            double stress1 = stress(edges);
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
    
    public static double polygonArea(Polygon p) {
        double area = 0;
        int x[] = p.xpoints;
        int y[] = p.ypoints;
        
        for( int i = 1; i+1 < x.length; ++i ) {
            int x1 = x[i] - x[0];
            int y1 = y[i] - y[0];
            int x2 = x[i+1] - x[0];
            int y2 = y[i+1] - y[0];
            int cross = x1*y2 - x2*y1;
            area += cross;
        }
        
        return Math.abs(area/2.0);
    }
    
    
    public static double[] extractParameters(double eps, ArrayList<OverlapRect> pivots, ArrayList<OverlapRect> elements) {
        double[] parameters = new double[2];
        int sum = 0;
        
        for( int i = 0; i < pivots.size(); ++i ) 
            sum += Util.elementsInRange(eps, pivots.get(i), elements);        
        parameters[0] = (double)sum/(double)pivots.size();
        
        parameters[1] = 0;
        for( int i = 0; i < elements.size(); ++i )
            if( elements.get(i).getHealth() >= 2 )
                parameters[1]++;
            
        
        return parameters;
    }
    
    private static int elementsInRange(double eps, OverlapRect r, ArrayList<OverlapRect> elements) {
        int qtd = 0;
        
        for( int i = 0; i < elements.size(); ++i ) {
            
            double distancia = Util.euclideanDistance(r.getCenterX(), r.getCenterY(), 
                    elements.get(i).getCenterX(), elements.get(i).getCenterY());
            if( distancia <= eps ) {
                qtd++;
                elements.get(i).increment();
            }
            
        }
        return qtd;
    }

    public static double rectToRectDistance(Rectangle2D.Double recti, Rectangle2D.Double rectj) {
        
        double dx = diff(recti.getMinX(), recti.getMaxX(), rectj.getMinX(), rectj.getMaxX());
        double dy = diff(recti.getMinY(), recti.getMaxY(), rectj.getMinY(), rectj.getMaxY());
        
        return Math.sqrt(dx*dx + dy*dy);        
    }
    
    private static double diff(double m1, double M1, double m2, double M2) {
        if( M1 <= m2 )
            return m2-M1;
        if( M2 <= m1 )
            return m1-M2;
        return 0;
    }

    public static double pointToLineDistance(Point2D.Double u, Point2D.Double v, Point2D.Double p) {
        Point2D.Double bc = new Point2D.Double(v.x-u.x, v.y-u.y);
        Point2D.Double ba = new Point2D.Double(p.x-u.x, p.y-u.y);
        
        double c1 = bc.x*ba.x + bc.y*ba.y;
        double c2 = bc.x*bc.x + bc.y*bc.y;
        double parameter = c1/c2;
        
        Point2D.Double res = new Point2D.Double(ba.x, ba.y);
        bc.setLocation(bc.x*parameter, bc.y*parameter);
        res.setLocation(res.x-bc.x, res.y-bc.y);
        
        return Util.euclideanDistance(res.x, res.y, 0, 0);
    }

    public static void computeDelaunayTriangulation(Rectangle2D.Double[] rects, Rectangle2D.Double[] initialPositions, 
            Point2D.Double[] points, List<List<Integer>> edges) {
        
        for( int i = 0; i < rects.length; ++i ) {
            edges.add(i, new ArrayList<>());
            
            Rectangle2D.Double temp = initialPositions[i];
            points[i] = new Point2D.Double(temp.getCenterX(), temp.getCenterY());
        }
        
        // is ijk a circle with no interior points?
        for( int i = 0; i < rects.length; ++i )
            for( int j = i+1; j < rects.length; ++j )
                for( int k = j+1; k < rects.length; ++k ) {
                    
                    boolean isTriangle = true;
                    for( int p = 0; isTriangle && p < rects.length; ++p ) 
                        if( p != i && p != j && p != k && Util.pointInsideCircle(points[p], points[i], points[j], points[k]) ) 
                            isTriangle = false;                                            
                    
                    if( isTriangle ) {
                        if (!edges.get(i).contains(j))
                            edges.get(i).add(j);
                        if (!edges.get(i).contains(k))
                            edges.get(i).add(k);
                        if (!edges.get(j).contains(i))
                            edges.get(j).add(i);
                        if (!edges.get(j).contains(k))
                            edges.get(j).add(k);
                        if (!edges.get(k).contains(i))
                            edges.get(k).add(i);
                        if (!edges.get(k).contains(j))
                            edges.get(k).add(j);
                    }                    
                }
        
        
        
    }

    private static boolean pointInsideCircle(Point2D.Double p, Point2D.Double i, Point2D.Double j, Point2D.Double k) {
        // Center
        Point2D.Double jj = new Point2D.Double(j.x-i.x, j.y-i.y);
        Point2D.Double kk = new Point2D.Double(k.x-i.x, k.y-i.y);
        
        double delta = 2. * (jj.x*kk.y - jj.y*kk.x);
        if( Math.abs(delta) < 1e-6 )
            return false;
        
        double jdot = jj.x*jj.x + jj.y*jj.y;
        double kdot = kk.x*kk.x + kk.y*kk.y;
        double delta1 = kk.y*jdot - jj.y*kdot;
        double delta2 = jj.x*kdot - kk.x*jdot;
        
        Point2D.Double centralPoint = new Point2D.Double(i.x + delta1/delta, i.y + delta2/delta);
        
        return Util.euclideanDistance(centralPoint.x, centralPoint.y, p.x, p.y) <= Util.euclideanDistance(centralPoint.x, centralPoint.y, i.x, i.y);        
    }
    
    public static ArrayList<OverlapRect> getProjectedValues(Map<OverlapRect, OverlapRect> projected) {
        ArrayList<OverlapRect> projectedValues = new ArrayList<>();
        projected.entrySet().forEach((element)->{            
            projectedValues.add(element.getValue());
            projectedValues.get(projectedValues.size()-1).setId(element.getKey().getId());
        });
        return projectedValues;
    }
    
    /**
     * Creata a matrix with elements corresponding to the columns and the dimensions corresponding to the rows
     * @param points
     * @return 
     */
    public static double[][] elementMatrix(List<? extends List<Double>> points) {
        double[][] elements = new double[points.get(0).size()][points.size()];
       
        for( int i = 0; i < points.size(); ++i ) {
            for( int j = 0; j < points.get(0).size(); ++j ) {
                elements[j][i] = points.get(i).get(j);
            }
        } 
            
        return elements;
    }
    
    public static int maxIndex(double[] v) {
        int index = 0;
        double maior = v[0];
        for( int i = 0; i < v.length; ++i )
            if( v[i] > maior ) {
                index = i;         
                maior = v[i];
            }
        
        return index;
    }
    
    public static double[][] multiply(double[][] a, double[][] b) {
        double[][] r = new double[a.length][b[0].length];
        for(int i = 0;i < a.length;i++)
            for(int j = 0;j < b[0].length;j++)
               for(int k = 0;k < b.length;k++)
                  r[i][j] += a[i][k] * b[k][j];
        
        return r;
    }
    
    public static double[] multiply(double[] b, double[][] a) {
        double[] ans = new double[a[0].length];
        for( int i = 0; i < a[0].length; ++i ) {
            ans[i] = 0;
            for( int j = 0; j < a.length; ++j )
                ans[i] += a[j][i]*b[j];
        }
        
        return ans;
    }
    
    public static double[] multiply(double[][] a, double[] b) {
        double[] ans = new double[a.length];
        for( int i = 0; i < a.length; ++i ) {
            ans[i] = 0;
            for( int j = 0; j < a[0].length; ++j )
                ans[i] += a[i][j]*b[j];
        }
        
        return ans;
    }
    
    public static double[][] multiply(double value, double[][] m) {
        double[][] valueM = new double[m.length][m[0].length];
        for( int i = 0; i < valueM.length; ++i )
            for( int j = 0; j < valueM[0].length; ++j )
                valueM[i][j] = value*m[i][j];
        return valueM;
        
    }
    
    public static double[][] minus(double[][] a, double[][] b) {
        double[][] r = new double[a.length][a[0].length];
        for( int i = 0; i < r.length; ++i )
            for( int j = 0; j < r[0].length; ++j )
                r[i][j] = a[i][j] - b[i][j];
        return r;
    }
    
    public static double[][] transposed(double[][] m) {
        double[][] mt = new double[m[0].length][m.length];
        
        for( int i = 0; i < m.length; ++i )
            for( int j =0; j < m[0].length; ++j )
                mt[j][i] = m[i][j];
        
        return mt;
    }
    
    public static double[] mean(double[][] m, int dim) {
        int n = dim == 0 ? m[0].length : m.length;
        int k = dim == 0 ? m.length : m[0].length;
        double[] meanVector = new double[n];
        
        for( int i = 0; i < n; ++i ) {
            double sum = 0;
            for( int j = 0; j < k; ++j )
                sum += (dim == 0 ? m[j][i] : m[i][j]);
            meanVector[i] = sum/k;
        }
        
        return meanVector;
    }
    
    public static double[][] reapmat(double[] v, int n) {
        
        double[][] m = new double[v.length][n];
        
        for( int i = 0; i < m.length; ++i )
            for( int j = 0; j < n; ++j )
                m[i][j] = v[i];
        
        return m;
    }
    
    public static double[][] repmatRow(double[] v, int n) {
        
        double[][] m = new double[v.length][n];
        
        for( int i = 0; i < n; ++i )
            for( int j = 0; j < m.length; ++j )
                m[j][i] = v[i];
        
        return m;
    }
    
    public static double sign(double v) {
        return v == 0 ? 0 : (v < 0 ? -1 : 1);
    }
    
    public static double norm(double[] v) {
        double s = 0;
        for( int i = 0; i < v.length; ++i )
            s += v[i]*v[i];
        
        return Math.sqrt(s);
    }
    
    public static double[] copyColumn(double[][] m, int index) {
        double[] v = new double[m.length];
        
        for( int i = 0; i < v.length; ++i )
            v[i] = m[i][index];
        return v;
    }

    public static double norm(double[] d, double q) {
        double sum = 0;
        for( int i = 0; i < d.length; ++i )
            sum += Math.pow(d[i], q);
        return Math.pow(sum, 1.0/q);
    }
    
    public static double[][] eye(int n) {
        
        double[][] identity = new double[n][n];
        for( int i = 0; i < n; ++i ) {
            for( int j = 0; j < n; ++j )
                identity[i][j] = 0.0;
            identity[i][i] = 1;
        }
        return identity;
    }

    public static double[][] sum(double[][] a, double[][] b) {
        double[][] c = new double[a.length][a[0].length];
        
        for( int i = 0; i < c.length; ++i )
            for( int j = 0; j < c[0].length; ++j )
                c[i][j] = a[i][j]+b[i][j];
        return c;
    }

    public static double[][] inverse(double[][] m) {
        Matrix jamaM = new Matrix(m);
        Matrix inverseM = jamaM.inverse();
        return inverseM.getArrayCopy();
    }

    public static double[][] createMatrix(int n, int m, double value) {
        double[][] matrix = new double[n][n];
        for( int i = 0; i < n; ++i )
            for( int j = 0; j < m; ++j )
                matrix[i][j] = value;
        return matrix;
    }
    
    public static double[][] sum(double[][] m, int dim) {
        
        if( dim == 0 ) {
            double[][] s = new double[1][m[0].length];
            for( int i = 0; i < m[0].length; ++i ) {
                s[0][i] = 0;
                for( int j = 0; j < m.length; ++j )
                    s[0][i] += m[j][i];
            }
            return s;
        } else {
            double[][] s = new double[1][m[0].length];
            for( int i = 0; i < m.length; ++i ) {
                s[i][0] = 0;
                for( int j = 0; j < m[0].length; ++j )
                    s[i][0] += m[i][j];
            }
            return s;
        }
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.vpsc;

import br.com.methods.overlap.OverlapRemoval;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.IntStream;

/**
 *
 * @author Wilson
 */
public class VPSC implements OverlapRemoval {
    
    /**
     * Aplica o método VPSC na projeção 'rectangles'
     * @param rectangles Projeção inicial
     * @return Projeção sem sobreposição
     */
    @Override
    public Map<OverlapRect, OverlapRect> apply(List<OverlapRect> rectangles) {
        /**
         * copy rectangles 
         */         
        ArrayList<OverlapRect> projected = new ArrayList<>();
        System.out.println("toApply()");
        rectangles.stream().forEach(r->{
            projected.add(new OverlapRect(r.getUX(), r.getUY(), r.width, r.height));
        });       
        System.out.println("----------------------");
        /**
         *  init all variables
         */         
        ArrayList<Variable> vars = new ArrayList<>();
        for( int i = 0; i < projected.size(); ++i ) 
            vars.add(new Variable(i));
                
        /**
         * generate horizontal constraints and solve vpsc for x
         */
        ArrayList<Constraint> restricoes = new ArrayList<>();
        VPSC.generateCx(projected, vars, restricoes);
        
        // solve vpsc for x
        VPSC.solveVPSC(vars, restricoes);
        
        // move rectangles in x coordinate
        for( int i = 0; i < projected.size(); ++i ) {
            
            if( Util.bounding_box != null ) {
                projected.get(i).moveX(Util.checkBoundX((float)vars.get(i).getPosition()));
            }
            else 
                projected.get(i).moveX(vars.get(i).getPosition());
        }
              
        // init all variables         
        for( int i = 0; i < vars.size(); ++i ) 
            vars.get(i).init(i);
        
        // generate constraint for y coordinate
        restricoes = new ArrayList<>();
        VPSC.generateCy(projected, vars, restricoes);
        
        // solve vpsc for y and move rectangles in y direction to remove all overlap remaining
        VPSC.solveVPSC(vars, restricoes);
        for( int i = 0; i < projected.size(); ++i )  {
            if( Util.bounding_box != null)
                projected.get(i).moveY(Util.checkBoundY((float)vars.get(i).getPosition()));
            else 
                projected.get(i).moveY(vars.get(i).getPosition());
        }           
        
        
        Map<OverlapRect, OverlapRect> projectedToReprojected = new HashMap<>();
        IntStream.range(0, projected.size()).forEach(
            i->projectedToReprojected.put(rectangles.get(i), projected.get(i))
        );
        
        return projectedToReprojected;
           
    }
    
    /**
     * Método principal que aplica o algoritmo VPSC segundo as Variaveis e as Restrições de não Sobreposição.
     * @param vars Variaveis criadas com base na projeção inicial
     * @param res Restrição de não sobreposição criadas a partir da projeção inicial
     */
    public static void solveVPSC(ArrayList<Variable> vars, ArrayList<Constraint> res) {
        Blocks blocos = new Blocks(vars);
        
        satisfyVPSC(blocos, vars, res);
        /**
         * tenta remover todas sobreposições, em seguida verifica se o menor coeficiente de Lagrande é negativo;
         * Caso seja negativo, continua "quebrando" e juntando os blocos e removendo as sobreposições
         */        
        while( true ) {
            
            Constraint r1 = blocos.getBlocos().get(0).findMinLM();
            int idx = 0;
            for( int i = 1; i < blocos.getBlocos().size(); ++i ) {
                Constraint r2 = blocos.getBlocos().get(i).findMinLM();
                if( r1 == null || r2 != null && r1.getLm() > r2.getLm() )  {
                    r1 = r2;                
                    idx = i;
                }
            }
            
            // caso o menor coeficiente de Lagrange seja positivo, todas as sobreposições já foram removidas;
            // então o método termina
            if( r1 == null || r1.getLm() >= 0 )
                break;
           
            // caso contrário, recuperamos o menor coeficiente e quebramos seu bloco em dois novos...
            Block lb = new Block();
            Block rb = new Block();
            blocos.restrictBlock(blocos.getBlocos().get(idx), lb, rb, r1);
            
                     
            rb.setPosn(blocos.getBlocos().get(idx).getPosn());
            rb.setWPosn(rb.getPosn()*rb.getWeight());
            blocos.mergeLeft(lb);

            rb = r1.getRight().getBloco();

            double wposn = 0;        
            for( int i = 0; i < rb.getVars().size(); ++i ) 
                wposn += (rb.getVars().get(i).getWeight()*(rb.getVars().get(i).getDes() - rb.getVars().get(i).getOffset()));

            rb.setWPosn(wposn);
            rb.setPosn(rb.getWPosn()/rb.getWeight());

            blocos.mergeRight(rb);        

            blocos.getBlocos().get(idx).setDeleted(true);
            blocos.getBlocos().add(lb);
            blocos.getBlocos().add(rb);
            

            for( int j = blocos.getBlocos().size()-1; j >= 0; --j )
                if( blocos.getBlocos().get(j).getDeleted() )
                    blocos.getBlocos().remove(j);
        }         
    }
        
    /**
     * Primeiramente um bloco fora criado para cada variável v (b.posn = v.des).
     * Alguma restrição pode ser violada com essas atribuições. Assim, caso haja alguma violação,
     * a restrição com maior valor de violation() é encontrada e seus blocos são mesclados.
     * @param blocos
     * @param vars
     * @param res 
     */
    private static void satisfyVPSC(Blocks blocos, ArrayList<Variable> vars, ArrayList<Constraint> res) {
        for( int i = 0; i < res.size(); ++i )
            res.get(i).setAtiva(false);
    
        // recupera a ordenação topológica
        ArrayList<Variable> varsOrdered = blocos.totalOrder();
        
        
        // esse algoritmo é quase ótimo, trabalha juntando as variaveis em blocos cada vez maiores
        // de variaveis conectadas por um árvore geradora
        for( int i = 0; i < varsOrdered.size(); ++i )
            if( !varsOrdered.get(i).getBloco().getDeleted() ) 
                blocos.mergeLeft(varsOrdered.get(i).getBloco());                
                             
        
        for( int i = blocos.getBlocos().size()-1; i >= 0; --i )
                if( blocos.getBlocos().get(i).getDeleted() )
                    blocos.getBlocos().remove(i);
    }
        
    /**
     * Gera restrições de não sobreposição verticais. A diferença para gerar restrições de não sobreposição
     * horizontais é que qualquer sobreposição restante deve ser removida verticalmente. Isso significa que 
     * precisamos somente encontrar os nós mais próximos analogamente as funções getLeftNeighbours() e 
     * getRightNeighbours(), já que qualquer outro nó na linha de varredura estará restringido acima ou abaixo.
     * Ou seja, o número de vizinhos na esquerda ou direita é menor ou igual a 1.
     * @param retangulos Projeção inicial
     * @param vars Variaveis criadas a partir da projeção
     * @param restricoes Restrições a serem preenchidas
     */    
    public static void generateCy(ArrayList<OverlapRect> retangulos, ArrayList<Variable> vars, ArrayList<Constraint> restricoes) {
        Event eventos[] = new Event[2*retangulos.size()];
        int j = 0;
        for( int i = 0; i < retangulos.size(); ++i ) {
            vars.get(i).setDesiredPosition(retangulos.get(i).getCenterY());            
            Node no = new Node(vars.get(i), retangulos.get(i), retangulos.get(i).getCenterY());
            
            eventos[j++] = (new Event("OPEN", no, retangulos.get(i).getUX()));
            eventos[j++] = (new Event("CLOSE", no, retangulos.get(i).getLX()));
        }
          
//               Util.quickSort(eventos, 0, eventos.length-1);
        
        Arrays.sort(eventos, (Event o1, Event o2) -> {
            return Double.compare(o1.getPosition(),o2.getPosition());
        });

        for( int i = 1; i < eventos.length; ++i )
            if( eventos[i-1].getNo().getRect() == eventos[i].getNo().getRect() && eventos[i-1].getTipo().equals("OPEN") ) {
                Event temp = eventos[i-1];           
                eventos[i-1] = eventos[i];      
                eventos[i] = temp;               
            }
        
          
        TreeSet<Node> scanline = new TreeSet<>(new NodeComparator());        
        for( int i = 0; i < eventos.length; ++i ) {
            Node v = eventos[i].getNo();
            
            if( eventos[i].getTipo().equals("OPEN") ) {
                // só preciso olhar um elemento para cima e para baixo...
                scanline.add(v);
                
                /**
                 * Finds the first lower element than v
                 */
                Iterator<Node> descendingIt = scanline.descendingIterator();        
                while( descendingIt.hasNext() ) {
                    Node no = descendingIt.next();
                    if( no == v ) 
                        break;
                }
                Node lower = null;
                while( descendingIt.hasNext() ) {
                    lower = descendingIt.next();
                    if( !lower.getDeleted() )
                        break;
                    lower = null;
                }
                
                Node u =  lower;
                if( u != null ) {                    
                    v.setAboveNeighbour(u);
                    u.setBelowNeighbour(v);
                }
                
                /**
                 * Finds the first higher element than v
                 */
                
                Iterator<Node> ascendingIt = scanline.iterator();
                while( ascendingIt.hasNext() ) {
                    Node no = ascendingIt.next();
                    if( no == v )
                        break;
                }

                Node higher = null;
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
                
                Node a = v.getAboveNeighbour();
                if( a != null ) {
                    double gap = (v.getRect().getHeight()+a.getRect().getHeight())/2.;
                    restricoes.add(new Constraint(a.getVar(), v.getVar(), gap));
                }
                
                Node b = v.getBelowNeighbour();
                if( b != null ) {
                    double gap = (v.getRect().getHeight()+b.getRect().getHeight())/2.;
                    restricoes.add(new Constraint(v.getVar(), b.getVar(), gap));                    
                }

                removeScanline(scanline, v);
            }
        }        
    }
    
    /**
     * Gera restrições de não sobreposição horizontais. Uma linha de varredura é utilizada após configurar os eventos de
     * entrada e saída de Variaveis. Assim é possível gerar uma restrição de não sobreposição quando um evento é aberto
     * antes de algum outro evento ser fechado (caracteriza uma sobreposição em relação a um eixo específico).
     * @param retangulos Projeção inicial
     * @param vars Variaveis criadas a partir da projeção
     * @param restricoes Restrições a serem preenchidas
     */
    public static void generateCx(ArrayList<OverlapRect> retangulos, ArrayList<Variable> vars, ArrayList<Constraint> restricoes) {
        /**
         * Adiciona os eventos de abertura e fechamento dos retangulos 
         */
        Event[] eventos = new Event[retangulos.size()*2];
        for( int i = 0, j = 0; i < retangulos.size(); ++i ) {
            vars.get(i).setDesiredPosition(retangulos.get(i).getCenterX());            
            Node no = new Node(vars.get(i), retangulos.get(i), retangulos.get(i).getCenterX());
            eventos[j++] = new Event("OPEN", no, retangulos.get(i).getUY());
            eventos[j++] = new Event("CLOSE", no, retangulos.get(i).getLY());            
        }
        // [e1,..., e2n] := events sorted by posn
//        Util.quickSort(eventos, 0, eventos.length-1);
        
        Arrays.sort(eventos, (Event o1, Event o2) -> {
            return Double.compare(o1.getPosition(),o2.getPosition());
        });
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
        TreeSet<Node> scanline = new TreeSet<>(new NodeComparator());
        for( int i = 0; i < eventos.length; ++i ) {
            
            Node v = eventos[i].getNo();            
            if( eventos[i].getTipo().equals("OPEN") ) {
                
                scanline.add(v);
                
                TreeSet<Node> leftv = getLeftNeighbours(scanline, v);                
                TreeSet<Node> rightv = getRightNeighbours(scanline, v);               
                
                v.setLeftNeighbours(leftv);
                v.setRightNeighbours(rightv);
                
            } else { /* ei.kind == close*/
                
                /**
                * Uma constraint é definida como left(c) + gap(c) <= right(c)
                * onde gap(c) é o menor separação entre left(c) e right(c).
                * Assim, neste caso é (left(c).width + right(c).width)/2.
                */  
                
                Iterator<Node> it = v.getLeftNeighbours().iterator();
                while( it.hasNext() ) {
                    
                    Node u = it.next();          
                    double gap = (v.getRect().getWidth()+u.getRect().getWidth())/2.;
                    
                    if( !u.getDeleted() && canAddConstraint(restricoes, u.getVar(), v.getVar(), gap) ) {                    
                        restricoes.add(new Constraint(u.getVar(), v.getVar(), gap));
                        u.removeRightNeighbour(v);
                    }
                    
                }
                
                it = v.getRightNeighbours().iterator();
                while( it.hasNext() ) {
                    Node u = it.next();         
                    double gap = (v.getRect().getWidth()+u.getRect().getWidth())/2.;
                    if( !u.getDeleted() && canAddConstraint(restricoes, v.getVar(), u.getVar(), gap) )  {
                        restricoes.add(new Constraint(v.getVar(), u.getVar(), gap));
                        u.removeLeftNeighbour(v);                        
                    }                    
                }                
                
                // "remove" o elemento da scanline
                removeScanline(scanline, v);
            }
        }
    }
    
    /**
     * Método usado para setar um flag de removido da scanline
     * @param scanline Linha de varredura
     * @param v Node a ser removido da linha de varredura
     */
    private static void removeScanline(TreeSet<Node> scanline, Node v) {
        Iterator<Node> it = scanline.iterator();
        Node toRemove = null;
        while( it.hasNext() ) {
            toRemove = it.next();
            if( toRemove == v )
                break;
        }
        
        if( toRemove != null )
            toRemove.setDeleted(true);            
    }
    
    /**
     * Recupera os vizinhos mais próximos que requerem uma restrição de não sobreposição.
     * Executa até o primeiro vizinho que não contém sobreposição.
     * @param scanline Linha de varredura
     * @param v Node referência para verificar vizinhos da esquerda
     * @return TreeSet que contém os vizinhos da esquerda do Node 'v'
     */
    private static TreeSet<Node> getLeftNeighbours(TreeSet<Node> scanline, Node v) {
        TreeSet<Node> leftv = new TreeSet<>(new NodeComparator());
        
        // neste caso, são os elementos menores que v
        Iterator<Node> it = scanline.descendingIterator();        
        while( it.hasNext() ) {
            Node no = it.next();
            if( no == v )
                break;
        }
        
        Node u = null;
        while( it.hasNext() ) {
            u = it.next();
            
            if( !u.getDeleted() )
                break;
            u = null;
        }
        
        while( u != null ) {      
            
            // achou um nó que não sobrepõe, como é armazenado em uma árvore, não há mais nenhuma em que existe sobreposição
            if( u.getRect().olapX(v.getRect()) <= 0.000 ) {
                leftv.add(u);                
                return leftv;
            }
            
            // adiciona somente se a sobreposição horizontal for menor que a sobreposição vertical            
            if( u.getRect().olapX(v.getRect()) <= u.getRect().olapY(v.getRect()) )  
                leftv.add(u);
            
            u = null;
            while( it.hasNext() ) {
                u = it.next();
                if( !u.getDeleted() ) 
                    break;
                u = null;               
            }
        }
        
        return leftv;
    }

    /**
     * * Recupera os vizinhos mais próximos que requerem uma restrição de não sobreposição.
     * Executa até o primeiro vizinho que não contém sobreposição.
     * @param scanline Linha de varredura
     * @param v Node referência para verificar vizinhos da direita
     * @return TreeSet que contém os vizinhos da direita do Node 'v'
     */
    private static TreeSet<Node> getRightNeighbours(TreeSet<Node> scanline, Node v) {
        TreeSet<Node> rightv = new TreeSet<>(new NodeComparator());
        Iterator<Node> it = scanline.iterator();
        while( it.hasNext() ) {
            Node no = it.next();
            if( no == v ) {
                break;
            }            
        }
        
        Node u = null;
        while( it.hasNext() ) {
            u = it.next();
            if( !u.getDeleted() )
                break;
            u = null;
        }
        while( u != null ) {
            
            // achou um nó que não sobrepõe, como é armazenado em uma árvore, não há mais nenhuma em que existe sobreposição
            if( u.getRect().olapX(v.getRect()) <= 0.000 ) {
                rightv.add(u);                
                return rightv;
            }            
            
            // adiciona somente se a sobreposição horizontal for menor que a sobreposição vertical            
            if( u.getRect().olapX(v.getRect()) <= u.getRect().olapY(v.getRect()) ) 
                rightv.add(u);                            
            
             
            u = null;
            while( it.hasNext() ) {
                u = it.next();                
                if( !u.getDeleted() )
                    break;
                u = null;
            }
        }
                
        return rightv;
    }

    /**
     * Método que evita adicionar constraints redundantes
     * @param restricoes
     * @param v1
     * @param v2
     * @param separation
     * @return 
     */
    private static boolean canAddConstraint(ArrayList<Constraint> restricoes, Variable v1, Variable v2, double separation) {
        
        for( int i = 0; i < restricoes.size(); ++i )
            if( restricoes.get(i).getLeft() == v1 && restricoes.get(i).getRight() == v2 && restricoes.get(i).getGap() == separation )
                return false;
        
        
        return true;
    }
    
    @Override
    public String toString() {
        return "VPSC";
    }
}

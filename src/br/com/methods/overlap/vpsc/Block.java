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
 */

package br.com.methods.overlap.vpsc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Classe Block que controla o posicionamento das Variaveis durante a execução do método.
 * @author Wilson
 */
public class Block {
    private ArrayList<Variable> vars; // variáveis do bloco
    private double posn; // posição de referência do bloco
    private double weight; // soma dos pesos das variaveis nesse bloco
    private double wposn;  // soma ponderada das posições desejadas das variaveis nesse bloco
    private PriorityQueue<Constraint> in; // in constraints
    private PriorityQueue<Constraint> out; // out constraints
    private boolean deleted; // usamos uma estratégia para nao ter problema de ponteiro nulo
    private Constraint menor = null;
    
    /**
     * Inicia um novo bloco.
     */
    public Block() {
        vars = new ArrayList<>();
          
        weight = 0;
        wposn = 0;
        in = null;
        out = null;
        deleted = false;
    }
    
    /**
     * Cria um novo bloco contendo uma Variable.
     * @param v Variable inicial do bloco
     */
    public Block(Variable v) { 
        vars = new ArrayList<>();        
        weight = 0;
        wposn = 0;
        in = null;
        out = null;
        deleted = false;
        if( v != null ) {
            v.setOffset(0);
            addVar(v);
        }
        
    }
    
    /**
     * Adiciona uma Variable no bloco, fazendo com que o mesmo aumente de tamanho.
     * @param v Variable a ser adicionada
     */
    public void addVar(Variable v) {
        v.setBloco(this);
        vars.add(v);
        weight += v.getWeight();
        wposn += v.getWeight()*(v.getDes()-v.getOffset());
        posn = wposn/weight;
    }
    
    /**
     * Define a posição de referência do bloco.
     * @param posn Posição de referência
     */
    public void setPosn(double posn) {
        this.posn = posn;
    }
    
    /**
     * Retorna a posição de referência do bloco.
     * @return double
     */
    public double getPosn() {
        return posn;
    }    
    
    /**
     * Inicia as restrições entrantes do bloco, considerando todas as variáveis pertencentes.
     * No topo da fila está sempre a menor restrição.
     * @return PriotyQueue de Restrições em relação as suas violações.
     */
    public PriorityQueue<Constraint> heapifyInConstraints() {
        in = new PriorityQueue<>(vars.size(), new Comparator<Constraint>() {
           @Override
           public int compare(Constraint a, Constraint b) {
               if( a.getViolationLeft() < b.getViolationLeft() )
                   return 1;
               else if( a.getViolationLeft() > b.getViolationLeft() )
                   return -1;
               return 0;
           }
        });
        
        for( int i = 0; i < vars.size(); ++i ) {
            ArrayList<Constraint> rest = vars.get(i).getIn();
            for( int j = 0; j < rest.size(); ++j )  {
                // evita que restrições redundantes sejam inseridas
                if( rest.get(j).getLeft().getBloco() != this  )
                    in.add(rest.get(j));
            }
                
        }
        return in;
    }
    
    /**
     * Inicia as restrições saintes do bloco, considerando todas as variáveis pertencentes.
     * No topo da fila está sempre a menor restrição.
     * @return PriotyQueue de Restrições em relação as suas violações.
     */
    public PriorityQueue<Constraint> heapifyOutConstraints() {
        out = new PriorityQueue<>(vars.size(), new Comparator<Constraint>() {
           @Override
           public int compare(Constraint a, Constraint b) {
               if( a.getViolationRight()< b.getViolationRight() )
                   return -1;
               else if( a.getViolationRight() > b.getViolationRight() )
                   return 1;
               return 0;
               
           }
        });
        
        for( int i = 0; i < vars.size(); ++i ) {
            ArrayList<Constraint> rest = vars.get(i).getOut();
            for( int j = 0; j < rest.size(); ++j )  {
                // evita que restrições redundantes sejam inseridas
                if( rest.get(j).getLeft().getBloco() != this )
                    out.add(rest.get(j));
            }
                
        }
        return in;
    }
    
    /**
     * Retorna a menor restrição sainte.
     * @return Restrição sainte com menor violação.
     */
    public Constraint getMinOutConstraint() {
        Constraint constraint = out.peek();
        
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

    /**
     * Retorna a menor restrição entrante.
     * @return Restrição entrante com menor violação.
     */
    public Constraint getMinInConstraint() {
        Constraint constraint = in.peek();
        
        // o autor diz que é importante reduzir restrições redundantes quando encontra-las
        while( constraint != null && constraint.getLeft().getBloco() == constraint.getRight().getBloco() ) {
            in.poll();            
            constraint = in.peek();
        }        
        
        return constraint;
    }

    /**
     * Remove a menor restrição entrante.
     */
    public void removeMinInConstraint() {
        in.poll();
    }
    
    /**
     * Remove a menor restrição sainte. 
     */
    public void removeMinOutConstraint() {
        out.poll();
    }
    
    /**
     * Retorna a fila de restrições entrantes ordenadas da menor para maior.
     * @return PriorityQueue< Restrição >
     */
    public PriorityQueue<Constraint> getIn() {
        return in;
    }
    
    /**
     * Retorna a fila de restrições saintes ordenadas da menor para maior.
     * @return PriorityQueue< Restrição >
     */
    public PriorityQueue<Constraint> getOut() {
        return out;
    }

    /**
     * Retorna as variáveis pertencentes ao Block.
     * @return ArrayList< Variable >
     */
    public ArrayList<Variable> getVars() {
        return vars;
    }
    
    /**
     * Define se o bloco será removido.
     * @param b true para agendar remoção,
     *          false para não agendar.
     */
    public void setDeleted(boolean b) {
        deleted = b;
    }
    
    /**
     * Verifica se o Block fora marcado para ser removido.
     * @return true se o bloco será removido,
     *         false caso contrário.
     */
    public boolean getDeleted() {
        return deleted;
    }

    /**
     * Define o peso do bloco (utilizado para calcular a posição ótima do bloco).
     * @param weight Peso do bloco
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Retorna o peso do bloco (utilizado para calcular a posição ótima do bloco).
     * @return double
     */
    public double getWeight() {
        return weight;
    }
    
    /**
     * Define a posição de referência ponderada (utilizado para calcular a posição ótima do bloco).
     * @param wposn Posição de referência ponderada
     */
    public void setWPosn(double wposn) {
        this.wposn = wposn;
    }

    /**
     * Retorna a posição de referência ponderada (utilizado para calcular a posição ótima do bloco).
     * @return double
     */
    public double getWPosn() {
        return wposn;
    }

    /**
     * Adiciona Restrições no bloco que foi quebrado.
     * Utiliza a subarvore de restrições ativas da esquerda e a
     * subarvore de restrições ativas da direita
     * @param b Block que foi quebrado
     * @param v Variable corrente
     */
    public void addInSplitBlock(Block b, Variable v) {
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
     * somando v.weight * (posn(v) - v.des)
     * @param v
     * @param u
     * @return 
     */
    private double compDfDv(Variable v, Variable u) {
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
    
    /**
     * Encontra o menor coeficiente de Lagrange das restrições.
     * @return Restrição com menor coeficiente de Lagrange
     */
    public Constraint findMinLM() {
       
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

    /**
     * Mescla o Block corrente com o Block 'b'.
     * @param bloco Block a 'desaparecer'
     * @param r Restrição em que o Block é left
     * @param distancia Distância até o Block corrente
     */
    public void mergeBlockLeft(Block bloco, Constraint r, double distancia) {
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
    
    /**
     * Mescla o Block corrente com o Block 'b'.
     * @param bloco Block a 'desaparecer'
     * @param r Restrição em que o Block é right
     * @param distancia Distância até o Block corrente
     */
    public void mergeBlockRight(Block bloco, Constraint r, double distancia) {
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

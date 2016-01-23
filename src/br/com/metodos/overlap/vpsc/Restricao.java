/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

/**
 * Classe que representa uma restrição de sobreposição sobre duas Variaveis
 * @author wilson
 */
public class Restricao {
    /* Nós u e v, bem como a menor lacuna entre eles*/
    private Variavel left, right;
    private double gap;
    private boolean ativa;
    private double lm;
    
    /**
     * Cria uma restrição de sobreposição
     * @param left Variavel left da restrição
     * @param right Variavel right da restrição
     * @param gap Menor espaço entre as variaveis
     */
    public Restricao(Variavel left, Variavel right, double gap) {
        this.left = left;
        this.right = right;
        this.gap = gap;
        this.left.getOut().add(this);
        this.right.getIn().add(this);
    }
    
    /**
     * Retorna o coeficiente de Lagrange
     * @return double
     */
    public double getLm() {
        return lm;
    }
    
    /**
     * Define o coeficiente de Lagrange calculado antes da aplicação do método
     * @param lm Coeficiente de Lagrande
     */
    public void setLm(double lm) {
        this.lm = lm;
    }

    /**
     * Retorna a Variavel right da restrição
     * @return Variavel
     */
    public Variavel getLeft() {
        return left;
    }

    /**
     * Define a Variavel da esqurda da restrição
     * @param left Variavel left da restrição
     */
    public void setLeft(Variavel left) {
        this.left = left;
    }

    /**
     * Retorna a Variavel right da restrição
     * @return Variavel
     */
    public Variavel getRight() {
        return right;
    }

    /**
     * Define a Variavel da direita da restrição
     * @param right Variavel right da restrição
     */
    public void setRight(Variavel right) {
        this.right = right;
    }
    
    /**
     * Retorna o menor espaço entre as duas Variaveis
     * @return double
     */
    public double getGap() {
        return gap;
    }

    /**
     * Define o menor espaço entre as duas Variaveis
     * @param gap menor espaço entre as duas variaveis
     */
    public void setGap(double gap) {
        this.gap = gap;
    }
    
    /**
     * Retorna a quantidade de violação da esquerda
     * @return double
     */
    public double getViolationLeft() {
        return left.getPosition()+gap-right.getPosition();
    }
    
    /**
     * Retorna a quantidade de violação da direita
     * @return double
     */
    public double getViolationRight() {
        return right.getPosition()-left.getPosition()-gap;
    }
    
    /**
     * Define se a restrição está ativa.
     * @param ativa true se a restrição deve ser ativa,
     *              false caso contrário.
     */
    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
    
    /**
     * Verifica se a restrição está ativa.
     * @return true se a restrição está ativa,
     *         false caso contrário.
     */
    public boolean getAtiva() {
        return ativa;
    }
    
    @Override
    public String toString() {
        return left.getId()+" + "+gap+" <= "+right.getId();    
    }
}

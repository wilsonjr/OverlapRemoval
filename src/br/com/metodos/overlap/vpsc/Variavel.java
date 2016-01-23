/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

import java.util.ArrayList;

/**
 * 
 * @author Wilson
 */
public class Variavel {
    private int id;
    private double des;
    private double weight;
    private ArrayList<Restricao> in;
    private ArrayList<Restricao> out;
    private Bloco bloco; // bloco ao qual a var pertence...
    private double offset;
    private boolean visitado;
    private boolean deletado;
    
    /**
     * Cria uma variavel com o id especificado
     * @param id Identificação da Variavel
     */
    public Variavel(int id) {
        init(id);
    }
    
    /**
     * Inicia a variavel novamente, especificando o id
     * @param id Identificação da variavel
     */
    public void init(int id) {
        this.id = id;
        des = 0;
        weight = 1;
        offset = 0;
        visitado = false;
        
        in = new ArrayList<>();
        out = new ArrayList<>();
    }    
    
    /**
     * Define o offset da variavel em relação ao bloco que a contém
     * @param offset 
     */
    public void setOffset(double offset) {
        this.offset = offset;
    }
    
    /**
     * Retorna a verdadeira posição da Variavel em relação ao seu bloco
     * @return double
     */
    public double getPosition() {
        return bloco.getPosn() + offset;
    }
    
    /**
     * Define a posição desejada para a Variavel ao final da aplicação do método
     * @param des Posição desejada para que a Variavel esteja
     */
    public void setDesiredPosition(double des) {
        this.des = des;
    }
    
    /**
     * Retorna a identificação da Variavel
     * @return int
     */
    public int getId() {
        return id;
    }
    
    /**
     * Retorna as restrições entrantes, ou seja, restrições em que right é essa Variavel
     * @return 
     */
    public ArrayList<Restricao> getIn() {
        return in;
    }
        
    /**
     * Retorna as restrições entrantes, ou seja, restrições em que left é essa Variavel
     * @return 
     */
    public ArrayList<Restricao> getOut() {
        return out;
    }    
    
    /**
     * Retorna o bloco ao qual a Variavel pertence
     * @return Bloco
     */
    public Bloco getBloco() {
        return bloco;
    }
    
    /**
     * Define o bloco da Variavel
     * @param b Bloco ao qual essa Variavel deve pertencer
     */
    public void setBloco(Bloco b) {
        bloco = b;
    }
    
    /**
     * Retorna o peso da variavel
     * @return double
     */
    public double getWeight() {
        return weight;
    }
    
    /**
     * Retorna a posição desejada da Variavel
     * @return double
     */
    public double getDes() {
        return des;
    }
    
    /**
     * Retorna a distância da variavel a até a posição de referência do Bloco
     * @return double 
     */
    public double getOffset() {
        return offset;
    }
    
    /**
     * Define se a Variavel já fora visitada (usado na ordenação topológica)
     * @param v true se já fora visitada,
     *          false caso contrário.
     */
    public void setVisitado(boolean v) {
        visitado = v;
    }
    
    /**
     * Verifica se a Variavel já fora visitada.
     * @return true se sim,
     *         false se não.
     */
    public boolean getVisitado() {
        return visitado;
    }

    /**
     * Verifica se a Variavel fora marcada para ser removida.
     * @return true se sim,
     *         false se não.
     */
    public boolean getDeletado() {
        return deletado;
    }
    
    /**
     * Marca a Variavel para ser removida ou não.
     * @param d true para marcar posterior remoção,
     *          false para não remover posteriormente.
     */
    public void setDeletado(boolean d) {
        deletado = d;
    }
}

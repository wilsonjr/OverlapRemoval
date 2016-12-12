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

/**
 * 
 * @author Wilson
 */
public class Variable {
    private int id;
    private double des;
    private double weight;
    private ArrayList<Constraint> in;
    private ArrayList<Constraint> out;
    private Block bloco;     private double offset;
    private boolean visitado;
    private boolean deletado;
    
    /**
     * Cria uma variavel com o id especificado
     * @param id Identificação da Variavel
     */
    public Variable(int id) {
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
     * Retorna a verdadeira posição da Variable em relação ao seu bloco
     * @return double
     */
    public double getPosition() {
        return bloco.getPosn() + offset;
    }
    
    /**
     * Define a posição desejada para a Variable ao final da aplicação do método
     * @param des Posição desejada para que a Variable esteja
     */
    public void setDesiredPosition(double des) {
        this.des = des;
    }
    
    /**
     * Retorna a identificação da Variable
     * @return int
     */
    public int getId() {
        return id;
    }
    
    /**
     * Retorna as restrições entrantes, ou seja, restrições em que right é essa Variable
     * @return 
     */
    public ArrayList<Constraint> getIn() {
        return in;
    }
        
    /**
     * Retorna as restrições entrantes, ou seja, restrições em que left é essa Variable
     * @return 
     */
    public ArrayList<Constraint> getOut() {
        return out;
    }    
    
    /**
     * Retorna o bloco ao qual a Variable pertence
     * @return Block
     */
    public Block getBloco() {
        return bloco;
    }
    
    /**
     * Define o bloco da Variable
     * @param b Block ao qual essa Variable deve pertencer
     */
    public void setBloco(Block b) {
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
     * Retorna a posição desejada da Variable
     * @return double
     */
    public double getDes() {
        return des;
    }
    
    /**
     * Retorna a distância da variavel a até a posição de referência do Block
     * @return double 
     */
    public double getOffset() {
        return offset;
    }
    
    /**
     * Define se a Variable já fora visitada (usado na ordenação topológica)
     * @param v true se já fora visitada,
     *          false caso contrário.
     */
    public void setVisitado(boolean v) {
        visitado = v;
    }
    
    /**
     * Verifica se a Variable já fora visitada.
     * @return true se sim,
     *         false se não.
     */
    public boolean getVisitado() {
        return visitado;
    }

    /**
     * Verifica se a Variable fora marcada para ser removida.
     * @return true se sim,
     *         false se não.
     */
    public boolean getDeletado() {
        return deletado;
    }
    
    /**
     * Marca a Variable para ser removida ou não.
     * @param d true para marcar posterior remoção,
     *          false para não remover posteriormente.
     */
    public void setDeletado(boolean d) {
        deletado = d;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

/**
 * Classe evento utilizada na ordenação dos elementos para encontrar restrições de sobreposição.
 * @author wilson
 */
public class Event {
    private String tipo;
    private No no;
    private double position;
    
    /**
     * Cria um evento para ordenação e criação de restrições.
     * @param tipo Tipo do evento (OPEN, CLOSE)
     * @param no No que forma o evento
     * @param position Posição considerando o eixo de referência (x,y)
     */
    public Event(String tipo, No no, double position) {
        this.tipo = tipo;
        this.no = no;
        this.position = position;
    }
        
    /**
     * Retorna o nó que forma o evento
     * @return No
     */
    public No getNo() {
        return no;
    }
    
    /**
     * Retorna o tipo do evento
     * @return OPEN se é o início de um retângulo,
     *         CLOSE se é o fim de um retângulo.
     */
    public String getTipo() {
        return tipo;
    }
    
    /**
     * Posição do evento considerando o eixo de referência (x,y)
     * @return double
     */
    public double getPosition() {
        return position;
    }
}

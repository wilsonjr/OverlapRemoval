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

package br.com.methods.utils;

import java.awt.Color;
import java.awt.Point;

/**
 * Classe para teste. Guarda informações para visualização dos retângulos após aplicação dos métodos.
 * @author wilson
 */
public class RectangleVis extends OverlapRect {
    public Color cor;
    public int numero;
    public boolean isHexBoard;
    public Point p;
    
    /**
     * Cria um retângulo para utilização na visualização.
     * @param xMin Coordenada x
     * @param yMin Coordenada y
     * @param width Largura do retângulo
     * @param height Altura do retângulo
     * @param cor Cor do retângulo
     * @param numero Número de identificação
     */
    public RectangleVis(double xMin, double yMin, double width, double height, Color cor, int numero) {
        super(xMin, yMin, width, height);
        this.cor = cor;
        this.numero = numero;
    }

    /**
     * Verifica se o método utilizado é o HexBoard.
     * @return true se o método for HexBoard,
     *         false caso contrário.
     */
    public boolean isIsHexBoard() {
        return isHexBoard;
    }

    /**
     * Define se o método utilizado é o HexBoard.
     * @param isHexBoard
     */
    public void setIsHexBoard(boolean isHexBoard) {
        this.isHexBoard = isHexBoard;
    }

    /**
     * Retorna o ponto para projeção após os métodos Board (Hex/In).
     * @return Point
     */
    public Point getP() {
        return p;
    }

    /**
     * Define o ponto referência para projeção dos métodos Board (Hex/In).
     * @param p Ponto referência.
     */
    public void setP(Point p) {
        this.p = p;
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.incboard;

import incboard.api.DataItemInterface;

/**
 * Classe criada para trabalhar (nesta aplicação) com dados multidimensionais.
 * Necessária para utilização dos métodos IncBoard e HexBoard.
 * @author wilson
 */
public class PontoItem implements DataItemInterface {
    private String uRI;
    private int x;
    private int y;
    private double[] dims;
    private int id, grupo;
    
    public PontoItem(double[] dims, String uri, int id, int grupo) {
        this.dims = dims;
        this.uRI = uri;
        this.id = id;
        this.grupo = grupo;
    }

    @Override
    public String getURI() {
        return uRI;
    }

    @Override
    public int getCol() {
        return x;
    }

    @Override
    public int getRow() {
        return y;
    }

    @Override
    public void setCol(int i) {
        x = i;
    }

    @Override
    public void setRow(int i) {
        y = i;
    }
    
    public double[] getDims() {
        return dims;
    }

    
    /**
     * Calcula a distância Euclideana entre dois PontoItem's (DataItemInterface)
     * @param dii Elemento com o qual calculará a distância
     * @return Distância Euclideana
     */
    @Override
    public Double getDistance(DataItemInterface dii) {
        PontoItem p = (PontoItem) dii;
        double soma = 0;
        System.out.println("tamanho p: "+p.getDims().length);
        System.out.println("tamanho dims: "+dims.length);
        for( int i = 0; i < dims.length; ++i ) 
            soma += Math.pow(dims[i]-p.getDims()[i], 2.);
        
        return Math.sqrt(soma);
    }
    
    public int getId() {
        return id;
    }
    
    public int getGrupo() {
        return grupo;
    }
}

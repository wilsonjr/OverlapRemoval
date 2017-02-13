/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.utils;

import java.awt.geom.Rectangle2D;

/**
 * Classe OverlapRect usada nos métodos de remoção de sobreposição.
 * @author wilson
 */
public class OverlapRect extends Rectangle2D.Double implements Pivot {    
    private double lx, ly;    
    private int id;
    private boolean pivot;
    private int level;
    private int cluster;
    private int health;
            
    /**
     * Cria um retângulo para aplicação do método.
     * @param minX Coordenada x superior do retângulo
     * @param minY Coordenada y superior do retângulo
     * @param width Largura do retângulo
     * @param height Altura do retângulo
     * @param id Identificação do retângulo
     */
    public OverlapRect(double minX, double minY, double width, double height, int id) {
        super(minX, minY, width, height);
        this.x = minX;
        lx = minX+width;
        this.y = minY;
        ly = minY+height;
        this.id = id;
        
        level = -1;
        pivot = false;
        
        health = 0;
    }    
    
    /**
     * Cria um retângulo para aplicação do método.
     * @param minX Coordenada x inferior do retângulo
     * @param minY Coordenada y inferior do retângulo
     * @param width Largura do retângulo
     * @param height Altura do retângulo
     */
    public OverlapRect(double minX, double minY, double width, double height) {
        this(minX, minY, width, height, -1);
    }    

    public OverlapRect(double ux, double uy, double width, double height, boolean pivot, int level, int cluster, int health, int id) {
        this(ux, uy, width, height, id);
        this.pivot = pivot;
        this.level = level;
        this.cluster = cluster;
        this.health = 0;
        
    }
     
    
    /**
     * Retorna a identificação.
     * @return int
     */
    public int getId() {
        return id;
    }
    
    /**
     * Define a coordenada X inferior.
     * @param x Coordenada X inferior
     */
    public void setLX(double x) {
        lx = x;
    }
    
    /**
     * Define a coordenada X superior.
     * @param x Coordenada X superior
     */
    public void setUX(double x) {
        double w = getWidth();
        this.x = x;
        lx = this.x+w;
    }
    
    /**
     * Define a coordenada Y inferior.
     * @param y Coordenada Y inferior
     */
    public void setLY(double y) {
        ly = y;
    }
    
    /**
     * Define a coordenada Y superior.
     * @param y Coordenada Y superior
     */
    public void setUY(double y) {
        double h = getHeight();
        this.y = y;
        ly = this.y+h;
    }
    
    /**
     * Define a largura.
     * @param w Largura do retângulo
     */
    public void setWidth(double w) {
        setLX(this.x+w);
        super.width = w;
    }
    
    /**
     * Define a altura.
     * @param h Altura do retângulo
     */
    public void setHeight(double h)  {
        setLY(this.y+h);        
        super.height = h;
    }
    
    /**
     * Retorna a coordenada X inferior.
     * @return double
     */
    public double getLX() {
        return lx;
    }
    
    /**
     * Retorna a coordenada Y inferior.
     * @return double
     */
    public double getLY() { 
        return ly; 
    }
    
    /**
     * Retorna a coordenada X superior.
     * @return double
     */
    public double getUX() { 
        return this.x; 
    }
    
    /**
     * Retorna a coordenada Y superior.
     * @return double
     */
    public double getUY() { 
        return this.y; 
    }
    
    /**
     * Retorna o centro em relação ao eixo X.
     * @return double
     */
//    @Override
//    public double getCenterX() { 
//        return this.x+getWidth()/2.0; 
//    }
//    
//    /**
//     * Retorna o centro em relação ao eixo Y.
//     * @return 
//     */
//    @Override
//    public double getCenterY() { 
//        return this.y+getHeight()/2.0; 
//    }
    
//    /**
//     * Retorna a largura.
//     * @return double
//     */
//    @Override
//    public double getWidth()  { 
//        return lx-this.x; 
//    }
//    
//    /**
//     * Retorna a altura.
//     * @return double
//     */
//    @Override
//    public double getHeight()  { 
//        return ly-this.y; 
//    }
    
    /**
     * Move o retângulo no eixo X.
     * @param x Quantidade a ser movida
     */
    public void moveX(double x) {
        double q = (x-getWidth()/2.);
        lx = q+getWidth();
        this.x = q;        
    }
    
    /**
     * Move o retângulo no eixo Y.
     * @param y Quantidade a ser movida
     */
    public void moveY(double y) {
        double q = (y-getHeight()/2.);
        ly = q+getHeight();
        this.y = q;
    }
    
    /**
     * Calcula a quantidade de sobreposição de dois retângulos em relação ao eixo X.
     * @param r Retângulo a ser usado
     * @return double
     */
    public double olapX(OverlapRect r) {
        return (getWidth() + r.getWidth())/2. - Math.abs(getCenterX() - r.getCenterX());
    }    
    
    /**
     * Calcula a quantidade de sobreposição de dois retângulos em relação ao eixo Y.
     * @param r Retângulo a ser usado
     * @return double
     */
    public double olapY(OverlapRect r) {
        return (getHeight() + r.getHeight())/2 - Math.abs(getCenterY() - r.getCenterY());
    }
    
    @Override
    public String toString() {
        return "<"+this.x+", "+lx+", "+this.y+", "+ly+">";
    }

    @Override
    public boolean equals(Object obj) {
        OverlapRect robj = (OverlapRect) obj;
        return getCenterX() == robj.getCenterX() && getCenterY() == robj.getCenterY();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (int) ((long)(this.x) ^ ((long)(this.x) >>> 32));
        hash = 73 * hash + (int) ((long)(this.y) ^ ((long)(this.y) >>> 32));
        hash = 73 * hash + (int) ((long)(this.lx) ^ ((long)(this.lx) >>> 32));
        hash = 73 * hash + (int) ((long)(this.ly) ^ ((long)(this.ly) >>> 32));
        hash = 73 * hash + this.id;
        return hash;
    }

    

   
    /**
     * 
     * @param id 
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean isPivot() {
        return pivot;
    }

    @Override
    public void setPivot(boolean pivot) {
        this.pivot = pivot;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }
    
    public int getCluster() {
        return cluster;
    }
    
    public void setCluster(int cluster) {
        this.cluster = cluster;    
    }
    
    public void increment() {
        if( health < 2 )
            health++;
    }
    
    public int getHealth() {
        return health;
    }
}

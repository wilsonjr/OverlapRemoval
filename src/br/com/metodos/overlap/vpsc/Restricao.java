/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

/**
 *
 * @author Thaís
 */
public class Restricao {
    /* Nós u e v, bem como a menor lacuna entre eles*/
    private Variavel left, right;
    private double gap;
    private boolean ativa;
    private long timeStamp;
    private double lm;
    
    public Restricao(Variavel left, Variavel right, double gap) {
        this.left = left;
        this.right = right;
        this.gap = gap;
        this.left.getOut().add(this);
        this.right.getIn().add(this);
    }
    
    
    
    public double getLm() {
        return lm;
    }
    
    public void setLm(double lm) {
        this.lm = lm;
    }

    public Variavel getLeft() {
        return left;
    }

    public void setLeft(Variavel left) {
        this.left = left;
    }

    public Variavel getRight() {
        return right;
    }

    public void setRight(Variavel right) {
        this.right = right;
    }

    public double getGap() {
        return gap;
    }

    public void setGap(double gap) {
        this.gap = gap;
    }
    
    public double getViolationLeft() {
        return left.getPosition()+gap-right.getPosition();
    }
    
    public double getViolationRight() {
        return right.getPosition()-left.getPosition()-gap;
    }
    
    
    public long getTimeStamp() {
        return timeStamp;
    }
    
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
    
    public boolean getAtiva() {
        return ativa;
    }
    
    @Override
    public String toString() {
        return left.getId()+" + "+gap+" <= "+right.getId();
    
    }

}

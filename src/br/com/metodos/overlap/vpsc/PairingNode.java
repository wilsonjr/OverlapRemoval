/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.metodos.overlap.vpsc;

/**
 *
 * @author wilson
 */
public class PairingNode {
    private Restricao elem;
    private PairingNode filhoEsquerdo;
    private PairingNode proxIrmao;
    private PairingNode anterior;
    
    
    public PairingNode(Restricao elem) {
        this.elem = elem;
        this.filhoEsquerdo = null;
        this.proxIrmao = null;
        this.anterior = null;
    }

    public Restricao getElem() {
        return elem;
    }

    public void setElem(Restricao elem) {
        this.elem = elem;
    }

    public PairingNode getFilhoEsquerdo() {
        return filhoEsquerdo;
    }

    public void setFilhoEsquerdo(PairingNode filhoEsquerdo) {
        this.filhoEsquerdo = filhoEsquerdo;
    }

    public PairingNode getProxIrmao() {
        return proxIrmao;
    }

    public void setProxIrmao(PairingNode proxIrmao) {
        this.proxIrmao = proxIrmao;
    }

    public PairingNode getAnterior() {
        return anterior;
    }

    public void setAnterior(PairingNode anterior) {
        this.anterior = anterior;
    }
    
    
    
    
            
}

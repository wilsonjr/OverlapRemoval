/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative;

/**
 *
 * @author Windows
 */
public enum RepresentativeTechniques {
    
    KMEANS(1), KMEDOID(2), DBSCAN(3), BKMEANS(4), FURS(5), AFFINITY_PROPAGATION(6),
    HIERARCHICAL(7), DS3(8), SMRS(9), CSM(10), KSVD(11), GNAT(12), MST(13), SSS(14);
    
    private final int value;
    
    RepresentativeTechniques(int value) {
        this.value = value;
    }
    
    
    @Override
    public String toString() {
        return Class.forName(className);
    }
    
}

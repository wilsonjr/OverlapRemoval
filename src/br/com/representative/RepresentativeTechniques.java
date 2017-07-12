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
        switch( value ) {
            case 1: return "KMeans";
            case 2: return "KMedoid";
            case 3: return "Dbscan";
            case 4: return "BisectingKMeans";
            case 5: return "FURS";
            case 6: return "AffinityPropagation";
            case 7: return "HierarchicalClustering";
            case 8: return "DS3";
            case 9: return "SMRS";
            case 10: return "CSM";
            case 11: return "KSvd";
            case 12: return "GNAT";
            case 13: return "MST";
            case 14: return "SSSS";
            default: return "KMeans";
        }
    }
    
}

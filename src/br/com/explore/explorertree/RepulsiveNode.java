/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.explore.explorertree;

import br.com.methods.utils.OverlapRect;
import java.util.List;

/**
 *
 * @author wilson
 */
public interface RepulsiveNode {
    
    
    public List<OverlapRect> repulsive(List<OverlapRect> elems, int representative, double minWeight, double maxWeight);
    
}

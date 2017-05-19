/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.explore.explorertree;

import br.com.representative.RepresentativeFinder;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Windows
 */
public class ExplorerTreeController {
    private final ExplorerTree _explorerTree;
    private final int _minInstances;
    private final RepresentativeFinder _representativeSelection;
    private final int _sizeInstances;
    private final Point2D.Double[] _projection;
    
    private int[] _representative;
    private Map<Integer, List<Integer>> _nearest;
    
    
    public ExplorerTreeController(Point2D.Double[] projection, RepresentativeFinder representativeSelection, int minInstances, int sizeIntances) {
        _projection = projection;
        _representativeSelection = representativeSelection;
        _minInstances = minInstances;
        _sizeInstances = sizeIntances;
        _explorerTree = new ExplorerTree(projection, representativeSelection, minInstances, minInstances);
        
        _nearest = new HashMap<>();
    }
    
    public void build() {
        
        _explorerTree.build();
        _explorerTree.buildActiveNodes();
        
        _representative = _explorerTree.topNodes().stream().mapToInt((node)->node.routing()).toArray();
        
        _explorerTree.topNodes().stream().forEach((node)->{            
            List<Integer> nearest = new ArrayList<>();
            Arrays.stream(node.indexes()).forEach((v)->nearest.add(v));
            _nearest.put(node.routing(), nearest);
        });
        
    }
    
    
    
    
}

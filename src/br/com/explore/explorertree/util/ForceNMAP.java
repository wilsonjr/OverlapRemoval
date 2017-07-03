/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.explore.explorertree.util;

import br.com.explorer.explorertree.ExplorerTreeController;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nmap.BoundingBox;
import nmap.Element;
import nmap.NMap;

/**
 *
 * @author wilson
 */
public class ForceNMAP implements RepulsiveNode {
    
    private int visualSpaceWidth;
    private int visualSpaceHeight;
    
    public ForceNMAP(int visualSpaceWidth, int visualSpaceHeight) {
        this.visualSpaceWidth = visualSpaceWidth;
        this.visualSpaceHeight = visualSpaceHeight;
    }

    @Override
    public List<OverlapRect> repulsive(List<OverlapRect> elems, int representative, double minWeight, double maxWeight) {
        
        double maxDist = maxDistance(elems, representative);
        
        List<Element> data = new ArrayList<>();
        
        for( int i = 0; i < elems.size(); ++i ) {
            double distance =  Util.euclideanDistance(elems.get(representative).x, elems.get(representative).y, 
                                                      elems.get(i).x, elems.get(i).y);
            
            double weight = ExplorerTreeController.calculateWeight(maxWeight, minWeight, maxDist, distance);
            data.add(new Element(elems.get(i).getId(), (float)elems.get(i).x, (float)elems.get(i).y, (float) weight, 1));            
        }
        
        NMap nmap = new NMap(visualSpaceWidth, visualSpaceHeight);
              
        List<BoundingBox> ac = nmap.alternateCut(data);               
        
        return ac.stream().map(
                   (v)-> new OverlapRect(v.getElement().x, v.getElement().y, 
                                         elems.get(0).width, elems.get(0).height, v.getElement().getId())
               ).collect(Collectors.toList());
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.expadingnode;

import br.com.explore.explorertree.ExplorerTreeController;
import br.com.explore.explorertree.ExplorerTreeNode;
import br.com.methods.utils.OverlapRect;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilson
 */
public class OverlapTree {
    
   private ExplorerTreeController controller;
   private int SIZERECT = 30;
   private List<OverlapRect> rects;
   
   public OverlapTree(ExplorerTreeController controller) {
       this.controller = controller;
   }
   
   
   public void execute() {
       
       OverlapNode node = removeOverlap(controller.explorerTree().topNodes());
       
       rects = new ArrayList<>();
       for( int i = 0; i < node.getInstances().size(); ++i ) {
           rects.add(new OverlapRect(node.getInstances().get(i).getBoundingBox().x, 
                                     node.getInstances().get(i).getBoundingBox().y,
                                     node.getInstances().get(i).getBoundingBox().width,
                                     node.getInstances().get(i).getBoundingBox().height,
                                     node.getInstances().get(i).getBoundingBox().getId()));
       }
       
       
       
   }
   
   private OverlapNode removeOverlap(List<ExplorerTreeNode> nodes) {
       
       List<OverlapNode> elems = new ArrayList<>();
       
       for( int i = 0; i < nodes.size(); ++i ) {
           if( !nodes.get(i).isChild() ) {
               
               OverlapNode overlapNode = removeOverlap(nodes.get(i).children());
               elems.add(overlapNode);
               
           } else {
               
               List<OverlapNode> instances = new ArrayList<>();
                              
               for( int j = 0; j < nodes.get(i).indexes().length; ++j ) {
                   instances.add(new OverlapNode(new OverlapRect(nodes.get(i).subprojection()[j].x, 
                                                                 nodes.get(i).subprojection()[j].y, 
                                                                 SIZERECT, SIZERECT, nodes.get(i).indexes()[j])));
               }
               
               OverlapNode node = new OverlapNode(instances, nodes.get(i).routing());
               node.removeOverlap();
               // it isn't necessary to perform update on nodes
               elems.add(node);
               
           }           
       }
       
       List<OverlapNode> instances = new ArrayList<>();
       int medoid = 0;
       int greater = -1;
       
       for( int i = 0; i < elems.size(); ++i ) {
           instances.add(elems.get(i));
           if( elems.get(i).getInstances().size() > greater ) {
               medoid = i;
               greater = elems.get(i).getInstances().size();
           }
       }
       
       
       OverlapNode node = new OverlapNode(instances, medoid);
       node.removeOverlap();
       
       for( int i = 0; i < node.getInstances().size(); ++i ) {
           
           double deltax = node.getInstances().get(i).getBoundingBox().x-node.getInstances().get(i).getFinalBoundingBox().x;
           double deltay = node.getInstances().get(i).getBoundingBox().y-node.getInstances().get(i).getFinalBoundingBox().y;
           
           node.getInstances().get(i).updatePositions(deltax, deltay);
       }
       
       
       node.updateInstances();
       
       return node;
   }
   
    
    
    
    
    
}

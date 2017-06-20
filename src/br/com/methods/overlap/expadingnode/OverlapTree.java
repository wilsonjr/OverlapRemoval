/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.expadingnode;

import br.com.explore.explorertree.ExplorerTreeController;
import br.com.explore.explorertree.ExplorerTreeNode;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilson
 */
public class OverlapTree {
    
   private ExplorerTreeController controller;
   private int SIZERECT = 20;
   private ArrayList<OverlapRect> rects;
   public ArrayList<OverlapRect> test = new ArrayList<>();
   public int count = 0;
   
   public OverlapTree(ExplorerTreeController controller) {
       this.controller = controller;
   }
   
   
   public void execute() {
       
       System.out.println("Init removing overlap");
       OverlapNode node = removeOverlap(controller.explorerTree().topNodes(), count+1);
//       System.out.println("Finish removing overlap");
       rects = new ArrayList<>();
       for( int i = 0; i < node.getInstances().size(); ++i ) {
           System.out.println(">> "+node.getInstances().get(i).boundingBox.x+"  "+node.getInstances().get(i).boundingBox.y+"  "
                                   +node.getInstances().get(i).boundingBox.width+"  "+node.getInstances().get(i).boundingBox.height);
           rects.add(new OverlapRect(node.getInstances().get(i).boundingBox.x, 
                                     node.getInstances().get(i).boundingBox.y,
                                     node.getInstances().get(i).boundingBox.width,
                                     node.getInstances().get(i).boundingBox.height,
                                     node.getInstances().get(i).boundingBox.getId()));
       }
       
       
       
   }
   
    private OverlapNode removeOverlap(List<ExplorerTreeNode> nodes, int count) {
        
        List<OverlapNode> elems = new ArrayList<>();

        for( int i = 0; i < nodes.size(); ++i ) {
            
            //if( count == 1 && i != 1 )
            //    continue;
            
            if( !nodes.get(i).isChild() ) {
                
                OverlapNode overlapNode = removeOverlap(nodes.get(i).children(), count+1);
                elems.add(overlapNode);

            } else {

                List<OverlapNode> instances = new ArrayList<>();

                for( int j = 0; j < nodes.get(i).indexes().length; ++j ) {

                    instances.add(new OverlapNode(new OverlapRect(nodes.get(i).subprojection()[j].x, 
                                                                  nodes.get(i).subprojection()[j].y, 
                                                                  SIZERECT, SIZERECT, nodes.get(i).indexes()[j])));
                }
                int representative = nodes.get(i).representative(controller.projection());
                OverlapNode node = new OverlapNode(instances, representative);
                node.removeOverlap();

 //               for( int j = 0; j < node.getInstances().size(); ++j ) {
 //                   rects.add(new OverlapRect(node.getInstances().get(j).boundingBox.x, node.getInstances().get(j).boundingBox.y, 
 //                            node.getInstances().get(j).boundingBox.width, node.getInstances().get(j).boundingBox.height, 
 //                           node.getInstances().get(j).boundingBox.getId()));
 //               }

                System.out.println("NODE>> "+node.boundingBox.x+"  "+node.boundingBox.y+"  "
                                        +node.boundingBox.width+"  "+node.boundingBox.height);
                // it isn't necessary to perform update on nodes

            //    if( count == 3 )
                    test.add(node.boundingBox);
                elems.add(node);


            }           
        }

        int medoid = 0;
        int greater = -1;

        for( int i = 0; i < elems.size(); ++i ) {
            if( elems.get(i).getInstances().size() > greater ) {
                medoid = i;
                greater = elems.get(i).getInstances().size();
            }
        }


        OverlapNode envolvingNode = new OverlapNode(elems, medoid);
        envolvingNode.updateInstances();
        
        
        Point p = new Point(0,0);
        for( int i = 0; i < envolvingNode.getInstances().size(); ++i ) {
            
            p.x += envolvingNode.getInstances().get(i).boundingBox.x;
            p.y += envolvingNode.getInstances().get(i).boundingBox.y;
            
            
        }
        p.x /= (double)envolvingNode.getInstances().size();
        p.y /= (double)envolvingNode.getInstances().size();
        
        double d = Double.MAX_VALUE;
        for( int i = 0; i < envolvingNode.getInstances().size(); ++i ) {
            
            double x = envolvingNode.getInstances().get(i).boundingBox.x;
            double y = envolvingNode.getInstances().get(i).boundingBox.y;
            
            double dist = Util.euclideanDistance(p.x, p.y, x, y);
            if( dist < d ) {
                medoid = i;
                d = dist;
            }
                
        }
        
       
        envolvingNode.setRepresentative(medoid);
        
        
        
        
        envolvingNode.removeOverlap();
        //if( count == 3 )
        //envolvingNode.calculateBoundingBox();
        test.add(envolvingNode.boundingBox);

//        for( int i = 0; i < envolvingNode.getInstances().size(); ++i ) {
//
//            double deltax = envolvingNode.getInstances().get(i).boundingBox.x-envolvingNode.getInstances().get(i).finalBoundingBox.x;
//            double deltay = envolvingNode.getInstances().get(i).boundingBox.y-envolvingNode.getInstances().get(i).finalBoundingBox.y;
//
//            System.out.println("Deltax: "+deltax);
//            System.out.println("Deltay: "+deltay);
//
//            envolvingNode.getInstances().get(i).updatePositions(deltax, deltay);
//        }
////
////
////
//        envolvingNode.updateInstances();

        return envolvingNode;
    }

    public ArrayList<OverlapRect> getProjection() {
        return rects;
    }
   
    
    
    
    
    
}

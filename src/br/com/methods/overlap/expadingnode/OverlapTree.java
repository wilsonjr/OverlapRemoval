/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.methods.overlap.expadingnode;

import br.com.explore.explorertree.ExplorerTreeController;
import br.com.explore.explorertree.ExplorerTreeNode;
import br.com.methods.overlap.OverlapRemoval;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wilson
 */
public class OverlapTree implements OverlapRemoval {

    private ExplorerTreeController controller;
    private int SIZERECT = 20;
    private int additionalIterations = 0;
    
    public OverlapTree(ExplorerTreeController controller, int additionalIterations) {
        this.controller = controller;
        this.additionalIterations = additionalIterations;
    }

    public OverlapTree(ExplorerTreeController controller) {
        this(controller, 0);
    }

    private OverlapNode removeOverlap(List<ExplorerTreeNode> nodes) {
        List<OverlapNode> elems = new ArrayList<>();

        for (int i = 0; i < nodes.size(); ++i) {
            if (!nodes.get(i).isChild()) {

                OverlapNode overlapNode = removeOverlap(nodes.get(i).children());
                elems.add(overlapNode);

            } else {

                List<OverlapNode> instances = new ArrayList<>();

                for (int j = 0; j < nodes.get(i).indexes().length; ++j) {
                    instances.add(new OverlapNode(new OverlapRect(nodes.get(i).subprojection()[j].x,
                            nodes.get(i).subprojection()[j].y,
                            SIZERECT, SIZERECT, nodes.get(i).indexes()[j])));
                }
                int representative = nodes.get(i).representative(controller.projection());
                OverlapNode node = new OverlapNode(instances);
                node.removeOverlap(representative);

                elems.add(node);
            }
        }

        int medoid = 0;
        int greater = -1;

        for (int i = 0; i < elems.size(); ++i) {
            if (elems.get(i).getInstances().size() > greater) {
                medoid = i;
                greater = elems.get(i).getInstances().size();
            }
        }

        OverlapNode envolvingNode = new OverlapNode(elems);
        envolvingNode.updateInstances();

        Point p = new Point(0, 0);
        for (int i = 0; i < envolvingNode.getInstances().size(); ++i) {
            p.x += envolvingNode.getInstances().get(i).boundingBox.x;
            p.y += envolvingNode.getInstances().get(i).boundingBox.y;
        }
        p.x /= (double) envolvingNode.getInstances().size();
        p.y /= (double) envolvingNode.getInstances().size();

        double d = Double.MAX_VALUE;
        for (int i = 0; i < envolvingNode.getInstances().size(); ++i) {

            double x = envolvingNode.getInstances().get(i).boundingBox.x;
            double y = envolvingNode.getInstances().get(i).boundingBox.y;

            double dist = Util.euclideanDistance(p.x, p.y, x, y);
            if( dist < d ) {
                medoid = i;
                d = dist;
            }
        }

        envolvingNode.removeOverlap(medoid);

        return envolvingNode;
    }

    @Override
    public Map<OverlapRect, OverlapRect> apply(ArrayList<OverlapRect> rects) {
        Map<OverlapRect, OverlapRect> map = null;
        
        for( int iter = 0; iter <= additionalIterations; ++iter ) {
            
            controller.build();

            OverlapNode node = removeOverlap(controller.explorerTree().topNodes());
            ArrayList<OverlapRect> reprojected = new ArrayList<>();

            for( int i = 0; i < node.getInstances().size(); ++i ) {
                reprojected.add(new OverlapRect(node.getInstances().get(i).boundingBox.x, node.getInstances().get(i).boundingBox.y,
                        node.getInstances().get(i).boundingBox.width, node.getInstances().get(i).boundingBox.height,
                        node.getInstances().get(i).boundingBox.getId()));
            }

            Collections.sort(reprojected, (a, b) -> {
                return Integer.compare(a.getId(), b.getId());
            });

            map = new HashMap<>();
            for (int i = 0; i < rects.size(); ++i) {
                System.out.println("*> "+rects.get(i).getId()+" -- "+reprojected.get(i).getId()+"<*");
                map.put(rects.get(i), reprojected.get(i));
            }

            rects = Util.getProjectedValues(map);
            Collections.sort(rects, (a, b)-> {
                return Integer.compare(a.getId(), b.getId());
            });
            

            Point2D.Double[] projection = rects.stream().map((v) -> new Point2D.Double(v.x, v.y)).toArray(Point2D.Double[]::new);
            Point2D.Double[] projectionCenter = rects.stream().map((v) -> new Point2D.Double(v.getCenterX(), v.getCenterY()))
                    .toArray(Point2D.Double[]::new);
            
            controller.setProjection(projection);
            controller.setProjectionCenter(projectionCenter);
        }
        

        return map;
    }

}

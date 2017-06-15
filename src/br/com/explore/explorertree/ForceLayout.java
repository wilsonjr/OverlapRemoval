/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.explore.explorertree;

import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Util;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author wilson
 */
public class ForceLayout implements RepulsiveNode {

    @Override
    public List<OverlapRect> repulsive(List<OverlapRect> elems, int representative, double minWeight, double maxWeight) {
        
        List<OverlapRect> after = new ArrayList<>();
        double maxDist = maxDistance(elems, representative);
        
        for( int i = 0; i < elems.size(); ++i ) {
            Point2D dxy = new Point2D.Double();
            
            if( representative == i ) {
                after.add(new OverlapRect(elems.get(representative).x, elems.get(representative).y, 
                                          elems.get(representative).width, elems.get(representative).height, 
                                          elems.get(representative).getId()));
                continue;
            }
            
            double ax = elems.get(representative).x;
            double ay = elems.get(representative).y;
            double bx = elems.get(i).x;
            double by = elems.get(i).y;
            double lenAB = Util.euclideanDistance(ax, ay, bx, by);
            
            double weight = ExplorerTreeController.calculateWeight(maxWeight, minWeight, maxDist, lenAB);
            
            Point2D dir = computeForceIJ(elems.get(representative), elems.get(i), weight);
            dxy.setLocation(dxy.getX()-dir.getX(), dxy.getY()-dir.getY());
            
            double x = elems.get(i).getX() + dxy.getX();
            double y = elems.get(i).getY() + dxy.getY();            
            after.add(new OverlapRect(x, y, elems.get(i).width, elems.get(i).height, elems.get(i).getId()));
            
        }
        
        return after;
        
        
    }
    
    protected Point2D computeForceIJ(Rectangle2D.Double rectI, Rectangle2D.Double rectJ, double KR) {
        
        double hix = Math.min(rectI.getMaxX(), rectJ.getMaxX());
        double lox = Math.max(rectI.getMinX(), rectJ.getMinX());
        double hiy = Math.min(rectI.getMaxY(), rectJ.getMaxY());
        double loy = Math.max(rectI.getMinY(), rectJ.getMinY());
        
        double dx = (hix-lox) * 1.1;
        double dy = (hiy-loy) * 1.1;
        
        double f = KR*Math.min(dx, dy);
        Point2D dir = new Point2D.Double(rectJ.getCenterX()-rectI.getCenterX(), rectJ.getCenterY()-rectI.getCenterY());
        double distanceToOrigin = Util.euclideanDistance(dir.getX(), dir.getY(), 0, 0);
        if( distanceToOrigin < 1e-5 ) {
            Random rand = new Random(2);
            dir = new Point2D.Double(-1 + 2*rand.nextDouble(), -1 + 2*rand.nextDouble());
        } else
            dir.setLocation(dir.getX()*(1.0/distanceToOrigin), dir.getY()*(1.0/distanceToOrigin));
            
        dir.setLocation(dir.getX()*f, dir.getY()*f);
        
        return dir;       
    }
    
}

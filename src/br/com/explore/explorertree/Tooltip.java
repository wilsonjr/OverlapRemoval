/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.explore.explorertree;

import br.com.methods.utils.OverlapRect;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 *
 * @author Windows
 */
public class Tooltip {
    
    private List<OverlapRect> projected;
    private Rectangle2D rect;
    private Point2D.Double point;
    private int ammountx, ammounty;
    
    public Tooltip(Point2D.Double point, List<OverlapRect> projected) {
        this.point = point;
        this.projected = projected;
        adjustPanel();
    }
    
    public void draw(Graphics2D g2) {
        
        g2.setColor(Color.WHITE);
        g2.fill(rect);
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
        g2.setColor(Color.BLACK);
        g2.draw(rect);
        g2.setColor(Color.BLUE);
        
        projected.stream().forEach((p)-> {
            
            int x = (int) p.getUX()+ammountx;
            int y = (int) p.getUY()+ammounty;
            
            g2.fillOval(x+15, y+15, (int)p.getWidth(), (int)p.getHeight());
        
        });
        
        
    }
    
    private void adjustPanel() {
        if( projected == null || projected.isEmpty() )
            return;

        double max_x = -1, max_y = -1;
        double min_x = Double.MAX_VALUE, min_y = Double.MAX_VALUE;
        int zero = 30;

        for( int i = 0; i < projected.size(); i++ ) {
            double x = projected.get(i).getCenterX();
            if (max_x < x)
                max_x = x;
            if (min_x > x)
                min_x = x;

            double y = projected.get(i).getCenterY();
            if (max_y < y)
                max_y = y;
            if (min_y > y)
                min_y = y;
        }
        
        ammountx = (int) (point.x-min_x);
        ammounty = (int) (point.y-min_y);
        
        Dimension d = new Dimension();
        d.width = (int) (max_x-min_x) + zero;
        d.height = (int) (max_y-min_y) + zero;
        rect = new Rectangle2D.Double(point.x, point.y, d.width, d.height);
    }
    
}

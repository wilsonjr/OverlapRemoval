/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.representative.metric;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilson
 */
public class SlimTreeNode implements Comparable<SlimTreeNode> {
    public List<Point2D.Double> points;
    
    public SlimTreeNode(List<Point2D.Double> points) {
        this.points = points;
    }
    
    public SlimTreeNode() {
        points = new ArrayList<>();
    }
    
    public void add(Point2D.Double p) {
        points.add(p);
    }
    
    public int size() {
        return points.size();
    }

    @Override
    public int compareTo(SlimTreeNode o) {
        return Integer.compare(size(), o.size());
    }

    public Point2D.Double get(int i) {
        return points.get(i);
    }
}

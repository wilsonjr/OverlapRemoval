/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.explore.explorertree;

import br.com.methods.utils.Util;
import br.com.representative.RepresentativeFinder;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import math.geom2d.polygon.SimplePolygon2D;

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
    
    private List<Polygon> _polygons;
    
    private Map<Point2D.Double, Polygon> _pointPolygon = new HashMap<>();
    
    
    public ExplorerTreeController(Point2D.Double[] projection, RepresentativeFinder representativeSelection, 
                                  int minInstances, int sizeIntances, int distinctionDistance) {
        _projection = projection;
        _representativeSelection = representativeSelection;
        _minInstances = minInstances;
        _sizeInstances = sizeIntances;
        _explorerTree = new ExplorerTree(projection, representativeSelection, distinctionDistance, minInstances);
        
        _nearest = new HashMap<>();
        _polygons = new ArrayList<>();        
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
    
    public void updateDiagram(int width, int height, int indexNewRepresentative, Polygon clickedPolygon) {
        
        Polygon window = new Polygon();
        window.addPoint(0, 0);
        window.addPoint(width, 0);
        window.addPoint(width, height);
        window.addPoint(0, height);
        
        Point2D.Double[] points = new Point2D.Double[_representative.length-indexNewRepresentative];
        for( int i = 0; i < points.length; ++i ) {
            int index = _representative[i+indexNewRepresentative];
            points[i] = new Point2D.Double(_projection[index].x + _sizeInstances/2, _projection[index].y + _sizeInstances/2);
        }
        
        int n = (int) Arrays.stream(points).distinct().count();
        Iterator<Point2D.Double> iterator = Arrays.stream(points).distinct().iterator();
        int k = 0;
        points = new Point2D.Double[n];
        while( iterator.hasNext() ) {
            points[k++] = iterator.next();
        }
        
        Point2D.Double[] involvePolygon = null;
        
        if( clickedPolygon == null ) {
            
            Point2D.Double[] pointsPolygon = new Point2D.Double[_projection.length];
            for( int i = 0; i < _projection.length; ++i )
                pointsPolygon[i] = new Point2D.Double(_projection[_representative[i]].x + _sizeInstances/2, 
                                                   _projection[_representative[i]].y + _sizeInstances/2);        
            involvePolygon = Util.convexHull(pointsPolygon);
            
        } else {
            
            involvePolygon = new Point2D.Double[clickedPolygon.npoints];
            for( int i = 0; i < clickedPolygon.npoints; ++i )
                involvePolygon[i] = new Point2D.Double(clickedPolygon.xpoints[i], clickedPolygon.ypoints[i]);
            
        }
        
                
        List<Point2D.Double> pVoronoi = new ArrayList<>();
        Polygon[] diagrams = Util.voronoiDiagram(window, points, pVoronoi);
        _polygons.addAll(new ArrayList<>(Arrays.asList(Util.clipBounds(diagrams, involvePolygon, _pointPolygon, pVoronoi))));
        
    }
    
    
    public int indexRepresentative(double x, double y) {
        int index = -1;
        
        for( int i = 0; i < _representative.length; ++i ) {
            double cx = _projection[_representative[i]].x + _sizeInstances/2;
            double cy = _projection[_representative[i]].y + _sizeInstances/2;
            if( Util.euclideanDistance(x, y, cx, cy) < _sizeInstances/2 ) {
                index = _representative[i];
                break;
            }
        }
        
        return index;
    }
    
    public ExplorerTreeNode getNode(int index) {
        return _explorerTree.activeNodes().get(index);
    }
    
    public Polygon clickedPolygon(double x, double y) {
        Polygon clickedPolygon = null;
        
        for( Polygon p: _polygons ) {
            SimplePolygon2D sp = new SimplePolygon2D();
            for( int i = 0; i < p.xpoints.length; ++i )
                sp.addVertex(new math.geom2d.Point2D(p.xpoints[i], p.ypoints[i]));
            if( sp.contains(x, y) )
                clickedPolygon = p;
        }
        
        return clickedPolygon;
    }
    
    public int expandNode(int index, Polygon clickedPolygon) {
        ExplorerTreeNode node = _explorerTree.activeNodes().get(index);
        
        _explorerTree.expandNode(index, clickedPolygon);
        int[] reps = new int[node.children().size() + _representative.length-1];
        
        int j = 0;
        for( int i = 0; i < _representative.length; ++i )
            if( _representative[i] != index )
                reps[j++] = _representative[i];
        
        int indexNewRepresentative = j;
        
        for( ExplorerTreeNode n: node.children() )
            reps[j++] = n.routing();
        
        _representative = Arrays.copyOf(reps, reps.length);
        
        for( ExplorerTreeNode n: node.children() ) {
            List<Integer> nearest = new ArrayList<>();
            Arrays.stream(n.indexes()).forEach((i)->nearest.add(i));
            _nearest.put(n.routing(), nearest);
        }
        
        _polygons.remove(clickedPolygon);
        return indexNewRepresentative;
    }
    
    
    
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.explore.explorertree;

import br.com.methods.utils.Util;
import br.com.methods.utils.Vect;
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

    private ExplorerTree _explorerTree;
    private final int _minInstances;
    private final RepresentativeFinder _representativeSelection;
    private final int _sizeInstances;
    private int _distinctionDistance;
    private Point2D.Double[] _projection;
    private Point2D.Double[] _projectionCenter;

    private int[] _representative;
    private Map<Integer, List<Integer>> _nearest;

    private List<Polygon> _polygons;

    private Map<ExplorerTreeNode, Polygon> _pointPolygon = new HashMap<>();
    
    private Vect[] _dataset;
    
    private Map<Integer, Point2D.Double> indexProjection = new HashMap<>();
    
    private static Point2D.Double[] incrementalProjection = null;
    private static Point2D.Double[] incrementalProjectionBefore = null;

    public ExplorerTreeController(Vect[] dataset, Point2D.Double[] projection, Point2D.Double[] projectionCenter,
            RepresentativeFinder representativeSelection,
            int minInstances, int sizeIntances, int distinctionDistance) {
        
        _dataset = dataset;
        _projectionCenter = projectionCenter;
        _projection = projection;
        _representativeSelection = representativeSelection;
        _minInstances = minInstances;
        _sizeInstances = sizeIntances;
        _distinctionDistance = distinctionDistance;

        _nearest = new HashMap<>();
        _polygons = new ArrayList<>();
    }

    public void setProjection(Point2D.Double[] _projection) {
        this._projection = _projection;
    }

    public void setProjectionCenter(Point2D.Double[] _projectionCenter) {
        this._projectionCenter = _projectionCenter;
    }

    public ExplorerTree explorerTree() {
        return _explorerTree;
    }

    public int getMinInstances() {
        return _minInstances;
    }

    public RepresentativeFinder getRepresentativeSelection() {
        return _representativeSelection;
    }

    public int getDistinctionDistance() {
        return _distinctionDistance;
    }
    
    private void normalizeProjection(List<ExplorerTreeNode> nodes) {
        
        
        for( int i = 0; i < nodes.size(); ++i ) {
            ExplorerTreeNode node = nodes.get(i);
            
            System.out.println("NODE ROUTING: "+node.routing()+" "+node.pointRep().x+" "+node.pointRep().y);
            
            List<ExplorerTreeNode> ch = node.children();
            
            double deltax = 0;
            double deltay = 0;
            
            if( !node.isChild() ) {
                System.out.println("isn't child");
                int idx = -1;
                
                for( int j = 0; j < ch.size(); ++j ) {
                    if( ch.get(j).routing() == node.routing() ) {
                        idx = j;
                        break;
                    }
                }
                
                if( idx != -1 ) {
                    
                    System.out.println("idx != -1");
                    
                    System.out.println("point>> "+ch.get(idx).pointRep().x+" "+ch.get(idx).pointRep().y);
                    
                    deltax = node.pointRep().x - ch.get(idx).pointRep().x;
                    deltay = node.pointRep().y - ch.get(idx).pointRep().y;
                    
                    for( int j = 0; j < ch.size(); ++j ) 
                        ch.get(j).normalize(deltax, deltay, node);
                    
                } else {
                    
                    int[] indexes = new int[ch.size()+1];
                    indexes[0] = node.routing();
                    
                    for( int j = 0; j < ch.size(); ++j )
                        indexes[j+1] = ch.get(j).routing();
                    
                    Point2D.Double[] points = Util.projectData(indexes, _dataset, node.subprojection().length);
                    
                    
                    System.out.println("("+node.pointRep().x+","+node.pointRep().y+")");
                    for( int j = 0; j < points.length; ++j ) {
                        System.out.println("    >> "+points[j].x+"  "+points[j].y);
                    }
                    
                    deltax = node.pointRep().x - points[0].x;
                    deltay = node.pointRep().y - points[0].y;
                    
                    
                    for( int j = 0; j < ch.size(); ++j ) 
                        ch.get(j).normalize(deltax, deltay, points[j+1]);
                    
                }
                
                
            
            
                normalizeProjection(node.children());
                
            } else {
                System.out.println("is child");
                node.projectData();
                deltax = node.pointRep().x - node.subsetProjection()[node.indexRep()].x;
                deltay = node.pointRep().y - node.subsetProjection()[node.indexRep()].y;
                node.normalize(deltax, deltay, node);
            }
            
            
        }
    }
    
    private void viewTree(List<ExplorerTreeNode> nodes, String distance) {
        
        for( int i = 0; i < nodes.size(); ++i ) {
            ExplorerTreeNode node = nodes.get(i);
            System.out.println(distance+"routing: "+node.routing()+", point("+node.pointRep().x+","+node.pointRep().y+")");
            viewTree(node.children(), distance+"-----");            
        }
        
    }
    
    

    public void build() {
        _explorerTree = new ExplorerTree(_dataset, _representativeSelection, _distinctionDistance, _minInstances);
        _explorerTree.build();
        _explorerTree.buildActiveNodes();

        _representative = _explorerTree.topNodes().stream().mapToInt((node) -> node.routing()).toArray();

        _explorerTree.topNodes().stream().forEach((node) -> {
            List<Integer> nearest = new ArrayList<>();
            Arrays.stream(node.indexes()).forEach((v) -> nearest.add(v));
            _nearest.put(node.routing(), nearest);
        });
        
        
        System.out.println("Init Normalizing nodes");
        viewTree(_explorerTree.topNodes(), "");
        normalizeProjection(_explorerTree.topNodes());
        viewTree(_explorerTree.topNodes(), "");
        for( ExplorerTreeNode node: _explorerTree.topNodes() ) {
            System.out.println("pointRep: "+node.pointRep().x+" "+node.pointRep().y);
            addPointsBefore(node.pointRep());
        }
        System.out.println("Finishing Normalizing nodes");
    }

    public List<Integer> updateDiagram(int width, int height, int indexNewRepresentative, Polygon clickedPolygon) {
        List<Integer> addedIndexes = new ArrayList<>();

        Polygon window = new Polygon();
        window.addPoint(0, 0);
        window.addPoint(width, 0);
        window.addPoint(width, height);
        window.addPoint(0, height);
        
        Point2D.Double[] points = new Point2D.Double[_representative.length - indexNewRepresentative];

        Map<Point2D.Double, Integer> pointIndex = new HashMap<>();
        for (int i = 0; i < points.length; ++i) {
            int index = _representative[i + indexNewRepresentative];
            addedIndexes.add(index);
            points[i] = new Point2D.Double(_explorerTree.indexesProjection()[i + indexNewRepresentative].x, 
                                           _explorerTree.indexesProjection()[i + indexNewRepresentative].y);
            indexProjection.put(index, points[i]);
            System.out.println(">> "+_explorerTree.indexesProjection()[i + indexNewRepresentative].x+" "+
                                     _explorerTree.indexesProjection()[i + indexNewRepresentative].y);
            pointIndex.put(points[i], index);
        }

        int n = (int) Arrays.stream(points).distinct().count();
        Iterator<Point2D.Double> iterator = Arrays.stream(points).distinct().iterator();
        int k = 0;
        points = new Point2D.Double[n];
        while (iterator.hasNext()) {
            points[k++] = iterator.next();
        }

        Point2D.Double[] involvePolygon;

        if (clickedPolygon == null) {
            Point2D.Double[] pointsPolygon = new Point2D.Double[incrementalProjection.length];
            for (int i = 0; i < incrementalProjection.length; ++i) {
                pointsPolygon[i] = new Point2D.Double(incrementalProjection[i].x, incrementalProjection[i].y);
                System.out.println(">> "+incrementalProjection[i].x+" "+incrementalProjection[i].y);
                
            }
            involvePolygon = Util.convexHull(pointsPolygon);

        } else {

            involvePolygon = new Point2D.Double[clickedPolygon.npoints];
            
            for (int i = 0; i < clickedPolygon.npoints; ++i) {
                involvePolygon[i] = new Point2D.Double(clickedPolygon.xpoints[i], clickedPolygon.ypoints[i]);
            }

        }
        
        List<Point2D.Double> pVoronoi = new ArrayList<>();
        Polygon[] diagrams = Util.voronoiDiagram(window, points, pVoronoi);
        _polygons.addAll(new ArrayList<>(Arrays.asList(Util.clipBounds(diagrams, involvePolygon, _pointPolygon,
                pVoronoi, this, pointIndex))));

        return addedIndexes;
    }

    public int sizeRepresentative(int size) {
        // return _sizeIntances/2;
        return (int) (5.0 * Math.sqrt(size));
    }

    public int indexRepresentative(double x, double y) {
        int index = -1;

        for (int i = 0; i < _representative.length; ++i) {
            double cx = _projectionCenter[_representative[i]].x;
            double cy = _projectionCenter[_representative[i]].y;
            if (Util.euclideanDistance(x, y, cx, cy) < sizeRepresentative(nearest().get(_representative[i]).size())) {
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

        for (Polygon p : _polygons) {
            SimplePolygon2D sp = new SimplePolygon2D();
            for (int i = 0; i < p.xpoints.length; ++i) {
                sp.addVertex(new math.geom2d.Point2D(p.xpoints[i], p.ypoints[i]));
            }
            if (sp.contains(x, y)) {
                clickedPolygon = p;
            }
        }

        return clickedPolygon;
    }

    public int expandNode(int index, Polygon clickedPolygon) {
        ExplorerTreeNode node = _explorerTree.activeNodes().get(index);

        _explorerTree.expandNode(index, clickedPolygon);
        int[] reps = new int[node.children().size() + _representative.length - 1];

        int j = 0;
        for (int i = 0; i < _representative.length; ++i) {
            if (_representative[i] != index) {
                reps[j++] = _representative[i];
            }
        }

        int indexNewRepresentative = j;

        for (ExplorerTreeNode n : node.children()) {
            reps[j++] = n.routing();
        }

        _representative = Arrays.copyOf(reps, reps.length);

        for (ExplorerTreeNode n : node.children()) {
            List<Integer> nearest = new ArrayList<>();
            Arrays.stream(n.indexes()).forEach((i) -> nearest.add(i));
            _nearest.put(n.routing(), nearest);
        }

        _polygons.remove(clickedPolygon);
        return indexNewRepresentative;
    }

    public List<Integer> agglomerateNode(int index) {

        ExplorerTreeNode node = _explorerTree.activeNodes().get(index);
        ExplorerTreeNode parent = node.parent();

        List<Integer> indexes = _explorerTree.filterNodes(parent);
        for (Integer v : indexes) {
            //Polygon associatedPolygon = _pointPolygon.get(new Point2D.Double(_projectionCenter[v].x, _projectionCenter[v].y));
            Polygon associatedPolygon = _pointPolygon.get(getNode(v));
            _polygons.remove(associatedPolygon);
        }

        int[] reps = new int[(_representative.length - indexes.size()) + 1];
        int j = 0;
        for (int i = 0; i < _representative.length; ++i) {
            if (!indexes.contains(_representative[i])) {
                reps[j++] = _representative[i];
            }
        }
        reps[j] = parent.routing();

        _representative = Arrays.copyOf(reps, reps.length);
        _polygons.add(parent.polygon());

        return indexes;
    }

    public int[] representative() {
        return _representative;
    }

    public Map<Integer, List<Integer>> nearest() {
        return _nearest;
    }

    public Point2D.Double[] projectionCenter() {
        return _projectionCenter;
    }

    public Point2D.Double[] projection() {
        return _projection;
    }

    public Polygon polygon(int index) {
        return _pointPolygon.get(getNode(index));
    }

    public List<Integer> expandNode(int index, int x, int y, int width, int height) {
        Polygon polygon = clickedPolygon(x, y);
        int indexNew = expandNode(index, polygon);
        return updateDiagram(width, height, indexNew, polygon);
    }
    
    public Point2D.Double getPoint(int index) {
        return indexProjection.get(index);
    }

    // returns weights in [0.5, 1]
    public static double calculateWeight(double maxWeight, double minWeight, double maxDistance, double distance) {

        double a = (maxWeight - minWeight) / 2.0;  // half of half of amplitude
        double q = (maxWeight + minWeight) / 2.0;
        double k = maxDistance;
        double period = (2.0 * Math.PI) / k;

        return a * Math.cos(period * distance) + q;
    }
    
    public static void addPoints(Point2D.Double[] points) {
        if( incrementalProjection == null ) {
            
            incrementalProjection = Arrays.copyOf(points, points.length);
            
        } else {
            Point2D.Double[] newProjection = new Point2D.Double[incrementalProjection.length+points.length];
            int i = 0;
            for( ; i < incrementalProjection.length; ++i ) {
                newProjection[i] = incrementalProjection[i];
            }
            
            for( int j = 0; j < points.length; ++j, ++i )
                newProjection[i] = points[j];
            incrementalProjection = Arrays.copyOf(newProjection, newProjection.length);
        }
    }
    
    public static void addPointsBefore(Point2D.Double[] points) {
        if( incrementalProjectionBefore == null ) {
            
            incrementalProjectionBefore = Arrays.copyOf(points, points.length);
            
        } else {
            Point2D.Double[] newProjection = new Point2D.Double[incrementalProjectionBefore.length+points.length];
            int i = 0;
            for( ; i < incrementalProjectionBefore.length; ++i ) {
                newProjection[i] = incrementalProjectionBefore[i];
            }
            
            for( int j = 0; j < points.length; ++j, ++i )
                newProjection[i] = points[j];
            incrementalProjectionBefore = Arrays.copyOf(newProjection, newProjection.length);
        }
    }
    
    public static void addPoints(Point2D.Double point) {
        if( incrementalProjection == null ) {
            
            incrementalProjection = new Point2D.Double[1];
            incrementalProjection[0] = point;
            
        } else {
            Point2D.Double[] newProjection = new Point2D.Double[incrementalProjection.length+1];
            int i = 0;
            for( ; i < incrementalProjection.length; ++i ) {
                newProjection[i] = incrementalProjection[i];
            }
            
            newProjection[i] = point;
            incrementalProjection = Arrays.copyOf(newProjection, newProjection.length);
                    
        }
    }
    
    public static void addPointsBefore(Point2D.Double point) {
        if( incrementalProjectionBefore == null ) {
            
            incrementalProjectionBefore = new Point2D.Double[1];
            incrementalProjectionBefore[0] = point;
            
        } else {
            Point2D.Double[] newProjection = new Point2D.Double[incrementalProjectionBefore.length+1];
            int i = 0;
            for( ; i < incrementalProjectionBefore.length; ++i ) {
                newProjection[i] = incrementalProjectionBefore[i];
            }
            
            newProjection[i] = point;
            incrementalProjectionBefore = Arrays.copyOf(newProjection, newProjection.length);
        }
    }

    public Point2D.Double[] getIncrementalInstances() {
        return incrementalProjection;
    }
    
    public Point2D.Double[] getIncrementalInstancesBefore() {
        return incrementalProjectionBefore;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.representative.sampling;

import br.com.methods.utils.Vect;
import br.com.representative.clustering.Partitioning;
import br.com.representative.sampling.QuadTree.XYPoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Windows
 */
public class SADIRE extends Partitioning {
    private final int alpha;
    private final int size;

    public SADIRE(List<Vect> items, int size, int alpha) {
        super(items);
        
        this.alpha = alpha;
        this.size = size;
    }

    @Override
    public void execute() {
        
        double maxX = -1, maxY = -1;
        
        for( Vect v: items ) {
            maxX = Math.max(v.get(0), maxX);
            maxY = Math.max(v.get(1), maxY);
        }
        
        QuadTree.PointRegionQuadTree quadtree = new QuadTree.PointRegionQuadTree(0, 0, maxX+10, maxY+10, items.size());
                
        
        for( int i = 0; i < items.size(); ++i ) {
            quadtree.insert(items.get(i).get(0), items.get(i).get(1), i);
        }
        
        int maxy = (int) (maxX/size + 50);
        int maxx = (int) (maxY/size + 50);
        
        List<Cell> cells = new ArrayList<>();
        
        for( int i = 1; i <= maxy; ++i ) {
            for( int j = 1; j <= maxx; ++j ) {
                Collection<XYPoint> d = quadtree.queryRange((i-1)*size, (j-1)*size, size, size);
                if( !d.isEmpty() )
                    cells.add(new Cell((i-1)*size, (j-1)*size, d));                
            }
        }
                
        int sizeSearch = 1;
        
        List<XYPoint> samples = sadire(quadtree, cells, size, alpha, sizeSearch, maxx, maxy, 1);
        
        List<Integer> indexes = new ArrayList<>();
        
        samples.forEach((xyp) -> { indexes.add(xyp.id); });
        
        representatives = indexes.stream().mapToInt(Integer::intValue).toArray();     
        
        
    }

    private List<XYPoint> sadire(QuadTree quadtree, List<Cell> cells, int delta, int alpha, int sizeSearch, int maxx, int maxy, int fraction) {
        
        List<XYPoint> sampledSet = new ArrayList<>();
        
        List<Element> A = new ArrayList<>();
        
        for( int i = 0; i < cells.size(); ++i ) {
            
            List<XYPoint> points = new ArrayList<>(quadtree.queryRange(cells.get(i).x, cells.get(i).y, 
                                                             delta, delta));
            
            if( sizeSearch == 1 ) {
                
                A.add(new Element(cells.get(i), points.get((int) Math.floor(Math.random()*points.size())), points, true));
            } else {
                
            }
            
        }
        
        A.sort((a, b) -> {
            return Integer.compare(b.points.size(), a.points.size());
        });
        
        for( int i = 0; i < A.size(); ++i ) {
            
            if( A.get(i).active ) {
                
                boolean hasNeighbors = false;
                int dMatrix = 2*alpha+1;
                int midPoint = (dMatrix-1)/2;
                
                for( int j = 0; j < dMatrix; ++j ) {
                    for( int k = 0; k < dMatrix; ++k ) {
                        int dx = (A.get(i).box.x - midPoint*delta) + j*delta;
                        int dy = (A.get(i).box.y - midPoint*delta) + k*delta;

                        if( dx == A.get(i).box.x && dy == A.get(i).box.y ) {
                            continue;
                        }

                        if( dx >= 0 && dx <= Math.ceil(maxy*delta) && 
                            dy >= 0 && dy <= Math.ceil(maxx*delta) ) {
                            
                            Element neighbor = findBox(A, dx, dy);
                            if( neighbor != null ) {
                                neighbor.active = false;
                                hasNeighbors = true;
                            }
                        }
                    }
                }
                
                
                if( hasNeighbors ) {
                    A.get(i).active = false;
                    
                    sampledSet.add(A.get(i).representative);
                }
            }
        }

        for( int i = 0; i < A.size(); ++i ) {
            if( A.get(i).active ) {
                A.get(i).active = false;

                XYPoint point = A.get(i).points.get((int) Math.floor(Math.random() * A.get(i).points.size()));

                double minDistance = 1000000.0;
                XYPoint neighbor = null;

                for( int j = 0; j < sampledSet.size(); ++j ) {

                    double d = Math.abs(sampledSet.get(j).x-point.x) + Math.abs(sampledSet.get(j).y-point.y);

                    if( d <= (2.0/fraction)*delta && minDistance > d ) {
                        minDistance = d;
                        neighbor = sampledSet.get(j);
                    }
                }

                if( neighbor == null ) {


                    if( sizeSearch == 1 ) {
                        sampledSet.add(point);
                    } else {
                        // TODO implement for percentage 
                    }

                }
            }
        }

        return sampledSet;
    }

    private Element findBox(List<Element> A, int x, int y) {
        for( int i = 0; i < A.size(); ++i ) {
            if( A.get(i).active && A.get(i).box.x == x && A.get(i).box.y == y )
                return A.get(i);
        }

        return null;
    }
    
    
    public class Cell {
        
        public int x, y;
        public Collection<XYPoint> points;
        
        public Cell(int x, int y, Collection<XYPoint> points) {
            this.x = x;
            this.y = y;
            this.points = points;
        }
        
    }
    
    public class Element {
        public Cell box;
        public XYPoint representative;
        public List<XYPoint> points;
        public boolean active;
        
        public Element(Cell box, XYPoint representative, List<XYPoint> points, boolean active) {
            this.box = box;
            this.representative = representative;
            this.points = points;
            this.active = active;
        }
    }
    
}

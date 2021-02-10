/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.projection.spacereduction;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

/**
 *
 * @author Windows
 */
public class SeamCarving {
    
    private Rectangle2D.Double[] _rects;
    private Rectangle2D.Double[] _rectPositions;
    
    private final Rectangle2D.Double[] _initialPositions;
    
    public SeamCarving(Rectangle2D.Double[] initialPositions) {
        _initialPositions = initialPositions;
    }    
    
    public Map<Rectangle2D.Double, Rectangle2D.Double> reduceSpace(Rectangle2D.Double[] projection) {
        
        _rects = projection;
        _rectPositions = new Rectangle2D.Double[_rects.length];
        
        applyAlgorithm();
        
        Map<Rectangle2D.Double, Rectangle2D.Double> positions = new HashMap<>();
        
        IntStream.range(0, _rects.length).forEach(i->positions.put(_rects[i], _rectPositions[i]));

        return positions;
    }

    private void applyAlgorithm() {        
        if( _initialPositions == null )
            throw new NullPointerException("The initial positions must be specified!");
         
        Zone[][] zones = createZones(_initialPositions);       
        _rectPositions = removeSeams(zones, _initialPositions);
        
        new ForceDirectedOverlapRemoval().run(_rectPositions);
        new ForceDirectedUniformity().run(_rectPositions);
    }

    private Zone[][] createZones(Rectangle2D.Double[] rectPositions) {
        Set<Double> xValues = new HashSet<>();
        Set<Double> yValues = new HashSet<>();
        
        for( Rectangle2D.Double rect: rectPositions ) {
            xValues.add(rect.getMinX());
            xValues.add(rect.getMaxX());
            yValues.add(rect.getMinY());
            yValues.add(rect.getMaxY());
        }
        
        double[] xx = new double[xValues.size()];
        double[] yy = new double[yValues.size()];
        
        int k = 0;
        for( Double value: xValues )
            xx[k++] = value;
        k = 0;
        for( Double value: yValues )
            yy[k++] = value;
        
        Arrays.sort(xx);
        Arrays.sort(yy);
        
        int n = xx.length-1;
        int m = yy.length-1;
        
        Zone[][] zones = new Zone[n][m];
        
        for( int i = 0; i < n; ++i ) {
            for( int j = 0; j < m; ++j ) {
                Rectangle2D.Double r = new Rectangle2D.Double(xx[i], yy[j], xx[i+1] - xx[i], yy[j+1] - yy[j]);
                zones[i][j] = new Zone(r, i, j);
            }
        }              
        
        return zones;
    }
    
    private double computeScalingFactor(Rectangle2D.Double[] wordPositions) {
        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;

        for( Rectangle2D.Double rect : wordPositions ) {
            minX = Math.min(minX, rect.getMinX());
            maxX = Math.max(maxX, rect.getMaxX());
            minY = Math.min(minY, rect.getMinY());
            maxY = Math.max(maxY, rect.getMaxY());
        }

        return Math.max(maxX - minX, maxY - minY) / 2;
    }

    private Rectangle2D.Double[] removeSeams(Zone[][] zones, Rectangle2D.Double[] rectPositions) {
        Rectangle2D.Double[] returnedPositions = Arrays.copyOf(rectPositions, rectPositions.length);
        
        double maxRectSize = returnedPositions[0].width;
        double scalingFactor = computeScalingFactor(returnedPositions);
        
        int MAX_ITERATIONS = 500;
        double minSeamSize = 10;
        
        for( int count = 0; count < MAX_ITERATIONS; ++count ) {
            if( count % 30 == 0 )
                alignRects(returnedPositions);
            
            double[][] E = energy(zones, returnedPositions, maxRectSize, scalingFactor);
            List<Zone> horizontalSeam = new ArrayList<>();
            List<Zone> verticalSeam = new ArrayList<>();
            
            double horizontalSeamCost = findOptimalSeam(true, zones, E, horizontalSeam, minSeamSize);
            double verticalSeamCost = findOptimalSeam(false, zones, E, verticalSeam, minSeamSize);
            
            if( horizontalSeamCost >= Double.POSITIVE_INFINITY && verticalSeamCost >= Double.POSITIVE_INFINITY ) {
                if( minSeamSize <= 0.5 )
                    break;
                minSeamSize /= 3.0;
                continue;
            } else if( horizontalSeamCost < verticalSeamCost )
                removeHorizontalSeamByFullReconstruction(returnedPositions, horizontalSeam);
            else 
                removeVerticalSeamByFullReconstruction(returnedPositions, verticalSeam);
                        
            zones = createZones(returnedPositions);
        }
        
        return returnedPositions;
    }

    private void alignRects(Rectangle2D.Double[] rectPositions) {
        double EPS = 0.1;
        
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();
        
        for( Rectangle2D.Double rect: rectPositions ) {
            xValues.add(rect.getMinX());
            xValues.add(rect.getMaxX());
            yValues.add(rect.getMinY());
            yValues.add(rect.getMaxY());
        }
        
        Collections.sort(xValues);
        Collections.sort(yValues);
        
        Map<Double, Double> indX = new HashMap<>();
        int top = 0;
        for( int i = 0; i < xValues.size(); ++i ) 
            if( xValues.get(i) > xValues.get(top) + EPS ) {
                top++;
                xValues.set(top, xValues.get(i));
                indX.put(xValues.get(i), xValues.get(top));
            } else 
                indX.put(xValues.get(i), xValues.get(top));
        
        for( Rectangle2D.Double rect: rectPositions ) {
            double oldX = rect.getX();
            double newX = indX.get(rect.getX());
            
            if( oldX != newX ) 
                rect.setRect(newX, rect.getY(), rect.getWidth(), rect.getHeight());            
        }
        
        Map<Double, Double> indY = new HashMap<>();
        top = 0;
        for( int i = 0; i < yValues.size(); ++i )
            if( yValues.get(i) > yValues.get(top) + EPS ) {
                top++;
                yValues.set(top, yValues.get(i));
                indY.put(yValues.get(i), yValues.get(top));
            } else {
                indY.put(yValues.get(i), yValues.get(top));
            }
        
        for( Rectangle2D.Double rect: rectPositions ) {
            double oldY = rect.getY();
            double newY = indY.get(rect.getY());
            if( oldY != newY ) 
                rect.setRect(rect.getX(), newY, rect.getWidth(), rect.getHeight());
        }
    }

    private double[][] energy(Zone[][] zones, Rectangle2D.Double[] rectPositions, double maxRectSize, double scalingFactor) {
        int n = zones.length;
        int m = zones[0].length;
        
        double[][] E = new double[n][m];
        
        for( int i = 0; i < n; ++i )
            for( int j = 0; j < m; ++j )
                E[i][j] = energy(zones[i][j], rectPositions, maxRectSize, scalingFactor);
        
        return E;
    }
        
    public static double fastexp(double val) {
        final long tmp = (long)(1512775 * val + 1072632447);
        return Double.longBitsToDouble(tmp << 32);
    }   
    
    private double energy(Zone zone, Rectangle2D.Double[] rectPositions, double maxRectSize, double scalingFactor) {
        if( zone.isOccupied )
            return Double.POSITIVE_INFINITY;        
        
        double result = 0;
        
        for( Rectangle2D.Double rect: rectPositions ) {
            if( rect.contains(zone.getRect().getCenterX(), zone.getRect().getCenterY()) ) {
                zone.isOccupied = true;
                return Double.POSITIVE_INFINITY;
            }
            
            double rectSize = rect.width;
            double mux = rect.getCenterX();
            double muy = rect.getCenterY();
            double x = zone.getRect().getCenterX();
            double y = zone.getRect().getCenterY();
            
            double diffX = (x - mux) / scalingFactor;
            double diffY = (y - muy) / scalingFactor;
            
            double exponent = (diffX * diffX + diffY*diffY)/2;
            
            result += (rectSize / maxRectSize) * (0.5 * Math.PI) * fastexp(-exponent);
                    
        }
        
        return result;
    }

    private double findOptimalSeam(boolean horizontal, Zone[][] zones, double[][] E, List<Zone> zonePath, double minSeamSize) {
        
        int n = zones.length;
        int m = zones[0].length;
        
        double[][] Ec = new double[n][m];
        int[][] parent = new int[n][m];
        
        for( int cell = 0; cell < n*m; ++cell ) {
            int i, j;
            if( horizontal ) {
                i = cell/m;
                j = cell%m;
            } else {
                i = cell%n;
                j = cell/n;
            }
            
            parent[i][j] = -1;
            Ec[i][j] = 0;
            if( zones[i][j].isOccupied ) {
                Ec[i][j] = Double.POSITIVE_INFINITY;
                continue;
            }
            
            int dt = 5;
            
            if( horizontal && i-1 >= 0 ) {
                
                Ec[i][j] = Double.POSITIVE_INFINITY;
                for( int t = -dt; t <= dt; ++t ) 
                    if( j+t >= 0 && j+t < m )
                        if( Ec[i][j] > Ec[i-1][j+t] ) {
                            Ec[i][j] = Ec[i-1][j+t];
                            parent[i][j] = j+t;
                        }
                
            } else if( !horizontal && j-1 >= 0 ) {
                
                Ec[i][j] = Double.POSITIVE_INFINITY;
                for( int t = -dt; t <= dt; ++t ) 
                    if( i+t >= 0 && i+t < n )
                        if( Ec[i][j] > Ec[i+t][j-1] ) {
                            Ec[i][j] = Ec[i+t][j-1];
                            parent[i][j] = i+t;
                        }                
            }
            
            double size = (horizontal ? zones[i][j].getRect().height : zones[i][j].getRect().width);
            if( size < minSeamSize )
                Ec[i][j] = Double.POSITIVE_INFINITY;
            else if( size < 1.0 )
                Ec[i][j] += E[i][j] / size;
            else 
                Ec[i][j] += E[i][j] * size;
        }
        
        // find optimal column
        int minIndex = -1;
        double minSum = Double.POSITIVE_INFINITY;
        
        if( horizontal ) {
            for( int i = 0; i < m; ++i ) 
                if( minSum > Ec[n-1][i] ) {
                    minSum = Ec[n-1][i];
                    minIndex = i;
                }
        } else {
            for( int i = 0; i < n; ++i ) 
                if( minSum > Ec[i][m-1] ) {
                    minSum = Ec[i][m-1];
                    minIndex = i;
                }
        }
        
        if( minIndex == -1 )
            return Double.POSITIVE_INFINITY;
        
        int curI = n-1;
        int curJ = m-1;
        if( horizontal ) curJ = minIndex;
        else curI = minIndex;
        
        while( curI >= 0 && curJ >= 0 ) {
            if( zones[curI][curJ].isOccupied )
                throw new RuntimeException("smth wrong with dp");
            
            zonePath.add(zones[curI][curJ]);
            if( horizontal ) {
                curJ = parent[curI][curJ];
                curI--;
            } else {
                curI = parent[curI][curJ];
                curJ--;
            }
        }
        
        if( curI != -1 | curJ != -1 )
            throw new RuntimeException("smth wrong with dp");
        
        return minSum;
    }

    private void removeHorizontalSeamByFullReconstruction(Rectangle2D.Double[] rectPositions, List<Zone> zonePath) {
        
        Map<Double, Double> zoneY = new HashMap<>();
        double minHeight = Double.POSITIVE_INFINITY;
        
        for( Zone z: zonePath ) {
            minHeight = Math.min(minHeight, z.getRect().height);
            zoneY.put(z.getRect().getMinX(), z.getRect().getMinY());            
        }
        
        // remove minHeight
        for( Rectangle2D.Double rect: rectPositions ) {
            double removedZoneY = zoneY.get(rect.getMinX());
            if( removedZoneY < rect.getMinY() ) 
                rect.setRect(rect.getMinX(), rect.getMinY() - minHeight, rect.width, rect.height);
        }        
    }

    private void removeVerticalSeamByFullReconstruction(Rectangle2D.Double[] rectPositions, List<Zone> zonePath) {
        Map<Double, Double> zoneX = new HashMap<>();
        
        double minWidth = Double.POSITIVE_INFINITY;
        for( Zone z: zonePath ) {
            minWidth = Math.min(minWidth, z.getRect().width);
            zoneX.put(z.getRect().getMinY(), z.getRect().getMinX());
        }
        
        // remove minWidth
        for( Rectangle2D.Double rect: rectPositions ) {
            double removedZoneX = zoneX.get(rect.getMinY());
            if( removedZoneX < rect.getMinX() )
                rect.setRect(rect.getMinX() - minWidth, rect.getMinY(), rect.width, rect.height);
        }
    }
    
    
    private class Zone {
        
        private Rectangle2D.Double rect;
        public boolean isOccupied = false;
        private int i, j;
        
        public Zone(Rectangle2D.Double rect, int i, int j) {
            this.rect = rect;
            this.i = i;
            this.j = j;
        }

        public Rectangle2D.Double getRect() {
            return rect;
        }

        public void setRect(Rectangle2D.Double rect) {
            this.rect = rect;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public int getJ() {
            return j;
        }

        public void setJ(int j) {
            this.j = j;
        }
        
        
    }
    
    
    
    
    
}

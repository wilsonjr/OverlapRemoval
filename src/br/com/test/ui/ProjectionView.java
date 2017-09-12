/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.test.ui;

import br.com.explore.explorertree.util.ForceLayout;
import br.com.explore.explorertree.util.Tooltip;
import br.com.explorer.explorertree.ExplorerTreeController;
import br.com.explorer.explorertree.ExplorerTreeNode;
import br.com.methods.overlap.prism.PRISM;
import br.com.methods.overlap.rwordle.RWordleC;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.RectangleVis;
import br.com.methods.utils.Util;
import br.com.test.draw.color.RainbowScale;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import nmap.BoundingBox;
import nmap.Element;
import nmap.NMap;

/**
 *
 * @author Windows
 */
public class ProjectionView extends JPanel {
    
    public static final int HEXBOARD_SIZE = 20;
    public static final int RECTSIZE = 8;
    
    
    private BufferedImage imageBuffer;

    private boolean semaphore = false;
    private int parentMoving = -1;

    private Timer timer = null;
    private Timer timerTooltip = null;

    private int representativePolygon = -1;
    
    private Point2D.Double lastClicked = null;
    
    private List<RectangleVis> rectangles;
    private List<RectangleVis> afterSeamCarving;
    
    private ExplorerTreeController controller;
    
    private Point2D.Double[] points;
       
    private List<Integer> movingIndexes = new ArrayList<>();
    private List<Point2D.Double> toDraw = new ArrayList<>();
    private Tooltip tooltip = null;
    
    private List<List<Integer>> currentCluster = null;
    
    private Polygon[] diagrams = null;
    private Polygon[] intersects = null;
    
    private List<Integer> nearest = null;
    private boolean hideShowNumbers = false;
    private int[] selectedRepresentatives = null;

    public ProjectionView(List<RectangleVis> rectangles, 
                          List<RectangleVis> afterSeamCarving, 
                          ExplorerTreeController explorerController,
                          Point2D.Double[] pointsProjection) {
        setBackground(Color.WHITE);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        this.rectangles = rectangles;
        this.afterSeamCarving = afterSeamCarving;
        this.controller = explorerController;
        this.points = pointsProjection;

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if( semaphore )
                    return;                        

                int notches = e.getWheelRotation();                    
                if( controller != null && controller.representative() != null && controller.nearest() != null ) {
                    int index = controller.indexRepresentative(e.getX(), e.getY());

                    if( index != -1 ) {  
                        ExplorerTreeNode node = controller.getNode(index);                            
                        if( notches > 0 && node.parent() != null )
                            agglomerateAnimation(index, node);
                        else if( notches < 0 && !node.children().isEmpty() )
                            expandAnimation(index, e);
                        else if( notches < 0 && node.children().isEmpty() ) {
                            semaphore = false;                                
                            removeSubsetOverlap(controller.nearest().get(index), index);
                            cleanImage();
                            repaint();
                        }                                
                    } 
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if( semaphore )
                    return;
                if( controller != null && controller.representative() != null && controller.nearest() != null ) {
                    int index = controller.indexRepresentative(e.getX(), e.getY());

                    lastClicked = new Point2D.Double(e.getX(), e.getY());
                    if( index != -1 ) {        

                        ExplorerTreeNode node = controller.getNode(index);                            
                        if( e.isControlDown() && node.parent() != null )
                            agglomerateAnimation(index, node);                                      
                        else if( !node.children().isEmpty() )
                            expandAnimation(index, e);
                        else if( node.children().isEmpty() ) {    
                            semaphore = false;
                            removeSubsetOverlap(controller.nearest().get(index), index);
                            cleanImage();
                            repaint();
                        }

                    } 
                }                     
            }    
        }); 

        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                
                if( semaphore )
                    return;

                if( controller != null && controller.representative() != null && controller.nearest() != null ) {                        

                    Polygon polygon = controller.clickedPolygon(e.getX(), e.getY());                        
                    if( polygon != null ) {
                        double dist = Double.MAX_VALUE;
                        int indexDist = 0;

                        for( int i = 0; i < controller.representative().length; ++i ) {

                            double d = Util.euclideanDistance(e.getX(), e.getY(), 
                                    points[controller.representative()[i]].x, points[controller.representative()[i]].y);

                            if( d < dist ) {
                                dist = d;
                                indexDist = controller.representative()[i];
                            }
                        }

                        representativePolygon = indexDist;
                    } else {
                        representativePolygon = -1;
                    }

                    cleanImage();
                    repaint();


                    int index = controller.indexRepresentative(e.getX(), e.getY());
                    if( index != -1 ) {

                        if( tooltip != null )
                            return;

                        ExplorerTreeNode node = controller.getNode(index);                            
                        if( node.children().isEmpty() ) {  
                            semaphore = true;
                            List<OverlapRect> projection = removeOverlap(controller.nearest().get(index));
                            tooltip = new Tooltip(new Point2D.Double(e.getX(), e.getY()), projection);
                            timerTooltip = new Timer(0, new ActionListener() {
                                private float opacity = 0;

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if( opacity > 1.0f )
                                        opacity = 1.0f;
                                    tooltip.setOpacity(opacity);
                                    opacity += 0.1f;
                                    if( opacity >= 1.0f ) {
                                        cleanImage();
                                        repaint();
                                        timerTooltip.stop();
                                        semaphore = false;
                                    }
                                    cleanImage();
                                    repaint();
                                }                                    
                            });

                            timerTooltip.setDelay(5);
                            timerTooltip.setRepeats(true);
                            timerTooltip.start();
                        } else if( tooltip != null ) {
                            tooltip = null;
                            cleanImage();
                            repaint();
                        }

                    } else if( tooltip != null ) {

                        semaphore = true;
                        timerTooltip = new Timer(0, new ActionListener() {
                            private float opacity = tooltip.getOpacity();

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if( opacity < 0.0f )
                                    opacity = 0.0f;
                                tooltip.setOpacity(opacity);
                                opacity -= 0.1f;
                                if( opacity <= 0.0f ) {
                                    cleanImage();
                                    repaint();
                                    timerTooltip.stop();
                                    semaphore = false;
                                    tooltip = null;
                                }
                                cleanImage();
                                repaint();
                            }                                    
                        });

                        timerTooltip.setDelay(5);
                        timerTooltip.setRepeats(true);
                        timerTooltip.start();
                    }

                }
            }


        });

    }

    private void agglomerateAnimation(int index, ExplorerTreeNode node) {

        semaphore = true;
        movingIndexes = controller.agglomerateNode(index);
        parentMoving = node.parent().routing();
        timer = new Timer(0, new ActionListener() {
            private double i = 0;

            @Override
            public void actionPerformed(ActionEvent e) {

                    for( int j = 0; j < movingIndexes.size(); ++j) {
                        int v = movingIndexes.get(j);
                        double x = (1.0-i)*controller.projection()[v].x +
                                i*controller.projection()[parentMoving].x;
                        double y = (1.0-i)*controller.projection()[v].y +
                                i*controller.projection()[parentMoving].y;

                        toDraw.add(new Point2D.Double(x, y));

                        i += 0.01;
                        if( i >= 1.0 ) {
                            parentMoving = -1;
                            movingIndexes.clear();
                            toDraw.clear();
                            cleanImage();
                            repaint();
                            timer.stop();
                            semaphore = false;
                        }
                    }

                    cleanImage();
                    repaint();
            }
        });

        timer.setDelay(10);
        timer.setRepeats(true);
        timer.start();
    }

    private void expandAnimation(int index, MouseEvent e) {
        semaphore = true;
        movingIndexes = controller.expandNode(index, e.getX(), e.getY(), getSize().width, getSize().height);
        timer = new Timer(0, new ActionListener() {
            private double i = 0;

            @Override
            public void actionPerformed(ActionEvent e) {

                    for( int j = 0; j < movingIndexes.size(); ++j) {
                        int v = movingIndexes.get(j);
                        double x = (1.0-i)*controller.projection()[index].x + i*controller.projection()[v].x;
                        double y = (1.0-i)*controller.projection()[index].y + i*controller.projection()[v].y;

                        toDraw.add(new Point2D.Double(x, y));

                        i += 0.01;
                        if( i >= 1.0 ) {
                            movingIndexes.clear();
                            toDraw.clear();
                            cleanImage();
                            repaint();
                            timer.stop();
                            semaphore = false;
                        }
                    }

                    cleanImage();
                    repaint();



            }
        });

        timer.setDelay(10);
        timer.setRepeats(true);
        timer.start();
    }

    public BufferedImage getImage() {
        return imageBuffer;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

        if( imageBuffer == null ) {
            adjustPanel();
            int width = 1600; // getSize().width;
            int height = 1200; // getSize().height;
            setPreferredSize(new Dimension(width, height));
            
            this.imageBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            java.awt.Graphics2D g2Buffer = this.imageBuffer.createGraphics();
            g2Buffer.setColor(this.getBackground());
            g2Buffer.fillRect(0, 0, width, height);

            g2Buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ArrayList<RectangleVis> pivots = new ArrayList<>();
            if( afterSeamCarving.isEmpty() ) {                
                if( currentCluster != null && !rectangles.isEmpty() ) {
                    RainbowScale rbS = new RainbowScale();
                    int passo = 30;                        
                    List<List<Integer>> indexes = currentCluster;
                    for( int i = 0; i < indexes.size(); ++i ) {
                        Color cor = rbS.getColor((i+1)*passo);                            
                        for( int j = 0; j < indexes.get(i).size(); ++j ) {
                            int index = indexes.get(i).get(j);
                            rectangles.get(index).cor = cor;
                        }                    
                    }

                }


                for( RectangleVis r: rectangles ) {                    
                    g2Buffer.setColor(r.cor);

                    if( r.isHexBoard ) {
                        int a = (int)Math.sqrt(Math.pow(HEXBOARD_SIZE, 2) - Math.pow(HEXBOARD_SIZE/2, 2));
                        Point p = r.getP();
                        Polygon poly = new Polygon();
                        poly.addPoint(p.x, p.y - HEXBOARD_SIZE);
                        poly.addPoint(p.x + a, p.y - HEXBOARD_SIZE/2);
                        poly.addPoint(p.x + a, p.y + HEXBOARD_SIZE/2);
                        poly.addPoint(p.x, p.y + HEXBOARD_SIZE);
                        poly.addPoint(p.x - a, p.y + HEXBOARD_SIZE/2);
                        poly.addPoint(p.x - a, p.y - HEXBOARD_SIZE/2);
                        g2Buffer.fillPolygon(poly);
                        g2Buffer.setColor(Color.WHITE);
                        g2Buffer.drawPolygon(poly);
                    } else {
                        if( r.isPivot() ) {
                            pivots.add(r);
                        } else {
                            if( currentCluster != null ) {
                                g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
                                g2Buffer.setColor(r.cor);
                                g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                            } else {
                                if( controller == null ) {
                                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
                                    g2Buffer.setColor(Color.BLUE);
                                    g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                                    g2Buffer.setColor(Color.BLACK);
                                    g2Buffer.drawOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                                }
//                                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
//                                    g2Buffer.setColor(Color.RED);
//                                    g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
//                                    g2Buffer.drawString(String.valueOf(r.numero), (int)r.getUX(), (int)r.getUY()+10); 
                            }
                        }
                    }

//                        if( !r.isPivot() && hideShowNumbers ) {
//                            g2Buffer.setColor(Color.RED);
//                            g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
//                            g2Buffer.drawString(String.valueOf(r.numero), (int)r.getUX()+10, (int)r.getUY()+10);                           
//                        }
                }



                if( nearest != null ) {                        
                    for( int i = 0; i < nearest.size(); ++i ) {
                        RectangleVis r = rectangles.get(nearest.get(i));
                        g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
                        g2Buffer.setColor(Color.RED);
                        g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                        g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));


                        if( hideShowNumbers ) {
                            g2Buffer.setColor(Color.BLACK);
                            g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));
                            g2Buffer.drawString(String.valueOf(r.numero), (int)r.getUX()+10, (int)r.getUY()+10);
                        }

                    }
                }
            }

            afterSeamCarving.stream().forEach(r->{
                g2Buffer.setColor(r.cor);
                g2Buffer.fillRect((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                g2Buffer.setColor(Color.BLACK);
                g2Buffer.drawRect((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                g2Buffer.setColor(Color.WHITE);
                g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
                g2Buffer.drawString(String.valueOf(r.numero), (int)r.getUX()+10, (int)r.getUY()+10); 
                System.out.print("pintando... "+r);
            });
         

            for( RectangleVis r: pivots ) {
                g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
                g2Buffer.setColor(Color.RED);                    
                g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());      
                g2Buffer.setColor(Color.BLACK);
                g2Buffer.drawOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());                    
                g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
            }
//                
//                if( controller != null ) {
//                    Point2D.Double[] instances = controller.getIncrementalInstances();
//                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.5f));
//                    for( int i = 0; i < instances.length; ++i ) {
//                        g2Buffer.setColor(Color.BLUE);
//                        g2Buffer.fillRect((int)instances[i].x, (int)instances[i].y, RECTSIZE, RECTSIZE);
//                        g2Buffer.setColor(Color.BLACK);
//                        g2Buffer.drawRect((int)instances[i].x, (int)instances[i].y, RECTSIZE, RECTSIZE);
//                    }
//                    
//                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
//                    g2Buffer.setColor(Color.RED);
//                     instances = controller.getIncrementalInstancesBefore();
//                    for( int i = 0; i < instances.length; ++i ) {
//                        g2Buffer.fillOval((int)instances[i].x, (int)instances[i].y, 10, 10);
//                    }
//                }

            if( selectedRepresentatives != null || controller !=  null && controller.representative() != null ) {

                if( controller != null && controller.nearest() != null ) {

                 //  Util.paintSphere(centerPoints, selectedRepresentatives, hashRepresentative, g2Buffer);
                    int[] representative = controller.representative();
                    Map<Integer, List<Integer>> map = controller.nearest();
                    Point2D.Double[] projectionCenter = controller.projectionCenter();
                    Point2D.Double[] projection = controller.projection();

                    for( int i = 0; i < representative.length; ++i ) {

                        Point2D.Double p = projection[representative[i]];

                        Polygon poly = controller.polygon(representative[i]);//getPolygon((int)r.x, (int)r.y);
                        if( poly != null ) {
                            g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.3f));
                            g2Buffer.setColor(Color.RED); 
                            
                            g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.3f));
                            if( representative[i] == representativePolygon ) {
                                Stroke before = g2Buffer.getStroke();
                                g2Buffer.setStroke(new BasicStroke(5));
                                g2Buffer.setColor(Color.BLUE);
                                g2Buffer.draw(poly);                                    
                                g2Buffer.setStroke(before);
                            } else {
                                g2Buffer.draw(poly);
                            }

                        }

                        if( movingRepresentative(representative[i]) )
                            continue;
                        
                       // Point2D.Double p = controller.getPoint(representative[i]);

                        g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));


                        float maxCluster = -1.0f;
                        for( int index: representative )
                            maxCluster = Math.max(maxCluster, map.get(index).size());


                        float normalizedSizeCluster = (float)map.get(representative[i]).size()/maxCluster;
                        int red = (int) ((255 * (normalizedSizeCluster*100))/100);
                        int green = 0;
                        int blue = (int) ((255 * (100 - normalizedSizeCluster*100)))/100;
                        Color clusterColor = new Color(red, green, blue);


                        int sizeScale = RECTSIZE*2;                            
                        g2Buffer.setColor(clusterColor);
                        g2Buffer.fillOval((int)(p.x-RECTSIZE/2.0), (int)(p.y-RECTSIZE/2.0), sizeScale, sizeScale);                            
                        g2Buffer.setColor(Color.BLACK);
                        g2Buffer.drawOval((int)(p.x-RECTSIZE/2.0), (int)(p.y-RECTSIZE/2.0), sizeScale, sizeScale);


                        g2Buffer.setColor(Color.RED);
                        g2Buffer.fillOval((int)p.x, (int)p.y, RECTSIZE, RECTSIZE);
                        g2Buffer.setColor(Color.BLACK);
                        g2Buffer.drawOval((int)p.x, (int)p.y, RECTSIZE, RECTSIZE);

                        if( hideShowNumbers ) {
                            g2Buffer.setColor(Color.BLUE);
                            g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
                            g2Buffer.drawString(String.valueOf(rectangles.get(representative[i]).numero), 
                                    (int)p.x, (int)p.y+10);  
                        }
                    }
                } else {
                    for( int i = 0; i < selectedRepresentatives.length; ++i ) {
                        RectangleVis r = rectangles.get(selectedRepresentatives[i]);
                        g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
                        g2Buffer.setColor(Color.RED);
                        g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                        g2Buffer.setColor(Color.BLACK);
                        g2Buffer.drawOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());

                        //if( hideShowNumbers ) {
                            g2Buffer.setColor(Color.BLACK);
                            g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
                            g2Buffer.drawString(String.valueOf(r.numero), (int)r.getUX()+10, (int)r.getUY()+10);  
                        //}
                    }
                }

                if( !toDraw.isEmpty() ) {
                    int j = movingIndexes.size()-1;
                    for( int i = toDraw.size()-1; i >= (toDraw.size()-movingIndexes.size()); --i ) {
                        Point2D.Double p = toDraw.get(i);
                        g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
                        int index = movingIndexes.get(j--);
                        int size = controller.sizeRepresentative(controller.nearest().get(index).size());
                        g2Buffer.setColor(Color.RED);
                        g2Buffer.fillOval((int)p.x, (int)p.y, size, size);
                        g2Buffer.setColor(Color.BLACK);
                        g2Buffer.drawOval((int)p.x, (int)p.y, size, size);
                    }
                }
                
                
                drawScale(g2Buffer);

                if( tooltip != null ) {
                    tooltip.draw(g2Buffer);
                }

            }

            

            g2Buffer.dispose();

        } 

        if( imageBuffer != null )  {
            g2.drawImage(this.imageBuffer, 0, 0, null);                  
        }

    }

    public void cleanImage() {
        this.imageBuffer = null;
    }

    public void adjustPanel() {
        if( rectangles == null || rectangles.isEmpty() )
            return;

        double iniX = rectangles.get(0).getCenterX();
        double iniY = rectangles.get(0).getCenterY();
        double max_x = iniX, max_y = iniX;
        double min_x = iniY, min_y = iniY;
        int zero = 100;//graph.getVertex().get(0).getRay() * 5 + 10;

        for( int i = 1; i < rectangles.size(); i++ ) {
            double x = rectangles.get(i).getCenterX();
            if (max_x < x)
                max_x = x;
            else if (min_x > x)
                min_x = x;

            double y = rectangles.get(i).getCenterY();
            if (max_y < y)
                max_y = y;
            else if (min_y > y)
                min_y = y;

        }
        
        Dimension d = this.getSize();
        d.width = (int) max_x + zero;
        d.height = (int) max_y + zero;
        this.setSize(d);
        this.setPreferredSize(d);
    }

    private boolean movingRepresentative(int i) {
        return movingIndexes.contains(i) || i == parentMoving;
    }

    private void drawScale(Graphics2D g2Buffer) {
        g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));            

        int width = 255;
        int height = 30;
        int x = 10;
        int y = 10;            
        int spaceDescription = 15;

        for( int i = 0; i < width; ++i ) {
            float value = (float)i/(float)width;
            int r = (int) ((255 * (value*100))/100);
            int g = 0;
            int b = (int) ((255 * (100 - value*100)))/100;

            g2Buffer.setColor(new Color(r, g, b));
            g2Buffer.drawLine(x+i, y, x+i, y+height);
        }

        g2Buffer.setColor(Color.BLACK);
        g2Buffer.drawRect(x, y, width, height);

        g2Buffer.drawString("min", x, y + height + spaceDescription);
        g2Buffer.drawString("max", x + width - g2Buffer.getFontMetrics().stringWidth("max"), y + height + spaceDescription);
    }
    
    
    private List<OverlapRect> removeOverlap(List<Integer> indexes) {
        int algo = 1;//Integer.parseInt(JOptionPane.showInputDialog("Deseja utilizar uma estrutura de matriz esparsa?\n0-Não\n1-Sim"));
        boolean applySeamCarving = false;//Integer.parseInt(JOptionPane.showInputDialog("Apply SeamCarving?")) == 1;
        List<OverlapRect> rects = Util.toRectangle(rectangles, indexes);
        
        double[] center0 = Util.getCenter(rects);
      //  PRISM prism = new PRISM(algo);
        RWordleC rwordlec = new RWordleC();
        
      //  Map<OverlapRect, OverlapRect> projected = prism.applyAndShowTime(rects);
        Map<OverlapRect, OverlapRect> projected = rwordlec.applyAndShowTime(rects);
        List<OverlapRect> projectedValues = Util.getProjectedValues(projected);
        double[] center1 = Util.getCenter(projectedValues);
        double ammountX = center0[0]-center1[0];
        double ammountY = center0[1]-center1[1];
        Util.translate(projectedValues, ammountX, ammountY);        
        Util.normalize(projectedValues);
     
        if( applySeamCarving )
            projectedValues = OverlapView.addSeamCarvingResult(projectedValues);
                
        return projectedValues;
    }
    
    private void removeSubsetOverlap(List<Integer> indexes, int representative) {
        int algo = 1;//Integer.parseInt(JOptionPane.showInputDialog("Deseja utilizar uma estrutura de matriz esparsa?\n0-Não\n1-Sim"));
        boolean applySeamCarving = false;//Integer.parseInt(JOptionPane.showInputDialog("Apply SeamCarving?")) == 1;
        List<OverlapRect> rects = Util.toRectangle(rectangles, indexes);
        
        System.out.println("-------------------");
        for( int i = 0; i < rects.size(); ++i ) {
            System.out.println(rects.get(i).x+", "+rects.get(i).y);
        }
        System.out.println("-------------------");
            
        
        double maxDistance = -1;
        for( int i = 0; i < indexes.size(); ++i ) {
            RectangleVis p1 = rectangles.get(representative);
            RectangleVis p2 = rectangles.get(indexes.get(i));
            
            double d = Util.euclideanDistance(p1.x, p1.y, p2.x, p2.y);            
            if( d > maxDistance )
                maxDistance = d;
                
        }
        double[] center0 = Util.getCenter(rects);
        PRISM prism = new PRISM(algo);
        Map<OverlapRect, OverlapRect> projected = prism.applyAndShowTime(rects);
        List<OverlapRect> projectedValues = Util.getProjectedValues(projected);
        double[] center1 = Util.getCenter(projectedValues);

        double ammountX = center0[0]-center1[0];
        double ammountY = center0[1]-center1[1];
        Util.translate(projectedValues, ammountX, ammountY);        
        Util.normalize(projectedValues);

        if( applySeamCarving )
            projectedValues = OverlapView.addSeamCarvingResult(projectedValues);
        
        ArrayList<RectangleVis> cluster = new ArrayList<>();
        Util.toRectangleVis(cluster, projectedValues, indexes);
//        

        int rep = -1;
        List<OverlapRect> toforce = new ArrayList<>();
        ArrayList<OverlapRect> overlaps = new ArrayList<>();
        List<Map.Entry<OverlapRect, OverlapRect>> entryset = projected.entrySet().stream().collect(Collectors.toList());
        for( int i = 0; i < entryset.size(); ++i ) {
            double d = Util.euclideanDistance(entryset.get(i).getKey().x, entryset.get(i).getKey().y, 
                    rectangles.get(representative).getUX(), rectangles.get(representative).getUY());
            System.out.println(">> distance: "+d);
            if( d == 0 ) {
                rep = i;
                System.out.println("INDEX REPRESENTATIVE: "+i+" ID: "+entryset.get(i).getValue().getId());
                
            }
            overlaps.add(entryset.get(i).getKey());
            toforce.add(entryset.get(i).getValue());
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        OverlapView panel = new OverlapView(projected, cluster, afterSeamCarving);
        
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);        
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        
        slider.addChangeListener(panel);
        
        
        frame.add(panel, BorderLayout.CENTER);
        frame.add(slider, BorderLayout.SOUTH);
        panel.setLocation((int)lastClicked.x, (int)lastClicked.y);
//        
        panel.cleanImage();
        panel.repaint();
        panel.adjustPanel();  
        frame.setSize(panel.getSize().width, panel.getSize().height+100);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
//        
        
        
        List<OverlapRect> after = new ForceLayout().repulsive(toforce, rep, 1, 5);
        
        ArrayList<RectangleVis> rectanglesforce = new ArrayList<>();
        for( OverlapRect o: after ) {
            RectangleVis rec = new RectangleVis(o.getUX(), o.getUY(), o.width, o.height, Color.BLUE, o.getId());
            rectanglesforce.add(rec);
        }
        JFrame frame2 = new JFrame();
        frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        OverlapView panel2 = new OverlapView(projected, rectanglesforce, afterSeamCarving);
        
        JSlider slider2= new JSlider(JSlider.HORIZONTAL, 0, 100, 100);        
        slider2.setPaintTicks(true);
        slider2.setPaintLabels(true);
        
        slider2.addChangeListener(panel2);
        
        
        frame2.add(panel2, BorderLayout.CENTER);
        frame2.add(slider2, BorderLayout.SOUTH);
        panel2.setLocation((int)lastClicked.x, (int)lastClicked.y);
        
//        panel2.cleanImage();
//        panel2.repaint();
//        panel2.adjustPanel();  
//        frame2.setSize(panel2.getSize().width, panel2.getSize().height+100);
//        frame2.setLocationRelativeTo(this);
//        frame2.setVisible(true);
        
        
        /**
         * Testing NMap representation
         */
        
        List<Element> data = new ArrayList<>();
        
        List<OverlapRect> proj1 = projected.entrySet().stream().map((v)->v.getKey()).collect(Collectors.toList());
        List<OverlapRect> proj2 = projected.entrySet().stream().map((v)->v.getValue()).collect(Collectors.toList());
        Random rand = new Random();
        
        for( int i = 0; i < proj2.size(); ++i ) {
            
            
            double distance =  Util.euclideanDistance(rectangles.get(representative).x, rectangles.get(representative).y, 
                                                      proj1.get(i).x, proj1.get(i).y);
            
            double weight = ExplorerTreeController.calculateWeight(10, 0.2*10, maxDistance, distance);
            data.add(new Element(proj2.get(i).getId(), (float)proj2.get(i).x, (float)proj2.get(i).y, (float) weight, 1));
            
            System.out.println("id: "+proj2.get(i).getId()+" x: "+proj2.get(i).x+" -- y: "+proj2.get(i).y);
            
        }
        int visualSpaceWidth = 800;
        int visualSpaceHeight = 600;
        
        NMap nmap = new NMap(visualSpaceWidth, visualSpaceHeight);
        
        // We can use this when weights are different        
        List<BoundingBox> ac = nmap.alternateCut(data);
//        Frame frameAlternateCut = new Frame(visualSpaceWidth, visualSpaceHeight, ac, "NMap Alternate Cut");
//        frameAlternateCut.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frameAlternateCut.setVisible(true);
////        
////        List<BoundingBox> ew = nmap.equalWeight(data);
////        Frame frameEqualWeight = new Frame(visualSpaceWidth, visualSpaceHeight, ew, "NMAP Equal Weight");
////        frameEqualWeight.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
////        frameEqualWeight.setVisible(true);       
////        
//        List<OverlapRect> proj = projected.entrySet().stream().map((v)->v.getKey()).collect(Collectors.toList());
//        
//        List<Element> data2 = new ArrayList<>();
//        for( int i = 0; i < proj.size(); ++i )
//            data2.add(new Element(proj.get(i).getId(), (float)proj.get(i).x, (float)proj.get(i).y, 1.0f, 1.0f));
//        
//        List<BoundingBox> ew2 = nmap.equalWeight(data2);
//        Frame frameEqualWeight2 = new Frame(visualSpaceWidth, visualSpaceHeight, ew2, "NMAP Equal Weight 2");
//        frameEqualWeight2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frameEqualWeight2.setVisible(true);
//        
//        
        List<OverlapRect> after2 = OverlapView.removeOverlap(overlaps, rep);//new ForceNMAP(800, 600).repulsive(toforce, rep, 0.2*10, 10);
//        List<OverlapRect> after2 = new ArrayList<>();
//        for( BoundingBox bb: ac ) {
//            Element e = bb.getElement();
//            System.out.println("id: "+e.getId()+" x: "+e.x+" -- y: "+e.y);
//            after2.add(new OverlapRect(e.x, e.y, RECTSIZE, RECTSIZE, e.getId()));
//        }
        
        
        
        
        
        ArrayList<RectangleVis> rectanglesforce2 = new ArrayList<>();
        for( OverlapRect o: after2 ) {
            RectangleVis rec = new RectangleVis(o.getUX(), o.getUY(), o.width, o.height, Color.BLUE, o.getId());
            rectanglesforce2.add(rec);
        }
        JFrame frame3 = new JFrame();
        frame3.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        OverlapView panel3 = new OverlapView(projected, rectanglesforce2, afterSeamCarving);
        
        JSlider slider3 = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);        
        slider3.setPaintTicks(true);
        slider3.setPaintLabels(true);
        
        slider3.addChangeListener(panel3);
        
        
        frame3.add(panel3, BorderLayout.CENTER);
        frame3.add(slider3, BorderLayout.SOUTH);
        panel3.setLocation((int)lastClicked.x, (int)lastClicked.y);
        
        panel3.cleanImage();
        panel3.repaint();
        panel3.adjustPanel();  
        frame3.setSize(panel3.getSize().width, panel3.getSize().height+100);
        frame3.setLocationRelativeTo(this);
        frame3.setVisible(true);
        
        
    }
    
    public void setController(ExplorerTreeController controller) {
        this.controller = controller;
    }
    
    public void setPoints(Point2D.Double[] points) {
        this.points = points;
    }
    
}
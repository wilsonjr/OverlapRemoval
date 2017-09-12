/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.test.ui;

import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.RectangleVis;
import br.com.methods.utils.Util;
import br.com.projection.spacereduction.SeamCarving;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Windows
 */
public class OverlapView extends JPanel implements ChangeListener {
        
    private BufferedImage imageBuffer;

    private List<RectangleVis> rects;
    private List<RectangleVis> afterSeamCarving;
    private Map<OverlapRect, OverlapRect> projected;
    private Timer timer;
    private int INITIAL_DELAY = 0;
    private int PERIOD_INTERVAL = 100;
    private int RECT_SIZE = 30;

    @SuppressWarnings(value="")
    public OverlapView(Map<OverlapRect, OverlapRect> projected, 
                       List<RectangleVis> rects, 
                       List<RectangleVis> afterSeamCarving) {

        setBackground(Color.WHITE);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        this.rects = rects;
        this.projected = projected;
        this.afterSeamCarving = afterSeamCarving;

        timer = new Timer(PERIOD_INTERVAL, new ActionListener() {
            private double i = 0;
            @Override
            public void actionPerformed(ActionEvent e) {

                int idx = 0;

                for( Map.Entry<OverlapRect, OverlapRect> v: projected.entrySet() ) {
                    double x = (1.0-i)*v.getKey().getUX() + i*v.getValue().getUX();
                    double y = (1.0-i)*v.getKey().getUY() + i*v.getValue().getUY();

                    rects.get(idx).setUX(x);
                    rects.get(idx++).setUY(y);

                    i += 0.01;
                    if( i >= 1.0 ) {
                        rects.get(idx-1).setUX(v.getValue().getUX());
                        rects.get(idx-1).setUY(v.getValue().getUY());

                        cleanImage();
                        repaint();
                        timer.stop();

                    }
                }        

                cleanImage();
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if( e.isControlDown() ) {

                    Rectangle2D.Double[] r2ds = new Rectangle2D.Double[projected.size()];
                    int i = 0;
                    for( Map.Entry<OverlapRect, OverlapRect> v: projected.entrySet() ) {
                        r2ds[i++] = new Rectangle2D.Double(v.getValue().getUX(), v.getValue().getUY(), v.getValue().width, v.getValue().height);
                    }

                    ArrayList<OverlapRect> returned = new ArrayList<>();

                    SeamCarving sc = new SeamCarving(r2ds);
                    OverlapRect[] array = projected.entrySet().stream().map((v)->v.getValue()).toArray(OverlapRect[]::new);
                    Map<Rectangle2D.Double, Rectangle2D.Double> mapSeamCarving = sc.reduceSpace(array);

                    //ContextPreserving cp = new ContextPreserving(r2ds);
                    //Map<Rectangle2D.Double, Rectangle2D.Double> mapContextPreserving = cp.reduceSpace(array);

                    mapSeamCarving.entrySet().forEach((element)->{
                        int idx = ((OverlapRect)element.getKey()).getId();
                        returned.add(new OverlapRect(element.getValue().getMinX(), element.getValue().getMinY(), ProjectionView.RECTSIZE, ProjectionView.RECTSIZE, idx));
                    });

                    for( int j = 0; j < returned.size(); ++j ) {
                        rects.get(j).x = returned.get(j).x;
                        rects.get(j).y = returned.get(j).y;
                    }
                    cleanImage();
                    repaint();
                } else {
                    timer.setDelay(PERIOD_INTERVAL);
                    timer.setRepeats(true);
                    timer.start();
                }
            }                 
        }); 


        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        for( int i = 0; i < rects.size(); ++i ) {
            if( minx > rects.get(i).x )
                minx = rects.get(i).x;
            if( miny > rects.get(i).y )
                miny = rects.get(i).y;
        }

        for( Map.Entry<OverlapRect, OverlapRect> p: projected.entrySet() ) {
            p.getKey().x = p.getKey().x - (minx-RECT_SIZE);
            p.getKey().y = p.getKey().y - (miny-RECT_SIZE);

            p.getValue().x = p.getValue().x - (minx-RECT_SIZE);
            p.getValue().y = p.getValue().y - (miny-RECT_SIZE);
        }


        for( int i = 0; i < rects.size(); ++i ) {
            rects.get(i).x = rects.get(i).x - (minx-RECT_SIZE);
            rects.get(i).y = rects.get(i).y - (miny-RECT_SIZE);
        }

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
            setPreferredSize(getSize());
            this.imageBuffer = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);

            java.awt.Graphics2D g2Buffer = this.imageBuffer.createGraphics();
            g2Buffer.setColor(this.getBackground());
            g2Buffer.fillRect(0, 0, 5000, 5000);

            g2Buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if( afterSeamCarving.isEmpty() ) {                
                for( RectangleVis r: rects ) {                    
                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
                    g2Buffer.setColor(Color.BLUE);
                    g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());

                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
                    g2Buffer.setColor(Color.RED);
                    g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
                    g2Buffer.drawString(String.valueOf(r.numero), (int)r.getUX()+10, (int)r.getUY()+10); 

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
        if( rects == null || rects.isEmpty() )
            return;

        double iniX = rects.get(0).getCenterX();
        double iniY = rects.get(0).getCenterY();
        double max_x = iniX, max_y = iniX;
        double min_x = iniY, min_y = iniY;
        int zero = 50;//graph.getVertex().get(0).getRay() * 5 + 10;

        for( int i = 1; i < rects.size(); i++ ) {
            double x = rects.get(i).getCenterX();
            if (max_x < x)
                max_x = x;
            else if (min_x > x)
                min_x = x;

            double y = rects.get(i).getCenterY();
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

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        int value = (int) source.getValue();

        double i = (double)value/source.getMaximum();

        int idx = 0;
        for( Map.Entry<OverlapRect, OverlapRect> v: projected.entrySet() ) {
            double x = (1.0-i)*v.getKey().getUX() + i*v.getValue().getUX();
            double y = (1.0-i)*v.getKey().getUY() + i*v.getValue().getUY();

            rects.get(idx).setUX(x);
            rects.get(idx++).setUY(y);


        }
        cleanImage();
        repaint();

    }

    public class ScheduleTask extends TimerTask {
        private double i = 0;

        @Override
        public void run() {
            int idx = 0;

            for( Map.Entry<OverlapRect, OverlapRect> v: projected.entrySet() ) {
                double x = (1.0-i)*v.getKey().getUX() + i*v.getValue().getUX();
                double y = (1.0-i)*v.getKey().getUY() + i*v.getValue().getUY();

                rects.get(idx).setUX(x);
                rects.get(idx++).setUY(y);

                i += 0.01;
                if( i >= 1.0 ) {
                    rects.get(idx-1).setUX(v.getValue().getUX());
                    rects.get(idx-1).setUY(v.getValue().getUY());

                    cleanImage();
                    repaint();
                    cancel(); 

                }
            }        

            cleanImage();
            repaint();
        }           
    }         
    
    
    public static List<OverlapRect> addSeamCarvingResult(List<OverlapRect> projected) {
        Rectangle2D.Double[] r2ds = new Rectangle2D.Double[projected.size()];
        for( int i = 0; i < r2ds.length; ++i ) {
            r2ds[i] = new Rectangle2D.Double(projected.get(i).getUX(), projected.get(i).getUY(), projected.get(i).width, projected.get(i).height);
        }
        ArrayList<OverlapRect> returned = new ArrayList<>();
        
        SeamCarving sc = new SeamCarving(r2ds);
        OverlapRect[] array = new OverlapRect[projected.size()];
        array = projected.toArray(array);
        Map<Rectangle2D.Double, Rectangle2D.Double> mapSeamCarving = sc.reduceSpace(array);
        //ContextPreserving cp = new ContextPreserving(r2ds);
        //Map<Rectangle2D.Double, Rectangle2D.Double> mapContextPreserving = cp.reduceSpace(array);
        
        mapSeamCarving.entrySet().forEach((element)->{
            int idx = ((OverlapRect)element.getKey()).getId();
            returned.add(new OverlapRect(element.getValue().getMinX(), element.getValue().getMinY(), ProjectionView.RECTSIZE, ProjectionView.RECTSIZE, idx));
        });
        
        
//        mapContextPreserving.entrySet().forEach((element)->{
//            int idx = ((OverlapRect)element.getKey()).getId();
//            System.out.println(">> "+idx);
//            
//            returned.add(new OverlapRect(element.getValue().getMinX(), element.getValue().getMinY(), RECTSIZE, RECTSIZE, idx));
//          //  afterSeamCarving.add(new RectangleVis(element.getValue().getMinX(), element.getValue().getMinY(), 
//          //      RECTSIZE, RECTSIZE, rectangles.get(idx).cor, rectangles.get(idx).numero));
//        
//        });
        
        return returned;
    }
    
    private static double intersection(OverlapRect u, OverlapRect v) {
        return Math.max(
            Math.min(
               (u.getWidth()/2. + v.getWidth()/2.)/Math.abs(u.getCenterX() - v.getCenterX()), 
               (u.getHeight()/2. + v.getHeight()/2.)/Math.abs(u.getCenterY() - v.getCenterY())
            ), 1);
    }
    
    public static ArrayList<OverlapRect> removeOverlap(ArrayList<OverlapRect> rect, int rep) {
        ArrayList<OverlapRect> rects = new ArrayList<>();
        for( int i = 0; i < rect.size(); ++i ) {
            rects.add(new OverlapRect(rect.get(i).x, rect.get(i).y, rect.get(i).width, rect.get(i).height, rect.get(i).getId()));
        }
        
        Collections.sort(rects, (a, b) -> {
            return Double.compare(Util.euclideanDistance(b.x, b.y, rect.get(rep).x, rect.get(rep).y), 
                                  Util.euclideanDistance(a.x, a.y, rect.get(rep).x, rect.get(rep).y));
        });
        
        //System.out.println("Representative "+rect.get(rep).getId());
        
                
        for( int i = 0; i < rects.size(); ++i ) {
            OverlapRect r1 = rects.get(i);
            for( int j = i+1; j < rects.size(); ++j ) {
                OverlapRect r2 = rects.get(j);
                if( r1.intersects(r2) ) {
                    //System.out.println("Removendo sobreposição de "+r1.getId()+" e "+r2.getId());
                    double inter = intersection(r1, r2);
                    
                    double ax = r2.x;
                    double ay = r2.y;
                    double bx = r1.x;
                    double by = r1.y;

                    double lenAB = Util.euclideanDistance(ax, ay, bx, by);
                    
                    if( lenAB == 0.0 ) {
                        ax += 0.5;
                        ay += 0.5;
                        lenAB = Util.euclideanDistance(ax, ay, bx, by);
                        inter = intersection(r1, new OverlapRect(ax, ay, r2.width, r2.height));
                    }

                    //System.out.println("len: "+lenAB+" --  inter: "+inter);
                    double ammountx = (bx-ax)/lenAB * (inter*lenAB - lenAB);
                    double ammounty = (by-ay)/lenAB * (inter*lenAB - lenAB);
                    
                    r1.x = bx+ammountx;
                    r1.y = by+ammounty;
                    
                    
                    for( int o = i; o >= 0; --o ) {                    
                        OverlapRect p = rects.get(o);

                        List<OverlapRect> first = new ArrayList<>();

                        for( int k = o-1; k >= 0; --k ) {
                            first.add(rects.get(k));
                        }

                        if( !first.isEmpty() ) {

                            Collections.sort(first, (a, b) -> {
                                return Double.compare(Util.euclideanDistance(a.x, a.y, p.x, p.y), 
                                                      Util.euclideanDistance(b.x, b.y, p.x, p.y));
                            });


                            for( int k = 0; k < first.size(); ++k ) {
                                OverlapRect r3 = first.get(k);
                                if( p.intersects(r3) ) {
                                   // System.out.println("Atualizando posições: "+r3.getId());
                                    inter = intersection(p, r3);
                                    ax = p.x;
                                    ay = p.y;
                                    bx = r3.x;
                                    by = r3.y;

                                    lenAB = Util.euclideanDistance(ax, ay, bx, by);

                                    ammountx = (bx-ax)/lenAB * (inter*lenAB - lenAB);
                                    ammounty = (by-ay)/lenAB * (inter*lenAB - lenAB);

                                    r3.x = bx + ammountx;
                                    r3.y = by + ammounty;
                                }
                            }

                        }
                    }
                    
                    
                } 
            }
            
        }
        
        return rects;        
    }
}

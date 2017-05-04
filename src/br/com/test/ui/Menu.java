/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.test.ui;


import br.com.representative.clustering.bisectingkmeans.BisectingKMeans;
import br.com.representative.clustering.dbscan.Dbscan;
import br.com.datamining.clustering.FarPointsMedoidApproach;
import br.com.representative.clustering.hierarchical.HierarchicalClustering;
import br.com.representative.clustering.hierarchical.SingleLinkageStrategy;
import br.com.representative.clustering.kmeans.KMeans;
import br.com.representative.clustering.kmedoid.KMedoid;
import br.com.datamining.clustering.RandomMedoidApproach;
import br.com.test.draw.color.GrayScale;
import br.com.test.draw.color.RainbowScale;
import br.com.methods.overlap.hexboard.HexBoardExecutor;
import br.com.methods.overlap.incboard.IncBoardExecutor;
import br.com.methods.overlap.incboard.PointItem;
import br.com.methods.overlap.prism.PRISM;
import br.com.methods.overlap.projsnippet.Edge;
import br.com.methods.overlap.projsnippet.ProjSnippet;
import br.com.methods.overlap.projsnippet.Vertex;
import br.com.methods.overlap.rwordle.RWordleC;
import br.com.methods.overlap.rwordle.RWordleL;
import br.com.methods.overlap.vpsc.VPSC;
import br.com.methods.pivot.GNAT;
import br.com.methods.pivot.MST;
import br.com.methods.pivot.OMNI;
import br.com.methods.pivot.SSS;
import br.com.methods.utils.ChangeRetangulo;
import br.com.methods.utils.Pair;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.RetanguloVis;
import br.com.methods.utils.Util;
import br.com.projection.spacereduction.ContextPreserving;
import br.com.representative.Dijsktra;
import br.com.representative.RepresentativeFinder;
import br.com.representative.lowrank.csm.CSM;
import br.com.representative.dictionaryrepresentation.ds3.DS3;
import br.com.representative.dictionaryrepresentation.smrs.SMRS;
import br.com.representative.lowrank.ksvd.KSvd;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author wilson
 */
public class Menu extends javax.swing.JFrame {
    private ViewPanel view;
    private ArrayList<RetanguloVis> rectangles, afterSeamCarving;
    private double alpha = 0;
    private int globalCounter = 0;
    private int globalCounterColor = 0;
    private boolean loadedData = false;
    private ArrayList<Point> hexPoints;
    private static final int HEXBOARD_SIZE = 20;
    private Polygon p1, p2;
    private static final int RECTSIZE = 20;
    private int maior, menor;
    private ArrayList<ArrayList<ArrayList<Integer>>> clusters = null;
    private ArrayList<ArrayList<Integer>> currentCluster = null;
    private int nivelDendrogram = 0, indexRepresentatives = -1;
    private int[] selectedRepresentatives = null;
    private boolean hideShowNumbers = false;
    private double iImage = 0;
    private HierarchicalClustering hc;
    
    private ArrayList<ChangeRetangulo> cRetangulo = null;
    
    private Polygon[] diagrams = null;
    private Polygon hullPolygon = null;
    private Polygon[] intersects = null;
    
    private Map<Integer, List<Integer>> hashRepresentative = null;
    private List<Integer> nearest = null;
    private int[][] heatmap = null;
    private int maxValue;
    private Point2D.Double[] centerPoints = null;
    private ArrayList<Point2D.Double> items = null;
    /**
     * Creates new form Menu
     */
    public Menu() {
        hexPoints = new ArrayList<>();
        view = new ViewPanel();
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        URL url = this.getClass().getResource("simpleGraph.png");    
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);
        setIconImage(iconeTitulo);
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        telaJScrollPane = new JScrollPane(view);
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        sairJMenuItem = new javax.swing.JMenuItem();
        loadDataJMenuItem = new javax.swing.JMenuItem();
        saveDataCoordJMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        limparJMenuItem = new javax.swing.JMenuItem();
        salvarImagemJMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        showHideJMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        rwordleCJMenuItem = new javax.swing.JMenuItem();
        rwordleLJMenuItem = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        vpscJMenuItem = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        prismJMenuItem = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        projSnippetJMenuItem = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        incBoardJMenuItem = new javax.swing.JMenuItem();
        hexBoardJMenuItem = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        sssJMenuItem = new javax.swing.JMenuItem();
        gnatJMenuItem = new javax.swing.JMenuItem();
        omniJMenuItem = new javax.swing.JMenuItem();
        mstJMenuItem = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        extractParametersJMenuItem = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        kMeansJMenuItem = new javax.swing.JMenuItem();
        kMedoidJMenuItem = new javax.swing.JMenuItem();
        bisectingKMeansJMenuItem = new javax.swing.JMenuItem();
        dbscanJMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        hierarchicalClusteringJMenuItem = new javax.swing.JMenuItem();
        nextDendogramJMenuItem = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        incrementJMenuItem = new javax.swing.JMenuItem();
        decrementJMenuItem = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        viewSelectedJMenuItem = new javax.swing.JMenuItem();
        dijsktraRepresentativeJMenuItem = new javax.swing.JMenuItem();
        csmJMenuItem = new javax.swing.JMenuItem();
        ksvdJMenuItem = new javax.swing.JMenuItem();
        smrsJMenuItem = new javax.swing.JMenuItem();
        runDs3JMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        ds3JMenuItem = new javax.swing.JMenuItem();
        voronoiDiagramJMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jMenu1.setText("File");

        sairJMenuItem.setText("Exit");
        sairJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sairJMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(sairJMenuItem);

        loadDataJMenuItem.setText("Load Data");
        loadDataJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadDataJMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(loadDataJMenuItem);

        saveDataCoordJMenuItem.setText("Save Data (Coords)");
        saveDataCoordJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDataCoordJMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(saveDataCoordJMenuItem);
        jMenu1.add(jSeparator1);

        limparJMenuItem.setText("Clean");
        limparJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limparJMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(limparJMenuItem);

        salvarImagemJMenuItem.setText("Save Image");
        salvarImagemJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salvarImagemJMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(salvarImagemJMenuItem);

        jMenuItem1.setText("Move");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        showHideJMenuItem.setText("Show/Hide Numbers");
        showHideJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showHideJMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(showHideJMenuItem);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Algorithm");

        rwordleCJMenuItem.setText("RWordle-C");
        rwordleCJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rwordleCJMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(rwordleCJMenuItem);

        rwordleLJMenuItem.setText("RWordle-L");
        rwordleLJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rwordleLJMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(rwordleLJMenuItem);
        jMenu2.add(jSeparator8);

        vpscJMenuItem.setText("VPSC");
        vpscJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vpscJMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(vpscJMenuItem);
        jMenu2.add(jSeparator9);

        prismJMenuItem.setText("PRISM");
        prismJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prismJMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(prismJMenuItem);
        jMenu2.add(jSeparator10);

        projSnippetJMenuItem.setText("ProjSnippet");
        projSnippetJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projSnippetJMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(projSnippetJMenuItem);
        jMenu2.add(jSeparator11);

        incBoardJMenuItem.setText("IncBoard");
        incBoardJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                incBoardJMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(incBoardJMenuItem);

        hexBoardJMenuItem.setText("HexBoard");
        hexBoardJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hexBoardJMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(hexBoardJMenuItem);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Pivot");

        sssJMenuItem.setText("SSS");
        sssJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sssJMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(sssJMenuItem);

        gnatJMenuItem.setText("GNAT");
        gnatJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gnatJMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(gnatJMenuItem);

        omniJMenuItem.setText("OMNI");
        omniJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                omniJMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(omniJMenuItem);

        mstJMenuItem.setText("MST");
        mstJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mstJMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(mstJMenuItem);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Analysis");

        extractParametersJMenuItem.setText("Extract Parameters");
        extractParametersJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractParametersJMenuItemActionPerformed(evt);
            }
        });
        jMenu4.add(extractParametersJMenuItem);

        jMenuBar1.add(jMenu4);

        jMenu6.setText("Clustering");

        kMeansJMenuItem.setText("k-means");
        kMeansJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kMeansJMenuItemActionPerformed(evt);
            }
        });
        jMenu6.add(kMeansJMenuItem);

        kMedoidJMenuItem.setText("k-medoid");
        kMedoidJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kMedoidJMenuItemActionPerformed(evt);
            }
        });
        jMenu6.add(kMedoidJMenuItem);

        bisectingKMeansJMenuItem.setText("Bisecting k-means");
        bisectingKMeansJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bisectingKMeansJMenuItemActionPerformed(evt);
            }
        });
        jMenu6.add(bisectingKMeansJMenuItem);

        dbscanJMenuItem.setText("DBSCAN");
        dbscanJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbscanJMenuItemActionPerformed(evt);
            }
        });
        jMenu6.add(dbscanJMenuItem);
        jMenu6.add(jSeparator3);

        hierarchicalClusteringJMenuItem.setText("Hierarchical Clustering");
        hierarchicalClusteringJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hierarchicalClusteringJMenuItemActionPerformed(evt);
            }
        });
        jMenu6.add(hierarchicalClusteringJMenuItem);

        nextDendogramJMenuItem.setText("Next Dendrogram Level");
        nextDendogramJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextDendogramJMenuItemActionPerformed(evt);
            }
        });
        jMenu6.add(nextDendogramJMenuItem);

        jMenuBar1.add(jMenu6);

        jMenu7.setText("Dendogram Controller");

        incrementJMenuItem.setText("Increment");
        incrementJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                incrementJMenuItemActionPerformed(evt);
            }
        });
        jMenu7.add(incrementJMenuItem);

        decrementJMenuItem.setText("Decrement");
        decrementJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decrementJMenuItemActionPerformed(evt);
            }
        });
        jMenu7.add(decrementJMenuItem);

        jMenuBar1.add(jMenu7);

        jMenu8.setText("Representative");

        viewSelectedJMenuItem.setText("View Representative");
        viewSelectedJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewSelectedJMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(viewSelectedJMenuItem);

        dijsktraRepresentativeJMenuItem.setText("Dijsktra");
        dijsktraRepresentativeJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dijsktraRepresentativeJMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(dijsktraRepresentativeJMenuItem);

        csmJMenuItem.setText("CSM");
        csmJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                csmJMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(csmJMenuItem);

        ksvdJMenuItem.setText("K-SVD");
        ksvdJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ksvdJMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(ksvdJMenuItem);

        smrsJMenuItem.setText("SMRS");
        smrsJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smrsJMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(smrsJMenuItem);

        runDs3JMenuItem.setText("DS3");
        runDs3JMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runDs3JMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(runDs3JMenuItem);
        jMenu8.add(jSeparator2);

        ds3JMenuItem.setText("Test DS3");
        ds3JMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ds3JMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(ds3JMenuItem);

        voronoiDiagramJMenuItem.setText("View Voronoi Diagram");
        voronoiDiagramJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voronoiDiagramJMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(voronoiDiagramJMenuItem);

        jMenuBar1.add(jMenu8);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(telaJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 736, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(telaJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void sairJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sairJMenuItemActionPerformed
        dispose();
    }//GEN-LAST:event_sairJMenuItemActionPerformed

    private void loadDataJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadDataJMenuItemActionPerformed
        JFileChooser jFileChooser = new JFileChooser();
        int result = jFileChooser.showOpenDialog(this);
        if( result == JFileChooser.APPROVE_OPTION ) {
            try {                 
                items = new ArrayList<>();
                File file = jFileChooser.getSelectedFile();
                Scanner scn = new Scanner(file);
                rectangles.clear();
                //RainbowScale rbS = new RainbowScale();
                GrayScale rbS = new GrayScale();
                
                int id = 0;
                while( scn.hasNext() ) {
                    String[] linha = scn.nextLine().split(";");
                    double x = Double.parseDouble(linha[1]);
                    double y = Double.parseDouble(linha[2]);
                    int grupo = id;//Integer.parseInt(linha[3]);

                    rectangles.add(new RetanguloVis(x, y, RECTSIZE, RECTSIZE, rbS.getColor((grupo*10)%255), id++));   
                    items.add(new Point2D.Double(x, y));
                }
                
                centerPoints = new Point2D.Double[rectangles.size()];
                for( int i = 0; i < centerPoints.length; ++i )
                    centerPoints[i] = new Point2D.Double(rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY());
                

                loadedData = true;
                if( view != null ) {
                    view.cleanImage();
                    view.repaint();            
                }
            } catch( FileNotFoundException e ) {

            }
        }
    }//GEN-LAST:event_loadDataJMenuItemActionPerformed

    private void rwordleCJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rwordleCJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        double[] center0 = Util.getCenter(rects);
        RWordleC rw = new RWordleC();
        Map<OverlapRect, OverlapRect> projected = rw.apply(rects);
        ArrayList<OverlapRect> projectedValues = Util.getProjectedValues(projected);
        double[] center1 = Util.getCenter(projectedValues);
        
        double ammountX = center0[0]-center1[0];
        double ammountY = center0[1]-center1[1];
        Util.translate(projectedValues, ammountX, ammountY);
        
        Util.normalize(projectedValues);
        Util.toRectangleVis(rectangles, projectedValues);

        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_rwordleCJMenuItemActionPerformed

    private void rwordleLJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rwordleLJMenuItemActionPerformed
        try {
            alpha = 0;//Double.parseDouble(anguloJTextField.getText());
        } catch( NumberFormatException e ) {
            alpha = 0;
        }
        
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        double[] center0 = Util.getCenter(rects);
        
        RWordleL rwl = new RWordleL(alpha, false);
        Map<OverlapRect, OverlapRect> projected = rwl.apply(rects);
        ArrayList<OverlapRect> projectedValues =  Util.getProjectedValues(projected);
        double[] center1 = Util.getCenter(projectedValues);
                
        double ammountX = center0[0]-center1[0];
        double ammountY = center0[1]-center1[1];
        Util.translate(projectedValues, ammountX, ammountY);
        
        Util.normalize(projectedValues);
        Util.toRectangleVis(rectangles, projectedValues);

        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_rwordleLJMenuItemActionPerformed
    
    private boolean temSobreposicao(double x, double y, ArrayList<ChangeRetangulo> R, int id) {
        double x1 = x, x2 = x+RECTSIZE;
        double y1 = y, y2 = y+RECTSIZE;
        
        for( int i = 0; i < R.size(); ++i ) {
            OverlapRect r = R.get(i).third.getUX() == 0.0 && R.get(i).third.getUY() == 0.0
                          ? R.get(i).second : R.get(i).third;
            
            if( r.getId() == id )
                continue;
            
            if( x2 > r.getUX() && x1 < r.getUX()+RECTSIZE && y2 > r.getUY() && y1 < r.getUY()+RECTSIZE )
                return true;            
        }
        return false;
    }
    
    private void findPosition(ArrayList<ChangeRetangulo> R) {
        Collections.sort(R, new Comparator<ChangeRetangulo>() {

            @Override
            public int compare(ChangeRetangulo o1, ChangeRetangulo o2) {
                
                double d1 = Math.sqrt(Math.pow(o1.first.getUX()-o1.second.getUX(), 2)
                                        +
                             Math.pow(o1.first.getUY()-o1.second.getUY(), 2));
                double d2 = Math.sqrt(Math.pow(o2.first.getUX()-o2.second.getUX(), 2)
                                        +
                             Math.pow(o2.first.getUY()-o2.second.getUY(), 2));
                if( d1 < d2 )
                    return -1;
                else if( d1 > d2 )
                    return 1;
                
                return 0;
            }
        });
        
        for( int j = 0; j < R.size(); ++j ) {
            ChangeRetangulo r = R.get(j);
            
            double FACTOR = 0.0001;
            double i = 1-FACTOR;
            double x = 0, y = 0;
            
            for( ; i >= 0.0; i -= FACTOR ) {
                x = (1.0-i)*r.second.getUX() + i*r.first.getUX();
                y = (1.0-i)*r.second.getUY() + i*r.first.getUY();                
                if( !temSobreposicao(x, y, R, r.third.getId()) )  
                    break;
            }
            i += FACTOR;
            x = (1.0-i)*r.second.getUX() + i*r.first.getUX();
            y = (1.0-i)*r.second.getUY() + i*r.first.getUY();
            
           // System.out.println("Position: (x:"+x+", y:"+y+") - (x:"+r.second.getUX()+", y:"+r.second.getUY()+")");
            r.third.setUX(x);
            r.third.setUY(y);
                    
        }
    }
    
    private void reduceKNN(int id, Pair[][] knn, ArrayList<ChangeRetangulo> R) {
        if( R.get(id).third.getUX() != 0.0 || R.get(id).third.getUY() != 0.0 )
            return;
        
        double FACTOR = 0.0001;
        double i = 1-FACTOR;
        double x = 0, y = 0;

        for( ; i >= 0.0; i -= FACTOR ) {
            x = (1.0-i)*R.get(id).second.getUX() + i*R.get(id).first.getUX();
            y = (1.0-i)*R.get(id).second.getUY() + i*R.get(id).first.getUY();                
            if( !temSobreposicao(x, y, R, R.get(id).third.getId()) )  
                break;
        }
        i += FACTOR;
       // System.out.println("i: "+i+", distancia: "+d);
        x = (1.0-i)*R.get(id).second.getUX() + i*R.get(id).first.getUX();
        y = (1.0-i)*R.get(id).second.getUY() + i*R.get(id).first.getUY();

       // System.out.println("Position: (x:"+x+", y:"+y+") - (x:"+r.second.getUX()+", y:"+r.second.getUY()+")");
        R.get(id).third.setUX(x);
        R.get(id).third.setUY(y);
        
        // faz para os vizinhos até parar...
        for( int j = 0; j < knn[id].length; ++j ) 
            reduceKNN(knn[id][j].index, knn, R);
    }
    
    private void findPosition(ArrayList<ChangeRetangulo> R, Pair[][] knn) {
        OverlapRect[] rs = new OverlapRect[R.size()];
        int idMinDist = 0;
        double dist = Double.MAX_VALUE;
        for( int i = 0; i < R.size(); ++i ) {
            double d = Math.sqrt(Math.pow(R.get(i).first.getUX()-R.get(i).second.getUX(), 2)
                                        +
                        Math.pow(R.get(i).first.getUY()-R.get(i).second.getUY(), 2));
            if( d < dist ) {
                dist = d;
                idMinDist = i;
            }
        }
        
        reduceKNN(idMinDist, knn, R);
        
        for( int j = 0; j < knn.length; ++j ) {
            reduceKNN(j, knn, R);
        }       
    }
    
    
    private void vpscJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vpscJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        double[] center0 = Util.getCenter(rects);
        VPSC vpsc = new VPSC();
        
        Map<OverlapRect, OverlapRect> projected = vpsc.applyAndShowTime(rects);        
        ArrayList<OverlapRect> projectedValues = Util.getProjectedValues(projected);        
        
        for( OverlapRect r: projectedValues ) {
            System.out.println(r.getId()+": "+r.x+"; "+r.y);
        }
        
        
        double[] center1 = Util.getCenter(projectedValues);
        double ammountX = center0[0]-center1[0];
        double ammountY = center0[1]-center1[1];
        Util.translate(projectedValues, ammountX, ammountY);                
        Util.normalize(projectedValues);          
        
        Util.toRectangleVis(rectangles, projectedValues);
        
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_vpscJMenuItemActionPerformed

    

    private void prismJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prismJMenuItemActionPerformed
        int algo = Integer.parseInt(JOptionPane.showInputDialog("Deseja utilizar uma estrutura de matriz esparsa?\n0-Não\n1-Sim"));
        boolean applySeamCarving = Integer.parseInt(JOptionPane.showInputDialog("Apply SeamCarving?")) == 1;
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        
        double[] center0 = Util.getCenter(rects);
        PRISM prism = new PRISM(algo);
        Map<OverlapRect, OverlapRect> projected = prism.applyAndShowTime(rects);
        ArrayList<OverlapRect> projectedValues = Util.getProjectedValues(projected);
        double[] center1 = Util.getCenter(projectedValues);
        
        double ammountX = center0[0]-center1[0];
        double ammountY = center0[1]-center1[1];
        Util.translate(projectedValues, ammountX, ammountY);        
        Util.normalize(projectedValues);
                
        if( applySeamCarving )
            addSeamCarvingResult(projectedValues);
        
        Util.toRectangleVis(rectangles, projectedValues);
        
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_prismJMenuItemActionPerformed

    private void addSeamCarvingResult(ArrayList<OverlapRect> projected) {
        Rectangle2D.Double[] r2ds = new Rectangle2D.Double[projected.size()];
        for( int i = 0; i < r2ds.length; ++i )
            r2ds[i] = new Rectangle2D.Double(projected.get(i).getUX(), projected.get(i).getUY(), projected.get(i).width, projected.get(i).height);
        
        //SeamCarving sc = new SeamCarving(r2ds);
        OverlapRect[] array = new OverlapRect[projected.size()];
        array = projected.toArray(array);
        //Map<Rectangle2D.Double, Rectangle2D.Double> mapSeamCarving = sc.reduceSpace(array);
        ContextPreserving cp = new ContextPreserving(r2ds);
        Map<Rectangle2D.Double, Rectangle2D.Double> mapContextPreserving = cp.reduceSpace(array);
        
        afterSeamCarving = new ArrayList<>();
//        mapSeamCarving.entrySet().forEach((element)->{
//            int idx = ((OverlapRect)element.getKey()).getId();
//            
//            afterSeamCarving.add(new RetanguloVis(element.getValue().getMinX(), element.getValue().getMinY(),
//                    RECTSIZE, RECTSIZE, rectangles.get(idx).cor, rectangles.get(idx).numero));
//        });
        mapContextPreserving.entrySet().forEach((element)->{
            int idx = ((OverlapRect)element.getKey()).getId();
            
            afterSeamCarving.add(new RetanguloVis(element.getValue().getMinX(), element.getValue().getMinY(), 
                RECTSIZE, RECTSIZE, rectangles.get(idx).cor, rectangles.get(idx).numero));
        
        });
        
    }

    private void projSnippetJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projSnippetJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        
        int i = 0;
        for( OverlapRect r: rects )
            r.setId(i++);
        double[] center0 = Util.getCenter(rects);
        //ArrayList<Retangulo> projected = ProjSnippet.e_o(rects, Double.parseDouble(projSnippetAlphaJTextField.getText()));
        
        String alpha_value = JOptionPane.showInputDialog("Por favor, insira o valor para 'alpha':");
        String k_value = JOptionPane.showInputDialog("Por favor, insira o valor de 'k':");
        boolean applySeamCarving = Integer.parseInt(JOptionPane.showInputDialog("Apply SeamCarving?")) == 1;
        
        ProjSnippet ps = new ProjSnippet(Double.parseDouble(alpha_value), Integer.parseInt(k_value)+1);
        Map<OverlapRect, OverlapRect> projected = ps.apply(rects);
        ArrayList<OverlapRect> projectedValues = Util.getProjectedValues(projected);
        if( projected != null ) {
            
            double[] center1 = Util.getCenter(projectedValues);

            double ammountX = center0[0]-center1[0];
            double ammountY = center0[1]-center1[1];
            Util.translate(projectedValues, ammountX, ammountY);
            Util.normalize(projectedValues);
            
            if( applySeamCarving )
                addSeamCarvingResult(projectedValues);
            
            Util.toRectangleVis(rectangles, projectedValues);

            view.cleanImage();
            view.repaint();
        } else
            JOptionPane.showMessageDialog(this, "Houve um problema ao aplicar o método Projsnippet.");
    }//GEN-LAST:event_projSnippetJMenuItemActionPerformed

    private void incBoardJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_incBoardJMenuItemActionPerformed
        
        JFileChooser jFileChooser = new JFileChooser();

        int result = jFileChooser.showOpenDialog(this);
        if( result == JFileChooser.APPROVE_OPTION ) {
            try {                 
                File file = jFileChooser.getSelectedFile();

                rectangles.clear();
                ArrayList<PointItem> items = new ArrayList<>();
                Scanner scn = new Scanner(file);
                for( int i = 0; i < 4; ++i ) 
                    if( scn.hasNext() )
                        scn.nextLine();
                
                int id = 0;
                while( scn.hasNext() ) {
                    String[] linha = scn.nextLine().split(";");
                    int grupo = Integer.parseInt(linha[linha.length-1]);
                    double[] dims = new double[linha.length-2];
                    for( int i = 1, j = 0; i < linha.length-1; ++i )
                        dims[j++] = Double.parseDouble(linha[i]);            
                    items.add(new PointItem(dims, String.valueOf(id), id, grupo));
                    id++;
                }

                IncBoardExecutor executor = new IncBoardExecutor();
                executor.apply(items);
                
                rectangles.clear();
                int ymin = Math.abs(executor.getMinRow());
                int xmin = Math.abs(executor.getMinCol());
                RainbowScale rbS = new RainbowScale();
                for( PointItem d: executor.getItems() ) {
                    rectangles.add(new RetanguloVis(30*(d.getCol()+xmin), 30*(d.getRow()+ymin), 
                                                    30, 30, rbS.getColor((d.getGrupo()*10)%255), 
                                                    d.getId()));
                }

                if( view != null ) {
                    view.cleanImage();
                    view.repaint();            
                }
            }catch(IOException e) {

            }
        }
    }//GEN-LAST:event_incBoardJMenuItemActionPerformed

    private void hexBoardJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hexBoardJMenuItemActionPerformed
        
        JFileChooser jFileChooser = new JFileChooser();

        int result = jFileChooser.showOpenDialog(this);
        if( result == JFileChooser.APPROVE_OPTION ) {
            try {                 
                File file = jFileChooser.getSelectedFile();

                rectangles.clear();
                ArrayList<PointItem> items = new ArrayList<>();
                Scanner scn = new Scanner(file);
                for( int i = 0; i < 4; ++i ) 
                    if( scn.hasNext() )
                        scn.nextLine();
                
                int id = 0;
                while( scn.hasNext() ) {
                    String[] linha = scn.nextLine().split(";");
                    int grupo = Integer.parseInt(linha[linha.length-1]);
                    double[] dims = new double[linha.length-2];
                    for( int i = 1, j = 0; i < linha.length-1; ++i )
                        dims[j++] = Double.parseDouble(linha[i]);            
                    items.add(new PointItem(dims, String.valueOf(id), id, grupo));
                    ++id;
                }

                HexBoardExecutor executor = new HexBoardExecutor();
                executor.apply(items);

                int zMin = executor.getMinRow()-executor.getMinCol();
                
                int minDist = Integer.MAX_VALUE;
                PointItem q = null;
                int zMIN = Integer.MAX_VALUE;
                for( PointItem d: executor.getItems() ) {
                    int z = d.getRow()-d.getCol();
                    if( z < zMIN )
                        zMIN = z;
                    int x;
                    if( zMin > z )
                        x = executor.getMinRow()+((Math.abs(zMin)+Math.abs(z))/2);
                    else
                        x = executor.getMinRow()-((Math.abs(zMin)+Math.abs(z))/2);
                    int dist = d.getCol()-x;
                    if( dist < minDist ) {
                        minDist = dist;
                        q = d;
                    }                    
                }
                
                int xmin = HEXBOARD_SIZE;
                int a  = (int)Math.sqrt( (HEXBOARD_SIZE*HEXBOARD_SIZE) - (Math.pow(HEXBOARD_SIZE/2,2)) );
                RainbowScale rbS = new RainbowScale();
                for( PointItem d: executor.getItems() ) {
                    int z = d.getRow() - d.getCol();
                    int centerHexY = (3*HEXBOARD_SIZE/2)*(z + Math.abs(zMIN))+HEXBOARD_SIZE;
                    int distancia = (Math.abs(q.getRow()-d.getRow())+Math.abs(q.getCol()-d.getCol()))*a + xmin;

                    rectangles.add(new RetanguloVis(distancia-(HEXBOARD_SIZE/2), centerHexY-(HEXBOARD_SIZE/2), HEXBOARD_SIZE, HEXBOARD_SIZE, 
                            d.getGrupo() == 1 ? rbS.getColor(5) : d.getGrupo() == 2 ? rbS.getColor(120) : rbS.getColor(200),
                            d.getId()));
                    rectangles.get(rectangles.size()-1).setP(new Point(distancia, centerHexY));
                    rectangles.get(rectangles.size()-1).setIsHexBoard(true);
                }
                view.cleanImage();
                view.repaint();
            } catch( IOException e ) {

            }
        }
    }//GEN-LAST:event_hexBoardJMenuItemActionPerformed

    private void limparJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limparJMenuItemActionPerformed
        currentCluster = null;
        selectedRepresentatives = null;
        embaralha();
    }//GEN-LAST:event_limparJMenuItemActionPerformed

    private void saveDataCoordJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDataCoordJMenuItemActionPerformed
        JFileChooser jFileChooser = new JFileChooser();

        int result = jFileChooser.showSaveDialog(this);
        if( result == JFileChooser.APPROVE_OPTION ) {
            try {
                File file = jFileChooser.getSelectedFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                try( BufferedWriter bw = new BufferedWriter(fw) ) {
                    ArrayList<OverlapRect> retangulos = Util.toRectangle(rectangles);
                    int i = 0;
                    for( OverlapRect r: retangulos ) {
                        bw.write(i+";"+r.getLX()+";"+r.getLY()+";"+i+"\n");
                        ++i;
                    }
                }
            } catch( IOException e ) {
                
            }
        }
    }//GEN-LAST:event_saveDataCoordJMenuItemActionPerformed

    private void sssJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sssJMenuItemActionPerformed
        double a = Double.parseDouble(JOptionPane.showInputDialog("Insira o valor de alpha: "));
        
        SSS sss = new SSS();
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        
        sss.selectPivots(rects, a, getMaxDistance());
        int i = 0;
        for( OverlapRect r: rects ) {
                r.setId(i++); 
                r.setLevel(1);
        }
        Util.toRectangleVis(rectangles, rects);
        System.out.println("OK SSS!");
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_sssJMenuItemActionPerformed

    private void gnatJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gnatJMenuItemActionPerformed
        
        
        GNAT gnat = new GNAT();
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        
        int k = 5*rects.size()/100; //Integer.parseInt(JOptionPane.showInputDialog("Insira o número de pivôs: "));
        
        gnat.selectPivots(rects, k);
        int i = 0;
        for( OverlapRect r: rects ) {
            r.setId(i++);
            r.setLevel(1);
        }
        Util.toRectangleVis(rectangles, rects);
        System.out.println("OK GNAT!");
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_gnatJMenuItemActionPerformed

    private void omniJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_omniJMenuItemActionPerformed
        int card = Integer.parseInt(JOptionPane.showInputDialog("Insira a número de focos: "));
        
        OMNI omni = new OMNI();
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        
        omni.selectPivots(rects, card);
        int i = 0;
        for( OverlapRect r: rects ) {
            r.setId(i++);
            r.setLevel(1);
        }
        
        Util.toRectangleVis(rectangles, rects);
        System.out.println("OK OMNI!");
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_omniJMenuItemActionPerformed

    private void mstJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mstJMenuItemActionPerformed
        
        MST mst = new MST();
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        
        int i = 0;
        for( OverlapRect r: rects ) 
            r.setId(i++);
        mst.selectPivots(rects, 20);
        
        Util.toRectangleVis(rectangles, rects);
        System.out.println("OK MST!");
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_mstJMenuItemActionPerformed

    private void extractParametersJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractParametersJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<OverlapRect> pivots = new ArrayList<>();
        ArrayList<OverlapRect> elements = new ArrayList<>();
        
        for( int i = 0; i < rects.size(); ++i ) {
            if( rects.get(i).isPivot() )
                pivots.add(rects.get(i));
            else
                elements.add(rects.get(i));
        }
        System.out.println("SIZE: "+pivots.size()+", "+elements.size());
        
        double parameters[] = Util.extractParameters(40, pivots, elements);
        
        JOptionPane.showMessageDialog(this, "Mean: "+parameters[0]+", #Overlap: "+parameters[1]);
        
    }//GEN-LAST:event_extractParametersJMenuItemActionPerformed

    private void salvarImagemJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salvarImagemJMenuItemActionPerformed
        JFileChooser jFileChooser = new JFileChooser();

        int result = jFileChooser.showSaveDialog(this);
        if( result == JFileChooser.APPROVE_OPTION ) {
            try {
                File file = jFileChooser.getSelectedFile();
                view.adjustPanel();
                BufferedImage img = view.getImage();
                ImageIO.write(img, "png", file);
            } catch( IOException e ) {
                System.out.println(e.getMessage());
            }
        }
    }//GEN-LAST:event_salvarImagemJMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        view.r3 = new RetanguloVis(view.r1.getUX(), view.r1.getUY(), 30, 30, Color.red, 3);
        
        double d = Math.sqrt(Math.pow(view.r2.getUX()-view.r1.getUX(), 2)
                                        +
                             Math.pow(view.r2.getUY()-view.r1.getUY(), 2));
        
        view.cleanImage();
        view.repaint();
        
        
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            private double i = 0.0;
            @Override
            public void run() {
                System.out.println("Ola: "+i);
                
                double x = (1.0-i)*view.r1.getUX() + i*view.r2.getUX();
                double y = (1.0-i)*view.r1.getUY() + i*view.r2.getUY();
                view.r3.setUX(x);
                view.r3.setUY(y);
                view.cleanImage();
                view.repaint();
                i += 0.05;
                if( i >= 1.0 )
                    cancel();
            }
        }, 0, 100);
        
        //Timer t = new Timer();
       // t.schedule(null, WIDTH, WIDTH);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void dijsktraRepresentativeJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dijsktraRepresentativeJMenuItemActionPerformed
        Vertex[] grafo = new Vertex[rectangles.size()];
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        OverlapRect[] rectsV = new OverlapRect[rects.size()];
        rectsV = rects.toArray(rectsV);

        for( int i = 0; i < grafo.length; ++i )
            grafo[i] = new Vertex(i);
        for( int i = 0; i < grafo.length; ++i ) 
            for( int j = i+1; j < grafo.length; ++j )  {
                double d = Util.euclideanDistance(rectsV[i].getCenterX(), rectsV[i].getCenterY(), rectsV[j].getCenterX(), rectsV[j].getCenterY());
                System.out.println("Distance: "+d);
                
                grafo[i].add(new Edge(i, j, d));
                grafo[j].add(new Edge(j, i, d));
            }
        
        Dijsktra d = new Dijsktra(grafo);
        for( int i = 0; i < grafo.length; ++i )
            for( int j = i+1; j < grafo.length; ++j )
                d.exec(i, j, rectsV);
        
        maior = Integer.MIN_VALUE;
        menor = Integer.MAX_VALUE;
        for( int i = 0; i < rectsV.length; ++i ) {
            System.out.println("Health: "+rectsV[i].getHealth());
            maior = Math.max(maior, rectsV[i].getHealth());
            menor = Math.min(menor, rectsV[i].getHealth());
        }
        Util.toRectangleVis(rectangles, rects);
        view.cleanImage();
        view.repaint();
        
    }//GEN-LAST:event_dijsktraRepresentativeJMenuItemActionPerformed

    private void hierarchicalClusteringJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hierarchicalClusteringJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Point.Double> points = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            points.add(new Point.Double(rects.get(i).getCenterX(), rects.get(i).getCenterY()));
        hc = new HierarchicalClustering(points, new SingleLinkageStrategy());        
        hc.execute();
        
        nivelDendrogram = 0;
        hc.setDendogramLevel(nivelDendrogram);
        selectedRepresentatives = hc.getRepresentatives();
        clusters = hc.getClusterHierarchy();
        currentCluster = clusters.get(0);
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_hierarchicalClusteringJMenuItemActionPerformed

    private void incrementJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_incrementJMenuItemActionPerformed
        if( nivelDendrogram < clusters.size()-1 )
            nivelDendrogram++;
        currentCluster = clusters.get(nivelDendrogram);
        selectedRepresentatives = Util.selectRepresentatives(currentCluster, items);
        
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_incrementJMenuItemActionPerformed

    private void decrementJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decrementJMenuItemActionPerformed
        if( nivelDendrogram > 0 )
            nivelDendrogram--;
        currentCluster = clusters.get(nivelDendrogram);
        selectedRepresentatives = Util.selectRepresentatives(currentCluster, items);
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_decrementJMenuItemActionPerformed

    private void kMeansJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kMeansJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Point.Double> points = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            points.add(new Point.Double(rects.get(i).getCenterX(), rects.get(i).getCenterY()));
                
        RepresentativeFinder kmeans = new KMeans(points, new FarPointsMedoidApproach(), 3);
        kmeans.execute();
        currentCluster = ((KMeans)kmeans).getClusters();        
        selectedRepresentatives = kmeans.getRepresentatives();
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_kMeansJMenuItemActionPerformed

    private void kMedoidJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kMedoidJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Point.Double> points = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            points.add(new Point.Double(rects.get(i).getCenterX(), rects.get(i).getCenterY()));
                
        RepresentativeFinder kmedoid = new KMedoid(points, new FarPointsMedoidApproach(), 4);
        kmedoid.execute();
        currentCluster = ((KMedoid)kmedoid).getClusters();        
        selectedRepresentatives = kmedoid.getRepresentatives();
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_kMedoidJMenuItemActionPerformed

    private void bisectingKMeansJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bisectingKMeansJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Point.Double> points = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            points.add(new Point.Double(rects.get(i).getCenterX(), rects.get(i).getCenterY()));
        
        RepresentativeFinder bkmeans = new BisectingKMeans(points, new FarPointsMedoidApproach(), 4);
        bkmeans.execute();
        currentCluster = ((BisectingKMeans)bkmeans).getClusters();
        selectedRepresentatives = bkmeans.getRepresentatives();
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_bisectingKMeansJMenuItemActionPerformed

    private void dbscanJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbscanJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Point.Double> points = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            points.add(new Point.Double(rects.get(i).getCenterX(), rects.get(i).getCenterY()));
        
        RepresentativeFinder dbscan = new Dbscan(points, 100, 10);
        dbscan.execute();
        currentCluster = ((Dbscan)dbscan).getClusters();
        selectedRepresentatives = dbscan.getRepresentatives();
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_dbscanJMenuItemActionPerformed

    private void csmJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_csmJMenuItemActionPerformed
        
        JFileChooser jFileChooser = new JFileChooser();
        int result = jFileChooser.showOpenDialog(this);
        if( result == JFileChooser.APPROVE_OPTION ) {
            try {                 
                File file = jFileChooser.getSelectedFile();
                Scanner scn = new Scanner(file);
                scn.nextLine();
                scn.nextLine();
                scn.nextLine();
                scn.nextLine();
                ArrayList<ArrayList<Double>> attrs = new ArrayList<>();
                while( scn.hasNext() ) {
                    
                    attrs.add(new ArrayList<>());
                    String[] linhas = scn.nextLine().split(";");
                    for( int i = 1; i < linhas.length-1; ++i ) 
                        attrs.get(attrs.size()-1).add(Double.parseDouble(linhas[i]));                        
                    
                }
                
                CSM csm = new CSM(attrs, (int) ((int) attrs.size()*0.2), attrs.size());
                csm.execute();

                selectedRepresentatives = csm.getRepresentatives();
                
                if( view != null ) {
                    view.cleanImage();
                    view.repaint();            
                }
            } catch( FileNotFoundException e ) {

            }
        }
    }//GEN-LAST:event_csmJMenuItemActionPerformed

    private void viewSelectedJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewSelectedJMenuItemActionPerformed
        
        indexRepresentatives = (indexRepresentatives+1) % selectedRepresentatives.length;
        
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_viewSelectedJMenuItemActionPerformed

    private void ksvdJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ksvdJMenuItemActionPerformed
        JFileChooser jFileChooser = new JFileChooser();
        int result = jFileChooser.showOpenDialog(this);
        if( result == JFileChooser.APPROVE_OPTION ) {
            try {                 
                File file = jFileChooser.getSelectedFile();
                Scanner scn = new Scanner(file);
                scn.nextLine();
                scn.nextLine();
                scn.nextLine();
                scn.nextLine();
                ArrayList<ArrayList<Double>> attrs = new ArrayList<>();
                while( scn.hasNext() ) {
                    
                    attrs.add(new ArrayList<>());
                    String[] linhas = scn.nextLine().split(";");
                    for( int i = 1; i < linhas.length-1; ++i ) 
                        attrs.get(attrs.size()-1).add(Double.parseDouble(linhas[i]));                        
                    
                }
                
                KSvd ksvd = new KSvd(attrs, (int) ((int) attrs.size()*0.2));
                ksvd.execute();
                
                selectedRepresentatives = ksvd.getRepresentatives();
                
                if( view != null ) {
                    view.cleanImage();
                    view.repaint();            
                }
                
            } catch( FileNotFoundException e ) {

            }
        }
        
        
    }//GEN-LAST:event_ksvdJMenuItemActionPerformed

    private void showHideJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showHideJMenuItemActionPerformed
        hideShowNumbers = !hideShowNumbers;
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_showHideJMenuItemActionPerformed

    private void smrsJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smrsJMenuItemActionPerformed
        JFileChooser jFileChooser = new JFileChooser();
        int result = jFileChooser.showOpenDialog(this);
        if( result == JFileChooser.APPROVE_OPTION ) {
            try {                 
                File file = jFileChooser.getSelectedFile();
                Scanner scn = new Scanner(file);
                scn.nextLine();
                scn.nextLine();
                scn.nextLine();
                scn.nextLine();
                ArrayList<ArrayList<Double>> attrs = new ArrayList<>();
                while( scn.hasNext() ) {
                    
                    attrs.add(new ArrayList<>());
                    String[] linhas = scn.nextLine().split(";");
                    for( int i = 1; i < linhas.length-1; ++i ) 
                        attrs.get(attrs.size()-1).add(Double.parseDouble(linhas[i]));                        
                    
                }
                SMRS smrs = new SMRS(attrs);
                smrs.execute();
                
                selectedRepresentatives = smrs.getRepresentatives();
                
                if( view != null ) {
                    view.cleanImage();
                    view.repaint();            
                }
                
            } catch( FileNotFoundException e ) {

            }
        }
    }//GEN-LAST:event_smrsJMenuItemActionPerformed

    private void ds3JMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ds3JMenuItemActionPerformed
        
        if( rectangles == null )
            loadDataJMenuItemActionPerformed(null);
        
        double[][] distances = new double[rectangles.size()][rectangles.size()];
        for( int i = 0; i < distances.length; ++i )
            for( int j = 0; j < distances[0].length; ++j )
                distances[i][j] = Util.euclideanDistance(rectangles.get(i).x, rectangles.get(i).y, rectangles.get(j).x, rectangles.get(j).y);
        
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            private double i = 0.0;
            @Override
            public void run() {
                iImage = i; 
                DS3 ds3 = new DS3(distances, i);
                ds3.execute();
                selectedRepresentatives = ds3.getRepresentatives();
                
                view.cleanImage();
                view.repaint();
                i += 0.01;
                if( i > 0.5 )
                    cancel();
            }
        }, 0, 1000);        
        
    }//GEN-LAST:event_ds3JMenuItemActionPerformed

    private void runDs3JMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runDs3JMenuItemActionPerformed
        if( rectangles == null )
            loadDataJMenuItemActionPerformed(null);
        
        Point2D.Double[] points = new Point2D.Double[rectangles.size()];
        double[][] distances = new double[rectangles.size()][rectangles.size()];
        for( int i = 0; i < distances.length; ++i ) {
            for( int j = 0; j < distances[0].length; ++j )
                distances[i][j] = Util.euclideanDistance(rectangles.get(i).x, rectangles.get(i).y, rectangles.get(j).x, rectangles.get(j).y);
            points[i] = new Point2D.Double(rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY());
        }
        
        RepresentativeFinder ds3 = new DS3(distances, 0.12); // gives the best results 
        ds3.execute(); 
        selectedRepresentatives = ds3.getRepresentatives();
        selectedRepresentatives = Util.distinct(selectedRepresentatives, points, 0);
        hashRepresentative = Util.createIndex(selectedRepresentatives, points);
        
//        //view.adjustPanel();
//        int width = view.getSize().width;
//        int height = view.getSize().height;
//        heatmap = new int[width][height];
//        System.out.println(">> "+width+", "+height);
//        
//        //for( int i = 0; i < selectedRepresentatives.length; ++i ) {
//            int radius = hashRepresentative.get(selectedRepresentatives[0]).size()*3;
//            Point2D.Double p = points[selectedRepresentatives[0]];
//            System.out.println("RADIUS: "+radius);
//            System.out.println("x: "+p.x+", y: "+p.y);
//
//            heatmap = Util.fillSphere(radius, heatmap, (int)p.x, (int)p.y, hashRepresentative.get(selectedRepresentatives[0]).size());
//        //}
    
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_runDs3JMenuItemActionPerformed

    private void nextDendogramJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextDendogramJMenuItemActionPerformed
        hc.setDendogramLevel(++nivelDendrogram);
        selectedRepresentatives = hc.getRepresentatives();
        //clusters = hc.getClusterHierarchy();
        //currentCluster = clusters.get(0);
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_nextDendogramJMenuItemActionPerformed

    private void voronoiDiagramJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voronoiDiagramJMenuItemActionPerformed
        
        if( view != null && selectedRepresentatives != null ) {
            Polygon window = new Polygon();
            int width = view.getSize().width;
            int height = view.getSize().height;
            window.addPoint(0, 0);
            window.addPoint(width, 0);
            window.addPoint(width, height);
            window.addPoint(0, height);
            
            
            Point2D.Double[] points = new Point2D.Double[selectedRepresentatives.length];
            for( int i = 0; i < selectedRepresentatives.length; ++i ) {
                int index = selectedRepresentatives[i];
                points[i] = new Point2D.Double(rectangles.get(index).getCenterX(), rectangles.get(index).getCenterY());
            }
            
            int n = (int)Arrays.stream(points).distinct().count();
            Iterator<Point2D.Double> it = Arrays.stream(points).distinct().iterator();
            int k = 0;
            points = new Point2D.Double[n];
            while( it.hasNext() ) {
                Point2D.Double p = it.next();
                points[k++] = p;
            }
            
            Point2D.Double[] pointsHull = new Point2D.Double[rectangles.size()];
            for( int i = 0; i < rectangles.size(); ++i )
                pointsHull[i] = new Point2D.Double(rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY());           
            Point2D.Double[] hull = Util.convexHull(pointsHull);
            
            diagrams = Util.voronoiDiagram(window, points);            
            intersects = Util.clipBounds(diagrams, hull);
            
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_voronoiDiagramJMenuItemActionPerformed

    
    public double getMaxDistance() {
        double d = Double.MIN_VALUE;
        
        for( int i = 0; i < rectangles.size(); ++i )
            for( int j = 0; j <= i-1; ++j ) {
                double dd = Util.euclideanDistance(rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY(), 
                                                     rectangles.get(j).getCenterX(), rectangles.get(j).getCenterY());
                d = Math.max(d, dd);
            }
                
        
        return d;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Menu().setVisible(true);
            }
        });
    }
    
    private void embaralha() {
        globalCounterColor = globalCounter = 0;
        
        rectangles.clear();
       
        if( view != null ) {
            view.r1 = view.r2 = view.r3 = null;
            view.cleanImage();
            view.repaint();            
        }
    }

    
    
    
    public class ViewPanel extends JPanel {
        private Color color = Color.RED;
        
        private double iniX, iniY, fimX, fimY;
        public RetanguloVis r1 = null, r2 = null, r3 = null;
        private BufferedImage imageBuffer;
        
        public ViewPanel() {
            setBackground(Color.WHITE);
            setLayout(new FlowLayout(FlowLayout.LEFT));
            rectangles = new ArrayList<>();
            afterSeamCarving = new ArrayList<>();
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    /*iniX = e.getX();
                    iniY = e.getY();
                    
                    //RainbowScale rbS = new RainbowScale();
                    GrayScale gS = new GrayScale();
                    rectangles.add(new RetanguloVis(iniX, iniY, RECTSIZE, RECTSIZE, 
                                                //gS.getColor((globalCounterColor++*10)%255), globalCounter++));   
                                                gS.getColor(0), globalCounter++));*/
                     
                } 
                
            }); 
            
            addMouseMotionListener(new MouseAdapter() {

                @Override
                public void mouseMoved(MouseEvent e) {
                    int index = -1;
                    if( selectedRepresentatives != null && hashRepresentative != null ) {
                        for( int i = 0; i < selectedRepresentatives.length; ++i ) {
                            Point2D.Double p = new Point2D.Double(rectangles.get(selectedRepresentatives[i]).getCenterX(), 
                                               rectangles.get(selectedRepresentatives[i]).getCenterY());
                            if( Util.euclideanDistance(e.getX(), e.getY(), p.x, p.y) < RECTSIZE/2 ) {
                                index = selectedRepresentatives[i];
                                break;
                            }
                        }

                        nearest = null;
                        if( index != -1 ) {
                            nearest = hashRepresentative.get(index);

                        }
                        cleanImage();
                        repaint();  
                    }
                }
            
            
            });
                     
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
                setPreferredSize(getSize());
                this.imageBuffer = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
                
                java.awt.Graphics2D g2Buffer = this.imageBuffer.createGraphics();
                g2Buffer.setColor(this.getBackground());
                g2Buffer.fillRect(0, 0, 5000, 5000);

                g2Buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ArrayList<RetanguloVis> pivots = new ArrayList<>();
                if( afterSeamCarving.isEmpty() )
                {                
                    if( currentCluster != null && !rectangles.isEmpty() ) {
                        RainbowScale rbS = new RainbowScale();
                        int passo = 30;                        
                        ArrayList<ArrayList<Integer>> indexes = currentCluster;
                        System.out.println("Painting "+nivelDendrogram+"th level");
                        for( int i = 0; i < indexes.size(); ++i ) {
                            Color cor = rbS.getColor((i+1)*passo);                            
                            for( int j = 0; j < indexes.get(i).size(); ++j ) {
                                int index = indexes.get(i).get(j);
                                rectangles.get(index).cor = cor;
                            }                            
                            System.out.println();
                        }
                        
                    }
                    
                    
                    for( RetanguloVis r: rectangles ) {                    
                        ///g2Buffer.setColor(r.cor);
                        int cinza = (int) (((double)(r.getHealth()-menor)/(double)(maior-menor))*255.0);
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
                                //g2Buffer.fillRect((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                                //g2Buffer.setColor(Color.BLACK);
                               // g2Buffer.drawRect((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                                if( currentCluster != null ) {
                                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
                                    g2Buffer.setColor(r.cor);
                                    g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                                } else if( selectedRepresentatives == null || hashRepresentative == null ) {
                                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
                                    g2Buffer.setColor(Color.BLUE);
                                    g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                                }
                            }
                        }

                        if( !r.isPivot() && hideShowNumbers ) {
                            g2Buffer.setColor(Color.RED);
                            g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
                            g2Buffer.drawString(String.valueOf(r.numero), (int)r.getUX()+10, (int)r.getUY()+10);                           
                        }
                    }
                     
                   
                    
                    if( nearest != null ) {                        
                        for( int i = 0; i < nearest.size(); ++i ) {
                            RetanguloVis r = rectangles.get(nearest.get(i));
                            g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
                            g2Buffer.setColor(Color.BLUE);
                            g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                            g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
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
                
                if( r1 != null ) {
                    g2Buffer.setColor(Color.RED);
                    g2Buffer.fillRect((int)r1.getUX(), (int)r1.getUY(), 30, 30);
                }
                
                if( r2 != null ) {
                    g2Buffer.setColor(Color.RED);
                    g2Buffer.fillRect((int)r2.getUX(), (int)r2.getUY(), 30, 30);
                }
                
                if( r3 != null ) {
                    g2Buffer.setColor(Color.BLACK);
                    g2Buffer.fillRect((int)r3.getUX(), (int)r3.getUY(), 20, 20);
                }
                                
                for( RetanguloVis r: pivots ) {
                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
                    g2Buffer.setColor(Color.RED);                    
                    g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());      
                    g2Buffer.setColor(Color.BLACK);
                    g2Buffer.drawOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());                    
                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
                }
                
                if( cRetangulo != null ) {
                    for( ChangeRetangulo r: cRetangulo ) {
                        int x1 = (int) r.second.getUX();
                        int y1 = (int) r.second.getUY();                    
                        int x2 = (int) r.third.getUX();
                        int y2 = (int) r.third.getUY();

                        OverlapRect rr = r.third;
                        
                        g2Buffer.fillRect((int)rr.getUX(), (int)rr.getUY(), (int)rr.getWidth(), (int)rr.getHeight());
                        g2Buffer.setColor(Color.BLACK);
                        g2Buffer.drawRect((int)rr.getUX(), (int)rr.getUY(), (int)rr.getWidth(), (int)rr.getHeight());
                        g2Buffer.setColor(Color.WHITE);
                        g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
                        g2Buffer.drawString(String.valueOf(rr.getId()), (int)rr.getUX()+10, (int)rr.getUY()+10);
                        
                        g2Buffer.setColor(Color.GRAY);
                        g2Buffer.drawLine(x1, y1, x2, y2);
                    }
                }
                
                if( iImage != 0 ) {
                    try {
                        File file = new File("C:\\Users\\wilson\\Desktop\\imagem\\imagem"+String.valueOf(iImage)+".png");
                        System.out.println("Salvando imagem"+String.valueOf(iImage)+".png...");
                        view.adjustPanel();
                        BufferedImage img = view.getImage();
                        ImageIO.write(img, "png", file);
                    } catch( IOException e ) {
                        System.out.println(e.getMessage());
                    } 
                }
                
//                if( diagrams != null ) {                    
//                    g2Buffer.setColor(Color.RED);                            
//                    
//                    for( int i = 0; i < diagrams.length; ++i ) 
//                        g2Buffer.drawPolygon(diagrams[i]);                    
//                }
                
//                if( hullPolygon != null ) {
//                    g2Buffer.setColor(Color.BLUE);
//                    g2Buffer.drawPolygon(hullPolygon);
//                }
                
                if( intersects != null ) {
                    g2Buffer.setColor(Color.BLUE);
                    
                    for( int i = 0; i < intersects.length; ++i )  
                        g2Buffer.drawPolygon(intersects[i]);
                }
                
                if( selectedRepresentatives != null ) {

                    if(  hashRepresentative != null ) {

                        Util.paintSphere(centerPoints, selectedRepresentatives, hashRepresentative, g2Buffer);

                        for( int i = 0; i < selectedRepresentatives.length; ++i ) {
                            RetanguloVis r = rectangles.get(selectedRepresentatives[i]);
                            g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
                            g2Buffer.setColor(Color.RED);
                            g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                            g2Buffer.setColor(Color.BLACK);
                            g2Buffer.drawOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                            if( hideShowNumbers ) {
                                g2Buffer.setColor(Color.GREEN);
                                g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
                                g2Buffer.drawString(String.valueOf(r.numero), (int)r.getUX()+10, (int)r.getUY()+10);  
                            }
                        }
                    } else {
                        for( int i = 0; i < selectedRepresentatives.length; ++i ) {
                            RetanguloVis r = rectangles.get(selectedRepresentatives[i]);
                            g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
                            g2Buffer.setColor(Color.RED);
                            g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                            g2Buffer.setColor(Color.BLACK);
                            g2Buffer.drawOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                                    
                            if( hideShowNumbers ) {
                                g2Buffer.setColor(Color.GREEN);
                                g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
                                g2Buffer.drawString(String.valueOf(r.numero), (int)r.getUX()+10, (int)r.getUY()+10);  
                            }
                        }
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

          /*  for( Vertex v : graph.getVertex() ) {
                v.setX(v.getX() + zero - min_x);
                v.setY(v.getY() + zero - min_y);
            }*/

            Dimension d = this.getSize();
            d.width = (int) max_x + zero;
            d.height = (int) max_y + zero;
            this.setSize(d);
            this.setPreferredSize(d);
        }

    }
    
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem bisectingKMeansJMenuItem;
    private javax.swing.JMenuItem csmJMenuItem;
    private javax.swing.JMenuItem dbscanJMenuItem;
    private javax.swing.JMenuItem decrementJMenuItem;
    private javax.swing.JMenuItem dijsktraRepresentativeJMenuItem;
    private javax.swing.JMenuItem ds3JMenuItem;
    private javax.swing.JMenuItem extractParametersJMenuItem;
    private javax.swing.JMenuItem gnatJMenuItem;
    private javax.swing.JMenuItem hexBoardJMenuItem;
    private javax.swing.JMenuItem hierarchicalClusteringJMenuItem;
    private javax.swing.JMenuItem incBoardJMenuItem;
    private javax.swing.JMenuItem incrementJMenuItem;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JMenuItem kMeansJMenuItem;
    private javax.swing.JMenuItem kMedoidJMenuItem;
    private javax.swing.JMenuItem ksvdJMenuItem;
    private javax.swing.JMenuItem limparJMenuItem;
    private javax.swing.JMenuItem loadDataJMenuItem;
    private javax.swing.JMenuItem mstJMenuItem;
    private javax.swing.JMenuItem nextDendogramJMenuItem;
    private javax.swing.JMenuItem omniJMenuItem;
    private javax.swing.JMenuItem prismJMenuItem;
    private javax.swing.JMenuItem projSnippetJMenuItem;
    private javax.swing.JMenuItem runDs3JMenuItem;
    private javax.swing.JMenuItem rwordleCJMenuItem;
    private javax.swing.JMenuItem rwordleLJMenuItem;
    private javax.swing.JMenuItem sairJMenuItem;
    private javax.swing.JMenuItem salvarImagemJMenuItem;
    private javax.swing.JMenuItem saveDataCoordJMenuItem;
    private javax.swing.JMenuItem showHideJMenuItem;
    private javax.swing.JMenuItem smrsJMenuItem;
    private javax.swing.JMenuItem sssJMenuItem;
    private javax.swing.JScrollPane telaJScrollPane;
    private javax.swing.JMenuItem viewSelectedJMenuItem;
    private javax.swing.JMenuItem voronoiDiagramJMenuItem;
    private javax.swing.JMenuItem vpscJMenuItem;
    // End of variables declaration//GEN-END:variables
}

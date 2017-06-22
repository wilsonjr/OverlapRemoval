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
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.test.ui;


import br.com.explore.explorertree.ExplorerTree;
import br.com.explore.explorertree.ExplorerTreeController;
import br.com.explore.explorertree.ExplorerTreeNode;
import br.com.explore.explorertree.ForceLayout;
import br.com.explore.explorertree.ForceNMAP;
import br.com.explore.explorertree.Tooltip;
import br.com.methods.overlap.expadingnode.OneLevelOverlap;
import br.com.methods.overlap.expadingnode.OverlapTree;
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
import br.com.methods.utils.ChangeRetangulo;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.Pair;
import br.com.methods.utils.RectangleVis;
import br.com.methods.utils.Util;
import br.com.projection.spacereduction.SeamCarving;
import br.com.representative.Dijsktra;
import br.com.representative.RepresentativeFinder;
import br.com.representative.clustering.FarPointsMedoidApproach;
import br.com.representative.clustering.affinitypropagation.AffinityPropagation;
import br.com.representative.clustering.furs.FURS;
import br.com.representative.clustering.hierarchical.HierarchicalClustering;
import br.com.representative.clustering.hierarchical.SingleLinkageStrategy;
import br.com.representative.clustering.partitioning.BisectingKMeans;
import br.com.representative.clustering.partitioning.Dbscan;
import br.com.representative.clustering.partitioning.KMeans;
import br.com.representative.clustering.partitioning.KMedoid;
import br.com.representative.dictionaryrepresentation.DS3;
import br.com.representative.dictionaryrepresentation.SMRS;
import br.com.representative.lowrank.CSM;
import br.com.representative.lowrank.KSvd;
import br.com.representative.metric.MST;
import br.com.test.draw.color.GrayScale;
import br.com.test.draw.color.RainbowScale;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Area;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import math.geom2d.polygon.SimplePolygon2D;
import nmap.BoundingBox;
import nmap.Element;
import nmap.NMap;

/**
 *
 * @author wilson
 */
public class Menu extends javax.swing.JFrame {
    private ViewPanel view;
    private ArrayList<RectangleVis> rectangles, afterSeamCarving;
    private double alpha = 0;
    private int globalCounter = 0;
    private int globalCounterColor = 0;
    private boolean loadedData = false;
    private ArrayList<Point> hexPoints;
    private static final int HEXBOARD_SIZE = 20;
    private Polygon p1, p2;
    private static final int RECTSIZE = 20;
    private int maior, menor;
    private List<List<List<Integer>>> clusters = null;
    private List<List<Integer>> currentCluster = null;
    private int nivelDendrogram = 0, indexRepresentatives = -1;
    private int[] selectedRepresentatives = null;
    private boolean hideShowNumbers = false;
    private double iImage = 0;
    private HierarchicalClustering hc;
    
    private ArrayList<ChangeRetangulo> cRetangulo = null;
    
    private Polygon[] diagrams = null;
    private Polygon hullPolygon = null;
    private Polygon[] intersects = null;
    
    private List<Polygon> intersectsPolygon = new ArrayList<>();
    
    private Map<Integer, List<Integer>> hashRepresentative = null;
    private List<Integer> nearest = null;
    private int[][] heatmap = null;
    private int maxValue;
    private Point2D.Double[] centerPoints = null;
    private ArrayList<Point2D.Double> items = null;
    private Point2D.Double[] points = null;
    private ExplorerTree explorerTree;
    
    private Polygon clickedPolygon = null;
    private int indexNewRepresentatives = -1;
    
    private Map<Point2D.Double, Polygon> mapPointPolygon = new HashMap<>();
    
    private ExplorerTreeController controller = null;
    
    private List<Integer> movingIndexes = new ArrayList<>();
    private List<Point2D.Double> toDraw = new ArrayList<>();
    private Tooltip tooltip = null;
    
    private Point2D.Double lastClicked = null;
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
    
    public static boolean LineIntersectsRect(Point p1, Point p2, Rectangle r)
    {
        return LineIntersectsLine(p1, p2, new Point(r.x, r.y), new Point(r.x + r.width, r.y)) ||
               LineIntersectsLine(p1, p2, new Point(r.x + r.width, r.y), new Point(r.x + r.width, r.y + r.height)) ||
               LineIntersectsLine(p1, p2, new Point(r.x + r.width, r.y + r.height), new Point(r.x, r.y + r.height)) ||
               LineIntersectsLine(p1, p2, new Point(r.x, r.y + r.height), new Point(r.x, r.y)) ||
               (r.contains(p1) && r.contains(p2));
    }

    private static boolean LineIntersectsLine(Point l1p1, Point l1p2, Point l2p1, Point l2p2)
    {
        float q = (l1p1.y - l2p1.y) * (l2p2.x - l2p1.x) - (l1p1.x - l2p1.x) * (l2p2.y - l2p1.y);
        float d = (l1p2.x - l1p1.x) * (l2p2.y - l2p1.y) - (l1p2.y - l1p1.y) * (l2p2.x - l2p1.x);

        if( d == 0 )
        {
            return false;
        }

        float r = q / d;

        q = (l1p1.y - l2p1.y) * (l1p2.x - l1p1.x) - (l1p1.x - l2p1.x) * (l1p2.y - l1p1.y);
        float s = q / d;

        if( r < 0 || r > 1 || s < 0 || s > 1 )
        {
            return false;
        }

        return true;
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
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        overlapTreeJMenuItem = new javax.swing.JMenuItem();
        expandingEdgeJMenuItem = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        sssJMenuItem = new javax.swing.JMenuItem();
        gnatJMenuItem = new javax.swing.JMenuItem();
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
        affinityPropagationJMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        ds3JMenuItem = new javax.swing.JMenuItem();
        voronoiDiagramJMenuItem = new javax.swing.JMenuItem();
        testTreeJMenuItem = new javax.swing.JMenuItem();
        fursJMenuItem = new javax.swing.JMenuItem();

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
        jMenu2.add(jSeparator4);

        overlapTreeJMenuItem.setText("OverlapTree");
        overlapTreeJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overlapTreeJMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(overlapTreeJMenuItem);

        expandingEdgeJMenuItem.setText("Expanding Edge");
        expandingEdgeJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandingEdgeJMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(expandingEdgeJMenuItem);

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

        affinityPropagationJMenuItem.setText("Affinity Propagation");
        affinityPropagationJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                affinityPropagationJMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(affinityPropagationJMenuItem);
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

        testTreeJMenuItem.setText("Test Tree");
        testTreeJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testTreeJMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(testTreeJMenuItem);

        fursJMenuItem.setText("FURS");
        fursJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fursJMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(fursJMenuItem);

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

                    rectangles.add(new RectangleVis(x, y, RECTSIZE, RECTSIZE, rbS.getColor((grupo*10)%255), id++));   
                    items.add(new Point2D.Double(x, y));
                }
                
                points = items.stream().toArray(Point2D.Double[]::new);
                centerPoints = new Point2D.Double[rectangles.size()];
                for( int i = 0; i < centerPoints.length; ++i )
                    centerPoints[i] = new Point2D.Double(rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY());               
                
            
//                int id = 0;
//                rectangles.add(new RectangleVis(30, 20, 20, 30, Color.BLUE, id++));
//                rectangles.add(new RectangleVis(40, 35, 20, 40, Color.BLUE, id++));
//                rectangles.add(new RectangleVis(55, 20, 20, 35, Color.BLUE, id++));
//                rectangles.add(new RectangleVis(55, 65, 10, 15, Color.BLUE, id++));
//                rectangles.add(new RectangleVis(20, 55, 35, 45, Color.BLUE, id++));
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
                    rectangles.add(new RectangleVis(30*(d.getCol()+xmin), 30*(d.getRow()+ymin), 
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

                    rectangles.add(new RectangleVis(distancia-(HEXBOARD_SIZE/2), centerHexY-(HEXBOARD_SIZE/2), HEXBOARD_SIZE, HEXBOARD_SIZE, 
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
//        double a = Double.parseDouble(JOptionPane.showInputDialog("Insira o valor de alpha: "));
//        
//        SSS sss = new SSS();
//        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
//        
//        sss.selectPivots(rects, a, getMaxDistance());
//        int i = 0;
//        for( OverlapRect r: rects ) {
//                r.setId(i++); 
//                r.setLevel(1);
//        }
//        Util.toRectangleVis(rectangles, rects);
//        System.out.println("OK SSS!");
//        view.cleanImage();
//        view.repaint();
    }//GEN-LAST:event_sssJMenuItemActionPerformed

    private void gnatJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gnatJMenuItemActionPerformed
//        
//        
//        GNAT gnat = new GNAT();
//        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
//        
//        int k = 5*rects.size()/100; //Integer.parseInt(JOptionPane.showInputDialog("Insira o número de pivôs: "));
//        
//        gnat.selectPivots(rects, k);
//        int i = 0;
//        for( OverlapRect r: rects ) {
//            r.setId(i++);
//            r.setLevel(1);
//        }
//        Util.toRectangleVis(rectangles, rects);
//        System.out.println("OK GNAT!");
//        view.cleanImage();
//        view.repaint();
    }//GEN-LAST:event_gnatJMenuItemActionPerformed

    private void mstJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mstJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Point.Double> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Point.Double(rects.get(i).getCenterX(), rects.get(i).getCenterY()));
        
        RepresentativeFinder mst = new MST(elems, 15);
        mst.execute();
        selectedRepresentatives = mst.getRepresentatives();
        //selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
        hashRepresentative = Util.createIndex(selectedRepresentatives, points);
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
//        MST mst = new MST();
//        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
//        
//        int i = 0;
//        for( OverlapRect r: rects ) 
//            r.setId(i++);
//        mst.selectPivots(rects, 20);
//        
//        Util.toRectangleVis(rectangles, rects);
//        System.out.println("OK MST!");
//        view.cleanImage();
//        view.repaint();
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
        view.r3 = new RectangleVis(view.r1.getUX(), view.r1.getUY(), 30, 30, Color.red, 3);
        
        double d = Math.sqrt(Math.pow(view.r2.getUX()-view.r1.getUX(), 2)
                                        +
                             Math.pow(view.r2.getUY()-view.r1.getUY(), 2));
        
        view.cleanImage();
        view.repaint();
        
        
        Timer t = new Timer(menor, new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
//        t.schedule(new TimerTask() {
//            private double i = 0.0;
//            @Override
//            public void run() {
//                System.out.println("Ola: "+i);
//                
//                double x = (1.0-i)*view.r1.getUX() + i*view.r2.getUX();
//                double y = (1.0-i)*view.r1.getUY() + i*view.r2.getUY();
//                view.r3.setUX(x);
//                view.r3.setUY(y);
//                view.cleanImage();
//                view.repaint();
//                i += 0.05;
//                if( i >= 1.0 )
//                    cancel();
//            }
//        }, 0, 100);
//        
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
        ArrayList<Point.Double> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Point.Double(rects.get(i).getCenterX(), rects.get(i).getCenterY()));
                
        RepresentativeFinder kmeans = new KMeans(elems, new FarPointsMedoidApproach(), 3);
        kmeans.execute();
        currentCluster = ((KMeans)kmeans).getClusters();        
        selectedRepresentatives = kmeans.getRepresentatives();
        selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
        hashRepresentative = Util.createIndex(selectedRepresentatives, points);
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_kMeansJMenuItemActionPerformed

    private void kMedoidJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kMedoidJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Point.Double> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Point.Double(rects.get(i).getCenterX(), rects.get(i).getCenterY()));
                
        RepresentativeFinder kmedoid = new KMedoid(elems, new FarPointsMedoidApproach(), 4);
        kmedoid.execute();
        currentCluster = ((KMedoid)kmedoid).getClusters();        
        selectedRepresentatives = kmedoid.getRepresentatives();
        selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
        hashRepresentative = Util.createIndex(selectedRepresentatives, points);
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_kMedoidJMenuItemActionPerformed

    private void bisectingKMeansJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bisectingKMeansJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Point.Double> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Point.Double(rects.get(i).getCenterX(), rects.get(i).getCenterY()));
        
        RepresentativeFinder bkmeans = new BisectingKMeans(elems, new FarPointsMedoidApproach(), 4);
        bkmeans.execute();
        currentCluster = ((BisectingKMeans)bkmeans).getClusters();
        selectedRepresentatives = bkmeans.getRepresentatives();
        selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
        hashRepresentative = Util.createIndex(selectedRepresentatives, points);
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_bisectingKMeansJMenuItemActionPerformed

    private void dbscanJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbscanJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Point.Double> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Point.Double(rects.get(i).getCenterX(), rects.get(i).getCenterY()));
        
        RepresentativeFinder dbscan = new Dbscan(elems,100, (int)(60.0/100.0)*7);
        dbscan.execute();
        currentCluster = ((Dbscan)dbscan).getClusters();
        selectedRepresentatives = dbscan.getRepresentatives();
        selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
        hashRepresentative = Util.createIndex(selectedRepresentatives, points);
        
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
                
                CSM csm = new CSM(attrs, (int) ((int) attrs.size()*0.10), attrs.size());
                csm.execute();

                selectedRepresentatives = csm.getRepresentatives();
                selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
                hashRepresentative = Util.createIndex(selectedRepresentatives, points);
                
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
                selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
                hashRepresentative = Util.createIndex(selectedRepresentatives, points);
                
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
                selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
                //hashRepresentative = Util.createIndex(selectedRepresentatives, points);
                
                if( view != null ) {
                    view.cleanImage();
                    view.repaint();            
                }
                
            } catch( FileNotFoundException e ) {

            }
        }
    }//GEN-LAST:event_smrsJMenuItemActionPerformed
    
    private void ds3JMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ds3JMenuItemActionPerformed
        
        Point p1 = new Point(0, 4);
        Point p2 = new Point(6, 4);       
        Rectangle r = new Rectangle(3, 0, 3, 3);
        
        
        boolean returned = LineIntersectsRect(p1, p2, r);
        if( returned )
            System.out.println("This line intersects the rectangle");
        else
            System.out.println("This line doesn't intersect the rectangle");
        
        
        
        double[][] distances = new double[rectangles.size()][rectangles.size()];
        for( int i = 0; i < distances.length; ++i )
            for( int j = 0; j < distances[0].length; ++j )
                distances[i][j] = Util.euclideanDistance(rectangles.get(i).x, rectangles.get(i).y, rectangles.get(j).x, rectangles.get(j).y);
        
        DS3 ds3 = new DS3(distances, 0.1);
        ds3.execute();
        selectedRepresentatives = ds3.getRepresentatives();

        view.cleanImage();
        view.repaint();  
        
    }//GEN-LAST:event_ds3JMenuItemActionPerformed

    private void runDs3JMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runDs3JMenuItemActionPerformed
        if( rectangles == null )
            loadDataJMenuItemActionPerformed(null);
        
        double[][] distances = new double[rectangles.size()][rectangles.size()];
        for( int i = 0; i < distances.length; ++i ) {
            for( int j = 0; j < distances[0].length; ++j )
                distances[i][j] = Util.euclideanDistance(rectangles.get(i).x, rectangles.get(i).y, rectangles.get(j).x, rectangles.get(j).y);
        }
        
        RepresentativeFinder ds3 = new DS3(distances, 0.1); // gives the best results 
        ds3.execute(); 
        selectedRepresentatives = ds3.getRepresentatives();
        
        
        
        int[] temp = new int[selectedRepresentatives.length];
        Map<Integer, List<Integer>> mapTemp = Util.createIndex(selectedRepresentatives, points);
        int tempIdx = 0;
        for( Map.Entry<Integer, List<Integer>> v: mapTemp.entrySet() ) {
            System.out.println(tempIdx+": Estou aqui: "+v.getKey());
            List<Integer> list = v.getValue();
            Point2D.Double p = new Point2D.Double(0,0);
            for( int i = 0; i < list.size(); ++i ) {
                p.x += points[list.get(i)].x;
                p.y += points[list.get(i)].y;
            }

            p.x /= list.size();
            p.y /= list.size();

            int idx = list.get(0);
            double dist = Double.MAX_VALUE;
            for( int i = 0; i < list.size(); ++i ) {
                double d = Util.euclideanDistance(p.x, p.y, points[list.get(i)].x, points[list.get(i)].y);
                if( d < dist ) {
                    dist = d;
                    idx = list.get(i);
                }
            }

            temp[tempIdx++] = idx;
            System.out.println("novo: "+idx);
        }
//
        selectedRepresentatives = temp;
        
        
        selectedRepresentatives = Util.distinct(selectedRepresentatives, points, RECTSIZE/2);
        hashRepresentative = Util.createIndex(selectedRepresentatives, points);
    
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
            
            List<Point2D.Double> pVoronoi = new ArrayList<>();
            diagrams = Util.voronoiDiagram(window, points, pVoronoi);            
//            intersects = Util.clipBounds(diagrams, hull, mapPointPolygon, pVoronoi);
            
            intersectsPolygon.addAll(new ArrayList<>(Arrays.asList(intersects)));
            
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_voronoiDiagramJMenuItemActionPerformed

    private void testTreeJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testTreeJMenuItemActionPerformed
        
        
        
        double[][] distances = new double[rectangles.size()][rectangles.size()];
        for( int i = 0; i < distances.length; ++i ) {
            for( int j = 0; j < distances[0].length; ++j )
                distances[i][j] = Util.euclideanDistance(rectangles.get(i).x, rectangles.get(i).y, rectangles.get(j).x, rectangles.get(j).y);
        }
        
        
        for( int i = 0; i < points.length; ++i ) {
            points[i].x = rectangles.get(i).x;
            points[i].y = rectangles.get(i).y;
        }
        
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
                
                // clustering techniques
                RepresentativeFinder kmeans = new KMeans(Arrays.asList(points), new FarPointsMedoidApproach(), (int)(points.length*0.1));
                RepresentativeFinder kmedoid = new KMedoid(Arrays.asList(points), new FarPointsMedoidApproach(), (int)(points.length*0.1));
                RepresentativeFinder bisectingKMeans = new BisectingKMeans(Arrays.asList(points), new FarPointsMedoidApproach(), (int) (points.length*0.1));
                RepresentativeFinder dbscan = new Dbscan(Arrays.asList(points), 100, (int)(60.0/100.0)*7);
                
                // singular value decomposition techniques
                RepresentativeFinder csm = new CSM(attrs, (int)(attrs.size()*0.2), attrs.size());
                RepresentativeFinder ksvd = new KSvd(attrs, (int)(attrs.size()*0.1));
                
                
                // dictionary representation
                // must test with alpha = 0.3
                RepresentativeFinder ds3 = new DS3(distances, 0.1);
                RepresentativeFinder smrs = new SMRS(attrs);
                
                controller = new ExplorerTreeController(points, 
                         rectangles.stream().map((e)->new Point2D.Double(e.getCenterX(), e.getCenterY())).toArray(Point2D.Double[]::new),
                         kmeans, 7, RECTSIZE, RECTSIZE/2);
                
                controller.build();                
                controller.updateDiagram(view.getSize().width, view.getSize().height, 0, null);
                
                view.cleanImage();
                view.repaint();
                
                
            } catch( IOException e ) {
                
            }
        }
        
       
    }//GEN-LAST:event_testTreeJMenuItemActionPerformed

    private void affinityPropagationJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_affinityPropagationJMenuItemActionPerformed
        if( rectangles == null )
            loadDataJMenuItemActionPerformed(null);
        
        RepresentativeFinder affinityPropagation = new AffinityPropagation(Arrays.asList(points));
        System.out.println("Init Affinity Propagation execution");
        affinityPropagation.execute();
        System.out.println("Finished Affinity Propagation execution");
        selectedRepresentatives = affinityPropagation.getRepresentatives();
        hashRepresentative = Util.createIndex(selectedRepresentatives, points);
    
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_affinityPropagationJMenuItemActionPerformed

    private void fursJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fursJMenuItemActionPerformed
        if( rectangles == null )
            loadDataJMenuItemActionPerformed(null);
        
        RepresentativeFinder furs = new FURS(Arrays.asList(points), (int)(0.2*points.length), 15);
        System.out.println("Init FURS");
        furs.execute();
        System.out.println("Finished FURS");
        selectedRepresentatives =  furs.getRepresentatives();
        hashRepresentative = Util.createIndex(selectedRepresentatives, points);
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_fursJMenuItemActionPerformed

    private void overlapTreeJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overlapTreeJMenuItemActionPerformed
        
        for( int i = 0; i < rectangles.size(); ++i ) {
            points[i].x = rectangles.get(i).x;
            points[i].y = rectangles.get(i).y;
        }
        
        double[][] distances = new double[rectangles.size()][rectangles.size()];
        for( int i = 0; i < distances.length; ++i ) {
            for( int j = 0; j < distances[0].length; ++j )
                distances[i][j] = Util.euclideanDistance(rectangles.get(i).x, rectangles.get(i).y, rectangles.get(j).x, rectangles.get(j).y);
        }
        
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
                
                // clustering techniques
                RepresentativeFinder kmeans = new KMeans(Arrays.asList(points), new FarPointsMedoidApproach(), 10);
                RepresentativeFinder kmedoid = new KMedoid(Arrays.asList(points), new FarPointsMedoidApproach(), (int)(points.length*0.1));
                RepresentativeFinder bisectingKMeans = new BisectingKMeans(Arrays.asList(points), new FarPointsMedoidApproach(), (int) (points.length*0.1));
                RepresentativeFinder dbscan = new Dbscan(Arrays.asList(points), 100, (int)(60.0/100.0)*7);
                
                // singular value decomposition techniques
                RepresentativeFinder csm = new CSM(attrs, (int)(attrs.size()*0.2), attrs.size());
                RepresentativeFinder ksvd = new KSvd(attrs, (int)(attrs.size()*0.1));
                
                
                // dictionary representation
                // must test with alpha = 0.3
                RepresentativeFinder ds3 = new DS3(distances, 0.1);
                RepresentativeFinder smrs = new SMRS(attrs);
                
                controller = new ExplorerTreeController(points, 
                         rectangles.stream().map((e)->new Point2D.Double(e.getCenterX(), e.getCenterY())).toArray(Point2D.Double[]::new),
                         kmeans, 10, RECTSIZE, RECTSIZE/2);
              
                OverlapTree overlapTree = new OverlapTree(controller, 1);
                ArrayList<OverlapRect> overlap = Util.toRectangle(rectangles);
                Map<OverlapRect, OverlapRect> projected = overlapTree.applyAndShowTime(overlap);
                ArrayList<OverlapRect> projectedValues = Util.getProjectedValues(projected);
                
                controller = null;
                
                Util.toRectangleVis(rectangles, projectedValues);                        
                view.cleanImage();
                view.repaint();
                
                
            } catch( IOException e ) {
                
            }
        }
        
        
    }//GEN-LAST:event_overlapTreeJMenuItemActionPerformed

    private void expandingEdgeJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandingEdgeJMenuItemActionPerformed
        ArrayList<OverlapRect> overlap = Util.toRectangle(rectangles);
        OneLevelOverlap oneLevelOverlap = new OneLevelOverlap(1);
        Map<OverlapRect, OverlapRect> projected = oneLevelOverlap.applyAndShowTime(overlap);
        ArrayList<OverlapRect> projectedValues = Util.getProjectedValues(projected);
        //overlap = removeOverlap(overlap, 1);
        Util.toRectangleVis(rectangles, projectedValues);
        
        view.cleanImage();
        view.repaint();
        
        
        Shape a = new Polygon();
        Area area = new Area();
        
        
    }//GEN-LAST:event_expandingEdgeJMenuItemActionPerformed
    
    
    public void updateDiagram() {
        
        Polygon window = new Polygon();
        int width = view.getSize().width;
        int height = view.getSize().height;
        window.addPoint(0, 0);
        window.addPoint(width, 0);
        window.addPoint(width, height);
        window.addPoint(0, height);

        System.out.println("initial index: "+indexNewRepresentatives);
        System.out.println("quantidade real: "+selectedRepresentatives.length);
        
        Point2D.Double[] points = new Point2D.Double[selectedRepresentatives.length-indexNewRepresentatives];
        System.out.println("SIZE: "+points.length);
        for( int i = 0; i < points.length; ++i ) {
            int index = selectedRepresentatives[i+indexNewRepresentatives];
            points[i] = new Point2D.Double(rectangles.get(index).getCenterX(), rectangles.get(index).getCenterY());
        }
        
        Point2D.Double[] pointsClickedPolygon = new Point2D.Double[clickedPolygon.npoints];
        for( int i = 0; i < clickedPolygon.npoints; ++i )
            pointsClickedPolygon[i] = new Point2D.Double(clickedPolygon.xpoints[i], clickedPolygon.ypoints[i]);           
        
        
        List<Point2D.Double> pVoronoi = new ArrayList<>();
        
        Polygon[] diagrams2 = Util.voronoiDiagram(window, points, pVoronoi);            
        Polygon[] intersects2 = Util.clipBounds(diagrams2, pointsClickedPolygon, null, pVoronoi, null, null);        
        
        List<Polygon> polys = new ArrayList<>(Arrays.asList(intersects2));
        
        
        System.out.println("Size: "+polys.size());
        
        for( Polygon p: polys ) {
            for( int i = 0; i < p.xpoints.length; ++i ) {
                System.out.println(p.xpoints[i]+", "+p.ypoints[i]);
            }
            System.out.println();
        }
        intersectsPolygon.addAll(polys);
        
    }
    
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
    
    public Polygon getPolygon(int x, int y) {
        Polygon polygon = null;
        for( Polygon p: intersectsPolygon ) {
            SimplePolygon2D sp = new SimplePolygon2D();
            for( int i = 0; i < p.xpoints.length; ++i )
                sp.addVertex(new math.geom2d.Point2D(p.xpoints[i], p.ypoints[i]));

            if( sp.contains(x, y) )
                polygon = p;
        }
        
        
        return polygon;
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
    
    private ArrayList<OverlapRect> addSeamCarvingResult(ArrayList<OverlapRect> projected) {
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
            returned.add(new OverlapRect(element.getValue().getMinX(), element.getValue().getMinY(), RECTSIZE, RECTSIZE, idx));
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
    
    private List<OverlapRect> removeOverlap(List<Integer> indexes) {
        int algo = 1;//Integer.parseInt(JOptionPane.showInputDialog("Deseja utilizar uma estrutura de matriz esparsa?\n0-Não\n1-Sim"));
        boolean applySeamCarving = false;//Integer.parseInt(JOptionPane.showInputDialog("Apply SeamCarving?")) == 1;
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles, indexes);
        
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
            projectedValues = addSeamCarvingResult(projectedValues);
                
        return projectedValues;
    }
    
    public double intersection(OverlapRect u, OverlapRect v) {
        return Math.max(
            Math.min(
               (u.getWidth()/2. + v.getWidth()/2.)/Math.abs(u.getCenterX() - v.getCenterX()), 
               (u.getHeight()/2. + v.getHeight()/2.)/Math.abs(u.getCenterY() - v.getCenterY())
            ), 1);
    }
    
    
    private ArrayList<OverlapRect> removeOverlap(ArrayList<OverlapRect> rect, int rep) {
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
    
    
    
    
    private void removeSubsetOverlap(List<Integer> indexes, int representative) {
        int algo = 1;//Integer.parseInt(JOptionPane.showInputDialog("Deseja utilizar uma estrutura de matriz esparsa?\n0-Não\n1-Sim"));
        boolean applySeamCarving = false;//Integer.parseInt(JOptionPane.showInputDialog("Apply SeamCarving?")) == 1;
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles, indexes);
        
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
        ArrayList<OverlapRect> projectedValues = Util.getProjectedValues(projected);
        double[] center1 = Util.getCenter(projectedValues);

        double ammountX = center0[0]-center1[0];
        double ammountY = center0[1]-center1[1];
        Util.translate(projectedValues, ammountX, ammountY);        
        Util.normalize(projectedValues);

        if( applySeamCarving )
            projectedValues = addSeamCarvingResult(projectedValues);
        
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
        OverlapPanel panel = new OverlapPanel(projected, cluster);
        
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
        OverlapPanel panel2= new OverlapPanel(projected, rectanglesforce);
        
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
        List<OverlapRect> after2 = removeOverlap(overlaps, rep);//new ForceNMAP(800, 600).repulsive(toforce, rep, 0.2*10, 10);
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
        OverlapPanel panel3 = new OverlapPanel(projected, rectanglesforce2);
        
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
    
    
    public class OverlapPanel extends JPanel implements ChangeListener {
        
        private BufferedImage imageBuffer;
        
        public ArrayList<RectangleVis> rects;
        public Map<OverlapRect, OverlapRect> projected;
        public Timer timer;
        public int INITIAL_DELAY = 0;
        public int PERIOD_INTERVAL = 100;
        public int RECT_SIZE = 30;
        
        public OverlapPanel(Map<OverlapRect, OverlapRect> projected, ArrayList<RectangleVis> rects) {
            setBackground(Color.WHITE);
            setLayout(new FlowLayout(FlowLayout.LEFT));
            this.rects = rects;
            this.projected = projected;
            
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
                            returned.add(new OverlapRect(element.getValue().getMinX(), element.getValue().getMinY(), RECTSIZE, RECTSIZE, idx));
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
    }
    
    public class ViewPanel extends JPanel {
        private Color color = Color.RED;
        
        private double iniX, iniY, fimX, fimY;
        public RectangleVis r1 = null, r2 = null, r3 = null;
        private BufferedImage imageBuffer;
        
        private boolean semaphore = false;
        private int parentMoving = -1;
        
        private Timer timer = null;
        private Timer timerTooltip = null;
        
        
        public ViewPanel() {
            setBackground(Color.WHITE);
            setLayout(new FlowLayout(FlowLayout.LEFT));
            rectangles = new ArrayList<>();
            afterSeamCarving = new ArrayList<>();
            
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
                setPreferredSize(getSize());
                this.imageBuffer = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
                
                java.awt.Graphics2D g2Buffer = this.imageBuffer.createGraphics();
                g2Buffer.setColor(this.getBackground());
                g2Buffer.fillRect(0, 0, getSize().width, getSize().height);

                g2Buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ArrayList<RectangleVis> pivots = new ArrayList<>();
                if( afterSeamCarving.isEmpty() )
                {                
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
                                if( currentCluster != null ) {
                                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
                                    g2Buffer.setColor(r.cor);
                                    g2Buffer.fillOval((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                                } else {
                                    if( controller == null ) {
                                        g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
                                        g2Buffer.setColor(Color.BLUE);
                                        g2Buffer.fillRect((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                                        g2Buffer.setColor(Color.BLACK);
                                        g2Buffer.drawRect((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                                    }
                                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
                                    g2Buffer.setColor(Color.RED);
                                    g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
                                    g2Buffer.drawString(String.valueOf(r.numero), (int)r.getUX(), (int)r.getUY()+10); 
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
                                
                for( RectangleVis r: pivots ) {
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
                
                for( Polygon p: intersectsPolygon ) {
                    g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
                    g2Buffer.setColor(Color.GREEN);
                    g2Buffer.drawPolygon(p);                    
                }
                
                if( selectedRepresentatives != null || controller !=  null && controller.representative() != null ) {

                    if( controller != null && controller.nearest() != null ) {

                     //  Util.paintSphere(centerPoints, selectedRepresentatives, hashRepresentative, g2Buffer);
                        int[] representative = controller.representative();
                        Map<Integer, List<Integer>> map = controller.nearest();
                        Point2D.Double[] projectionCenter = controller.projectionCenter();
                        Point2D.Double[] projection = controller.projection();
                                
                        for( int i = 0; i < representative.length; ++i ) {
                            
                            float alpha = (float)map.get(representative[i]).size()/(float)points.length;
                          
                            Polygon poly = controller.polygon(representative[i]);//getPolygon((int)r.x, (int)r.y);
                            if( poly != null ) {
                                g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
                                g2Buffer.setColor(Color.RED);
                                g2Buffer.fillPolygon(poly);
                                g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
                                g2Buffer.setColor(Color.RED);
                                g2Buffer.drawPolygon(poly);                                
                            }
                            
                            if( movingRepresentative(representative[i]) )
                                continue;
                            
                            int size = controller.sizeRepresentative(map.get(representative[i]).size());
                            
                            Point2D.Double p = projection[representative[i]];
                            
                            g2Buffer.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
                            g2Buffer.setColor(Color.RED);
                            g2Buffer.fillOval((int)p.x, (int)p.y, size, size);
                            g2Buffer.setColor(Color.BLACK);
                            g2Buffer.drawOval((int)p.x, (int)p.y, size, size);
                            if( hideShowNumbers ) {
                                g2Buffer.setColor(Color.BLUE);
                                g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
                                g2Buffer.drawString(String.valueOf(rectangles.get(representative[i]).numero), 
                                        (int)p.x+10, (int)p.y+10);  
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
                                    
                            if( hideShowNumbers ) {
                                g2Buffer.setColor(Color.GREEN);
                                g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 10));                    
                                g2Buffer.drawString(String.valueOf(r.numero), (int)r.getUX()+10, (int)r.getUY()+10);  
                            }
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
                    
                    
                }
                
                /*if( clickedPolygon != null ) {
                    g2Buffer.setColor(Color.BLACK);
                    for( int i = 0; i < clickedPolygon.xpoints.length; ++i )
                        g2Buffer.fillOval(clickedPolygon.xpoints[i], clickedPolygon.ypoints[i], 5, 5); 
                }*/
                
                if( tooltip != null ) {
                    tooltip.draw(g2Buffer);
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

        private boolean movingRepresentative(int i) {
            return movingIndexes.contains(i) || i == parentMoving;
        }

    }
    
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem affinityPropagationJMenuItem;
    private javax.swing.JMenuItem bisectingKMeansJMenuItem;
    private javax.swing.JMenuItem csmJMenuItem;
    private javax.swing.JMenuItem dbscanJMenuItem;
    private javax.swing.JMenuItem decrementJMenuItem;
    private javax.swing.JMenuItem dijsktraRepresentativeJMenuItem;
    private javax.swing.JMenuItem ds3JMenuItem;
    private javax.swing.JMenuItem expandingEdgeJMenuItem;
    private javax.swing.JMenuItem extractParametersJMenuItem;
    private javax.swing.JMenuItem fursJMenuItem;
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
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JMenuItem kMeansJMenuItem;
    private javax.swing.JMenuItem kMedoidJMenuItem;
    private javax.swing.JMenuItem ksvdJMenuItem;
    private javax.swing.JMenuItem limparJMenuItem;
    private javax.swing.JMenuItem loadDataJMenuItem;
    private javax.swing.JMenuItem mstJMenuItem;
    private javax.swing.JMenuItem nextDendogramJMenuItem;
    private javax.swing.JMenuItem overlapTreeJMenuItem;
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
    private javax.swing.JMenuItem testTreeJMenuItem;
    private javax.swing.JMenuItem viewSelectedJMenuItem;
    private javax.swing.JMenuItem voronoiDiagramJMenuItem;
    private javax.swing.JMenuItem vpscJMenuItem;
    // End of variables declaration//GEN-END:variables
}

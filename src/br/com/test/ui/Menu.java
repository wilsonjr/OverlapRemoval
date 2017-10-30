package br.com.test.ui;


import br.com.explore.explorertree.util.ForceLayout;
import br.com.explore.explorertree.util.Tooltip;
import br.com.explorer.explorertree.ExplorerTree;
import br.com.explorer.explorertree.ExplorerTreeController;
import br.com.explorer.explorertree.ExplorerTreeNode;
import br.com.methods.overlap.OverlapRegistry;
import br.com.methods.overlap.OverlapRemoval;
import br.com.methods.overlap.expadingnode.OneLevelOverlap;
import br.com.methods.overlap.hexboard.HexBoardExecutor;
import br.com.methods.overlap.incboard.IncBoardExecutor;
import br.com.methods.overlap.incboard.PointItem;
import br.com.methods.overlap.prism.PRISM;
import br.com.methods.overlap.projsnippet.ProjSnippet;
import br.com.methods.overlap.rwordle.RWordleC;
import br.com.methods.overlap.rwordle.RWordleL;
import br.com.methods.overlap.vpsc.VPSC;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.RectangleVis;
import br.com.methods.utils.Util;
import br.com.methods.utils.Vect;
import br.com.projection.spacereduction.SeamCarving;
import br.com.representative.RepresentativeFinder;
import br.com.representative.RepresentativeRegistry;
import br.com.representative.analysis.AnalysisController;
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
import br.com.representative.metric.GNAT;
import br.com.representative.metric.MST;
import br.com.representative.metric.SSS;
import br.com.test.draw.color.GrayScale;
import br.com.test.draw.color.RainbowScale;
import java.awt.BasicStroke;
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
import java.awt.Stroke;
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
import visualizer.matrix.DenseMatrix;
import visualizer.matrix.DenseVector;
import visualizer.projection.ProjectionData;
import visualizer.projection.ProjectionFactory;
import visualizer.projection.ProjectionType;
import visualizer.projection.ProjectorType;
import visualizer.projection.distance.DissimilarityType;
import visualizer.projection.lsp.ControlPointsType;
import visualizer.util.ChangeRetangulo;

/**
 *
 * @author wilson
 */
public class Menu extends javax.swing.JFrame {
    //private ViewPanel view;
    private ProjectionView view;
    private ArrayList<RectangleVis> rectangles, afterSeamCarving;
    private double alpha = 0;
    private int globalCounter = 0;
    private int globalCounterColor = 0;
    private boolean loadedData = false;
    private ArrayList<Point> hexPoints;
    private static final int HEXBOARD_SIZE = 20;
    private static final int RECTSIZE = 8;
    private int menor;
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
    private ArrayList<Vect> items = null;
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
        rectangles = new ArrayList<>();
        afterSeamCarving = new ArrayList<>();
        //view = new ViewPanel();
        view = new ProjectionView(rectangles, afterSeamCarving, controller, points);
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
        testProjectionJMenuItem = new javax.swing.JMenuItem();
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
        csmJMenuItem = new javax.swing.JMenuItem();
        ksvdJMenuItem = new javax.swing.JMenuItem();
        smrsJMenuItem = new javax.swing.JMenuItem();
        runDs3JMenuItem = new javax.swing.JMenuItem();
        affinityPropagationJMenuItem = new javax.swing.JMenuItem();
        fursJMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        ds3JMenuItem = new javax.swing.JMenuItem();
        voronoiDiagramJMenuItem = new javax.swing.JMenuItem();
        testTreeJMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        analysisJMenuItem = new javax.swing.JMenuItem();

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

        testProjectionJMenuItem.setText("Test Projection");
        testProjectionJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testProjectionJMenuItemActionPerformed(evt);
            }
        });
        jMenu4.add(testProjectionJMenuItem);

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

        fursJMenuItem.setText("FURS");
        fursJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fursJMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(fursJMenuItem);
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
        jMenu8.add(jSeparator5);

        analysisJMenuItem.setText("Analysis");
        analysisJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analysisJMenuItemActionPerformed(evt);
            }
        });
        jMenu8.add(analysisJMenuItem);

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
        JFileChooser jFileChooser = new JFileChooser("C:\\Users\\wilson\\Desktop\\UNESP\\datasets");
        int result = jFileChooser.showOpenDialog(this);
        if( result == JFileChooser.APPROVE_OPTION ) {
            try {                 
                items = new ArrayList<>();
                File file = jFileChooser.getSelectedFile();
                Scanner scn = new Scanner(file);
                rectangles.clear();
                //RainbowScale rbS = new RainbowScale();
                GrayScale rbS = new GrayScale();
                List<Point2D.Double> pts = new ArrayList<>();
                int id = 0;
                while( scn.hasNext() ) {
                    String[] linha = scn.nextLine().split(";");
                    double x = Double.parseDouble(linha[1]);
                    double y = Double.parseDouble(linha[2]);
                    int grupo = id;//Integer.parseInt(linha[3]);

                    rectangles.add(new RectangleVis(x, y, RECTSIZE, RECTSIZE, rbS.getColor((grupo*10)%255), id++));   
                    items.add(new Vect(new double[]{x, y}));
                    pts.add(new Point2D.Double(x, y));
                }
                
                points = pts.stream().toArray(Point2D.Double[]::new);
                view.setPoints(points);
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
        //OverlapRemoval rw = new RWordleC();
        OverlapRemoval rw = (OverlapRemoval) OverlapRegistry.getInstance(RWordleC.class);
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
        
        //OverlapRemoval rwl = new RWordleL(alpha, false);
        OverlapRemoval rwl = (OverlapRemoval) OverlapRegistry.getInstance(RWordleL.class, alpha, false);
        
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
        
    
    private void vpscJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vpscJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        double[] center0 = Util.getCenter(rects);
        //OverlapRemoval vpsc = new VPSC();
        OverlapRemoval vpsc = (OverlapRemoval) OverlapRegistry.getInstance(VPSC.class);
        
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
        //OverlapRemoval prism = new PRISM(algo);
        OverlapRemoval prism = (OverlapRemoval) OverlapRegistry.getInstance(PRISM.class, algo);
        Map<OverlapRect, OverlapRect> projected = prism.applyAndShowTime(rects);
        ArrayList<OverlapRect> projectedValues = Util.getProjectedValues(projected);
        
        double[] center1 = Util.getCenter(projectedValues);
        
        double ammountX = center0[0]-center1[0];
        double ammountY = center0[1]-center1[1];
        Util.translate(projectedValues, ammountX, ammountY);        
        Util.normalize(projectedValues);
                
        if( applySeamCarving )
            OverlapView.addSeamCarvingResult(projectedValues);
        
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
        
        OverlapRemoval ps = new ProjSnippet(Double.parseDouble(alpha_value), Integer.parseInt(k_value)+1);
        //OverlapRemoval ps = (OverlapRemoval) OverlapRegistry.getInstance(Double.parseDouble(alpha_value), Integer.parseInt(k_value)+1);
        
        Map<OverlapRect, OverlapRect> projected = ps.apply(rects);
        ArrayList<OverlapRect> projectedValues = Util.getProjectedValues(projected);
        if( projected != null ) {
            
            double[] center1 = Util.getCenter(projectedValues);

            double ammountX = center0[0]-center1[0];
            double ammountY = center0[1]-center1[1];
            Util.translate(projectedValues, ammountX, ammountY);
            Util.normalize(projectedValues);
            
            if( applySeamCarving )
                OverlapView.addSeamCarvingResult(projectedValues);
            
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

                //IncBoardExecutor executor = new IncBoardExecutor();
                IncBoardExecutor executor = (IncBoardExecutor) OverlapRegistry.getInstance(IncBoardExecutor.class);
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

                //HexBoardExecutor executor = new HexBoardExecutor();
                HexBoardExecutor executor = (HexBoardExecutor) OverlapRegistry.getInstance(HexBoardExecutor.class);
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
        double alpha = Double.parseDouble(JOptionPane.showInputDialog("alpha"));

        double maxDistance = getMaxDistance();
        if( rectangles == null )
            loadDataJMenuItemActionPerformed(null);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < points.length; ++i ) {
            elems.add(new Vect(new double[]{points[i].x, points[i].y}));
        }
        
        RepresentativeFinder sss = (RepresentativeFinder) RepresentativeRegistry.getInstance(SSS.class, items, alpha, maxDistance);
        ((SSS)sss).setAlpha(alpha);
        ((SSS)sss).setMaxDistance(maxDistance);
        sss.execute();
        selectedRepresentatives = sss.getRepresentatives();
        System.out.println("Size: "+selectedRepresentatives.length);
        hashRepresentative = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
    
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }

    }//GEN-LAST:event_sssJMenuItemActionPerformed

    private void gnatJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gnatJMenuItemActionPerformed
        
        if( rectangles == null )
            loadDataJMenuItemActionPerformed(null);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < points.length; ++i )
            elems.add(new Vect(new double[]{points[i].x, points[i].y}));
        int k = Integer.parseInt(JOptionPane.showInputDialog("k"));
        
        RepresentativeFinder gnat = (RepresentativeFinder) RepresentativeRegistry.getInstance(GNAT.class, items, k);
        ((GNAT)gnat).setK(k);
        
        gnat.execute();
        selectedRepresentatives = gnat.getRepresentatives();
        System.out.println("Size: "+selectedRepresentatives.length);
        hashRepresentative = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
    
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_gnatJMenuItemActionPerformed

    private void mstJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mstJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Vect(new double[]{rects.get(i).getCenterX(), rects.get(i).getCenterY()}));
        
        int k = Integer.parseInt(JOptionPane.showInputDialog("k"));
        
        RepresentativeFinder mst = (RepresentativeFinder) RepresentativeRegistry.getInstance(MST.class, elems, k, 8);
        ((MST)mst).setMaxNodes(k);
        mst.execute();
        selectedRepresentatives = mst.getRepresentatives();
        System.out.println("Size: "+selectedRepresentatives.length);
        hashRepresentative = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
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

    private void hierarchicalClusteringJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hierarchicalClusteringJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Vect(new double[]{rects.get(i).getCenterX(), rects.get(i).getCenterY()}));
       // hc = new HierarchicalClustering(elems, new SingleLinkageStrategy());   
        hc = (HierarchicalClustering) RepresentativeRegistry.getInstance(HierarchicalClustering.class, elems, new SingleLinkageStrategy());
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
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Vect(new double[]{rects.get(i).getCenterX(), rects.get(i).getCenterY()}));
                
        RepresentativeFinder kmeans = (RepresentativeFinder) RepresentativeRegistry.getInstance(KMeans.class, 
                elems, new FarPointsMedoidApproach(), (int)(elems.size()*(8.0/elems.size())));
        System.out.println("Init kmeans");
        kmeans.execute();
        System.out.println("Finished kmeans");
        //currentCluster = ((KMeans)kmeans).getClusters();        
        selectedRepresentatives = kmeans.getRepresentatives();
        System.out.println("Size: "+selectedRepresentatives.length);
        //selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
        hashRepresentative = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_kMeansJMenuItemActionPerformed

    private void kMedoidJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kMedoidJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Vect(new double[]{rects.get(i).getCenterX(), rects.get(i).getCenterY()}));
                
        RepresentativeFinder kmedoid = (RepresentativeFinder) RepresentativeRegistry.getInstance(KMedoid.class, 
                elems, new FarPointsMedoidApproach(), (int)(elems.size()*0.0332));
        kmedoid.execute();
        //currentCluster = ((KMedoid)kmedoid).getClusters();        
        selectedRepresentatives = kmedoid.getRepresentatives();
        System.out.println("Size: "+selectedRepresentatives.length);
        //selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
        hashRepresentative = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_kMedoidJMenuItemActionPerformed

    private void bisectingKMeansJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bisectingKMeansJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Vect(new double[]{rects.get(i).getCenterX(), rects.get(i).getCenterY()}));
        
        //RepresentativeFinder bkmeans = new BisectingKMeans(elems, new FarPointsMedoidApproach(), 4);
        RepresentativeFinder bkmeans = (RepresentativeFinder) RepresentativeRegistry.getInstance(BisectingKMeans.class, elems, 
                new FarPointsMedoidApproach(), (int)(elems.size()*0.0332));
        
        bkmeans.execute();
        //currentCluster = ((BisectingKMeans)bkmeans).getClusters();
        selectedRepresentatives = bkmeans.getRepresentatives();
        //selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
        //hashRepresentative = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_bisectingKMeansJMenuItemActionPerformed

    private void dbscanJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbscanJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Vect(new double[]{rects.get(i).getCenterX(), rects.get(i).getCenterY()}));
        
        //RepresentativeFinder dbscan = new Dbscan(elems, 100, (int)(60.0/100.0)*7);
        RepresentativeFinder dbscan = (RepresentativeFinder) RepresentativeRegistry.getInstance(Dbscan.class, elems, 100, (int)(60.0/100.0)*7);
        dbscan.execute();
        currentCluster = ((Dbscan)dbscan).getClusters();
        selectedRepresentatives = dbscan.getRepresentatives();
        selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
        hashRepresentative = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_dbscanJMenuItemActionPerformed

    private void csmJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_csmJMenuItemActionPerformed
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Vect(new double[]{rects.get(i).getCenterX(), rects.get(i).getCenterY()}));
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
                
                
                
                //RepresentativeFinder csm = new CSM(attrs, (int) (attrs.size()*0.10), attrs.size());
                RepresentativeFinder csm = (RepresentativeFinder) RepresentativeRegistry.getInstance(CSM.class, attrs, 
                        (int) (attrs.size()*0.0332), attrs.size());
                csm.execute();

                selectedRepresentatives = csm.getRepresentatives();
                System.out.println("Size: "+selectedRepresentatives.length);
               // selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
                hashRepresentative = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
                
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
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Vect(new double[]{rects.get(i).getCenterX(), rects.get(i).getCenterY()}));
        
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
                
                //RepresentativeFinder ksvd = new KSvd(attrs, (int) (attrs.size()*0.2));
                System.out.println("Executing...");
                RepresentativeFinder ksvd = (RepresentativeFinder) RepresentativeRegistry.getInstance(KSvd.class, attrs, 
                        (int) (attrs.size()*0.0332));
                ksvd.execute();
                
                selectedRepresentatives = ksvd.getRepresentatives();
                System.out.println("Size: "+selectedRepresentatives.length);
                //selectedRepresentatives = Util.distinct(selectedRepresentatives, points, (int) (rectangles.get(0).getWidth()/2));
                hashRepresentative = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
                
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
                //RepresentativeFinder smrs = new SMRS(attrs);
                RepresentativeFinder smrs = (RepresentativeFinder) RepresentativeRegistry.getInstance(SMRS.class, attrs);
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
        
        double[][] distances = new double[rectangles.size()][rectangles.size()];
        for( int i = 0; i < distances.length; ++i )
            for( int j = 0; j < distances[0].length; ++j )
                distances[i][j] = Util.euclideanDistance(rectangles.get(i).x, rectangles.get(i).y, rectangles.get(j).x, rectangles.get(j).y);
        
        DS3 ds3 = new DS3(distances, 0.1, 10, 30);
        ds3.execute();
        selectedRepresentatives = ds3.getRepresentatives();

        view.cleanImage();
        view.repaint();  
        
    }//GEN-LAST:event_ds3JMenuItemActionPerformed

    private void runDs3JMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runDs3JMenuItemActionPerformed
        if( rectangles == null )
            loadDataJMenuItemActionPerformed(null);
        
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Vect(new double[]{rects.get(i).getCenterX(), rects.get(i).getCenterY()}));
        
        double[][] distances = new double[rectangles.size()][rectangles.size()];
        for( int i = 0; i < distances.length; ++i ) {
            for( int j = 0; j < distances[0].length; ++j )
                distances[i][j] = Util.euclideanDistance(rectangles.get(i).x, rectangles.get(i).y, rectangles.get(j).x, rectangles.get(j).y);
        }
        
        //RepresentativeFinder ds3 = new DS3(distances, 0.1); // gives the best results 
        
        double alphaParameter = Double.parseDouble(JOptionPane.showInputDialog("alpha"));
        RepresentativeFinder ds3 = (RepresentativeFinder) RepresentativeRegistry.getInstance(DS3.class, distances, alphaParameter, 0, 0);
        ((DS3)ds3).setAlpha(alphaParameter);
        ds3.execute(); 
        selectedRepresentatives = ds3.getRepresentatives();
        
        
        
//        int[] temp = new int[selectedRepresentatives.length];
//        Map<Integer, List<Integer>> mapTemp = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
//        int tempIdx = 0;
//        for( Map.Entry<Integer, List<Integer>> v: mapTemp.entrySet() ) {
//            System.out.println(tempIdx+": Estou aqui: "+v.getKey());
//            List<Integer> list = v.getValue();
//            Point2D.Double p = new Point2D.Double(0,0);
//            for( int i = 0; i < list.size(); ++i ) {
//                p.x += points[list.get(i)].x;
//                p.y += points[list.get(i)].y;
//            }
//
//            p.x /= list.size();
//            p.y /= list.size();
//
//            int idx = list.get(0);
//            double dist = Double.MAX_VALUE;
//            for( int i = 0; i < list.size(); ++i ) {
//                double d = Util.euclideanDistance(p.x, p.y, points[list.get(i)].x, points[list.get(i)].y);
//                if( d < dist ) {
//                    dist = d;
//                    idx = list.get(i);
//                }
//            }
//
//            temp[tempIdx++] = idx;
//            System.out.println("novo: "+idx);
//        }
////
//        selectedRepresentatives = temp;
//        
//        
       // selectedRepresentatives = Util.distinct(selectedRepresentatives, points, RECTSIZE/2);
        hashRepresentative = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
    
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
        
        JFileChooser jFileChooser = new JFileChooser("C:\\Users\\wilson\\Desktop\\UNESP\\datasets");
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
//                RepresentativeFinder kmeans = new KMeans(Arrays.asList(points), new FarPointsMedoidApproach(), (int)(points.length*0.1));
//                RepresentativeFinder kmedoid = new KMedoid(Arrays.asList(points), new FarPointsMedoidApproach(), (int)(points.length*0.1));
//                RepresentativeFinder bisectingKMeans = new BisectingKMeans(Arrays.asList(points), new FarPointsMedoidApproach(), (int) (points.length*0.1));
//                RepresentativeFinder dbscan = new Dbscan(Arrays.asList(points), 100, (int)(60.0/100.0)*7);
                
                // singular value decomposition techniques
                RepresentativeFinder csm = new CSM(attrs, (int)(attrs.size()*0.2), attrs.size());
                RepresentativeFinder ksvd = new KSvd(attrs, (int)(attrs.size()*0.1));
                
                
                // dictionary representation
                // must test with alpha = 0.3
                //RepresentativeFinder ds3 = new DS3(distances, 0.1);
                RepresentativeFinder ds3 = (RepresentativeFinder) RepresentativeRegistry.getInstance(DS3.class, distances, 0.02, 0, 0);
                RepresentativeFinder smrs = new SMRS(attrs);
                
                controller = new ExplorerTreeController(points, 
                         rectangles.stream().map((e)->new Point2D.Double(e.getCenterX(), e.getCenterY())).toArray(Point2D.Double[]::new),
                         ds3, 4, RECTSIZE, RECTSIZE/2);
                
                controller.setCreateDiagram(true);
                controller.build();                
                controller.updateDiagram(view.getSize().width, view.getSize().height, 0, null);
                
                view.setController(controller);
                
                view.cleanImage();
                view.repaint();
                
                
            } catch( IOException e ) {
                
            }
        }
        
       
    }//GEN-LAST:event_testTreeJMenuItemActionPerformed

    private void affinityPropagationJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_affinityPropagationJMenuItemActionPerformed
        if( rectangles == null )
            loadDataJMenuItemActionPerformed(null);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < points.length; ++i )
            elems.add(new Vect(new double[]{points[i].x, points[i].y}));
        
        
        RepresentativeFinder affinityPropagation = (RepresentativeFinder) RepresentativeRegistry.getInstance(AffinityPropagation.class, 
                elems, 8);
        ///RepresentativeFinder affinityPropagation = new AffinityPropagation(elems);
        System.out.println("Init Affinity Propagation execution");
        affinityPropagation.execute();
        System.out.println("Finished Affinity Propagation execution");
        selectedRepresentatives = affinityPropagation.getRepresentatives();
        
        System.out.println("Size: "+selectedRepresentatives.length);
        hashRepresentative = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
    
        if( view != null ) {
            view.setParameters(selectedRepresentatives, hashRepresentative);
            view.cleanImage();
            view.repaint();
        }
    }//GEN-LAST:event_affinityPropagationJMenuItemActionPerformed

    private void fursJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fursJMenuItemActionPerformed
        if( rectangles == null )
            loadDataJMenuItemActionPerformed(null);
        
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Vect(new double[]{rects.get(i).getCenterX(), rects.get(i).getCenterY()}));
        
        //RepresentativeFinder furs = new FURS(elems, (int)(0.2*points.length), 15, 0.2f, 15.0f/(float)points.length);
        
        int k = Integer.parseInt(JOptionPane.showInputDialog("K"));
        
        RepresentativeFinder furs = (FURS) RepresentativeRegistry.getInstance(FURS.class, 
                                       elems, (int)((8.0/points.length)*points.length), k, 0.2f, 15.0f/(float)points.length);
        System.out.println("Init FURS");
        ((FURS)furs).setK(k);
        furs.execute();
        
        selectedRepresentatives =  furs.getRepresentatives(); 
        System.out.println("Size: "+selectedRepresentatives.length);
//        List<Vect> elements = new ArrayList<>();        
//        for( int i = 0; i < points.length; ++i ) {
//            elements.add(new Vect(new double[]{points[i].x, points[i].y}));
//        }
//        selectedRepresentatives = furs.execute(elements, 15, (int)(0.2*points.length));
        System.out.println("Finished FURS");
       
        hashRepresentative = Util.createIndex(selectedRepresentatives, elems.stream().map((v)->v).toArray(Vect[]::new));
        
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
                
                DenseMatrix matrix = new DenseMatrix();
                matrix.load(file.getAbsolutePath());
                
                List<Vect> dataset = new ArrayList<>();
                for( int i = 0; i < matrix.getRowCount(); ++i ) {
                    float[] values = matrix.getRow(i).getValues();
                    double[] dvalues = new double[values.length];
                    for( int j = 0; j < dvalues.length; ++j )
                        dvalues[j] = values[i];
                    dataset.add(new Vect(dvalues));
                }
                
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
                
                List<Vect> elements = new ArrayList<>();
                for( int i = 0; i < points.length; ++i ) {
                    elements.add(new Vect(new double[]{points[i].x, points[i].y}));
                }
                
                // clustering techniques
                RepresentativeFinder kmeans = new KMeans(elements, new FarPointsMedoidApproach(), (int)(points.length*0.1));
                RepresentativeFinder kmedoid = new KMedoid(elements, new FarPointsMedoidApproach(), (int)(points.length*0.1));
                RepresentativeFinder bisectingKMeans = new BisectingKMeans(elements, new FarPointsMedoidApproach(), (int) (points.length*0.1));
                RepresentativeFinder dbscan = new Dbscan(elements, 100, (int)(60.0/100.0)*7);
                
                // singular value decomposition techniques
                RepresentativeFinder csm = new CSM(attrs, (int)(attrs.size()*0.2), attrs.size());
                RepresentativeFinder ksvd = new KSvd(attrs, (int)(attrs.size()*0.1));
                
                
                // dictionary representation
                // must test with alpha = 0.3
                RepresentativeFinder ds3 = new DS3(distances, 0.1, (int)(0.01*points.length), (int)(0.02*points.length));
                RepresentativeFinder smrs = new SMRS(attrs);
                RepresentativeFinder furs = new FURS(elements, (int)(0.2*points.length), 15, 0.2f, 15.0f/(float)points.length);
                
//                controller = new IncrementalExplorerTreeController(dataset.stream().map((v)->v).toArray(Vect[]::new), points, 
//                         rectangles.stream().map((e)->new Point2D.Double(e.getCenterX(), e.getCenterY())).toArray(Point2D.Double[]::new),
//                         kmeans, 10, RECTSIZE, RECTSIZE/2);
//              
//                OverlapTree overlapTree = new OverlapTree(controller, 1);
//                ArrayList<OverlapRect> overlap = Util.toRectangle(rectangles);
//                Map<OverlapRect, OverlapRect> projected = overlapTree.applyAndShowTime(overlap);
//                ArrayList<OverlapRect> projectedValues = Util.getProjectedValues(projected);
//                
//                controller = null;
//                
//                Util.toRectangleVis(rectangles, projectedValues);                        
//                view.cleanImage();
//                view.repaint();
                
                
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

    
    public float[][] normalizeVertex(float begin, float end, float[][] proj) {
        
        float[][] newproj = new float[proj.length][proj[0].length];
        float maxX = proj[0][0];
        float minX = proj[0][0];
        float maxY = proj[0][1];
        float minY = proj[0][1];

        //Encontra o maior e menor valores para X e Y        
        for( int i = 0; i < proj.length; ++i ) {
            if (maxX < proj[i][0]) {
                maxX = proj[i][0];
            } else {
                if (minX > proj[i][0]) {
                    minX = proj[i][0];
                }
            }

            if (maxY < proj[i][1]) {
                maxY = proj[i][1];
            } else {
                if (minY > proj[i][1]) {
                    minY = proj[i][1];
                }
            }
        }
        
        

        ///////Fazer a largura ficar proporcional a altura
        float endX = ((maxX - minX) * end);
        if (maxY != minY) {
            endX = ((maxX - minX) * end) / (maxY - minY);
        }
        //////////////////////////////////////////////////

        //Normalizo        
        for( int i = 0; i < proj.length; ++i ) {
            if (maxX != minX) {                
                newproj[i][0] = (((proj[i][0] - minX) / (maxX - minX)) * (endX - begin)) + begin;
            } else {
                newproj[i][0] = begin;
            }

            if (maxY != minY) {
                newproj[i][1] = ((((proj[i][1] - minY) / (maxY - minY)) * (end - begin)) + begin);
            } else {
                newproj[i][1] = begin;
            }
        }
        
        return newproj;
    }
    
    
    
    private void testProjectionJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testProjectionJMenuItemActionPerformed
        JFileChooser jFileChooser = new JFileChooser();
        int result = jFileChooser.showOpenDialog(this);
        if( result == JFileChooser.APPROVE_OPTION ) {
            
            try {
                String path = jFileChooser.getSelectedFile().getAbsolutePath();

                DenseMatrix matrix = new DenseMatrix();
                matrix.load(path);

                System.out.println(matrix.getRowCount());
                
                DenseVector vec = (DenseVector) matrix.getRow(10);
                for( float v:  vec.getValues() ) {
                    System.out.println(v);
                }
                
                
                                
                ProjectionData pdata = new ProjectionData();
                pdata.setDissimilarityType(DissimilarityType.COSINE_BASED);
			pdata.setNumberNeighborsConnection(10);
			pdata.setFractionDelta(8.0f);
			pdata.setNumberIterations(50);
			pdata.setControlPointsChoice(ControlPointsType.KMEANS);
			pdata.setProjectorType(ProjectorType.FASTMAP);
			pdata.setNumberControlPoints(57);
			
                float[][] proj = ProjectionFactory.getInstance(ProjectionType.LSP).project(matrix, pdata, null);
                
                float begin = 10 * 5 + 10;
                float value = 768.5f*9.f/150f;
                float end = ((float) 768.5) / 1.65f;
                float[][] newproj = normalizeVertex(begin, end, proj);
                DenseMatrix projecao = new DenseMatrix();
                ArrayList<String> att = new ArrayList<>();
                att.add("X");
                att.add("Y");
                projecao.setAttributes(att);
                
                for( int i = 0; i < proj.length; ++i ) {
                    
                    projecao.addRow(new DenseVector(newproj[i], matrix.getRow(i).getId(), matrix.getRow(i).getKlass()));
                    
                    
                }
                projecao.save("test.prj");
            
                
                System.out.println("0k");
            
            } catch( IOException e ) {
                
            }
        }   
    }//GEN-LAST:event_testProjectionJMenuItemActionPerformed

    private double[][] createSimilarityMatrix(double[][] distances) {
        double minDistance =  Util.euclideanDistance(points[0].x, points[0].y, points[0].x, points[0].y);
        double maxDistance = -1.0;
        distances = new double[points.length][points.length];
        for( int i = 0; i < distances.length; ++i )
            for( int j = i; j < distances.length; ++j ) {
                distances[i][j] = Util.euclideanDistance(points[i].x, points[i].y, points[j].x, points[j].y);
                distances[j][i] = distances[i][j];
                maxDistance = Math.max(maxDistance, distances[i][j]);
                minDistance = Math.min(minDistance, distances[i][j]);
            }
        
        // normalize distances
        double[][] similarity = new double[distances.length][distances.length];        
        for( int i = 0; i < similarity.length; ++i )
            for( int j = 0; j < similarity.length; ++j )
                similarity[i][j] = (distances[i][j]-minDistance)/(maxDistance-minDistance);
        
        return similarity;
    }
    
    
    private void analysisJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analysisJMenuItemActionPerformed
        boolean dataset = Integer.parseInt(JOptionPane.showInputDialog("CBR-ILP-IR dataset? 1 - yes; 0 - no")) == 1;
        
        double[][] distances = new double[rectangles.size()][rectangles.size()];
        for( int i = 0; i < distances.length; ++i ) {
            for( int j = 0; j < distances[0].length; ++j )
                distances[i][j] = Util.euclideanDistance(rectangles.get(i).x, rectangles.get(i).y, rectangles.get(j).x, rectangles.get(j).y);
        }        
        
        double maxDistance = getMaxDistance();
        ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
        ArrayList<Vect> elems = new ArrayList<>();
        for( int i = 0; i < rects.size(); ++i )
            elems.add(new Vect(new double[]{rects.get(i).getCenterX(), rects.get(i).getCenterY()}));
        
        
        ArrayList<ArrayList<Double>> attrs = new ArrayList<>();
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
                
                while( scn.hasNext() ) {
                    attrs.add(new ArrayList<>());
                    String[] linhas = scn.nextLine().split(";");
                    for( int i = 1; i < linhas.length-1; ++i ) 
                        attrs.get(attrs.size()-1).add(Double.parseDouble(linhas[i]));                                            
                }
                
            } catch( FileNotFoundException e ) {

            }
        }
        
        
        double[][] similarity = createSimilarityMatrix(distances);
        
        // tests for CBR-ILP-IR dataset
        if( dataset ) {
            
            RepresentativeFinder sss = (RepresentativeFinder) RepresentativeRegistry.getInstance(SSS.class, elems, 0.23, maxDistance); // verificar se é isso msm
            RepresentativeFinder gnat = (RepresentativeFinder) RepresentativeRegistry.getInstance(GNAT.class, elems, 8);
            RepresentativeFinder kmeans = (RepresentativeFinder) RepresentativeRegistry.getInstance(KMeans.class, elems, new FarPointsMedoidApproach(), (int)(elems.size()*(8.0/elems.size())));
            RepresentativeFinder kmedoid = (RepresentativeFinder) RepresentativeRegistry.getInstance(KMedoid.class, elems, new FarPointsMedoidApproach(), (int)(elems.size()*(8.0/elems.size())));
            RepresentativeFinder bkmeans = (RepresentativeFinder) RepresentativeRegistry.getInstance(BisectingKMeans.class, elems, new FarPointsMedoidApproach(), (int)(elems.size()*(8.0/elems.size())));
            RepresentativeFinder csm = (RepresentativeFinder) RepresentativeRegistry.getInstance(CSM.class, attrs, (int)(attrs.size()*(8.0/elems.size())), attrs.size());
            RepresentativeFinder ksvd = (RepresentativeFinder) RepresentativeRegistry.getInstance(KSvd.class, attrs, (int)(attrs.size()*(8.0/elems.size())));
            RepresentativeFinder ds3 = (RepresentativeFinder) RepresentativeRegistry.getInstance(DS3.class, distances, 0.09, 8, 8);
            RepresentativeFinder ap = (RepresentativeFinder) RepresentativeRegistry.getInstance(AffinityPropagation.class, elems, 8, 8);
            RepresentativeFinder furs = (RepresentativeFinder) RepresentativeRegistry.getInstance(FURS.class, elems, (int)(elems.size()*(8.0/elems.size())), 3, 0.2f, 15.0f/(float)points.length);


            List<RepresentativeFinder> techniques = Arrays.asList(sss, gnat, kmeans, kmedoid, bkmeans, csm, ksvd, ds3, ap, furs);
                
            
            techniques.forEach((v) -> {

                    long startTime = System.currentTimeMillis();
                    v.execute();
                    long endTime = System.currentTimeMillis();
                    int[] indexes = v.getRepresentatives();

                    System.out.println("Technique: "+v.toString());
                    AnalysisController.execute(indexes, similarity, points); 
                    System.out.println("Execution Time: "+ ((endTime-startTime)/1000.0));
                    System.out.println("Number of representatives: "+indexes.length);
                    for( int i = 0; i < indexes.length; ++i )
                        System.out.print(indexes[i]+" ");
                    
                    System.out.println("\n-------");

                    System.out.println("\n");

            });
            
        } else { // tests for ImageCorel dataset
            System.out.println("ImageCorel dataset");
            
            RepresentativeFinder sss = (RepresentativeFinder) RepresentativeRegistry.getInstance(SSS.class, elems, 0.21, maxDistance); // verificar se é isso msm
            RepresentativeFinder gnat = (RepresentativeFinder) RepresentativeRegistry.getInstance(GNAT.class, elems, 10);
            RepresentativeFinder kmeans = (RepresentativeFinder) RepresentativeRegistry.getInstance(KMeans.class, elems, new FarPointsMedoidApproach(), (int)(elems.size()*(10.0/elems.size())));
            RepresentativeFinder kmedoid = (RepresentativeFinder) RepresentativeRegistry.getInstance(KMedoid.class, elems, new FarPointsMedoidApproach(), (int)(elems.size()*(10.0/elems.size())));
            RepresentativeFinder bkmeans = (RepresentativeFinder) RepresentativeRegistry.getInstance(BisectingKMeans.class, elems, new FarPointsMedoidApproach(), (int)(elems.size()*(10.0/elems.size())));
            RepresentativeFinder csm = (RepresentativeFinder) RepresentativeRegistry.getInstance(CSM.class, attrs, (int)(attrs.size()*(10.0/elems.size())), attrs.size());
            RepresentativeFinder ksvd = (RepresentativeFinder) RepresentativeRegistry.getInstance(KSvd.class, attrs, (int)(attrs.size()*(10.0/elems.size())));
            RepresentativeFinder ds3 = (RepresentativeFinder) RepresentativeRegistry.getInstance(DS3.class, distances, 0.02, 10, 10);
            RepresentativeFinder ap = (RepresentativeFinder) RepresentativeRegistry.getInstance(AffinityPropagation.class, elems, 10, 10);
            RepresentativeFinder furs = (RepresentativeFinder) RepresentativeRegistry.getInstance(FURS.class, elems, (int)(elems.size()*(10.0/elems.size())), 3, 0.2f, 15.0f/(float)points.length);


            List<RepresentativeFinder> techniques = Arrays.asList(sss, gnat, kmeans, kmedoid, bkmeans, csm, ksvd, ds3, ap, furs);

            techniques.forEach((v) -> {

                    long startTime = System.currentTimeMillis();
                    v.execute();
                    long endTime = System.currentTimeMillis();
                    int[] indexes = v.getRepresentatives();
                    
                    System.out.println("Technique: "+v.toString());
                    AnalysisController.execute(indexes, similarity, points); 
                    System.out.println("Execution Time: "+ ((endTime-startTime)/1000.0));
                    System.out.println("Number of representatives: "+indexes.length);
                    for( int i = 0; i < indexes.length; ++i )
                        System.out.print(indexes[i]+" ");
                    
                    System.out.println("\n-------");

                    System.out.println("\n");

            });
            
            
        }
    }//GEN-LAST:event_analysisJMenuItemActionPerformed
    
//    
//    public void updateDiagram() {
//        
//        Polygon window = new Polygon();
//        int width = view.getSize().width;
//        int height = view.getSize().height;
//        window.addPoint(0, 0);
//        window.addPoint(width, 0);
//        window.addPoint(width, height);
//        window.addPoint(0, height);
//
//        System.out.println("initial index: "+indexNewRepresentatives);
//        System.out.println("quantidade real: "+selectedRepresentatives.length);
//        
//        Point2D.Double[] points = new Point2D.Double[selectedRepresentatives.length-indexNewRepresentatives];
//        System.out.println("SIZE: "+points.length);
//        for( int i = 0; i < points.length; ++i ) {
//            int index = selectedRepresentatives[i+indexNewRepresentatives];
//            points[i] = new Point2D.Double(rectangles.get(index).getCenterX(), rectangles.get(index).getCenterY());
//        }
//        
//        Point2D.Double[] pointsClickedPolygon = new Point2D.Double[getClickedPolygon.npoints];
//        for( int i = 0; i < getClickedPolygon.npoints; ++i )
//            pointsClickedPolygon[i] = new Point2D.Double(getClickedPolygon.xpoints[i], getClickedPolygon.ypoints[i]);           
//        
//        
//        List<Point2D.Double> pVoronoi = new ArrayList<>();
//        
//        Polygon[] diagrams2 = Util.voronoiDiagram(window, points, pVoronoi);            
//        Polygon[] intersects2 = Util.clipBounds(diagrams2, pointsClickedPolygon, null, pVoronoi, null, null);        
//        
//        List<Polygon> polys = new ArrayList<>(Arrays.asList(intersects2));
//        
//        
//        System.out.println("Size: "+polys.size());
//        
//        for( Polygon p: polys ) {
//            for( int i = 0; i < p.xpoints.length; ++i ) {
//                System.out.println(p.xpoints[i]+", "+p.ypoints[i]);
//            }
//            System.out.println();
//        }
//        intersectsPolygon.addAll(polys);
//        
//    }
    
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
        java.awt.EventQueue.invokeLater(() -> {
            new Menu().setVisible(true);
        });
    }
    
    private void embaralha() {
        globalCounterColor = globalCounter = 0;
        
        rectangles.clear();
       
        if( view != null ) {
            view.cleanImage();
            view.repaint();            
        }
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem affinityPropagationJMenuItem;
    private javax.swing.JMenuItem analysisJMenuItem;
    private javax.swing.JMenuItem bisectingKMeansJMenuItem;
    private javax.swing.JMenuItem csmJMenuItem;
    private javax.swing.JMenuItem dbscanJMenuItem;
    private javax.swing.JMenuItem decrementJMenuItem;
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
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
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
    private javax.swing.JMenuItem testProjectionJMenuItem;
    private javax.swing.JMenuItem testTreeJMenuItem;
    private javax.swing.JMenuItem viewSelectedJMenuItem;
    private javax.swing.JMenuItem voronoiDiagramJMenuItem;
    private javax.swing.JMenuItem vpscJMenuItem;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.test.ui;

import br.com.methods.overlap.OverlapRegistry;
import br.com.methods.overlap.OverlapRemoval;
import br.com.methods.overlap.pfsp.Graph;
import br.com.methods.overlap.pfsp.Node;
import br.com.methods.overlap.pfsp.PFSPrime;
import br.com.methods.overlap.prism.PRISM;
import br.com.methods.overlap.projsnippet.ProjSnippet;
import br.com.methods.overlap.rwordle.RWordleC;
import br.com.methods.overlap.rwordle.RWordleL;
import br.com.methods.overlap.vpsc.VPSC;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.RectangleVis;
import br.com.methods.utils.Util;
import br.com.test.draw.color.RainbowScale;
import de.visone.visualization.layout.overlapRemoval.NodeOverlapRemovalByShape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author wilson
 */
public class RunTestsReArrange {
    private final static int RECTSIZE = 10;
    private static int BEGIN_NORM = 0;
    private static int END_NORM = 1000;
    private static List<Integer> labels, ids;
    
    public static ArrayList<RectangleVis> load_dataset(String path, int width, int height) {
        
        
        try {                 
            ArrayList<RectangleVis> rectangles = new ArrayList<>();
            List<Point2D.Double> pts = new ArrayList<>();
            Scanner scn = new Scanner(new File(path));
            rectangles.clear();
            RainbowScale rbS = new RainbowScale();
            labels = new ArrayList<>();
             ids = new ArrayList<>();
            
//            int id = 0;
//            while( scn.hasNext() ) {
//                String temp = scn.nextLine();
//                if( temp.equals("DY") ) {
//                    scn.nextLine();
//                    scn.nextLine();
//                    scn.nextLine();
//                    
//                    temp = scn.nextLine();
//                }
//                
//                String[] linha = temp.split(";");
//                double x = Double.parseDouble(linha[1]);
//                double y = Double.parseDouble(linha[2]);
//                int grupo = (int) Double.parseDouble(linha[3]);
//                
//
//                rectangles.add(new RectangleVis(x, y, RECTSIZE, RECTSIZE, rbS.getColor((grupo*10)%255), id++)); 
//                labels.add(grupo);
//            };
            
            
            System.out.println(scn.nextLine());
             while( scn.hasNext() ) {
                String temp = scn.nextLine();
                String[] linha = temp.split(",");
                int id = (int) Integer.parseInt(linha[0]);
                double x = Double.parseDouble(linha[1]);
                double y = Double.parseDouble(linha[2]);
                int grupo = (int) Integer.parseInt(linha[5]);
                
                rectangles.add(new RectangleVis(x, y, RECTSIZE, RECTSIZE, rbS.getColor((grupo*10)%255), id)); 
                labels.add(grupo);
                ids.add(id);
            }
            

//            float[][] proj = new float[rectangles.size()][2];
//            for( int i = 0; i < proj.length; ++i ) {
//                proj[i][0] = (float) rectangles.get(i).x;
//                proj[i][1] = (float) rectangles.get(i).y;
//            };
//            float[][] updated_proj = normalizeVertex(BEGIN_NORM, END_NORM, proj, width, height);
//
//
//            for( int i = 0; i < updated_proj.length; ++i ) {
//
//                rectangles.get(i).x = updated_proj[i][0];
//                rectangles.get(i).y = updated_proj[i][1];
//
//                float x = updated_proj[i][0];
//                float y = updated_proj[i][1];
//
//
////                pts.add(new Point2D.Double(x, y));
//            }

            return rectangles;

        } catch( Exception e ) {
            System.out.println(e);
            return null;
        }
        
            
    }
    
    
    public static void main(String[] args) {
        
        
        
        String[] technique_name = new String[]{"ReArrange"};

//        String[] technique_name = new String[]{"ProjSnippet"};
//        OverlapRemoval[] technique = new OverlapRemoval[]{
//            (OverlapRemoval) new ProjSnippet(0.0, 10)
//        };
        
        
//        String path1 = "/home/wilson/Área de Trabalho/OverlapRemoval/original-final/"; 
        String path1 = "original-large/";
        File[] files = new File(path1).listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null. 
        
        List<String> datasets = new ArrayList<>(); 
        for (File file : files) 
            if (file.isFile()) {
                if( file.getName().split("\\.")[1].equals("csv") ) {
                    datasets.add(file.getName());
//                System.out.println(">> "+file.getName());
                }
                
            }

        
        FileWriter fw_metrics = null;
        try
        {
            fw_metrics = new FileWriter("results-large/result_metrics-rearrange.csv", false); 
            fw_metrics.write("Dataset,Technique,Metric,Value\n");           
            
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        } 
        finally
        {
            try {
                if (fw_metrics != null) {
                    fw_metrics.close();
                }
            } catch( IOException e) {
                System.out.println(e);
                return;
            }
        }
        
        
        // for all datasets...
        for( int index = 0; index < datasets.size(); ++index ) {           
            
            System.out.println("=======================================================");
            System.out.printf("====================DATASET %d/%d======================\n", (index+1), datasets.size());
            System.out.println("=======================================================");
            
            
            
            
            System.out.println("Loading dataset: "+(path1+"/"+datasets.get(index)));
            
            

            ArrayList<RectangleVis> rectangles = load_dataset(path1+"/"+datasets.get(index), 0, 0);
            ArrayList<RectangleVis> temp_rect = load_dataset(path1+"/"+datasets.get(index), 0, 0);
            /******
             * PRE-PROCESSING STEPS
             */
            // choose where to center the coordinates after overlap removal
            // here, I am using the origin
            double[] center_middle = {0, 0};

            // save the initial positions for metric computation
            ArrayList<OverlapRect> initial_positions = Util.toRectangle(rectangles);
//            double[] center0 = Util.getCenter(initial_positions);
//            Util.translate(initial_positions, center_middle[0]-center0[0], center_middle[1]-center0[1]);
//
            double xmin = Collections.min(initial_positions, Comparator.comparing(s -> s.x)).x;
            double xmax = Collections.max(initial_positions, Comparator.comparing(s -> s.x)).x;

            double ymin = Collections.min(initial_positions, Comparator.comparing(s -> s.y)).y;
            double ymax = Collections.max(initial_positions, Comparator.comparing(s -> s.y)).y;

            // convert the coordinates to use the overlap removal techniques
            ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
            
//            Util.translate(rects, center_middle[0]-center0[0], center_middle[1]-center0[1]);
            
            List<Node> nodes = new ArrayList<>();
            rects.forEach(or -> {
                nodes.add(new Node(or.getCenterX(), or.getCenterY(), or.width, or.height, or.getId()));
            });
            
            Graph graph = new Graph(nodes);
            
           

            for( int index_technique = 0; index_technique < technique_name.length; ++index_technique ) {
                                
                System.out.println("Applying overlap removal using: "+technique_name[index_technique]);
                
                
                /******
                * APPLYING OVERLAP REMOVAL 
                */
                // Choose a overlap removal technique
                


                // apply the algorithm
                
                
               
                
                
                
                Rectangle2D.Double[] aux_rect = new Rectangle2D.Double[rects.size()];
                for( int i = 0; i < rects.size(); ++i )
                    aux_rect[i] = new Rectangle2D.Double(rects.get(i).getCenterX(), rects.get(i).getCenterY(), rects.get(i).width, rects.get(i).height);
                
                long startTime = System.currentTimeMillis();
                Point2D.Double[] points_without_overlap = NodeOverlapRemovalByShape.RemoveOverlap(aux_rect, 0);
                long endTime = System.currentTimeMillis();
                
                double secs = ((endTime-startTime)/1000.0);
                for( int i = 0; i < rects.size(); ++i ) {
                    rects.get(i).setUX(points_without_overlap[i].x - rects.get(i).width/2);
                    rects.get(i).setUY(points_without_overlap[i].y - rects.get(i).height/2);
                }
            
                ArrayList<OverlapRect> projectedValues = rects;
                
                
                projectedValues.sort((OverlapRect a, OverlapRect b) -> Integer.compare(a.getId(), b.getId()));
                
//                List<OverlapRect> sortedProjectedValues = ArrayList<>();
                OverlapRect[] sortedProjectedValues = new OverlapRect[projectedValues.size()];
                for( int i = 0; i < projectedValues.size(); ++i ) {
                    sortedProjectedValues[i] = projectedValues.get(ids.get(i));
                }
                
                

                Util.toRectangleVis2(temp_rect, new ArrayList<>(Arrays.asList(sortedProjectedValues)));
                
               
                FileWriter points = null;
                try {
                    
                    points = new FileWriter("results-large/"+technique_name[index_technique]+"/"+datasets.get(index)+"_"+technique_name[index_technique]+".csv", false);
                    points.write("id,ux,uy,width,height,label\n");
                    
                    for( int k = 0; k < projectedValues.size(); ++k ) {
                        points.write(temp_rect.get(k).getId()+","+temp_rect.get(k).getUX()+","+temp_rect.get(k).getUY()+","+
                                     RECTSIZE+","+RECTSIZE+","+labels.get(k)+"\n");
                    }
                } catch( IOException ioe ) {
                    System.out.println("IOException: "+ioe.getMessage());
                    
                } finally {
                    try {
                        
                        if( points != null  )
                            points.close();
                        
                    } catch( IOException e ) {
                       System.out.println(e);
                       return;
                    }
                }
                
                try
                {
                    fw_metrics = new FileWriter("results-large/result_metrics-rearrange.csv", true); 
                    fw_metrics.write(datasets.get(index)+","+technique_name[index_technique]+",Time (s),"+secs+"\n");   

                  

                }
                catch(IOException ioe)
                {
                    System.err.println("IOException: " + ioe.getMessage());
                } 
                finally
                {
                    try {
                        if (fw_metrics != null) {
                            fw_metrics.close();
                        }
                    } catch( IOException e) {
                        System.out.println(e);
                        return;
                    }
                }


            }

            

            
            
        }

        
        
        
    }

    public static float[][] normalizeVertex(float begin, float end, float[][] proj, int width, int height) {
        
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
//        float endX = ((maxX - minX) * end);
//        if (maxY != minY) {
//            endX = ((maxX - minX) * end) / (maxY - minY);
//        }

        float endX = width;
        float endY = height;
        //////////////////////////////////////////////////

        //Normalizo        
        for( int i = 0; i < proj.length; ++i ) {
            if (maxX != minX) {                
                newproj[i][0] = (((proj[i][0] - minX) / (maxX - minX)) * (endX - begin)) + begin;
            } else {
                newproj[i][0] = begin;
            }

            if (maxY != minY) {
                newproj[i][1] = ((((proj[i][1] - minY) / (maxY - minY)) * (endY - begin)) + begin);
            } else {
                newproj[i][1] = begin;
            }
        }
        
        return newproj;
    }

  
    
}

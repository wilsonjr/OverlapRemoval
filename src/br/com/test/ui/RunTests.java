/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.test.ui;

import br.com.methods.overlap.OverlapRegistry;
import br.com.methods.overlap.OverlapRemoval;
import br.com.methods.overlap.prism.PRISM;
import br.com.methods.overlap.projsnippet.ProjSnippet;
import br.com.methods.overlap.rwordle.RWordleC;
import br.com.methods.overlap.rwordle.RWordleL;
import br.com.methods.utils.Intersector;
import br.com.methods.utils.OverlapRect;
import br.com.methods.utils.RectangleVis;
import br.com.methods.utils.Util;
import br.com.methods.utils.Vect;
import br.com.overlayanalisys.layoutsimilarity.LayoutSimilarity;
import br.com.overlayanalisys.neighorhoodpreservation.NeighborhoodPreservation;
import br.com.test.draw.color.GrayScale;
import br.com.test.draw.color.RainbowScale;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wilson
 */
public class RunTests {
    private final static int RECTSIZE = 90;
    private final static int BEGIN_NORM = 50;
    private final static int END_NORM = 550;
    private static List<Integer> labels;
    
    public static ArrayList<RectangleVis> load_dataset(String path) {
        
        
        try {                 
            ArrayList<RectangleVis> rectangles = new ArrayList<>();
            List<Point2D.Double> pts = new ArrayList<>();
            Scanner scn = new Scanner(new File(path));
            rectangles.clear();
            RainbowScale rbS = new RainbowScale();
            labels = new ArrayList<>();
            
            int id = 0;
            while( scn.hasNext() ) {
                String temp = scn.nextLine();
                if( temp.equals("DY") ) {
                    scn.nextLine();
                    scn.nextLine();
                    scn.nextLine();
                    
                    temp = scn.nextLine();
                }
                
                String[] linha = temp.split(";");
                double x = Double.parseDouble(linha[1]);
                double y = Double.parseDouble(linha[2]);
                int grupo = (int) Double.parseDouble(linha[3]);
                

                rectangles.add(new RectangleVis(x, y, RECTSIZE, RECTSIZE, rbS.getColor((grupo*10)%255), id++)); 
                labels.add(grupo);
            }

            float[][] proj = new float[rectangles.size()][2];
            for( int i = 0; i < proj.length; ++i ) {
                proj[i][0] = (float) rectangles.get(i).x;
                proj[i][1] = (float) rectangles.get(i).y;
            }
            float[][] updated_proj = normalizeVertex(BEGIN_NORM, END_NORM, proj);


            for( int i = 0; i < updated_proj.length; ++i ) {

                rectangles.get(i).x = updated_proj[i][0];
                rectangles.get(i).y = updated_proj[i][1];

                float x = updated_proj[i][0];
                float y = updated_proj[i][1];


                pts.add(new Point2D.Double(x, y));
            }

            return rectangles;

        } catch( Exception e ) {
            System.out.println(e);
            return null;
        }
        
            
    }
    
    
    public static void main(String[] args) {
        
        List<String> results = new ArrayList<String>(); 
        
//        /****
//         * PC WILSON
//         */
        String[] technique_name = new String[]{"RWordle-L", "ProjSnippet"};
        OverlapRemoval[] technique = new OverlapRemoval[]{
            (OverlapRemoval) OverlapRegistry.getInstance(RWordleL.class, 0, false),
            new ProjSnippet(0.5, 10)
        };
        
        
//        /****
//         * PC DANILO
//         */
//        String[] technique_name = new String[]{"PRISM"};
//        OverlapRemoval[] technique = new OverlapRemoval[]{
//            (OverlapRemoval) OverlapRegistry.getInstance(PRISM.class, 1)
//        };
        
        
        /****
         * PC GLADYS
         */
//        String[] technique_name = new String[]{"RWordle-C"};
//        OverlapRemoval[] technique = new OverlapRemoval[]{
//            (OverlapRemoval) OverlapRegistry.getInstance(RWordleC.class)
//        };

        String path1 = "/home/wilson/Área de Trabalho/OverlapRemoval/datasets_dgrid/"; 
        File[] files = new File(path1).listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null. 

        for (File file : files) 
            if (file.isFile()) {
                if( file.getName().split("\\.")[1].equals("data") ) {
                    results.add(file.getName());
//                System.out.println(">> "+file.getName());
                }
                
            }
        FileWriter fw_metrics = null, fw_np = null, fw_boundingbox = null;
        try
        {
            fw_metrics = new FileWriter("result_metrics.csv", false); 
            fw_metrics.write("Dataset,Technique,Metric,Value\n");
            
            fw_np = new FileWriter("result_np_knn.csv", false);
            fw_np.write("Dataset,Technique,Neighbors,Value\n");
            
            fw_boundingbox = new FileWriter("result_boundingbox.csv", false);
            fw_boundingbox.write("Dataset,ux,uy,lx,ly\n");
            
            
            
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
                if (fw_np != null) {
                    fw_np.close();
                }
                if (fw_boundingbox != null) {
                    fw_boundingbox.close();
                }
            } catch( IOException e) {
                System.out.println(e);
                return;
            }
        }
        
        
        
        
        
        // for all datasets...
        for( int index = 0; index < 1; ++index ) {                
            
            System.out.println("Loading dataset: "+(path1+"/"+results.get(index)));
            
            ArrayList<RectangleVis> rectangles = load_dataset(path1+"/"+results.get(index));
            ArrayList<RectangleVis> temp_rect = load_dataset(path1+"/"+results.get(index));
            /******
             * PRE-PROCESSING STEPS
             */
            // choose where to center the coordinates after overlap removal
            // here, I am using the origin
            double[] center_middle = {0, 0};


            // save the initial positions for metric computation
            ArrayList<OverlapRect> initial_positions = Util.toRectangle(rectangles);
            double[] center0 = Util.getCenter(initial_positions);
            Util.translate(initial_positions, center_middle[0]-center0[0], center_middle[1]-center0[1]);

            double xmin = Collections.min(initial_positions, Comparator.comparing(s -> s.x)).x;
            double xmax = Collections.max(initial_positions, Comparator.comparing(s -> s.x)).x;

            double ymin = Collections.min(initial_positions, Comparator.comparing(s -> s.y)).y;
            double ymax = Collections.max(initial_positions, Comparator.comparing(s -> s.y)).y;

            // convert the coordinates to use the overlap removal techniques
            ArrayList<OverlapRect> rects = Util.toRectangle(rectangles);
            
            
            
            /******
             * APPLYING THE BOUNDING BOX
             */        
            // define width and height of bounding b.
            // Here, I am using the one its greater: projection's bounding box or 300x300 pixels
            double width = Math.max(xmax-xmin, rectangles.size()*RECTSIZE);
            double height = Math.max(ymax-ymin, rectangles.size()*RECTSIZE);

            // define upper x, upper y, lower x, lower y coordinates (visual space)
            double ux = center_middle[0] - width/2 - initial_positions.get(0).width;
            double uy = center_middle[1] - height/2 - initial_positions.get(0).height;
            double lx = center_middle[0] + width/2 + initial_positions.get(0).width;
            double ly = center_middle[1] + height/2 + initial_positions.get(0).height;
            
            
            try
            {
                fw_boundingbox = new FileWriter("result_boundingbox.csv", true);
                fw_boundingbox.write(results.get(index)+","+ux+","+uy+","+lx+","+ly+"\n");
            }
            catch(IOException ioe)
            {
                System.err.println("IOException: " + ioe.getMessage());
            } 
            finally
            {
                try {
                    if (fw_boundingbox != null) {
                        fw_boundingbox.close();
                    }
                } catch( IOException e) {
                    System.out.println(e);
                    return;
                }
            }


            for( int index_technique = 0; index_technique < technique_name.length; ++index_technique ) {
                
                System.out.println("Applying overlap removal using: "+technique_name[index_technique]);
                
                
                /******
                * APPLYING OVERLAP REMOVAL 
                */
                // Choose a overlap removal technique
                OverlapRemoval or = technique[index_technique];


                // apply the algorithm

                long startTime = System.currentTimeMillis();
                Map<OverlapRect, OverlapRect> projected = or.apply(rects);
                long endTime = System.currentTimeMillis();
                double secs = ((endTime-startTime)/1000.0);
                System.out.printf("Computed in %.4f seconds.\n", secs);
                // get the projected values
                ArrayList<OverlapRect> projectedValues = Util.getProjectedValues(projected);

                // get the center
                double[] center1 = Util.getCenter(projectedValues);

                // translate the projection to the origin (we define it in the pre-processing steps)
                double ammountX = center_middle[0]-center1[0];
                double ammountY = center_middle[1]-center1[1];
                Util.translate(projectedValues, ammountX, ammountY);







                // check if the new coordinate (after overlap removal) lie outside the bouding box
                // if so, set the position on the borders

                System.out.println("Imposing bounding box...");

                for( int i = 0; i < projectedValues.size(); ++i ) {
                    // create a Intersector 
                    Intersector boundingb = new Intersector(ux, uy, lx, ly,  initial_positions.get(i).x, initial_positions.get(i).y);
                    if( !boundingb.isInside(projectedValues.get(i).x, projectedValues.get(i).y) ) {
                        double[] new_coords = boundingb.mouseUp(projectedValues.get(i).x, projectedValues.get(i).y);

                        // necessary for the case when initial and final positions are outside the bounding box
                        if( new_coords == null ) {
                            Intersector bb = new Intersector(ux, uy, lx, ly,  center_middle[0], center_middle[1]);
                            new_coords = bb.mouseUp(projectedValues.get(i).x, projectedValues.get(i).y);
                        }

                        projectedValues.get(i).setUX(new_coords[0]);
                        projectedValues.get(i).setUY(new_coords[1]);
                    } 
                }

                System.out.println("Computing Layout Similarity...");               
                double ls = new LayoutSimilarity().execute(initial_positions, projectedValues);

                System.out.println("Computing Neighborhood Preservation up to 20 neighbors...");
                NeighborhoodPreservation np = new NeighborhoodPreservation(20);

                double mean_np = np.execute(initial_positions, projectedValues);
                List<Double> np_values = np.getNP();

                System.out.println("Saving results...");

                System.out.println();
                
                Util.toRectangleVis(temp_rect, projectedValues);
               
               
                try
                {
                    fw_metrics = new FileWriter("result_metrics.csv", true); 
                    fw_metrics.write(results.get(index)+","+technique_name[index_technique]+",Layout Similarity,"+ls+"\n");   
                    fw_metrics.write(results.get(index)+","+technique_name[index_technique]+",Mean NP,"+mean_np+"\n");   
                    fw_metrics.write(results.get(index)+","+technique_name[index_technique]+",Time (s),"+secs+"\n");   

                    fw_np = new FileWriter("result_np_knn.csv", true);
                    
                    for( int i = 0; i < np_values.size(); ++i ) {
                        fw_np.write(results.get(index)+","+technique_name[index_technique]+","+(i+1)+","+np_values.get(i)+"\n");
                    }

                  

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
                        if (fw_np != null) {
                            fw_np.close();
                        }
                    } catch( IOException e) {
                        System.out.println(e);
                        return;
                    }
                }
                FileWriter points = null;
                try {
                    
                    points = new FileWriter(results.get(index)+"_points_"+technique_name[index_technique]+".csv", false);
                    points.write("x,y,width,height,label\n");
                    
                    for( int k = 0; k < temp_rect.size(); ++k )
                        points.write(temp_rect.get(k).getUX()+","+temp_rect.get(k).getUY()+","+temp_rect.get(k).getWidth()+","+temp_rect.get(k).getHeight()+","+labels.get(k)+"\n");
                    
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


            }

            

            
            
        }

        
        
        
        
        // remove negative coordinates
//        Util.normalize(projectedValues);
        
        
        
    }

    public static float[][] normalizeVertex(float begin, float end, float[][] proj) {
        
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

  
    
}

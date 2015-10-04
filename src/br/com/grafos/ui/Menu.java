/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.grafos.ui;


import br.com.grafos.desenho.color.RainbowScale;
import br.com.metodos.overlap.IDShape;
import br.com.metodos.utils.Retangulo;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author wilson
 */
public class Menu extends javax.swing.JFrame {
    private ViewPanel view;
    private ArrayList<Retangulo> rectangles;
    private ArrayList<Retangulo> novos;
    private ArrayList<Rectangle> espiral = new ArrayList<>();
    private double alpha = 0;
    private int globalCounter = 0;
    private int globalCounterColor = 0;
        
    /**
     * Creates new form Menu
     */
    public Menu() {
        
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
        recentralizarJCheckBox = new javax.swing.JCheckBox();
        rwordleCJButton = new javax.swing.JButton();
        rwordleLJButton = new javax.swing.JButton();
        anguloJTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        embaralhaJButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        rwordlelJMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        recentralizarJCheckBox.setText("Recentralizar a cada iteração (RWordle-L)");

        rwordleCJButton.setText("RWordle-C");
        rwordleCJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rwordleCJButtonActionPerformed(evt);
            }
        });

        rwordleLJButton.setText("RWordle-L");
        rwordleLJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rwordleLJButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Ângulo:");

        embaralhaJButton.setText("Embaralha");
        embaralhaJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                embaralhaJButtonActionPerformed(evt);
            }
        });

        jMenu1.setText("Algoritmos");
        jMenu1.add(jSeparator1);

        rwordlelJMenuItem.setText("RWordle-L");
        rwordlelJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rwordlelJMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(rwordlelJMenuItem);

        jMenuItem1.setText("RWordle-C");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(telaJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(recentralizarJCheckBox)
                    .addComponent(rwordleCJButton)
                    .addComponent(embaralhaJButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(anguloJTextField))
                        .addComponent(rwordleLJButton, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(telaJScrollPane)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(embaralhaJButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rwordleCJButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rwordleLJButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(anguloJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(recentralizarJCheckBox)
                .addContainerGap(310, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private double distanciaEuclideana(double p1x, double p1y, double p2x, double p2y) {
        return Math.sqrt(Math.pow(p1x-p2x, 2) + Math.pow(p1y-p2y, 2));
    }
    
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        double xmin = rectangles.get(0).getMinX(), xmax = rectangles.get(0).getMaxX(), 
               ymin = rectangles.get(0).getMinY(), ymax = rectangles.get(0).getMaxY();
               
        for( Retangulo r: rectangles ) {
            if( r.getMinX() < xmin ) 
                xmin = r.getMinX();
            if( r.getMaxX() > xmax )
                xmax = r.getMaxX();
            if( r.getMinY() < ymin )
                ymin = r.getMinY();
            if( r.getMaxY() > ymax )
                ymax = r.getMaxY();
        }
        
        Shape r = new Rectangle((int)xmin, (int)ymin, (int)distanciaEuclideana(xmin, ymin, xmax, ymin), (int)distanciaEuclideana(xmin, ymin, xmin, ymax));
        
        double centerX = r.getBounds().getCenterX();
        double centerY = r.getBounds().getCenterY();
        
        System.out.println(centerX+"  "+centerY);
        
        ArrayList<IDShape> shapes = new ArrayList<>();
        for( int i = 0; i < rectangles.size(); ++i ) {
           shapes.add(new IDShape(new Retangulo(rectangles.get(i).x, rectangles.get(i).y, 
                                                rectangles.get(i).width, rectangles.get(i).height,
                                                rectangles.get(i).cor, rectangles.get(i).numero),
                    (int) distanciaEuclideana(centerX, centerY, rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY())));
            
        }
        
        Collections.sort(shapes, new Comparator<IDShape>() {

            @Override
            public int compare(IDShape o1, IDShape o2) {
                return o1.getDistance() - o2.getDistance();
            }
            
        });
            
        
        ArrayList<Retangulo> projected = new ArrayList<>();
        for( int i = 0; i < shapes.size(); ++i ) {
            double angle = 2;
            double adjust = .5;
            
            boolean flag;  
            
            Area areaS = null;
            do {
                
                flag = true;
                
                double x = shapes.get(i).getRect().getCenterX() + Math.sin(angle)*angle*adjust;
                double y = shapes.get(i).getRect().getCenterY() + Math.cos(angle)*angle*adjust;                
                
                Shape s = new Rectangle.Double(x, y, 50, 50);
                areaS = new Area(s);
                for( Retangulo rect: projected ) {
                    Shape s1 = new Rectangle.Double(rect.x, rect.y, rect.width, rect.height);
                    Area areaS1 = new Area(s1);
                    areaS1.intersect(areaS);
                    if( !areaS1.isEmpty() ) {
                        flag = false;
                        break;
                    }                        
                } 
                angle += (0.5/angle); 
            } while( !flag );
            
            
            projected.add(new Retangulo(areaS.getBounds().x, areaS.getBounds().y, areaS.getBounds().getWidth(), 
                                        areaS.getBounds().getHeight(), shapes.get(i).getRect().cor, shapes.get(i).getRect().numero));
                
                
            
        }
        
        rectangles.clear();
        //rectangles.add(new Retangulo(new Rectangle((int)centerX, (int)centerY, 5, 5), Color.RED));
        for( Retangulo e: projected )            
            rectangles.add(new Retangulo(e.x, e.y, e.width, e.height, e.cor, e.numero));           
            
        
        
        
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void rwordlelJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rwordlelJMenuItemActionPerformed
        double xmin = rectangles.get(0).getMinX(), xmax = rectangles.get(0).getMaxX(), 
               ymin = rectangles.get(0).getMinY(), ymax = rectangles.get(0).getMaxY();
               
        for( Retangulo r: rectangles ) {
            if( r.getMinX() < xmin ) 
                xmin = r.getMinX();
            if( r.getMaxX() > xmax )
                xmax = r.getMaxX();
            if( r.getMinY() < ymin )
                ymin = r.getMinY();
            if( r.getMaxY() > ymax )
                ymax = r.getMaxY();
        }
        
        double initialCenterX = (xmin+xmax)/2;
        double initialCenterY = (ymin+ymax)/2;
        
        
        novos = new ArrayList<>();
        for( int i = 0; i < rectangles.size(); ++i ) {
            int aux = (int)distanciaEuclideana(0, 0, rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY());
                        
            novos.add(new Retangulo(
                        (rectangles.get(i).x-aux), (rectangles.get(i).y-aux), rectangles.get(i).width, 
                        rectangles.get(i).height, 
                        rectangles.get(i).cor,
                        rectangles.get(i).numero
                      ));            
        }
        
        for( int i = 0; i < novos.size(); ++i ) {
            double x = novos.get(i).x;
            double y = novos.get(i).y;
            
            novos.get(i).x =  (x*Math.cos(Math.toRadians(alpha)) - y*Math.sin(Math.toRadians(alpha)));
            novos.get(i).y =  (x*Math.sin(Math.toRadians(alpha)) + y*Math.cos(Math.toRadians(alpha)));
        }
        
        
        for( int i = 0; i < novos.size(); ++i ) {
            double aux = distanciaEuclideana(0, 0, rectangles.get(i).getCenterX(), rectangles.get(i).getCenterY());
            novos.get(i).x += aux;
            novos.get(i).y += aux;
        }
        
        
        
        Collections.sort(novos, new Comparator<Retangulo>() {

            @Override
            public int compare(Retangulo o1, Retangulo o2) {
                return (int)o1.x - (int)o2.x;
            }
            
        });
        
        Rectangle r = new Rectangle(0, 0, 10, 10);        
        xmin = 99999; xmax = -1; ymin = 99999; ymax = -1;
       
        int xlinha = 0, ylinha = 0;
        ArrayList<Retangulo> projected = new ArrayList<>();
        for( int i = 0; i < novos.size(); ++i ) {
            double angle = 3;
            double adjust = .5;
            
            boolean flag;  
            
            Area areaS = null;
            do {
                
                flag = true;
                
                double x = (novos.get(i).getCenterX()+xlinha) + Math.sin(angle)*angle*adjust;
                double y = (novos.get(i).getCenterY()+ylinha) + Math.cos(angle)*angle*adjust;                
                
                Shape s = new Rectangle.Double(x, y, novos.get(i).width, novos.get(i).height);
                areaS = new Area(s);
                for( Retangulo rect: projected ) {
                    Shape s1 = new Rectangle.Double(rect.x, rect.y, rect.width, rect.height);
                    Area areaS1 = new Area(s1);
                    areaS1.intersect(areaS);
                    if( !areaS1.isEmpty() ) {
                        flag = false;
                        break;
                    }                        
                } 
                angle += (0.5/angle); 
            } while( !flag );
                        
            projected.add(new Retangulo(areaS.getBounds().x, areaS.getBounds().y, 
                                        areaS.getBounds().width, areaS.getBounds().height,
                                        novos.get(i).cor, novos.get(i).numero));
                        
            if( recentralizarJCheckBox.isSelected() ) {
                if( areaS.getBounds().getMinX() < xmin || areaS.getBounds().getMaxX() > xmax || 
                    areaS.getBounds().getMinY() < ymin || areaS.getBounds().getMaxY() > ymax ) {
                    xmin = Math.min(xmin, areaS.getBounds().getMinX());
                    xmax = Math.max(xmax, areaS.getBounds().getMaxX());
                    ymin = Math.min(ymin, areaS.getBounds().getMinY());
                    ymax = Math.max(ymax, areaS.getBounds().getMaxY());


                    r.setBounds((int)xmin, (int)ymin, (int)distanciaEuclideana(xmin, ymin, xmax, ymin), (int)distanciaEuclideana(xmin, ymin, xmin, ymax));

                    xlinha = (int)initialCenterX - (int)r.getCenterX();
                    ylinha = (int)initialCenterY - (int)r.getCenterY();
                }
            }   
            
        }
        rectangles.clear();
        for( Retangulo e: projected )            
            rectangles.add(new Retangulo(e.x, e.y, e.width, e.height, e.cor, e.numero));           
            
        
        
        
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_rwordlelJMenuItemActionPerformed

    private void rwordleLJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rwordleLJButtonActionPerformed
        alpha = Double.parseDouble(anguloJTextField.getText());
        rwordlelJMenuItemActionPerformed(null);
    }//GEN-LAST:event_rwordleLJButtonActionPerformed

    private void embaralhaJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_embaralhaJButtonActionPerformed
        embaralha();
    }//GEN-LAST:event_embaralhaJButtonActionPerformed

    private void rwordleCJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rwordleCJButtonActionPerformed
        jMenuItem1ActionPerformed(null);
    }//GEN-LAST:event_rwordleCJButtonActionPerformed

           
    
    
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
        RainbowScale rbS = new RainbowScale();
        rectangles.add(new Retangulo(140,  158, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(153,  130, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(128,  103, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(89 , 82, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(75 , 130, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(88 , 191, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(153,  191, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(216,  189, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(200,  99, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(157,  83, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(196,  154, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(108,  149, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(115,  226, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++)); 
        rectangles.add(new Retangulo(175,  213, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
      /*  
        rectangles.add(new Retangulo(new Rectangle(1*10, 2*10, (3-1)*10, (4-2)*10), rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(new Rectangle(2*10, 3*10, (5-2)*10, (5-3)*10), rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(new Rectangle(9*10, 3*10, (11-9)*10, (5-3)*10), rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(new Rectangle(4*10, 4*10, (8-4)*10, (8-4)*10), rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(new Rectangle(7*10, 6*10, (9-7)*10, (8-6)*10), rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(new Rectangle(3*10, 7*10, (5-3)*10, (10-7)*10), rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(new Rectangle(11*10, 7*10, (13-11)*10, (9-7)*10), rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(new Rectangle(7*10, 9*10, (9-7)*10, (11-9)*10), rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new Retangulo(new Rectangle(12*10, 9*10, (14-12)*10, (11-9)*10), rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
*/
        
        
        if( view != null ) {
            view.cleanImage();
            view.repaint();            
        }
    }
    
    
    public class ViewPanel extends JPanel {
        private Color color = Color.RED;
        
        private BufferedImage imageBuffer;
        
        public ViewPanel() {
            setBackground(Color.WHITE);
            setLayout(new FlowLayout(FlowLayout.LEFT));
            rectangles = new ArrayList<>();
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    RainbowScale rbS = new RainbowScale();
                    rectangles.add(new Retangulo(e.getX(), e.getY(), e.getX()+50, e.getY()+50, 
                                                 rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
                    cleanImage();
                    repaint();    
                }   
            }); 
            
            addMouseMotionListener(new MouseMotionAdapter() {            
                @Override
                public void mouseDragged(MouseEvent e) {
                    System.out.println("*> "+e.getX()+"  "+e.getY());
                }            
            });
            
            embaralha();
            cleanImage();
            repaint();            
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
                this.imageBuffer = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);

                java.awt.Graphics2D g2Buffer = this.imageBuffer.createGraphics();
                g2Buffer.setColor(this.getBackground());
                g2Buffer.fillRect(0, 0, 2000, 2000);

                g2Buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                for( Retangulo r: rectangles ) {                    
                    g2Buffer.setColor(r.cor);
                    g2Buffer.fillRect((int)r.x, (int)r.y, (int)r.width, (int)r.height);
                    
                    g2Buffer.setColor(Color.WHITE);
                   // g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 20));                    
                   // g2Buffer.drawString(String.valueOf(r.numero), r.rect.x+20, r.rect.y+20);                           
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

    }
    
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField anguloJTextField;
    private javax.swing.JButton embaralhaJButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JCheckBox recentralizarJCheckBox;
    private javax.swing.JButton rwordleCJButton;
    private javax.swing.JButton rwordleLJButton;
    private javax.swing.JMenuItem rwordlelJMenuItem;
    private javax.swing.JScrollPane telaJScrollPane;
    // End of variables declaration//GEN-END:variables
}

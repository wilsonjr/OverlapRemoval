/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.grafos.ui;


import br.com.grafos.desenho.color.RainbowScale;
import br.com.metodos.overlap.rwordle.RWordleC;
import br.com.metodos.overlap.rwordle.RWordleL;
import br.com.metodos.overlap.vpsc.VPSC;
import br.com.metodos.utils.Retangulo;
import br.com.metodos.utils.RetanguloVis;
import br.com.metodos.utils.Util;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author wilson
 */
public class Menu extends javax.swing.JFrame {
    private ViewPanel view;
    private ArrayList<RetanguloVis> rectangles;
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
        vpscJButton = new javax.swing.JButton();
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

        vpscJButton.setText("VPSC");
        vpscJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vpscJButtonActionPerformed(evt);
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(embaralhaJButton)
                        .addGap(18, 18, 18)
                        .addComponent(vpscJButton))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(rwordleCJButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(anguloJTextField))
                        .addComponent(rwordleLJButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(telaJScrollPane)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(embaralhaJButton)
                    .addComponent(vpscJButton))
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

    
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        ArrayList<Retangulo> projected = RWordleC.apply(Util.toRetangulo(rectangles));
        Util.toRetanguloVis(rectangles, projected);
        
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void rwordlelJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rwordlelJMenuItemActionPerformed
        ArrayList<Retangulo> projected  = RWordleL.apply(Util.toRetangulo(rectangles), alpha, recentralizarJCheckBox.isSelected());                   
        Util.toRetanguloVis(rectangles, projected);
        
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

    private void vpscJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vpscJButtonActionPerformed
//        ArrayList<Retangulo> rects = new ArrayList<>();
//        rects.add(new Retangulo(1, 2, 3-1, 4-2));
//        rects.add(new Retangulo(2, 3, 5-2, 5-3));
//        rects.add(new Retangulo(9, 3, 11-9, 5-3));
//        rects.add(new Retangulo(4, 4, 8-4, 8-4));
//        rects.add(new Retangulo(7, 6, 9-7, 8-6));
//        rects.add(new Retangulo(3, 7, 5-3, 10-7));
//        rects.add(new Retangulo(11, 7, 13-11, 9-7));
//        rects.add(new Retangulo(7, 9, 9-7, 11-9));
//        rects.add(new Retangulo(12, 9, 14-12, 11-9));
        
        ArrayList<Retangulo> projected = VPSC.apply(Util.toRetangulo(rectangles), 0, 0);
        for( Retangulo r: projected )
            System.out.println(r);
        Util.toRetanguloVis(rectangles, projected);
        
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_vpscJButtonActionPerformed

           
    
    
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
        rectangles.add(new RetanguloVis(140,  158, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new RetanguloVis(153,  130, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new RetanguloVis(128,  103, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new RetanguloVis(89 , 82, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new RetanguloVis(75 , 130, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new RetanguloVis(88 , 191, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new RetanguloVis(153,  191, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new RetanguloVis(216,  189, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new RetanguloVis(200,  99, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new RetanguloVis(157,  83, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new RetanguloVis(196,  154, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new RetanguloVis(108,  149, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
        rectangles.add(new RetanguloVis(115,  226, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++)); 
        rectangles.add(new RetanguloVis(175,  213, 50, 50, rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
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
                    rectangles.add(new RetanguloVis(e.getX(), e.getY(), 50, 50, 
                                                 rbS.getColor((globalCounterColor++*10)%255), globalCounter++));
                    cleanImage();
                    repaint();    
                    System.out.println(">> "+e.getX()+" "+e.getY());
                }   
            }); 
            
            addMouseMotionListener(new MouseMotionAdapter() {            
                @Override
                public void mouseDragged(MouseEvent e) {
                    System.out.println("*> "+e.getX()+"  "+e.getY());
                }            
            });
            
         //   embaralha();
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
                
                for( RetanguloVis r: rectangles ) {                    
                    g2Buffer.setColor(r.cor);
                    g2Buffer.fillRect((int)r.getUX(), (int)r.getUY(), (int)r.getWidth(), (int)r.getHeight());
                    
                    g2Buffer.setColor(Color.WHITE);
                    g2Buffer.setFont(new Font("Helvetica", Font.PLAIN, 20));                    
                    g2Buffer.drawString(String.valueOf(r.numero), (int)r.getUX()+20, (int)r.getUY()+20);                           
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
    private javax.swing.JButton vpscJButton;
    // End of variables declaration//GEN-END:variables
}

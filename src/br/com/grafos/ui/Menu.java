/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.grafos.ui;


import br.com.grafos.desenho.color.RainbowScale;
import br.com.metodos.overlap.prism.PRISM;
import br.com.metodos.overlap.rwordle.RWordleC;
import br.com.metodos.overlap.rwordle.RWordleL;
import br.com.metodos.overlap.vpsc.VPSC;
import br.com.metodos.utils.Retangulo;
import br.com.metodos.utils.RetanguloVis;
import br.com.metodos.utils.Util;
import java.awt.Color;
import java.awt.Dimension;
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
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        prismJButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        sairJMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        recentralizarJCheckBox.setText("Recentralizar a cada iteração");

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

        embaralhaJButton.setText("Embaralhar");
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

        prismJButton.setText("PRISM");
        prismJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prismJButtonActionPerformed(evt);
            }
        });

        jMenu1.setText("Algoritmos");

        sairJMenuItem.setText("Sair");
        sairJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sairJMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(sairJMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(telaJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(embaralhaJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rwordleCJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(recentralizarJCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(anguloJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(rwordleLJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(vpscJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(prismJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(telaJScrollPane)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(embaralhaJButton)
                .addGap(3, 3, 3)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(rwordleCJButton)
                .addGap(12, 12, 12)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rwordleLJButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(anguloJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(recentralizarJCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vpscJButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prismJButton)
                .addContainerGap(175, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void rwordleLJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rwordleLJButtonActionPerformed
        try {
            alpha = Double.parseDouble(anguloJTextField.getText());
        } catch( NumberFormatException e ) {
            alpha = 0;
        }
        ArrayList<Retangulo> projected  = RWordleL.apply(Util.toRetangulo(rectangles), alpha, recentralizarJCheckBox.isSelected());
        Util.normalize(projected);
        Util.toRetanguloVis(rectangles, projected);

        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_rwordleLJButtonActionPerformed

    private void embaralhaJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_embaralhaJButtonActionPerformed
        embaralha();
    }//GEN-LAST:event_embaralhaJButtonActionPerformed

    private void rwordleCJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rwordleCJButtonActionPerformed
        ArrayList<Retangulo> projected = RWordleC.apply(Util.toRetangulo(rectangles));
        Util.normalize(projected);
        Util.toRetanguloVis(rectangles, projected);

        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_rwordleCJButtonActionPerformed

    private void vpscJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vpscJButtonActionPerformed
        ArrayList<Retangulo> projected = VPSC.apply(Util.toRetangulo(rectangles), 0, 0);
        int i = 0;
        for( Retangulo r: projected ) 
            r.setId(i++);       
        
        Util.normalize(projected);        
        Util.toRetanguloVis(rectangles, projected);
        
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_vpscJButtonActionPerformed

    private void prismJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prismJButtonActionPerformed
        ArrayList<Retangulo> projected = PRISM.apply(Util.toRetangulo(rectangles));
        int i = 0;
        for( Retangulo r: projected )
            r.setId(i++);        
        
        Util.normalize(projected);
        Util.toRetanguloVis(rectangles, projected);
        
        view.cleanImage();
        view.repaint();
    }//GEN-LAST:event_prismJButtonActionPerformed

    private void sairJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sairJMenuItemActionPerformed
        dispose();
    }//GEN-LAST:event_sairJMenuItemActionPerformed

           
    
    
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
                setPreferredSize(new Dimension(5000, 5000));
                this.imageBuffer = new BufferedImage(5000, 5000, BufferedImage.TYPE_INT_RGB);

                java.awt.Graphics2D g2Buffer = this.imageBuffer.createGraphics();
                g2Buffer.setColor(this.getBackground());
                g2Buffer.fillRect(0, 0, 5000, 5000);

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
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JButton prismJButton;
    private javax.swing.JCheckBox recentralizarJCheckBox;
    private javax.swing.JButton rwordleCJButton;
    private javax.swing.JButton rwordleLJButton;
    private javax.swing.JMenuItem sairJMenuItem;
    private javax.swing.JScrollPane telaJScrollPane;
    private javax.swing.JButton vpscJButton;
    // End of variables declaration//GEN-END:variables
}

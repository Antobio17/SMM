/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.ajr.iu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import sm.ajr.graphics.AJREllipse;
import sm.ajr.graphics.AJRGeneralPath;
import sm.ajr.graphics.AJRLine;
import sm.ajr.graphics.AJRRectangle;
import sm.ajr.graphics.AJRShape2D;


/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public class Canvas2D extends javax.swing.JPanel {
    
    /******************************* PROPERTIES ******************************/
    
    public enum EnumShape { GENERALPATH, LINE, RECTANGLE, ELLIPSE }
    EnumShape activeShape;
    private Color activeColor, activeStrokeColor;
    private Point2D initialPoint;
    private Point movingPoint; /* To do a secuencial movement */
    private boolean fillMode, selectorMode, antialiasingMode,
            transparencyMode;
    private Composite activeComposite;
    
    /* Shape */
    List<AJRShape2D> vShape = new ArrayList();
    AJRShape2D actualShape;
    
    /* Stroke */
    float widthStroke;
    float[] dash;
    float disPatternClipArea[] = {5.0f, 5.0f};

    /* Image */
    BufferedImage image;
    
    /* Clip Area */
    Shape clipArea;
    
    /* Windows Effect */
    private boolean windowsEffectMode;
    Ellipse2D windowsEffect;

    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Creates new form Canvas
     */
    public Canvas2D() {
        initComponents();
        initialPoint = new Point2D.Float(-10,-10);
        movingPoint = new Point(-10, -10);
        activeShape = EnumShape.GENERALPATH;
        activeColor = Color.BLACK;
        activeStrokeColor = Color.BLACK;
        fillMode = false;
        transparencyMode = false;
        selectorMode = false;
        windowsEffectMode = false;
        antialiasingMode = false;
        widthStroke = 1.0f;
        clipArea = null;
        actualShape = null;
        windowsEffect = new Ellipse2D.Float(100,100,300,200);
        image = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
    }
    
    /*************************** GETTER AND SETTER ***************************/
    
    /**
     * Sets the image on the canvas.
     * 
     * @param image 
     */
    public void setImage(BufferedImage image){
        this.image = image;
        if(image != null)
            this.setPreferredSize(
                    new Dimension(
                            image.getWidth(), 
                            image.getHeight()
                    )
            );
    }
    
    /**
     * Gets the image of the canvas.
     * 
     * @param drawVector The boolean to draw or not the vector of shapes.
     * 
     * @return BufferedImage
     */
    public BufferedImage getImage(boolean drawVector)
    {
        if (drawVector) {
            int type = image.getType();
            BufferedImage imageOut = new BufferedImage(
                    image.getWidth(), image.getHeight(), type != 0 ? type : 2);
            boolean actualOpaque = this.isOpaque();
            if (image.getColorModel().hasAlpha()) {
                this.setOpaque(false);
            }
            this.paint(imageOut.createGraphics());
            this.setOpaque(actualOpaque);
            return imageOut;
        }else{
            return image;
        }
    }
    
    /**
     * Sets the windows effect mode on the canvas.
     * 
     * @param windowsEffectMode
     */
    public void setWindowsEffectMode(boolean windowsEffectMode)
    {
        this.windowsEffectMode = windowsEffectMode;
    }
    
    /**
     * Gets the windows effect mode of the canvas.
     * 
     * @return boolean
     */
    public boolean getWindowsEffectMode()
    {
        return this.windowsEffectMode;
    }
    
    /**
     * Sets the transparency mode on the canvas.
     * 
     * @param transparencyMode
     */
    public void setTransparencyMode(boolean transparencyMode)
    {
        this.transparencyMode = transparencyMode;
    }
    
    /**
     * Gets the transparency mode of the canvas.
     * 
     * @return boolean
     */
    public boolean getTransparencyMode()
    {
        return this.transparencyMode;
    }
    
    /**
     * Sets the width stroke on the canvas.
     * 
     * @param widthStroke
     */
    public void setWidthStroke(int widthStroke)
    {
        this.widthStroke = (float)widthStroke;
    }
    
    /**
     * Gets the width stroke of the canvas.
     * 
     * @return Float
     */
    public float getWidthStroke()
    {
        return this.widthStroke;
    }
    
    /**
     * Sets the active shape on the canvas.
     * 
     * @param activeShape 
     */
    public void setActiveShape(EnumShape activeShape)
    {
        this.activeShape = activeShape;
    }
    
    /**
     * Gets the active shape of the canvas.
     * 
     * @return EnumShape
     */
    public EnumShape getActiveShape()
    {
        return this.activeShape;
    }
    
    /**
     * Sets the active color on the canvas.
     * 
     * @param activeColor 
     */
    public void setActiveColor(Color activeColor)
    {
        this.activeColor = activeColor;
    }
    
    /**
     * Gets the active color on the canvas.
     * 
     * @return Color
     */
    public Color getActiveColor()
    {
        return this.activeColor;
    }
    
    /**
     * Sets the active color on the canvas.
     * 
     * @param activeStrokeColor 
     */
    public void setActiveStrokeColor(Color activeStrokeColor)
    {
        this.activeStrokeColor = activeStrokeColor;
    }
    
    /**
     * Gets the active color on the canvas.
     * 
     * @return Color
     */
    public Color getActiveStrokeColor()
    {
        return this.activeStrokeColor;
    }
    
    /**
     * Sets the fill mode on the canvas.
     * 
     * @param fillMode 
     */
    public void setFillMode(Boolean fillMode)
    {
        this.fillMode = fillMode;
    }
    
    /**
     * Gets the fill mode of the canvas.
     * 
     * @return boolean 
     */
    public boolean getFillMode()
    {
        return this.fillMode;
    }
    
    
    /**
     * Sets the selector mode on the canvas.
     * 
     * @param selectorMode 
     */
    public void setSelectorMode(boolean selectorMode)
    {
        this.selectorMode = selectorMode;
    }
    
    /**
     * Gets the fill mode of the canvas.
     * 
     * @return boolean
     */
    public boolean getSelectorMode()
    {
        return this.selectorMode;
    }
    
    /**
     * Sets the antialiasing mode on the canvas.
     * 
     * @param antialiasingMode 
     */
    public void setAntialiasingMode(boolean antialiasingMode)
    {
        this.antialiasingMode = antialiasingMode;
    }
    
    /**
     * Gets the antialiasing mode of the canvas.
     * 
     * @return boolean
     */
    public boolean getAntialiasingMode()
    {
        return this.antialiasingMode;
    }
    
    /**
     * Sets the active composite on the canvas.
     * 
     * @param activeComposite 
     */
    public void setActiveComposite(Composite activeComposite)
    {
        this.activeComposite = activeComposite;
    }
    
    /**
     * Gets the active composite of the canvas.
     * 
     * @return Composite
     */
    public Composite getActiveComposite()
    {
        return this.activeComposite;
    }
    
    /**
     * Establece la figura actual selecccionada en el lienzo.
     * 
     * @param actualShape AJRShape2D figura a setear.
     */
    public void setActualShape(AJRShape2D actualShape)
    {
        this.actualShape = actualShape;
    }
    
    /**
     * Obtiene la figura actual seleccionada en el lienzo.
     * 
     * @return AJRShape2D figura actual seleccionada | null en caso de no 
     * haber ninguna figura seleccionada.
     */
    public AJRShape2D getActualShape()
    {
        return this.actualShape;
    }
    
    /***************************** PUBLIC METHODS ****************************/
    
    /**
     * Method to paint the list of shapes that are contained in the canvas.
     * 
     * @param g 
     */
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d=(Graphics2D)g;
        
        if(windowsEffectMode){
            g2d.clip(windowsEffect);
            g2d.draw(windowsEffect);
        }
        g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        this._setClipArea(g2d);
        
        for(AJRShape2D s:vShape){
            s.paint(g2d);
        }
    }
    
    /**
     * Method to set the position of the elipse windows effect.
     * 
     * @param position The coords of the center of the elipse in windows effect.
     */
    public void setWindowsEffectPosition(Point2D position)
    {
        Point2D corner = new Point2D.Double(
                position.getX()+200, position.getY()+100);
        windowsEffect.setFrameFromCenter(position, corner);
    }
    
    /**
     * Method to clear the shapes array
     * 
     */
    public void clearShapesArray()
    {
        vShape.clear();
    } 
    
    /***************************** PRIVARE METHODS ***************************/
    
    /**
     * Method to get the selected shape in the canvas when the mouse click it.
     * 
     * @param p
     * 
     * @return AJRShape|null
     */
    private AJRShape2D _getSelectedShape(Point2D p)
    {
        for(AJRShape2D s:vShape){
            if(s.contains(p)){
                String classShape = s.getClass().getSimpleName();
                switch (classShape) {
                    case "AJRGeneralPath":
                        activeShape = EnumShape.GENERALPATH;
                        break;
                    case "AJRLine":
                        activeShape = EnumShape.LINE;
                        break;
                    case "AJRRectangle":
                        activeShape = EnumShape.RECTANGLE;
                        break;
                    case "AJREllipse":
                        activeShape = EnumShape.ELLIPSE;
                        break;
                    default:
                        break;
                }
                return s;
            }
        }
        
        return null;
    }
    
    /**
     * Method to manage the clip area of the canvas
     * 
     * @param g2d 
     */
    private void _setClipArea(Graphics2D g2d)
    {
        // Sets the discontinue stroke of the clip area
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, 
                BasicStroke.JOIN_MITER, 1.0f,
                disPatternClipArea, 0.0f));
        
        clipArea = new Rectangle.Float(
                0,0,image.getWidth(), image.getHeight());
        g2d.clip(clipArea);
        g2d.draw(clipArea);
    }
    
    /************************** JAVA GENERATED CODE **************************/
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        if(!windowsEffectMode){
            initialPoint = evt.getPoint();
            // To save the start of the movement 
            movingPoint = evt.getPoint();
            BasicStroke stroke = new BasicStroke(widthStroke,
                                BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,
                                1.0f, dash, 0.0f);
            if(!selectorMode){
                switch (activeShape){
                    case GENERALPATH:
                        actualShape = new AJRGeneralPath();
                        break;
                    case LINE:
                        actualShape = new AJRLine();
                        break;
                    case RECTANGLE:
                        actualShape = new AJRRectangle();
                        ((AJRRectangle)actualShape).setIsFill(fillMode);
                        break;
                    case ELLIPSE:
                        actualShape = new AJREllipse();
                        ((AJREllipse)actualShape).setIsFill(fillMode);
                        break;
                }
                actualShape.createShape(initialPoint, activeColor, activeStrokeColor, antialiasingMode,
                        activeComposite, stroke
                );    
                vShape.add(actualShape);
            }else{
                actualShape = _getSelectedShape(movingPoint);
            }
        }
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        if(!windowsEffectMode){
            // To calculate de movement of a shape
            int localMovX = evt.getPoint().x - movingPoint.x;
            int localMovY = evt.getPoint().y - movingPoint.y;
            movingPoint = evt.getPoint();

            if(!selectorMode){
                actualShape.updateShape(initialPoint, evt.getPoint());
            }else if(actualShape != null){
                actualShape.setLocation(
                        (float)(actualShape).getLocation().getX()+localMovX,
                        (float)(actualShape).getLocation().getY()+localMovY);
            }

            repaint();
        }
    }//GEN-LAST:event_formMouseDragged

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        repaint();
    }//GEN-LAST:event_formMouseReleased

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        if(selectorMode)
            actualShape = _getSelectedShape(evt.getPoint());
    }//GEN-LAST:event_formMouseClicked
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

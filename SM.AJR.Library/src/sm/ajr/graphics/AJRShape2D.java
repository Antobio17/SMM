/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.ajr.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public abstract class AJRShape2D
{
    
    /******************************* PROPERTIES ******************************/
    
    Color color;
    boolean hasAntialiasing;
    BasicStroke stroke;
    Composite composite;

    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Create new form AJRShape2D
     */
    public AJRShape2D()
    {
        color = Color.BLACK;
        hasAntialiasing = false;
        stroke = null;
        composite = null;
    }
    
    /*************************** GETTER AND SETTER ***************************/
    
    /**
     * Sets the color of the shape.
     * 
     * @param color 
     */
    public void setColor(Color color)
    {
        this.color = color;
    }
    
    /**
     * Gets the actual color of the shape.
     * 
     * @return Color
     */
    public Color getColor()
    {
        return this.color;
    }
    
    
    /**
     * Sets the width stroke of the shape.
     * 
     * @param stroke 
     */
    public void setStroke(BasicStroke stroke)
    {
        this.stroke = stroke;
    }
    
    /**
     * Gets the width stroke of the shape.
     * 
     * @return BasicStroke 
     */
    public BasicStroke getStroke()
    {
        return this.stroke;
    }
    
    /**
     * Sets the antialiasing condition of the shape.
     * 
     * @param hasAntialiasing 
     */
    public void setHasAntialiasing(boolean hasAntialiasing)
    {
        this.hasAntialiasing = hasAntialiasing;
    }
    
    /**
     * Gets the actual antialiasing condition of the shape.
     * 
     * @return boolean 
     */
    public boolean getHasAntialiasing()
    {
        return this.hasAntialiasing;
    }
    
    /**
     * Sets the composite of the shape to apply the transparency.
     * 
     * @param composite 
     */
    public void setComposite(Composite composite)
    {
        this.composite = composite;
    }
    
    /**
     * Gets the composite of the shape to get the transparency.
     * 
     * @return 
     */
    public Composite getComposite()
    {
        return this.composite;
    }
    
    /**
     * Sets the location of the shape.
     * 
     * @param x
     * @param y
     */
    public abstract void setLocation(float x, float y); 
    
    /**
     * Gets the location of the shape.
     * 
     * @return Point2D
     */
    public abstract Point2D getLocation();
    
    /***************************** PUBLIC METHODS ****************************/
    
    /**
     * Method to paint the shape in the canvas.
     * 
     * @param g2d 
     */
    public void paint(Graphics2D g2d)
    {
        g2d.setColor(this.color);
        g2d.setStroke(this.stroke);
        g2d.setComposite(this.composite);
        if(hasAntialiasing)
            g2d.setRenderingHints(new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING, 
                    RenderingHints.VALUE_ANTIALIAS_ON));
        else
            g2d.setRenderingHints(new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING, 
                    RenderingHints.VALUE_ANTIALIAS_OFF));
    }
    
    /**
     * Methods to know if the point p it is contains in the shape.
     * 
     * @param p
     * @return boolean
     */
    public abstract boolean contains(Point2D p);
    
    /**
     * Method to update the form of the shape while dragging.
     * 
     * @param initPoint
     * @param endPoint 
     */
    public abstract void updateShape(Point2D initPoint, Point2D endPoint);
    
    /**
     * Method to create a new shape.
     * 
     * @param initPoint
     * @param color
     * @param hasAntialiasing
     * @param composite
     * @param stroke
     */
    public void createShape(Point2D initPoint, Color color,
            boolean hasAntialiasing, Composite composite, BasicStroke stroke)
    {
        this.setStroke(stroke);
        this.setColor(color);
        this.setHasAntialiasing(hasAntialiasing);
        this.setComposite(composite);
    }
    
    /***************************** PRIVARE METHODS ***************************/
    
}

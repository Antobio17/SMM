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
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public class AJRRectangle extends AJRFillShape2D
{    
    
    /******************************* PROPERTIES ******************************/
    
    private Rectangle rectangle;
    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Create new form AJRRectangle
     * 
     */
    public AJRRectangle() 
    {
        super();
        rectangle = new Rectangle();
    }
    
    /**
     * Create new form AJRRectangle
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public AJRRectangle(int x, int y, int width, int height) 
    {
        super();
        rectangle = new Rectangle(x, y, width, height);
    }
    
    /*************************** GETTER AND SETTER ***************************/
    
    /**
     * 
     * @param x
     * @param y 
     */
    public void setLocation(float x, float y) 
    {
        rectangle.setLocation((int)x, (int)y);
    }
    
    /**
     * 
     * @return Point2D
     */
    public Point2D getLocation() 
    {
        return (Point2D)rectangle.getLocation();
    }
  
    /***************************** PUBLIC METHODS ****************************/
    
    /**
     * 
     * @param p
     * @return boolean
     */
    public boolean contains(Point2D p) 
    {
        return rectangle.contains(p);
    }
    
    /**
     * 
     * @param g2d 
     */
    public void paint(Graphics2D g2d) 
    {
        super.paint(g2d);
        if(this.getIsFill())
            g2d.fill(rectangle);
        else
            g2d.draw(rectangle);
    }
    
    /**
     * 
     * @param initPoint
     * @param endPoint 
     */
    public void updateShape(Point2D initPoint, Point2D endPoint)
    {
        rectangle.setFrameFromDiagonal(initPoint, endPoint);
    }
    
    /**
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
        super.createShape(initPoint, color, hasAntialiasing, composite, stroke);
        rectangle = new Rectangle();
    }
    
    /***************************** PRIVARE METHODS ***************************/
    
}

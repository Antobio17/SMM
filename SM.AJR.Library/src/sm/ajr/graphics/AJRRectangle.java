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
     * Crear nuevo objeto AJRRectangle
     * 
     */
    public AJRRectangle() 
    {
        super();
        rectangle = new Rectangle();
    }
    
    /**
     * Crear nuevo objeto AJRRectangle
     * 
     * @param x int: coordenada x de donde se situa el principio del rectángulo.
     * @param y int: coordenada y de donde se situa el principio del rectángulo.
     * @param width int: ancho del rectángulo.
     * @param height int: alto del rectángulo.
     */
    public AJRRectangle(int x, int y, int width, int height) 
    {
        super();
        rectangle = new Rectangle(x, y, width, height);
    }
    
    /*************************** GETTER AND SETTER ***************************/
  
    /***************************** PUBLIC METHODS ****************************/
    
    /**
     * 
     * @param g2d 
     */
    public void paint(Graphics2D g2d) 
    {
        super.paint(g2d);
        if(this.getIsFill()){
            g2d.setColor(this.fillColor);
            g2d.fill(rectangle);
        }
        
        g2d.setColor(this.strokeColor);
        g2d.draw(rectangle);
    }
    
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
     * @param strokeColor
     * @param hasAntialiasing
     * @param composite
     * @param stroke
     */
    @Override
    public void createShape(Point2D initPoint,Color strokeColor,
            boolean hasAntialiasing, Composite composite, BasicStroke stroke) 
    {
        super.createShape(
                initPoint, strokeColor, hasAntialiasing, composite, stroke
        );
        rectangle = new Rectangle();
    }
    
    /***************************** PRIVARE METHODS ***************************/
    
}

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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public class AJREllipse extends AJRFillShape2D
{           
    
    /******************************* PROPERTIES ******************************/
    
    private Ellipse2D.Float ellipse;
    private Point2D initialPoint, endPoint;
    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Crear nuevo objeto AJREllipse
     * 
     */
    public AJREllipse(){
        super();
        ellipse = new Ellipse2D.Float();
        initialPoint = new Point2D.Float();
        endPoint = new Point2D.Float();
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
        if(this.getIsFill())
            g2d.fill(ellipse);
        else
            g2d.draw(ellipse);
    }
    
    /**
     * 
     * @param x
     * @param y 
     */
    public void setLocation(float x, float y) 
    {
        initialPoint.setLocation(x, y);
        endPoint.setLocation(x + ellipse.width, y + ellipse.height);
        ellipse.setFrameFromDiagonal(initialPoint, endPoint);
    }
    
    /**
     * 
     * @return Point2D
     */
    public Point2D getLocation() 
    {
        return initialPoint;
    }
    
    /**
     * 
     * @param p
     * @return boolean
     */
    public boolean contains(Point2D p) 
    {
        return ellipse.contains(p);
    }
    
    /**
     * 
     * @param initPoint
     * @param endPoint 
     */
    public void updateShape(Point2D initPoint, Point2D endPoint)
    {
        this.setFrameFromDiagonal(initPoint, endPoint);
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
        ellipse = new Ellipse2D.Float(
                (float)initPoint.getX(), (float)initPoint.getY(), 0, 0
        );
        initialPoint = initPoint;
        endPoint = initPoint;
    }
    
    /**
     * Establece la diagonal del rectángulo de encuadre de la elipse.
     * 
     * @param p1 Point2D: punto inicial de la diagonal.
     * @param p2 Point2D: punto final de la diagonal.
     */
    public void setFrameFromDiagonal(Point2D p1, Point2D p2)
    {
        endPoint = p2;
        ellipse.setFrameFromDiagonal(
                p1.getX(), p1.getY(), p2.getX(), p2.getY()
        );
    }
    
    /***************************** PRIVARE METHODS ***************************/
}

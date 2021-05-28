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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public class AJRLine extends AJRShape2D
{

    /******************************* PROPERTIES ******************************/
    
    Line2D.Float line;
    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Crear nuevo objeto AJRLine
     * 
     */
    public AJRLine() 
    {
        super();
        line = new Line2D.Float();
    }
    
    /*************************** GETTER AND SETTER ***************************/
    
    /***************************** PUBLIC METHODS ****************************/
    
    /**
     * 
     * @param g2d 
     */
    @Override
    public void paint(Graphics2D g2d) 
    {
        super.paint(g2d);
        g2d.draw(line);
    }
    
    /**
     * 
     * @param x
     * @param y 
     */
    public void setLocation(float x, float y) 
    {
        double dx = x - line.getX1();
        double dy = y - line.getY1();
        Point2D newp2 = new Point2D.Double(line.getX2() + dx, line.getY2() + dy);
        line.setLine(new Point2D.Float(x, y), newp2);
    }
    
    /**
     * 
     * @return Point2D
     */
    public Point2D getLocation() 
    {
        return (Point2D)line.getP1();
    }
    
    /**
     * 
     * @param p
     * @return boolean
     */
    public boolean contains(Point2D p) 
    {
        return isNear(p);
    }
    
    /**
     * 
     * @param initPoint
     * @param endPoint 
     */
    public void updateShape(Point2D initPoint, Point2D endPoint)
    {
        line.setLine(initPoint, endPoint);
    }
    
    /**
     * 
     * @param initPoint
     * @param color
     * @param hasAntialiasing
     * @param composite
     * @param stroke
     */
    @Override
    public void createShape(Point2D initPoint, Color color,
            boolean hasAntialiasing, Composite composite, BasicStroke stroke) 
    {
        super.createShape(initPoint, color, hasAntialiasing, composite, stroke);
        line = new Line2D.Float(initPoint, initPoint);
    }
    
    /**
     * Método para saber si un punto está cerda de la línea.
     * 
     * @param p Point2D: punto a checkear.
     * 
     * @return boolean si el punto está cerca de la línea o no.
     */
    public boolean isNear(Point2D p)
    {
        return line.ptLineDist(p) <= 3.0;
    }
    
    /***************************** PRIVARE METHODS ***************************/
    
}

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
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public class AJRGeneralPath extends AJRShape2D
{
    
    /******************************* PROPERTIES ******************************/
    
    private GeneralPath.Float generalPath;
    List<Point2D> coordsPath = new ArrayList(); /* To save the path */
    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Crear nuevo objeto AJRGeneralPath
     * 
     */
    public AJRGeneralPath() 
    {
        super();
        generalPath = new GeneralPath.Float();
    }
    
    /*************************** GETTER AND SETTER ***************************/
    
    /***************************** PUBLIC METHODS ****************************/
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics2D g2d) 
    {
        super.paint(g2d);
        g2d.draw(generalPath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocation(float x, float y) 
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Point2D getLocation() 
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean contains(Point2D p) 
    {
        return false;
    }
    
    /**
     * 
     * @param initPoint
     * @param endPoint 
     */
    @Override
    public void updateShape(Point2D initPoint, Point2D endPoint) 
    {
        coordsPath.add(endPoint);
        for (int i = 1; i < coordsPath.size(); i++) {
            generalPath.moveTo(
                    coordsPath.get(i).getX(),
                    coordsPath.get(i).getY()
            );
            generalPath.lineTo(
                    coordsPath.get(i).getX(),
                    coordsPath.get(i).getY()
            );
        }
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
    public void createShape(Point2D initPoint, Color color, Color strokeColor,
            boolean hasAntialiasing, Composite composite, BasicStroke stroke)
    {
        super.createShape(
                initPoint, color, strokeColor, hasAntialiasing, composite, stroke
        );
    }
    
    /***************************** PRIVARE METHODS ***************************/
    
    
    
    /***************************** STATIC METHODS ****************************/
    
    /**
     * Obtiene la propiedad WIND_NON_ZERO de Path2D para determinar el interior
     * de un camino.
     * 
     * @return static final int WIND_NON_ZERO
     */
    public static final int getWindNonZero()
    {
        return Path2D.Float.WIND_NON_ZERO;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.ajr.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
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
    
    Color strokeColor;
    boolean hasAntialiasing;
    BasicStroke stroke;
    Composite composite;

    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Crear nuevo objeto AJRShape2D
     */
    public AJRShape2D()
    {
        strokeColor = Color.BLACK;
        hasAntialiasing = false;
        stroke = null;
        composite = null;
    }
    
    /*************************** GETTER AND SETTER ***************************/
    
    /**
     * Establece la propiedad ColorStroke de la figura.
     * 
     * @param strokeColor Color: color a establecer.
     */
    public void setStrokeColor(Color strokeColor)
    {
        this.strokeColor = strokeColor;
    }
    
    /**
     * Obtiene la propiedad ColorStroke de la figura.
     * 
     * @return Color color del trazo de la figura.
     */
    public Color getStrokeColor()
    {
        return this.strokeColor;
    }
    
    /**
     * Establece la propiedad Stroke de la figura.
     * 
     * @param stroke BasicStroke: trazo a establecer.
     */
    public void setStroke(BasicStroke stroke)
    {
        this.stroke = stroke;
    }
    
    /**
     * Establece la propiedad Stroke de la figura.
     * 
     * @return BasicStroke trazo de la figura 
     */
    public BasicStroke getStroke()
    {
        return this.stroke;
    }
    
    /**
     * Establece la propiedad HasAntialiasing de la figura.
     * 
     * @param hasAntialiasing Boolean: booleano a establecer
     */
    public void setHasAntialiasing(boolean hasAntialiasing)
    {
        this.hasAntialiasing = hasAntialiasing;
    }
    
    /**
     * Obtiene la propiedad HasAntialiasing de la figura.
     * 
     * @return boolean si la figura está o no alisada
     */
    public boolean getHasAntialiasing()
    {
        return this.hasAntialiasing;
    }
    
    /**
     * Establece la propiedad Composite de la figura.
     * 
     * @param composite Composite: compuesto para aplicar transparencia
     */
    public void setComposite(Composite composite)
    {
        this.composite = composite;
    }
    
    /**
     * Obtiene la propiedad Composite de la figura.
     * 
     * @return Composite compuesto de la figura para la transparencia.
     */
    public Composite getComposite()
    {
        return this.composite;
    }
    
    /***************************** PUBLIC METHODS ****************************/
    
    /**
     * Método para pintar una figura en el lienzo.
     * 
     * @param g2d Graphics2D: objeto para pintar figuras en el liezo.
     */
    public void paint(Graphics2D g2d)
    {
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
     * Método para establecer la localización de la figura.
     * 
     * @param x int: Valor x de las coordenadas.
     * @param y int: Valor y de las coordenadas.
     */
    public abstract void setLocation(float x, float y); 
    
    /**
     * Método para obtener la localización de la figura.
     * 
     * @return Point2D punto donde se encuentra la figura.
     */
    public abstract Point2D getLocation();
    
    /**
     * Método para saber si un punto P está contenido en la figura.
     * 
     * @param p Point2D: punto a checkear.
     * 
     * @return boolean si el punto está o no contenido.
     */
    public abstract boolean contains(Point2D p);
    
    /**
     * Método para actualizar el tamaño de la figura.
     * 
     * @param initPoint Point2D: punto donde empieza la figura.
     * @param endPoint Point2D: punto donde termina la figura.
     */
    public abstract void updateShape(Point2D initPoint, Point2D endPoint);
    
    /**
     * Método para crear una nueva figura (establecer sus propiedades).
     * 
     * @param initPoint Point2D: punto donde empieza la figura
     * @param strokeColor Color: color del trazo de la figura.
     * @param hasAntialiasing Boolean: si la figura es lisa o no.
     * @param composite Composite: composición de la figura para la transparencia.
     * @param stroke BasicStroke: trazo de la figura.
     */
    public void createShape(Point2D initPoint, Color strokeColor,
            boolean hasAntialiasing, Composite composite, BasicStroke stroke)
    {
        this.setStroke(stroke);
        this.setStrokeColor(strokeColor);
        this.setHasAntialiasing(hasAntialiasing);
        this.setComposite(composite);
    }
    
    /***************************** PRIVARE METHODS ***************************/
    
}

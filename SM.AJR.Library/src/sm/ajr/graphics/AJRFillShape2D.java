/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.ajr.graphics;

import java.awt.Color;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public abstract class AJRFillShape2D extends AJRShape2D 
{
    
    /******************************* PROPERTIES ******************************/

    boolean isFill;
    Color fillColor;
    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Crear nuevo objeto AJRShape2D
     */
    public AJRFillShape2D() 
    {
        super();
        isFill = false;
        fillColor = Color.BLACK;
    }
    
    /*************************** GETTER AND SETTER ***************************/
    
    /**
     * Establece la propiedad IsFill de la figura.
     * 
     * @param isFill Boolean: si la figura está rellena o no.
     */
    public void setIsFill(boolean isFill)
    {
        this.isFill = isFill;
    }
    
    /**
     * Obtiene la propiedad FillColor de la figura.
     * 
     * @return boolean si la figura está rellena o no.
     */
    public boolean getIsFill()
    {
        return this.isFill;
    }
    
    /**
     * Establece la propiedad FillColor de la figura.
     * 
     * @param fillColor Color: color de relleno de la figura.
     */
    public void setFillColor(Color fillColor)
    {
        this.fillColor = fillColor;
    }
    
    /**
     * Obtiene la propiedad IsFill de la figura.
     * 
     * @return Color color del relleno de la figura.
     */
    public Color getFillColor()
    {
        return this.fillColor;
    }
    
    /***************************** PUBLIC METHODS ****************************/
   
    
    /***************************** PRIVARE METHODS ***************************/
    
    
}


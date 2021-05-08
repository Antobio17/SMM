/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.ajr.graphics;

import java.awt.Graphics2D;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public abstract class AJRFillShape2D extends AJRShape2D 
{
    
    /******************************* PROPERTIES ******************************/

    private boolean isFill;
    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Create new form AJRShape2D
     */
    public AJRFillShape2D() 
    {
        super();
        isFill = false;
    }
    
    /*************************** GETTER AND SETTER ***************************/
    
    /**
     * Sets the fill condition of the shape.
     * 
     * @param isFill 
     */
    public void setIsFill(boolean isFill)
    {
        this.isFill = isFill;
    }
    
    /**
     * Gets the fill condition of the shape.
     * 
     * @return boolean 
     */
    public boolean getIsFill()
    {
        return this.isFill;
    }
    
    /***************************** PUBLIC METHODS ****************************/
   
    
    /***************************** PRIVARE METHODS ***************************/
    
    
}


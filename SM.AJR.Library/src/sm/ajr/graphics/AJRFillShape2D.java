/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.ajr.graphics;

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
     * Crear nuevo objeto AJRShape2D
     */
    public AJRFillShape2D() 
    {
        super();
        isFill = false;
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
     * Obtiene la propiedad IsFill de la figura.
     * 
     * @return boolean si la figura está rellena o no.
     */
    public boolean getIsFill()
    {
        return this.isFill;
    }
    
    /***************************** PUBLIC METHODS ****************************/
   
    
    /***************************** PRIVARE METHODS ***************************/
    
    
}


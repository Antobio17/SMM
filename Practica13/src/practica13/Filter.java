/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica13;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public class Filter {
    
    /******************************* PROPERTIES ******************************/
    
    int numRows;
    int numColums;
    float[] mask;
    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Creates new form InternalFrame
     * @param numRows 
     * @param numColums
     * @param mask
     */
    public Filter(int numRows, int numColums, float[] mask ) {
        this.numRows = numRows;
        this.numColums = numColums;
        this.mask = mask;
    }
    
    /*************************** GETTER AND SETTER ***************************/
    
    /**
     * Gets the number of rows of the filter
     * 
     * @return int
     */
    public int getNumRows(){
        return this.numRows;
    }
    
    /**
     * Sets the number of rows of the filter
     * 
     * @param numRows
     */
    public void setNumRows(int numRows){
        this.numRows = numRows;
    }
    
    /**
     * Gets the number of colums of the filter
     * 
     * @return int
     */
    public int getNumColums(){
        return this.numColums;
    }
    
    /**
     * Sets the number of colums of the filter
     * 
     * @param numColums
     */
    public void setNumColums(int numColums){
        this.numColums = numColums;
    }
    
    /**
     * Gets the mask of the filter
     * 
     * @return int
     */
    public float[] getMask(){
        return this.mask;
    }
    
    /**
     * Sets the mask of the filter
     * 
     * @param mask
     */
    public void setMask(float[] mask){
        this.mask = mask;
    }
    

    /***************************** PUBLIC METHODS ****************************/
    
    /***************************** PRIVARE METHODS ***************************/
    
    /************************** JAVA GENERATED CODE **************************/
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.ajr.image;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import sm.image.BufferedImageOpAdapter;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public class RedOp extends BufferedImageOpAdapter{
    
    /******************************* PROPERTIES ******************************/
    
    private int threshold;
    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Creates new operator PosterizeOp
     * 
     * @param threshold 
     */
    public RedOp(int threshold) {
        this.threshold = threshold;
    }

    /*************************** GETTER AND SETTER ***************************/
    
    /***************************** PUBLIC METHODS ****************************/
    
    /**
     * Performs a posterize operation on a BufferedImage.
     * 
     * @param src
     * @param dest
     * @return 
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (src == null)
            throw new NullPointerException("La imagen fuentes es nula.");
        
        if (dest == null)
            dest = createCompatibleDestImage(src, null);
        
        WritableRaster srcRaster = src.getRaster();
        WritableRaster destRaster = dest.getRaster();
        int[] pixelComp = new int[srcRaster.getNumBands()];
        int[] pixelCompDest = new int[srcRaster.getNumBands()];
        System.out.println(threshold);
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                    srcRaster.getPixel(x, y, pixelComp);
                    if(pixelComp[0] - pixelComp[1] - pixelComp[2] >= threshold){
                        pixelCompDest[0] = pixelComp[0];
                        pixelCompDest[1] = pixelComp[1];
                        pixelCompDest[2] = pixelComp[2];
                    }else{
                        int average = 
                                (int)(pixelComp[0] + pixelComp[1] + pixelComp[2])/3;
                        pixelCompDest[0] = average;
                        pixelCompDest[1] = average;
                        pixelCompDest[2] = average;
                    }    
                    destRaster.setPixel(x, y, pixelCompDest);
            }
        }
        return dest;
    }
    
    /***************************** PRIVATE METHODS ***************************/
}

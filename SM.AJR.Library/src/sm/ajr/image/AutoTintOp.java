/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.ajr.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import sm.image.BufferedImageOpAdapter;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public class AutoTintOp extends BufferedImageOpAdapter{
    
    /******************************* PROPERTIES ******************************/
    
    private Color color;
    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Creates new operator AutoTintOp
     * 
     * @param color 
     */
    public AutoTintOp(Color color) {
        this.color = color;
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
        System.out.println(color.getGreen());
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                    srcRaster.getPixel(x, y, pixelComp);
                    
                    float mixingDegree = (float)(((pixelComp[0] + pixelComp[1] + pixelComp[2])/3.0)/255.0);
                    
                    pixelCompDest[0] = (int)(mixingDegree * color.getRed() + (1 - mixingDegree) * pixelComp[0]);
                    pixelCompDest[1] = (int)(mixingDegree * color.getGreen() + (1 - mixingDegree) * pixelComp[1]);
                    pixelCompDest[2] = (int)(mixingDegree * color.getBlue() + (1 - mixingDegree) * pixelComp[2]);
                    
                    destRaster.setPixel(x, y, pixelCompDest);
            }
        }
        return dest;
    }
    
    /***************************** PRIVATE METHODS ***************************/
}

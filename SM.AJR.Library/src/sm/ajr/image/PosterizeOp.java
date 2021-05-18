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
public class PosterizeOp extends BufferedImageOpAdapter{
    
    /******************************* PROPERTIES ******************************/
    
    private int levels;
    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Creates new operator PosterizeOp
     * 
     * @param levels 
     */
    public PosterizeOp(int levels) {
        this.levels = levels;
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
        int sample;
        int K = 256/levels;
        
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                for (int band = 0; band < srcRaster.getNumBands(); band++) {
                    sample = srcRaster.getSample(x, y, band);
                    sample = K * (int)(sample/K);
                    destRaster.setSample(x, y, band, sample);
                }
            }
        }
        return dest;
    }
    
    /***************************** PRIVATE METHODS ***************************/
}

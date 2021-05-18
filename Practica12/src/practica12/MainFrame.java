/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica12;

import sm.ajr.iu.Canvas2D;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;
import sm.ajr.image.PosterizeOp;
import sm.ajr.image.RedOp;
import sm.image.EqualizationOp;
import sm.image.KernelProducer;
import sm.image.LookupTableProducer;
import sm.image.SepiaOp;
import sm.image.TintOp;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public class MainFrame extends javax.swing.JFrame 
{
    
    /******************************** CONSTS *********************************/
        
    public final float[] mediaMask = {
        0.1f, 0.1f, 0.1f,
        0.1f, 0.2f, 0.1f,
        0.1f, 0.1f, 0.1f
    };
    public final Filter media = new Filter(3, 3, mediaMask);
        
    public final float[] smoothedMask = {
        1/9f, 1/9f, 1/9f,
        1/9f, 1/9f, 1/9f,
        1/9f, 1/9f, 1/9f
    };
    public final Filter smoothed = new Filter(3, 3, smoothedMask);
    
    public final float[] enhancementMask = {
         0f, -1f,  0f,
        -1f,  5f, -1f,
        0f, -1f,  0f
    };
    public final Filter enhancement = new Filter(3, 3, enhancementMask);
    
    public final float[] bordersMask = {
        1f,  1f, 1f,
        1f, -8f, 1f,
        1f,  1f, 1f
    };
    public final Filter borders = new Filter(3, 3, bordersMask);
    
    public final float[] horizontalBlurringMask = {
        0.2f, 0.2f, 0.2f, 0.2f, 0.2f
    };
    public final Filter horizontalBlurring = new Filter(
            5, 1, horizontalBlurringMask);
    
    public final float[] diagonalBlurringMask = {
        0.2f,   0f,   0f,   0f,   0f,
          0f, 0.2f,   0f,   0f,   0f,
          0f,   0f, 0.2f,   0f,   0f,
          0f,   0f,   0f, 0.2f,   0f,
          0f,   0f,   0f,   0f, 0.2f,
    };
    public final Filter diagonalBlurring = new Filter(
            5, 5, diagonalBlurringMask);
    
    public final Filter binomial = new Filter(
            3, 3, KernelProducer.MASCARA_BINOMIAL_3x3);
    
    public final Filter intenseFocus = new Filter(
            3, 3, KernelProducer.MASCARA_ENFOQUEINTENSO_3x3);
    
    public final Filter focus = new Filter(
            3, 3, KernelProducer.MASCARA_ENFOQUE_3x3);
    
    public final Filter laplacian = new Filter(
            3, 3, KernelProducer.MASCARA_LAPLACIANA_3x3);
    
    public final Filter relief = new Filter(
            3, 3, KernelProducer.MASCARA_RELIEVE_3x3);
    
    public final Filter solbeX = new Filter(
            3, 3, KernelProducer.MASCARA_SOBELX_3x3);
    
    public final Filter solbeY = new Filter(
            3, 3, KernelProducer.MASCARA_SOBELY_3x3);

    public final Filter filters[] = {media, smoothed, enhancement, borders,
        horizontalBlurring, diagonalBlurring, binomial, intenseFocus, focus,
        laplacian, relief, solbeX, solbeY};
    
    /******************************* PROPERTIES ******************************/
    
    InternalFrame internalFrame = null;
    InternalFrameHandler internalFrameListener = null;
    MouseMotionHandler mouseMotionListener = null;
    Color colorsArray[] = { Color.BLACK, Color.WHITE, Color.RED, Color.YELLOW,
        Color.BLUE, Color.GREEN };
    
    /* Canvas measurements */
    int canvasWidth, canvasHeight;
    
    /* Brightness */
    BufferedImage sourceImage;
    
    /******************************* CONSTRUCTS ******************************/
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        generalPath.setSelected(true);
        widthStroke.setValue(1);
        internalFrame = null;
        internalFrameListener = new InternalFrameHandler();
        mouseMotionListener = new MouseMotionHandler();
        canvasWidth = 700;
        canvasHeight = 600;
        brightnessSlider.setValue(0);
        sourceImage = null;
        brightnessSlider.setMinimum(-100);
        brightnessSlider.setMaximum(100);
        quadraticFunctionSlider.setValue(0);
        rotationSlider.setValue(0);
    }
    
    /*************************** GETTER AND SETTER ***************************/
    
    /**
     * Sets the canvasWidth on the Internal Frame.
     * 
     * @param canvasWidth 
     */
    public void setCanvasWidth(int canvasWidth)
    {
        this.canvasWidth = canvasWidth;
    }
    
    /**
     * Sets the canvasHeight on the Internal Frame.
     * 
     * @param canvasHeight 
     */
    public void setCanvasHeight(int canvasHeight)
    {
        this.canvasHeight = canvasHeight;
    }
    
    /**
     * Gets the selected canvas of the main frame
     * 
     * @return Canvas2D
     */
    public Canvas2D getSelectedCanvas()
    {
        if(internalFrame != null)
            return internalFrame.getCanvas2D();
        else
            return null;
    }
    
    /**
     * Method to know if there ara any Internal Frame in the Main Frame.
     * 
     * @return boolean
     */
    public boolean hasInternalFrameActived()
    {
        return desktop.getComponentCount() != 0;      
    }
    
    /***************************** PUBLIC METHODS ****************************/
 
    /**
     * Method to create a LookupTable with the quadratic function.
     * 
     * @param m
     * @return LookupTable
     */
    public LookupTable quadraticFunctionTable(double m){
        double Max;
        if(m < 128)
            Max = Math.pow(255-m, 2)/100;
        else
            Max = Math.pow(0-m, 2)/100;
        double K = 255.0/Max;
 
        byte lookupTable[] = new byte[256];
        for(int i = 0; i < 256; i++){
            lookupTable[i] = (byte)(K*(Math.pow(i-m, 2)/100));
        }
        
        ByteLookupTable slt = new ByteLookupTable(0,lookupTable);
        return slt;
    }
    
    /**
     * Method to create a LookupTable with a threshold to limit the function.
     * 
     * @param threshold
     * @return LookupTable
     */
    public LookupTable thresholdFunctionTable(int threshold){
        double Max = Math.log(128.0 + 1.0);
        double K = 128.0/Max;
 
        byte lookupTable[] = new byte[256];
        
        for(int i = 0; i < 128; i++)
            lookupTable[i] = (byte)(K*(Math.log(1.0 + i)));
        for(int i = 128; i < 256; i++)
            lookupTable[i] = (byte)i;
        
        
        ByteLookupTable slt = new ByteLookupTable(0,lookupTable);
        return slt;
    }
    
    /**
     * Method to scale the image to the meassure of the canvas
     * 
     * @param scaledWidth
     * @param scaledHeight
     */
    public void scaleImage(float scaledWidth, float scaledHeight)
    {    
        if(internalFrame != null){
            try{
                BufferedImage image = internalFrame.getCanvas2D().getImage(false);
                BufferedImage scaledImage = new BufferedImage(
                        (int)(image.getWidth()*scaledWidth),
                        (int)(image.getHeight()*scaledHeight),
                        image.getType()
                );

                AffineTransform affineTransform = new AffineTransform();
                affineTransform.scale(scaledWidth, scaledHeight);
                AffineTransformOp scaleOp = new AffineTransformOp(
                        affineTransform, AffineTransformOp.TYPE_BILINEAR);

                internalFrame.getCanvas2D().setImage(
                        scaleOp.filter(image, scaledImage));

                desktop.repaint();
            } catch(IllegalArgumentException e){
                System.err.println(e.getLocalizedMessage());
            }
        }
    }
    
    /***************************** PRIVATE METHODS ***************************/
    
    /**
     * Method to set the toggle buttons (GeneralPath, Line, Rectangle,
     * Ellipse, Selector) to non selected.
     * 
     */
    private void toolsSelectedToFalse()
    {
        generalPath.setSelected(false);
        line.setSelected(false);
        rectangle.setSelected(false);
        ellipse.setSelected(false);
        selector.setSelected(false);
        if(internalFrame != null)
            internalFrame.getCanvas2D().setSelectorMode(false);
    }
    
    /**
     * Method to create a new internal frame with an initializate canvas in
     * the desktop
     * 
     */
    private void newCanvas()
    {
        InternalFrame iF = new InternalFrame();
        desktop.add(iF);
        iF.setVisible(true);
        
        initializeCanvas(iF);
        internalFrame = iF;
    }
    
    /**
     * Method to open a new internal frame with a image selected and an 
     * initializate canvas in the desktop
     * 
     */
    private void openCanvas()
    {
        JFileChooser dlg = new JFileChooser();
        dlg.setFileFilter(new FileNameExtensionFilter(
                "Imagenes [jpg, bmp, gif, png, jpeg, wbmp]",
                "jpg", "bmp", "gif", "png", "jpeg", "wbmp"));
        int resp = dlg.showOpenDialog(this);
        if( resp == JFileChooser.APPROVE_OPTION) {
            try{
                File file = dlg.getSelectedFile();
                BufferedImage image = ImageIO.read(file);
                InternalFrame iF = new InternalFrame();
                this.desktop.add(iF);
                iF.setTitle(file.getName());
                iF.setVisible(true);
                initializeCanvas(iF);
                internalFrame = iF;
                // Dialog to set meassure of the canvas
                throwDialogMeassureAndSetImage(
                        image.getWidth(), image.getHeight());
                iF.getCanvas2D().setImage(image);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(
                        null, 
                        "Error al leer la imagen: " + ex.getLocalizedMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Method to save the active canvas to an image.
     * 
     */
    private void saveCanvas()
    {
        if (internalFrame != null) {
            BufferedImage image = internalFrame.getCanvas2D().getImage(true);
            if (image != null) {
                JFileChooser dlg = new JFileChooser();
                dlg.setFileFilter(new FileNameExtensionFilter(
                        "Imagenes [jpg, bmp, gif, png, jpeg, wbmp]",
                        "jpg", "bmp", "gif", "png", "jpeg", "wbmp"));
                int resp = dlg.showSaveDialog(this);
                if (resp == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = dlg.getSelectedFile();
                        ImageIO.write(image, this.getExtension(
                                file.getName()), file);
                        internalFrame.setTitle(file.getName());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                null, 
                                "Error al guardar la imagen", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    
    /**
     * Methods to initialize the options of the canvas.
     * 
     */
    private void initializeCanvas(InternalFrame iF)
    {
        // Assign the listeners
        iF.addInternalFrameListener(internalFrameListener);
        iF.getCanvas2D().addMouseMotionListener(mouseMotionListener);
        
        // Initialize the options
        iF.getCanvas2D().setCursor(
                        new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        if(selector.isSelected()){
            iF.getCanvas2D().setSelectorMode(true);
            iF.getCanvas2D().setCursor(
                        new java.awt.Cursor(java.awt.Cursor.MOVE_CURSOR));
        }else if(generalPath.isSelected()){
            iF.getCanvas2D().setActiveShape(Canvas2D.EnumShape.GENERALPATH);
        }else if(line.isSelected()){
            iF.getCanvas2D().setActiveShape(Canvas2D.EnumShape.LINE);
        }else if(rectangle.isSelected()){
            iF.getCanvas2D().setActiveShape(Canvas2D.EnumShape.RECTANGLE);
        }else if(ellipse.isSelected()){
            iF.getCanvas2D().setActiveShape(Canvas2D.EnumShape.ELLIPSE);
        }
        
        iF.getCanvas2D().setActiveColor((Color)colors.getSelectedItem());
        iF.getCanvas2D().setWidthStroke((int)widthStroke.getValue());
        iF.getCanvas2D().setFillMode(fill.isSelected());
        iF.getCanvas2D().setTransparencyMode(transparency.isSelected());
        if(transparency.isSelected()){
            iF.getCanvas2D().setActiveComposite(
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }else{
            iF.getCanvas2D().setActiveComposite(
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        iF.getCanvas2D().setAntialiasingMode(antialiasing.isSelected());
        iF.getCanvas2D().setWindowsEffectMode(windowsEffect.isSelected());
        
        // Canvas Background
        iF.getCanvas2D().setImage(new BufferedImage(
                canvasWidth,canvasHeight,BufferedImage.TYPE_INT_ARGB));
        iF.getCanvas2D().getImage(false).getGraphics().setColor(Color.WHITE);
        iF.getCanvas2D().getImage(false).getGraphics().fillRect(
                0, 0, canvasWidth, canvasHeight);
    }
    
    /**
     * Methods to throw the SizeDialog and set the image with the meassurement
     * defined.
     * 
     * @param width Width input.
     * @param height Height input.
     */
    private void throwDialogMeassureAndSetImage(int width, int height)
    {
        SizeDialog sizeDialog = new SizeDialog(this, true, width, height);
        sizeDialog.setMainFrame(this);
        sizeDialog.setVisible(true);
    }
    
    /**
     * Gets the file extension of the string passed.
     * 
     * @param fileName Name of the file.
     * 
     * @return String
     */
    private String getExtension(String fileName)
    {
        String extension = "";
        
        if(!fileName.equals("")){
            int index = fileName.indexOf(".");
            if(index != -1)
                extension = fileName.substring(index + 1);
            else // Default extension
                extension = "jpg";
        }
        
        return extension;
    }
    
    /**
     * Method that is activated when any controls' focus is gained.
     * 
     */
    private void controlsFocusGained()
    {
        if(internalFrame != null){
            if(tipOverFilters.isSelected())
                this.tipOverShapes();
            
            ColorModel colorModel = 
                    internalFrame.getCanvas2D().getImage(false).getColorModel();
            WritableRaster raster = 
                    internalFrame.getCanvas2D().getImage(false).copyData(null);
            boolean alphaPre = 
                    internalFrame.getCanvas2D().getImage(false).isAlphaPremultiplied();
            sourceImage = new BufferedImage(colorModel, raster, alphaPre, null);
        }
    }
    
    /**
     * Method to tip over the shapes on the canvas.
     */
    private void tipOverShapes()
    {
        if(internalFrame != null){
            internalFrame.getCanvas2D().setImage(
                    internalFrame.getCanvas2D().getImage(true));
            internalFrame.getCanvas2D().clearShapesArray();
        }
    }
    
    /**
     * Method to get the Kernel from ComboBox selection.
     * 
     * @param index
     * @return Kernel
     */
    private Kernel getKernelFromSelect(int index)
    {       
        return new Kernel(
                filters[index].getNumRows(), 
                filters[index].getNumColums(), 
                filters[index].getMask()
        );
    }
    
    /**
     * Method to apply the LookupTable to the canvas' image of 
     * the actual internal frame.
     * 
     * @param lookupTable
     */
    private void applyLookup(LookupTable lookupTable)
    {
        if (internalFrame != null) {
            if(sourceImage != null){
                try{
                    LookupOp lookupOP = new LookupOp(lookupTable, null);
                    lookupOP.filter(
                        sourceImage, 
                        internalFrame.getCanvas2D().getImage(false)
                    );
                    desktop.repaint();
                } catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }
    
    /**
     * Method to apply rotation to the canvas' image of the actual
     * internal frame.
     * 
     * @param degrees Degrees to apply in rotation.
     */
    private void applyRotation(int degrees)
    {
        if(internalFrame != null){
            try{
                if(sourceImage != null){
                    double radians = Math.toRadians(degrees);
                    Point p = new Point(sourceImage.getWidth()/2, sourceImage.getHeight()/2);
                    AffineTransform at = AffineTransform.getRotateInstance(radians, p.x, p.y);
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(at,AffineTransformOp.TYPE_BILINEAR);
                    internalFrame.getCanvas2D().setImage(atop.filter(sourceImage, null));
                    desktop.repaint();
                }
            } catch(IllegalArgumentException e){
                System.err.println(e.getLocalizedMessage());
            }
        }
    }
    
    /**
     * Method to apply the band combine operator to the canvas' image of the actual
     * internal frame.
     * 
     * @param matrix 
     */
    private void applyCombineOp(float[][] matrix)
    {
        if(internalFrame != null){
            if(sourceImage != null){
                try{
                    BandCombineOp bandCombineop = new BandCombineOp(matrix, null);
                    bandCombineop.filter(sourceImage.getRaster(), sourceImage.getRaster());
                    internalFrame.getCanvas2D().setImage(sourceImage);
                    desktop.repaint();
                } catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }
    
    /**
     * Method to get the image's band of a image.
     * 
     * @param image
     * @param band
     * @return 
     */
    private BufferedImage getImageBand(BufferedImage image, int band) {
        ColorSpace colorSpace = new sm.image.color.GreyColorSpace();
        ComponentColorModel colorModel = new ComponentColorModel(
                colorSpace, false, false,Transparency.OPAQUE,DataBuffer.TYPE_BYTE
        );
        
        int vband[] = {band};
        WritableRaster bRaster = 
                (WritableRaster)image.getRaster().createWritableChild(
                        0, 0, image.getWidth(), image.getHeight(),0,0,vband
                );
        
        return new BufferedImage(colorModel, bRaster, false, null);
    }
    
    /******************************** HANDLERS *******************************/
    
    private class MouseMotionHandler extends MouseAdapter
    {
        
        /**
         * 
         * @param e 
         */
        @Override
        public void mouseMoved(MouseEvent e)
        {
            if(internalFrame != null){
                setStatusBarText(e.getPoint());
                if(windowsEffect.isSelected()){
                    internalFrame.getCanvas2D().setWindowsEffectPosition(
                            e.getPoint());
                }
                internalFrame.getCanvas2D().repaint();
            }
        }
        
        /**
         * 
         * @param e 
         */
        @Override
        public void mouseDragged(MouseEvent e)
        {
            if(internalFrame != null){
                setStatusBarText(e.getPoint());
                if (windowsEffect.isSelected()) {
                    internalFrame.getCanvas2D().setWindowsEffectPosition(
                            e.getPoint());
                }
                internalFrame.getCanvas2D().repaint();
            }
        }
        
        private void setStatusBarText(Point p)
        {
            statusBarVariable.setText(
                    "Coordenadas: ( " + (int)p.getX() + ", " +
                    (int)p.getY() + " );"  
            );
            BufferedImage image = internalFrame.getCanvas2D().getImage(true);
            if(p.getX() >= 0 && p.getX() < image.getWidth() &&
                    p.getY() >= 0 && p.getY() < image.getHeight()){
                Color pixelColor = new Color(
                    image.getRGB((int)p.getX(), (int)p.getY()), true);
                statusBarVariable.setText(
                        statusBarVariable.getText() +
                        "  RGB: ( " + pixelColor.getRed() +
                        ", " + pixelColor.getGreen() + ", " + pixelColor.getBlue() +
                        " )"
                );
            }else{
                statusBarVariable.setText(
                        statusBarVariable.getText() +
                        "  RGB: Fuera de Imagen"
                );
            }           
        }
        
    }
    
    private class InternalFrameHandler extends InternalFrameAdapter
    {
        
        /**
         * 
         * @param evt 
         */
        @Override
        public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt)
        {
            internalFrame = (InternalFrame)evt.getInternalFrame();
            
            if(internalFrame.getCanvas2D().getSelectorMode()){
                toolsSelectedToFalse();
                selector.setSelected(true);
                internalFrame.getCanvas2D().setSelectorMode(true);
                internalFrame.getCanvas2D().setCursor(
                        new java.awt.Cursor(java.awt.Cursor.MOVE_CURSOR));
            }else{
                toolsSelectedToFalse();
                switch(internalFrame.getCanvas2D().getActiveShape()){
                    case GENERALPATH:
                        generalPath.setSelected(true);
                        break;
                    case LINE:
                        line.setSelected(true);
                        break;
                    case RECTANGLE:
                        rectangle.setSelected(true);
                        break;
                    case ELLIPSE:
                        ellipse.setSelected(true);
                        break;
                }
                internalFrame.getCanvas2D().setCursor(new java.awt.Cursor(
                        java.awt.Cursor.CROSSHAIR_CURSOR));
            }
            colors.setSelectedItem(internalFrame.getCanvas2D().getActiveColor());
            widthStroke.setValue((int)internalFrame.getCanvas2D().getWidthStroke());
            fill.setSelected(internalFrame.getCanvas2D().getFillMode());
            transparency.setSelected(internalFrame.getCanvas2D().getTransparencyMode());
            antialiasing.setSelected(internalFrame.getCanvas2D().getAntialiasingMode());
            windowsEffect.setSelected(internalFrame.getCanvas2D().getWindowsEffectMode());
        }
     
    }
    
    /************************** JAVA GENERATED CODE **************************/
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        toolBar = new javax.swing.JToolBar();
        newCanvas = new javax.swing.JButton();
        openCanvas = new javax.swing.JButton();
        saveCanvas = new javax.swing.JButton();
        separator2 = new javax.swing.JToolBar.Separator();
        generalPath = new javax.swing.JToggleButton();
        line = new javax.swing.JToggleButton();
        rectangle = new javax.swing.JToggleButton();
        ellipse = new javax.swing.JToggleButton();
        selector = new javax.swing.JToggleButton();
        separator1 = new javax.swing.JToolBar.Separator();
        colors = new javax.swing.JComboBox<>(colorsArray);
        jSeparator1 = new javax.swing.JToolBar.Separator();
        widthStroke = new javax.swing.JSpinner();
        fill = new javax.swing.JToggleButton();
        transparency = new javax.swing.JToggleButton();
        antialiasing = new javax.swing.JToggleButton();
        tipOver = new javax.swing.JButton();
        windowsEffect = new javax.swing.JToggleButton();
        tipOverFilters = new javax.swing.JToggleButton();
        duplicate = new javax.swing.JButton();
        statusBar = new javax.swing.JPanel();
        statusBarTitle = new javax.swing.JLabel();
        statusBarVariable = new javax.swing.JLabel();
        containerPanel = new javax.swing.JPanel();
        toolBarImages = new javax.swing.JToolBar();
        toolsPanel = new javax.swing.JPanel();
        brightPanel = new javax.swing.JPanel();
        brightnessSlider = new javax.swing.JSlider();
        filterPanel = new javax.swing.JPanel();
        filterComboBox = new javax.swing.JComboBox<>();
        contrastPanel = new javax.swing.JPanel();
        contrast = new javax.swing.JButton();
        illuminate = new javax.swing.JButton();
        darken = new javax.swing.JButton();
        negative = new javax.swing.JButton();
        quadraticFunctionPanel = new javax.swing.JPanel();
        quadraticFunctionSlider = new javax.swing.JSlider();
        quadraticFunction = new javax.swing.JButton();
        yellowBandCombination = new javax.swing.JButton();
        greenBandCombination = new javax.swing.JButton();
        tint = new javax.swing.JButton();
        sepia = new javax.swing.JButton();
        equalization = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        posterizeSlider = new javax.swing.JSlider();
        rotationPanel = new javax.swing.JPanel();
        rotationSlider = new javax.swing.JSlider();
        ninetyDegreesRotation = new javax.swing.JButton();
        hundredEightyDegreesRotation = new javax.swing.JButton();
        twoHundredSeventyDegreesRotation = new javax.swing.JButton();
        scalePanel = new javax.swing.JPanel();
        scaleIn = new javax.swing.JButton();
        scaleOut = new javax.swing.JButton();
        thresholdFunctionPanel = new javax.swing.JPanel();
        thresholdFunction = new javax.swing.JButton();
        spaceColorPanel = new javax.swing.JPanel();
        bandExtractor = new javax.swing.JButton();
        colorSpaceComboBox = new javax.swing.JComboBox<>();
        desktop = new javax.swing.JDesktopPane();
        menu = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenu = new javax.swing.JMenuItem();
        openMenu = new javax.swing.JMenuItem();
        saveMenu = new javax.swing.JMenuItem();
        separatorFile = new javax.swing.JPopupMenu.Separator();
        printMenu = new javax.swing.JMenu();
        printerMenu = new javax.swing.JMenuItem();
        printerFileMenu = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        copyMenu = new javax.swing.JMenuItem();
        cutMenu = new javax.swing.JMenuItem();
        pasteMenu = new javax.swing.JMenuItem();
        imageMenu = new javax.swing.JMenu();
        newSizeImage = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        statusBarMenu = new javax.swing.JCheckBoxMenuItem();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Práctica 12");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        toolBar.setRollover(true);

        newCanvas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/nuevo.png"))); // NOI18N
        newCanvas.setToolTipText("Nuevo lienzo");
        newCanvas.setFocusable(false);
        newCanvas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newCanvas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newCanvas.addActionListener(formListener);
        toolBar.add(newCanvas);

        openCanvas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/abrir.png"))); // NOI18N
        openCanvas.setToolTipText("Abrir lienzo");
        openCanvas.setFocusable(false);
        openCanvas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openCanvas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openCanvas.addActionListener(formListener);
        toolBar.add(openCanvas);

        saveCanvas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/guardar.png"))); // NOI18N
        saveCanvas.setToolTipText("Guardar lienzo");
        saveCanvas.setFocusable(false);
        saveCanvas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveCanvas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveCanvas.addActionListener(formListener);
        toolBar.add(saveCanvas);
        toolBar.add(separator2);

        generalPath.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/punto.png"))); // NOI18N
        generalPath.setToolTipText("Dibujar libre");
        generalPath.setFocusable(false);
        generalPath.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        generalPath.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        generalPath.addActionListener(formListener);
        toolBar.add(generalPath);

        line.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/linea.png"))); // NOI18N
        line.setToolTipText("Dibujar línea");
        line.setFocusable(false);
        line.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        line.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        line.addActionListener(formListener);
        toolBar.add(line);
        line.getAccessibleContext().setAccessibleDescription("Línea");

        rectangle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/rectangulo.png"))); // NOI18N
        rectangle.setToolTipText("Dibujar rectángulo");
        rectangle.setFocusable(false);
        rectangle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rectangle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rectangle.addActionListener(formListener);
        toolBar.add(rectangle);
        rectangle.getAccessibleContext().setAccessibleDescription("Rectángulo");

        ellipse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/elipse.png"))); // NOI18N
        ellipse.setToolTipText("Dibujar elipse");
        ellipse.setFocusable(false);
        ellipse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ellipse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ellipse.addActionListener(formListener);
        toolBar.add(ellipse);
        ellipse.getAccessibleContext().setAccessibleDescription("Elipse");

        selector.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/seleccion.png"))); // NOI18N
        selector.setToolTipText("Mover figura");
        selector.setFocusable(false);
        selector.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selector.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selector.addActionListener(formListener);
        toolBar.add(selector);
        selector.getAccessibleContext().setAccessibleDescription("Mover");

        toolBar.add(separator1);

        colors.setToolTipText("Seleccionar color");
        colors.setMaximumSize(new java.awt.Dimension(56, 32767));
        colors.setRenderer(new ColorCellRender());
        colors.setRequestFocusEnabled(false);
        colors.addActionListener(formListener);
        toolBar.add(colors);
        colors.getAccessibleContext().setAccessibleDescription("Color");

        toolBar.add(jSeparator1);

        widthStroke.setToolTipText("Cambiar grosor del trazo");
        widthStroke.setBorder(null);
        widthStroke.setMaximumSize(new java.awt.Dimension(60, 30));
        widthStroke.setMinimumSize(new java.awt.Dimension(50, 30));
        widthStroke.setPreferredSize(new java.awt.Dimension(50, 30));
        widthStroke.addChangeListener(formListener);
        toolBar.add(widthStroke);
        widthStroke.getAccessibleContext().setAccessibleDescription("Grosor");

        fill.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/rellenar.png"))); // NOI18N
        fill.setToolTipText("Rellenar figura");
        fill.setFocusable(false);
        fill.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fill.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fill.addActionListener(formListener);
        toolBar.add(fill);
        fill.getAccessibleContext().setAccessibleDescription("Relleno");

        transparency.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/transparencia.png"))); // NOI18N
        transparency.setToolTipText("Aplicar transparencia a la figura");
        transparency.setFocusable(false);
        transparency.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        transparency.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        transparency.addActionListener(formListener);
        toolBar.add(transparency);
        transparency.getAccessibleContext().setAccessibleDescription("Transparencia");

        antialiasing.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/alisar.png"))); // NOI18N
        antialiasing.setToolTipText("Alisar figuras");
        antialiasing.setFocusable(false);
        antialiasing.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        antialiasing.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        antialiasing.addActionListener(formListener);
        toolBar.add(antialiasing);
        antialiasing.getAccessibleContext().setAccessibleDescription("Alisar");

        tipOver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/download.png"))); // NOI18N
        tipOver.setToolTipText("Volcar figuras en la imagen");
        tipOver.setFocusable(false);
        tipOver.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tipOver.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tipOver.addActionListener(formListener);
        toolBar.add(tipOver);

        windowsEffect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/view.png"))); // NOI18N
        windowsEffect.setToolTipText("Efecto ventana");
        windowsEffect.setFocusable(false);
        windowsEffect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        windowsEffect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        windowsEffect.addActionListener(formListener);
        toolBar.add(windowsEffect);

        tipOverFilters.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volcar.png"))); // NOI18N
        tipOverFilters.setToolTipText("Aplicar filtros a figuras");
        tipOverFilters.setFocusable(false);
        tipOverFilters.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tipOverFilters.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(tipOverFilters);

        duplicate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/duplicate.png"))); // NOI18N
        duplicate.setToolTipText("Duplicar");
        duplicate.setFocusable(false);
        duplicate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        duplicate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        duplicate.addActionListener(formListener);
        toolBar.add(duplicate);

        getContentPane().add(toolBar, java.awt.BorderLayout.PAGE_START);

        statusBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        statusBarTitle.setText("Barra de Estado");

        javax.swing.GroupLayout statusBarLayout = new javax.swing.GroupLayout(statusBar);
        statusBar.setLayout(statusBarLayout);
        statusBarLayout.setHorizontalGroup(
            statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusBarTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1716, Short.MAX_VALUE)
                .addComponent(statusBarVariable)
                .addGap(21, 21, 21))
        );
        statusBarLayout.setVerticalGroup(
            statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(statusBarTitle)
                .addComponent(statusBarVariable))
        );

        getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);

        containerPanel.setLayout(new java.awt.BorderLayout());

        toolsPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        brightPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Brillo"));

        brightnessSlider.setMinimum(-100);
        brightnessSlider.setToolTipText("Brillo");
        brightnessSlider.setValue(0);
        brightnessSlider.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        brightnessSlider.addChangeListener(formListener);
        brightnessSlider.addFocusListener(formListener);

        javax.swing.GroupLayout brightPanelLayout = new javax.swing.GroupLayout(brightPanel);
        brightPanel.setLayout(brightPanelLayout);
        brightPanelLayout.setHorizontalGroup(
            brightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(brightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(brightnessSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        brightPanelLayout.setVerticalGroup(
            brightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, brightPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(brightnessSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        filterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        filterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Media", "Suavizado", "Realce", "Fronteras", "Enborronamiento Horizontal", "Enborronamiento Diagonal", "Binomial", "Foco Intenso", "Foco", "Laplaciana", "Relieve", "SolbeX", "SolbeY", " " }));
        filterComboBox.setToolTipText("Filtros");
        filterComboBox.addFocusListener(formListener);
        filterComboBox.addActionListener(formListener);

        javax.swing.GroupLayout filterPanelLayout = new javax.swing.GroupLayout(filterPanel);
        filterPanel.setLayout(filterPanelLayout);
        filterPanelLayout.setHorizontalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        filterPanelLayout.setVerticalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, filterPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(filterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        contrastPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Contraste"));

        contrast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/contraste.png"))); // NOI18N
        contrast.setToolTipText("Contraste");
        contrast.addActionListener(formListener);

        illuminate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iluminar.png"))); // NOI18N
        illuminate.setToolTipText("Iluminar");
        illuminate.addActionListener(formListener);

        darken.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/oscurecer.png"))); // NOI18N
        darken.setToolTipText("Oscurecer");
        darken.addActionListener(formListener);

        negative.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/contrast.png"))); // NOI18N
        negative.setToolTipText("Negativo");
        negative.addActionListener(formListener);

        javax.swing.GroupLayout contrastPanelLayout = new javax.swing.GroupLayout(contrastPanel);
        contrastPanel.setLayout(contrastPanelLayout);
        contrastPanelLayout.setHorizontalGroup(
            contrastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contrastPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(contrast, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(illuminate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(darken, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(negative, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        contrastPanelLayout.setVerticalGroup(
            contrastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contrastPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(contrastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contrast, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(illuminate, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(darken, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(negative, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        quadraticFunctionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), " "));

        quadraticFunctionSlider.setMajorTickSpacing(50);
        quadraticFunctionSlider.setMaximum(255);
        quadraticFunctionSlider.setMinorTickSpacing(10);
        quadraticFunctionSlider.setPaintTicks(true);
        quadraticFunctionSlider.setToolTipText("Función Cuadrática");
        quadraticFunctionSlider.setValue(0);
        quadraticFunctionSlider.addChangeListener(formListener);
        quadraticFunctionSlider.addFocusListener(formListener);

        quadraticFunction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cuadratica.png"))); // NOI18N
        quadraticFunction.setToolTipText("Cuadrática");
        quadraticFunction.addActionListener(formListener);

        yellowBandCombination.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/combinar.png"))); // NOI18N
        yellowBandCombination.addActionListener(formListener);

        greenBandCombination.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/paint-bucket.png"))); // NOI18N
        greenBandCombination.setToolTipText("Enverdecer");
        greenBandCombination.addActionListener(formListener);

        tint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/tintar.png"))); // NOI18N
        tint.setToolTipText("Tinte");
        tint.addActionListener(formListener);

        sepia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/sepia.png"))); // NOI18N
        sepia.setToolTipText("sepia");
        sepia.addActionListener(formListener);

        equalization.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ecualizar.png"))); // NOI18N
        equalization.setToolTipText("Ecualizar");
        equalization.addActionListener(formListener);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/rojo.png"))); // NOI18N
        jButton2.setToolTipText("");
        jButton2.addActionListener(formListener);

        posterizeSlider.setMaximum(30);
        posterizeSlider.setMinimum(1);
        posterizeSlider.setValue(30);
        posterizeSlider.addChangeListener(formListener);
        posterizeSlider.addFocusListener(formListener);

        javax.swing.GroupLayout quadraticFunctionPanelLayout = new javax.swing.GroupLayout(quadraticFunctionPanel);
        quadraticFunctionPanel.setLayout(quadraticFunctionPanelLayout);
        quadraticFunctionPanelLayout.setHorizontalGroup(
            quadraticFunctionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(quadraticFunctionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(quadraticFunctionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(quadraticFunction, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yellowBandCombination, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(greenBandCombination, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tint, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sepia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(equalization, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(posterizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        quadraticFunctionPanelLayout.setVerticalGroup(
            quadraticFunctionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, quadraticFunctionPanelLayout.createSequentialGroup()
                .addGroup(quadraticFunctionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(posterizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(quadraticFunctionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(yellowBandCombination, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(quadraticFunction, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(quadraticFunctionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(sepia, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(tint, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(greenBandCombination, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(equalization, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(43, 43, 43))
        );

        rotationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Rotación"));

        rotationSlider.setMajorTickSpacing(180);
        rotationSlider.setMaximum(360);
        rotationSlider.setMinimum(-360);
        rotationSlider.setMinorTickSpacing(60);
        rotationSlider.setPaintTicks(true);
        rotationSlider.setToolTipText("Aplicar rotación");
        rotationSlider.setValue(0);
        rotationSlider.addChangeListener(formListener);
        rotationSlider.addFocusListener(formListener);

        ninetyDegreesRotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/rotacion90.png"))); // NOI18N
        ninetyDegreesRotation.setToolTipText("Rotar 90");
        ninetyDegreesRotation.addActionListener(formListener);

        hundredEightyDegreesRotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/rotacion180.png"))); // NOI18N
        hundredEightyDegreesRotation.setToolTipText("Rotar 180");
        hundredEightyDegreesRotation.addActionListener(formListener);

        twoHundredSeventyDegreesRotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/rotacion270.png"))); // NOI18N
        twoHundredSeventyDegreesRotation.setToolTipText("Rotar 270");
        twoHundredSeventyDegreesRotation.addActionListener(formListener);

        javax.swing.GroupLayout rotationPanelLayout = new javax.swing.GroupLayout(rotationPanel);
        rotationPanel.setLayout(rotationPanelLayout);
        rotationPanelLayout.setHorizontalGroup(
            rotationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rotationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rotationSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ninetyDegreesRotation, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hundredEightyDegreesRotation, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(twoHundredSeventyDegreesRotation, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        rotationPanelLayout.setVerticalGroup(
            rotationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rotationPanelLayout.createSequentialGroup()
                .addGroup(rotationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rotationSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ninetyDegreesRotation)
                    .addComponent(hundredEightyDegreesRotation)
                    .addComponent(twoHundredSeventyDegreesRotation))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        scalePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Escala"));

        scaleIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/aumentar.png"))); // NOI18N
        scaleIn.setToolTipText("Aumentar escala");
        scaleIn.addActionListener(formListener);

        scaleOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/disminuir.png"))); // NOI18N
        scaleOut.setToolTipText("Disminuir escala");
        scaleOut.addActionListener(formListener);

        javax.swing.GroupLayout scalePanelLayout = new javax.swing.GroupLayout(scalePanel);
        scalePanel.setLayout(scalePanelLayout);
        scalePanelLayout.setHorizontalGroup(
            scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scalePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scaleIn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scaleOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        scalePanelLayout.setVerticalGroup(
            scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scalePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scaleOut, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scaleIn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        thresholdFunctionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Reto P10: Umbral"));

        thresholdFunction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/diagram.png"))); // NOI18N
        thresholdFunction.setToolTipText("Umbral");
        thresholdFunction.addActionListener(formListener);

        javax.swing.GroupLayout thresholdFunctionPanelLayout = new javax.swing.GroupLayout(thresholdFunctionPanel);
        thresholdFunctionPanel.setLayout(thresholdFunctionPanelLayout);
        thresholdFunctionPanelLayout.setHorizontalGroup(
            thresholdFunctionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(thresholdFunctionPanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(thresholdFunction, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );
        thresholdFunctionPanelLayout.setVerticalGroup(
            thresholdFunctionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, thresholdFunctionPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(thresholdFunction, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        spaceColorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Color"));

        bandExtractor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/bandas.png"))); // NOI18N
        bandExtractor.setToolTipText("Bandas");
        bandExtractor.addActionListener(formListener);

        colorSpaceComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "sRGB", "YCC", "Grey", "YCbCr" }));
        colorSpaceComboBox.setToolTipText("Espacio de color");
        colorSpaceComboBox.addActionListener(formListener);

        javax.swing.GroupLayout spaceColorPanelLayout = new javax.swing.GroupLayout(spaceColorPanel);
        spaceColorPanel.setLayout(spaceColorPanelLayout);
        spaceColorPanelLayout.setHorizontalGroup(
            spaceColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spaceColorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bandExtractor, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorSpaceComboBox, 0, 80, Short.MAX_VALUE)
                .addContainerGap())
        );
        spaceColorPanelLayout.setVerticalGroup(
            spaceColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spaceColorPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(spaceColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(colorSpaceComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bandExtractor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout toolsPanelLayout = new javax.swing.GroupLayout(toolsPanel);
        toolsPanel.setLayout(toolsPanelLayout);
        toolsPanelLayout.setHorizontalGroup(
            toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(brightPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contrastPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(quadraticFunctionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spaceColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rotationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scalePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(thresholdFunctionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        toolsPanelLayout.setVerticalGroup(
            toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolsPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(thresholdFunctionPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scalePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rotationPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(spaceColorPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(quadraticFunctionPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(contrastPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(brightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(filterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        toolBarImages.add(toolsPanel);

        containerPanel.add(toolBarImages, java.awt.BorderLayout.SOUTH);

        desktop.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        desktop.setPreferredSize(new java.awt.Dimension(800, 600));

        javax.swing.GroupLayout desktopLayout = new javax.swing.GroupLayout(desktop);
        desktop.setLayout(desktopLayout);
        desktopLayout.setHorizontalGroup(
            desktopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1826, Short.MAX_VALUE)
        );
        desktopLayout.setVerticalGroup(
            desktopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        containerPanel.add(desktop, java.awt.BorderLayout.CENTER);

        getContentPane().add(containerPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText("Archivo");

        newMenu.setText("Nuevo");
        newMenu.addActionListener(formListener);
        fileMenu.add(newMenu);

        openMenu.setText("Abrir");
        openMenu.addActionListener(formListener);
        fileMenu.add(openMenu);

        saveMenu.setText("Guardar");
        saveMenu.addActionListener(formListener);
        fileMenu.add(saveMenu);
        fileMenu.add(separatorFile);

        printMenu.setText("Imprimir");

        printerMenu.setText("Impresora");
        printMenu.add(printerMenu);

        printerFileMenu.setText("Fichero");
        printMenu.add(printerFileMenu);

        fileMenu.add(printMenu);

        menu.add(fileMenu);

        editMenu.setText("Editar");

        copyMenu.setText("Copiar");
        editMenu.add(copyMenu);

        cutMenu.setText("Cortar");
        editMenu.add(cutMenu);

        pasteMenu.setText("Pegar");
        editMenu.add(pasteMenu);

        menu.add(editMenu);

        imageMenu.setText("Imagen");

        newSizeImage.setText("Tamaño nueva imagen");
        newSizeImage.addActionListener(formListener);
        imageMenu.add(newSizeImage);

        menu.add(imageMenu);

        viewMenu.setText("Ver");

        statusBarMenu.setSelected(true);
        statusBarMenu.setText("Barra de estado");
        statusBarMenu.addActionListener(formListener);
        viewMenu.add(statusBarMenu);

        menu.add(viewMenu);

        setJMenuBar(menu);

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.FocusListener, javax.swing.event.ChangeListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == newCanvas) {
                MainFrame.this.newCanvasActionPerformed(evt);
            }
            else if (evt.getSource() == openCanvas) {
                MainFrame.this.openCanvasActionPerformed(evt);
            }
            else if (evt.getSource() == saveCanvas) {
                MainFrame.this.saveCanvasActionPerformed(evt);
            }
            else if (evt.getSource() == generalPath) {
                MainFrame.this.generalPathActionPerformed(evt);
            }
            else if (evt.getSource() == line) {
                MainFrame.this.lineActionPerformed(evt);
            }
            else if (evt.getSource() == rectangle) {
                MainFrame.this.rectangleActionPerformed(evt);
            }
            else if (evt.getSource() == ellipse) {
                MainFrame.this.ellipseActionPerformed(evt);
            }
            else if (evt.getSource() == selector) {
                MainFrame.this.selectorActionPerformed(evt);
            }
            else if (evt.getSource() == colors) {
                MainFrame.this.colorsActionPerformed(evt);
            }
            else if (evt.getSource() == fill) {
                MainFrame.this.fillActionPerformed(evt);
            }
            else if (evt.getSource() == transparency) {
                MainFrame.this.transparencyActionPerformed(evt);
            }
            else if (evt.getSource() == antialiasing) {
                MainFrame.this.antialiasingActionPerformed(evt);
            }
            else if (evt.getSource() == tipOver) {
                MainFrame.this.tipOverActionPerformed(evt);
            }
            else if (evt.getSource() == windowsEffect) {
                MainFrame.this.windowsEffectActionPerformed(evt);
            }
            else if (evt.getSource() == duplicate) {
                MainFrame.this.duplicateActionPerformed(evt);
            }
            else if (evt.getSource() == filterComboBox) {
                MainFrame.this.filterComboBoxActionPerformed(evt);
            }
            else if (evt.getSource() == contrast) {
                MainFrame.this.contrastActionPerformed(evt);
            }
            else if (evt.getSource() == illuminate) {
                MainFrame.this.illuminateActionPerformed(evt);
            }
            else if (evt.getSource() == darken) {
                MainFrame.this.darkenActionPerformed(evt);
            }
            else if (evt.getSource() == negative) {
                MainFrame.this.negativeActionPerformed(evt);
            }
            else if (evt.getSource() == quadraticFunction) {
                MainFrame.this.quadraticFunctionActionPerformed(evt);
            }
            else if (evt.getSource() == yellowBandCombination) {
                MainFrame.this.yellowBandCombinationActionPerformed(evt);
            }
            else if (evt.getSource() == greenBandCombination) {
                MainFrame.this.greenBandCombinationActionPerformed(evt);
            }
            else if (evt.getSource() == tint) {
                MainFrame.this.tintActionPerformed(evt);
            }
            else if (evt.getSource() == sepia) {
                MainFrame.this.sepiaActionPerformed(evt);
            }
            else if (evt.getSource() == equalization) {
                MainFrame.this.equalizationActionPerformed(evt);
            }
            else if (evt.getSource() == ninetyDegreesRotation) {
                MainFrame.this.ninetyDegreesRotationActionPerformed(evt);
            }
            else if (evt.getSource() == hundredEightyDegreesRotation) {
                MainFrame.this.hundredEightyDegreesRotationActionPerformed(evt);
            }
            else if (evt.getSource() == twoHundredSeventyDegreesRotation) {
                MainFrame.this.twoHundredSeventyDegreesRotationActionPerformed(evt);
            }
            else if (evt.getSource() == scaleIn) {
                MainFrame.this.scaleInActionPerformed(evt);
            }
            else if (evt.getSource() == scaleOut) {
                MainFrame.this.scaleOutActionPerformed(evt);
            }
            else if (evt.getSource() == thresholdFunction) {
                MainFrame.this.thresholdFunctionActionPerformed(evt);
            }
            else if (evt.getSource() == bandExtractor) {
                MainFrame.this.bandExtractorActionPerformed(evt);
            }
            else if (evt.getSource() == colorSpaceComboBox) {
                MainFrame.this.colorSpaceComboBoxActionPerformed(evt);
            }
            else if (evt.getSource() == newMenu) {
                MainFrame.this.newMenuActionPerformed(evt);
            }
            else if (evt.getSource() == openMenu) {
                MainFrame.this.openMenuActionPerformed(evt);
            }
            else if (evt.getSource() == saveMenu) {
                MainFrame.this.saveMenuActionPerformed(evt);
            }
            else if (evt.getSource() == newSizeImage) {
                MainFrame.this.newSizeImageActionPerformed(evt);
            }
            else if (evt.getSource() == statusBarMenu) {
                MainFrame.this.statusBarMenuActionPerformed(evt);
            }
            else if (evt.getSource() == jButton2) {
                MainFrame.this.jButton2ActionPerformed(evt);
            }
        }

        public void focusGained(java.awt.event.FocusEvent evt) {
            if (evt.getSource() == brightnessSlider) {
                MainFrame.this.brightnessSliderFocusGained(evt);
            }
            else if (evt.getSource() == filterComboBox) {
                MainFrame.this.filterComboBoxFocusGained(evt);
            }
            else if (evt.getSource() == quadraticFunctionSlider) {
                MainFrame.this.quadraticFunctionSliderFocusGained(evt);
            }
            else if (evt.getSource() == rotationSlider) {
                MainFrame.this.rotationSliderFocusGained(evt);
            }
            else if (evt.getSource() == posterizeSlider) {
                MainFrame.this.posterizeSliderFocusGained(evt);
            }
        }

        public void focusLost(java.awt.event.FocusEvent evt) {
            if (evt.getSource() == brightnessSlider) {
                MainFrame.this.brightnessSliderFocusLost(evt);
            }
            else if (evt.getSource() == filterComboBox) {
                MainFrame.this.filterComboBoxFocusLost(evt);
            }
            else if (evt.getSource() == quadraticFunctionSlider) {
                MainFrame.this.quadraticFunctionSliderFocusLost(evt);
            }
            else if (evt.getSource() == rotationSlider) {
                MainFrame.this.rotationSliderFocusLost(evt);
            }
            else if (evt.getSource() == posterizeSlider) {
                MainFrame.this.posterizeSliderFocusLost(evt);
            }
        }

        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            if (evt.getSource() == widthStroke) {
                MainFrame.this.widthStrokeStateChanged(evt);
            }
            else if (evt.getSource() == brightnessSlider) {
                MainFrame.this.brightnessSliderStateChanged(evt);
            }
            else if (evt.getSource() == quadraticFunctionSlider) {
                MainFrame.this.quadraticFunctionSliderStateChanged(evt);
            }
            else if (evt.getSource() == rotationSlider) {
                MainFrame.this.rotationSliderStateChanged(evt);
            }
            else if (evt.getSource() == posterizeSlider) {
                MainFrame.this.posterizeSliderStateChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void newMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuActionPerformed
        newCanvas();
    }//GEN-LAST:event_newMenuActionPerformed

    private void saveMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuActionPerformed
        saveCanvas();
    }//GEN-LAST:event_saveMenuActionPerformed

    private void openMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuActionPerformed
        openCanvas();
    }//GEN-LAST:event_openMenuActionPerformed

    private void generalPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generalPathActionPerformed
        if(internalFrame != null)
            internalFrame.getCanvas2D().setActiveShape(
                    Canvas2D.EnumShape.GENERALPATH);
        toolsSelectedToFalse();
        generalPath.setSelected(true);
        internalFrame.getCanvas2D().setCursor(new java.awt.Cursor(
                java.awt.Cursor.CROSSHAIR_CURSOR));
    }//GEN-LAST:event_generalPathActionPerformed

    private void lineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineActionPerformed
        if(internalFrame != null)
            internalFrame.getCanvas2D().setActiveShape(Canvas2D.EnumShape.LINE);
        toolsSelectedToFalse();
        line.setSelected(true);
        internalFrame.getCanvas2D().setCursor(new java.awt.Cursor(
                java.awt.Cursor.CROSSHAIR_CURSOR));
    }//GEN-LAST:event_lineActionPerformed

    private void rectangleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rectangleActionPerformed
        if(internalFrame != null)
            internalFrame.getCanvas2D().setActiveShape(
                    Canvas2D.EnumShape.RECTANGLE);
        toolsSelectedToFalse();
        rectangle.setSelected(true);
        internalFrame.getCanvas2D().setCursor(new java.awt.Cursor(
                java.awt.Cursor.CROSSHAIR_CURSOR));
    }//GEN-LAST:event_rectangleActionPerformed

    private void ellipseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseActionPerformed
        if(internalFrame != null)
            internalFrame.getCanvas2D().setActiveShape(
                    Canvas2D.EnumShape.ELLIPSE);
        toolsSelectedToFalse();
        ellipse.setSelected(true);
        internalFrame.getCanvas2D().setCursor(new java.awt.Cursor(
                java.awt.Cursor.CROSSHAIR_CURSOR));
    }//GEN-LAST:event_ellipseActionPerformed

    private void statusBarMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusBarMenuActionPerformed
        if(statusBar.isVisible())
            statusBar.setVisible(false);
        else
            statusBar.setVisible(true);
    }//GEN-LAST:event_statusBarMenuActionPerformed

    private void selectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectorActionPerformed
        toolsSelectedToFalse();
        if(internalFrame != null)
            internalFrame.getCanvas2D().setSelectorMode(true);
        selector.setSelected(true);
        internalFrame.getCanvas2D().setCursor(new java.awt.Cursor(
                java.awt.Cursor.MOVE_CURSOR));
    }//GEN-LAST:event_selectorActionPerformed

    private void fillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillActionPerformed
        if(internalFrame != null)
                internalFrame.getCanvas2D().setFillMode(fill.isSelected());
    }//GEN-LAST:event_fillActionPerformed

    private void widthStrokeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_widthStrokeStateChanged
        if((int)widthStroke.getValue() < 1)
            widthStroke.setValue(1);
        else if(internalFrame != null)
            internalFrame.getCanvas2D().setWidthStroke(
                    (int)widthStroke.getValue());
    }//GEN-LAST:event_widthStrokeStateChanged

    private void antialiasingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_antialiasingActionPerformed
        if(internalFrame != null)
            internalFrame.getCanvas2D().setAntialiasingMode(
                    antialiasing.isSelected());
    }//GEN-LAST:event_antialiasingActionPerformed

    private void colorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorsActionPerformed
        if(internalFrame != null)
            internalFrame.getCanvas2D().setActiveColor(
                    colors.getItemAt(colors.getSelectedIndex()));
    }//GEN-LAST:event_colorsActionPerformed

    private void newCanvasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCanvasActionPerformed
        newCanvas();
    }//GEN-LAST:event_newCanvasActionPerformed

    private void openCanvasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCanvasActionPerformed
        openCanvas();
    }//GEN-LAST:event_openCanvasActionPerformed

    private void saveCanvasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCanvasActionPerformed
        saveCanvas();
    }//GEN-LAST:event_saveCanvasActionPerformed

    private void transparencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transparencyActionPerformed
        if(internalFrame != null){
            internalFrame.getCanvas2D().setTransparencyMode(
                    transparency.isSelected());
            if(transparency.isSelected()){
                internalFrame.getCanvas2D().setActiveComposite(
                        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }else{
                internalFrame.getCanvas2D().setActiveComposite(
                        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }  
        }
    }//GEN-LAST:event_transparencyActionPerformed

    private void tipOverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipOverActionPerformed
        tipOverShapes();
    }//GEN-LAST:event_tipOverActionPerformed

    private void windowsEffectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_windowsEffectActionPerformed
        if(internalFrame != null){
            internalFrame.getCanvas2D().setWindowsEffectMode(
                windowsEffect.isSelected());
        }
        repaint();
    }//GEN-LAST:event_windowsEffectActionPerformed

    private void newSizeImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSizeImageActionPerformed
        throwDialogMeassureAndSetImage(canvasWidth, canvasHeight);
        if(internalFrame != null)
            internalFrame.getCanvas2D().setImage(
                    internalFrame.getCanvas2D().getImage(false));
    }//GEN-LAST:event_newSizeImageActionPerformed

    private void brightnessSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_brightnessSliderStateChanged
        if(internalFrame != null){
            try {
                if(sourceImage != null){
                    RescaleOp rescaleOp;
                    int value = brightnessSlider.getValue();
                    
                    if(sourceImage.getColorModel().hasAlpha()){
                        float[] a = {1f, 1f, 1f, 1f};
                        float[] b = {value, value, value, 0f};
                        rescaleOp = new RescaleOp(a, b, null);
                    }else{
                        rescaleOp = new RescaleOp(1.0F, value, null);
                    }
                   
                    rescaleOp.filter(
                            sourceImage,
                            internalFrame.getCanvas2D().getImage(false)
                    );
                    desktop.repaint();
                }
            } catch(IllegalArgumentException e){
                System.err.println(e.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_brightnessSliderStateChanged

    private void brightnessSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_brightnessSliderFocusGained
        if(tipOverFilters.isSelected())
            tipOverShapes();
        this.controlsFocusGained();
    }//GEN-LAST:event_brightnessSliderFocusGained

    private void brightnessSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_brightnessSliderFocusLost
        sourceImage = null;
        brightnessSlider.setValue(0);
    }//GEN-LAST:event_brightnessSliderFocusLost

    private void filterComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterComboBoxActionPerformed
        if(internalFrame != null){
            try {
                Kernel kernel = getKernelFromSelect(filterComboBox.getSelectedIndex());
                ConvolveOp convolveOp = new ConvolveOp(kernel);
                sourceImage = convolveOp.filter(internalFrame.getCanvas2D().getImage(false), null);
                internalFrame.getCanvas2D().setImage(sourceImage);
                desktop.repaint();
            } catch(IllegalArgumentException e){
                System.err.println(e.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_filterComboBoxActionPerformed

    private void filterComboBoxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filterComboBoxFocusGained
        this.controlsFocusGained();
    }//GEN-LAST:event_filterComboBoxFocusGained

    private void filterComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filterComboBoxFocusLost
        sourceImage = null;
    }//GEN-LAST:event_filterComboBoxFocusLost

    private void contrastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contrastActionPerformed
        this.controlsFocusGained();
        this.applyLookup(
                LookupTableProducer.createLookupTable(
                        LookupTableProducer.TYPE_SFUNCION)
        );
        sourceImage = null;
    }//GEN-LAST:event_contrastActionPerformed

    private void illuminateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_illuminateActionPerformed
        this.controlsFocusGained();
        this.applyLookup(
                LookupTableProducer.createLookupTable(
                        LookupTableProducer.TYPE_ROOT)
        );
        sourceImage = null;
    }//GEN-LAST:event_illuminateActionPerformed

    private void darkenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_darkenActionPerformed
        this.controlsFocusGained();
        this.applyLookup(
                LookupTableProducer.createLookupTable(
                        LookupTableProducer.TYPE_POWER)
        );
        sourceImage = null;
    }//GEN-LAST:event_darkenActionPerformed

    private void rotationSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rotationSliderFocusGained
        this.controlsFocusGained();
    }//GEN-LAST:event_rotationSliderFocusGained

    private void rotationSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rotationSliderStateChanged
        this.applyRotation(rotationSlider.getValue());
    }//GEN-LAST:event_rotationSliderStateChanged

    private void rotationSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rotationSliderFocusLost
        sourceImage = null;
        rotationSlider.setValue(0);
    }//GEN-LAST:event_rotationSliderFocusLost

    private void ninetyDegreesRotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ninetyDegreesRotationActionPerformed
        this.controlsFocusGained();
        this.applyRotation(90);
        sourceImage = null;
    }//GEN-LAST:event_ninetyDegreesRotationActionPerformed

    private void hundredEightyDegreesRotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hundredEightyDegreesRotationActionPerformed
        this.controlsFocusGained();
        this.applyRotation(180);
        sourceImage = null;
    }//GEN-LAST:event_hundredEightyDegreesRotationActionPerformed

    private void twoHundredSeventyDegreesRotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twoHundredSeventyDegreesRotationActionPerformed
        this.controlsFocusGained();
        this.applyRotation(270);
        sourceImage = null;
    }//GEN-LAST:event_twoHundredSeventyDegreesRotationActionPerformed

    private void scaleInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleInActionPerformed
        this.controlsFocusGained();
        this.scaleImage(1.25f, 1.25f);
        sourceImage = null;
    }//GEN-LAST:event_scaleInActionPerformed

    private void scaleOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleOutActionPerformed
        this.controlsFocusGained();
        this.scaleImage(0.75f, 0.75f);
        sourceImage = null;
    }//GEN-LAST:event_scaleOutActionPerformed

    private void quadraticFunctionSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_quadraticFunctionSliderFocusGained
        this.controlsFocusGained();
    }//GEN-LAST:event_quadraticFunctionSliderFocusGained

    private void quadraticFunctionSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_quadraticFunctionSliderFocusLost
        sourceImage = null;
        quadraticFunctionSlider.setValue(0);
    }//GEN-LAST:event_quadraticFunctionSliderFocusLost

    private void quadraticFunctionSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_quadraticFunctionSliderStateChanged
        this.applyLookup(
                this.quadraticFunctionTable(quadraticFunctionSlider.getValue())
        );
    }//GEN-LAST:event_quadraticFunctionSliderStateChanged

    private void thresholdFunctionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thresholdFunctionActionPerformed
        this.controlsFocusGained();
        this.applyLookup(this.thresholdFunctionTable(127));
        sourceImage = null;
    }//GEN-LAST:event_thresholdFunctionActionPerformed

    private void negativeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_negativeActionPerformed
        this.controlsFocusGained();
        this.applyLookup(
                LookupTableProducer.createLookupTable(
                        LookupTableProducer.TYPE_NEGATIVE)
        );
        sourceImage = null;
    }//GEN-LAST:event_negativeActionPerformed

    private void duplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplicateActionPerformed
        BufferedImage image = internalFrame.getCanvas2D().getImage(false);
        String title = internalFrame.getTitle(),
                extension = getExtension(title),
                duplicateTitle = title.replace("." + extension, "") 
                + "-copia." + extension;
        newCanvas();
        // Set a deep copy image to no modify both at the same time.
        internalFrame.getCanvas2D().setImage(
                new BufferedImage(
                        image.getColorModel(),
                        image.copyData(null),
                        image.getColorModel().isAlphaPremultiplied(),
                        null
                )
        );
        internalFrame.setTitle(duplicateTitle);
    }//GEN-LAST:event_duplicateActionPerformed

    private void quadraticFunctionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quadraticFunctionActionPerformed
        this.controlsFocusGained();
        this.applyLookup(
                this.quadraticFunctionTable(128)
        );
        sourceImage = null;
    }//GEN-LAST:event_quadraticFunctionActionPerformed

    private void yellowBandCombinationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yellowBandCombinationActionPerformed
        this.controlsFocusGained();
        float[][] matrix = {
            {0.0F, 0.5F, 0.5F},
            {0.5F, 0.0F, 0.5F},
            {0.5F, 0.5F, 0.0F}
        };
        this.applyCombineOp(matrix);
        sourceImage = null;
    }//GEN-LAST:event_yellowBandCombinationActionPerformed

    private void greenBandCombinationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_greenBandCombinationActionPerformed
        this.controlsFocusGained();
        float[][] matrix = {
            {0.0F, 0.6F, 0.4F},
            {0.0F, 1.0F, 0.0F},
            {0.0F, 0.0F, 1.0F}
        };
        this.applyCombineOp(matrix);
        sourceImage = null;
    }//GEN-LAST:event_greenBandCombinationActionPerformed

    private void bandExtractorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bandExtractorActionPerformed
        if(internalFrame != null){
            BufferedImage image = 
                    internalFrame.getCanvas2D().getImage(
                            tipOverFilters.isSelected()
                    );
            String title = internalFrame.getTitle();
            
            if(image != null){
                InternalFrame iF = null;
                for(int i = 0; i < image.getRaster().getNumBands(); i++){
                    BufferedImage bandImage = getImageBand(image, i);
                    
                    iF = new InternalFrame();
                    desktop.add(iF);
                    iF.setVisible(true);
                    initializeCanvas(iF);
                    iF.getCanvas2D().setImage(bandImage);
                    iF.setTitle(title + " [banda " + i + "]");
                }
                internalFrame = iF;
            }
        }
    }//GEN-LAST:event_bandExtractorActionPerformed

    private void colorSpaceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorSpaceComboBoxActionPerformed
        if(internalFrame != null){
            String option = (String)colorSpaceComboBox.getSelectedItem();
            sourceImage = internalFrame.getCanvas2D().getImage(tipOver.isSelected());

            try{
                ColorSpace colorSpace = null;
                switch(option){
                    case "sRGB":
                        colorSpace = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
                        break;
                    case "YCC":
                        colorSpace = ColorSpace.getInstance(ColorSpace.CS_PYCC);
                        break;
                    case "Grey":
                        colorSpace = new sm.image.color.GreyColorSpace();
                        break;
                    case "YCbCr":
                        colorSpace = new sm.image.color.YCbCrColorSpace();
                        break;
                }
                
                if(colorSpace.getType() != sourceImage.getColorModel().getColorSpace().getType()){
                    ColorConvertOp op = new ColorConvertOp(colorSpace, null);
                    BufferedImage image = op.filter(sourceImage, null);

                    InternalFrame iF = new InternalFrame();
                    desktop.add(iF);
                    iF.setVisible(true);
                    initializeCanvas(iF);
                    iF.getCanvas2D().setImage(image);
                    iF.setTitle(internalFrame.getTitle() + " [" + option + "]");
                    internalFrame = iF;
                } else {
                    JOptionPane.showMessageDialog(
                        null, 
                        "La imagén ya es del tipo de espacio de color seleccionado: " 
                            + option,
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }catch (IllegalArgumentException e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_colorSpaceComboBoxActionPerformed

    private void tintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tintActionPerformed
        this.controlsFocusGained();
        TintOp tintOp = new sm.image.TintOp(
                (Color)colors.getSelectedItem(), 0.5f
        );
        tintOp.filter(
                sourceImage, 
                internalFrame.getCanvas2D().getImage(false)
        );
        sourceImage = null;
        desktop.repaint();
// llamar al filter correspondiente;


//Craer nuestro OP del rojo
// REDOP


// Slider entre 0 y 255 que indica el numero de niveles para representar los colores
// posterizacionOP

// reto nuevo operador propio
    }//GEN-LAST:event_tintActionPerformed

    private void sepiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sepiaActionPerformed
        this.controlsFocusGained();
        SepiaOp sepiaOp = new sm.image.SepiaOp();
        sepiaOp.filter(
                sourceImage,
                internalFrame.getCanvas2D().getImage(false)
        );
        sourceImage = null;
        desktop.repaint();
    }//GEN-LAST:event_sepiaActionPerformed

    private void equalizationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_equalizationActionPerformed
        this.controlsFocusGained();
        EqualizationOp equalizationOp = new sm.image.EqualizationOp();
        equalizationOp.filter(
                sourceImage, internalFrame.getCanvas2D().getImage(false)
        );
        sourceImage = null;
        desktop.repaint();
    }//GEN-LAST:event_equalizationActionPerformed

    private void posterizeSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_posterizeSliderFocusGained
        this.controlsFocusGained();
    }//GEN-LAST:event_posterizeSliderFocusGained

    private void posterizeSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_posterizeSliderFocusLost
        sourceImage = null;
    }//GEN-LAST:event_posterizeSliderFocusLost

    private void posterizeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_posterizeSliderStateChanged
        PosterizeOp posterizeOp = new PosterizeOp(posterizeSlider.getValue());
        posterizeOp.filter(
                sourceImage, 
                internalFrame.getCanvas2D().getImage(false)
        );
        desktop.repaint();
    }//GEN-LAST:event_posterizeSliderStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.controlsFocusGained();
        RedOp redOp = new RedOp(240);
        redOp.filter(
                sourceImage, 
                internalFrame.getCanvas2D().getImage(false)
        );
        sourceImage = null;
        desktop.repaint();
    }//GEN-LAST:event_jButton2ActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton antialiasing;
    private javax.swing.JButton bandExtractor;
    private javax.swing.JPanel brightPanel;
    private javax.swing.JSlider brightnessSlider;
    private javax.swing.JComboBox<String> colorSpaceComboBox;
    private javax.swing.JComboBox<Color> colors;
    private javax.swing.JPanel containerPanel;
    private javax.swing.JButton contrast;
    private javax.swing.JPanel contrastPanel;
    private javax.swing.JMenuItem copyMenu;
    private javax.swing.JMenuItem cutMenu;
    private javax.swing.JButton darken;
    private javax.swing.JDesktopPane desktop;
    private javax.swing.JButton duplicate;
    private javax.swing.JMenu editMenu;
    private javax.swing.JToggleButton ellipse;
    private javax.swing.JButton equalization;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JToggleButton fill;
    private javax.swing.JComboBox<String> filterComboBox;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JToggleButton generalPath;
    private javax.swing.JButton greenBandCombination;
    private javax.swing.JButton hundredEightyDegreesRotation;
    private javax.swing.JButton illuminate;
    private javax.swing.JMenu imageMenu;
    private javax.swing.JButton jButton2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToggleButton line;
    private javax.swing.JMenuBar menu;
    private javax.swing.JButton negative;
    private javax.swing.JButton newCanvas;
    private javax.swing.JMenuItem newMenu;
    private javax.swing.JMenuItem newSizeImage;
    private javax.swing.JButton ninetyDegreesRotation;
    private javax.swing.JButton openCanvas;
    private javax.swing.JMenuItem openMenu;
    private javax.swing.JMenuItem pasteMenu;
    private javax.swing.JSlider posterizeSlider;
    private javax.swing.JMenu printMenu;
    private javax.swing.JMenuItem printerFileMenu;
    private javax.swing.JMenuItem printerMenu;
    private javax.swing.JButton quadraticFunction;
    private javax.swing.JPanel quadraticFunctionPanel;
    private javax.swing.JSlider quadraticFunctionSlider;
    private javax.swing.JToggleButton rectangle;
    private javax.swing.JPanel rotationPanel;
    private javax.swing.JSlider rotationSlider;
    private javax.swing.JButton saveCanvas;
    private javax.swing.JMenuItem saveMenu;
    private javax.swing.JButton scaleIn;
    private javax.swing.JButton scaleOut;
    private javax.swing.JPanel scalePanel;
    private javax.swing.JToggleButton selector;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToolBar.Separator separator2;
    private javax.swing.JPopupMenu.Separator separatorFile;
    private javax.swing.JButton sepia;
    private javax.swing.JPanel spaceColorPanel;
    private javax.swing.JPanel statusBar;
    private javax.swing.JCheckBoxMenuItem statusBarMenu;
    private javax.swing.JLabel statusBarTitle;
    private javax.swing.JLabel statusBarVariable;
    private javax.swing.JButton thresholdFunction;
    private javax.swing.JPanel thresholdFunctionPanel;
    private javax.swing.JButton tint;
    private javax.swing.JButton tipOver;
    private javax.swing.JToggleButton tipOverFilters;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JToolBar toolBarImages;
    private javax.swing.JPanel toolsPanel;
    private javax.swing.JToggleButton transparency;
    private javax.swing.JButton twoHundredSeventyDegreesRotation;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JSpinner widthStroke;
    private javax.swing.JToggleButton windowsEffect;
    private javax.swing.JButton yellowBandCombination;
    // End of variables declaration//GEN-END:variables
}

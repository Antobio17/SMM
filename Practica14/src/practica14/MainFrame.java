/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica14;

import com.github.sarxos.webcam.Webcam;
import sm.ajr.iu.Canvas2D;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;
import sm.ajr.graphics.AJREllipse;
import sm.ajr.graphics.AJRFillShape2D;
import sm.ajr.graphics.AJRGeneralPath;
import sm.ajr.graphics.AJRLine;
import sm.ajr.graphics.AJRRectangle;
import sm.ajr.graphics.AJRShape2D;
import sm.ajr.image.AutoTintOp;
import sm.ajr.image.OwnOp;
import sm.ajr.image.PosterizeOp;
import sm.ajr.image.RedOp;
import sm.image.EqualizationOp;
import sm.image.KernelProducer;
import sm.image.LookupTableProducer;
import sm.image.SepiaOp;
import sm.image.TintOp;
import sm.sound.SMClipPlayer;
import sm.sound.SMSoundRecorder;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

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
          0f,   0f,   0f,   0f, 0.2f,
          0f,   0f,   0f, 0.2f,   0f,
          0f,   0f, 0.2f,   0f,   0f,
          0f, 0.2f,   0f,   0f,   0f,
        0.2f,   0f,   0f,   0f,   0f,
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
    MouseHandler mouseListener = null;
    Color colorsArray[] = {
        Color.BLACK, Color.WHITE, Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN 
    };
    ImageIcon icons[] = {
        new ImageIcon(this.getClass().getClassLoader().getResource(
                "icons/contraste.png")
        ),
        new ImageIcon(this.getClass().getClassLoader().getResource(
                "icons/iluminar.png")
        ),
        new ImageIcon(this.getClass().getClassLoader().getResource(
                "icons/oscurecer.png")
        ),
        new ImageIcon(this.getClass().getClassLoader().getResource(
                "icons/negative.png")
        )
    };
    
    /* Canvas measurements */
    int canvasWidth, canvasHeight;
    
    /* Brightness */
    BufferedImage sourceImage;
    
    /* Audio */
    SMClipPlayer player = null;
    SMSoundRecorder recorder = null;
    AudioHandler audioListener = null;
    RecordHandler recordListener = null;
    File recordingFile;
    Timer timer = null;
    int minutes, seconds;
    
    /* Video */
    WebcamInternalFrame webcamIF;
    
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
        mouseListener = new MouseHandler();
        canvasWidth = 700;
        canvasHeight = 600;
        brightnessSlider.setValue(0);
        sourceImage = null;
        brightnessSlider.setMinimum(-100);
        brightnessSlider.setMaximum(100);
        quadraticFunctionSlider.setValue(0);
        rotationSlider.setValue(0);
        firstRotationLabel.setVisible(false);
        secondRotationLabel.setVisible(false);
        rotationSlider.setVisible(false);
        tintSlider.setVisible(false);        
        redHighlightSlider.setVisible(false);
        posterizeSlider.setVisible(false);
        quadraticFunctionSlider.setVisible(false); 
        ownOperatorSlider.setVisible(false);
        ownLookupOp.setVisible(false);
        audioListener = new AudioHandler();
        recordListener = new RecordHandler();
        minutes = 0;
        seconds = 0;
        timer = new Timer(1000, new ActionListener(){
        @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                if(seconds == 60){
                    seconds = 0;
                    minutes++;
                }
                recordingTime.setText(
                        (minutes < 10 ? "0" + minutes : minutes) 
                        + ":" + (seconds < 10 ? "0" + seconds : seconds) 
                );
            }
        });
        stop.setEnabled(false);
        shapeStatusBar.setText("Dibujando: Trazo libre");
        webcamIF = null;
        for(Webcam webcam: Webcam.getWebcams()){
            webcamComboBox.addItem(webcam);
        }
        _setWebcamDimensions();
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
    
    public LookupTable ownLookupOpTable(int value){
        double Max;
        if(value < 0){
            Max = Math.pow(255,2);
        }else{
            Max = Math.log(1.0 + 255);
        }

        double K = 255.0/Max;
 
        byte lookupTable[] = new byte[256];
        for(int i = 0; i < 256; i++){
            if(i < 150)
                lookupTable[i] = (byte)i;
            else{
                if(value < 0){
                    lookupTable[i] = (byte)(K*(Math.pow(i,2)));
                }else{
                    lookupTable[i] = (byte)(K*(Math.log(1.0 + i)));
                }
            }
        }
        
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
    private void _toolsSelectedToFalse()
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
    private void _newCanvas()
    {
        // Dialog to set meassure of the canvas
        _throwDialogMeassureAndSetImage(canvasWidth, canvasHeight);
        
        InternalFrame iF = new InternalFrame();
        desktop.add(iF);
        iF.setVisible(true);
        
        _initializeCanvas(iF);
        internalFrame = iF;
    }
    
    /**
     * Method to open a file.
     * 
     */
    private void _openFile()
    {
        JFileChooser dlg = new JFileChooser();
        dlg.setFileFilter(new FileNameExtensionFilter(
                "Videos [mp4, mpg, avi]",
                "mp4", "mpg", "avi"));
        dlg.setFileFilter(new FileNameExtensionFilter(
                "Audios [wav, au, mid]",
                "wav", "au", "mid"));
        dlg.setFileFilter(new FileNameExtensionFilter(
                "Imagenes [jpg, bmp, gif, png, jpeg, wbmp]",
                "jpg", "bmp", "gif", "png", "jpeg", "wbmp"));
        int resp = dlg.showOpenDialog(this);
        if( resp == JFileChooser.APPROVE_OPTION) {
            try{
                if(this._isImage(dlg.getSelectedFile())){
                    File file = dlg.getSelectedFile();
                    BufferedImage image = ImageIO.read(file);
                    InternalFrame iF = new InternalFrame();
                    this.desktop.add(iF);
                    iF.setTitle(file.getName());
                    iF.setVisible(true);
                    _initializeCanvas(iF);
                    internalFrame = iF;
                    // Dialog to set meassure of the canvas
                    _throwDialogMeassureAndSetImage(
                            image.getWidth(), image.getHeight()
                    );
                    iF.getCanvas2D().setImage(image);
                }else if(this._isAudio(dlg.getSelectedFile())){
                    File file = new File(dlg.getSelectedFile().getPath()) {
                        @Override
                        public String toString() {
                            return this.getName();
                        }
                    };
                    audioComboBox.addItem(file);
                    if(player != null){
                        player.stop();
                        player = null;
                    }
                    if(recorder != null){
                        recorder.stop();
                        recorder = null;
                    }
                }else if(this._isVideo(dlg.getSelectedFile())){
                    File file = dlg.getSelectedFile();
                    VideoInternalFrame videoIF =
                            VideoInternalFrame.getInstance(file);
                    videoIF.addMediaPlayerEventListener(new VideoHandler());
                    this.desktop.add(videoIF);
                    videoIF.setTitle(file.getName());
                    videoIF.setVisible(true);
                }
            }catch(IOException ex){
                JOptionPane.showMessageDialog(
                        null, 
                        "Error al leer archivo: " + ex.getLocalizedMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Method to save the active canvas to an image.
     * 
     */
    private void _saveFile()
    {
        
                JFileChooser dlg = new JFileChooser();
                dlg.setFileFilter(new FileNameExtensionFilter(
                        "Imagenes [jpg, bmp, gif, png, jpeg, wbmp]",
                        "jpg", "bmp", "gif", "png", "jpeg", "wbmp"));
                dlg.setFileFilter(new FileNameExtensionFilter(
                        "Audios [wav, au, mid]",
                        "wav", "au", "mid"));
                
                int resp = dlg.showSaveDialog(this);
                if (resp == JFileChooser.APPROVE_OPTION) {
                    try {
                        if (this._isImage(dlg.getSelectedFile())) {
                            if (internalFrame != null) {
                                BufferedImage image = internalFrame.getCanvas2D().getImage(true);
                                if (image != null) {
                                    File file = dlg.getSelectedFile();
                                    ImageIO.write(image, this._getExtension(
                                            file.getName()), file);
                                    internalFrame.setTitle(file.getName());
                                }
                            }
                        }else if(this._isAudio(dlg.getSelectedFile())){
                            if(recordingFile != null){
                                File file = new File(dlg.getSelectedFile().getPath()) {
                                        @Override
                                        public String toString() {
                                            return this.getName();
                                        }
                                };
                                _cloneFile(recordingFile, file);
                                audioComboBox.addItem(file);
                                audioComboBox.setSelectedItem(file);
                            }
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(
                                null, 
                                "Error al guardar el archivo", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    
                }
            
    }
    
    /**
     * Método para saber si un archivo es de tipo Imagen.
     * 
     * @param file File: archivo a comprobar.
     * 
     * @return boolean si el archivo es de tipo imagen o no.
     */
    private boolean _isImage(File file)
    {
        String extension = this._getExtension(file.getName());
        return ("jpg".equals(extension) || "bmp".equals(extension) ||
                "gif".equals(extension) || "png".equals(extension) ||
                "jpeg".equals(extension) || "wbmp".equals(extension));
    }
    
    /**
     * Método para saber si un archivo es de tipo Audio.
     * 
     * @param file File: archivo a comprobar.
     * 
     * @return boolean si el archivo es de tipo Audio o no.
     */
    private boolean _isAudio(File file)
    {
        String extension = this._getExtension(file.getName());
        return ("wave".equals(extension) || "au".equals(extension));
    }
    
    /**
     * Método para saber si un archivo es de tipo Video.
     * 
     * @param file File: archivo a comprobar.
     * 
     * @return boolean si el archivo es de tipo video o no.
     */
    private boolean _isVideo(File file)
    {
        String extension = this._getExtension(file.getName());
        return ("mp4".equals(extension) || "mpg".equals(extension) 
                || "avi".equals(extension));
    }
    
    /**
     * Methods to initialize the options of the canvas.
     * 
     */
    private void _initializeCanvas(InternalFrame iF)
    {
        // Assign the listeners
        iF.addInternalFrameListener(internalFrameListener);
        iF.getCanvas2D().addMouseListener(mouseListener);
        iF.getCanvas2D().addMouseMotionListener(mouseListener);
        
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
    private void _throwDialogMeassureAndSetImage(int width, int height)
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
    private String _getExtension(String fileName)
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
    private void _swingControlsFocusGained()
    {
        if(internalFrame != null){
            if(tipOverFilters.isSelected())
                this._tipOverShapes();
            
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
    private void _tipOverShapes()
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
    private Kernel _getKernelFromSelect(int index)
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
    private void _applyLookup(LookupTable lookupTable)
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
    private void _applyRotation(int degrees)
    {
        if(internalFrame != null){
            try{
                if(sourceImage != null){
                    double radians = Math.toRadians(degrees);
                    Point p = new Point(
                            sourceImage.getWidth()/2, sourceImage.getHeight()/2
                    );
                    AffineTransform at = AffineTransform.getRotateInstance(
                            radians, p.x, p.y
                    );
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(
                            at,AffineTransformOp.TYPE_BILINEAR
                    );
                    internalFrame.getCanvas2D().setImage(
                            atop.filter(sourceImage, null)
                    );
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
    private void _applyCombineOp(float[][] matrix)
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
    private BufferedImage _getImageBand(BufferedImage image, int band) {
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
    
    private void _resetTimer(){
        seconds = 0;
        minutes = 0;
        recordingTime.setText("00:00");
    }
    
    /**
     * Función para clonar el buffer de un archivo.
     * 
     * @param source File: archivo fuente a clonar de tipo File.
     * @param dest File: archivo destino al que aplicar la clonacion de tipo File.
     * @throws IOException 
     */
    private static void _cloneFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if(is != null && os != null){
                is.close();
                os.close();
            }
        }
    }
    
    /**
     * Función para establecer las resoluciones de la Webcam seleccionada.
     * 
     */
    private void _setWebcamDimensions()
    {
        dimensionComboBox.removeAllItems();
        for(Dimension d: ((Webcam)webcamComboBox.getSelectedItem()).getViewSizes()){
            Dimension dimension = new Dimension(d){
                @Override
                public String toString() {
                    return (int)this.getWidth() + "x" + (int)this.getHeight();
                }
            };
            dimensionComboBox.addItem(dimension);
        }
    }
    
    /******************************** HANDLERS *******************************/
    
    private class MouseHandler extends MouseAdapter
    {
        
        /**
         * 
         * @param e 
         */
        @Override
        public void mouseClicked(MouseEvent e)
        {
            _mouseActionClick();
        }
        
        /**
         * 
         * @param e 
         */
        @Override
        public void mousePressed(MouseEvent e)
        {
            _mouseActionClick();
        }
        
        /**
         * 
         * @param e 
         */
        @Override
        public void mouseMoved(MouseEvent e)
        {
            if(internalFrame != null){
                _setStatusBarText(e.getPoint());
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
                _setStatusBarText(e.getPoint());
                if (windowsEffect.isSelected()) {
                    internalFrame.getCanvas2D().setWindowsEffectPosition(
                            e.getPoint());
                }
                internalFrame.getCanvas2D().repaint();
            }
        }
        
        private void _setStatusBarText(Point p)
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
                        ", " + pixelColor.getGreen() + ", " +
                        pixelColor.getBlue() + " )"
                );
            }else{
                statusBarVariable.setText(
                        statusBarVariable.getText() +
                        "  RGB: Fuera de Imagen"
                );
            }           
        }
        
        private void _mouseActionClick()
        {
            if(internalFrame != null && selector.isSelected()){
                AJRShape2D shape = internalFrame.getCanvas2D().getActualShape();
                if(shape != null){
                    // Set the status bar
                    if(shape instanceof AJRLine){
                        shapeStatusBar.setText("Figura seleccionada: Línea");
                    }else if(shape instanceof AJRRectangle){
                        shapeStatusBar.setText("Figura seleccionada: Rectángulo");
                    }else if(shape instanceof AJREllipse){
                        shapeStatusBar.setText("Figura seleccionada: Elipse");
                    }
                    
                    Composite transparencyComposite =
                            AlphaComposite.getInstance(
                                    AlphaComposite.SRC_OVER, 0.5f
                            );
                   
                    if(shape instanceof AJRFillShape2D){
                        // Set the fill mode in MainFrame and Canvas
                        boolean isFill = ((AJRFillShape2D) shape).getIsFill();
                        fill.setSelected(isFill);
                        internalFrame.getCanvas2D().setFillMode(isFill);
                        // Set the color in MainFrame and Canvas
                         colors.setSelectedItem(
                                ((AJRFillShape2D)shape).getFillColor()
                        );
                        internalFrame.getCanvas2D().setActiveColor(
                                ((AJRFillShape2D)shape).getFillColor()
                        );
                    }else{
                        // Set the fill mode in MainFrame and Canvas
                        fill.setSelected(false);
                        internalFrame.getCanvas2D().setFillMode(false);
                        // Set the color in MainFrame and Canvas
                        colors.setSelectedItem(
                                Color.BLACK
                        );
                        internalFrame.getCanvas2D().setActiveColor(
                                Color.BLACK
                        );
                    }
                    
                    // Set the stroke color in MainFrame and Canvas
                    strokeColors.setSelectedItem(
                            shape.getStrokeColor()
                    );
                    internalFrame.getCanvas2D().setActiveColor(
                            shape.getStrokeColor()
                    );
                    
                    // Set the transparency in MainFrame and Canvas
                    boolean isTransparency = 
                            shape.getComposite().equals(transparencyComposite);
                    transparency.setSelected(isTransparency);
                    internalFrame.getCanvas2D().setTransparencyMode(isTransparency);
                    // Set the antialiasing in MainFrame and Canvas
                    antialiasing.setSelected(shape.getHasAntialiasing());
                    internalFrame.getCanvas2D().setAntialiasingMode(
                            shape.getHasAntialiasing()
                    );
                    // Set the widthStroke in MainFrame and Canvas
                    widthStroke.setValue((int)shape.getStroke().getLineWidth());
                    internalFrame.getCanvas2D().setWidthStroke(
                            (int)shape.getStroke().getLineWidth()
                    );
                }
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
                _toolsSelectedToFalse();
                selector.setSelected(true);
                internalFrame.getCanvas2D().setSelectorMode(true);
                internalFrame.getCanvas2D().setCursor(
                        new java.awt.Cursor(java.awt.Cursor.MOVE_CURSOR));
                shapeStatusBar.setText("Figura Seleccionada: Ninguna");
            }else{
                _toolsSelectedToFalse();
                switch(internalFrame.getCanvas2D().getActiveShape()){
                    case GENERALPATH:
                        generalPath.setSelected(true);
                        shapeStatusBar.setText("Dibujando: Trazo libre");
                        break;
                    case LINE:
                        line.setSelected(true);
                        shapeStatusBar.setText("Dibujando: Línea");
                        break;
                    case RECTANGLE:
                        rectangle.setSelected(true);
                        shapeStatusBar.setText("Dibujando: Rectángulo");
                        break;
                    case ELLIPSE:
                        ellipse.setSelected(true);
                        shapeStatusBar.setText("Dibujando: Elipse");
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
    
    private class AudioHandler implements LineListener
    {

        @Override
        public void update(LineEvent event) {
            if (event.getType() == LineEvent.Type.START) {
                play.setIcon(
                        new ImageIcon(
                                this.getClass().getClassLoader().getResource(
                                        "icons/1-Pause.png"
                                )
                        )
                );
                rec.setEnabled(false);
                stop.setEnabled(true);
                audioComboBox.setEnabled(false);
                timer.start();
            }
            if (event.getType() == LineEvent.Type.STOP) {
                play.setIcon(
                        new ImageIcon(
                                this.getClass().getClassLoader().getResource(
                                        "icons/1-Play.png"
                                )
                        )
                );
                timer.stop();
                if(player.getClip().getFrameLength() == player.getClip().getFramePosition()){
                    _closeEvent();
                }
            }
            if (event.getType() == LineEvent.Type.CLOSE) {
                _closeEvent();
            }
        }
        
        private void _closeEvent(){
            rec.setEnabled(true);
            stop.setEnabled(false);
            audioComboBox.setEnabled(true);
            player = null;
            _resetTimer();
        }
    }
    
    private class RecordHandler implements LineListener
    {
        
        @Override
        public void update(LineEvent event) {
            if (event.getType() == LineEvent.Type.START) {
                play.setEnabled(false);
                stop.setEnabled(false);
                audioComboBox.setEnabled(false);
                rec.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("icons/1-StopRecord.png")));
                timer.start();
            }
            if (event.getType() == LineEvent.Type.STOP) {
                play.setEnabled(true);
                stop.setEnabled(true);
                audioComboBox.setEnabled(true);
                rec.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("icons/1-Rec.png")));
                timer.stop();
                _resetTimer();
            }
            if (event.getType() == LineEvent.Type.CLOSE) {
                
            }
        }
        
    }
    
    private class VideoHandler extends MediaPlayerEventAdapter {
        
        @Override
        public void playing(MediaPlayer mediaPlayer) {
            play.setEnabled(false);
            stop.setEnabled(true);
        }
        
        @Override
        public void paused(MediaPlayer mediaPlayer) {
            stop.setEnabled(false);
            play.setEnabled(true);
        }

        @Override
        public void finished(MediaPlayer mediaPlayer) {
            this.paused(mediaPlayer);
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
        openFile = new javax.swing.JButton();
        saveCanvas = new javax.swing.JButton();
        separator2 = new javax.swing.JToolBar.Separator();
        generalPath = new javax.swing.JToggleButton();
        line = new javax.swing.JToggleButton();
        rectangle = new javax.swing.JToggleButton();
        ellipse = new javax.swing.JToggleButton();
        selector = new javax.swing.JToggleButton();
        separator1 = new javax.swing.JToolBar.Separator();
        colors = new javax.swing.JComboBox<>(colorsArray);
        strokeColors = new javax.swing.JComboBox<>(colorsArray);
        jSeparator1 = new javax.swing.JToolBar.Separator();
        widthStroke = new javax.swing.JSpinner();
        fill = new javax.swing.JToggleButton();
        transparency = new javax.swing.JToggleButton();
        antialiasing = new javax.swing.JToggleButton();
        tipOver = new javax.swing.JButton();
        windowsEffect = new javax.swing.JToggleButton();
        tipOverFilters = new javax.swing.JToggleButton();
        duplicate = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        brightnessSlider = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        contrastComboBox = new javax.swing.JComboBox<>(icons);
        jSeparator5 = new javax.swing.JToolBar.Separator();
        filterComboBox = new javax.swing.JComboBox<>();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        rotationButton = new javax.swing.JButton();
        firstRotationLabel = new javax.swing.JLabel();
        rotationSlider = new javax.swing.JSlider();
        secondRotationLabel = new javax.swing.JLabel();
        scaleIn = new javax.swing.JButton();
        scaleOut = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        quadraticFunctionButton = new javax.swing.JButton();
        quadraticFunctionSlider = new javax.swing.JSlider();
        bandCombination = new javax.swing.JButton();
        greenBandCombination = new javax.swing.JButton();
        tint = new javax.swing.JButton();
        tintSlider = new javax.swing.JSlider();
        autoTintOp = new javax.swing.JButton();
        sepia = new javax.swing.JButton();
        equalization = new javax.swing.JButton();
        redHighlight = new javax.swing.JButton();
        redHighlightSlider = new javax.swing.JSlider();
        posterizeButton = new javax.swing.JButton();
        posterizeSlider = new javax.swing.JSlider();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        bandExtractor = new javax.swing.JButton();
        colorSpaceComboBox = new javax.swing.JComboBox<>();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jLabel7 = new javax.swing.JLabel();
        challengesComboBox = new javax.swing.JComboBox<>();
        ownOperatorButton = new javax.swing.JButton();
        ownOperatorSlider = new javax.swing.JSlider();
        ownLookupOp = new javax.swing.JSlider();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        histogram = new javax.swing.JButton();
        statusBar = new javax.swing.JPanel();
        statusBarTitle = new javax.swing.JLabel();
        statusBarVariable = new javax.swing.JLabel();
        shapeStatusBar = new javax.swing.JLabel();
        containerPanel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        play = new javax.swing.JButton();
        stop = new javax.swing.JButton();
        audioComboBox = new javax.swing.JComboBox<>();
        jSeparator12 = new javax.swing.JToolBar.Separator();
        rec = new javax.swing.JButton();
        recordingTime = new javax.swing.JLabel();
        jSeparator13 = new javax.swing.JToolBar.Separator();
        webcamButton = new javax.swing.JButton();
        webcamComboBox = new javax.swing.JComboBox<>();
        dimensionComboBox = new javax.swing.JComboBox<>();
        screenShotButton = new javax.swing.JButton();
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
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        tintItemMenu = new javax.swing.JMenuItem();
        redHighlightItemMenu = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        statusBarMenu = new javax.swing.JCheckBoxMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutItemMenu = new javax.swing.JMenuItem();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Práctica 14");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setPreferredSize(new java.awt.Dimension(1800, 676));

        toolBar.setRollover(true);

        newCanvas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-New.png"))); // NOI18N
        newCanvas.setToolTipText("Nuevo lienzo");
        newCanvas.setFocusable(false);
        newCanvas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newCanvas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newCanvas.addActionListener(formListener);
        toolBar.add(newCanvas);

        openFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Open.png"))); // NOI18N
        openFile.setToolTipText("Abrir archivo");
        openFile.setFocusable(false);
        openFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openFile.addActionListener(formListener);
        toolBar.add(openFile);

        saveCanvas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Save.png"))); // NOI18N
        saveCanvas.setToolTipText("Guardar lienzo");
        saveCanvas.setFocusable(false);
        saveCanvas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveCanvas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveCanvas.addActionListener(formListener);
        toolBar.add(saveCanvas);
        toolBar.add(separator2);

        generalPath.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Draw.png"))); // NOI18N
        generalPath.setToolTipText("Dibujar libre");
        generalPath.setFocusable(false);
        generalPath.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        generalPath.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        generalPath.addActionListener(formListener);
        toolBar.add(generalPath);

        line.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Line.png"))); // NOI18N
        line.setToolTipText("Dibujar línea");
        line.setFocusable(false);
        line.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        line.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        line.addActionListener(formListener);
        toolBar.add(line);
        line.getAccessibleContext().setAccessibleDescription("Línea");

        rectangle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Square.png"))); // NOI18N
        rectangle.setToolTipText("Dibujar rectángulo");
        rectangle.setFocusable(false);
        rectangle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rectangle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rectangle.addActionListener(formListener);
        toolBar.add(rectangle);
        rectangle.getAccessibleContext().setAccessibleDescription("Rectángulo");

        ellipse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Circle.png"))); // NOI18N
        ellipse.setToolTipText("Dibujar elipse");
        ellipse.setFocusable(false);
        ellipse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ellipse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ellipse.addActionListener(formListener);
        toolBar.add(ellipse);
        ellipse.getAccessibleContext().setAccessibleDescription("Elipse");

        selector.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Edit.png"))); // NOI18N
        selector.setToolTipText("Editar figura");
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

        strokeColors.setToolTipText("Color de trazo");
        strokeColors.setRenderer(new ColorCellRender());
        strokeColors.addActionListener(formListener);
        toolBar.add(strokeColors);
        toolBar.add(jSeparator1);

        widthStroke.setToolTipText("Cambiar grosor del trazo");
        widthStroke.setBorder(null);
        widthStroke.setMaximumSize(new java.awt.Dimension(60, 30));
        widthStroke.setMinimumSize(new java.awt.Dimension(50, 30));
        widthStroke.setPreferredSize(new java.awt.Dimension(50, 30));
        widthStroke.addChangeListener(formListener);
        toolBar.add(widthStroke);
        widthStroke.getAccessibleContext().setAccessibleDescription("Grosor");

        fill.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Fill.png"))); // NOI18N
        fill.setToolTipText("Rellenar figura");
        fill.setFocusable(false);
        fill.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fill.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fill.addActionListener(formListener);
        toolBar.add(fill);
        fill.getAccessibleContext().setAccessibleDescription("Relleno");

        transparency.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Transparency.png"))); // NOI18N
        transparency.setToolTipText("Aplicar transparencia a la figura");
        transparency.setFocusable(false);
        transparency.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        transparency.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        transparency.addActionListener(formListener);
        toolBar.add(transparency);
        transparency.getAccessibleContext().setAccessibleDescription("Transparencia");

        antialiasing.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Antialiasing.png"))); // NOI18N
        antialiasing.setToolTipText("Alisar figuras");
        antialiasing.setFocusable(false);
        antialiasing.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        antialiasing.setMaximumSize(new java.awt.Dimension(25, 25));
        antialiasing.setMinimumSize(new java.awt.Dimension(25, 25));
        antialiasing.setPreferredSize(new java.awt.Dimension(25, 25));
        antialiasing.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        antialiasing.addActionListener(formListener);
        toolBar.add(antialiasing);
        antialiasing.getAccessibleContext().setAccessibleDescription("Alisar");

        tipOver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Download.png"))); // NOI18N
        tipOver.setToolTipText("Volcar figuras en la imagen");
        tipOver.setFocusable(false);
        tipOver.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tipOver.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tipOver.addActionListener(formListener);
        toolBar.add(tipOver);

        windowsEffect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-View.png"))); // NOI18N
        windowsEffect.setToolTipText("Efecto ventana");
        windowsEffect.setFocusable(false);
        windowsEffect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        windowsEffect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        windowsEffect.addActionListener(formListener);
        toolBar.add(windowsEffect);

        tipOverFilters.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Tipover.png"))); // NOI18N
        tipOverFilters.setToolTipText("Aplicar filtros a figuras");
        tipOverFilters.setFocusable(false);
        tipOverFilters.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tipOverFilters.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(tipOverFilters);

        duplicate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Duplicate.png"))); // NOI18N
        duplicate.setToolTipText("Duplicar");
        duplicate.setFocusable(false);
        duplicate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        duplicate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        duplicate.addActionListener(formListener);
        toolBar.add(duplicate);
        toolBar.add(jSeparator4);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-BrightnessLow.png"))); // NOI18N
        toolBar.add(jLabel2);

        brightnessSlider.setMinimum(-100);
        brightnessSlider.setToolTipText("Brillo");
        brightnessSlider.setValue(0);
        brightnessSlider.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        brightnessSlider.setMaximumSize(new java.awt.Dimension(100, 26));
        brightnessSlider.setPreferredSize(new java.awt.Dimension(100, 26));
        brightnessSlider.addChangeListener(formListener);
        brightnessSlider.addFocusListener(formListener);
        toolBar.add(brightnessSlider);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-BrightnessHigh.png"))); // NOI18N
        toolBar.add(jLabel3);

        contrastComboBox.addActionListener(formListener);
        toolBar.add(contrastComboBox);
        toolBar.add(jSeparator5);

        filterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Media", "Suavizado", "Realce", "Fronteras", "Enb. Horizontal", "Enb. Diagonal", "Binomial", "Foco Intenso", "Foco", "Laplaciana", "Relieve", "SolbeX", "SolbeY", " " }));
        filterComboBox.setToolTipText("Filtros");
        filterComboBox.setMaximumSize(new java.awt.Dimension(120, 23));
        filterComboBox.setMinimumSize(new java.awt.Dimension(120, 23));
        filterComboBox.setPreferredSize(new java.awt.Dimension(120, 23));
        filterComboBox.addFocusListener(formListener);
        filterComboBox.addActionListener(formListener);
        toolBar.add(filterComboBox);
        toolBar.add(jSeparator6);

        rotationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Rotation.png"))); // NOI18N
        rotationButton.setToolTipText("Mostrar/Ocultar rotación");
        rotationButton.setFocusable(false);
        rotationButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rotationButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rotationButton.addActionListener(formListener);
        toolBar.add(rotationButton);

        firstRotationLabel.setText("0");
        toolBar.add(firstRotationLabel);

        rotationSlider.setMajorTickSpacing(180);
        rotationSlider.setMaximum(360);
        rotationSlider.setMinorTickSpacing(60);
        rotationSlider.setToolTipText("Aplicar rotación");
        rotationSlider.setValue(0);
        rotationSlider.setMaximumSize(new java.awt.Dimension(100, 26));
        rotationSlider.setMinimumSize(new java.awt.Dimension(100, 26));
        rotationSlider.setPreferredSize(new java.awt.Dimension(100, 26));
        rotationSlider.addChangeListener(formListener);
        rotationSlider.addFocusListener(formListener);
        toolBar.add(rotationSlider);

        secondRotationLabel.setText("360");
        toolBar.add(secondRotationLabel);

        scaleIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-ZoomIn.png"))); // NOI18N
        scaleIn.setToolTipText("Aumentar escala");
        scaleIn.addActionListener(formListener);
        toolBar.add(scaleIn);

        scaleOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-ZoomOut.png"))); // NOI18N
        scaleOut.setToolTipText("Disminuir escala");
        scaleOut.addActionListener(formListener);
        toolBar.add(scaleOut);
        toolBar.add(jSeparator7);

        quadraticFunctionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Quadratic.png"))); // NOI18N
        quadraticFunctionButton.setToolTipText("Mostrar/Ocultar función Cuadrática");
        quadraticFunctionButton.setMaximumSize(new java.awt.Dimension(25, 25));
        quadraticFunctionButton.setMinimumSize(new java.awt.Dimension(25, 25));
        quadraticFunctionButton.setPreferredSize(new java.awt.Dimension(25, 25));
        quadraticFunctionButton.addActionListener(formListener);
        toolBar.add(quadraticFunctionButton);

        quadraticFunctionSlider.setMajorTickSpacing(50);
        quadraticFunctionSlider.setMaximum(255);
        quadraticFunctionSlider.setMinorTickSpacing(10);
        quadraticFunctionSlider.setPaintTicks(true);
        quadraticFunctionSlider.setToolTipText("Función Cuadrática");
        quadraticFunctionSlider.setValue(0);
        quadraticFunctionSlider.setMaximumSize(new java.awt.Dimension(100, 26));
        quadraticFunctionSlider.setMinimumSize(new java.awt.Dimension(100, 26));
        quadraticFunctionSlider.setPreferredSize(new java.awt.Dimension(100, 26));
        quadraticFunctionSlider.addChangeListener(formListener);
        quadraticFunctionSlider.addFocusListener(formListener);
        toolBar.add(quadraticFunctionSlider);

        bandCombination.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-BindCombine.png"))); // NOI18N
        bandCombination.setToolTipText("Combinación de bandas");
        bandCombination.setMaximumSize(new java.awt.Dimension(25, 25));
        bandCombination.setMinimumSize(new java.awt.Dimension(25, 25));
        bandCombination.setPreferredSize(new java.awt.Dimension(25, 25));
        bandCombination.addActionListener(formListener);
        toolBar.add(bandCombination);

        greenBandCombination.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/paint-bucket.png"))); // NOI18N
        greenBandCombination.setToolTipText("Enverdecer");
        greenBandCombination.setMaximumSize(new java.awt.Dimension(25, 25));
        greenBandCombination.setMinimumSize(new java.awt.Dimension(25, 25));
        greenBandCombination.setPreferredSize(new java.awt.Dimension(25, 25));
        greenBandCombination.addActionListener(formListener);
        toolBar.add(greenBandCombination);

        tint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Tint.png"))); // NOI18N
        tint.setToolTipText("Mostrar/Ocultar tintado");
        tint.addActionListener(formListener);
        toolBar.add(tint);

        tintSlider.setValue(0);
        tintSlider.setMaximumSize(new java.awt.Dimension(100, 26));
        tintSlider.setMinimumSize(new java.awt.Dimension(100, 26));
        tintSlider.setPreferredSize(new java.awt.Dimension(100, 26));
        tintSlider.addChangeListener(formListener);
        tintSlider.addFocusListener(formListener);
        toolBar.add(tintSlider);

        autoTintOp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-AutoTintOp.png"))); // NOI18N
        autoTintOp.setToolTipText("Tintado automático");
        autoTintOp.setFocusable(false);
        autoTintOp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        autoTintOp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        autoTintOp.addActionListener(formListener);
        toolBar.add(autoTintOp);

        sepia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Sepia.png"))); // NOI18N
        sepia.setToolTipText("Sepia");
        sepia.addActionListener(formListener);
        toolBar.add(sepia);

        equalization.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Equalize.png"))); // NOI18N
        equalization.setToolTipText("Ecualizar");
        equalization.addActionListener(formListener);
        toolBar.add(equalization);

        redHighlight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Red.png"))); // NOI18N
        redHighlight.setToolTipText("Mostrar/Ocultar resalte tono rojo");
        redHighlight.setMaximumSize(new java.awt.Dimension(25, 25));
        redHighlight.setMinimumSize(new java.awt.Dimension(25, 25));
        redHighlight.setPreferredSize(new java.awt.Dimension(25, 25));
        redHighlight.addActionListener(formListener);
        toolBar.add(redHighlight);

        redHighlightSlider.setMaximum(256);
        redHighlightSlider.setMinimum(-512);
        redHighlightSlider.setValue(-512);
        redHighlightSlider.setMaximumSize(new java.awt.Dimension(100, 26));
        redHighlightSlider.setMinimumSize(new java.awt.Dimension(100, 26));
        redHighlightSlider.setPreferredSize(new java.awt.Dimension(100, 26));
        redHighlightSlider.addChangeListener(formListener);
        redHighlightSlider.addFocusListener(formListener);
        toolBar.add(redHighlightSlider);

        posterizeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Posterize.png"))); // NOI18N
        posterizeButton.setToolTipText("Mostrar/Ocultar posterizado");
        posterizeButton.setFocusable(false);
        posterizeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        posterizeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        posterizeButton.addActionListener(formListener);
        toolBar.add(posterizeButton);

        posterizeSlider.setMaximum(40);
        posterizeSlider.setMinimum(2);
        posterizeSlider.setToolTipText("Posterizar");
        posterizeSlider.setMaximumSize(new java.awt.Dimension(100, 26));
        posterizeSlider.setMinimumSize(new java.awt.Dimension(100, 26));
        posterizeSlider.setPreferredSize(new java.awt.Dimension(100, 26));
        posterizeSlider.addChangeListener(formListener);
        posterizeSlider.addFocusListener(formListener);
        toolBar.add(posterizeSlider);
        toolBar.add(jSeparator8);

        bandExtractor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Bands.png"))); // NOI18N
        bandExtractor.setToolTipText("Extraer bandas");
        bandExtractor.addActionListener(formListener);
        toolBar.add(bandExtractor);

        colorSpaceComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "sRGB", "YCC", "Grey", "YCbCr" }));
        colorSpaceComboBox.setToolTipText("Espacio de color");
        colorSpaceComboBox.setMaximumSize(new java.awt.Dimension(70, 23));
        colorSpaceComboBox.setMinimumSize(new java.awt.Dimension(70, 23));
        colorSpaceComboBox.setPreferredSize(new java.awt.Dimension(70, 23));
        colorSpaceComboBox.addActionListener(formListener);
        toolBar.add(colorSpaceComboBox);
        toolBar.add(jSeparator9);

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Challenges.png"))); // NOI18N
        toolBar.add(jLabel7);

        challengesComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Prác. 10: Umbral" }));
        challengesComboBox.setToolTipText("Otros retos");
        challengesComboBox.setMaximumSize(new java.awt.Dimension(160, 23));
        challengesComboBox.setMinimumSize(new java.awt.Dimension(160, 23));
        challengesComboBox.setName(""); // NOI18N
        challengesComboBox.setPreferredSize(new java.awt.Dimension(160, 23));
        challengesComboBox.addFocusListener(formListener);
        challengesComboBox.addActionListener(formListener);
        toolBar.add(challengesComboBox);

        ownOperatorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Own.png"))); // NOI18N
        ownOperatorButton.setToolTipText("Mostrar/Ocultar Prác.12: Operador propio");
        ownOperatorButton.setFocusable(false);
        ownOperatorButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ownOperatorButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ownOperatorButton.addActionListener(formListener);
        toolBar.add(ownOperatorButton);

        ownOperatorSlider.setMaximum(256);
        ownOperatorSlider.setToolTipText("Prác. 12: Operador propio");
        ownOperatorSlider.setValue(0);
        ownOperatorSlider.setMaximumSize(new java.awt.Dimension(100, 26));
        ownOperatorSlider.setMinimumSize(new java.awt.Dimension(100, 26));
        ownOperatorSlider.setPreferredSize(new java.awt.Dimension(100, 26));
        ownOperatorSlider.addChangeListener(formListener);
        ownOperatorSlider.addFocusListener(formListener);
        toolBar.add(ownOperatorSlider);

        ownLookupOp.setMaximum(50);
        ownLookupOp.setMinimum(-50);
        ownLookupOp.setToolTipText("LookupOp Propio");
        ownLookupOp.setValue(0);
        ownLookupOp.setMaximumSize(new java.awt.Dimension(100, 26));
        ownLookupOp.setMinimumSize(new java.awt.Dimension(100, 26));
        ownLookupOp.setPreferredSize(new java.awt.Dimension(100, 26));
        ownLookupOp.addChangeListener(formListener);
        ownLookupOp.addFocusListener(formListener);
        toolBar.add(ownLookupOp);
        toolBar.add(jSeparator10);

        histogram.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Histogram.png"))); // NOI18N
        histogram.setFocusable(false);
        histogram.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        histogram.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        histogram.addActionListener(formListener);
        toolBar.add(histogram);

        getContentPane().add(toolBar, java.awt.BorderLayout.PAGE_START);

        statusBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        statusBar.setLayout(new java.awt.BorderLayout());

        statusBarTitle.setText("Barra de Estado");
        statusBarTitle.setPreferredSize(new java.awt.Dimension(300, 14));
        statusBar.add(statusBarTitle, java.awt.BorderLayout.WEST);
        statusBar.add(statusBarVariable, java.awt.BorderLayout.EAST);
        statusBar.add(shapeStatusBar, java.awt.BorderLayout.CENTER);

        getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);

        containerPanel.setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);

        play.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Play.png"))); // NOI18N
        play.setToolTipText("Play");
        play.setFocusable(false);
        play.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        play.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        play.addActionListener(formListener);
        jToolBar1.add(play);

        stop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Stop.png"))); // NOI18N
        stop.setToolTipText("Stop");
        stop.setFocusable(false);
        stop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        stop.addActionListener(formListener);
        jToolBar1.add(stop);

        audioComboBox.setToolTipText("Selector de audios");
        audioComboBox.setMaximumSize(new java.awt.Dimension(150, 26));
        audioComboBox.setMinimumSize(new java.awt.Dimension(150, 26));
        audioComboBox.setPreferredSize(new java.awt.Dimension(150, 26));
        audioComboBox.addActionListener(formListener);
        jToolBar1.add(audioComboBox);
        jToolBar1.add(jSeparator12);

        rec.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Rec.png"))); // NOI18N
        rec.setFocusable(false);
        rec.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rec.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rec.addActionListener(formListener);
        jToolBar1.add(rec);

        recordingTime.setText("00:00");
        jToolBar1.add(recordingTime);
        jToolBar1.add(jSeparator13);

        webcamButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Webcam.png"))); // NOI18N
        webcamButton.setFocusable(false);
        webcamButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        webcamButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        webcamButton.addActionListener(formListener);
        jToolBar1.add(webcamButton);

        webcamComboBox.setMaximumSize(new java.awt.Dimension(100, 26));
        webcamComboBox.addActionListener(formListener);
        jToolBar1.add(webcamComboBox);

        dimensionComboBox.setMaximumSize(new java.awt.Dimension(100, 26));
        dimensionComboBox.setMinimumSize(new java.awt.Dimension(100, 26));
        dimensionComboBox.setPreferredSize(new java.awt.Dimension(100, 26));
        jToolBar1.add(dimensionComboBox);

        screenShotButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1-Screenshot.png"))); // NOI18N
        screenShotButton.setFocusable(false);
        screenShotButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        screenShotButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        screenShotButton.addActionListener(formListener);
        jToolBar1.add(screenShotButton);

        containerPanel.add(jToolBar1, java.awt.BorderLayout.SOUTH);

        desktop.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        desktop.setPreferredSize(new java.awt.Dimension(800, 600));

        javax.swing.GroupLayout desktopLayout = new javax.swing.GroupLayout(desktop);
        desktop.setLayout(desktopLayout);
        desktopLayout.setHorizontalGroup(
            desktopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2071, Short.MAX_VALUE)
        );
        desktopLayout.setVerticalGroup(
            desktopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 628, Short.MAX_VALUE)
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
        imageMenu.add(jSeparator11);

        tintItemMenu.setText("Tintar imagen");
        tintItemMenu.addActionListener(formListener);
        imageMenu.add(tintItemMenu);

        redHighlightItemMenu.setText("Resaltar tonos rojos de la imagen");
        redHighlightItemMenu.addActionListener(formListener);
        imageMenu.add(redHighlightItemMenu);

        menu.add(imageMenu);

        viewMenu.setText("Ver");

        statusBarMenu.setSelected(true);
        statusBarMenu.setText("Barra de estado");
        statusBarMenu.addActionListener(formListener);
        viewMenu.add(statusBarMenu);

        menu.add(viewMenu);

        helpMenu.setText("Ayuda");

        aboutItemMenu.setText("Acerca de");
        aboutItemMenu.addActionListener(formListener);
        helpMenu.add(aboutItemMenu);

        menu.add(helpMenu);

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
            else if (evt.getSource() == openFile) {
                MainFrame.this.openFileActionPerformed(evt);
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
            else if (evt.getSource() == strokeColors) {
                MainFrame.this.strokeColorsActionPerformed(evt);
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
            else if (evt.getSource() == contrastComboBox) {
                MainFrame.this.contrastComboBoxActionPerformed(evt);
            }
            else if (evt.getSource() == filterComboBox) {
                MainFrame.this.filterComboBoxActionPerformed(evt);
            }
            else if (evt.getSource() == rotationButton) {
                MainFrame.this.rotationButtonActionPerformed(evt);
            }
            else if (evt.getSource() == scaleIn) {
                MainFrame.this.scaleInActionPerformed(evt);
            }
            else if (evt.getSource() == scaleOut) {
                MainFrame.this.scaleOutActionPerformed(evt);
            }
            else if (evt.getSource() == quadraticFunctionButton) {
                MainFrame.this.quadraticFunctionButtonActionPerformed(evt);
            }
            else if (evt.getSource() == bandCombination) {
                MainFrame.this.bandCombinationActionPerformed(evt);
            }
            else if (evt.getSource() == greenBandCombination) {
                MainFrame.this.greenBandCombinationActionPerformed(evt);
            }
            else if (evt.getSource() == tint) {
                MainFrame.this.tintActionPerformed(evt);
            }
            else if (evt.getSource() == autoTintOp) {
                MainFrame.this.autoTintOpActionPerformed(evt);
            }
            else if (evt.getSource() == sepia) {
                MainFrame.this.sepiaActionPerformed(evt);
            }
            else if (evt.getSource() == equalization) {
                MainFrame.this.equalizationActionPerformed(evt);
            }
            else if (evt.getSource() == redHighlight) {
                MainFrame.this.redHighlightActionPerformed(evt);
            }
            else if (evt.getSource() == posterizeButton) {
                MainFrame.this.posterizeButtonActionPerformed(evt);
            }
            else if (evt.getSource() == bandExtractor) {
                MainFrame.this.bandExtractorActionPerformed(evt);
            }
            else if (evt.getSource() == colorSpaceComboBox) {
                MainFrame.this.colorSpaceComboBoxActionPerformed(evt);
            }
            else if (evt.getSource() == challengesComboBox) {
                MainFrame.this.challengesComboBoxActionPerformed(evt);
            }
            else if (evt.getSource() == ownOperatorButton) {
                MainFrame.this.ownOperatorButtonActionPerformed(evt);
            }
            else if (evt.getSource() == histogram) {
                MainFrame.this.histogramActionPerformed(evt);
            }
            else if (evt.getSource() == play) {
                MainFrame.this.playActionPerformed(evt);
            }
            else if (evt.getSource() == stop) {
                MainFrame.this.stopActionPerformed(evt);
            }
            else if (evt.getSource() == audioComboBox) {
                MainFrame.this.audioComboBoxActionPerformed(evt);
            }
            else if (evt.getSource() == rec) {
                MainFrame.this.recActionPerformed(evt);
            }
            else if (evt.getSource() == webcamButton) {
                MainFrame.this.webcamButtonActionPerformed(evt);
            }
            else if (evt.getSource() == webcamComboBox) {
                MainFrame.this.webcamComboBoxActionPerformed(evt);
            }
            else if (evt.getSource() == screenShotButton) {
                MainFrame.this.screenShotButtonActionPerformed(evt);
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
            else if (evt.getSource() == tintItemMenu) {
                MainFrame.this.tintItemMenuActionPerformed(evt);
            }
            else if (evt.getSource() == redHighlightItemMenu) {
                MainFrame.this.redHighlightItemMenuActionPerformed(evt);
            }
            else if (evt.getSource() == statusBarMenu) {
                MainFrame.this.statusBarMenuActionPerformed(evt);
            }
            else if (evt.getSource() == aboutItemMenu) {
                MainFrame.this.aboutItemMenuActionPerformed(evt);
            }
        }

        public void focusGained(java.awt.event.FocusEvent evt) {
            if (evt.getSource() == brightnessSlider) {
                MainFrame.this.brightnessSliderFocusGained(evt);
            }
            else if (evt.getSource() == filterComboBox) {
                MainFrame.this.filterComboBoxFocusGained(evt);
            }
            else if (evt.getSource() == rotationSlider) {
                MainFrame.this.rotationSliderFocusGained(evt);
            }
            else if (evt.getSource() == quadraticFunctionSlider) {
                MainFrame.this.quadraticFunctionSliderFocusGained(evt);
            }
            else if (evt.getSource() == tintSlider) {
                MainFrame.this.tintSliderFocusGained(evt);
            }
            else if (evt.getSource() == redHighlightSlider) {
                MainFrame.this.redHighlightSliderFocusGained(evt);
            }
            else if (evt.getSource() == posterizeSlider) {
                MainFrame.this.posterizeSliderFocusGained(evt);
            }
            else if (evt.getSource() == challengesComboBox) {
                MainFrame.this.challengesComboBoxFocusGained(evt);
            }
            else if (evt.getSource() == ownOperatorSlider) {
                MainFrame.this.ownOperatorSliderFocusGained(evt);
            }
            else if (evt.getSource() == ownLookupOp) {
                MainFrame.this.ownLookupOpFocusGained(evt);
            }
        }

        public void focusLost(java.awt.event.FocusEvent evt) {
            if (evt.getSource() == brightnessSlider) {
                MainFrame.this.brightnessSliderFocusLost(evt);
            }
            else if (evt.getSource() == filterComboBox) {
                MainFrame.this.filterComboBoxFocusLost(evt);
            }
            else if (evt.getSource() == rotationSlider) {
                MainFrame.this.rotationSliderFocusLost(evt);
            }
            else if (evt.getSource() == quadraticFunctionSlider) {
                MainFrame.this.quadraticFunctionSliderFocusLost(evt);
            }
            else if (evt.getSource() == tintSlider) {
                MainFrame.this.tintSliderFocusLost(evt);
            }
            else if (evt.getSource() == redHighlightSlider) {
                MainFrame.this.redHighlightSliderFocusLost(evt);
            }
            else if (evt.getSource() == posterizeSlider) {
                MainFrame.this.posterizeSliderFocusLost(evt);
            }
            else if (evt.getSource() == challengesComboBox) {
                MainFrame.this.challengesComboBoxFocusLost(evt);
            }
            else if (evt.getSource() == ownOperatorSlider) {
                MainFrame.this.ownOperatorSliderFocusLost(evt);
            }
            else if (evt.getSource() == ownLookupOp) {
                MainFrame.this.ownLookupOpFocusLost(evt);
            }
        }

        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            if (evt.getSource() == widthStroke) {
                MainFrame.this.widthStrokeStateChanged(evt);
            }
            else if (evt.getSource() == brightnessSlider) {
                MainFrame.this.brightnessSliderStateChanged(evt);
            }
            else if (evt.getSource() == rotationSlider) {
                MainFrame.this.rotationSliderStateChanged(evt);
            }
            else if (evt.getSource() == quadraticFunctionSlider) {
                MainFrame.this.quadraticFunctionSliderStateChanged(evt);
            }
            else if (evt.getSource() == tintSlider) {
                MainFrame.this.tintSliderStateChanged(evt);
            }
            else if (evt.getSource() == redHighlightSlider) {
                MainFrame.this.redHighlightSliderStateChanged(evt);
            }
            else if (evt.getSource() == posterizeSlider) {
                MainFrame.this.posterizeSliderStateChanged(evt);
            }
            else if (evt.getSource() == ownOperatorSlider) {
                MainFrame.this.ownOperatorSliderStateChanged(evt);
            }
            else if (evt.getSource() == ownLookupOp) {
                MainFrame.this.ownLookupOpStateChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void newMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuActionPerformed
        _newCanvas();
    }//GEN-LAST:event_newMenuActionPerformed

    private void saveMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuActionPerformed
        _saveFile();
    }//GEN-LAST:event_saveMenuActionPerformed

    private void openMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuActionPerformed
        _openFile();
    }//GEN-LAST:event_openMenuActionPerformed

    private void generalPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generalPathActionPerformed
        _toolsSelectedToFalse();
        generalPath.setSelected(true);
        shapeStatusBar.setText("Dibujando: Trazo libre");
        if(internalFrame != null){
            internalFrame.getCanvas2D().setActiveShape(
                    Canvas2D.EnumShape.GENERALPATH
            );
            internalFrame.getCanvas2D().setCursor(
                    new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR)
            );
        }
    }//GEN-LAST:event_generalPathActionPerformed

    private void lineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineActionPerformed
        _toolsSelectedToFalse();
        line.setSelected(true);
        shapeStatusBar.setText("Dibujando: Línea");
        if(internalFrame != null){
            internalFrame.getCanvas2D().setActiveShape(Canvas2D.EnumShape.LINE);
            internalFrame.getCanvas2D().setCursor(
                    new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR)
            );
        }
    }//GEN-LAST:event_lineActionPerformed

    private void rectangleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rectangleActionPerformed
        _toolsSelectedToFalse();
        rectangle.setSelected(true);
        shapeStatusBar.setText("Dibujando: Rectángulo");
        if(internalFrame != null){
            internalFrame.getCanvas2D().setActiveShape(
                    Canvas2D.EnumShape.RECTANGLE
            );
            internalFrame.getCanvas2D().setCursor(
                    new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR)
            );
        }
    }//GEN-LAST:event_rectangleActionPerformed

    private void ellipseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseActionPerformed
        _toolsSelectedToFalse();
        ellipse.setSelected(true);
        shapeStatusBar.setText("Dibujando: Elipse");
        if(internalFrame != null){
            internalFrame.getCanvas2D().setActiveShape(
                    Canvas2D.EnumShape.ELLIPSE
            );
            internalFrame.getCanvas2D().setCursor(
                    new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR)
            );
        }
    }//GEN-LAST:event_ellipseActionPerformed

    private void statusBarMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusBarMenuActionPerformed
        if(statusBar.isVisible())
            statusBar.setVisible(false);
        else
            statusBar.setVisible(true);
    }//GEN-LAST:event_statusBarMenuActionPerformed

    private void selectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectorActionPerformed
        _toolsSelectedToFalse();
        selector.setSelected(true);
        shapeStatusBar.setText("Figura seleccionada: Ninguna");
        if(internalFrame != null){
            internalFrame.getCanvas2D().setActualShape(null);
            internalFrame.getCanvas2D().setSelectorMode(true);
            internalFrame.getCanvas2D().setCursor(
                    new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)
            );
        }
    }//GEN-LAST:event_selectorActionPerformed

    private void fillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillActionPerformed
        if(internalFrame != null){
            internalFrame.getCanvas2D().setFillMode(fill.isSelected());
            if(selector.isSelected()){
                AJRShape2D actualShape = 
                        internalFrame.getCanvas2D().getActualShape();
                if(actualShape instanceof AJRFillShape2D){
                    ((AJRFillShape2D)actualShape).setIsFill(fill.isSelected());
                }
            }    
        }
        desktop.repaint();
    }//GEN-LAST:event_fillActionPerformed

    private void widthStrokeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_widthStrokeStateChanged
        if((int)widthStroke.getValue() < 1)
            widthStroke.setValue(1);
        else if(internalFrame != null){
            internalFrame.getCanvas2D().setWidthStroke(
                    (int)widthStroke.getValue()
            );
            if(selector.isSelected()){
                AJRShape2D actualShape = 
                        internalFrame.getCanvas2D().getActualShape();
                float dash[] = null;
                actualShape.setStroke(
                        new BasicStroke(
                                (int)widthStroke.getValue(),
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_MITER,
                                1.0f,
                                dash,
                                0.0f
                        )
                );
            }
        }
        desktop.repaint();
    }//GEN-LAST:event_widthStrokeStateChanged

    private void antialiasingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_antialiasingActionPerformed
        if(internalFrame != null){
            internalFrame.getCanvas2D().setAntialiasingMode(
                    antialiasing.isSelected()
            );
            if(selector.isSelected()){
                AJRShape2D actualShape = 
                        internalFrame.getCanvas2D().getActualShape();
                actualShape.setHasAntialiasing(antialiasing.isSelected());
            }
        }
        desktop.repaint();
    }//GEN-LAST:event_antialiasingActionPerformed

    private void colorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorsActionPerformed
        if(internalFrame != null){
            internalFrame.getCanvas2D().setActiveColor(
                    (Color)colors.getSelectedItem()
            );
            if(selector.isSelected()){
                AJRShape2D actualShape = 
                        internalFrame.getCanvas2D().getActualShape();
                if(actualShape instanceof AJRFillShape2D){
                    ((AJRFillShape2D)actualShape).setFillColor(
                            (Color)colors.getSelectedItem()
                    );
                }
            }
        }
        desktop.repaint();
    }//GEN-LAST:event_colorsActionPerformed

    private void newCanvasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCanvasActionPerformed
        _newCanvas();
    }//GEN-LAST:event_newCanvasActionPerformed

    private void openFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileActionPerformed
        _openFile();
    }//GEN-LAST:event_openFileActionPerformed

    private void saveCanvasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCanvasActionPerformed
        this._saveFile();
    }//GEN-LAST:event_saveCanvasActionPerformed

    private void transparencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transparencyActionPerformed
        if(internalFrame != null){
            internalFrame.getCanvas2D().setTransparencyMode(
                    transparency.isSelected());
            AlphaComposite alphaComp = 
                        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
            if(transparency.isSelected()){
                alphaComp = 
                        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                internalFrame.getCanvas2D().setActiveComposite(alphaComp);
            }else{
                internalFrame.getCanvas2D().setActiveComposite(alphaComp);
            }
            if(selector.isSelected()){
                AJRShape2D actualShape = 
                        internalFrame.getCanvas2D().getActualShape();
                actualShape.setComposite(alphaComp);
            }
        }
    }//GEN-LAST:event_transparencyActionPerformed

    private void tipOverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipOverActionPerformed
        _tipOverShapes();
    }//GEN-LAST:event_tipOverActionPerformed

    private void windowsEffectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_windowsEffectActionPerformed
        if(internalFrame != null){
            internalFrame.getCanvas2D().setWindowsEffectMode(
                windowsEffect.isSelected());
        }
        repaint();
    }//GEN-LAST:event_windowsEffectActionPerformed

    private void newSizeImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSizeImageActionPerformed
        _throwDialogMeassureAndSetImage(canvasWidth, canvasHeight);
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
            _tipOverShapes();
        this._swingControlsFocusGained();
    }//GEN-LAST:event_brightnessSliderFocusGained

    private void brightnessSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_brightnessSliderFocusLost
        sourceImage = null;
        brightnessSlider.setValue(0);
    }//GEN-LAST:event_brightnessSliderFocusLost

    private void filterComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterComboBoxActionPerformed
        if(internalFrame != null){
            try {
                Kernel kernel = _getKernelFromSelect(filterComboBox.getSelectedIndex());
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
        this._swingControlsFocusGained();
    }//GEN-LAST:event_filterComboBoxFocusGained

    private void filterComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filterComboBoxFocusLost
        sourceImage = null;
    }//GEN-LAST:event_filterComboBoxFocusLost

    private void rotationSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rotationSliderFocusGained
        this._swingControlsFocusGained();
    }//GEN-LAST:event_rotationSliderFocusGained

    private void rotationSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rotationSliderStateChanged
        this._applyRotation(rotationSlider.getValue());
    }//GEN-LAST:event_rotationSliderStateChanged

    private void rotationSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rotationSliderFocusLost
        sourceImage = null;
        rotationSlider.setValue(0);
    }//GEN-LAST:event_rotationSliderFocusLost

    private void scaleInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleInActionPerformed
        this._swingControlsFocusGained();
        this.scaleImage(1.25f, 1.25f);
        sourceImage = null;
    }//GEN-LAST:event_scaleInActionPerformed

    private void scaleOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleOutActionPerformed
        this._swingControlsFocusGained();
        this.scaleImage(0.75f, 0.75f);
        sourceImage = null;
    }//GEN-LAST:event_scaleOutActionPerformed

    private void quadraticFunctionSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_quadraticFunctionSliderFocusGained
        this._swingControlsFocusGained();
    }//GEN-LAST:event_quadraticFunctionSliderFocusGained

    private void quadraticFunctionSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_quadraticFunctionSliderFocusLost
        sourceImage = null;
        quadraticFunctionSlider.setValue(0);
    }//GEN-LAST:event_quadraticFunctionSliderFocusLost

    private void quadraticFunctionSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_quadraticFunctionSliderStateChanged
        this._applyLookup(
                this.quadraticFunctionTable(quadraticFunctionSlider.getValue())
        );
    }//GEN-LAST:event_quadraticFunctionSliderStateChanged

    private void duplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplicateActionPerformed
        if(internalFrame != null){
            BufferedImage image = internalFrame.getCanvas2D().getImage(false);
            String title = internalFrame.getTitle(),
                    extension = _getExtension(title),
                    duplicateTitle = title.replace("." + extension, "") 
                    + "-copia." + extension;
            _newCanvas();
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
        }
    }//GEN-LAST:event_duplicateActionPerformed

    private void quadraticFunctionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quadraticFunctionButtonActionPerformed
        quadraticFunctionSlider.setVisible(!quadraticFunctionSlider.isVisible());
    }//GEN-LAST:event_quadraticFunctionButtonActionPerformed

    private void bandCombinationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bandCombinationActionPerformed
        this._swingControlsFocusGained();
        float[][] matrix = {
            {0.0F, 0.5F, 0.5F},
            {0.5F, 0.0F, 0.5F},
            {0.5F, 0.5F, 0.0F}
        };
        this._applyCombineOp(matrix);
        sourceImage = null;
    }//GEN-LAST:event_bandCombinationActionPerformed

    private void greenBandCombinationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_greenBandCombinationActionPerformed
        this._swingControlsFocusGained();
        float[][] matrix = {
            {0.0F, 0.6F, 0.4F},
            {0.0F, 1.0F, 0.0F},
            {0.0F, 0.0F, 1.0F}
        };
        this._applyCombineOp(matrix);
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
                    BufferedImage bandImage = _getImageBand(image, i);
                    
                    iF = new InternalFrame();
                    desktop.add(iF);
                    iF.setVisible(true);
                    _initializeCanvas(iF);
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
                
                int srcImgCSType = 
                        sourceImage.getColorModel().getColorSpace().getType();
                if(colorSpace != null && colorSpace.getType() != srcImgCSType){
                    ColorConvertOp op = new ColorConvertOp(colorSpace, null);
                    BufferedImage image = op.filter(sourceImage, null);

                    InternalFrame iF = new InternalFrame();
                    desktop.add(iF);
                    iF.setVisible(true);
                    _initializeCanvas(iF);
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
        tintSlider.setVisible(!tintSlider.isVisible());
    }//GEN-LAST:event_tintActionPerformed

    private void sepiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sepiaActionPerformed
        this._swingControlsFocusGained();
        if(sourceImage != null){
            SepiaOp sepiaOp = new sm.image.SepiaOp();
            sepiaOp.filter(
                    sourceImage,
                    internalFrame.getCanvas2D().getImage(false)
            );
            sourceImage = null;
            desktop.repaint();
        }
    }//GEN-LAST:event_sepiaActionPerformed

    private void equalizationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_equalizationActionPerformed
        this._swingControlsFocusGained();
        if(sourceImage != null){
            EqualizationOp equalizationOp = new sm.image.EqualizationOp();
            equalizationOp.filter(
                    sourceImage, internalFrame.getCanvas2D().getImage(false)
            );
            sourceImage = null;
            desktop.repaint();
        }
    }//GEN-LAST:event_equalizationActionPerformed

    private void posterizeSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_posterizeSliderFocusGained
        this._swingControlsFocusGained();
    }//GEN-LAST:event_posterizeSliderFocusGained

    private void posterizeSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_posterizeSliderFocusLost
        sourceImage = null;
        posterizeSlider.setValue(40);
    }//GEN-LAST:event_posterizeSliderFocusLost

    private void posterizeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_posterizeSliderStateChanged
        if(sourceImage != null){
            PosterizeOp posterizeOp = new PosterizeOp(posterizeSlider.getValue());
            posterizeOp.filter(
                    sourceImage, 
                    internalFrame.getCanvas2D().getImage(false)
            );
            desktop.repaint();
        }
    }//GEN-LAST:event_posterizeSliderStateChanged

    private void redHighlightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redHighlightActionPerformed
        boolean action = !redHighlightSlider.isVisible();
        redHighlightSlider.setVisible(action);
    }//GEN-LAST:event_redHighlightActionPerformed

    private void challengesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_challengesComboBoxActionPerformed
        if(internalFrame != null){
            String option = (String)challengesComboBox.getSelectedItem();

            switch(option){
                case "Prác. 10: Umbral":
                    this._applyLookup(this.thresholdFunctionTable(127));
                    break;
            }
        }   
    }//GEN-LAST:event_challengesComboBoxActionPerformed

    private void challengesComboBoxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_challengesComboBoxFocusGained
        this._swingControlsFocusGained();
    }//GEN-LAST:event_challengesComboBoxFocusGained

    private void challengesComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_challengesComboBoxFocusLost
        sourceImage = null;
    }//GEN-LAST:event_challengesComboBoxFocusLost

    private void rotationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotationButtonActionPerformed
        boolean action = !rotationSlider.isVisible();
        firstRotationLabel.setVisible(action);
        secondRotationLabel.setVisible(action);
        rotationSlider.setVisible(action);
    }//GEN-LAST:event_rotationButtonActionPerformed

    private void tintItemMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tintItemMenuActionPerformed
        this._swingControlsFocusGained();
        if(sourceImage != null){
            AutoTintOp tintOp = new AutoTintOp(
                    (Color)colors.getSelectedItem()
            );
            tintOp.filter(
                    sourceImage, 
                    internalFrame.getCanvas2D().getImage(false)
            );
            sourceImage = null;
            desktop.repaint();
        }
    }//GEN-LAST:event_tintItemMenuActionPerformed

    private void tintSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tintSliderFocusGained
        this._swingControlsFocusGained();
    }//GEN-LAST:event_tintSliderFocusGained

    private void tintSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tintSliderFocusLost
        sourceImage = null;
        tintSlider.setValue(0);
    }//GEN-LAST:event_tintSliderFocusLost

    private void tintSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tintSliderStateChanged
        if(sourceImage != null){
            TintOp tintOp = new sm.image.TintOp(
                    (Color)colors.getSelectedItem(),
                    (float)(tintSlider.getValue()/100.0)
            );
            tintOp.filter(
                    sourceImage, 
                    internalFrame.getCanvas2D().getImage(false)
            );
            desktop.repaint();
        }
    }//GEN-LAST:event_tintSliderStateChanged

    private void redHighlightItemMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redHighlightItemMenuActionPerformed
        this._swingControlsFocusGained();
        if(sourceImage != null){
            RedOp redOp = new RedOp(240);
            redOp.filter(
                    sourceImage, 
                    internalFrame.getCanvas2D().getImage(false)
            );
            sourceImage = null;
            desktop.repaint(); 
        }
    }//GEN-LAST:event_redHighlightItemMenuActionPerformed

    private void redHighlightSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_redHighlightSliderFocusGained
        this._swingControlsFocusGained();
    }//GEN-LAST:event_redHighlightSliderFocusGained

    private void redHighlightSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_redHighlightSliderFocusLost
        sourceImage = null;
        redHighlightSlider.setValue(-512);
    }//GEN-LAST:event_redHighlightSliderFocusLost

    private void redHighlightSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_redHighlightSliderStateChanged
        if(sourceImage != null){
            RedOp redOp = new RedOp(redHighlightSlider.getValue());
            redOp.filter(
                    sourceImage, 
                    internalFrame.getCanvas2D().getImage(false)
            );
            desktop.repaint(); 
        }
    }//GEN-LAST:event_redHighlightSliderStateChanged

    private void posterizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_posterizeButtonActionPerformed
        posterizeSlider.setVisible(!posterizeSlider.isVisible());
    }//GEN-LAST:event_posterizeButtonActionPerformed

    private void contrastComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contrastComboBoxActionPerformed
        this._swingControlsFocusGained();
        if(sourceImage != null){
            LookupTable lookupTable = null;
            switch(contrastComboBox.getSelectedIndex()){
                case 0:
                    lookupTable = 
                            LookupTableProducer.createLookupTable(
                                    LookupTableProducer.TYPE_SFUNCION
                            );
                    break;
                case 1:
                    lookupTable =
                            LookupTableProducer.createLookupTable(
                                    LookupTableProducer.TYPE_ROOT
                            );
                    break;
                case 2:
                    lookupTable =
                            LookupTableProducer.createLookupTable(
                                    LookupTableProducer.TYPE_POWER
                            );
                    break;
                case 3:
                    lookupTable =
                            LookupTableProducer.createLookupTable(
                                    LookupTableProducer.TYPE_NEGATIVE
                            );
                    break;
            }
            if(lookupTable != null)
                this._applyLookup(lookupTable);
            sourceImage = null;
        }
    }//GEN-LAST:event_contrastComboBoxActionPerformed

    private void ownOperatorSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ownOperatorSliderFocusGained
        this._swingControlsFocusGained();
    }//GEN-LAST:event_ownOperatorSliderFocusGained

    private void ownOperatorSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ownOperatorSliderFocusLost
        sourceImage = null;
        ownOperatorSlider.setValue(0);
    }//GEN-LAST:event_ownOperatorSliderFocusLost

    private void ownOperatorSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ownOperatorSliderStateChanged
        if(sourceImage != null){
            OwnOp ownOp = new OwnOp(ownOperatorSlider.getValue());
            ownOp.filter(
                    sourceImage, 
                    internalFrame.getCanvas2D().getImage(false)
            );
            desktop.repaint(); 
        }
    }//GEN-LAST:event_ownOperatorSliderStateChanged

    private void ownOperatorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownOperatorButtonActionPerformed
        ownOperatorSlider.setVisible(!ownOperatorSlider.isVisible());
        ownLookupOp.setVisible(!ownLookupOp.isVisible());
    }//GEN-LAST:event_ownOperatorButtonActionPerformed

    private void autoTintOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoTintOpActionPerformed
        this._swingControlsFocusGained();
        if(sourceImage != null){
            AutoTintOp tintOp = new AutoTintOp(
                    (Color)colors.getSelectedItem()
            );
            tintOp.filter(
                    sourceImage, 
                    internalFrame.getCanvas2D().getImage(false)
            );
            sourceImage = null;
            desktop.repaint();
        }
    }//GEN-LAST:event_autoTintOpActionPerformed

    private void histogramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_histogramActionPerformed
        this._swingControlsFocusGained();
        if(sourceImage != null){
            sm.image.Histogram histogram = new sm.image.Histogram(sourceImage);
            System.out.println(histogram.getNormalizedHistogram(0)[0]);
        }
    }//GEN-LAST:event_histogramActionPerformed

    private void playActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playActionPerformed
        if(desktop.getSelectedFrame() instanceof VideoInternalFrame){
            VideoInternalFrame videoIF = 
                    (VideoInternalFrame)desktop.getSelectedFrame();
            videoIF.play();
        }else{
            if(audioComboBox.getItemCount() > 0){
                if(player == null) {
                    File f = (File) audioComboBox.getSelectedItem();
                    if (f != null) {
                        player = new SMClipPlayer(f);
                        player.addLineListener(audioListener);
                    }
                }
                if(player.getClip().isRunning()){
                    player.pause();
                }else{
                    player.play();
                }
            }
        }
    }//GEN-LAST:event_playActionPerformed

    private void stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopActionPerformed
        if(desktop.getSelectedFrame() instanceof VideoInternalFrame){
            VideoInternalFrame videoIF = 
                    (VideoInternalFrame)desktop.getSelectedFrame();
            videoIF.stop();
        }else{
            if(player != null){
                player.stop();
            }
        }
    }//GEN-LAST:event_stopActionPerformed

    private void audioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audioComboBoxActionPerformed
        File f = (File) audioComboBox.getSelectedItem();
        if (f != null) {
            player = new SMClipPlayer(f);
            player.addLineListener(audioListener);
        }
    }//GEN-LAST:event_audioComboBoxActionPerformed

    private void recActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recActionPerformed
        if(recorder == null){
            try {
                recordingFile = File.createTempFile("auxRecord", ".tmp");
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            recorder = new SMSoundRecorder(recordingFile);
            recorder.addLineListener(recordListener);
            recorder.record();
        }else{
            recorder.stop();
            recorder = null;
            this._saveFile();
//            this.resetTimer();
            recordingFile = null;
        }
    }//GEN-LAST:event_recActionPerformed

    private void aboutItemMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutItemMenuActionPerformed
        JOptionPane.showMessageDialog(
                        null, 
                        "Programa: Aplicación multimedia\n"
                        + "Version: 1.15\n"
                        + "Autor: Antonio Jiménez Rodríguez\n",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_aboutItemMenuActionPerformed

    private void strokeColorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strokeColorsActionPerformed
        if(internalFrame != null){
            internalFrame.getCanvas2D().setActiveStrokeColor(
                    (Color)strokeColors.getSelectedItem()
            );
            if(selector.isSelected()){
                AJRShape2D actualShape = 
                        internalFrame.getCanvas2D().getActualShape();
                actualShape.setStrokeColor((Color)strokeColors.getSelectedItem());
            }
        }
        desktop.repaint();
    }//GEN-LAST:event_strokeColorsActionPerformed

    private void webcamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webcamButtonActionPerformed
        if(webcamIF == null || 
                (webcamIF != null && !webcamIF.getWebcam().isOpen())){
            webcamIF = WebcamInternalFrame.getInstance(
                    (Webcam)webcamComboBox.getSelectedItem(),
                    (Dimension)dimensionComboBox.getSelectedItem()
            );
            if(webcamIF != null){
                desktop.add(webcamIF);
                webcamIF.setVisible(true);
            }
        }
    }//GEN-LAST:event_webcamButtonActionPerformed

    private void webcamComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webcamComboBoxActionPerformed
        _setWebcamDimensions();
    }//GEN-LAST:event_webcamComboBoxActionPerformed

    private void screenShotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_screenShotButtonActionPerformed
        if(desktop.getSelectedFrame() instanceof VideoInternalFrame){
            VideoInternalFrame videoIF = (VideoInternalFrame)desktop.getSelectedFrame();
            InternalFrame iF = new InternalFrame();
            desktop.add(iF);
            iF.setVisible(true);

            _initializeCanvas(iF);
            internalFrame = iF;
            internalFrame.getCanvas2D().setImage(videoIF.getImage());
        }else if(desktop.getSelectedFrame() instanceof WebcamInternalFrame){
            if(webcamIF != null && webcamIF.getWebcam().isOpen()){
                InternalFrame iF = new InternalFrame();
                desktop.add(iF);
                iF.setVisible(true);

                _initializeCanvas(iF);
                internalFrame = iF;
                internalFrame.getCanvas2D().setImage(webcamIF.getImage());
            }
        }
    }//GEN-LAST:event_screenShotButtonActionPerformed

    private void ownLookupOpFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ownLookupOpFocusGained
        this._swingControlsFocusGained();
    }//GEN-LAST:event_ownLookupOpFocusGained

    private void ownLookupOpFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ownLookupOpFocusLost
        sourceImage = null;
        ownOperatorSlider.setValue(0);
    }//GEN-LAST:event_ownLookupOpFocusLost

    private void ownLookupOpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ownLookupOpStateChanged
        this._applyLookup(
                this.ownLookupOpTable(ownLookupOp.getValue())
        );
    }//GEN-LAST:event_ownLookupOpStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutItemMenu;
    private javax.swing.JToggleButton antialiasing;
    private javax.swing.JComboBox<File> audioComboBox;
    private javax.swing.JButton autoTintOp;
    private javax.swing.JButton bandCombination;
    private javax.swing.JButton bandExtractor;
    private javax.swing.JSlider brightnessSlider;
    private javax.swing.JComboBox<String> challengesComboBox;
    private javax.swing.JComboBox<String> colorSpaceComboBox;
    private javax.swing.JComboBox<Color> colors;
    private javax.swing.JPanel containerPanel;
    private javax.swing.JComboBox<ImageIcon> contrastComboBox;
    private javax.swing.JMenuItem copyMenu;
    private javax.swing.JMenuItem cutMenu;
    private javax.swing.JDesktopPane desktop;
    private javax.swing.JComboBox<Dimension> dimensionComboBox;
    private javax.swing.JButton duplicate;
    private javax.swing.JMenu editMenu;
    private javax.swing.JToggleButton ellipse;
    private javax.swing.JButton equalization;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JToggleButton fill;
    private javax.swing.JComboBox<String> filterComboBox;
    private javax.swing.JLabel firstRotationLabel;
    private javax.swing.JToggleButton generalPath;
    private javax.swing.JButton greenBandCombination;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton histogram;
    private javax.swing.JMenu imageMenu;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator12;
    private javax.swing.JToolBar.Separator jSeparator13;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToggleButton line;
    private javax.swing.JMenuBar menu;
    private javax.swing.JButton newCanvas;
    private javax.swing.JMenuItem newMenu;
    private javax.swing.JMenuItem newSizeImage;
    private javax.swing.JButton openFile;
    private javax.swing.JMenuItem openMenu;
    private javax.swing.JSlider ownLookupOp;
    private javax.swing.JButton ownOperatorButton;
    private javax.swing.JSlider ownOperatorSlider;
    private javax.swing.JMenuItem pasteMenu;
    private javax.swing.JButton play;
    private javax.swing.JButton posterizeButton;
    private javax.swing.JSlider posterizeSlider;
    private javax.swing.JMenu printMenu;
    private javax.swing.JMenuItem printerFileMenu;
    private javax.swing.JMenuItem printerMenu;
    private javax.swing.JButton quadraticFunctionButton;
    private javax.swing.JSlider quadraticFunctionSlider;
    private javax.swing.JButton rec;
    private javax.swing.JLabel recordingTime;
    private javax.swing.JToggleButton rectangle;
    private javax.swing.JButton redHighlight;
    private javax.swing.JMenuItem redHighlightItemMenu;
    private javax.swing.JSlider redHighlightSlider;
    private javax.swing.JButton rotationButton;
    private javax.swing.JSlider rotationSlider;
    private javax.swing.JButton saveCanvas;
    private javax.swing.JMenuItem saveMenu;
    private javax.swing.JButton scaleIn;
    private javax.swing.JButton scaleOut;
    private javax.swing.JButton screenShotButton;
    private javax.swing.JLabel secondRotationLabel;
    private javax.swing.JToggleButton selector;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToolBar.Separator separator2;
    private javax.swing.JPopupMenu.Separator separatorFile;
    private javax.swing.JButton sepia;
    private javax.swing.JLabel shapeStatusBar;
    private javax.swing.JPanel statusBar;
    private javax.swing.JCheckBoxMenuItem statusBarMenu;
    private javax.swing.JLabel statusBarTitle;
    private javax.swing.JLabel statusBarVariable;
    private javax.swing.JButton stop;
    private javax.swing.JComboBox<Color> strokeColors;
    private javax.swing.JButton tint;
    private javax.swing.JMenuItem tintItemMenu;
    private javax.swing.JSlider tintSlider;
    private javax.swing.JButton tipOver;
    private javax.swing.JToggleButton tipOverFilters;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JToggleButton transparency;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JButton webcamButton;
    private javax.swing.JComboBox<Webcam> webcamComboBox;
    private javax.swing.JSpinner widthStroke;
    private javax.swing.JToggleButton windowsEffect;
    // End of variables declaration//GEN-END:variables
}

/**
 * Class: EmfPanel
 * Package: edu.colorado.phet.waves.view
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.emf.view;

//import edu.colorado.phet.common.view.ApparatusPanel;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.TargetedImageGraphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.AffineTransformFactory;
import edu.colorado.phet.coreadditions.graphics.ImageGraphic;
import edu.colorado.phet.emf.model.Electron;
import edu.colorado.phet.emf.model.EmfModel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class EmfPanel extends ApparatusPanel {
    private FieldLatticeView fieldLatticeView;
    private Dimension size = new Dimension();
    private BufferedImage bi;
    public static int NO_FIELD = 1;
    public static int FULL_FIELD = 2;
    public static int CURVE = 3;
    public static int CURVE_WITH_VECTORS = 4;

    public void setUseBufferedImage( boolean useBufferedImage ) {
        this.useBufferedImage = useBufferedImage;
    }

    private boolean useBufferedImage = false;

    public EmfPanel( EmfModel model, Electron electron, Point origin, int fieldWidth, int fieldHeight ) {

        // Set up with an identity affine transform
        super( new AffineTransformFactory() {
            public AffineTransform getTx( Rectangle rectangle ) {
                return AffineTransform.getScaleInstance( 1, 1 );
            }
        } );

        EmfPanel.setInstance( this );

        // Add the field lattice
//        int latticeSpacingX = 10;
//        int latticeSpacingY = 10;
        int latticeSpacingX = 50;
        int latticeSpacingY = 50;
        fieldLatticeView = new FieldLatticeView( electron,
                                                 new Point( 0, 0 ),
                                                 fieldWidth - latticeSpacingX, fieldHeight,
                                                 latticeSpacingX,
                                                 latticeSpacingY );
        addGraphic( fieldLatticeView, 4 );

        // Add the background
        final BufferedImage im = GraphicsUtil.toBufferedImage( new ImageLoader().loadBufferedImage( "images/background.gif" ) );
        fieldLatticeView.paintDots( im.getGraphics() );

        addGraphic( new Graphic() {
            public void paint( Graphics2D g ) {
                g.drawImage( im, 0, 0, EmfPanel.this );
            }
        }, 0 );

        // Observe the model
        model.addObserver( this );
    }

    public void setFieldCurvesVisible( boolean enabled ) {
        fieldLatticeView.setFieldCurvesEnabled( enabled );
    }

    protected void paintComponent( Graphics graphics ) {
        if( useBufferedImage ) {
            if( size.getWidth() != this.getSize().getWidth()
                    || size.getHeight() != this.getSize().getHeight() ) {
                size.setSize( this.getSize() );
                bi = new BufferedImage( (int)size.getWidth(), (int)size.getHeight(), BufferedImage.TYPE_INT_RGB );
            }
            Graphics gBI = bi.getGraphics();
            super.paintComponent( gBI );
            graphics.drawImage( bi, 0, 0, this );
            gBI.dispose();
        }
        else {
            super.paintComponent( graphics );
        }
    }

    //
    // Static fields and methods
    //
    private static EmfPanel s_instance;

    private static void setInstance( EmfPanel panel ) {
        s_instance = panel;
    }

    public static EmfPanel instance() {
        return s_instance;
    }

    public void setAutoscaleEnabled( boolean enabled ) {
        fieldLatticeView.setAutoscaleEnabled( enabled );
    }


    private static int s_latticePtDiam = 5;
    private static BufferedImage s_latticePtImg = new BufferedImage( s_latticePtDiam,
                                                                     s_latticePtDiam,
                                                                     BufferedImage.TYPE_INT_ARGB );

    public void displayStaticField( boolean display ) {
        fieldLatticeView.setDisplayStaticField( display );
    }

    public void displayDynamicField( boolean display ) {
        fieldLatticeView.setDisplayDynamicField( display );
    }

    public void setFieldSense( int fieldSense ) {
        fieldLatticeView.setFieldSense( fieldSense );
    }

    public void setFieldDisplay( int display ) {
        fieldLatticeView.setDisplay( display );
    }

    static {     // Create a graphics context on the buffered image
        Graphics2D g2d = s_latticePtImg.createGraphics();

        // Draw on the image
        g2d.setColor( Color.blue );
        g2d.drawArc( 0, 0,
                     2, 2,
                     0, 360 );
        g2d.dispose();
    }

}

/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.nodes.HTMLNode;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * BoxOfHydrogenNode is the small "box of hydrogen" into which
 * the gun fires photons and alpha particles.  A "tiny box"
 * indicates the portion of the box of hydrogen that is shown
 * in the "exploded" view.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoxOfHydrogenNode extends PNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Paint BOX_FRONT_PAINT = new Color( 106, 112, 49 ); // gold

    private static final Color TOP_COLOR_FRONT = Color.GRAY;
    private static final Color TOP_COLOR_BACK = Color.DARK_GRAY;
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;
    
    private static final float BACK_DEPTH = 10f;
    private static final float BACK_OFFSET = 0.15f;
    
    private static final double Y_SPACING = 5;  // space between label and box
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PNode _tinyBoxNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param boxSize
     * @param tinyBoxSize
     */
    public BoxOfHydrogenNode( Dimension boxSize, Dimension tinyBoxSize ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        // Box, origin in upper-left corner of bounds
        PNode boxNode = new PNode();
        {
            final float w = (float)boxSize.width;
            GeneralPath topPath = new GeneralPath();
            topPath.moveTo( BACK_OFFSET * w, 0 );
            topPath.lineTo( ( 1 - BACK_OFFSET ) * w, 0 );
            topPath.lineTo( w, BACK_DEPTH );
            topPath.lineTo( 0, BACK_DEPTH ); 
            topPath.closePath();
            PPath topNode = new PPath();
            topNode.setPathTo( topPath );
            topNode.setPaint( new GradientPaint( 0f, 0f, TOP_COLOR_BACK, 0f, BACK_DEPTH, TOP_COLOR_FRONT ) );
            topNode.setStroke( STROKE );
            topNode.setStrokePaint( STROKE_COLOR );
            
            PPath frontNode = new PPath( new Rectangle2D.Double( 0, BACK_DEPTH, boxSize.width, boxSize.height ) );
            frontNode.setPaint( BOX_FRONT_PAINT );
            frontNode.setStroke( STROKE );
            frontNode.setStrokePaint( STROKE_COLOR );
            
            boxNode.addChild( frontNode );
            boxNode.addChild( topNode );
        }

        // Tiny box
        _tinyBoxNode = new AnimationBoxNode( tinyBoxSize );
        
        // Label, origin in upper-left corner of bounds
        HTMLNode labelNode = new HTMLNode();
        labelNode.setHTML( SimStrings.get( "label.boxOfHydrogen" ) );
        labelNode.setHTMLColor( RSConstants.CANVAS_LABELS_COLOR );
        labelNode.setFont( RSConstants.DEFAULT_FONT );
        
        // Layering
        addChild( boxNode );
        addChild( _tinyBoxNode );
        addChild( labelNode );
        
        // Label centered above box, orgin in upper-left corner of bounds
        final double labelWidth = labelNode.getFullBounds().getWidth();
        final double boxWidth = boxNode.getFullBounds().getWidth();
        if ( boxWidth > labelWidth ) {
            labelNode.setOffset( ( boxWidth - labelWidth ) / 2, 0 );
            boxNode.setOffset( 0, labelNode.getFullBounds().getHeight() + Y_SPACING );
        }
        else {
            labelNode.setOffset( 0, 0 );
            boxNode.setOffset( ( labelWidth - boxWidth ) / 2, labelNode.getFullBounds().getHeight() + Y_SPACING );
        }
        
        // Tiny box in upper right quadrant of box
        double x = boxNode.getFullBounds().getX() + ( 0.6 * boxNode.getFullBounds().getWidth() );
        double y = boxNode.getFullBounds().getY() + ( 0.5 * boxNode.getFullBounds().getHeight() );
        _tinyBoxNode.setOffset( x, y );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the global full bounds of the tiny box that shows the "exploded" 
     * part of the box of hydrogen.  We use these bounds to attached
     * dashed lines between the box of hydrogen and the exploded view.
     * 
     * @return PBounds
     */
    public Rectangle2D getTinyBoxGlobalFullBounds() {
        return _tinyBoxNode.getGlobalFullBounds();
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Key for a surface's color scheme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SurfaceColorKeyNode extends PComposite {

    private static final Dimension SIZE = new Dimension( 400, 20 );
    private static final Font TITLE_FONT = new PhetFont( Font.BOLD, 14 );
    private static final Font RANGE_FONT = new PhetFont( 12 );
    private static final double Y_SPACING = 3;
    private static final double X_INSET = 5;

    // Color key for "electron density" surface representation.
    public static class ElectronDensityColorKeyNode extends SurfaceColorKeyNode {
        public ElectronDensityColorKeyNode() {
            super( new Color[] { Color.WHITE, Color.BLACK },
                   MPStrings.ELECTRON_DENSITY, MPStrings.LESS, MPStrings.MORE );
        }
    }

    // Color key for primary "electrostatic potential" surface representation.
    public static class ElectrostaticPotentialColorKeyNode extends SurfaceColorKeyNode {
        public ElectrostaticPotentialColorKeyNode() {
            super( new Color[] { Color.BLUE, Color.WHITE, Color.RED },
                   MPStrings.ELECTROSTATIC_POTENTIAL, MPStrings.POSITIVE, MPStrings.NEGATIVE );
        }
    }

    // Color key for secondary "electrostatic potential" surface representation.
    //TODO white seams are visible between the segments in this color key
    public static class RainbowElectrostaticPotentialColorKeyNode extends SurfaceColorKeyNode {
        public RainbowElectrostaticPotentialColorKeyNode() {
            super( new Color[] { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, VisibleColor.INDIGO, VisibleColor.VIOLET },
                   MPStrings.ELECTROSTATIC_POTENTIAL, MPStrings.POSITIVE, MPStrings.NEGATIVE );
        }
    }

    /**
     * Constructor
     *
     * @param colors     colors used for the gradient, in left-to-right order
     * @param leftLabel
     * @param rightLabel
     */
    public SurfaceColorKeyNode( Color[] colors, String title, String leftLabel, String rightLabel ) {

        final double segmentWidth = SIZE.width / (double) ( colors.length - 1 );

        // spectrum, composed of multiple PPath's because Java 1.5 doesn't include LinearGradientPaint
        final Shape spectrumShape = new Rectangle2D.Double( 0, 0, SIZE.width, SIZE.height );
        PPath spectrumNode = new PPath( spectrumShape ) {{
            setStroke( null );
        }};
        for ( int i = 0; i < colors.length - 1; i++ ) {
            Color leftColor = colors[i];
            Color rightColor = colors[i + 1];
            double x = i * segmentWidth;
            final Paint gradient = new GradientPaint( (float) x, 0f, leftColor, (float) ( x + segmentWidth ), 0f, rightColor );
            spectrumNode.addChild( new PPath( new Rectangle2D.Double( x, 0, segmentWidth, SIZE.height ) ) {{
                setPaint( gradient );
                setStroke( null );
            }} );
        }

        // put an outline on top, because outlining spectrumNode looks incorrect
        spectrumNode.addChild( new PPath( spectrumShape ) );

        // labels
        PText titleNode = new PText( title ) {{
            setFont( TITLE_FONT );
        }};
        PText leftLabelNode = new PText( leftLabel ) {{
            setFont( RANGE_FONT );
        }};
        PText rightLabelNode = new PText( rightLabel ) {{
            setFont( RANGE_FONT );
        }};

        // rendering order
        addChild( spectrumNode );
        addChild( titleNode );
        addChild( leftLabelNode );
        addChild( rightLabelNode );

        // layout
        spectrumNode.setOffset( 0, titleNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        titleNode.setOffset( spectrumNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ), 0 );
        leftLabelNode.setOffset( spectrumNode.getFullBoundsReference().getMinX() + X_INSET,
                                 spectrumNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        rightLabelNode.setOffset( spectrumNode.getFullBoundsReference().getMaxX() - rightLabelNode.getFullBoundsReference().getWidth() - X_INSET,
                                  spectrumNode.getFullBoundsReference().getMaxY() + Y_SPACING );
    }
}

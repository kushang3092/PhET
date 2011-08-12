// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.model.Bond;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Base class for visual representation of dipoles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DipoleNode extends PPath {

    // Note: heights are parallel to dipole axis, widths are perpendicular.
    private static final double REFERENCE_MAGNITUDE = MPConstants.ELECTRONEGATIVITY_RANGE.getLength(); // model value
    private static final double REFERENCE_LENGTH = 150; // view size
    private static final Dimension HEAD_SIZE = new Dimension( 12, 25 ); // similar to Jmol
    private static final Dimension CROSS_SIZE = new Dimension( 10, 10 ); // similar to Jmol
    private static final double REFERENCE_CROSS_OFFSET = 10; // offset from the tail of the arrow
    private static final double TAIL_WIDTH = 4; // similar to Jmol
    private static final double FRACTIONAL_HEAD_HEIGHT = 0.5; // when the head size is less than fractionalHeadHeight * arrow length, the head will be scaled.

    private double x;

    public DipoleNode( Color color ) {
        super();
        setPaint( color );
        update();
    }

    protected void setComponentX( double x ) {
        if ( x != this.x ) {
            this.x = x;
            update();
        }
    }

    // Updates the arrow to match the node's state.
    private void update() {
        final double y = 0;
        final double magnitude = PolarCartesianConverter.getRadius( x, y );
        if ( magnitude == 0 ) {
            setPathTo( new Rectangle2D.Double() ); // because Arrow doesn't handle zero-length arrows
        }
        else {
            // arrow
            Arrow arrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( x * ( REFERENCE_LENGTH / REFERENCE_MAGNITUDE ), y ),
                                     HEAD_SIZE.height, HEAD_SIZE.width, TAIL_WIDTH, FRACTIONAL_HEAD_HEIGHT, true /* scaleTailToo */ );
            // cross
            Shape cross = createCross( arrow );

            // Combine arrow and cross using constructive area geometry.
            Area area = new Area( arrow.getShape() );
            area.add( new Area( cross ) );
            setPathTo( area );
        }
    }

    /*
     * Creates the cross that signifies the positive end of the dipole.
     * We're attempting to make this look like Jmol's representation, which looks more like a 3D cylinder.
     * <p>
     * TODO: Complicating this implementation is the fact that the arrow begins to scale when it reaches
     * some minimum length (as defined by FRACTIONAL_HEAD_HEIGHT).  The Arrow class doesn't tell us
     * when it is being scaled, or what the scaling factor is. So in order to decorate the Arrow shape,
     * we're relying on internal information about the Arrow implementation. So if Arrow's behavior changes,
     * this implementation may break.
     */
    private Shape createCross( Arrow arrow ) {

        double arrowLength = Math.abs( arrow.getTipLocation().getX() - arrow.getTailLocation().getX() );

        // offset and height are always scaled
        double crossOffset = REFERENCE_CROSS_OFFSET * arrowLength / REFERENCE_LENGTH;
        double crossHeight = CROSS_SIZE.height * arrowLength / REFERENCE_LENGTH;

        // width is scaled if arrow head is scaled
        double crossWidth = CROSS_SIZE.width;
        if ( arrowLength < HEAD_SIZE.height / FRACTIONAL_HEAD_HEIGHT ) { //TODO this relies on implementation of Arrow.computeArrow
            // the arrow is being scaled, so scale the cross
            double scaledHeadHeight = arrowLength * FRACTIONAL_HEAD_HEIGHT; //TODO this relies on implementation of Arrow.computeArrow
            double crossScale = scaledHeadHeight / HEAD_SIZE.height;
            crossOffset *= crossScale;
            crossWidth *= crossScale;
            crossHeight *= crossScale;
        }

        // arrow points left, flip sign of offset and shift it to the left
        if ( arrow.getTipLocation().getX() < arrow.getTailLocation().getX() ) {
            crossOffset = ( -1 * crossOffset ) - crossHeight;
        }

        return new Rectangle2D.Double( crossOffset, -crossWidth / 2, crossHeight, crossWidth );
    }

    // Visual representation of a bond dipole.
    public static class BondDipoleNode extends DipoleNode {

        private static final double PERPENDICULAR_OFFSET = 75; // offset perpendicular to the axis of the endpoints

        public BondDipoleNode( final Bond bond ) {
            super( Color.BLACK );

            // align the dipole to be parallel with the bond, with some perpendicular offset
            SimpleObserver update = new SimpleObserver() {
                public void update() {

                    setComponentX( bond.deltaElectronegativity.get() );

                    // compute location of dipole, with offset
                    final double angle = bond.getAngle() - Math.PI / 2; // above the bond
                    double dipoleX = PolarCartesianConverter.getX( PERPENDICULAR_OFFSET, angle );
                    double dipoleY = PolarCartesianConverter.getY( PERPENDICULAR_OFFSET, angle );

                    // clear the transform
                    setOffset( 0, 0 );
                    setRotation( 0 );

                    // compute length before transforming
                    final double length = getFullBoundsReference().getWidth();

                    // offset from bond
                    translate( bond.getCenter().getX() + dipoleX, bond.getCenter().getY() + dipoleY );

                    // parallel to bond
                    rotate( bond.getAngle() );

                    // center vector on bond
                    if ( bond.deltaElectronegativity.get() > 0 ) {
                        translate( -length / 2, 0 );
                    }
                    else {
                        translate( +length / 2, 0 );
                    }
                }
            };
            bond.endpoint1.addObserver( update );
            bond.endpoint2.addObserver( update );
            bond.deltaElectronegativity.addObserver( update );
        }
    }
}

package edu.colorado.phet.buildanatom.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Class that defines the shape and functionality of a "bucket", which is
 * (in this sim anyway) a container into which sub-atomic particles can be
 * placed.  It is defined such that it will have somewhat of a 3D look to
 * it, so it has two shapes, one that is the hole, and one that is the
 * outside of the bucket.
 *
 * IMPORTANT NOTE: The shapes that are created and that comprise the
 * bucket are set up such that the point (0,0) is in the center of the
 * bucket's hole.
 *
 * @author John Blanco
 */
public class SubatomicParticleBucket {

    // Proportion of the total height which the ellipse that represents
    // the hole occupies.  It is assumed that the width of the hole
    // is the same as the width specified at construction.
    private static final double HOLE_ELLIPSE_HEIGHT_PROPORTION = 0.3;

    // The position is defined to be where the center of the hole is.
    private final Point2D position = new Point2D.Double();

    // The two shapes that define the overall shape of the bucket.
    private final Shape holeShape;
    private final Shape containerShape;

    // Base color of the bucket.
    private final Color baseColor;

    // Caption to be shown on the bucket.
    private final String captionText;

    // Particles that are in this bucket.
    private final ArrayList<SubatomicParticle> containedParticles = new ArrayList<SubatomicParticle>();

    // Radius of particles that will be going into this bucket.  This is
    // used for placing particles.
    private final double particleRadius;

    public SubatomicParticleBucket( Point2D position, Dimension2D size, Color baseColor, String caption, double particleRadius ) {
        this.position.setLocation( position );
        this.baseColor = baseColor;
        this.captionText = caption;
        this.particleRadius = particleRadius;

        // Create the shape of the bucket's hole.
        holeShape = new Ellipse2D.Double( -size.getWidth() / 2,
                -size.getHeight() * HOLE_ELLIPSE_HEIGHT_PROPORTION / 2,
                size.getWidth(),
                size.getHeight() * HOLE_ELLIPSE_HEIGHT_PROPORTION );

        // Create the shape of the container.  This code is a bit "tweaky",
        // meaning that there are a lot of fractional multipliers in here
        // to try to achieve the desired pseudo-3D look.  The intent is
        // that the "tilt" of the bucket can be changed without needing to
        // rework this code.  It may or may not work out that way, so
        // adjust as necessary to get the look you need.
        double containerHeight = size.getHeight() * ( 1 - ( HOLE_ELLIPSE_HEIGHT_PROPORTION / 2 ) );
        DoubleGeneralPath containerPath = new DoubleGeneralPath();
        containerPath.moveTo( -size.getWidth() * 0.5, 0 );
        containerPath.lineTo( -size.getWidth() * 0.4, -containerHeight * 0.8 );
        containerPath.curveTo(
                -size.getWidth() * 0.3,
                -containerHeight * 0.8 - size.getHeight() * HOLE_ELLIPSE_HEIGHT_PROPORTION * 0.6,
                size.getWidth() * 0.3,
                -containerHeight * 0.8 - size.getHeight() * HOLE_ELLIPSE_HEIGHT_PROPORTION * 0.6,
                size.getWidth() * 0.4,
                -containerHeight * 0.8 );
        containerPath.lineTo( size.getWidth() * 0.5, 0 );
        containerPath.closePath();
        Area containerArea = new Area( containerPath.getGeneralPath() );
        containerArea.subtract( new Area( holeShape ) );
        containerShape = containerArea;
    }

    public void reset() {
        containedParticles.clear();
    }

    public Point2D getPosition() {
        return position;
    }

    public Shape getHoleShape() {
        return holeShape;
    }

    public Shape getContainerShape() {
        return containerShape;
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public String getCaptionText() {
        return captionText;
    }

    public void addParticle( final SubatomicParticle particle, boolean moveImmediately ) {
        // Determine an open location in the bucket.
        Point2D freeParticleLocation = getFirstOpenLocation();

        // Move the particle.
        if ( moveImmediately ) {
            // Move the particle instantaneously to the destination.
            particle.setPositionAndDestination( freeParticleLocation );
        }
        else {
            // Set the destination and let the particle find its own way.
            particle.setDestination( freeParticleLocation );
        }

        // Listen for when the user removes this particle from the bucket.
        particle.addUserControlListener( new SimpleObserver() {
            public void update() {
                if ( particle.isUserControlled() ) {
                    // The user has picked up this particle, so we assume
                    // that it is essentially removed from the bucket.
                    removeParticle( particle );
                    particle.removeUserControlListener( this );
                }
            }
        } );

        containedParticles.add( particle );
    }

    public void removeParticle( SubatomicParticle particle ) {
        assert containedParticles.contains( particle );
        containedParticles.remove( particle );
    }

    private Point2D getFirstOpenLocation() {
        Point2D openLocation = new Point2D.Double();
        int numParticlesInLayer = (int) Math.floor( holeShape.getBounds2D().getWidth() / ( particleRadius * 2 ) ) - 1;
        int layer = 0;
        int positionInLayer = 0;
        double offset = particleRadius * 2; // Initial offset is NOT zero, since we don't want to go right up to the edge.
        boolean found = false;
        while ( !found ) {
            double yPos = getPosition().getY() + layer * particleRadius * 2 * 0.866;
            double xPos = getPosition().getX() - holeShape.getBounds2D().getWidth() / 2 + offset + positionInLayer * 2 * particleRadius;
            if ( isPositionOpen( xPos, yPos ) ) {
                // We found a location that is open.
                openLocation.setLocation( xPos, yPos );
                found = true;
                continue;
            }
            else {
                positionInLayer++;
                if ( positionInLayer >= numParticlesInLayer ) {
                    // Move to the next layer.
                    layer++;
                    positionInLayer = 0;
                    numParticlesInLayer--;
                    offset += particleRadius;
                    if ( numParticlesInLayer == 0 ) {
                        // This algorithm doesn't handle the situation
                        // where more particles are added than can be
                        // stacked into a pyramid of the needed size, but
                        // so far it hasn't needed to.  If this
                        // requirement changes, the algorithm will need to
                        // change too.
                        //                            assert false;
                        numParticlesInLayer = 1;
                        offset -= particleRadius;
                    }
                }
            }
        }
        return openLocation;
    }

    /**
     * Determine whether the given particle position is open (i.e.
     * unoccupied) in the bucket.
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isPositionOpen( double x, double y ) {
        boolean positionOpen = true;
        for ( SubatomicParticle particle : containedParticles ) {
            Point2D position = particle.getPosition();
            if ( position.getX() == x && position.getY() == y ) {
                positionOpen = false;
                break;
            }
        }
        return positionOpen;
    }
}
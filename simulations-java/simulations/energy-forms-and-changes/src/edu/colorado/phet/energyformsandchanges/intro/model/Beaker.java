// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Model element that represents a beaker in the simulation.
 *
 * @author John Blanco
 */
public class Beaker extends UserMovableModelElement {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double WIDTH = 0.085; // In meters.
    private static final double HEIGHT = WIDTH;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Property that is used to control the amount of fluid in the beaker.
    public final Property<Double> fluidLevel = new Property<Double>( 0.3 );

    // Surface upon which any model elements will sit.
    private final Property<HorizontalSurface> topSurface;

    // Bottom of this model element, used when setting it on something.
    private Property<HorizontalSurface> bottomSurface;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param initialPosition The initial position in model space.  This is
     *                        the center bottom of the model element.
     */
    public Beaker( ImmutableVector2D initialPosition ) {

        this.position.set( initialPosition );

        // Create the top surface.  Since the beaker has objects rest at the
        // bottom inside the beaker, the top surface is the bottom of the
        // defining rectangle.
        topSurface = new Property<HorizontalSurface>( new HorizontalSurface( new DoubleRange( getOutlineRect().getMinX(), getOutlineRect().getMaxX() ), getOutlineRect().getMinY(), this ) );

        // For the beaker, the top and bottom surfaces are the same.
        bottomSurface = topSurface;

        // Update the top and bottom surfaces whenever the position changes.
        position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( final ImmutableVector2D immutableVector2D ) {
                updateSurfaces();
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Get a rectangle that defines the outline of the burner.  In the model,
     * the burner is essentially a 2D rectangle.
     *
     * @return
     */
    public Rectangle2D getOutlineRect() {
        return new Rectangle2D.Double( position.get().getX() - WIDTH / 2,
                                       position.get().getY(),
                                       WIDTH,
                                       HEIGHT );
    }

    private void updateSurfaces() {
        topSurface.set( new HorizontalSurface( new DoubleRange( getOutlineRect().getMinX(), getOutlineRect().getMaxX() ),
                                               getOutlineRect().getMaxY(),
                                               this ) );
    }

    @Override public Property<HorizontalSurface> getTopSurfaceProperty() {
        return topSurface;
    }

    @Override public Property<HorizontalSurface> getBottomSurfaceProperty() {
        return bottomSurface;
    }

    @Override public IUserComponent getUserComponent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public IUserComponentType getUserComponentType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.geom.Point2D;

/**
 * @author John Blanco
 */
public abstract class MotionStrategy {

    protected MotionBounds motionBounds = new MotionBounds();

    public abstract Point2D getNextLocation( double dt, Point2D currentLocation );

    public void setMotionBounds( MotionBounds motionBounds ) {
        this.motionBounds = motionBounds;
    }
}

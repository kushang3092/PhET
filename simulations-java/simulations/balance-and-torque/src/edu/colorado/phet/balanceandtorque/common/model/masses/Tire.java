// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a tire.
 *
 * @author John Blanco
 */
public class Tire extends ImageMass {

    private static final double MASS = 15; // in kg
    private static final double HEIGHT = 0.15; // In meters.

    public Tire( boolean isMystery ) {
        super( MASS, Images.TIRE, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}

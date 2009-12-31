/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;


/**
 * Class that represents lac promoter, which is the binding region on the DNA
 * strand for the RNA polymerase.
 * 
 * @author John Blanco
 */
public class LacPromoter extends Promoter {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Paint ELEMENT_PAINT = new Color(0, 153, 255);
	private static final double ATTACHMENT_RECOVERY_TIME = 8;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

	public LacPromoter(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, initialPosition, ELEMENT_PAINT, false, Double.POSITIVE_INFINITY);
		setAttachmentRecoveryTime(ATTACHMENT_RECOVERY_TIME);
	}
	
	public LacPromoter(IGeneNetworkModelControl model) {
		this(model, new Point2D.Double());
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	@Override
	protected boolean isInAllowableLocation() {
		// Find out if we are within range of our location on the DNA strand.
		return getPositionRef().distance(getModel().getDnaStrand().getLacPromoterLocation()) < LOCK_TO_DNA_DISTANCE;
	}

	@Override
	protected Point2D getDefaultLocation() {
		return getModel().getDnaStrand().getLacPromoterLocation();
	}
}

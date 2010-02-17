/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.model;

/**
 * Base class for fade strategies that can be used to fade model elements in
 * and out.
 * 
 * @author John Blanco
 */
public abstract class FadeStrategy {

	/**
	 * Fade the associated model element according to the specified amount of
	 * time and the nature of the strategy.
	 * 
	 * @param dt
	 */
	public abstract void updateOpaqueness(IFadable moveableModelElement, double dt);
	
	/**
	 * Get an indication of whether or not the model element that is
	 * associated with this strategy should continue to exist.  This is
	 * generally used to figure out when to remove a model element that has
	 * faded away.
	 */
	public boolean shouldContinueExisting(){
		return true;
	}
}

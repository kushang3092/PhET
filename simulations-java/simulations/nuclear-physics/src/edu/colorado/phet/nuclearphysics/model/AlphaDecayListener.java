package edu.colorado.phet.nuclearphysics.model;

/**
 * This listener interface allows listeners to get notifications for various
 * events that could come from a model the is simulating alpha decay.
 */
public interface AlphaDecayListener {
	
    /**
     * This informs the listener that some new element has been added
     * to the model.
     * 
     * @param modelElement - Element that was added to the model.
     */
    public void modelElementAdded(Object modelElement);
    
    /**
     * This informs the listener that a model element was removed.
     * 
     * @param modelElement - Element that was removed from the model.
     */
    public void modelElementRemoved(Object modelElement);

    /**
     * This informs the listener that the nucleus type has changed.
     * 
     * @param modelElement - Element that was removed from the model.
     */
    public void nucleusTypeChanged();
}

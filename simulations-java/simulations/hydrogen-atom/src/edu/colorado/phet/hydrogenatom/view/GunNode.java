/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.HAResources;
import edu.colorado.phet.hydrogenatom.model.Gun;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * GunNode is the visual representation of the gun.
 * It includes on/off controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GunNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Point2D BUTTON_OFFSET = new Point2D.Double( 36, 75 );
    private static final Point2D CABLE_OFFSET = new Point2D.Double( 46, 140 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Gun _gun; // model element
    private PImage _onButton, _offButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param gun the gun model
     */
    public GunNode( Gun gun ) {
        super();
        
        _gun = gun;
        
        // Nodes
        PImage gunNode = HAResources.getImageNode( HAConstants.IMAGE_GUN );
        _onButton = HAResources.getImageNode(HAConstants.IMAGE_GUN_ON_BUTTON );
        _offButton = HAResources.getImageNode( HAConstants.IMAGE_GUN_OFF_BUTTON );
        PImage cableNode = HAResources.getImageNode( HAConstants.IMAGE_GUN_CONTROL_CABLE );
        
        // Layering
        addChild( cableNode );
        addChild( gunNode );
        addChild( _onButton );
        addChild( _offButton );
        
        // Positioning
        gunNode.setOffset( 0, 0 );
        _onButton.setOffset( BUTTON_OFFSET );
        _offButton.setOffset( BUTTON_OFFSET );
        cableNode.setOffset( CABLE_OFFSET );
        
        // Event handling
        {
            gunNode.setPickable( false );
            cableNode.setPickable( false );
            
            PBasicInputEventHandler buttonHandler = new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    _gun.setEnabled( !_gun.isEnabled() );
                }
            };
            _onButton.addInputEventListener( buttonHandler );
            _offButton.addInputEventListener( buttonHandler );
            _onButton.addInputEventListener( new CursorHandler() );
            _offButton.addInputEventListener( new CursorHandler() );
        }

        // Sync with model
        updateAll();
        _gun.addObserver( this );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets a handle to the button, for the purposes of attaching a Wiggle Me.
     * @return PNode
     */
    public PNode getButtonNode() {
        return _offButton;
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the display with the gun model.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _gun && arg == Gun.PROPERTY_ENABLED ) {
            updateAll();
        }
    }
    
    /*
     * Updates the state of the on/off buttons to match the model.
     */
    public void updateAll() {
        boolean enabled = _gun.isEnabled();
        _onButton.setVisible( enabled );
        _onButton.setPickable( _onButton.getVisible() );
        _offButton.setVisible( !enabled );
        _offButton.setPickable( _offButton.getVisible() );
    }
}

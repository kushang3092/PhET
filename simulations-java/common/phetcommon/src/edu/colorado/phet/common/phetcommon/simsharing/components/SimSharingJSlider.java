// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;

/**
 * TODO: Not done yet, needs to be implemented.
 *
 * @author Sam Reid
 */
public class SimSharingJSlider extends JSlider {
    private final IUserComponent userComponent;

    public SimSharingJSlider( IUserComponent userComponent ) {
        this.userComponent = userComponent;
    }

    public SimSharingJSlider( IUserComponent userComponent, int orientation ) {
        super( orientation );
        this.userComponent = userComponent;
    }

    public SimSharingJSlider( IUserComponent userComponent, int min, int max ) {
        super( min, max );
        this.userComponent = userComponent;
    }

    public SimSharingJSlider( IUserComponent userComponent, int min, int max, int value ) {
        super( min, max, value );
        this.userComponent = userComponent;
    }

    public SimSharingJSlider( IUserComponent userComponent, int orientation, int min, int max, int value ) {
        super( orientation, min, max, value );
        this.userComponent = userComponent;
    }

    public SimSharingJSlider( IUserComponent userComponent, BoundedRangeModel brm ) {
        super( brm );
        this.userComponent = userComponent;
    }

    //TODO: add messages for startDrag, endDrag actions (via a MouseListener?)

    @Override protected void fireStateChanged() {
        SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.slider, UserActions.drag, ParameterSet.parameterSet( ParameterKeys.value, getValue() ) );
        super.fireStateChanged();
    }
}
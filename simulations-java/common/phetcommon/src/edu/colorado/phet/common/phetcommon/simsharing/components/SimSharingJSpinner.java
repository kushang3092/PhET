// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants.ComponentTypes.spinner;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants.ParameterKeys.value;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants.User.UserActions.changed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants.User.UserComponent;

/**
 * Swing spinner that sends sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJSpinner extends JSpinner {

    private final UserComponent object;

    public SimSharingJSpinner( UserComponent object, SpinnerModel model ) {
        super( model );
        this.object = object;
    }

    public SimSharingJSpinner( UserComponent object ) {
        this.object = object;
    }

    @Override protected void fireStateChanged() {
        SimSharingManager.sendUserEvent( object, changed,
                                         Parameter.componentType( spinner ),
                                         Parameter.param( value, getValue().toString() ) );
        super.fireStateChanged();
    }
}

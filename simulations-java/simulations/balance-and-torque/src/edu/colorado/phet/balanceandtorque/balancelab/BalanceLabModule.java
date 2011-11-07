// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.balancelab;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.balancelab.model.BalanceLabModel;
import edu.colorado.phet.balanceandtorque.balancelab.view.BalanceLabCanvas;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * The "Balance Lab" module.
 *
 * @author John Blanco
 */
public class BalanceLabModule extends Module {

    private BalanceLabModel model;

    //REVIEW move setClockControlPanel and setLogoPanel to private constructor
    public BalanceLabModule() {
        this( new BalanceLabModel() );
        setClockControlPanel( null );
        getModulePanel().setLogoPanel( null );
    }

    private BalanceLabModule( BalanceLabModel model ) {
        super( BalanceAndTorqueResources.Strings.BALANCE_LAB, model.getClock() );
        this.model = model;
        setSimulationPanel( new BalanceLabCanvas( model ) );
        reset();
    }

    @Override public void reset() {
        model.reset();
    }
}

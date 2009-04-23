/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.defaults.SingleNucleusAlphaDecayDefaults;
import edu.colorado.phet.nuclearphysics.module.alphadecay.AlphaDecayControlPanel;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that demonstrates alpha decay of a single atomic
 * nucleus. 
 *
 * @author John Blanco
 */
public class RadioactiveDatingGameModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private RadioactiveDatingGameModel _model;
    private RadioactiveDatingGameCanvas _canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public RadioactiveDatingGameModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_RADIOACTIVE_DATING_GAME,
               new NuclearPhysicsClock( SingleNucleusAlphaDecayDefaults.CLOCK_FRAME_RATE, SingleNucleusAlphaDecayDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysicsClock clock = (NuclearPhysicsClock) getClock();
        _model = new RadioactiveDatingGameModel(clock);

        // Canvas
        _canvas = new RadioactiveDatingGameCanvas( _model );
        setSimulationPanel( _canvas );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Reset the clock, which ultimately resets the model too.
        _model.getClock().resetSimulationTime();
        setClockRunningWhenActive( SingleNucleusAlphaDecayDefaults.CLOCK_RUNNING );
    }
}

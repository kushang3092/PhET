/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.comparing;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.acidbasesolutions.defaults.ComparingDefaults;
import edu.colorado.phet.acidbasesolutions.defaults.SolutionsDefaults;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.persistence.ComparingConfig;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * ComparingModule is the "Comparing Solutions" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ComparingModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final ABSClock CLOCK = new ABSClock( SolutionsDefaults.CLOCK_FRAME_RATE, SolutionsDefaults.CLOCK_DT );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ComparingModel _model;
    private ComparingCanvas _canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ComparingModule( Frame parentFrame ) {
        super( ABSStrings.TITLE_COMPARING_MODULE, CLOCK );

        // Model
        _model = new ComparingModel( CLOCK );

        // Canvas
        _canvas = new ComparingCanvas( _model );
        setSimulationPanel( _canvas );

        // No control Panel
        setControlPanel( null );
        
        //  No clock controls
        setClockControlPanel( null );

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

        // Clock
        ABSClock clock = _model.getClock();
        clock.resetSimulationTime();
        clock.setDt( ComparingDefaults.CLOCK_DT );
        setClockRunningWhenActive( ComparingDefaults.CLOCK_RUNNING );

        //XXX other stuff
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public ComparingConfig save() {

        ComparingConfig config = new ComparingConfig();

        // Module
        config.setActive( isActive() );

        // Clock
        ABSClock clock = _model.getClock();
        config.setClockDt( clock.getDt() );
        config.setClockRunning( getClockRunningWhenActive() );

        //XXX other stuff
        
        return config;
    }

    public void load( ComparingConfig config ) {

        // Module
        if ( config.isActive() ) {
            AcidBaseSolutionsApplication.getInstance().setActiveModule( this );
        }

        // Clock
        ABSClock clock = _model.getClock();
        clock.setDt( config.getClockDt() );
        setClockRunningWhenActive( config.isClockRunning() );

        //XXX other stuff
    }
}

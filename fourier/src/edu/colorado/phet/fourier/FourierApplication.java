/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier;

import java.io.IOException;

import javax.swing.JMenu;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.module.ContinuousModule;
import edu.colorado.phet.fourier.module.DiscreteModule;
import edu.colorado.phet.fourier.module.DiscreteToContinousModule;
import edu.colorado.phet.fourier.module.WavePulseShaperModule;


/**
 * FourierApplication is the main application for the PhET "Fourier Analysis" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierApplication extends PhetApplication {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean ENABLE_DEVELOPER_MENU = true;
    
    private static final boolean TEST_ONE_MODULE = false;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param args command line arguments
     * @param title
     * @param description
     * @param version
     * @param clock
     * @param useClockControlPanel
     * @param frameSetup
     */
    public FourierApplication( String[] args, 
            String title, String description, String version, AbstractClock clock,
            boolean useClockControlPanel, FrameSetup frameSetup )
    {
        super( args, title, description, version, clock, useClockControlPanel, frameSetup );
        initMenubar();
    }
    
    //----------------------------------------------------------------------------
    // Menubar
    //----------------------------------------------------------------------------
    
    /**
     * Initializes the menubar.
     */
    private void initMenubar() {
           
        // Developer menu
        if ( ENABLE_DEVELOPER_MENU ) {

            JMenu developerMenu = new JMenu( "Developer" );
            developerMenu.setMnemonic( 'v' );
            getPhetFrame().addMenu( developerMenu );

            //XXX Add menu items for Developer menu
        }
    }
    
    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point for the PhET Color Vision application.
     * 
     * @param args command line arguments
     */
    public static void main( String[] args ) throws IOException {

        // Initialize localization.
        SimStrings.init( args, FourierConfig.LOCALIZATION_BUNDLE_BASENAME );
        
        // Initialize Look-&-Feel
        PhetLookAndFeel.setLookAndFeel();
        PhetLookAndFeel laf = new PhetLookAndFeel();
        laf.apply();
        
        // Get stuff needed to initialize the application model.
        String title = SimStrings.get( "FourierApplication.title" );
        String description = SimStrings.get( "FourierApplication.description" );
        String version = FourierConfig.APP_VERSION;
        int width = FourierConfig.APP_FRAME_WIDTH;
        int height = FourierConfig.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

        // Clock
        double timeStep = FourierConfig.CLOCK_TIME_STEP;
        int waitTime = ( 1000 / FourierConfig.CLOCK_FRAME_RATE ); // milliseconds
        boolean isFixed = FourierConfig.CLOCK_TIME_STEP_IS_CONSTANT;
        AbstractClock clock = new SwingTimerClock( timeStep, waitTime, isFixed );
        boolean useClockControlPanel = true;
        
        // Create the application.
        FourierApplication app = new FourierApplication( args,
                 title, description, version, clock, useClockControlPanel, frameSetup );
        
        // Simulation Modules
        if ( TEST_ONE_MODULE ) {
            Module module = new DiscreteModule( clock );
            app.setModules( new Module[] { module } );
            app.setInitialModule( module );
        }
        else {
            DiscreteModule discreteModule = new DiscreteModule( clock );
            DiscreteToContinousModule discreteToContinuousModule = new DiscreteToContinousModule( clock );
            ContinuousModule continuousModule = new ContinuousModule( clock );
            WavePulseShaperModule wavePulseShapeModule = new WavePulseShaperModule( clock );
            app.setModules( new Module[] { discreteModule, discreteToContinuousModule, continuousModule, wavePulseShapeModule } );
            app.setInitialModule( discreteModule );
        }
        
        // Start the application.
        app.startApplication();
    }
}

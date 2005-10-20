/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenuItem;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.util.DebugMenu;
import edu.colorado.phet.common.view.components.menu.HelpMenu;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.model.FourierSeries;
import edu.colorado.phet.shaper.module.ShaperModule;
import edu.colorado.phet.shaper.view.CheatDialog;


/**
 * ShaperApplication is the main application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ShaperApplication extends PhetApplication {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ShaperModule _shaperModule;
    
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
    public ShaperApplication( String[] args, 
            String title, String description, String version, AbstractClock clock,
            boolean useClockControlPanel, FrameSetup frameSetup )
    {
        super( args, title, description, version, clock, useClockControlPanel, frameSetup );
        initModules( clock );  
        initMenubar();
    }
    
    //----------------------------------------------------------------------------
    // Modules
    //----------------------------------------------------------------------------
    
    /*
     * Initializes the modules.
     * 
     * @param clock
     */
    private void initModules( AbstractClock clock ) {
        _shaperModule = new ShaperModule( clock );
        setModules( new Module[] { _shaperModule } );
        setInitialModule( _shaperModule );
    }
    
    //----------------------------------------------------------------------------
    // Menubar
    //----------------------------------------------------------------------------
    
    /*
     * Initializes the menubar.
     */
    private void initMenubar() {
     
        // Debug menu extensions
        DebugMenu debugMenu = getPhetFrame().getDebugMenu();
        if ( debugMenu != null ) {
            //XXX Add debug menu items here.
        }
        
        // Help menu extensions
        HelpMenu helpMenu = getPhetFrame().getHelpMenu();
        JMenuItem cheatItem = new JMenuItem( SimStrings.get( "HelpMenu.cheat" ) );
        cheatItem.setMnemonic( SimStrings.get( "HelpMenu.cheat.mnemonic" ).charAt(0) );
        cheatItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                FourierSeries outputFourierSeries = _shaperModule.getOutputFourierSeries();
                CheatDialog dialog = new CheatDialog( getPhetFrame(), outputFourierSeries );
                dialog.show();
            }
        } );
        helpMenu.add( cheatItem );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point for the PhET "Optical Pulse Shaper" application.
     * 
     * @param args command line arguments
     */
    public static void main( String[] args ) throws IOException {

        // Initialize localization.
        SimStrings.init( args, ShaperConstants.LOCALIZATION_BUNDLE_BASENAME );
        
        // Title, etc.
        String title = SimStrings.get( "ShaperApplication.title" );
        String description = SimStrings.get( "ShaperApplication.description" );
        String version = Version.NUMBER;
        
        // Clock
        double timeStep = ShaperConstants.CLOCK_TIME_STEP;
        int waitTime = ( 1000 / ShaperConstants.CLOCK_FRAME_RATE ); // milliseconds
        boolean isFixed = ShaperConstants.CLOCK_TIME_STEP_IS_CONSTANT;
        AbstractClock clock = new SwingTimerClock( timeStep, waitTime, isFixed );
        boolean useClockControlPanel = false;
        
        // Frame setup
        int width = ShaperConstants.APP_FRAME_WIDTH;
        int height = ShaperConstants.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );
        
        // Create the application.
        ShaperApplication app = new ShaperApplication( args,
                 title, description, version, clock, useClockControlPanel, frameSetup );
        
        // Start the application.
        app.startApplication();
    }
}

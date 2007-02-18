package edu.colorado.phet.lookandfeels;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.piccolo.PiccoloModule;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 3:55:03 PM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */

public class TestPhetApplicationLookAndFeel {
    public static void main( String[] args ) {
        PhetApplication phetApplication = new PhetApplication( args, "title", "", "", new FrameSetup.CenteredWithSize( 800, 600 ) );
        MyModule myModule = new MyModule( "module", new SwingClock( 30, 1 ) );
        myModule.setSimulationPanel( new JPanel() );

        phetApplication.addModule( myModule );
        phetApplication.getPhetFrame().addMenu( new LookAndFeelMenu() );
        phetApplication.startApplication();
    }

    static class MyModule extends PiccoloModule {

        public MyModule( String name, IClock clock ) {
            super( name, clock );
        }

        public void setSimulationPanel( JComponent panel ) {
            super.setSimulationPanel( panel );
        }
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.dilutions.molarity.MolarityModule;

/**
 * Main class for the "Molarity" application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityApplication extends PiccoloPhetApplication {

    public MolarityApplication( PhetApplicationConfig config ) {
        super( config );
        Frame frame = getPhetFrame();
        addModule( new MolarityModule( frame ) );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, MolarityResources.PROJECT_NAME, MolarityResources.MOLARITY_FLAVOR, MolarityApplication.class );
    }
}

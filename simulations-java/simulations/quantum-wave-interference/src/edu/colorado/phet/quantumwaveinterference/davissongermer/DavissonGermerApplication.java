/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.quantumwaveinterference.QWIFrameSetup;
import edu.colorado.phet.quantumwaveinterference.QWIPhetLookAndFeel;
import edu.colorado.phet.quantumwaveinterference.QuantumWaveInterferenceApplication;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 */

public class DavissonGermerApplication extends PhetApplication {
    static {
        QWIStrings.init( new String[]{} );
    }

    public static String TITLE = QWIStrings.getString( "davisson.germer.electron.diffraction" );
    public static String DESCRIPTION = MessageFormat.format( QWIStrings.getString( "davisson-germer.description" ), new Object[0] );

    public DavissonGermerApplication( String[] args ) {
        super( args, TITLE, DESCRIPTION, QuantumWaveInterferenceApplication.getQWIVersion(), new QWIFrameSetup() );
        addModule( new DGModule( this, createClock() ) );
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1 );
    }

    protected PhetFrame createPhetFrame( NonPiccoloPhetApplication phetApplication ) {
        return new PhetFrameWorkaround( phetApplication );
    }

    public static void main( String[] args ) {
        new QWIPhetLookAndFeel().initLookAndFeel();
        DavissonGermerApplication schrodingerApplication = new DavissonGermerApplication( args );
        schrodingerApplication.startApplication();
    }

}

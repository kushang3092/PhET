package edu.colorado.phet.cck;

import edu.colorado.phet.cck.controls.LookAndFeelMenu;
import edu.colorado.phet.cck.controls.OptionsMenu;
import edu.colorado.phet.cck.piccolo_cck.CCKPiccoloModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 9:17:52 AM
 */

public class CCKApplication extends PiccoloPhetApplication {
    //version is generated automatically (with ant)
    public static final String localizedStringsPath = "cck/localization/cck-strings";
    private CCKPiccoloModule cckPiccoloModule;
    public static final String AC_OPTION = "-dynamics";

    public CCKApplication( String[] args ) throws IOException {
        super( args, CCKResources.getString( isDynamic( args ) ? "cck-ac.name" : "cck-dc.name" ),
               CCKResources.getString( isDynamic( args ) ? "cck-ac.description" : "cck-dc.description" ),
               readVersion(), createFrameSetup() );

        boolean debugMode = false;
        if( Arrays.asList( args ).contains( "debug" ) ) {
            debugMode = true;
            System.out.println( "debugMode = " + debugMode );
        }

        cckPiccoloModule = new CCKPiccoloModule( args );
        cckPiccoloModule.getCckSimulationPanel().addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
            }

            public void keyReleased( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_F1 ) {
                    getPhetFrame().setSize( 1024, 768 );
                    getPhetFrame().invalidate();
                    getPhetFrame().validate();
                }
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        Module[] modules = new Module[]{cckPiccoloModule};
        setModules( modules );
        if( getPhetFrame().getTabbedModulePane() != null ) {
            getPhetFrame().getTabbedModulePane().setLogoVisible( false );
        }
        getPhetFrame().addMenu( new LookAndFeelMenu() );
        getPhetFrame().addMenu( new OptionsMenu( this, cckPiccoloModule ) );//todo options menu
    }

    private static FrameSetup createFrameSetup() {
        if( Toolkit.getDefaultToolkit().getScreenSize().height <= 768 ) {
            return new FrameSetup.MaxExtent( new FrameSetup.TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, 700 ) );
        }
        else {
            return new FrameSetup.TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 100 );
        }
    }

    protected PhetFrame createPhetFrame() {
        return new PhetFrameWorkaround( this );
    }

    private static boolean isDynamic( String[] args ) {
        return Arrays.asList( args ).contains( AC_OPTION );
    }

    public void startApplication() {
        super.startApplication();
        cckPiccoloModule.applicationStarted();
    }

    protected PhetAboutDialog createPhetAboutDialog() {
        return new CCKAboutDialog( this );
    }

    private static String readVersion() {
        return PhetApplicationConfig.getVersion( "cck" ).formatForTitleBar();
    }

//    static class CCKPhetGraphicModuleAdapter extends Module {
//        private CCKPhetgraphicsModule cckModule;
//
//        /* Aspect ratio panel used in single-module setups*/
//        private AspectRatioPanel aspectRatioPanel;
//
//        public CCKPhetGraphicModuleAdapter( String[] args ) throws IOException {
//            super( "CCK-phetgraphics", new SwingClock( 30, 1 ) );
//
//            cckModule = new CCKPhetgraphicsModule( args );
//            aspectRatioPanel = new AspectRatioPanel( cckModule.getCCKApparatusPanel(), 5, 5, 1.2 );
//            cckModule.initControlPanel( this );
////            setSimulationPanel( cckModule.getCCKApparatusPanel() );
//            setSimulationPanel( aspectRatioPanel );
//            setControlPanel( cckModule.getControlPanel() );
//            addModelElement( new ModelElement() {
//                public void stepInTime( double dt ) {
//                    cckModule.getModel().stepInTime( dt );
//                    cckModule.getCCKApparatusPanel().synchronizeImmediately();
//                }
//            } );
//            setLogoPanel( null );
//        }
//
//        public void setHelpEnabled( boolean enabled ) {
//            super.setHelpEnabled( enabled );
//            cckModule.setHelpEnabled( enabled );
//        }
//
//        public void activate() {
//            super.activate();
//            Bulb.setHeightScale( 0.25 );
//        }
//    }

    public static void main( final String[] args ) throws InvocationTargetException, InterruptedException {

        //    Locale.setDefault( new Locale( "ja" ) );
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
//                CCKResources.init( args, CCKApplication.localizedStringsPath );
                new CCKPhetLookAndFeel().initLookAndFeel();
                try {
                    CCKApplication cckApplication = new CCKApplication( args );
                    cckApplication.startApplication();
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

}

package edu.colorado.phet.ehockey;

//Mediator applet for Electric edu.colorado.phet.ehockey.Hockey

import javax.swing.*;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Locale;

//Need File class

public class Hockey extends JApplet implements Runnable {
    static boolean isApplet = true;
    private int width;
    private int height;
    private PlayingField playingField;
    private Model model;
    private FieldGrid fieldGrid;
    private ControlPanel controlPanel;
    private BarrierList barrierList;
    AudioClip tada, cork;
    Image plusDisk, minusDisk, plusBag, minusBag, positivePuckImage;

    Container pane;
    MyClipLoader mcl;
    public Image negativePuckImage;

    public void init() {

        //width = getWidth();
        //height = getHeight();
        width = 700;
        height = 600;

        if ( isApplet ) {
            String applicationLocale = Toolkit.getDefaultToolkit().getProperty( "javaws.locale", null );
            if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
                SimStrings.setLocale( new Locale( applicationLocale ) );
            }
            SimStrings.setStrings( HockeyConfig.localizedStringPath );
        }

        barrierList = new BarrierList( this );
        model = new Model( width, height, this );
        controlPanel = new ControlPanel( this );

        playingField = new PlayingField( width, height, this );
        fieldGrid = new FieldGrid( width, height, this );

        ClassLoader cl = getClass().getClassLoader();
        this.mcl = new MyClipLoader( cl, this );
        ResourceLoader4 ralf = new ResourceLoader4( cl, this );
        new Thread( this ).start();
        plusDisk = getImage( ralf, "plusDisk.gif" );
        minusDisk = getImage( ralf, "minusDisk.gif" );
        plusBag = getImage( ralf, "plusBag.gif" );
        minusBag = getImage( ralf, "minusBag.gif" );
        positivePuckImage = getImage( ralf, "puckPositive.gif" );
        negativePuckImage = getImage( ralf, "puckNegative.gif" );
        pane = getContentPane();
        pane.setLayout( new BorderLayout() );
        pane.add( playingField, BorderLayout.CENTER );
        pane.add( controlPanel, BorderLayout.SOUTH );
    }

    public AudioClip getAudioClip( MyClipLoader mcl, String name ) {
        return mcl.loadAudioClip( name );
    }

    public Image getImage( ResourceLoader4 ralf, String name ) {
        return ralf.loadBufferedImage( name );
    }

    public void paintComponent( Graphics g ) {

    }

    public PlayingField getPlayingField() {
        return playingField;
    }

    public FieldGrid getFieldGrid() {
        return fieldGrid;
    }

    public Model getModel() {
        return model;
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public static void main( String[] args ) {

        String applicationLocale = System.getProperty( "javaws.locale" );
        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            SimStrings.setLocale( new Locale( applicationLocale ) );
        }
        String argsKey = "user.language=";
        if( args.length > 0 && args[0].startsWith( argsKey )) {
            String locale = args[0].substring( argsKey.length(), args[0].length() );
            SimStrings.setLocale( new Locale( locale ));
        }

        SimStrings.setStrings( HockeyConfig.localizedStringPath );
        isApplet = false;

        JFrame f = new JFrame( SimStrings.get( "HockeyApplication.Title" ) );
        Hockey mg = new Hockey();
        f.setContentPane( mg );
        f.setSize( 800, 750 );
        f.setVisible( true );
        mg.init();

        f.addWindowListener( new Exit() );
        f.invalidate();
        f.repaint();
        f.validate();
        //f.update();
        f.repaint();
    }

    public void run() {
        tada = getAudioClip( mcl, "tada.WAV" );
        cork = getAudioClip( mcl, "cork.au" );
    }

    public static class Exit implements WindowListener {
        public void windowOpened( WindowEvent e ) {
        }

        public void windowClosing( WindowEvent e ) {
            System.exit( 0 );
        }

        public void windowClosed( WindowEvent e ) {
        }

        public void windowIconified( WindowEvent e ) {
        }

        public void windowDeiconified( WindowEvent e ) {
        }

        public void windowActivated( WindowEvent e ) {
        }

        public void windowDeactivated( WindowEvent e ) {
        }

    }
}

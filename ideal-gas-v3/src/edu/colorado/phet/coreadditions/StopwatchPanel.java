/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.*;
import edu.colorado.phet.common.util.EventChannel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.EventListener;
import java.util.EventObject;

/**
 * A stopwatch panel.
 * <p/>
 * The panel has its own clock that has the same dt as the clock provided as a parameter in the constructor.
 */
public class StopwatchPanel extends JPanel implements ClockTickListener, ClockStateListener, ModelElement {

    private JTextField clockTF = new JTextField();
    private NumberFormat clockFormat = new DecimalFormat( "0.00" );
    private String[] startStopStr;
    private EventChannel stopwatchEventChannel = new EventChannel( StopwatchListener.class );
    private StopwatchListener stopwatchListenerProxy = (StopwatchListener)stopwatchEventChannel.getListenerProxy();
    private JButton resetBtn;
    // Time scale factor
    private double scaleFactor = 1;
    private double runningTime = 0;
    private boolean isRunning = false;

    /**
     * @param model
     */
    public StopwatchPanel( BaseModel model ) {
        this( model, "", 1 );
    }

    /**
     * @param model
     * @param timeUnits
     * @param scaleFactor     Time scale factor
     */
    public StopwatchPanel( BaseModel model, String timeUnits, double scaleFactor ) {

        model.addModelElement( this );
        setBackground( new Color( 237, 225, 113 ) );

        this.scaleFactor = scaleFactor;

        // Clock readout
        setBorder( BorderFactory.createRaisedBevelBorder() );
        clockTF = new JTextField( 5 );
        Font clockFont = clockTF.getFont();
        clockTF.setFont( new Font( clockFont.getName(), Font.BOLD, 16 ) );
        clockTF.setEditable( false );
        clockTF.setHorizontalAlignment( JTextField.RIGHT );

        // Initialize the contents of the clockTF
        resetClock();

        // Start/stop button
        startStopStr = new String[2];
        startStopStr[0] = "Start";
        startStopStr[1] = "Stop";
        JButton startStopBtn = new JButton( startStopStr[0] );
        startStopBtn.addActionListener( new StartStopActionListener( startStopBtn ) );

        // Reset button
        resetBtn = new JButton( "Reset" );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                resetClock();
            }
        } );

        // Lay out the panel
        this.setLayout( new FlowLayout() );
        add( startStopBtn );
        add( resetBtn );
        add( clockTF );
        add( new JLabel( timeUnits ) );

        // Clear the clock
        resetClock();
    }

    private void resetClock() {
        runningTime = 0;
        displayRunningTime();
        StopwatchEvent event = new StopwatchEvent( this );
        event.setReset( true );
        event.setRunning( false );
        stopwatchListenerProxy.reset( event );
    }

    public void setClockPanelVisible( boolean isVisible ) {
        setVisible( isVisible );
    }

    public boolean isClockPanelVisible() {
        return isVisible();
    }

    private class StartStopActionListener implements ActionListener {
        JButton startStopBtn;
        int startStopState = 0;

        public StartStopActionListener( JButton startStopBtn ) {
            this.startStopBtn = startStopBtn;
        }

        public void actionPerformed( ActionEvent e ) {
            if( startStopState == 0 ) {
                StopwatchEvent event = new StopwatchEvent( this );
                event.setRunning( true );
                stopwatchListenerProxy.start( event );
                resetBtn.setEnabled( false );
                isRunning = true;
            }
            else {
                StopwatchEvent event = new StopwatchEvent( this );
                event.setRunning( false );
                stopwatchListenerProxy.stop( event );
                resetBtn.setEnabled( true );
                isRunning = false;
            }

            // Set the proper text for the button, and do a bunch of messing arround to
            // set the size so it doesn't change when the text changes.
            startStopState = ( startStopState + 1 ) % 2;
            Dimension prevSize = startStopBtn.getSize();
            startStopBtn.setText( startStopStr[startStopState] );
            Dimension currSize = startStopBtn.getSize();
            Dimension newSize = new Dimension( Math.max( prevSize.width, currSize.width ), currSize.height );
            startStopBtn.setPreferredSize( newSize );
        }
    }

    private void displayRunningTime() {
        String s = clockFormat.format( runningTime * scaleFactor );
        clockTF.setText( s );
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------
    boolean savedResetState;

    /**
     * Responds to state changes in the simulation clock
     *
     * @param event
     */
    public void stateChanged( ClockStateEvent event ) {
        if( event.getIsPaused() ) {
            savedResetState = resetBtn.isEnabled();
            resetBtn.setEnabled( true );
        }
        else {
            resetBtn.setEnabled( savedResetState );
        }
    }

    /**
     * Responds to ticks of the stopwatch clock
     *
     * @param event
     */
    public void clockTicked( ClockTickEvent event ) {
        AbstractClock c = (AbstractClock)event.getSource();
        // TODO: scale factor goes here
        String s = clockFormat.format( c.getRunningTime() );
        clockTF.setText( s );
    }

    public void stepInTime( double dt ) {
        if( isRunning ) {
            runningTime += dt;
            displayRunningTime();
        }
    }

    //-----------------------------------------------------------------
    // Event and Listener definitions
    //-----------------------------------------------------------------
    public interface StopwatchListener extends EventListener {
        void start( StopwatchEvent event );

        void stop( StopwatchEvent event );

        void reset( StopwatchEvent event );
    }

    public class StopwatchEvent extends EventObject {
        boolean isRunning = true;
        boolean isReset;

        public StopwatchEvent( Object source ) {
            super( source );
        }

        public boolean isRunning() {
            return isRunning;
        }

        public void setRunning( boolean running ) {
            isRunning = running;
        }

        public boolean isReset() {
            return isReset;
        }

        public void setReset( boolean reset ) {
            isReset = reset;
        }
    }

    public void addListener( StopwatchListener listener ) {
        stopwatchEventChannel.addListener( listener );
    }

    public void removeListener( StopwatchListener listener ) {
        stopwatchEventChannel.removeListener( listener );
    }
}

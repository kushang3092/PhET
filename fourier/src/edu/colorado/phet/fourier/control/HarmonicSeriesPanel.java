/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.model.HarmonicSeries;
import edu.colorado.phet.fourier.util.EasyGridBagLayout;


/**
 * HarmonicSeriesPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicSeriesPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Things to be controlled.
    private HarmonicSeries _harmonicSeriesModel;
    
    // UI components
    private ControlPanelSlider _numberOfHarmonicsSlider;
    private ControlPanelSlider _fundamentalFrequencySlider;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param harmonicSeriesModel
     */
    public HarmonicSeriesPanel( HarmonicSeries harmonicSeriesModel )
    {
        assert( harmonicSeriesModel != null );
        
        // Things we'll be controlling.
        _harmonicSeriesModel = harmonicSeriesModel;
        
        // Title
        Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, 2 );
        String title = SimStrings.get( "HarmonicSeriesPanel.title" );
        TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, title );
        setBorder( titleBorder );
        
        // Number of harmonics
        {
            String format = SimStrings.get( "HarmonicSeriesPanel.numberOfHarmonics" );
            _numberOfHarmonicsSlider = new ControlPanelSlider( format );
            _numberOfHarmonicsSlider.setMaximum( 15 );
            _numberOfHarmonicsSlider.setMinimum( 5 );
            _numberOfHarmonicsSlider.setValue( 7 );
            _numberOfHarmonicsSlider.setMajorTickSpacing( 2 );
            _numberOfHarmonicsSlider.setMinorTickSpacing( 1 );
            _numberOfHarmonicsSlider.setSnapToTicks( true );
        }
        
        // Fundamental frequency
        {
            String format = SimStrings.get( "HarmonicSeriesPanel.fundamentalFrequency" );
            _fundamentalFrequencySlider = new ControlPanelSlider( format );
            _fundamentalFrequencySlider.setMaximum( 1200 );
            _fundamentalFrequencySlider.setMinimum( 200 );
            _fundamentalFrequencySlider.setValue( 440 );
            _fundamentalFrequencySlider.setMajorTickSpacing( 250 );
            _fundamentalFrequencySlider.setMinorTickSpacing( 50 );
            _fundamentalFrequencySlider.setSnapToTicks( false );
        }
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        layout.addFilledComponent( _numberOfHarmonicsSlider, row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addFilledComponent( _fundamentalFrequencySlider, row++, 0, GridBagConstraints.HORIZONTAL );
        
        // Wire up event handling.
        EventListener listener = new EventListener();
        _numberOfHarmonicsSlider.addChangeListener( listener );
        _fundamentalFrequencySlider.addChangeListener( listener );
        
        // Set the state of the controls.
        update();
    }
    
    /**
     * Updates the control panel to match the state of the things that it's controlling.
     */
    public void update() {
        _numberOfHarmonicsSlider.setValue( _harmonicSeriesModel.getNumberOfHarmonics() );
        _fundamentalFrequencySlider.setValue( (int)_harmonicSeriesModel.getFundamentalFrequency() );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     */
    private class EventListener implements ChangeListener {
        
        public EventListener() {}
        
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _numberOfHarmonicsSlider ) {
                int numberOfHarmonics = _numberOfHarmonicsSlider.getValue();
                _harmonicSeriesModel.setNumberOfHarmonics( numberOfHarmonics );
            }
            else if ( event.getSource() == _fundamentalFrequencySlider ) {
                int fundamentalFrequency = _fundamentalFrequencySlider.getValue();
                _harmonicSeriesModel.setFundamentalFrequency( fundamentalFrequency );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
    }
}

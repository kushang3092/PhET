/**
 * Class: SingleSourceWithBoxModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 19, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.sound.model.AttenuationFunction;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.WaveMedium;
import edu.colorado.phet.sound.view.SoundApparatusPanel;
import edu.colorado.phet.sound.view.SoundControlPanel;
import edu.colorado.phet.sound.view.DialGauge;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;

public class SingleSourceWithBoxModule extends SingleSourceListenModule {
    private AirBoxGraphic boxInteriorGraphic;
    private PhetShapeGraphic boxGraphic;

    protected SingleSourceWithBoxModule( ApplicationModel appModel ) {
        super( appModel, "<html>Listen with<br>Varying Air Pressure</html>" );
        init();
    }

    private void init() {
        SoundModel soundModel = (SoundModel)getModel();
        WaveMedium waveMedium = soundModel.getWaveMedium();

        Shape box = createBox();
        VariableWaveMediumAttenuationFunction attenuationFunction = new VariableWaveMediumAttenuationFunction();
        attenuationFunction.setVariableRegion( box );
        waveMedium.setAttenuationFunction( attenuationFunction );
        boxGraphic = new PhetShapeGraphic( getApparatusPanel(), box, new BasicStroke( 8f ), new Color( 124, 80, 10 ) );
        boxInteriorGraphic = new AirBoxGraphic( getApparatusPanel(), box );

        SoundControlPanel controlPanel = (SoundControlPanel)getControlPanel();
        controlPanel.addPanel( new BoxAirDensityControlPanel( attenuationFunction ) );

        // Make the listener the audio source
        getAudioControlPanel().setAudioSource( SoundApparatusPanel.LISTENER_SOURCE );
    }

    /**
     * Creates a shape that is a box with a round wall on the front
     *
     * @return
     */
    private Shape createBox() {
        int boxWidth = 180;
        int boxHeight = 350;

        float b = 50;
        GeneralPath box = new GeneralPath();
        float x0 = SoundConfig.s_wavefrontBaseX - b;
        float y0 = SoundConfig.s_wavefrontBaseY - boxHeight / 2;
        double alpha = Math.atan( ( boxHeight / 2 ) / ( boxWidth - b ) );
        float d = (float)( boxWidth + ( boxHeight / 2 ) * Math.tan( alpha ) );
        box.moveTo( x0, y0 );
        box.lineTo( x0 + boxWidth, y0 );
        box.quadTo( x0 + d * 0.85f, SoundConfig.s_wavefrontBaseY,
                    x0 + boxWidth, y0 + boxHeight );
        box.lineTo( x0, y0 + boxHeight );
        box.closePath();
        return box;
    }

    public int rgbAt( int x, int y ) {
        // todo: figure out how to get rid of the hard-coded 80.
        if( boxInteriorGraphic.contains( SoundConfig.s_wavefrontBaseX + 80 + x,
                                         SoundConfig.s_wavefrontBaseY + y ) ) {
            return boxInteriorGraphic.getGrayLevel();
        }
        else {
            return super.rgbAt( x, y );
        }
    }

    public void setAudioSource( int source ) {
        super.setAudioSource( source );
    }

    static class AirBoxGraphic extends PhetShapeGraphic {
        static Color[] grayLevels = new Color[256];
        private int grayLevel;

        static {
            for( int i = 0; i < 256; i++ ) {
                grayLevels[i] = new Color( i, i, i );
            }
        }

        AirBoxGraphic( Component component, Shape shape ) {
            super( component, shape, new Color( 128, 128, 128 ), new BasicStroke( 1f ), new Color( 138, 100, 70 ) );
        }

        void setAirDensity( double density ) {
            grayLevel = 255 - (int)( 128 * density );
            this.setPaint( grayLevels[grayLevel] );
        }

        int getGrayLevel() {
            return grayLevel;
        }
    }

    class BoxAirDensityControlPanel extends JPanel {
        VariableWaveMediumAttenuationFunction attenuationFunction;
        String evacuateLabel = "<html>Remove Air<br>from Box</html>";
        String addLabel = "<html>Add Air<br>to Box</html>";
        boolean evacuateToggle = true;
        private JButton airButton;
        private JSlider densitySlider;

        public BoxAirDensityControlPanel( final VariableWaveMediumAttenuationFunction attenuationFunction ) {

            this.attenuationFunction = attenuationFunction;

            airButton = new JButton( evacuateLabel );
            airButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new BoxEvacuator().start();
                }
            } );

            final int maxValue = 200;
            densitySlider = new JSlider( JSlider.HORIZONTAL, 0, maxValue, maxValue );
            densitySlider.setSnapToTicks( true );
            densitySlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setAirDensity( densitySlider, maxValue, attenuationFunction );
                }
            } );
            setAirDensity( densitySlider, maxValue, attenuationFunction );
            densitySlider.setEnabled( false );

            this.setLayout( new GridBagLayout() );
            Insets insets = new Insets( 0, 0, 0, 0 );
            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL,
                                                             insets, 0, 0 );
            this.add( airButton, gbc );
            airButton.setBackground( new Color( 100, 200, 100 ) );
            gbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
                                          GridBagConstraints.CENTER,
                                          GridBagConstraints.HORIZONTAL,
                                          insets, 0, 0 );
            this.add( densitySlider, gbc );
//            this.setLayout( new GridLayout( 2, 1 ) );
//            this.add( airButton );
//            this.add( densitySlider );
            this.setBorder( new TitledBorder( "Air Density" ) );
            this.setPreferredSize( new Dimension( 120, 120 ) );
        }

        class BoxEvacuator extends Thread {
            private int maxValue;
            private int minValue;

            BoxEvacuator() {
                this.maxValue = densitySlider.getMaximum();
                this.minValue = densitySlider.getMinimum();
            }

            public void run() {

                try {
                    // If we're removig air, display box
                    if( evacuateToggle ) {
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                getApparatusPanel().addGraphic( boxGraphic, 8 );
                                getApparatusPanel().addGraphic( boxInteriorGraphic, 6 );
                            }
                        } );
                    }
                    Thread.sleep( 2000 );

                    // Pump air out or in
                    int incr = evacuateToggle ? -1 : 1;
                    int value = evacuateToggle ? maxValue : minValue;
                    int stop = evacuateToggle ? minValue : maxValue;
                    while( value != stop ) {
                        value += incr;
                        densitySlider.setValue( value );
                        Thread.sleep( 20 );
                    }

                    // If we're adding air, hide box
                    Thread.sleep( 2000 );
                    if( !evacuateToggle ) {
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                getApparatusPanel().removeGraphic( boxGraphic );
                                getApparatusPanel().removeGraphic( boxInteriorGraphic );
                            }
                        } );
                    }
                    evacuateToggle = !evacuateToggle;
                    airButton.setText( evacuateToggle ? evacuateLabel : addLabel );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }

        private void setAirDensity( final JSlider densitySlider, final int maxValue, final VariableWaveMediumAttenuationFunction attenuationFunction ) {
            double airDensity = ( (double)densitySlider.getValue() ) / maxValue;
            attenuationFunction.setVariableRegionAttenuation( airDensity );
            boxInteriorGraphic.setAirDensity( airDensity );
        }

    }

    class VariableWaveMediumAttenuationFunction implements AttenuationFunction {
        private Shape variableRegion;
        private double variableRegionAttenuation = 1;

        public void setVariableRegion( Shape region ) {
            this.variableRegion = region;
        }

        /**
         * This computes the attenuation of the signal given distance from the source.
         *
         * @param variableRegionAttenuation
         */
        public void setVariableRegionAttenuation( double variableRegionAttenuation ) {
            // The function is an arc of a circle
            this.variableRegionAttenuation = Math.sqrt( 1 - ( variableRegionAttenuation - 1 ) * ( variableRegionAttenuation - 1 ) );
        }

        public double getAttenuation( double x, double y ) {
            if( variableRegion != null && variableRegion.contains( x + SoundConfig.s_speakerBaseX, y + 201 ) ) {
                return variableRegionAttenuation;
            }
            else {
                return 1;
            }
        }
    }
}

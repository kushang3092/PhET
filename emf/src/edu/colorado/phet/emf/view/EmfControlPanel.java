/**
 * Class: EmfControlPanel
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: May 27, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.ControlPane;
import edu.colorado.phet.coreadditions.MessageFormatter;
import edu.colorado.phet.emf.EmfModule;
import edu.colorado.phet.emf.command.*;
import edu.colorado.phet.emf.model.EmfModel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class EmfControlPanel extends ControlPane implements Observer {

    private EmfModel model;
    private EmfModule module;

    public EmfControlPanel( EmfModel model, EmfModule module ) {
        this.model = model;
        this.module = module;
        model.addObserver( this );
        createControls();
    }

    private void createControls() {
        this.setPreferredSize( new Dimension( 180, 400 ) );
        this.setLayout( new GridBagLayout());
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, new Legend(),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new MovementControlPane(),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new OptionControlPane(),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
//        this.add( new Legend() );
//        this.add( new MovementControlPane() );
//        this.add( new OptionControlPane() );
    }

    public void update( Observable o, Object arg ) {
    }

    //
    // Inner classes
    //

    /**
     * An inner class for the controls that enable, disable or set the values
     * of various options
     */
    private class OptionControlPane extends ControlPane {

//        JCheckBox autoscaleCB = new JCheckBox( MessageFormatter.format( ( "Autoscale vectors" ) ) );
        private JRadioButton fullFieldRB = new JRadioButton( "Full field" );
        private JRadioButton splineCurveWVectorsRB = new JRadioButton( "Curve with Vectors" );
        private JRadioButton splineCurveRB = new JRadioButton( "Curve" );
        JRadioButton hideFieldRB = new JRadioButton( "None" );
        private JCheckBox stripChartCB = new JCheckBox( "Display strip chart" );
        private JRadioButton staticFieldRB = new JRadioButton( "Static field" );
        private JRadioButton dynamicFieldRB = new JRadioButton( "Radiated field " );
        private ButtonGroup fieldDisplayRBGroup;
        ;

        OptionControlPane() {

            this.setLayout( new GridBagLayout() );

            // Field vector display radio buttons
            ButtonGroup fieldTypeRBGroup = new ButtonGroup();
            fieldTypeRBGroup.add( dynamicFieldRB );
            fieldTypeRBGroup.add( staticFieldRB );
            JPanel fieldVectorPane = new JPanel( new GridLayout( 2, 1 ) );
            fieldVectorPane.setBorder( BorderFactory.createTitledBorder( "Field Displayed" ) );
            fieldVectorPane.add( dynamicFieldRB );
            fieldVectorPane.add( staticFieldRB );

            fieldDisplayRBGroup = new ButtonGroup();
            fieldDisplayRBGroup.add( hideFieldRB );
            hideFieldRB.addActionListener( new DisplayTypeRBActionListener() );
            fieldDisplayRBGroup.add( splineCurveWVectorsRB );
            splineCurveWVectorsRB.addActionListener( new DisplayTypeRBActionListener() );
            fieldDisplayRBGroup.add( splineCurveRB );
            splineCurveRB.addActionListener( new DisplayTypeRBActionListener() );
            fieldDisplayRBGroup.add( fullFieldRB );
            fullFieldRB.addActionListener( new DisplayTypeRBActionListener() );
            JPanel fieldDisplayPane = new JPanel( new GridLayout( 4, 1 ));
            fieldDisplayPane.setBorder( BorderFactory.createTitledBorder( "Display Type" ));
            fieldDisplayPane.add( splineCurveWVectorsRB );
            fieldDisplayPane.add( splineCurveRB );
            fieldDisplayPane.add( fullFieldRB );
            fieldDisplayPane.add( hideFieldRB );
//            fieldVectorPane.add( autoscaleCB );

            staticFieldRB.addActionListener( new FieldViewRBActionListener() );
            dynamicFieldRB.addActionListener( new FieldViewRBActionListener() );
            hideFieldRB.addActionListener( new FieldViewRBActionListener() );

            // Field sense options
            ButtonGroup fieldSenseRBGroup = new ButtonGroup();
            JPanel fieldSensePane = new JPanel( new GridLayout( 2, 1 ));
            JRadioButton fFieldRB = new JRadioButton( MessageFormatter.format( "Force on\nelectron" ));
            fFieldRB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setFieldSense( FieldLatticeView.FORCE_ON_ELECTRON );
                }
            } );
            fieldSenseRBGroup.add( fFieldRB );
            fieldSensePane.add( fFieldRB );
            JRadioButton eFieldRB = new JRadioButton( "Electric field" );
            eFieldRB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setFieldSense( FieldLatticeView.ELECTRIC_FIELD );
                }
            } );
            fieldSenseRBGroup.add( eFieldRB );
            fieldSensePane.add( eFieldRB );
            TitledBorder fieldSenseBorder = BorderFactory.createTitledBorder( "Field Sense" );
            fieldSensePane.setBorder( fieldSenseBorder );

            // Option check boxes
//            autoscaleCB.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    module.setAutoscaleEnabled( autoscaleCB.isSelected() );
//                }
//            } );

            splineCurveWVectorsRB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new SetFieldCurveEnabledCmd( splineCurveWVectorsRB.isSelected() ).doIt();
                }
            } );

            stripChartCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setStripChartEnabled( stripChartCB.isSelected() );
                }
            } );

            JButton recenterButton = new JButton( "Recenter" );
            recenterButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.recenterElectrons();
                }
            } );

            final JCheckBox useBufferedImageCB = new JCheckBox( "Use buffered image" );
            useBufferedImageCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setUseBufferedImage( useBufferedImageCB.isSelected() );
                }
            } );
            module.setUseBufferedImage( false );


            try {
                int componentIdx = 0;
                GraphicsUtil.addGridBagComponent( this, fieldVectorPane,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, fieldDisplayPane,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, fieldSensePane,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, stripChartCB,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }

            // Set initial conditions
            splineCurveWVectorsRB.setSelected( true );
            this.setDisplayType();

            fFieldRB.setSelected( true );

            dynamicFieldRB.setSelected( true );
            setFieldView();

//            autoscaleCB.setSelected( false );
//            module.setAutoscaleEnabled( autoscaleCB.isSelected() );

        }

        private void setDisplayType() {
            JRadioButton rb = GraphicsUtil.getSelection( fieldDisplayRBGroup );
            int display = EmfPanel.NO_FIELD;
            display = rb == fullFieldRB ? EmfPanel.FULL_FIELD : display;
            display = rb == splineCurveRB ? EmfPanel.CURVE : display;
            display = rb == splineCurveWVectorsRB ? EmfPanel.CURVE_WITH_VECTORS : display;
            module.setFieldDisplay( display );
//            new SetFieldCurveEnabledCmd( splineCurveWVectorsRB.isSelected() ).doIt();
        }

        private class DisplayTypeRBActionListener implements ActionListener {
            public void actionPerformed( ActionEvent e ) {
                setDisplayType();
            }
        }

        private void setFieldView() {
            module.displayStaticField( staticFieldRB.isSelected() );
            module.displayDynamicField( dynamicFieldRB.isSelected() );
            new StaticFieldIsEnabledCmd( model, true ).doIt();
            new DynamicFieldIsEnabledCmd( model, true ).doIt();

            splineCurveRB.setEnabled( dynamicFieldRB.isSelected() );
            splineCurveWVectorsRB.setEnabled( dynamicFieldRB.isSelected() );
        }

        private class FieldViewRBActionListener implements ActionListener {
            public void actionPerformed( ActionEvent e ) {
                setFieldView();
            }
        }
    }

    /**
     * An inner class for the radio buttons that control how the transmitting
     * electrons move
     */
    private class MovementControlPane extends ControlPane {

        JRadioButton sineRB = new JRadioButton( "Sinusoidal" );
        JRadioButton manualRB = new JRadioButton( "Manual" );
        JCheckBox coordinateFACB = new JCheckBox( MessageFormatter.format( "Coordinate frequency\nand amplitude" ) );
        ButtonGroup rbGroup = new ButtonGroup();
        JSlider freqSlider = new JSlider( 0, 200, 100 );
        int maxAmplitude = 100;
        JSlider ampSlider = new JSlider( 0, maxAmplitude, maxAmplitude / 2 );
        private boolean coordinateFandA;

        MovementControlPane() {
            this.setLayout( new GridBagLayout() );
            rbGroup.add( sineRB );
            rbGroup.add( manualRB );
            EtchedBorder etchedBorder = (EtchedBorder)BorderFactory.createEtchedBorder();
            this.setBorder( etchedBorder );
            this.setBorder( BorderFactory.createTitledBorder( "Tranmitter Movement" ) );

            sineRB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setMovementType();
                }
            } );

            manualRB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setMovementType();
                }
            } );

            freqSlider.setPreferredSize( new Dimension( 100, 20 ) );
            freqSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    new SetFreqencyCmd( model, (float)freqSlider.getValue() / 5000 ).doIt();

                    // Adjust the amplitude so the vectors don't get too big
                    if( coordinateFandA ) {
                        double ampPercentage = 1 - ( (double)freqSlider.getValue() ) / freqSlider.getMaximum();
                        ampSlider.setValue( (int)( maxAmplitude * ampPercentage ) );
                    }
                }
            } );


            ampSlider.setPreferredSize( new Dimension( 100, 20 ) );
            ampSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    new SetAmplitudeCmd( model, (float)ampSlider.getValue() ).doIt();
                }
            } );
            new SetAmplitudeCmd( model, (float)ampSlider.getValue() ).doIt();

            coordinateFACB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setCoordinateFandA( coordinateFACB.isSelected() );
                }
            } );
            coordinateFACB.setSelected( false );
            setCoordinateFandA( coordinateFACB.isSelected() );

            int componentIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, manualRB,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, sineRB,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, new JLabel( "Frequency" ),
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, freqSlider,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, new JLabel( "Amplitude" ),
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, ampSlider,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.CENTER );
//                GraphicsUtil.addGridBagComponent( this, coordinateFACB,
//                                                  0, componentIdx++, 1, 1,
//                                                  GridBagConstraints.NONE,
//                                                  GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }

            // Set initial conditions
            manualRB.setSelected( true );
            setMovementType();
        }

        void setCoordinateFandA( boolean coordinateFandA ) {
            this.coordinateFandA = coordinateFandA;
        }

        void setMovementType() {
            if( manualRB.isSelected() ) {
                module.setMovementManual();
                module.recenterElectrons();
                module.setAutoscaleEnabled( false );
                freqSlider.setEnabled( false );
                ampSlider.setEnabled( false );
            }
            if( sineRB.isSelected() ) {
                module.setMovementSinusoidal();
                module.recenterElectrons();
                module.setAutoscaleEnabled( false );
                freqSlider.setEnabled( true );
                ampSlider.setEnabled( true );
            }
        }
    }

    private class Legend extends JPanel {

        Legend() {
            setLayout( new GridBagLayout() );
            this.setBorder( BorderFactory.createTitledBorder( "Legend" ) );
            ImageIcon electronImg = new ImageIcon( ImageLoader.fetchImage( "images/small-yellow-electron.gif" ));
            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, new JLabel( "Electron", electronImg, SwingConstants.LEFT ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }
}
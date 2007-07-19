package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.motion.MotionResources;
import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 8:55:43 AM
 */

public class GraphTimeControlNode extends PNode {
    private PSwing goStopButton;
    private PSwing clearButton;
    private PNode seriesLayer = new PNode();
    private boolean editable = true;
    private boolean constructed = false;
    private TimeSeriesModel timeSeriesModel;

    public GraphTimeControlNode( TimeSeriesModel timeSeriesModel ) {
        this.timeSeriesModel = timeSeriesModel;
        addChild( seriesLayer );

        goStopButton = new PSwing( new GoStopButton( timeSeriesModel ) );
        addChild( goStopButton );

        clearButton = new PSwing( new ClearButton( timeSeriesModel ) );
        addChild( clearButton );

        constructed = true;
        relayout();
    }

    public GraphTimeControlNode( String title, String abbr, ISimulationVariable simulationVariable, TimeSeriesModel graphTimeSeries ) {
        this( title, abbr, simulationVariable, graphTimeSeries, Color.black );
    }

    public GraphTimeControlNode( String title, String abbr, ISimulationVariable simulationVariable, TimeSeriesModel graphTimeSeries, Color color ) {
        this( graphTimeSeries );
        addVariable( title, abbr, color, simulationVariable );
        relayout();
    }

    public SeriesNode addVariable( String title, String abbr, Color color, ISimulationVariable simulationVariable ) {
        SeriesNode seriesNode = new SeriesNode( title, abbr, color, simulationVariable );
        seriesNode.setEditable( editable );
        seriesNode.setOffset( 0, seriesLayer.getFullBounds().getHeight() + 5 );
        seriesLayer.addChild( seriesNode );
        relayout();
        return seriesNode;
    }

    public static class SeriesNode extends PNode {
        private ShadowPText shadowPText;
        private PSwing textBox;
        private TextBox box;

        public SeriesNode( String title, String abbr, Color color, ISimulationVariable simulationVariable ) {
            shadowPText = new ShadowPText( title );
            Font labelFont = new Font( "Lucida Sans", Font.BOLD, 14 );
            shadowPText.setFont( labelFont );
            shadowPText.setTextPaint( color );
            shadowPText.setShadowColor( Color.black );
            addChild( shadowPText );

            box = new TextBox( abbr, simulationVariable, color );
            textBox = new PSwing( box );
            addChild( textBox );
        }

        public void relayout( double dy ) {
            shadowPText.setOffset( 0, 0 );
            textBox.setOffset( 0, shadowPText.getFullBounds().getMaxY() + dy );
        }

        public void setEditable( boolean editable ) {
            box.setEditable( editable );
        }

        public TextBox getTextBox() {
            return box;
        }
    }

    private void relayout() {
        if( constructed ) {
            double dy = 5;
            seriesLayer.setOffset( 0, 0 );
            for( int i = 0; i < seriesLayer.getChildrenCount(); i++ ) {
                SeriesNode child = (SeriesNode)seriesLayer.getChild( i );
                child.relayout( dy );
            }
            goStopButton.setOffset( 0, seriesLayer.getFullBounds().getMaxY() + dy );
            clearButton.setOffset( 0, goStopButton.getFullBounds().getMaxY() + dy );
        }
    }

    public void setEditable( boolean editable ) {
        this.editable = editable;
        for( int i = 0; i < seriesLayer.getChildrenCount(); i++ ) {
            SeriesNode child = (SeriesNode)seriesLayer.getChild( i );
            child.setEditable( editable );
        }
        setHasChild( goStopButton, editable );
        setHasChild( clearButton, editable );
    }

    private void setHasChild( PNode child, boolean addChild ) {
        if( addChild && !getChildrenReference().contains( child ) ) {
            addChild( child );
        }
        else if( !addChild && getChildrenReference().contains( child ) ) {
            removeChild( child );
        }
    }

    public static class ClearButton extends JButton {
        private TimeSeriesModel graphTimeSeries;

        public ClearButton( final TimeSeriesModel graphTimeSeries ) {
            super( "Clear" );
            this.graphTimeSeries = graphTimeSeries;
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    graphTimeSeries.clear();
                }
            } );
            graphTimeSeries.addListener( new TimeSeriesModel.Adapter() {
                public void dataSeriesChanged() {
                    updateEnabledState();
                }
            } );

            updateEnabledState();
        }

        private void updateEnabledState() {
            setEnabled( graphTimeSeries.isThereRecordedData() );
        }
    }

    public static class GoStopButton extends JButton {
        private boolean goButton = true;
        private TimeSeriesModel timeSeriesModel;

        public GoStopButton( final TimeSeriesModel timeSeriesModel ) {
            super( "Go" );
            this.timeSeriesModel = timeSeriesModel;
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( isGoButton() ) {
                        timeSeriesModel.startRecording();
                    }
                    else {
                        timeSeriesModel.setPaused( true );
                    }
                }
            } );
            timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {

                public void modeChanged() {
                    updateGoState();
                }

                public void pauseChanged() {
                    updateGoState();
                }
            } );
            updateGoState();
        }

        private void updateGoState() {
            setGoButton( !timeSeriesModel.isRecording() );
        }

        private void setGoButton( boolean go ) {
            this.goButton = go;
            setText( goButton ? "Go!" : "Stop" );
            try {
                setIcon( new ImageIcon( MotionResources.loadBufferedImage( goButton ? "go.png" : "stop.png" ) ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }

        private boolean isGoButton() {
            return goButton;
        }
    }

//    public static class ShadowJLabel extends JPanel {
//        private JLabel back;
//        private JLabel front;
//
//        public ShadowJLabel( String text, Color color ) {
////            setLayout( null );
//            back = new JLabel( text );
//            back.setForeground( Color.black );
////            back.setLocation( 2, 2 );
////            back.setOpaque( false );
//            add( back );
//
//            front = new JLabel( text );
//            front.setForeground( color );
////            front.setOpaque( false );
//            add( front );
////            setOpaque( false );
//            setBorder( new EtchedBorder( ) );
//        }
//
//        public Dimension getPreferredSize() {
//            return new Dimension( back.getPreferredSize().width + 2, back.getPreferredSize().height + 2 );
//        }
//
//        protected void paintComponent( Graphics g ) {
//            super.paintComponent( g );
//            g.drawString( "hello",0,0);
//        }
//    }

    public static class TextBox extends JPanel {
        private JTextField textField;
        private DecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );
        private ISimulationVariable simulationVariable;

        public TextBox( String valueAbbreviation, final ISimulationVariable simulationVariable, Color color ) {
            this.simulationVariable = simulationVariable;

            Font labelFont = new Font( "Lucida Sans", Font.BOLD, 18);
            add( new ShadowJLabel( valueAbbreviation, color, labelFont ) );

            JLabel equalsSign = new JLabel( " =" );
            equalsSign.setBackground( Color.white );
            equalsSign.setFont( labelFont );
            add( equalsSign );

            textField = new JTextField( "0.0", 6 );
            textField.setHorizontalAlignment( JTextField.RIGHT );
            add( textField );
            setBorder( BorderFactory.createLineBorder( Color.black ) );
            simulationVariable.addListener( new ISimulationVariable.Listener() {
                public void valueChanged() {
                    update();
                }
            } );
            textField.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setSimValueFromTextField();
                }
            } );
            textField.addFocusListener( new FocusAdapter() {
                public void focusLost( FocusEvent e ) {
                    setSimValueFromTextField();
                }

                public void focusGained( FocusEvent e ) {
                    textField.setSelectionStart( 0 );
                    textField.setSelectionEnd( textField.getText().length() );
                }
            } );
            update();
        }

        private void setSimValueFromTextField() {
            simulationVariable.setValue( Double.parseDouble( textField.getText() ) );
        }

        private void update() {
            textField.setText( decimalFormat.format( simulationVariable.getData().getValue() ) );
        }

        public void setEditable( boolean editable ) {
            textField.setEditable( editable );
        }

        public JTextField getTextField() {
            return textField;
        }
    }
}

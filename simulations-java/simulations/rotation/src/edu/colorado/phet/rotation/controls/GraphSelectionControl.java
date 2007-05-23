package edu.colorado.phet.rotation.controls;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSuite;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.util.GraphicsUtil;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:16:34 AM
 */

public class GraphSelectionControl extends JPanel {

    public GraphSelectionControl( RotationGraphSet rotationGraphSet, final GraphSetModel graphSetModel ) {
        setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = GraphicsUtil.createVerticalGridBagConstraints();
        JLabel label = new JLabel( "Show graphs for:" );
        label.setFont( RotationLookAndFeel.getControlPanelTitleFont() );
        add( label, gridBagConstraints );
        for( int i = 0; i < rotationGraphSet.getGraphSuites().length; i++ ) {
            add( new GraphSuiteRadioButton( graphSetModel, rotationGraphSet.getGraphSuites()[i] ), gridBagConstraints );
        }
    }

    static class GraphSuiteRadioButton extends JRadioButton {
        GraphSetModel graphSetPanel;
        GraphSuite graphSuite;

        public GraphSuiteRadioButton( final GraphSetModel graphSetModel, final GraphSuite graphSuite ) {
            super( graphSuite.getLabel(), graphSetModel.getGraphSuite() == graphSuite );
            this.graphSetPanel = graphSetModel;
            this.graphSuite = graphSuite;
            setFont( RotationLookAndFeel.getGraphSelectionItemFont() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    graphSetModel.setGraphSuite( graphSuite );
                }
            } );
            graphSetModel.addListener( new GraphSetModel.Listener() {
                public void graphSuiteChanged() {
                    setSelected( graphSetModel.getGraphSuite() == graphSuite );
                }
            } );
        }

        protected void paintComponent( Graphics g ) {
            boolean aa = GraphicsUtil.antialias( g, true );
            super.paintComponent( g );
            GraphicsUtil.antialias( g, aa );
        }
    }

}

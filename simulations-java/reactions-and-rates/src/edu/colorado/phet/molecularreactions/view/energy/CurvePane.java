/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.view.energy;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.view.AxisNode;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.common.view.util.SimStrings;

import java.awt.geom.Rectangle2D;
import java.awt.*;

public class CurvePane extends PPath {
    private final Color energyPaneBackgroundColor = MRConfig.ENERGY_PANE_BACKGROUND;
    private final Color curveColor = MRConfig.POTENTIAL_ENERGY_COLOR;
    private final Insets curveAreaInsets = new Insets( 20, 30, 40, 10 );
    private Dimension curvePaneSize, curveAreaSize;

    private volatile EnergyProfileGraphic energyProfileGraphic;

    private volatile EnergyLine energyLine;

    public CurvePane(final MRModel model, Dimension upperPaneSize, EnergyView.State state) {
        super( new Rectangle2D.Double( 0,
              0,
              state.curvePaneSize.getWidth() - 1,
              state.curvePaneSize.getHeight()
        ));

        curvePaneSize = new Dimension( upperPaneSize.width, (int)( MRConfig.ENERGY_VIEW_SIZE.getHeight() )
                                              - upperPaneSize.height
                                              - MRConfig.ENERGY_VIEW_REACTION_LEGEND_SIZE.height );

        curveAreaSize = new Dimension( (int)curvePaneSize.getWidth() - curveAreaInsets.left - curveAreaInsets.right,
                                       (int)curvePaneSize.getHeight() - curveAreaInsets.top - curveAreaInsets.bottom );

        final PNode totalEnergyLineLayer = new PNode();
        totalEnergyLineLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );
        final PNode curveLayer = new PNode();
        curveLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );
        PNode cursorLayer = new PNode();
        cursorLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );

        // the -1 adjusts for a stroke width issue between this pane and the chart pane.

        this.setOffset( 0, upperPaneSize.getHeight() );
        this.setPaint( energyPaneBackgroundColor );
        this.setStrokePaint( new Color( 0, 0, 0, 0 ) );
        this.addChild( totalEnergyLineLayer );
        this.addChild( curveLayer );
        this.addChild( cursorLayer );

        // Determine the size of the area where the curve will appear
        state.curveAreaSize = new Dimension( (int)state.curvePaneSize.getWidth() - curveAreaInsets.left - curveAreaInsets.right,
                                       (int)state.curvePaneSize.getHeight() - curveAreaInsets.top - curveAreaInsets.bottom );

        // Create the line that shows total energy, and a legend for it
        energyLine = new EnergyLine( state.curveAreaSize, model, state.module.getClock() );
        totalEnergyLineLayer.addChild( energyLine );

        // Create the curve, and add a listener to the model that will update the curve if the
        // model's energy profile changes
        createCurve( model, curveLayer );
        model.addListener( new MRModel.ModelListenerAdapter() {
            public void notifyEnergyProfileChanged( EnergyProfile profile ) {
                createCurve( model, curveLayer );
            }
        } );

        // Create the cursor
        state.cursor = new EnergyCursor( state.curveAreaSize.getHeight(), 0, state.curveAreaSize.getWidth(), model );
        state.cursor.setVisible( false );
        cursorLayer.addChild( state.cursor );

        // Add axes
        RegisterablePNode xAxis = new RegisterablePNode( new AxisNode( SimStrings.get( "EnergyView.ReactionCoordinate" ),
                                                                       200,
                                                                       MRConfig.ENERGY_PANE_TEXT_COLOR,
                                                                       AxisNode.HORIZONTAL,
                                                                       AxisNode.BOTTOM ) );
        xAxis.setRegistrationPoint( xAxis.getFullBounds().getWidth() / 2, 0 );
        xAxis.setOffset( this.getFullBounds().getWidth() / 2 + curveAreaInsets.left / 2,
                         this.getHeight() - 25 );
        this.addChild( xAxis );

        RegisterablePNode yAxis = new RegisterablePNode( new AxisNode( "Energy", 200,
                                                                       MRConfig.ENERGY_PANE_TEXT_COLOR,
                                                                       AxisNode.VERTICAL,
                                                                       AxisNode.TOP ) );
        yAxis.setRegistrationPoint( yAxis.getFullBounds().getWidth() / 2,
                                    -yAxis.getFullBounds().getHeight() / 2 );
        yAxis.setOffset( curveAreaInsets.left / 2, this.getFullBounds().getHeight() / 2 );
        this.addChild( yAxis );
    }

    public Dimension getCurvePaneSize() {
        return curvePaneSize;
    }

    public Dimension getCurveAreaSize() {
        return curveAreaSize;
    }

    public Insets getCurveAreaInsets() {
        return curveAreaInsets;
    }

    public Color getCurveColor() {
        return curveColor;
    }

    private void createCurve( MRModel model, PNode curveLayer ) {
        if( energyProfileGraphic != null ) {
            try {
                curveLayer.removeChild( energyProfileGraphic );
            }
            catch (ArrayIndexOutOfBoundsException e) {

            }
        }
        energyProfileGraphic = new EnergyProfileGraphic( model.getEnergyProfile(),
                                                         curveAreaSize,
                                                         curveColor );
        curveLayer.addChild( energyProfileGraphic );
    }

    public void setProfileManipulable(boolean manipulable ) {
        energyProfileGraphic.setManipulable( manipulable ); 
    }

    public void setTotalEnergyLineVisible( boolean visible ) {
        energyLine.setVisible( visible );
    }

    public void setLegendVisible(boolean visible) {
        energyLine.setLegendVisible( visible );
        energyProfileGraphic.setVisible(visible);
    }

    public double getIntersectionWithHorizontal(double x) {
        return energyProfileGraphic.getIntersectionWithHorizontal( energyLine.getEnergyLineY(), x);
    }
}

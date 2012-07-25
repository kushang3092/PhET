// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Base class model for the 2 tabs that deal with line forms (slope-intercept and point-slope).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineFormsModel implements Resettable {

    private static final int GRID_VIEW_UNITS = 530; // max dimension (width or height) of the grid in the view

    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final WellDefinedLineProperty interactiveLine; // the line that can be manipulated by the user
    public final ObservableList<StraightLine> savedLines; // lines that have been saved by the user
    public final ObservableList<StraightLine> standardLines; // standard lines (eg, y=x) that are available for viewing
    public final Graph graph; // the graph that plots the lines
    public final PointTool pointTool1, pointTool2; // tools for measuring points on the graph

    /**
     * Constructor.
     *
     * @param xRange          range of the x axis
     * @param yRange          range of the y axis
     * @param interactiveLine line that the user can manipulate
     */
    public LineFormsModel( IntegerRange xRange, IntegerRange yRange, WellDefinedLineProperty interactiveLine ) {

        final double mvtScale = GRID_VIEW_UNITS / Math.max( xRange.getLength(), yRange.getLength() ); // view units / model units
        this.mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 1.2 * GRID_VIEW_UNITS / 2, 1.25 * GRID_VIEW_UNITS / 2 ), mvtScale, -mvtScale ); // y is inverted

        this.interactiveLine = interactiveLine;
        this.savedLines = new ObservableList<StraightLine>();
        this.standardLines = new ObservableList<StraightLine>();

        this.graph = new Graph( xRange, yRange );

        // Observable collection of all lines, required by point tool.
        final ObservableList<StraightLine> allLines = new ObservableList<StraightLine>();
        {
            interactiveLine.addObserver( new ChangeObserver<StraightLine>() {
                public void update( StraightLine newLine, StraightLine oldLine ) {
                    allLines.add( newLine ); // add interactive line to end, so we find it last
                    if ( oldLine != null ) {
                        allLines.remove( oldLine );
                    }
                }
            } );
            final VoidFunction1<StraightLine> elementAddedObserver = new VoidFunction1<StraightLine>() {
                public void apply( StraightLine line ) {
                    allLines.add( 0, line ); // add saved and standard lines to from, so we find them first
                }
            };
            final VoidFunction1<StraightLine> elementRemovedObserver = new VoidFunction1<StraightLine>() {
                public void apply( StraightLine line ) {
                    allLines.remove( line );
                }
            };
            savedLines.addElementAddedObserver( elementAddedObserver );
            savedLines.addElementRemovedObserver( elementRemovedObserver );
            standardLines.addElementAddedObserver( elementAddedObserver );
            standardLines.addElementRemovedObserver( elementRemovedObserver );
        }

        this.pointTool1 = new PointTool( new Vector2D( xRange.getMin() + ( 0.75 * xRange.getLength() ), yRange.getMin() - 3 ), allLines );
        this.pointTool2 = new PointTool( new Vector2D( xRange.getMin() + ( 0.25 * xRange.getLength() ), pointTool1.location.get().getY() ), allLines );
    }

    public void reset() {
        interactiveLine.reset();
        savedLines.clear();
        standardLines.clear();
        pointTool1.reset();
        pointTool2.reset();
    }
}

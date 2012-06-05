// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode2.RiseSpinnerNode2;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode2.RunSpinnerNode2;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode2.X1SpinnerNode2;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode2.Y1SpinnerNode2;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Interface for manipulating a point-slope equation.
 * This version uses spinner buttons to increment/decrement rise, run, x1 and y1.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointSlopeEquationNodeSpinners extends PhetPNode {

    private static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private final Property<Double> rise, run, x1, y1;

    public PointSlopeEquationNodeSpinners( final Property<StraightLine> interactiveLine,
                                           Property<DoubleRange> riseRange,
                                           Property<DoubleRange> runRange,
                                           Property<DoubleRange> x1Range,
                                           Property<DoubleRange> y1Range ) {
        this( interactiveLine, riseRange, runRange, x1Range, y1Range,
              LGConstants.INTERACTIVE_EQUATION_FONT, LGConstants.STATIC_EQUATION_FONT, LGColors.STATIC_EQUATION_ELEMENT );
    }

    private PointSlopeEquationNodeSpinners( final Property<StraightLine> interactiveLine,
                                           Property<DoubleRange> riseRange,
                                           Property<DoubleRange> runRange,
                                           Property<DoubleRange> x1Range,
                                           Property<DoubleRange> y1Range,
                                           PhetFont interactiveFont,
                                           PhetFont staticFont,
                                           final Color staticColor ) {

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.x1 = new Property<Double>( interactiveLine.get().x1 );
        this.y1 = new Property<Double>( interactiveLine.get().y1 );

        // determine the max width of the rise and run spinners, based on the extents of their range
        double maxSlopeWidth;
        {
            PNode maxRiseNode = new RiseSpinnerNode2( UserComponents.riseSpinner, new Property<Double>( riseRange.get().getMax() ), riseRange, interactiveFont, FORMAT );
            PNode minRiseNode = new RiseSpinnerNode2( UserComponents.riseSpinner, new Property<Double>( riseRange.get().getMin() ), riseRange, interactiveFont, FORMAT );
            double maxRiseWidth = Math.max( maxRiseNode.getFullBoundsReference().getWidth(), minRiseNode.getFullBoundsReference().getWidth() );
            PNode maxRunNode = new RunSpinnerNode2( UserComponents.riseSpinner, new Property<Double>( runRange.get().getMax() ), runRange, interactiveFont, FORMAT );
            PNode minRunNode = new RunSpinnerNode2( UserComponents.riseSpinner, new Property<Double>( runRange.get().getMin() ), runRange, interactiveFont, FORMAT );
            double maxRunWidth = Math.max( maxRunNode.getFullBoundsReference().getWidth(), minRunNode.getFullBoundsReference().getWidth() );
            maxSlopeWidth = Math.max( maxRiseWidth, maxRunWidth );
        }

        // (y-y1) = m(x-x1)
        PText yNode = new PhetPText( "y", staticFont, staticColor );
        final PText y1SignNode = new PhetPText( "-", staticFont, staticColor );
        PNode y1Node = new ZeroOffsetNode( new Y1SpinnerNode2( UserComponents.y1Spinner, this.y1, y1Range, interactiveFont, FORMAT ) );
        PText equalsNode = new PhetPText( "=", staticFont, staticColor );
        PNode riseNode = new ZeroOffsetNode( new RiseSpinnerNode2( UserComponents.riseSpinner, this.rise, riseRange, interactiveFont, FORMAT ) );
        PNode runNode = new ZeroOffsetNode( new RunSpinnerNode2( UserComponents.runSpinner, this.run, runRange, interactiveFont, FORMAT ) );
        final PPath lineNode = new PPath( new Line2D.Double( 0, 0, maxSlopeWidth, 0 ) ) {{
            setStroke( new BasicStroke( 3f ) );
            setStrokePaint( staticColor );
        }};
        PText xNode = new PhetPText( "x", staticFont, staticColor );
        final PText x1SignNode = new PhetPText( "-", staticFont, staticColor );
        PNode x1Node = new ZeroOffsetNode( new X1SpinnerNode2( UserComponents.x1Spinner, this.x1, x1Range, interactiveFont, FORMAT ) );
        PText yLeftParenNode = new PhetPText( "(", staticFont, staticColor );
        PText yRightParenNode = new PhetPText( ")", staticFont, staticColor );
        PText xLeftParenNode = new PhetPText( "(", staticFont, staticColor );
        PText xRightParenNode = new PhetPText( ")", staticFont, staticColor );

        // rendering order
        {
            addChild( yLeftParenNode );
            addChild( yNode );
            addChild( y1SignNode );
            addChild( y1Node );
            addChild( yRightParenNode );
            addChild( equalsNode );
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xLeftParenNode );
            addChild( xNode );
            addChild( x1SignNode );
            addChild( x1Node );
            addChild( xRightParenNode );
        }

        // layout
        {
            // NOTE: x spacing varies and was tweaked to look good
            final double ySpacing = 6;
            yLeftParenNode.setOffset( 0, 0 );
            yNode.setOffset( yLeftParenNode.getFullBoundsReference().getMaxX() + 2,
                             yLeftParenNode.getYOffset() );
            y1SignNode.setOffset( yNode.getFullBoundsReference().getMaxX() + 5,
                                  yNode.getYOffset() );
            y1Node.setOffset( y1SignNode.getFullBoundsReference().getMaxX() + 5,
                                  yNode.getFullBoundsReference().getCenterY() - ( y1Node.getFullBoundsReference().getHeight() / 2 ) );
            yRightParenNode.setOffset( y1Node.getFullBoundsReference().getMaxX() + 2,
                                       yNode.getYOffset() );
            equalsNode.setOffset( yRightParenNode.getFullBoundsReference().getMaxX() + 10,
                                  yNode.getYOffset() );
            lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + 10,
                                equalsNode.getFullBoundsReference().getCenterY() + 2 );
            riseNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - riseNode.getFullBoundsReference().getWidth(),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            runNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - runNode.getFullBoundsReference().getWidth(),
                               lineNode.getFullBoundsReference().getMinY() + ySpacing );
            xLeftParenNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + 2,
                                      yNode.getYOffset() );
            xNode.setOffset( xLeftParenNode.getFullBoundsReference().getMaxX() + 2,
                             yNode.getYOffset() );
            x1SignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + 10,
                                  xNode.getYOffset() );
            x1Node.setOffset( x1SignNode.getFullBoundsReference().getMaxX() + 5,
                              xNode.getFullBoundsReference().getCenterY() - ( x1Node.getFullBoundsReference().getHeight() / 2 ) );
            xRightParenNode.setOffset( x1Node.getFullBoundsReference().getMaxX() + 2,
                                      yNode.getYOffset() );
        }

        // sync the model with the controls
        RichSimpleObserver lineUpdater = new RichSimpleObserver() {
            @Override public void update() {
                interactiveLine.set( new StraightLine( rise.get(), run.get(), x1.get(), y1.get(), interactiveLine.get().color, interactiveLine.get().highlightColor ) );
            }
        };
        lineUpdater.observe( rise, run, x1, y1 );

        // sync the controls with the model
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {
                rise.set( line.rise );
                run.set( line.run );
                x1.set( line.x1 );
                y1.set( line.y1 );
            }
        } );
    }

    // test
    public static void main( String[] args ) {

        // model
        PointSlopeModel model = new PointSlopeModel();

        // equation
        PointSlopeEquationNodeSpinners equationNode =
                new PointSlopeEquationNodeSpinners( model.interactiveLine, model.riseRange, model.runRange, model.x1Range, model.y1Range);
        equationNode.setOffset( 50, 100 );

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 600, 400 ) );
        canvas.getLayer().addChild( equationNode );

        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}

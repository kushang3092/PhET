// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.umd.cs.piccolo.PNode;

/**
 * View representation for messenger RNA.  This is done differently from most
 * if not all of the other mobile biomolecules because it is represented as an
 * unclosed shape.
 *
 * @author John Blanco
 */
public class MessengerRnaNode extends MobileBiomoleculeNode {

    // For debug - turn on to show the enclosing shape segments.
    private static final boolean SHOW_SHAPE_SEGMENTS = true;

    /**
     * Constructor.
     *
     * @param mvt
     * @param messengerRna
     */
    public MessengerRnaNode( final ModelViewTransform mvt, final MessengerRna messengerRna ) {
        super( mvt, messengerRna, new BasicStroke( 2 ) );

        // Add a placement hint that shows where a ribosome could be attached.
        addChild( new PlacementHintNode( mvt, messengerRna.ribosomePlacementHint ) );

        if ( SHOW_SHAPE_SEGMENTS ) {

            // Observe the mRNA and add new shape segments as they come into existence.
            messengerRna.shapeSegments.addElementAddedObserver( new VoidFunction1<MessengerRna.ShapeSegment>() {

                public void apply( final MessengerRna.ShapeSegment addedShapeSegment ) {
                    final ShapeSegmentNode shapeSegmentNode = new ShapeSegmentNode( addedShapeSegment, mvt );

                    // Watch for removal of this shape segment.  If it goes
                    // away, remove the corresponding node.
                    messengerRna.shapeSegments.addElementRemovedObserver( new VoidFunction1<MessengerRna.ShapeSegment>() {
                        public void apply( MessengerRna.ShapeSegment removedShapeSegment ) {
                            if ( removedShapeSegment == addedShapeSegment ) {
                                removeChild( shapeSegmentNode );
                            }
                        }
                    } );
                }
            } );
        }
    }

    /**
     * Class that defines a node that can be used to visualize the shapes that
     * enclose the mRNA strand.  This was created primarily for debugging, and
     * can probably be removed once the shape algorithm is worked out.
     */
    private static class ShapeSegmentNode extends PNode {

        private static final Color FILL_COLOR = new Color( 150, 150, 150, 150 );
        private static final Color STROKE_COLOR = new Color( 150, 150, 150, 150 );
        private static final Stroke STROKE = new BasicStroke( 1 );

        private ShapeSegmentNode( final MessengerRna.ShapeSegment shapeSegment, final ModelViewTransform mvt ) {

            // Create the node that represents the segment.
            final PhetPPath shapeSegmentNode = new PhetPPath( FILL_COLOR, STROKE, STROKE_COLOR );
            addChild( shapeSegmentNode );

            // Set the initial shape and watch for changes.
            shapeSegment.bounds.addObserver( new VoidFunction1<Rectangle2D>() {
                Shape shape;

                public void apply( Rectangle2D bounds ) {
                    // TODO: This assumes I can't create a PPath with zero height.  Verify true and if not, if clause may be removed.
                    if ( bounds.getHeight() == 0 ) {
                        // This is a horizontal segment, so just make it a line.
                        DoubleGeneralPath path = new DoubleGeneralPath( bounds.getMinX(), bounds.getMinY() );
                        path.lineTo( bounds.getMaxX(), bounds.getMinY() );
                        shape = path.getGeneralPath();
                    }
                    else {
                        // This is a diagonal segment, so make it a rect.
                        shape = getBounds();
                    }
                    // Update the shape.
                    shapeSegmentNode.setPathTo( mvt.modelToView( shape ) );
                }
            } );
        }
    }

    // TODO: Started on this, decided not to use it for now, delete if never used.  It is intended for debugging the mRNA shape.
    private static class PointMassNode {
        private static final int DIAMETER = 5;
        private static final Shape SHAPE = new Ellipse2D.Double( -DIAMETER / 2, -DIAMETER / 2, DIAMETER, DIAMETER );
        private static final Color COLOR = Color.RED;
        public final MessengerRna.PointMass pointMass;
        private final ModelViewTransform mvt;
        private final PNode representation;

        public PointMassNode( MessengerRna.PointMass pointMass, ModelViewTransform mvt ) {
            this.pointMass = pointMass;
            this.mvt = mvt;
            representation = new PhetPPath( SHAPE, COLOR );
            updatePosition();
        }

        public void updatePosition() {
            representation.setOffset( mvt.modelToView( pointMass.getPosition() ) );
        }
    }


}

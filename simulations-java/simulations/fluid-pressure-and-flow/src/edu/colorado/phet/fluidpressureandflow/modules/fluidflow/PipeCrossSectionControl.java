// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.model.CrossSection;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PipeCrossSectionControl extends PNode {
    private double DISTANCE_THRESHOLD = 0.5;

    public PipeCrossSectionControl( final ModelViewTransform transform, final CrossSection pipePosition ) {
        final PipeControlPoint top = new PipeControlPoint( pipePosition.getTopProperty(), true );
        final PipeControlPoint bottom = new PipeControlPoint( pipePosition.getBottomProperty(), false );

        Function1<Point2D, Point2D> bottomConstraint = new Function1<Point2D, Point2D>() {
            public Point2D apply( Point2D bottomLocation ) {
                final boolean tooClose = bottomLocation.getY() > top.getPoint().getY() - DISTANCE_THRESHOLD;
                double bottomY = tooClose ?
                                 top.getPoint().getY() - DISTANCE_THRESHOLD :
                                 bottomLocation.getY();
                return new Point2D.Double( bottom.getPoint().getX(), bottomY );
            }
        };

        Function1<Point2D, Point2D> topConstraint = new Function1<Point2D, Point2D>() {
            public Point2D apply( Point2D topLocation ) {
                final boolean tooClose = topLocation.getY() < bottom.getPoint().getY() + DISTANCE_THRESHOLD;
                double topY = tooClose ?
                              bottom.getPoint().getY() + DISTANCE_THRESHOLD :
                              topLocation.getY();
                return new Point2D.Double( top.getPoint().getX(), topY );
            }
        };

        addChild( new PipeBackNode.GrabHandle( transform, bottom, bottomConstraint ) );
        addChild( new PipeBackNode.GrabHandle( transform, top, topConstraint ) );
    }
}

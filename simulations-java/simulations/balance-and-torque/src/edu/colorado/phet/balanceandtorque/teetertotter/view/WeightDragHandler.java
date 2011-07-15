// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import edu.colorado.phet.balanceandtorque.teetertotter.model.weights.Mass;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author John Blanco
 */
public class WeightDragHandler extends PDragEventHandler {
    private final Mass weight;
    private final PNode pNode;
    private final ModelViewTransform mvt;

    public WeightDragHandler( Mass weight, PNode node, ModelViewTransform mvt ) {
        this.weight = weight;
        pNode = node;
        this.mvt = mvt;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        // The user is moving this, so they have control.
        weight.userControlled.set( true );
    }

    @Override
    public void mouseDragged( PInputEvent event ) {
        PDimension viewDelta = event.getDeltaRelativeTo( pNode.getParent() );
        ImmutableVector2D modelDelta = mvt.viewToModelDelta( new ImmutableVector2D( viewDelta ) );
        weight.translate( modelDelta );
    }

    @Override protected void endDrag( PInputEvent event ) {
        super.endDrag( event );
        // The user is no longer moving this, so they have relinquished control.
        weight.userControlled.set( false );
    }
}

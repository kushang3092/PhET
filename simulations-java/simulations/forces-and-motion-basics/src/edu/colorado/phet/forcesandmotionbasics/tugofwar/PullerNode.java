// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PSize;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class PullerNode extends PNode {
    private final Vector2D initialOffset;

    public PullerNode( PColor color, PSize size, int item, final double scale, Vector2D offset, PullerContext context ) {
        BufferedImage image = ForcesAndMotionBasicsResources.RESOURCES.getImage( "pull_figure_" + sizeText( size ) + color.name() + "_" + item + ".png" );
        addChild( new PImage( image ) );
        setScale( scale );
        setOffset( offset.x, offset.y );
        initialOffset = offset;

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( final PInputEvent event ) {
                super.mouseDragged( event );
                final PDimension delta = event.getDeltaRelativeTo( PullerNode.this.getParent() );
                PullerNode.this.translate( delta.width / PullerNode.this.getScale(), delta.height / PullerNode.this.getScale() );
//                System.out.println( event.getPickedNode().getOffset() );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                System.out.println( "PullerNode.mouseReleased" );
                System.out.println( "getOffset() = " + getOffset() + ", init = " + initialOffset );
                PullerNode.this.animateToPositionScaleRotation( initialOffset.x, initialOffset.y, scale, 0, TugOfWarCanvas.ANIMATION_DURATION );
            }
        } );
    }

    private static String sizeText( final PSize size ) {
        return size == PSize.LARGE ? "lrg_" :
               size == PSize.SMALL ? "small_" :
               "";
    }

}
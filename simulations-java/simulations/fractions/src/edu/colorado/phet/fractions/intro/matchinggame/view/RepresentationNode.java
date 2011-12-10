// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.CanvasBoundedDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class RepresentationNode extends PNode {

    public final Fraction fraction;
    public final Property<ImmutableVector2D> velocity = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    public final double mass = 1;
    public final ImmutableVector2D force = new ImmutableVector2D( 0, 9.8 * mass * 300 );
    public final BooleanProperty dropped = new BooleanProperty( false );
    public final BooleanProperty dragging = new BooleanProperty( false ) {{
        addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean dragging ) {
                if ( dragging ) {
                    setOverPlatform( null );
                }
            }
        } );
    }};
    private PNode platform;
    public final BooleanProperty scored = new BooleanProperty( false );
    private long timeArrivedOnPlatform = -1;

    public RepresentationNode( final ModelViewTransform transform, Fraction fraction ) {
        this.fraction = fraction;
//        representation.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
//            public void apply( ImmutableVector2D immutableVector2D ) {
//                setOffset( transform.modelToView( immutableVector2D ).toPoint2D() );
//            }
//        } );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new CanvasBoundedDragHandler( this ) {
            @Override protected void dragNode( DragEvent event ) {
                setOffset( new ImmutableVector2D( getOffset() ).plus( event.delta ) );
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                dragging.set( true );
                moveToFront();
            }

            @Override public void mouseReleased( PInputEvent event ) {
                dropped.set( true );
                dragging.set( false );
            }
        } );
    }

    public void setOffset( ImmutableVector2D immutableVector2D ) {
        setOffset( immutableVector2D.getX(), immutableVector2D.getY() );
    }

    public void setOverPlatform( PNode platform ) {
        this.platform = platform;
        if ( platform != null ) {
            timeArrivedOnPlatform = System.currentTimeMillis();
        }
        else {
            timeArrivedOnPlatform = -1;
            dropped.set( false );
            dragging.set( false );
        }
    }

    public PNode getOverPlatform() {
        return platform;
    }

    public double getWeight() {
        return fraction.getValue();
    }

    public void solved() {
        scored.set( true );
        setOverPlatform( null );
        dragging.set( false );
    }

    public long getTimeArrivedOnPlatform() {
        return timeArrivedOnPlatform;
    }
}
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Slider node that controls the amount of friction on the track(s) upon which
 * the skater is moving.
 *
 * @author John Blanco
 */
public class TrackFrictionSlider extends PNode {

    private static final double FRICTION_MAX = 0.01;

    private double savedFrictionValue = FRICTION_MAX / 2;

    public TrackFrictionSlider( final EnergySkateParkBasicsModule module ) {

        final Property<Double> frictionAmount = module.frictionAmount;

        // Create a property and hook it up to the module.
        frictionAmount.addObserver( new VoidFunction1<Double>() {
            public void apply( Double friction ) {
                if ( module.frictionEnabled.get() ) {
                    module.setCoefficientOfFriction( friction );
                }
            }
        } );

        // Create and add the slider node.
        final HSliderNode sliderNode = new HSliderNode( 0, FRICTION_MAX, 90, 5, frictionAmount, module.frictionEnabled ) {
            @Override protected Paint getTrackFillPaint( Rectangle2D trackRect ) {
                // Override the gradient and fill with white.  The gradient
                // just looked weird.
                return Color.WHITE;
            }
        };
        sliderNode.addLabel( 0, new PText( EnergySkateParkResources.getString( "controls.gravity.none" ) ) );
        sliderNode.addLabel( FRICTION_MAX, new PText( EnergySkateParkResources.getString( "controls.gravity.lots" ) ) );
        addChild( sliderNode );

        // Observe the property that indicates whether friction is enabled so
        // that the friction level can be set to zero when friction is disabled.
        module.frictionEnabled.addObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean isEnabled, Boolean wasEnabled ) {
                if ( isEnabled && !wasEnabled ) {
                    frictionAmount.set( savedFrictionValue );
                }
                else if ( wasEnabled && !isEnabled ) {
                    savedFrictionValue = frictionAmount.get();
                    frictionAmount.set( 0.0 );
                }
            }
        } );
    }
}

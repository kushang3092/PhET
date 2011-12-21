// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.control;

import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Evaporation control, appropriated from sugar-and-salt-solutions.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EvaporationControlNode extends ControlPanelNode {

    public EvaporationControlNode( final Property<Double> evaporationRate, final DoubleRange range ) {
        super( new HBox(

                // Label
                new PText( Strings.EVAPORATION ) {{
                    setFont( new PhetFont( 18 ) );
                }},

                // Slider
                new HSliderNode( range.getMin(), range.getMax(), evaporationRate ) {{

                    // Tick labels
                    addLabel( range.getMin(), new PhetPText( Strings.NONE, new PhetFont( 14 ) ) );
                    addLabel( range.getMax(), new PhetPText( Strings.LOTS, new PhetFont( 14 ) ) );

                    // Set rate to zero when slider is released.
                    this.addInputEventListener( new PBasicInputEventHandler() {
                        @Override public void mouseReleased( PInputEvent event ) {
                            evaporationRate.set( 0.0 );
                        }
                    } );
                }}
        ) );
    }
}

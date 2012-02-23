// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Convenience class for a horizontal slider with labels at ends instead of
 * having tick marks with labels below them.
 *
 * @author John Blanco
 */
class HorizontalSliderWithLabelsAtEnds extends PNode {
    private static final double OVERALL_WIDTH = 150;
    private static final Font LABEL_FONT = new PhetFont( 12 );
    private static final double INTER_ELEMENT_SPACING = 5;

    HorizontalSliderWithLabelsAtEnds( UserComponent userComponent, String leftLabel, String rightLabel ) {
        PText leftLabelNode = new PText( leftLabel ) {{
            setFont( LABEL_FONT );
        }};
        PText rightLabelNode = new PText( rightLabel ) {{
            setFont( LABEL_FONT );
        }};
        double sliderWidth = OVERALL_WIDTH - leftLabelNode.getFullBoundsReference().width -
                             rightLabelNode.getFullBoundsReference().width - ( 2 * INTER_ELEMENT_SPACING );
        PNode sliderNode = new HSliderNode( userComponent, 0, 1, sliderWidth, 5, new Property<Double>( 0.0 ), new BooleanProperty( true ) );
        addChild( new HBox( INTER_ELEMENT_SPACING, leftLabelNode, sliderNode, rightLabelNode ) );
    }
}

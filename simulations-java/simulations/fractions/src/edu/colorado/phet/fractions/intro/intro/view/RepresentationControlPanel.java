// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.intro.intro.view.FractionsIntroCanvas.CONTROL_FONT;

/**
 * @author Sam Reid
 */
public class RepresentationControlPanel extends ControlPanelNode {
    public RepresentationControlPanel( Property<ChosenRepresentation> chosenRepresentation ) {
        super( new RepresentationControlPanelContentNode( chosenRepresentation ) );
    }

    private static class RepresentationControlPanelContentNode extends PNode {
        private RepresentationControlPanelContentNode( final Property<ChosenRepresentation> c ) {
            final PhetPText title = new PhetPText( "Representation", CONTROL_FONT );
            addChild( title );
            final RepIcon[] elements = new RepIcon[] { new HorizontalBarElement( c ), new VerticalBarElement( c ), new PieElement( c ), new SquareElement( c ), new NumberLineElement( c ) };

            double maxWidth = 0;
            double maxHeight = 0;

            for ( RepIcon repIcon : elements ) {
                PNode pNode = repIcon.getNode();
                if ( pNode.getFullBounds().getWidth() > maxWidth ) {
                    maxWidth = pNode.getFullBounds().getWidth();
                }
                if ( pNode.getFullBounds().getHeight() > maxHeight ) {
                    maxHeight = pNode.getFullBounds().getHeight();
                }
            }

            final double finalMaxHeight = maxHeight;
            final double finalMaxWidth = maxWidth;
            final HBox representationLayer = new HBox( 10 ) {{
                for ( final RepIcon element : elements ) {
                    PNode highlighter = new PhetPPath( new Rectangle2D.Double( -2, -2, finalMaxWidth + 4, finalMaxHeight + 4 ), null ) {{

                        PNode node = new ZeroOffsetNode( element.getNode() );

                        addChild( node );
                        node.setOffset( finalMaxWidth / 2 - node.getFullBounds().getWidth() / 2, finalMaxHeight / 2 - node.getFullBounds().getHeight() / 2 );

                        c.valueEquals( element.getRepresentation() ).addObserver( new VoidFunction1<Boolean>() {
                            public void apply( Boolean aBoolean ) {
                                setPaint( aBoolean ? Color.yellow : null );
                            }
                        } );
                    }};
                    addChild( highlighter );
                }
                setOffset( 30, title.getFullBounds().getMaxY() );
            }};
            addChild( representationLayer );
        }
    }
}
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.lightreflectionandrefraction.view.LightReflectionAndRefractionCanvas;
import edu.colorado.phet.lightreflectionandrefraction.view.MediumControlPanel;

/**
 * @author Sam Reid
 */
public class PrismsCanvas extends LightReflectionAndRefractionCanvas<PrismsModel> {
    public PrismsCanvas( final PrismsModel model ) {
        super( model, new Function1.Identity<Double>(), new Function1.Constant<Double, Boolean>( true ), new Function1.Constant<Double, Boolean>( true ) );
        for ( Prism prism : model.getPrisms() ) {
            addChild( new PrismNode( transform, prism, model.prismMedium ) );
        }

        model.outerMedium.addObserver( new SimpleObserver() {
            public void update() {
                setBackground( model.colorMappingFunction.getValue().apply( model.outerMedium.getValue().getIndexOfRefraction() ) );
            }
        } );

        addChild( new ControlPanelNode( new MediumControlPanel( this, model.outerMedium, model.colorMappingFunction ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, 10 );
        }} );

        final ControlPanelNode prismToolbox = new ControlPanelNode( new PrismToolboxNode( this, transform, model ) ) {{
            setOffset( 10, stageSize.height - getFullBounds().getHeight() - 10 );
        }};
        addChild( prismToolbox );

        final ControlPanelNode prismMediumControlPanel = new ControlPanelNode( new MediumControlPanel( this, model.prismMedium, model.colorMappingFunction ) ) {{
            setOffset( prismToolbox.getFullBounds().getMaxX() + 10, stageSize.height - getFullBounds().getHeight() - 10 );
        }};
        addChild( prismMediumControlPanel );

        final LaserControlPanelNode laserControlPanelNode = new LaserControlPanelNode( model.manyRays, laserView, model.getLaser().color, model.showReflections, showNormal ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, prismMediumControlPanel.getFullBounds().getMinY() - 10 - getFullBounds().getHeight() );
        }};
        addChild( laserControlPanelNode );

        showNormal.addObserver( new SimpleObserver() {
            public void update() {
                model.updateModel();//could do this completely in the view, but simpler just to have the model recompute everything
            }
        } );
        model.addIntersectionListener( new VoidFunction1<Intersection>() {
            public void apply( Intersection intersection ) {
                if ( showNormal.getValue() ) {
                    final IntersectionNode node = new IntersectionNode( transform, intersection );
                    intersection.addCleanupListener( new VoidFunction0() {
                        public void apply() {
                            removeChild( node );
                        }
                    } );
                    addChild( node );
                }
            }
        } );
    }
}

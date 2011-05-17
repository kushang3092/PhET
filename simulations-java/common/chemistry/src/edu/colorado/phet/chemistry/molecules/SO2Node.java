// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.molecules;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

import static edu.colorado.phet.chemistry.model.Atom.O;
import static edu.colorado.phet.chemistry.model.Atom.S;

/**
 * SO2 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SO2Node extends PComposite {

    public SO2Node() {

        // atom nodes
        AtomNode centerNode = new AtomNode( S );
        AtomNode leftNode = new AtomNode( O );
        AtomNode rightNode = new AtomNode( O );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( leftNode );
        parentNode.addChild( centerNode );
        parentNode.addChild( rightNode );

        // layout
        double x = 0;
        double y = 0;
        centerNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMinX();
        y = centerNode.getYOffset() + ( 0.25 * leftNode.getFullBoundsReference().getHeight() );
        leftNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMaxX();
        y = centerNode.getYOffset() + ( 0.25 * rightNode.getFullBoundsReference().getHeight() );
        rightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}

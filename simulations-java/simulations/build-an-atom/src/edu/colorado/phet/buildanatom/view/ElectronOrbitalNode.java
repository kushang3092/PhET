package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that represents an electron shell, aka "orbit", in the view.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class ElectronOrbitalNode extends PNode {

    // Stroke for drawing the electron orbits.
    private static final Stroke ELECTRON_SHELL_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_BEVEL, 0, new float[] { 3, 3 }, 0 );

    // Paint for the electron orbitals.
    private static final Paint ELECTRON_SHELL_STROKE_PAINT = new Color( 0, 0, 255, 100 );

    /**
     * Constructor.
     */
    public ElectronOrbitalNode( final ModelViewTransform2D mvt, final BooleanProperty viewOrbitals, final Atom atom, final ElectronShell electronShell, final boolean allowsUserInput ) {

        final Shape electronShellShape = mvt.createTransformedShape( new Ellipse2D.Double(
                -electronShell.getRadius(),
                -electronShell.getRadius(),
                electronShell.getRadius() * 2,
                electronShell.getRadius() * 2 ) );

        // Create and add the node that will depict the shell as a circular
        // orbit.
        final PNode electronOrbitNode = new PhetPPath( electronShellShape, ELECTRON_SHELL_STROKE, ELECTRON_SHELL_STROKE_PAINT ) { {
                setOffset( atom.getPosition() );
                final SimpleObserver updateVisibility = new SimpleObserver() {
                    public void update() {
                        setVisible( viewOrbitals.getValue() );
                    }
                };
                viewOrbitals.addObserver( updateVisibility );
            } };
        addChild( electronOrbitNode );
    }
}

/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.hydrogenatom.util.RoundGradientPaint;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class ElectronNode extends PhetPNode {

    private static final double DIAMETER = 8;
    private static final Color COLOR = new Color( 70, 70, 255 );
    private static final Color HILITE_COLOR = new Color( 140, 140, 255 );
    private static final Paint ROUND_GRADIENT = new RoundGradientPaint( 0, DIAMETER/6, HILITE_COLOR, new Point2D.Double( DIAMETER/4, DIAMETER/4 ), COLOR );
    
    private PPath _pathNode;
    
    public ElectronNode() {
        super();

        Shape shape = new Ellipse2D.Double( -DIAMETER/2, -DIAMETER/2, DIAMETER, DIAMETER );
        PPath pathNode = new PPath( shape );
        pathNode.setPaint( ROUND_GRADIENT );
        pathNode.setStroke( null );   
        
        addChild( pathNode );
    }
    
    public void setStroke( Stroke stroke, Color color ) {
        _pathNode.setStroke( stroke );
        _pathNode.setStrokePaint( color );
    }
}

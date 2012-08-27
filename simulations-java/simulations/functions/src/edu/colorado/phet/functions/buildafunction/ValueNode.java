package edu.colorado.phet.functions.buildafunction;

import fj.F;
import fj.data.List;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.functions.intro.ShapeValue;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class ValueNode extends PNode {

    private PNode node;
    private Area a;
    private PhetPPath boundNode;
    private Object originalValue;

    private ArrayList<F<Object, Object>> functions = new ArrayList<F<Object, Object>>();
    private final Color textPaint;

    public ValueNode( final ValueContext valueContext, Object originalValue, Stroke stroke, Color paint, Color strokePaint, Color textPaint ) {
        this.originalValue = originalValue;
        this.textPaint = textPaint;
        final double ellipseWidth = Constants.ellipseWidth;
        a = new Area( new Rectangle2D.Double( 0, 0, 50, ellipseWidth ) );
        if ( originalValue instanceof ShapeValue ) {
            a.add( roundedLeftSide( ellipseWidth, a ) );
            a.add( roundedRightSide( ellipseWidth, a ) );
        }
        else if ( originalValue instanceof String ) {
            a.add( angledLeftSide( ellipseWidth, a ) );
            a.add( angledRightSide( ellipseWidth, a ) );
        }
        boundNode = new PhetPPath( a, paint, stroke, strokePaint );
        addChild( boundNode );
        this.node = toNode( getCurrentValue() );
        centerContent();
        addChild( node );

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
            }

            @Override public void mouseDragged( final PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( ValueNode.this.getParent() );
                valueContext.mouseDragged( ValueNode.this, delta );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                valueContext.mouseReleased( ValueNode.this );
            }
        } );
        addInputEventListener( new CursorHandler() );
    }

    public static Area roundedRightSide( final double ellipseWidth, Shape a ) {
        return new Area( new Ellipse2D.Double( a.getBounds2D().getMinX() - ellipseWidth / 2, a.getBounds2D().getCenterY() - ellipseWidth / 2, ellipseWidth, ellipseWidth ) );
    }

    public static Area roundedLeftSide( final double ellipseWidth, Shape a ) {
        return new Area( new Ellipse2D.Double( a.getBounds2D().getMaxX() - ellipseWidth / 2, a.getBounds2D().getCenterY() - ellipseWidth / 2, ellipseWidth, ellipseWidth ) );
    }

    public static Area angledRightSide( final double ellipseWidth, Shape a ) {
        return new Area( ShapeUtils.createShapeFromPoints( List.list( Vector2D.v( a.getBounds2D().getMaxX(), a.getBounds2D().getMinY() ),
                                                                      Vector2D.v( a.getBounds2D().getMaxX() + ellipseWidth / 2, a.getBounds2D().getCenterY() ),
                                                                      Vector2D.v( a.getBounds2D().getMaxX(), a.getBounds2D().getMaxY() ) ) ) );
    }

    public static Area angledLeftSide( final double ellipseWidth, Shape a ) {
        return new Area( ShapeUtils.createShapeFromPoints( List.list( Vector2D.v( a.getBounds2D().getMinX(), a.getBounds2D().getMinY() ),
                                                                      Vector2D.v( a.getBounds2D().getMinX() - ellipseWidth / 2, a.getBounds2D().getCenterY() ),
                                                                      Vector2D.v( a.getBounds2D().getMinX(), a.getBounds2D().getMaxY() ) ) ) );
    }

    public Object getCurrentValue() {
        Object x = originalValue;
        for ( F<Object, Object> function : functions ) {
            x = function.f( x );
        }
        return x;
    }

    private static PhetPText toTextNode( String text, final Color textPaint ) {return new PhetPText( text, new PhetFont( 42, true ) ) {{setTextPaint( textPaint );}};}

    public void centerContent() {
        this.node.setOffset( a.getBounds2D().getCenterX() - node.getFullBounds().getWidth() / 2, a.getBounds2D().getCenterY() - node.getFullBounds().getHeight() / 2 );
    }

    public void setStrokePaint( final Color color ) { boundNode.setStrokePaint( color ); }

    public void applyFunction( F<Object, Object> f ) {
        System.out.println( getCurrentValue() );
        functions.add( f );
        System.out.println( getCurrentValue() );
        removeChild( node );
        node = toNode( getCurrentValue() );
        addChild( node );
        centerContent();
    }

    private PNode toNode( final Object currentValue ) {
        if ( currentValue instanceof ShapeValue ) {
            return ( (ShapeValue) currentValue ).toNode();
        }
        else {
            final PhetPText textNode = toTextNode( currentValue.toString(), textPaint );
            if ( textNode.getFullWidth() > Constants.ellipseWidth ) {
                textNode.scale( Constants.ellipseWidth / textNode.getFullWidth() );
            }
            return textNode;
        }
    }

    public int getNumberRotations() {
        if ( getCurrentValue() instanceof ShapeValue ) {
            return ( (ShapeValue) getCurrentValue() ).numRotations;
        }
        else { return 0; }
    }
}
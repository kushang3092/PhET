/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.util.Vector2D;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


public class TrapForceNode extends PComposite implements Observer {

    // properties of the vectors
    private static final double VECTOR_HEAD_HEIGHT = 20;
    private static final double VECTOR_HEAD_WIDTH = 20;
    private static final double VECTOR_TAIL_WIDTH = 5;
    private static final double VECTOR_MIN_TAIL_LENGTH = 2;
    private static final double VECTOR_MAX_TAIL_LENGTH = 125;
    private static final Stroke VECTOR_STROKE = new BasicStroke( 1f );
    private static final Paint VECTOR_STROKE_PAINT = Color.BLACK;
    private static final Paint VECTOR_FILL_PAINT = Color.GREEN;
    
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( "0.##E0" );
    
    private Laser _laser;
    private Bead _bead;
    private ModelViewTransform _modelViewTransform;
    private PPath _xComponentNode, _yComponentNode;
    private PText _xTextNode, _yTextNode;
    private double _fMax;
    
    public TrapForceNode( Laser laser, Bead bead, ModelViewTransform modelViewTransform ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _bead = bead;
        _bead.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _xComponentNode = new PPath();
        _xComponentNode.setStroke( VECTOR_STROKE );
        _xComponentNode.setStrokePaint( VECTOR_STROKE_PAINT );
        _xComponentNode.setPaint( VECTOR_FILL_PAINT );
        
        _yComponentNode = new PPath();
        _yComponentNode.setStroke( VECTOR_STROKE );
        _yComponentNode.setStrokePaint( VECTOR_STROKE_PAINT );
        _yComponentNode.setPaint( VECTOR_FILL_PAINT );
        
        _xTextNode = new PText();
        _xTextNode.setTextPaint( Color.BLACK );
        
        _yTextNode = new PText();
        _yTextNode.setTextPaint( Color.BLACK );
        
        double x = _laser.getX() + ( _laser.getDiameterAtWaist() / 4 ); // halfway between center and edge of waist
        double y = _laser.getY();
        double maxPower = _laser.getPowerRange().getMax();
        Vector2D fMax = _laser.getTrapForce( x, y, maxPower );
        _fMax = Math.max( Math.abs( fMax.getX() ), Math.abs( fMax.getY() ) );
//        System.out.println( "TrapForceNode.init fMax=" + fMax );//XXX
        
        updatePosition();
        updateVectors();
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
        _bead.deleteObserver( this );
    }
    
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POSITION || arg == Laser.PROPERTY_POWER || arg == Laser.PROPERTY_RUNNING ) {
                updateVectors();
            }
        }
        else if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_POSITION ) {
                updatePosition();
                updateVectors();
            }
        }
    }
    
    private void updatePosition() {
        Point2D position = _modelViewTransform.modelToView( _bead.getPositionRef() );
        setOffset( position.getX(), position.getY() );
    }
    
    private void updateVectors() {
        
        removeAllChildren();

        if ( _laser.isRunning() ) {
            
            Point2D beadPosition = _bead.getPositionRef();
            Vector2D f = _laser.getTrapForce( beadPosition );
            double fx = f.getX();
            double fy = f.getY();
//            System.out.println( "TrapForceNode.updateVectors beadPosition=" + beadPosition + " laserPosition=" + _laser.getPositionRef() + " f=" + f );//XXX
            assert ( Math.abs( fx ) <= _fMax );
            assert ( Math.abs( fy ) <= _fMax );

            if ( fx != 0 ) {
                double length = ( fx / _fMax ) * ( VECTOR_MAX_TAIL_LENGTH - VECTOR_MIN_TAIL_LENGTH );
                if ( length > 0 ) {
                    length = length + VECTOR_HEAD_HEIGHT + VECTOR_MIN_TAIL_LENGTH;
                }
                else {
                    length = length - VECTOR_HEAD_HEIGHT - VECTOR_MIN_TAIL_LENGTH;
                }
                Point2D tail = new Point2D.Double( 0, 0 );
                Point2D tip = new Point2D.Double( length, 0 );
                Arrow arrow = new Arrow( tail, tip, VECTOR_HEAD_HEIGHT, VECTOR_HEAD_WIDTH, VECTOR_TAIL_WIDTH );
                _xComponentNode.setPathTo( arrow.getShape() );
                addChild( _xComponentNode );
                
                _xTextNode.setText( VALUE_FORMAT.format( fx ) );
                addChild( _xTextNode );
                if ( fx > 0 ) {
                    _xTextNode.setOffset( _xComponentNode.getFullBounds().getMaxX() + 2, -_yTextNode.getFullBounds().getHeight() / 2 );
                }
                else {
                    _xTextNode.setOffset( _xComponentNode.getFullBounds().getX() - 2 - _xTextNode.getFullBounds().getWidth(), -_yTextNode.getFullBounds().getHeight() / 2 );
                }
            }

            if ( fy != 0 ) {
                double length = ( fy / _fMax ) * ( VECTOR_MAX_TAIL_LENGTH - VECTOR_MIN_TAIL_LENGTH );
                if ( length > 0 ) {
                    length = length + VECTOR_HEAD_HEIGHT + VECTOR_MIN_TAIL_LENGTH;
                }
                else {
                    length = length - VECTOR_HEAD_HEIGHT - VECTOR_MIN_TAIL_LENGTH;
                }
                Point2D tail = new Point2D.Double( 0, 0 );
                Point2D tip = new Point2D.Double( 0, length );
                Arrow arrow = new Arrow( tail, tip, VECTOR_HEAD_HEIGHT, VECTOR_HEAD_WIDTH, VECTOR_TAIL_WIDTH );
                _yComponentNode.setPathTo( arrow.getShape() );
                addChild( _yComponentNode );
                
                _yTextNode.setText( VALUE_FORMAT.format( fy ) );
                addChild( _yTextNode );
                if ( fy > 0 ) {
                    _yTextNode.setOffset( -_yTextNode.getFullBounds().getWidth() / 2, _yComponentNode.getFullBounds().getMaxY() + 2 );
                }
                else {
                    _yTextNode.setOffset( -_yTextNode.getFullBounds().getWidth() / 2, _yComponentNode.getFullBounds().getY() - 2 - _yTextNode.getFullBounds().getHeight() );
                }
            }
        }
    }
}

/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;


/**
 * DipoleMagnet is an abstract magnet model based on a pair of dipoles.
 * The magnetic field is modeled as a pair of dipoles.
 * One dipole is located at each pole of the magnet.
 * This model is applicable to magnets that are cylindrical in shape.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class DipoleMagnet extends AbstractMagnet {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
 
    /*
     * Arbitrary positive "fudge factor".
     * This should be adjusted so that transitions between inside and outside
     * the magnet don't result in abrupt changes in the magnetic field.
     */
    private static final double FUDGE_FACTOR = 700.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AffineTransform _transform;
    private Rectangle _bounds;
    private Point2D _northPoint, _southPoint;
    private Point2D _normalizedPoint;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DipoleMagnet() {
        super();
        _transform = new AffineTransform();
        _bounds = new Rectangle();
        _northPoint = new Point2D.Double();
        _southPoint = new Point2D.Double();
        _normalizedPoint = new Point2D.Double();
    }
    
    //----------------------------------------------------------------------------
    // AbstractMagnet implementation
    //----------------------------------------------------------------------------
    
    /**
     * Algorithm courtesy of Michael Dubson (dubson@spot.colorado.edu).
     * <p>
     * Assumptions made by this algorithm:
     * <ul>
     * <li>the magnet's physical center is positioned at the magnet's location
     * <li>the magnet's width > height
     * </ul>
     * 
     * @see edu.colorado.phet.faraday.model.IMagnet#getStrength(java.awt.geom.Point2D)
     */
    public AbstractVector2D getStrength( Point2D p ) {
        assert( p != null );
        assert( getWidth() > getHeight() );
 
        // All of our calculations are based a magnet located at the origin,
        // with the north pole pointing down the X-axis.
        // The point we received is based on the magnet's actual location and origin.
        // So transform the point accordingly, adjusting for location and rotation of the magnet.
        double radians = -1 * getDirection();
        _transform.setToIdentity();       
        _transform.translate( -getX(), -getY() );
        _transform.rotate( radians, getX(), getY() );
        _transform.transform( p, _normalizedPoint );
        
        // Bounds that define the "inside" of the magnet.
        _bounds.setRect( -(getWidth()/2), -(getHeight()/2), getWidth(), getHeight() );
              
        // Choose the appropriate algorithm based on
        // whether the point is inside or outside the magnet.
        AbstractVector2D B = null;
        if ( _bounds.contains( _normalizedPoint ) )  {
            B = getStrengthInside( _normalizedPoint );
        }
        else
        {
            B = getStrengthOutside( _normalizedPoint );
        }
        
        // Adjust the field vector to match the magnet's direction.
        B = B.getRotatedInstance( getDirection() );

        // Clamp magnitude to magnet strength.
        double magnetStrength = super.getStrength();
        double magnitude = B.getMagnitude();
        if ( magnitude > magnetStrength ) {
            B = ImmutableVector2D.Double.parseAngleAndMagnitude( magnetStrength, B.getAngle() );
            //System.out.println( "BarMagnet.getStrengthOutside - magnitude exceeds magnet strength by " + (magnitude - magnetStrength ) ); // DEBUG
        }
        
        return B;
    }
    
    /**
     * Gets the magnetic field strength at a point inside the magnet.
     * This is constant for all points inside the magnet.
     * <p>
     * This algorithm makes the following assumptions:
     * <ul>
     * <li>the point is guaranteed to be inside the magnet
     * <li>the magnet's direction is 0.0 (north pole pointing down the positive X axis)
     * </ul>
     * 
     * @param p the point
     * @return magnetic field strength vector
     */
    private AbstractVector2D getStrengthInside( Point2D p ) {
        assert( p != null );
        return new ImmutableVector2D.Double( getStrength(), 0 );
    }
    
    /**
     * Gets the magnetic field strength at a point outside the magnet.
     * The magnitude is guaranteed to be >=0 and <= the magnet strength.
     * <p>
     * This algorithm makes the following assumptions:
     * <ul>
     * <li>the magnet's location is (0,0)
     * <li>the magnet's direction is 0.0 (north pole pointing down the positive X axis)
     * <li>the magnet's physical center is at (0,0)
     * <li>the magnet's width > height
     * <li>the point is guaranteed to be outside the magnet
     * <li>the point has been transformed so that it is relative to above magnet assumptions
     * </ul>
     * 
     * @param p the point
     * @return magnetic field strength vector
     */
    private AbstractVector2D getStrengthOutside( Point2D p ) {
        assert( p != null );
        assert( getWidth() > getHeight() );
        
        // Magnet strength.
        double magnetStrength = super.getStrength();
        
        // Dipole locations.
        _northPoint.setLocation( +getWidth()/2 - getHeight()/2, 0 ); // north dipole
        _southPoint.setLocation( -getWidth()/2 + getHeight()/2, 0 ); // south dipole
        
        // Distances.
        double rN = _northPoint.distance( p ); // north dipole to point
        double rS = _southPoint.distance( p ); // south dipole to point
        double L = _southPoint.distance( _northPoint ); // dipole to dipole
        
        // Fudge factor
        double C = FUDGE_FACTOR * magnetStrength;
        
        // North dipole field strength vector.
        double cN = +( C / Math.pow( rN, 3.0 ) ); // constant multiplier
        double xN = cN * ( p.getX() - ( L / 2 ) ); // X component
        double yN = cN * p.getY(); // Y component
        AbstractVector2D BN = new ImmutableVector2D.Double( xN, yN ); // north dipole vector
        
        // South dipole field strength vector.
        double cS = -( C / Math.pow( rS, 3.0 ) ); // constant multiplier
        double xS = cS * ( p.getX() + ( L / 2 ) ); // X component
        double yS = cS * p.getY(); // Y component
        AbstractVector2D BS = new ImmutableVector2D.Double( xS, yS ); // south dipole vector
        
        // Total field strength is the vector sum.
        AbstractVector2D BT = BN.getAddedInstance( BS );
        
        return BT;
    }
}

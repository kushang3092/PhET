/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class is used to represent an alpha particle in a model of alpha
 * radiation behavior.
 *
 * @author John Blanco
 */
public class AlphaParticle implements AtomicNucleusConstituent {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    static final double MAX_AUTO_TRANSLATE_AMT = 0.75;
    
    // The radius at which an alpha particle will tunnel out of the nucleus.
    static final double TUNNEL_OUT_RADIUS = 20.0;
    
    // The maximum radius at which a particle may tunnel.
    static final double MAX_TUNNEL_RADIUS = 20.0;
    
    // Radius at which we are considered outside the nucleus
    static final double BASIC_NUCLEUS_RADIUS = 8.0;
    
    // Hysteresis count - used to lock out changes when needed.
    static final int HYSTERESIS_VALUE = 8;
    
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    private ArrayList _listeners = new ArrayList();
    
    // Location in space of this particle.
    private Point2D.Double _position;
    
    // Values used for autonomous translation.
    private double _xAutoDelta;
    private double _yAutoDelta;
    private int _changeHysteresis = HYSTERESIS_VALUE;
    
    // Random number generator, used for creating some random behavior.
    Random _rand = new Random();
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AlphaParticle(double xPos, double yPos)
    {
        _position = new Point2D.Double(xPos, yPos);

        _xAutoDelta = MAX_AUTO_TRANSLATE_AMT *((_rand.nextDouble() * 2.0) - 1.0); 
        _yAutoDelta = MAX_AUTO_TRANSLATE_AMT * ((_rand.nextDouble() * 2.0) - 1.0); 
    }
    
    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------
    
    public Point2D getPosition()
    {
        return new Point2D.Double(_position.getX(), _position.getY());
    }
    
    //------------------------------------------------------------------------
    // Behavior methods
    //------------------------------------------------------------------------

    /**
     * Move the particle by some amount.
     */
    public void translate(double dx, double dy)
    {
        _position.x += dx;
        _position.y += dy;
        
        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(); 
        }
    }
    
    // TODO: jblanco 03-19-2008 - The most recent feedback is that the
    // particles should just tunnel and not translate.  Based on that feedback,
    // the following method is no longer used.  Remove it if this decision is
    // ratified.
    /**
     * Tell the particle to move itself based on its current velocity.
     */
    public void autoTranslate()
    {
        if (_changeHysteresis > 0)
        {
            _changeHysteresis--;
        }
        else if (Point2D.distance( _position.x, _position.y, 0, 0 ) > BASIC_NUCLEUS_RADIUS)
        {
            /*
            // Needs to be pulled back in to the center.
            if (_position.x > 0)
            {
               _xAutoDelta = -MAX_AUTO_TRANSLATE_AMT;
            }
            else
            {
                _xAutoDelta = MAX_AUTO_TRANSLATE_AMT;                
            }
            
            if (_position.y > 0)
            {
               _yAutoDelta = -MAX_AUTO_TRANSLATE_AMT;
            }
            else
            {
                _xAutoDelta = MAX_AUTO_TRANSLATE_AMT;                
            }
            */
            // Time to "bounce", meaning that we change direction more toward
            // the center of the nucleus.  This is a simple bouncing algorithm
            // intended to minimize computation.
            if (Math.abs( _position.x ) > 3 * Math.abs( _position.y ))
            {
                // Bounce only in x direction.
                _xAutoDelta = - _xAutoDelta;
            }
            else if (Math.abs( _position.y ) > 3 * Math.abs( _position.x ))
            {
                // Bounce only in y direction.
                _yAutoDelta = - _yAutoDelta;
            }
            else
            {
                // Bounce in both directions.
                _xAutoDelta = - _xAutoDelta;
                _yAutoDelta = - _yAutoDelta;
            }
            
            // Reset the hysteresis counter.
            _changeHysteresis = HYSTERESIS_VALUE;
        }
        else
        {
            // We aren't bouncing, but we aren't locked out by the hysteresis
            // counter, so decide whether to keep on with the current
            // direction, change direction, or tunnel.
            
            double changeDecider = _rand.nextDouble();
            
            if (changeDecider < 0.01)
            {
                // Tunnel to a new location.
                //tunnel(0, MAX_TUNNEL_RADIUS);
                tunnel(0, BASIC_NUCLEUS_RADIUS);
                
                // Reset the hysteresis counter.
                _changeHysteresis = HYSTERESIS_VALUE;
            }
            else if (changeDecider < 0.12)
            {
                double distanceFromOrigin = Point2D.distance( _position.x, _position.y, 0, 0 );

                // Change our auto-translation speed and direction.  This is
                // done to simulate collisions and other interactions in the
                // nucleus.
                _xAutoDelta = MAX_AUTO_TRANSLATE_AMT *((_rand.nextDouble() * 2.0) - 1.0); 
                _yAutoDelta = MAX_AUTO_TRANSLATE_AMT * ((_rand.nextDouble() * 2.0) - 1.0);             
                
                if (distanceFromOrigin > BASIC_NUCLEUS_RADIUS)
                {
                    // We are outside the basic radius of the nucleus, so we
                    // create a slight bias towards moving back towards the
                    // center.
                    
                    if (((_xAutoDelta > 0.0) && (_position.x > 0.0)) ||
                        ((_xAutoDelta < 0.0) && (_position.x < 0.0)))
                    {
                        // Reverse our direction in this dimension in order to
                        // tend more toward the origin.
                        _xAutoDelta = -_xAutoDelta;
                    }
                    if (((_yAutoDelta > 0.0) && (_position.y > 0.0)) ||
                            ((_yAutoDelta < 0.0) && (_position.y < 0.0)))
                    {
                        // Reverse our direction in this dimension in order to
                        // tend more toward the origin.
                        _yAutoDelta = -_yAutoDelta;
                    }
                }
                
                // Reset the hysteresis counter.
                _changeHysteresis = HYSTERESIS_VALUE;
            }
        }
        
        // Update our position based on our current delta (i.e. velocity).
        _position.x += _xAutoDelta;
        _position.y += _yAutoDelta;
        
        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(); 
        }        
    }
    
    /**
     * This method simulates the quantum tunneling behavior, which means that
     * it causes the particle to move to some new random location within the
     * confines of the supplied parameters.
     * 
     * @param minDistance - Minimum distance from origin (0,0).  This is
     * generally the radius of the nucleus.
     * @param maxDistance - Maximum distance from origin (0,0).
     */
    public void tunnel(double minDistance, double maxDistance)
    {       
        // Create a probability distribution that will cause the particles to
        // be fairly evenly spread around the core of the nucleus and appear
        // occasionally at the outer reaches.
        
        double multiplier = _rand.nextDouble();
        
        if (multiplier > 0.8)
        {
            // Cause the distribution to tail off in the outer regions of the
            // nucleus.
            multiplier = _rand.nextDouble() * _rand.nextDouble();
        }
        
        double newRadius = minDistance + (multiplier * (maxDistance - minDistance));
       
        // Calculate the new angle, in radians, from the origin.
        double newAngle = _rand.nextDouble() * 2 * Math.PI;
        
        // Convert from polar to Cartesian coordinates.
        double xPos = Math.cos( newAngle ) * newRadius;
        double yPos = Math.sin( newAngle ) * newRadius;
        
        // Save the new position.
        _position.setLocation( xPos, yPos );

        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(); 
        }        
    }
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------

    public void addListener(Listener listener)
    {
        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        _listeners.add( listener );
    }
    
    public static interface Listener {
        void positionChanged();
    }
}

/**
 * Class: CollimatedBeam
 * Package: edu.colorado.phet.lasers.model.photon
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.photon;


import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.lasers.model.LaserModel;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.LinkedList;

/**
 * A CollimatedBeam is a collection of photons that all have identical
 * velocities. The beam has a height, and the photons are randomly distributed
 * across that height.
 */
public class CollimatedBeam extends Particle {

    private static Random gaussianGenerator = new Random();

    private double nextTimeToProducePhoton = 0;
    private int wavelength;
    private Point2D origin;
    private double height;
    private double width;
    private Vector2D velocity;
    private ArrayList photons = new ArrayList();
    // The rate at which the beam produces photons
    private double timeSinceLastPhotonProduced = 0;
    // Used to deterimine when photons should be produced
    private double photonsPerSecond = 30;
    // Is the collimated beam currently generating photons?
    private boolean isActive;
    private LaserModel model;
    private LinkedList listeners = new LinkedList();

    public interface Listener {
        void photonCreated( CollimatedBeam beam, Photon photon );
    }

    public CollimatedBeam( LaserModel model, int wavelength, Point2D origin, double height, double width, Vector2D direction ) {
        this.model = model;
        this.wavelength = wavelength;
        this.origin = origin;
        this.height = height;
        this.width = width;
        this.velocity = new Vector2D.Double( direction ).normalize().scale( Photon.s_speed );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public Point2D getOrigin() {
        return origin;
    }

    public void setOrigin( Point2D origin ) {
        this.origin = origin;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight( double height ) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth( double width ) {
        this.width = width;
    }

    public double getPhotonsPerSecond() {
        return photonsPerSecond;
    }

    public void setPhotonsPerSecond( double photonsPerSecond ) {

        // The following if statement prevents the system from sending out a big
        // wave of photons if it has been set at a rate of 0 for awhile.
        if( this.photonsPerSecond == 0 ) {
            timeSinceLastPhotonProduced = 0;
        }
        this.photonsPerSecond = photonsPerSecond;
        nextTimeToProducePhoton = getNextTimeToProducePhoton();
    }

    public int getWavelength() {
        return wavelength;
    }

    public void addPhoton() {
        Photon newPhoton = Photon.create( this );
        newPhoton.setPosition( genPositionX(), genPositionY() + newPhoton.getRadius() );
        newPhoton.setVelocity( new Vector2D.Double( velocity ) );
        newPhoton.setWavelength( this.wavelength );
        model.addModelElement( newPhoton );
        photons.add( newPhoton );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.photonCreated( this, newPhoton );
        }
    }

    public void removePhoton( Photon photon ) {
        photons.remove( photon );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // Produce photons
        if( isActive() ) {
            timeSinceLastPhotonProduced += dt;
            int numPhotons = (int)( photonsPerSecond * timeSinceLastPhotonProduced );
//            for( int i = 0; i < numPhotons; i++ ) {
            if( nextTimeToProducePhoton < timeSinceLastPhotonProduced ) {
                timeSinceLastPhotonProduced = 0;
                this.addPhoton();
                nextTimeToProducePhoton = getNextTimeToProducePhoton();
            }
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive( boolean active ) {
        isActive = active;
        timeSinceLastPhotonProduced = 0;
    }

    private double genPositionY() {
        double yDelta = velocity.getX() != 0 ? Math.random() * height : 0;
        return this.getPosition().getY() + yDelta;
    }

    private double genPositionX() {
        double xDelta = velocity.getY() != 0 ?
                        Math.random() * width : 0;
        return this.getPosition().getX() + xDelta;
    }

    private double getNextTimeToProducePhoton() {
        double temp = ( gaussianGenerator.nextGaussian() + 1.0 );
        return temp / photonsPerSecond;
    }
}

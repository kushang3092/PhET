/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.model.photon.Photon;

public class GroundState extends AtomicState {

    public interface Listener {
        void numInstancesChanged( int numInstances );
    }

    public GroundState() {
        setEnergyLevel( AtomicState.minEnergy );
        setMeanLifetime( Double.POSITIVE_INFINITY );
    }

    public void collideWithPhoton( Atom atom, Photon photon ) {

        // Only respond a specified percentage of the time
        if( Math.random() < s_collisionLikelihood ) {
            AtomicState newState = getStimulatedState( atom, photon, 0 );
            if( newState != null ) {
                photon.removeFromSystem();
                atom.setCurrState( newState );
            }
        }
    }

    public AtomicState getNextLowerEnergyState() {
        return AtomicState.MinEnergyState.instance();
    }
}

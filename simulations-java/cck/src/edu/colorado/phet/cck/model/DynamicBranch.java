package edu.colorado.phet.cck.model;

/**
 * Marker interface to indicate that the circuit has to be solved as a function of time.
 */
public interface DynamicBranch {
    void stepInTime( double dt );

    void resetDynamics();

    void setTime( double time );
}

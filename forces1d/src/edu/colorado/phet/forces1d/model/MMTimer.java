/*PhET, 2004.*/
package edu.colorado.phet.forces1d.model;

import edu.colorado.phet.common.model.ModelElement;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:45:47 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class MMTimer implements ModelElement {
    private double time = 0;
    private String name;
    private ArrayList listeners = new ArrayList();

    public MMTimer( String name ) {
        this.name = name;
    }

    public static interface Listener {
        void timeChanged();
    }

    public void stepInTime( double dt ) {
        time += dt;
        updateObservers();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void updateObservers() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.timeChanged();
        }
    }

    public String toString() {
        return "MMTimer, name=" + name;
    }

    public double getTime() {
        return time;
    }

    public void reset() {
        this.time = 0;
        updateObservers();
    }

    public void setTime( double time ) {
        this.time = time;
        updateObservers();
    }

}

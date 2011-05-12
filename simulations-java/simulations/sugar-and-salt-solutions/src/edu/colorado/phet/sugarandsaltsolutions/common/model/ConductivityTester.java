// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.IConductivityTester;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Conductivity tester that can be dipped in the water to light a light bulb.
 *
 * @author Sam Reid
 */
public class ConductivityTester implements IConductivityTester {
    //Locations are in model coordinates (meters)
    private static final double PROBE_Y = 1;
    private static final double NEGATIVE_PROBE_X = -0.6;
    private static final double POSITIVE_PROBE_X = -0.2;
    private static final Point2D.Double location = new Point2D.Double( 0, 0.40 );

    //Position of the probes, in model coordinates
    private Point2D.Double negativeProbeLocation = new Point2D.Double( NEGATIVE_PROBE_X, PROBE_Y );
    private Point2D.Double positiveProbeLocation = new Point2D.Double( POSITIVE_PROBE_X, PROBE_Y );

    //Listeners
    private final ArrayList<ConductivityTesterChangeListener> conductivityTesterListeners = new ArrayList<ConductivityTesterChangeListener>();

    //True if the user has selected to use the conductivity tester
    public final Property<Boolean> visible = new Property<Boolean>( false );

    //Brightness value (between 0 and 1)
    public final Property<Double> brightness = new Property<Double>( 0.0 ) {{

        //When brightness changes, forward change events to ConductivityTesterChangeListeners
        addObserver( new SimpleObserver() {
            public void update() {
                for ( ConductivityTesterChangeListener conductivityTesterListener : conductivityTesterListeners ) {
                    conductivityTesterListener.brightnessChanged();
                }
            }
        } );
    }};

    //Determine if the conductivity tester is visible
    public boolean isVisible() {
        return visible.get();
    }

    //Add a listener
    public void addConductivityTesterChangeListener( ConductivityTesterChangeListener conductivityTesterChangeListener ) {
        conductivityTesterListeners.add( conductivityTesterChangeListener );
    }

    //Determine the size of the probes
    public PDimension getProbeSizeReference() {
        return new PDimension( 0.05, 0.1 );
    }

    //Determine the location of the positive probe
    public Point2D getPositiveProbeLocationReference() {
        return positiveProbeLocation;
    }

    //Determine the location of the bulb/battery unit.
    public Point2D getLocationReference() {
        return location;
    }

    //Set the location of the positive probe and notify observers
    public void setPositiveProbeLocation( double x, double y ) {
        positiveProbeLocation.setLocation( x, y );
        System.out.println( "positiveProbeLocation = " + positiveProbeLocation );
        for ( ConductivityTesterChangeListener listener : conductivityTesterListeners ) {
            listener.positiveProbeLocationChanged();
        }
    }

    //Get the location of the negative probe
    public Point2D getNegativeProbeLocationReference() {
        return negativeProbeLocation;
    }

    //Set the location of the negative probe and notify observers
    public void setNegativeProbeLocation( double x, double y ) {
        negativeProbeLocation.setLocation( x, y );
        System.out.println( "negativeProbeLocation = " + negativeProbeLocation );
        for ( ConductivityTesterChangeListener listener : conductivityTesterListeners ) {
            listener.negativeProbeLocationChanged();
        }
    }

    //Get the bulb brightness, a function of the conductivity of the liquid
    public double getBrightness() {
        return brightness.get();
    }

    public void reset() {

        //Reset the location of the probes
        setNegativeProbeLocation( NEGATIVE_PROBE_X, PROBE_Y );
        setPositiveProbeLocation( POSITIVE_PROBE_X, PROBE_Y );
    }

    //Either I don't understand ConductivityTesterNode's coordinate frames or it is buggy (or both).
    //Until those issues are resolved, we subtract out the faulty values here
    //TODO: Remove the need for this awkward hack workaround
    public Point2D getNegativeProbeModelLocation() {
        return new Point2D.Double( getNegativeProbeLocationReference().getX() + 0.6, getNegativeProbeLocationReference().getY() - 0.6471336996336996 );
    }

    public Point2D getPositiveProbeModelLocation() {
        return new Point2D.Double( getPositiveProbeLocationReference().getX() + 0.2, getPositiveProbeLocationReference().getY() - 0.6471336996336996 );
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * @author Sam Reid
 */
public class CartoonModeList extends ModeList {
    //Use the real radius for the earth, and base other values on that
    private static final double EARTH_RADIUS = RealModeList.EARTH_RADIUS;
    private static final double EARTH_ORBITAL_SPEED_AT_PERIHELION = 30;
    public static final double EARTH_MASS = RealModeList.EARTH_MASS / 1E6;

    private static final double SUN_RADIUS = EARTH_RADIUS * 2;
    private static final double SUN_MASS = EARTH_MASS * 100;

    public static final double EARTH_PERIHELION = SUN_RADIUS * 4;

    private static final double MOON_MASS = EARTH_MASS / 100;
    private static final double MOON_RADIUS = EARTH_RADIUS / 2;
    private static final double MOON_SPEED = -10;
    public static final double MOON_PERIGEE = SUN_RADIUS * 2;
    private static final double MOON_X = EARTH_PERIHELION;
    private static final double MOON_Y = MOON_PERIGEE;

    //see http://en.wikipedia.org/wiki/International_Space_Station
    private static final double SPACE_STATION_RADIUS = 109;
    public static final double SPACE_STATION_MASS = 369914;
    private static final double SPACE_STATION_SPEED = 7706;
    private static final double SPACE_STATION_PERIGEE = 347000;

    public CartoonModeList( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Scale> scaleProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty ) {
        super( new ModeListParameter( clockPausedProperty, gravityEnabledProperty, scaleProperty, stepping, rewinding, timeSpeedScaleProperty ),
               new BodyPrototype( SUN_RADIUS, SUN_MASS, new ImmutableVector2D( 0, 0 ), new ImmutableVector2D( 0, 0 ) ),
               new BodyPrototype( EARTH_RADIUS, EARTH_MASS, new ImmutableVector2D( EARTH_PERIHELION, 0 ), new ImmutableVector2D( 0, EARTH_ORBITAL_SPEED_AT_PERIHELION ) ),
               new BodyPrototype( MOON_RADIUS, MOON_MASS, new ImmutableVector2D( MOON_X, MOON_Y ), new ImmutableVector2D( MOON_SPEED, 0 ) ),
               new BodyPrototype( SPACE_STATION_RADIUS, SPACE_STATION_MASS, new ImmutableVector2D( EARTH_PERIHELION + SPACE_STATION_PERIGEE + EARTH_RADIUS, 0 ), new ImmutableVector2D( 0, SPACE_STATION_SPEED ) ),
               2500 );
    }
}

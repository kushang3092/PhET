// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5.doubleproperty;

import edu.colorado.phet.common.phetcommon.model.property5.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property5.Property;

/**
 * Convenience subclass that adds methods such as plus(), times(), divide(), etc.
 *
 * @author Sam Reid
 */
public class DoubleProperty extends Property<Double> {
    public DoubleProperty( Double value ) {
        super( value );
    }

    public ObservableProperty<Double> plus( ObservableProperty<Double> b ) {
        return new Plus( this, b );
    }

    public DividedBy dividedBy( ObservableProperty<Double> volume ) {
        return new DividedBy( this, volume );
    }

    public GreaterThan greaterThan( double value ) {
        return new GreaterThan( this, value );
    }
}

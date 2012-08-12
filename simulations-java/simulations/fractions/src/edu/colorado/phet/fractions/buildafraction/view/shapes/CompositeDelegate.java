// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;

/**
 * @author Sam Reid
 */
public class CompositeDelegate implements PActivityDelegate {
    private final PActivityDelegate[] delegates;

    public CompositeDelegate( PActivityDelegate... delegates ) {
        this.delegates = delegates;
    }

    @Override public void activityStarted( final PActivity activity ) {
        for ( PActivityDelegate delegate : delegates ) {
            delegate.activityStarted( activity );
        }
    }

    @Override public void activityStepped( final PActivity activity ) {
        for ( PActivityDelegate delegate : delegates ) {
            delegate.activityStepped( activity );
        }
    }

    @Override public void activityFinished( final PActivity activity ) {
        for ( PActivityDelegate delegate : delegates ) {
            delegate.activityFinished( activity );
        }
    }
}
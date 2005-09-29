/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.Segment;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:54 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class FreeSplineMode extends ForceMode {
    private AbstractSpline spline;
    private Body body;

    public FreeSplineMode( AbstractSpline spline, Body body ) {
        super( new Vector2D.Double() );
        this.spline = spline;
        this.body = body;
    }

    static {
        System.out.println( "origKE\torigPE\torigTot" );
    }

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
        double origKE = body.getKineticEnergy();
        double origPE = model.getPotentialEnergy( body );
        double origTotalEnergy = model.getTotalEnergy( body );
        System.out.println( origKE + "\t" + origPE + "\t" + origTotalEnergy );
        EnergyDebugger.stepStarted( model, body, dt );
        double position = new SplineLogic( body ).guessPositionAlongSpline( getSpline() );
        if( position == 0 ) {//todo is this necessary?
            body.setFreeFallMode();
            super.setNetForce( new Vector2D.Double( 0, 0 ) );
            super.stepInTime( model, body, dt );
        }
        else {
            Segment segment = spline.getSegmentPath().getSegmentAtPosition( position );//todo this duplicates much work.
            body.setAngle( segment.getAngle() );//todo rotations.

            AbstractVector2D netForce = computeNetForce( model, segment );
            super.setNetForce( netForce );

            super.stepInTime( model, body, dt );
            //just kill the perpendicular part of velocity, if it is through the track.
            // this should be lost to friction.
            //or to a bounce.
            RVector2D origVector = new RVector2D( body.getVelocity(), segment.getUnitDirectionVector() );

//            double bounceThreshold = 20;
            double bounceThreshold = 30;
            boolean bounced = false;
            boolean grabbed = false;
            if( origVector.getPerpendicular() < 0 ) {//velocity is through the segment
                if( Math.abs( origVector.getPerpendicular() ) > bounceThreshold ) {//bounce
                    origVector.setPerpendicular( Math.abs( origVector.getPerpendicular() ) );
                    bounced = true;
                }
                else {//grab
                    origVector.setPerpendicular( 0.0 );
                    grabbed = true;
                }
            }
            Vector2D.Double newVelocity = origVector.toCartesianVector();

            EC3Debug.debug( "newVelocity = " + newVelocity );
            body.setVelocity( newVelocity );

            if( bounced || grabbed ) {
                //set bottom at zero.
                setBottomAtZero( segment, body );
            }
        }

        new EnergyConserver().fixEnergy( model, body, origTotalEnergy );
    }

    private void setBottomAtZero( Segment segment, Body body ) {
        double bodyYPerp = segment.getUnitNormalVector().dot( body.getPositionVector() );
        double segmentYPerp = segment.getUnitNormalVector().dot( new ImmutableVector2D.Double( segment.getCenter2D() ) );
        double overshoot = -( bodyYPerp - segmentYPerp - body.getHeight() / 2.0 );
        EC3Debug.debug( "overshoot = " + overshoot );
        overshoot -= 1;//hang in there
        if( overshoot > 0 ) {
            AbstractVector2D tx = segment.getUnitNormalVector().getScaledInstance( overshoot );
            body.translate( tx.getX(), tx.getY() );
        }
    }

    private AbstractVector2D computeNetForce( EnergyConservationModel model, Segment segment ) {
        double fgy = model.getGravity() * body.getMass();
        EC3Debug.debug( "segment.getAngle() = " + segment.getAngle() );
        EC3Debug.debug( "Math.cos( segment.getAngle()) = " + Math.cos( segment.getAngle() ) );
        AbstractVector2D normalForce = segment.getUnitNormalVector().getScaledInstance( -fgy * Math.cos( segment.getAngle() ) );
        EC3Debug.debug( "normalForce.getY() = " + normalForce.getY() );
        double fy = fgy + normalForce.getY();
        double fx = normalForce.getX();
        Vector2D.Double netForce = new Vector2D.Double( fx, fy );
        return netForce;
    }

    public AbstractSpline getSpline() {
        return spline;
    }

}

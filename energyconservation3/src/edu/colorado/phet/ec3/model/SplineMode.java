package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 17, 2006
 * Time: 11:55:53 AM
 * Copyright (c) Oct 17, 2006 by Sam Reid
 */

public class SplineMode implements UpdateMode {
    private EnergyConservationModel model;
    private AbstractSpline spline;
    private double savedLocation;
    private Body lastState;
    private Body afterNewton;
    private Vector2D.Double lastNormalForce;

    public SplineMode( EnergyConservationModel model, AbstractSpline spline ) {
        this.model = model;
        this.spline = spline;
    }

    public boolean isUserControlled( Body body ) {
        return body.isUserControlled() || spline.isUserControlled();
    }

    public void stepInTime( Body body, double dt ) {
        Body origState = body.copyState();
        double x = savedLocation;
        double sign = spline.getUnitParallelVector( x ).dot( body.getVelocity() ) > 0 ? 1 : -1;
        body.setVelocity( spline.getUnitParallelVector( x ).getInstanceOfMagnitude( body.getVelocity().getMagnitude() * sign ) );
        AbstractVector2D netForceWithoutNormal = getNetForcesWithoutNormal( body, x );
        new ForceMode( netForceWithoutNormal ).stepInTime( body, dt );
        afterNewton = body.copyState();

        double x2 = getDistAlongSplineSearch( body.getAttachPoint(), x, 0.3, 60, 2 );
//        System.out.println( "x2=" + x2 );
        if( x2 <= 0 || x2 >= spline.getLength() - 0.01 ) {//fly off the end of the spline
            body.setLastFallTime( spline, System.currentTimeMillis() );
            body.setFreeFallMode();
            return;
        }
        if( afterNewton.getVelocity().dot( spline.getUnitNormalVector( x2 ) ) > 0 ) {
            body.setLastFallTime( spline, System.currentTimeMillis() );
            body.setFreeFallMode();
            body.setAngularVelocity( 0.0 );
            return;
        }
        double thermalEnergy = getFrictionForce( body, ( x + x2 ) / 2 ).getMagnitude() * origState.getPositionVector().getSubtractedInstance( body.getPositionVector() ).getMagnitude();
        body.addThermalEnergy( thermalEnergy );

        savedLocation = x2;
        Point2D splineLocation = spline.evaluateAnalytical( x2 );

        //make sure we sank into the spline before applying this change
        body.setAttachmentPointPosition( splineLocation );
        rotateBody( body, x2, dt, Double.POSITIVE_INFINITY );
        System.out.println( "isUserControlled( body ) = " + isUserControlled( body ) );
        if( !isUserControlled( body ) ) {
            boolean fixed = new EnergyConserver().fixEnergyWithVelocity( body, origState.getTotalEnergy(), 15 );

            if( !fixed ) {
                //look for a nearby rotation and/or spline position that conserves energy...?
                AbstractVector2D netForce = netForceWithoutNormal.getAddedInstance( lastNormalForce );
                //wait until upside up to stop in a well
                if( netForce.getMagnitude() < 5000 && ( Math.abs( Math.sin( body.getAttachmentPointRotation() ) ) < 0.1 ) )
                {
                    body.setVelocity( origState.getVelocity() );
                    body.setAttachmentPointPosition( origState.getAttachPoint() );
                    body.setAttachmentPointRotation( origState.getAttachmentPointRotation() );
                    body.setThermalEnergy( origState.getThermalEnergy() );
                }
                else {
                    if( origState.getEnergyDifferenceAbs( body ) > 1E1 ) {
                        System.out.println( "Energy error=" + origState.getEnergyDifferenceAbs( body ) + ", rolling back changes." );
                        body.setVelocity( origState.getVelocity() );
                        body.setAttachmentPointPosition( origState.getAttachPoint() );
                        body.setAttachmentPointRotation( origState.getAttachmentPointRotation() );
                        body.setThermalEnergy( origState.getThermalEnergy() );
                    }
                }
                //maybe could fix by rotation?, i think no.
                //could fix with friction, if friction is enabled.
            }
        }
        lastState = body.copyState();

        lastNormalForce = updateNormalForce( origState, body, netForceWithoutNormal, dt );
    }

    private Vector2D.Double updateNormalForce( Body origState, Body body, AbstractVector2D netForce, double dt ) {
        //numerically unstable, since we divide by dt^2
        //2m/t^2 (x1-x0-v0t)-Fa
        Vector2D.Double vec = new Vector2D.Double();
        vec.add( body.getPositionVector() );
        vec.subtract( origState.getPositionVector() );
        vec.subtract( origState.getVelocity() );
        vec.scale( 2 * body.getMass() / dt / dt );
        vec.subtract( netForce );
        return vec;
    }

    private void rotateBody( Body body, double x, double dt, double maxRotationDTheta ) {
        double bodyAngle = body.getAttachmentPointRotation();
        double dA = spline.getUnitParallelVector( x ).getAngle() - bodyAngle;
        if( dA > Math.PI ) {
            dA -= Math.PI * 2;
        }
        else if( dA < -Math.PI ) {
            dA += Math.PI * 2;
        }
        if( dA > maxRotationDTheta ) {
            dA = maxRotationDTheta;
        }
        else if( dA < -maxRotationDTheta ) {
            dA = -maxRotationDTheta;
        }
        body.rotateAboutAttachmentPoint( dA );
    }

    private double getDistAlongSplineSearch( Point2D attachPoint, double center, double epsilon, int numPts, int numIterations ) {
        double best = 0;
        for( int i = 0; i < numIterations; i++ ) {
            best = getDistAlongSpline( attachPoint, Math.max( center - epsilon, 0 ), Math.min( spline.getLength(), center + epsilon ), numPts );
            center = best;
            epsilon = epsilon / numPts * 2;
        }
        return best;
    }

    private double getDistAlongSpline( Point2D attachPoint ) {
        return getDistAlongSpline( attachPoint, 0, spline.getLength(), 100 );
    }

    public void init( Body body ) {
        body.convertToSpline();
        savedLocation = getDistAlongSpline( body.getAttachPoint() );
        lastState = body.copyState();
        lastNormalForce = new Vector2D.Double();
    }

    private double getDistAlongSpline( Point2D pt, double min, double max, double numPts ) {
        return spline.getDistAlongSpline( pt, min, max, numPts );
    }

    private AbstractVector2D getNetForcesWithoutNormal( Body body, double x ) {
        //todo: normal should opposed both gravity and thrust when applicable
        AbstractVector2D[] forces = new AbstractVector2D[]{
                body.getGravityForce(),
                body.getThrust(),
                getFrictionForce( body, x )
        };
        Vector2D.Double sum = new Vector2D.Double();
        for( int i = 0; i < forces.length; i++ ) {
            AbstractVector2D force = forces[i];
            sum.add( force );
        }
        if( Double.isNaN( sum.getX() ) ) {
            System.out.println( "nan" );
        }
        return sum;
    }

    private AbstractVector2D getFrictionForce( Body body, double x ) {
        //todo kind of a funny workaround for getting friction on the ground.
        double coefficient = Math.max( body.getFrictionCoefficient(), spline.getFrictionCoefficient() );
        double fricMag = coefficient * lastNormalForce.getMagnitude() / 10.0;//todo should the normal force be computed as emergent?
        if( body.getVelocity().getMagnitude() > 0 ) {
            return body.getVelocity().getInstanceOfMagnitude( -fricMag );
        }
        else {
            return new ImmutableVector2D.Double( 0, 0 );
        }
    }

    public AbstractSpline getSpline() {
        return spline;
    }

    public UpdateMode copy() {
        return new SplineMode( model, getSpline() );
    }

    public static class GrabSpline {
        private EnergyConservationModel energyConservationModel;

        public GrabSpline( EnergyConservationModel energyConservationModel ) {
            this.energyConservationModel = energyConservationModel;
        }

        public void interactWithSplines( Body body ) {
            body.convertToSpline();
            double bestScore = Double.POSITIVE_INFINITY;
            AbstractSpline bestSpline = null;
            ArrayList allSplines = energyConservationModel.getAllSplines();
            for( int i = 0; i < allSplines.size(); i++ ) {
                AbstractSpline splineSurface = (AbstractSpline)allSplines.get( i );
                double score = getGrabScore( splineSurface, body );
//                System.out.println( "grab score = " + score );
                if( score < bestScore ) {
                    bestScore = score;
                    bestSpline = splineSurface;
                }
            }
            if( bestSpline != null ) {
                body.setSplineMode( energyConservationModel, bestSpline );
            }
            else {
                tryCollision( body );
                body.convertToFreefall();
            }
        }

        private void tryCollision( Body body ) {
            body.convertToSpline();
            double bestScore = Double.POSITIVE_INFINITY;
            AbstractSpline bestSpline = null;
            ArrayList allSplines = energyConservationModel.getAllSplines();
            for( int i = 0; i < allSplines.size(); i++ ) {
                AbstractSpline splineSurface = (AbstractSpline)allSplines.get( i );
                double score = getBounceScore( splineSurface, body );
                if( !Double.isInfinite( score ) ) {
//                    System.out.println( "bounce score = " + score );
                }
                if( score < bestScore ) {
                    bestScore = score;
                    bestSpline = splineSurface;
                }
            }
            if( bestSpline != null ) {
                Area area = new Area( bestSpline.getArea() );//todo duplicate computation
                //todo: add restitution
                area.intersect( new Area( body.getShape() ) );
                Rectangle2D r = area.getBounds2D();
                double x = bestSpline.getDistAlongSpline( new Point2D.Double( r.getCenterX(), r.getCenterY() ), 0, bestSpline.getLength(), 100 );
                if( !feetAreClose( body, x, bestSpline ) ) {
                    double epsilon = 0.05;
                    if( ( x <= epsilon || x >= bestSpline.getLength() - epsilon ) && isBodyMovingTowardSpline( body, bestSpline, x ) )
                    {
                        System.out.println( "Collision with end." );
                        body.setVelocity( body.getVelocity().getScaledInstance( -1 ) );
                        double angle = body.getVelocity().getAngle();
                        double maxDTheta = Math.PI / 16;
                        double dTheta = ( Math.random() * 2 - 1 ) * maxDTheta;
                        body.setVelocity( Vector2D.Double.parseAngleAndMagnitude( body.getVelocity().getMagnitude(), angle + dTheta ) );
                        body.setAngularVelocity( dTheta * 10 );
                        body.convertToFreefall();
                    }
                    else {//( !feetAreClose( body, x, bestSpline ) ) {
                        double parallelPart = bestSpline.getUnitParallelVector( x ).dot( body.getVelocity() );
                        double perpPart = bestSpline.getUnitNormalVector( x ).dot( body.getVelocity() );
                        Vector2D.Double newVelocity = new Vector2D.Double();
                        newVelocity.add( bestSpline.getUnitParallelVector( x ).getScaledInstance( parallelPart ) );
                        newVelocity.add( bestSpline.getUnitNormalVector( x ).getScaledInstance( -perpPart ) );
                        body.setVelocity( newVelocity );
                        body.convertToFreefall();
                        body.setAngularVelocity( parallelPart / 2 );
//                    System.out.println( "Collision, feet far@,x = "+x );
                    }
                }
            }
            else {
                body.convertToFreefall();
            }
        }

        private boolean isBodyMovingTowardSpline( Body body, AbstractSpline bestSpline, double x ) {
            Point2D loc = bestSpline.evaluateAnalytical( x );
            Point2D cm = body.getCenterOfMass();
            Vector2D.Double dir = new Vector2D.Double( loc, cm );
            double v = dir.dot( body.getVelocity() );
            System.out.println( "v = " + v );
            return v < -1;
        }

        private boolean centerOfMassTooClose( AbstractSpline spline, Body body, double x ) {
            double v = body.getCenterOfMass().distance( spline.evaluateAnalytical( x ) );
            System.out.println( "v = " + v );
            return v < 0.5;
        }

        private boolean feetAreClose( Body body, double x, AbstractSpline bestSpline ) {
            return bestSpline.evaluateAnalytical( x ).distance( body.getAttachPoint() ) < bestSpline.evaluateAnalytical( x ).distance( body.getCenterOfMass() );
        }

        private double getBounceScore( AbstractSpline splineSurface, Body body ) {
            Area area = new Area( splineSurface.getArea() );
            area.intersect( new Area( body.getShape() ) );
            return area.isEmpty() ? Double.POSITIVE_INFINITY : 0.0;//Todo: don't need to compute others, could break
        }

        private double getGrabScore( AbstractSpline splineSurface, Body body ) {
            double x = splineSurface.getDistAlongSpline( body.getAttachPoint(), 0, splineSurface.getLength(), 100 );
            Point2D pt = splineSurface.evaluateAnalytical( x );
            double dist = pt.distance( body.getAttachPoint() );
            if( dist < 0.5 && correctSide( body, x, pt, splineSurface ) && !justLeft( body, splineSurface ) ) {
                return dist;
            }
            else {
                return Double.POSITIVE_INFINITY;
            }
        }

        private boolean justLeft( Body body, AbstractSpline splineSurface ) {
            return body.getLastFallSpline() == splineSurface && ( System.currentTimeMillis() - body.getLastFallTime() ) < 1000;
        }

        private boolean correctSide( Body body, double x, Point2D splineAttachPoint, AbstractSpline abstractSpline ) {
            return true;
//            Point2D cm = body.getCenterOfMass();
//            Vector2D.Double cmVector = new Vector2D.Double( splineAttachPoint, cm );
//            Vector2D.Double attachVector = new Vector2D.Double( body.getAttachPoint(), cm );
//            return cmVector.dot( abstractSpline.getUnitNormalVector( x ) ) > 0 && attachVector.dot( abstractSpline.getUnitNormalVector( x ) ) > 0;
        }

    }
}

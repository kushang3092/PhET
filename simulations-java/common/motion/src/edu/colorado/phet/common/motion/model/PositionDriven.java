package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class PositionDriven implements UpdateStrategy {
    private int velocityWindow = 6;
    private int accelerationWindow = 6;

    //todo: try 2nd order derivative directly from position data?
    public void update( MotionBodySeries model, double dt, MotionBodyState state, double time ) {
        TimeData v = MotionMath.getDerivative( model.getRecentPositionTimeSeries( Math.min( velocityWindow, model.getVelocitySampleCount() ) ) );
        TimeData a = MotionMath.getDerivative( model.getRecentVelocityTimeSeries( Math.min( accelerationWindow, model.getAccelerationSampleCount() ) ) );

        model.addPositionData( state.getPosition(), time );
        model.addVelocityData( v.getValue(), v.getTime() );
        model.addAccelerationData( a.getValue(), a.getTime() );
    }

    public double getAccelerationWindow() {
        return accelerationWindow;
    }

    public void setVelocityWindow( int maxWindowSize ) {
        this.velocityWindow = maxWindowSize;
    }

    public int getVelocityWindow() {
        return velocityWindow;
    }

}

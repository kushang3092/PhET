/**
 * Class: WallSphereContactExpertTest
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.idealgas.model.HorizontalWall;
import edu.colorado.phet.idealgas.model.SphericalBody;
import edu.colorado.phet.idealgas.model.VerticalWall;
import edu.colorado.phet.idealgas.model.Wall;

//import edu.colorado.phet.idealgas.physics.body.Wall;

/**
 * Determines if a wall and sphere are in contact. Currently, this doesn't take
 * in to account the extent of the wall. In other words, it considers the wall
 * to be infinite in length
 */
public class SphereWallContactDetector extends ContactDetector {

    //    private Point2D.Double tempVector = new Point2D.Double();
    private Vector2D tempVector = new Vector2D.Double();

    /**
     * @param bodyA
     * @param bodyB
     * @return
     */
    protected boolean applies( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean b = ( ( bodyA instanceof SphericalBody && bodyB instanceof Wall )
                      || ( bodyB instanceof SphericalBody && bodyA instanceof Wall ) );
        return b;
    }

    /**
     * @param bodyA
     * @param bodyB
     * @return
     */
    public boolean areInContact( CollidableBody bodyA, CollidableBody bodyB ) {
        SphericalBody sphere = null;
        Wall wall = null;
        if( bodyA instanceof Wall && bodyB instanceof SphericalBody ) {
            wall = (Wall)bodyA;
            sphere = (SphericalBody)bodyB;
        }
        else if( bodyA instanceof SphericalBody && bodyB instanceof Wall ) {
            wall = (Wall)bodyB;
            sphere = (SphericalBody)bodyA;
        }

        // Determine if the sphere within a radius distance of the line on which
        // the wall lies
        double x = wall.getPosition().getX();
        double y = wall.getPosition().getY();
        tempVector.setComponents( Double.isNaN( x ) ? 0 : x, Double.isNaN( y ) ? 0 : y );
        tempVector.setComponents( tempVector.getX() - sphere.getPosition().getX(),
                                  tempVector.getY() - sphere.getPosition().getY() );
        float dist = (float)Math.abs( tempVector.dot( wall.getLoaUnit( sphere ) ) );
        boolean result = dist <= sphere.getRadius();

        // If the sphere managed to go all the way through the wall in a single step,
        // we need to count that as contact, too.
        if( wall instanceof VerticalWall ) {
            double d = ( wall.getPosition().getX() - sphere.getPosition().getX() )
                       * ( wall.getPosition().getX() - sphere.getPositionPrev().getX() );
            //            if( d < 0 ) {
            //                System.out.println( "VVVV" );
            //            }
            result |= ( d < 0 );
        }
        if( wall instanceof HorizontalWall ) {
            double d = ( wall.getPosition().getY() - sphere.getPosition().getY() )
                       * ( wall.getPosition().getY() - sphere.getPositionPrev().getY() );
            //            if( d < 0 ) {
            //                System.out.println( "HHHH" );
            //            }
            result |= ( d < 0 );
        }

        // If the previous result is true, determine if a line through the sphere's
        // CM perpendicular to the line on which the wall lies passes through the
        // wall itself
        if( result ) {
            double dx = wall.getEnd1().getX() - wall.getEnd2().getX();
            double dy = wall.getEnd1().getY() - wall.getEnd2().getY();
            double xSphere = sphere.getPosition().getX();
            double ySphere = sphere.getPosition().getY();
            double xSphere2 = xSphere + dy;
            double ySphere2 = ySphere + dx;
            result &= MathUtil.segmentIntersectsLine( wall.getEnd1().getX(),
                                                      wall.getEnd1().getY(),
                                                      wall.getEnd2().getX(),
                                                      wall.getEnd2().getY(),
                                                      xSphere, ySphere,
                                                      xSphere2, ySphere2 );
        }
        return result;
    }
}

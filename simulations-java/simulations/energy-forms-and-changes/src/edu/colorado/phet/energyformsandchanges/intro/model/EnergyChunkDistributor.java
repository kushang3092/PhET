// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * A class that contains static methods for redistributing a set of energy
 * chunks.
 *
 * @author John Blanco
 */
public class EnergyChunkDistributor {

    private static final double FORCE_CONSTANT = 0.00003; // Chosen empirically.
    private static final double OUTSIDE_RECT_FORCE = 1; // In Newtons.
    private static final double TIME_STEP = 1E-6; // In seconds, for algorithm that moves the points.
    private static final int NUM_TIME_STEPS = 100;
    private static final Random RAND = new Random();

    // TODO: This method may be obsolete, but if not, it should probably call the method with the time step.  For efficiency, I could have a common method that takes a list of points.
    public static void distribute( Rectangle2D rect, List<EnergyChunk> energyChunkList ) {

        // Limit distances to avoid moving too much in one time step.
        double minDistance = Math.min( rect.getWidth() / 100, rect.getHeight() / 100 );

        // Create a map that relates each energy chunk to a point mass.
        Map<EnergyChunk, PointMass> map = new HashMap<EnergyChunk, PointMass>();
        for ( EnergyChunk energyChunk : energyChunkList ) {
            map.put( energyChunk, new PointMass( energyChunk.position.get(), rect ) );
        }

        // Iterate on the positions of the point masses in order to
        // redistribute them.
        for ( int i = 0; i < NUM_TIME_STEPS; i++ ) {

            // Update the forces acting on each point mass.
            for ( PointMass p : map.values() ) {
                if ( rect.contains( p.position.toPoint2D() ) ) {
                    double distance;

                    // Force from left side of rectangle.
                    distance = Math.max( p.position.getX() - rect.getX(), minDistance );
                    p.applyForce( new ImmutableVector2D( FORCE_CONSTANT / Math.pow( distance, 2 ), 0 ) );

                    // Force from right side of rectangle.
                    distance = Math.max( rect.getMaxX() - p.position.getX(), minDistance );
                    p.applyForce( new ImmutableVector2D( -FORCE_CONSTANT / Math.pow( distance, 2 ), 0 ) );

                    // Force from bottom of rectangle.
                    distance = Math.max( p.position.getY() - rect.getY(), minDistance );
                    p.applyForce( new ImmutableVector2D( 0, FORCE_CONSTANT / Math.pow( distance, 2 ) ) );

                    // Force from top of rectangle.
                    distance = Math.max( rect.getMaxY() - p.position.getY(), minDistance );
                    p.applyForce( new ImmutableVector2D( 0, -FORCE_CONSTANT / Math.pow( distance, 2 ) ) );

                    // Apply the force from each of the other particles.
                    for ( PointMass otherP : map.values() ) {
                        if ( p == otherP ) {
                            continue; // Skip self.
                        }

                        // Calculate force vector, but handle cases where too close.
                        ImmutableVector2D vectorToOther = p.position.getSubtractedInstance( otherP.position );
                        if ( vectorToOther.getMagnitude() < minDistance ) {
                            if ( vectorToOther.getMagnitude() == 0 ) {
                                // Create a random vector of min distance.
                                System.out.println( "Creating random vector" );
                                double angle = RAND.nextDouble() * Math.PI * 2;
                                vectorToOther = new ImmutableVector2D( minDistance * Math.cos( angle ), minDistance * Math.sin( angle ) );
                            }
                            else {
                                vectorToOther = vectorToOther.getInstanceOfMagnitude( minDistance );
                            }
                        }
                        p.applyForce( vectorToOther.getInstanceOfMagnitude( FORCE_CONSTANT / ( vectorToOther.getMagnitudeSq() ) ) );
                    }
                }
                else {
                    // Point is outside, move it towards center of rectangle.
                    ImmutableVector2D vectorToCenter = new ImmutableVector2D( rect.getCenterX(), rect.getCenterY() ).getSubtractedInstance( p.position );
                    p.applyForce( vectorToCenter.getInstanceOfMagnitude( OUTSIDE_RECT_FORCE ) );
                }
            }

            // Update the point mass positions.
            for ( PointMass p : map.values() ) {
                // Update the position of the point.
                p.updatePosition( TIME_STEP );
                p.clearAcceleration();
            }
        }

        // Update the positions of the energy chunks.
        for ( EnergyChunk energyChunk : energyChunkList ) {
            energyChunk.position.set( map.get( energyChunk ).position );
        }
        System.out.println( "Updated positions for this many chunks: " + energyChunkList.size() );
    }

    public static void updatePositions( List<EnergyChunk> energyChunkList, Rectangle2D enclosingRect, double dt ) {

        // Create a map that relates each energy chunk to a point mass.
        Map<EnergyChunk, PointMass> map = new HashMap<EnergyChunk, PointMass>();
        for ( EnergyChunk energyChunk : energyChunkList ) {
            map.put( energyChunk, new PointMass( energyChunk.position.get(), enclosingRect ) );
        }

        // Limit minimum distances to avoid forces that are too large.
        double minDistance = Math.min( enclosingRect.getWidth() / 20, enclosingRect.getHeight() / 20 );

        // Update the forces acting on each point mass.
        for ( PointMass p : map.values() ) {
            if ( enclosingRect.contains( p.position.toPoint2D() ) ) {

                // Force from left side of rectangle.
                double distanceFromLeftSide = Math.max( p.position.getX() - enclosingRect.getX(), minDistance );
                p.applyForce( new ImmutableVector2D( FORCE_CONSTANT / Math.pow( distanceFromLeftSide, 2 ), 0 ) );

                // Force from right side of rectangle.
                double distanceFromRightSide = Math.max( enclosingRect.getMaxX() - p.position.getX(), minDistance );
                p.applyForce( new ImmutableVector2D( -FORCE_CONSTANT / Math.pow( distanceFromRightSide, 2 ), 0 ) );

                // Force from bottom of rectangle.
                double distanceFromBottom = Math.max( p.position.getY() - enclosingRect.getY(), minDistance );
                p.applyForce( new ImmutableVector2D( 0, FORCE_CONSTANT / Math.pow( distanceFromBottom, 2 ) ) );

                // Force from top of rectangle.
                double distanceFromTop = Math.max( enclosingRect.getMaxY() - p.position.getY(), minDistance );
                p.applyForce( new ImmutableVector2D( 0, -FORCE_CONSTANT / Math.pow( distanceFromTop, 2 ) ) );

                // Apply the force from each of the other particles.
                for ( PointMass otherP : map.values() ) {
                    if ( p != otherP ) {
                        // Calculate force vector, but handle cases where too close.
                        ImmutableVector2D vectorToOther = p.position.getSubtractedInstance( otherP.position );
                        if ( vectorToOther.getMagnitude() < minDistance ) {
                            if ( vectorToOther.getMagnitude() == 0 ) {
                                // Create a random vector of min distance.
                                System.out.println( "Creating random vector" );
                                double angle = RAND.nextDouble() * Math.PI * 2;
                                vectorToOther = new ImmutableVector2D( minDistance * Math.cos( angle ), minDistance * Math.sin( angle ) );
                            }
                            else {
                                vectorToOther = vectorToOther.getInstanceOfMagnitude( minDistance );
                            }
                        }
                        p.applyForce( vectorToOther.getInstanceOfMagnitude( FORCE_CONSTANT / ( vectorToOther.getMagnitudeSq() ) ) );
                    }
                }
            }
            else {
                // Point is outside container, move it towards center of rectangle.
                System.out.println( "Point detected outside of container." );
                ImmutableVector2D vectorToCenter = new ImmutableVector2D( enclosingRect.getCenterX(), enclosingRect.getCenterY() ).getSubtractedInstance( p.position );
                p.applyForce( vectorToCenter.getInstanceOfMagnitude( OUTSIDE_RECT_FORCE ) );
            }
        }

        // Update the positions of the point masses and the corresponding
        // energy chunks.
        for ( PointMass p : map.values() ) {
            // Update the position of the point.
            p.updatePosition( dt );
            p.clearAcceleration();
        }

        // Update the positions of the energy chunks.
        for ( EnergyChunk energyChunk : energyChunkList ) {
            energyChunk.position.set( map.get( energyChunk ).position );
        }
    }

    public static ImmutableVector2D generateRandomLocation( Rectangle2D rect ) {
        return new ImmutableVector2D( rect.getMinX() + ( RAND.nextDouble() * rect.getWidth() ), rect.getMinY() + ( RAND.nextDouble() * rect.getHeight() ) );
    }

    private static class PointMass {
        private static final double MASS = 1; // In kg.
        private final double maxDistancePerStep;
        private Vector2D position = new Vector2D();
        private Vector2D velocity = new Vector2D( 0, 0 );
        private Vector2D acceleration = new Vector2D( 0, 0 );
        private final Rectangle2D containerRect;

        public PointMass( ImmutableVector2D initialPosition, Rectangle2D container ) {
            this.containerRect = container;
            position.setValue( initialPosition );

            // Since this is a repositioning algorithm, there shouldn't be much
            // motion in a single step.
            maxDistancePerStep = Math.min( container.getWidth() / 10, container.getHeight() / 10 );
        }

        public void applyForce( ImmutableVector2D force ) {
            acceleration.add( force.getScaledInstance( force.getMagnitude() / MASS ) );
        }

        public void clearAcceleration() {
            acceleration.setComponents( 0, 0 );
        }

        public void updatePosition( double dt ) {

            // Update the velocity based on previous velocity and current acceleration.
            velocity.add( acceleration.getScaledInstance( dt ) );

            if ( containerRect.contains( position.toPoint2D() ) ) {
                // Limit the velocity.  This acts much like a drag force that
                // gets stronger as the velocity gets bigger.
                double maxVelocity = 0.15;
                velocity.setMagnitude( maxVelocity * velocity.getMagnitude() / ( velocity.getMagnitude() + maxVelocity ) );

                // Check that the velocity won't move the point outside of the container.
                if ( containerRect.contains( position.toPoint2D() ) && !containerRect.contains( position.getAddedInstance( velocity.getScaledInstance( dt ) ).toPoint2D() ) ) {
                    velocity.setMagnitude( 0 );
                }
            }

            // Update the position.
            position.add( velocity.getScaledInstance( dt ) );
        }
    }
}


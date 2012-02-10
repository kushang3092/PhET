// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.tests.model;

import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;
import fj.data.List;

import java.awt.geom.Area;

import static fj.Function.curry;
import static fj.Ord.ord;

/**
 * Immutable model representing the entire state at one instant, including the number and location of slices
 *
 * @author Sam Reid
 */
public class PieSetState {
    public final int numerator;
    public final int denominator;
    public final List<Slice> cells;
    public final List<MovableSlice> slices;

    public final List<Slice> emptyCells;

    public PieSetState( int numerator, int denominator, List<Slice> cells, List<MovableSlice> slices ) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.cells = cells;
        this.slices = slices;

        //TODO: should this be eager or lazy?
        this.emptyCells = cells.filter( new F<Slice, Boolean>() {
            @Override public Boolean f( Slice slice ) {
                return !cellFilled( slice );
            }
        } );
    }

    public PieSetState stepInTime() {
        final List<MovableSlice> slices = this.slices.map( new F<MovableSlice, MovableSlice>() {
            public MovableSlice f( final MovableSlice s ) {
                if ( s.dragging ) {

                    //TODO: make this minimum function a bit cleaner please?
                    Slice closest = emptyCells.minimum( ord( curry( new F2<Slice, Slice, Ordering>() {
                        public Ordering f( final Slice u1, final Slice u2 ) {
                            return Ord.<Comparable>comparableOrd().compare( u1.center.distance( s.center ), u2.center.distance( s.center ) );
                        }
                    } ) ) );

                    //Account for winding number
                    double closestAngle = closest.angle;
                    if ( Math.abs( closestAngle - s.angle ) > Math.PI ) {
                        if ( closestAngle > s.angle ) { closestAngle -= 2 * Math.PI; }
                        else if ( closestAngle < s.angle ) { closestAngle += 2 * Math.PI; }
                    }
                    double delta = closestAngle - s.angle;
                    final MovableSlice rotated = s.angle( s.angle + delta / 6 );//Xeno effect

                    //Keep the center in the same place
                    return rotated.translate( s.center.minus( rotated.center ) );
                }
                else {
                    return s;
                }
            }
        } );
        return slices( slices );
    }

    public PieSetState slices( List<MovableSlice> slices ) { return new PieSetState( numerator, denominator, cells, slices ); }

    public boolean cellFilled( final Slice cell ) {
        return slices.exists( new F<MovableSlice, Boolean>() {
            public Boolean f( MovableSlice m ) {
                return m.container == cell;
            }
        } );
    }

    //Find which cell a slice should get dropped into 
    public Slice getDropTarget( final MovableSlice s ) {
        final Slice closestCell = emptyCells.minimum( ord( curry( new F2<Slice, Slice, Ordering>() {
            public Ordering f( final Slice u1, final Slice u2 ) {
                return Ord.<Comparable>comparableOrd().compare( u1.center.distance( s.center ), u2.center.distance( s.center ) );
            }
        } ) ) );

        //Only allow it if the shapes actually overlapped
        return closestCell != null && !( new Area( closestCell.shape ) {{intersect( new Area( s.shape ) );}}.isEmpty() ) ? closestCell : null;
    }
}
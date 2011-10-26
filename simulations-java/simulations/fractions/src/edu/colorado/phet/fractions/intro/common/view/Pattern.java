// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.common.view;

import fj.data.List;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class Pattern {
    public final List<? extends Shape> shapes;

    public Pattern( List<? extends Shape> shapes ) {
        this.shapes = shapes;
    }

    public static Rectangle2D.Double square( double x, double y, double length ) {
        return new Rectangle2D.Double( x, y, length, length );
    }

    public static class NineGrid extends Pattern {
        public NineGrid() {
            super( List.iterableList( new ArrayList<Shape>() {{
                double length = 20;
                for ( int i = 0; i < 3; i++ ) {
                    for ( int j = 0; j < 3; j++ ) {
                        add( square( i * length, j * length, length ) );
                    }
                }
            }} ) );
        }
    }

//    public static class SixPlusses extends Pattern {
//
//    }
}
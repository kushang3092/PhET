/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model.spline;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:03:37 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public abstract class AbstractSpline implements Cloneable {
    private ArrayList points = new ArrayList();

    private boolean segmentPathDirty = true;
    private SegmentPath segmentPath = null;

    private boolean generalPathDirty = true;
    private GeneralPath generalPath = null;

    private boolean areaShapeDirty = true;
    private Shape areaShape;
    private boolean areaDirty = true;
    private Area area = null;
    public static final float SPLINE_THICKNESS = 12.0f;
    private AbstractSpline reverseSpline = null;

    public boolean equals( Object obj ) {
        if( obj instanceof AbstractSpline ) {
            AbstractSpline as = (AbstractSpline)obj;
            return as.points.equals( points );
        }
        return false;
    }

    protected Object clone() {
        try {
            AbstractSpline clone = (AbstractSpline)super.clone();
            clone.points = new ArrayList();
            for( int i = 0; i < points.size(); i++ ) {
                Point2D.Double aDouble = (Point2D.Double)points.get( i );
                clone.points.add( new Point2D.Double( aDouble.getX(), aDouble.getY() ) );
            }
            clone.segmentPath = new SegmentPath();
            clone.generalPath = new GeneralPath();
            clone.areaShape = null;

            clone.generalPathDirty = true;
            clone.areaShapeDirty = true;

            return clone;
        }
        catch( CloneNotSupportedException e ) {
            throw new RuntimeException( e );
        }
    }

    public abstract AbstractSpline copySpline();

    protected AbstractSpline() {
        setAllDirty();
    }

    public void addControlPoint( Point2D point ) {
        points.add( point );
        setAllDirty();
    }

    private void setAllDirty() {
        segmentPathDirty = true;
        generalPathDirty = true;
        areaShapeDirty = true;
        areaDirty = true;
//        System.out.println( "All dirty @ t=" + System.currentTimeMillis() );
    }

    public void addControlPoint( double x, double y ) {
        addControlPoint( new Point2D.Double( x, y ) );
    }

    public abstract Point2D[] getInterpolationPoints();

    public SegmentPath getSegmentPath() {
        if( segmentPathDirty ) {
            this.segmentPath = constructSegmentPath();
            segmentPathDirty = false;
        }
        return segmentPath;
    }

    private SegmentPath constructSegmentPath() {
        Point2D[] interp = getInterpolationPoints();
        SegmentPath path = new SegmentPath();
        for( int i = 0; i < interp.length - 1; i++ ) {
            path.addSegment( new Segment( interp[i], interp[i + 1], SPLINE_THICKNESS ) );
        }
        return path;
    }

    public Point2D evaluate( double distAlongSpline ) {
        return getSegmentPath().evaluate( distAlongSpline );
    }

    public Point2D[] getControlPoints() {
        return (Point2D[])points.toArray( new Point2D.Double[0] );
    }

    public GeneralPath getInterpolationPath() {
        if( generalPathDirty ) {
            this.generalPath = createInterpolationPath();
            generalPathDirty = false;
        }
        return generalPath;
    }

    private GeneralPath createInterpolationPath() {

        Point2D[] pts = getInterpolationPoints();
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( pts[0].getX(), pts[0].getY() );
        for( int i = 1; i < pts.length; i++ ) {
            path.lineTo( pts[i] );
        }
        return path.getGeneralPath();
    }

    public int numControlPoints() {
        return points.size();
    }

    public Point2D controlPointAt( int i ) {
        return (Point2D)points.get( i );
    }

    public void translateControlPoint( int index, double x, double y ) {
        Point2D.Double pt = (Point2D.Double)points.get( index );
        pt.x += x;
        pt.y += y;
        setAllDirty();
        //todo notify this moved.
    }

    public Area getArea() {
        if( areaDirty ) {
            area = new Area( createAreaShape() );
            areaDirty = false;
        }
        return area;
    }

    public Shape getAreaShape() {
        if( areaShapeDirty ) {
            areaShape = createAreaShape();
            areaShapeDirty = false;
        }
        return areaShape;
    }

    private Shape createAreaShape() {
        BasicStroke stroke = new BasicStroke( SPLINE_THICKNESS );
        return stroke.createStrokedShape( getInterpolationPath() );
    }

    public void printControlPointCode() {
        AbstractSpline spline = this;
        for( int i = 0; i < spline.numControlPoints(); i++ ) {
            System.out.println( "spline.addControlPoint(" + (int)spline.controlPointAt( i ).getX() + "," + (int)spline.controlPointAt( i ).getY() + ");" );
        }
    }

    public abstract AbstractSpline createReverseSpline();

    public void setControlPoints( Point2D[] controlPoints ) {
        points.clear();
        for( int i = 0; i < controlPoints.length; i++ ) {
            Point2D controlPoint = controlPoints[i];
            addControlPoint( controlPoint );
        }
        setAllDirty();
    }

    public void translate( double dx, double dy ) {
        for( int i = 0; i < points.size(); i++ ) {
            Point2D.Double aDouble = (Point2D.Double)points.get( i );
            aDouble.x += dx;
            aDouble.y += dy;
        }
        setAllDirty();
    }

    public void removeControlPoint( int index ) {
        points.remove( index );
        setAllDirty();
    }

    public void setReverseSpline( AbstractSpline spline ) {
        this.reverseSpline = spline;
    }
}

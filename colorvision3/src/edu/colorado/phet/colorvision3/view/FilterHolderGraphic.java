/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 *   $Source$
 *   $Revision$ on branch $Name$
 *   Modified by $Author$ on $Date$
 */

package edu.colorado.phet.colorvision3.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

/**
 * FilterHolderGraphic
 *
 * @author cmalley
 * @version $Revision$
 */
public class FilterHolderGraphic extends PhetShapeGraphic
{
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------
  
  // Location, relative to upper-left corner of bounding box.
  private Point _location;

	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------
  
  public FilterHolderGraphic( Component component )
  {
    super( component, null, null );
    
    //  Request antialiasing.
    RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    super.setRenderingHints( hints );
    
    super.setPaint( Color.DARK_GRAY );

    // Use constructive area geometry to create the holder shape.
    Area area = new Area();
    Rectangle2D r1 = new Rectangle2D.Double( 0, 0, 5, 10 ); // left
    Rectangle2D r2 = new Rectangle2D.Double( 5, 5, 10, 20 ); // center
    Rectangle2D r3 = new Rectangle2D.Double( 15, 0, 5, 10 ); // right
    area.add( new Area(r1) );
    area.add( new Area(r2) );
    area.add( new Area(r3) );
    super.setShape( area );
    
    setLocation( 0, 0 );
  }
  
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Sets the location.
   * 
   * @param location the location
   */
  public void setLocation( Point location )
  { 
    if ( _location != null )
    {
      super.translate( -_location.x, -_location.y );
    }
    _location = location;
    super.translate( location.x, location.y );
  }
  
  /**
   * Convenience method for setting the location.
   * 
   * @param x X coordinate
   * @param y Y coordinate
   */
  public void setLocation( int x, int y )
  {
    this.setLocation( new Point( x, y ) );
  }
  
  /**
   * Gets the location.
   */
  public Point getLocation()
  {
    return _location;
  }
  
	//----------------------------------------------------------------------------
	// Rendering
  //----------------------------------------------------------------------------

  /**
   * Draws the pipe assembly.
   * 
   * @param g2 graphics context
   */
  public void paint( Graphics2D g2 )
  {
    if ( super.isVisible() )
    {
      super.paint( g2 );
      BoundsOutline.paint( g2, this ); // DEBUG
    }
  }
  
}


/* end of file */
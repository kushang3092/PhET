package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

/**
 * DebuggerGraphic displays the bounds and locations of a set of PhetGraphics.
 * It is intended for use in debugging phetcommon and client applications.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DebuggerGraphic extends PhetGraphic {
    
    private boolean _boundsEnabled;
    private Color _boundsColor;
    private BasicStroke _boundsStroke;
    private boolean _locationEnabled;
    private Color _locationColor;
    private BasicStroke _locationStroke;
    private Dimension _locationSize;
    private Hashtable _specifications; // key=PhetGraphic, value=Specification
    
    /** Rendering specification, tells us how to draw bounds & location. */
    private static class Specification {
        public Color boundsColor;
        public Color locationColor;
        public Specification( Color boundsColor, Color locationColor ) {
            this.boundsColor = boundsColor;
            this.locationColor = locationColor;
        }
    }
    
    public DebuggerGraphic( Component component ) {
        super( component );
        assert( component != null );
        _boundsEnabled = true;
        _boundsColor = Color.BLUE;
        _boundsStroke = new BasicStroke( 1f );
        _locationEnabled = true;
        _locationColor = Color.RED;
        _locationStroke = new BasicStroke( 2f );
        _locationSize = new Dimension( 10, 10 );
        _specifications = new Hashtable();
    }
    
    public void add( PhetGraphic graphic ) {
        add( graphic, _boundsColor, _locationColor );
    }
    
    public void add( PhetGraphic graphic, Color boundsColor, Color locationColor ) {
        assert( graphic != null );
        assert( boundsColor != null );
        assert( locationColor != null );
        _specifications.put( graphic, new Specification( boundsColor, locationColor ) );
    }
    
    public void remove( PhetGraphic graphic ) {
        assert( graphic != null );
        _specifications.remove( graphic);
    }
    
    public void setBoundsColor( Color boundsColor ) {
        assert( boundsColor != null );
        _boundsColor = boundsColor;
    }
    
    public Color getBoundsColor() {
        return _boundsColor;
    }
    
    public void setBoundsStrokeWidth( float width ) {
        _boundsStroke = new BasicStroke( width );
    }
    
    public float getBoundsStrokeWidth() {
        return _boundsStroke.getLineWidth();
    }
    
    public void setLocationColor( Color locationColor ) {
        assert( locationColor != null );
        _locationColor = locationColor;
    }
    
    public Color getLocationColor() {
        return _locationColor;
    }
    
    public void setLocationStrokeWidth( float width ) {
        _locationStroke = new BasicStroke( width );
    }
    
    public float getLocationStrokeWidth() {
        return _locationStroke.getLineWidth();
    }
    
    public void setLocationSize( Dimension size ) {
        assert( size != null );
        setLocationSize( size.width, size.height );
    }
    
    public void setLocationSize( int width, int height ) {
        _locationSize = new Dimension( width, height );
    }
    
    public Dimension getLocationSize() {
        return new Dimension( _locationSize );
    }
    
    public void setBoundsEnabled( boolean enabled ) {
        _boundsEnabled = enabled;
    }
    
    public boolean isBoundsEnabled() {
        return _boundsEnabled;
    }
    
    public void setLocationEnabled( boolean enabled ) {
        _locationEnabled = enabled;
    }
    
    public boolean isLocationEnabled() {
        return _locationEnabled;
    }
    
    protected Rectangle determineBounds() {
        Rectangle bounds = null;
        Enumeration e = _specifications.keys();
        while ( e.hasMoreElements() ) {
            PhetGraphic graphic = (PhetGraphic) e.nextElement();
            if ( bounds == null ) {
                bounds = new Rectangle();
            }
            bounds.union( graphic.getBounds() );
        }
        return bounds;
    }

    public void paint( Graphics2D g2 ) {
        assert( g2 != null );
        if ( isVisible() && ( isBoundsEnabled() || isLocationEnabled() )) {
            saveGraphicsState( g2 );
            Enumeration e = _specifications.keys();
            while ( e.hasMoreElements() ) {
                // Get the rendering details for the next graphic.
                PhetGraphic graphic = (PhetGraphic) e.nextElement();
                Rectangle bounds = graphic.getBounds();
                Point location = graphic.getLocation();
                Specification s = (Specification)_specifications.get( graphic );
                
                // Outline the bounds.
                if ( isBoundsEnabled() ) {
                    g2.setStroke( _boundsStroke );
                    g2.setPaint( s.boundsColor );
                    g2.draw( bounds );
                }
                
                // Draw a cross, centered at the graphic's location.
                if ( isLocationEnabled() ) {
                    g2.setStroke( _locationStroke );
                    g2.setPaint( s.locationColor );
                    g2.drawLine( location.x, location.y - _locationSize.height/2, location.x, location.y + _locationSize.height/2 );
                    g2.drawLine( location.x - _locationSize.width/2, location.y, location.x + _locationSize.width/2, location.y );
                }
            }
            restoreGraphicsState();
        }
    }
}
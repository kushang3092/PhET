/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.RenderingHints;

import javax.swing.JLabel;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;


/**
 * SubscriptedSymbol is the graphical representation of a symbol with a subscript.
 * <p>
 * Rendering of subscripted symbols proved to be problematic in Fourier.
 * Three implementations are provided here as inner classes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SubscriptedSymbol extends CompositePhetGraphic {
    
    ISubscriptedSymbol _graphic;
    
    public SubscriptedSymbol( Component component, String symbol, String subscript, Font font, Color color ) {
        super( component );
        _graphic = new HTMLGraphicSubscriptedSymbol( component, symbol, subscript, font, color );
//        _graphic = new PhetJComponentSubscriptedSymbol( component, symbol, subscript, font, color );
//        _graphic = new PhetTextGraphicSubscriptedSymbol( component, symbol, subscript, font, color );
        addGraphic( (PhetGraphic) _graphic );
    }
    
    public void setLabel( String symbol, String subscript ) {
        _graphic.setLabel( symbol, subscript );
    }
    
    public void setColor( Color color ) {
        _graphic.setColor( color );
    }
    
    public void setFont( Font font ) {
        _graphic.setFont( font );
    }
    
    //----------------------------------------------------------------------------
    // Interface for all implementations
    //----------------------------------------------------------------------------
    
    private interface ISubscriptedSymbol {
        public void setLabel( String symbol, String subscript );
        public void setColor( Color color );
        public void setFont( Font font );
    }
    
    //----------------------------------------------------------------------------
    // HTMLGraphic implementation
    //----------------------------------------------------------------------------
    
    private static class HTMLGraphicSubscriptedSymbol extends HTMLGraphic implements ISubscriptedSymbol {

        public HTMLGraphicSubscriptedSymbol( Component component, String symbol, String subscript, Font font, Color color ) {
            super( component, font, "", color );
            setLabel( symbol, subscript );
        }

        public void setLabel( String symbol, String subscript ) {
            String string = "<html>" + symbol + "<sub>" + subscript + "</sub></html>";
            setHTML( string );
            centerRegistrationPoint();
        }
        
        // setFont and setColor are provided by HTMLGraphic
    }
    
    //----------------------------------------------------------------------------
    // PhetJComponent implementation
    //
    // Pros: uses standard JLabel interface for HTML
    // Cons: looks the worst, especially when scaled
    //----------------------------------------------------------------------------
    
    private static class PhetJComponentSubscriptedSymbol extends CompositePhetGraphic implements ISubscriptedSymbol {

        private PhetGraphic _labelGraphic;
        private JLabel _label;
        
        public PhetJComponentSubscriptedSymbol( Component component, String symbol, String subscript, Font font, Color color ) {
            super( component );

            setRenderingHints( new RenderingHints( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC ) );
            
            _label = new JLabel();
            _label.setFont( font );
            _label.setForeground( color );
            _label.setBackground( new Color( 255, 255, 255, 0 ) );
            _labelGraphic = PhetJComponent.newInstance( component, _label );
            addGraphic( _labelGraphic );
            
            setLabel( symbol, subscript );
        }
        
        public void setLabel( String symbol, String subscript ) {
            String string = "<html>" + symbol + "<sub>" + subscript + "</sub></html>";
            _label.setText( string );
            centerRegistrationPoint();
        }
        
        public void setFont( Font font ) {
            _label.setFont( font );
        }
        
        public void setColor( Color color ) {
            _label.setForeground( color );
        }
    }
    
    //----------------------------------------------------------------------------
    // PhetTextGraphic implementation
    // 
    // Pros: looks the best, even when scaled
    // Cons: Unicode string causes screen turds on Macintosh
    //----------------------------------------------------------------------------
    
    public class PhetTextGraphicSubscriptedSymbol extends CompositePhetGraphic implements ISubscriptedSymbol {
        
        private PhetTextGraphic _symbolGraphic, _subscriptGraphic;

        public PhetTextGraphicSubscriptedSymbol( Component component, String symbol, String subscript, Font font, Color color ) {
            super( component );

            _symbolGraphic = new PhetTextGraphic( component, font, "", color );
            addGraphic( _symbolGraphic );

            _subscriptGraphic = new PhetTextGraphic( component, font, "", color );
            addGraphic( _subscriptGraphic );

            setLabel( symbol, subscript );
        }

        public void setLabel( String symbol, String subscript ) {
            _symbolGraphic.setText( symbol );
            _symbolGraphic.setJustification( PhetTextGraphic.SOUTH_EAST );
            _symbolGraphic.setLocation( 0, 0 );
            
            _subscriptGraphic.setText( subscript );
            _subscriptGraphic.setJustification( PhetTextGraphic.WEST );
            _subscriptGraphic.setLocation( 0, 0 );
        }
        
        public void setFont( Font font ) {
            _symbolGraphic.setFont( font );
            _subscriptGraphic.setFont( font );
        }
        
        public void setColor( Color color ) {
            _symbolGraphic.setColor( color );
            _subscriptGraphic.setColor( color );
        }
    }

}

/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.util;

import java.awt.Color;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * A check box whose text is HTML.  Swing doesn't properly handle the "graying out"
 * of text for JComponents that use HTML strings. See Unfuddle #1704.  This is a 
 * quick-and-dirty workaround for one type of JComponent. A more general solution 
 * is needed - or better yet, a Java fix.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HTMLCheckBox extends JCheckBox {
    
    private static final Color DEFAULT_DISABLED_COLOR = Color.GRAY;

    private Color foreground, disabledColor;
    
    public HTMLCheckBox( String text ) {
        this( text, DEFAULT_DISABLED_COLOR );
    }
    
    public HTMLCheckBox( String text, Color disabledColor ) {
        super( HTMLUtils.toHTMLString( text ) );
        this.foreground = getForeground();
        this.disabledColor = disabledColor;
    }
    
    public void setForeground( Color foreground ) {
        this.foreground = foreground;
        if ( isEnabled() ) {
            super.setForeground( foreground );
        }
    }
    
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        super.setForeground( enabled ? foreground : disabledColor );
    }
}

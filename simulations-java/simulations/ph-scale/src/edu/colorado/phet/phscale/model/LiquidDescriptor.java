/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.phscale.PHScaleStrings;


public class LiquidDescriptor {
    
    // water is a special singleton
    public static class WaterDescriptor extends LiquidDescriptor {
        private WaterDescriptor() {
            super(  PHScaleStrings.CHOICE_WATER, 7, new Color( 255, 255, 255, 100 ) );
        }
    }
    public static final WaterDescriptor WATER = new WaterDescriptor();
    
    // singletons for each liquid type
    public static final LiquidDescriptor MILK = new LiquidDescriptor( PHScaleStrings.CHOICE_MILK, 6.5, Color.WHITE );
    public static final LiquidDescriptor BEER = new LiquidDescriptor( PHScaleStrings.CHOICE_BEER, 4.5, new Color( 185, 79, 5 ) );
    public static final LiquidDescriptor COLA = new LiquidDescriptor( PHScaleStrings.CHOICE_COLA, 2.5, new Color( 122, 60, 35 ) );
    public static final LiquidDescriptor LEMON_JUICE = new LiquidDescriptor( PHScaleStrings.CHOICE_LEMON_JUICE, 2.4, Color.YELLOW );
    
    // all choices except water
    private static final LiquidDescriptor[] CHOICES = new LiquidDescriptor[] { MILK, BEER, COLA, LEMON_JUICE };
    
    public static LiquidDescriptor[] getChoices() {
        return CHOICES;
    }
    
    private static final DecimalFormat PH_FORMAT = new DecimalFormat( "0.0" );
    
    private final String _name;
    private final double _pH;
    private final Color _color;
    private final java.util.ArrayList _listeners;

    protected LiquidDescriptor( String name, double pH, Color color ) {
        _name = name;
        _pH = pH;
        _color = color;
        _listeners = new ArrayList();
    }
    
    public String getName() {
        return _name;
    }
    
    public double getPH() {
        return _pH;
    }
    
    public Color getColor() {
        return _color;
    }
    
    public String toString() { 
        return _name + " (" + PH_FORMAT.format( _pH ) + ")";
    }
}

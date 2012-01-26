// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.molarity;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.ColorRange;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.dilutions.MolarityResources.Strings;
import edu.colorado.phet.dilutions.MolarityResources.Symbols;
import edu.colorado.phet.dilutions.common.model.Solute;
import edu.colorado.phet.dilutions.common.model.Solution;
import edu.colorado.phet.dilutions.common.model.Solvent.Water;

/**
 * Model for the "Molarity" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityModel implements Resettable {

    private static final DoubleRange SOLUTE_AMOUNT_RANGE = new DoubleRange( 0, 1, 0.5 ); // moles
    private static final DoubleRange SOLUTION_VOLUME_RANGE = new DoubleRange( 0.2, 1, 0.5 ); // liters
    private static final DoubleRange CONCENTRATION_RANGE = new DoubleRange( SOLUTE_AMOUNT_RANGE.getMin() / SOLUTION_VOLUME_RANGE.getMax(),
                                                                            SOLUTE_AMOUNT_RANGE.getMax() / SOLUTION_VOLUME_RANGE.getMin() );

    static {
        assert ( SOLUTION_VOLUME_RANGE.getMin() > 0 );
    }

    private final ArrayList<Solute> solutes; // the supported set of solutes
    public final Solution solution;

    public MolarityModel() {

        // solutes, in rainbow (ROYBIV) order
        this.solutes = new ArrayList<Solute>() {{
            add( new Solute( Strings.KOOL_AID, Symbols.KOOL_AID, 5.0, new ColorRange( new Color( 255, 225, 225 ), Color.RED ), 5, 200 ) );
            add( new Solute( Strings.COBALT_II_NITRATE, Symbols.COBALT_II_NITRATE, 5.0, new ColorRange( new Color( 255, 225, 225 ), Color.RED ), 5, 200 ) );
            add( new Solute( Strings.COBALT_CHLORIDE, Symbols.COBALT_CHLORIDE, 4.35, new ColorRange( new Color( 255, 242, 242 ), new Color( 0xFF6A6A ) ), 5, 200 ) );
            add( new Solute( Strings.POTASSIUM_DICHROMATE, Symbols.POTASSIUM_DICHROMATE, 0.50, new ColorRange( new Color( 255, 232, 210 ), new Color( 0xFF7F00 ) ), 5, 200 ) );
            add( new Solute( Strings.GOLD_III_CHLORIDE, Symbols.GOLD_III_CHLORIDE, 2.25, new ColorRange( new Color( 255, 255, 199 ), new Color( 0xFFD700 ) ), 5, 200 ) );
            add( new Solute( Strings.POTASSIUM_CHROMATE, Symbols.POTASSIUM_CHROMATE, 3.35, new ColorRange( new Color( 255, 255, 199 ), Color.YELLOW ), 5, 200 ) );
            add( new Solute( Strings.NICKEL_II_CHLORIDE, Symbols.NICKEL_II_CHLORIDE, 5.0, new ColorRange( new Color( 234, 244, 234 ), new Color( 0x008000 ) ), 5, 200 ) );
            add( new Solute( Strings.COPPER_SULFATE, Symbols.COPPER_SULFATE, 1.40, new ColorRange( new Color( 222, 238, 255 ), new Color( 0x1E90FF ) ), 5, 200 ) );
            add( new Solute( Strings.POTASSIUM_PERMANGANATE, Symbols.POTASSIUM_PERMANGANATE, 0.50, new ColorRange( new Color( 255, 0, 255 ), new Color( 0x8B008B ) ), Color.BLACK, 5, 200 ) );
        }};
        for ( Solute solute : solutes ) {
            assert ( CONCENTRATION_RANGE.contains( solute.saturatedConcentration ) );
        }

        this.solution = new Solution( new Water(), solutes.get( 0 ), SOLUTE_AMOUNT_RANGE.getDefault(), SOLUTION_VOLUME_RANGE.getDefault() );
    }

    public ArrayList<Solute> getSolutes() {
        return new ArrayList<Solute>( solutes );
    }

    public DoubleRange getSoluteAmountRange() {
        return SOLUTE_AMOUNT_RANGE;
    }

    public DoubleRange getSolutionVolumeRange() {
        return SOLUTION_VOLUME_RANGE;
    }

    public DoubleRange getConcentrationRange() {
        return CONCENTRATION_RANGE;
    }

    public void reset() {
        solution.reset();
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.dilution.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.model.Solute;
import edu.colorado.phet.dilutions.common.model.Solution;

/**
 * Model for the "Dilution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionModel implements Resettable {

    private static final double MAX_BEAKER_VOLUME = 1; // liter
    private static final DoubleRange SOLUTE_AMOUNT_RANGE = new DoubleRange( 0, 0.2, 0.1 ); // moles
    private static final DoubleRange SOLUTION_VOLUME_RANGE = new DoubleRange( 0.0, 0.2, 0.1 ); // liters
    private static final DoubleRange DILUTION_VOLUME_RANGE = new DoubleRange( 0.2, 1, 0.5 ); // liters
    private static final DoubleRange CONCENTRATION_RANGE = new DoubleRange( SOLUTE_AMOUNT_RANGE.getMin() / DILUTION_VOLUME_RANGE.getMax(),
                                                                            SOLUTE_AMOUNT_RANGE.getMax() / DILUTION_VOLUME_RANGE.getMin() ); // M

    static {
        assert ( SOLUTION_VOLUME_RANGE.getMax() <= MAX_BEAKER_VOLUME );
        assert ( DILUTION_VOLUME_RANGE.getMax() <= MAX_BEAKER_VOLUME );
        assert ( DILUTION_VOLUME_RANGE.getMin() > 0 );
    }

    public final Solute solute;
    public final Solution solution, dilution, water;

    public DilutionModel() {
        this.solute = new Solute( Strings.SOLUTE, "?", CONCENTRATION_RANGE.getMax(), Color.BLUE, 5, 200 ); // hypothetical solute with unknown formula
        this.solution = new Solution( solute, SOLUTE_AMOUNT_RANGE.getDefault(), SOLUTION_VOLUME_RANGE.getDefault() );
        this.dilution = new Solution( solute, SOLUTE_AMOUNT_RANGE.getDefault(), DILUTION_VOLUME_RANGE.getDefault() );
        this.water = new Solution( solute, 0, DILUTION_VOLUME_RANGE.getDefault() );
    }

    public double getMaxBeakerVolume() {
        return MAX_BEAKER_VOLUME;
    }

    public DoubleRange getSolutionVolumeRange() {
        return SOLUTION_VOLUME_RANGE;
    }

    public DoubleRange getDiutionVolumeRange() {
        return DILUTION_VOLUME_RANGE;
    }

    public DoubleRange getConcentrationRange() {
        return CONCENTRATION_RANGE;
    }

    public void reset() {
        solution.reset();
        dilution.reset();
        water.reset();
    }
}

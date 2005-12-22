/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model;

import edu.colorado.phet.dischargelamps.model.DefaultEnergyEmissionStrategy;
import edu.colorado.phet.dischargelamps.model.DischargeLampElementProperties;
import edu.colorado.phet.dischargelamps.model.EnergyAbsorptionStrategy;
import edu.colorado.phet.lasers.model.atom.EnergyEmissionStrategy;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * Zinc
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Copper extends DischargeLampElementProperties {
    private static final String NAME = SimStrings.get("Element.Copper");
    private static final double[] ENERGY_LEVELS = new double[]{-13.6};
    private static final EnergyEmissionStrategy ENERGY_EMISSION_STRATEGY = new DefaultEnergyEmissionStrategy();
    private static final double WORK_FUNCTION = 4.7;
    private static final EnergyAbsorptionStrategy ENERGY_ABSORPTION_STRATEGY = new MetalEnergyAbsorptionStrategy( WORK_FUNCTION );

    public Copper() {
        super( NAME, ENERGY_LEVELS, ENERGY_EMISSION_STRATEGY, ENERGY_ABSORPTION_STRATEGY );
        setWorkFunction( WORK_FUNCTION );
    }
}

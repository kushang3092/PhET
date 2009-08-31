package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.neuron.NeuronStrings;

public class PotassiumIon extends Atom {

	@Override
	public String getChemicalSymbol() {
		return NeuronStrings.POTASSIUM_CHEMICAL_SYMBOL;
	}

	@Override
	public Color getRepresentationColor() {
		return Color.RED;
	}

	@Override
	public int getCharge() {
		return -1;
	}
}

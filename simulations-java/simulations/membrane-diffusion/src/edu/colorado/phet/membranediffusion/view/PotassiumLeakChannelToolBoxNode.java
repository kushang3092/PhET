/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.membranediffusion.model.MembraneChannel;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionModel;
import edu.colorado.phet.membranediffusion.model.PotassiumLeakageChannel;
import edu.colorado.phet.membranediffusion.model.SodiumLeakageChannel;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that goes in the membrane channel tool box and allows users to add
 * potassium leak channels to the membrane.
 * 
 * @author John Blanco
 */
public class PotassiumLeakChannelToolBoxNode extends ToolBoxItem {

	public PotassiumLeakChannelToolBoxNode(MembraneDiffusionModel model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected MembraneChannel createModelElement( Point2D position ) {
	    return new PotassiumLeakageChannel(getModel(), getModel().getHodgkinHuxleyModel());
	}

	@Override
	protected void initializeSelectionNode() {
		MembraneChannel channel = new PotassiumLeakageChannel();
		PNode representation = new MembraneChannelNode(channel, SCALING_MVT);
		setSelectionNode(representation);
	}
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.ModelRepresentation;

/**
 * "Model" control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ModelControlPanel extends MPControlPanel {

    public ModelControlPanel( final Property<ModelRepresentation> modelRepresentation ) {
        super( MPStrings.MODEL );
        add( new GridPanel() {{
            setGridX( 0 ); // vertical
            setAnchor( Anchor.WEST ); // left justified
            add( new PropertyRadioButton<ModelRepresentation>( MPStrings.BALL_AND_STICK, modelRepresentation, ModelRepresentation.BALL_AND_STICK ) );
            add( new PropertyRadioButton<ModelRepresentation>( MPStrings.ELECTROSTATIC_POTENTIAL, modelRepresentation, ModelRepresentation.ELECTROSTATIC_POTENTIAL ) );
            add( new PropertyRadioButton<ModelRepresentation>( MPStrings.ELECTRON_DENSITY, modelRepresentation, ModelRepresentation.ELECTRON_DENSITY ) );
        }} );
    }
}

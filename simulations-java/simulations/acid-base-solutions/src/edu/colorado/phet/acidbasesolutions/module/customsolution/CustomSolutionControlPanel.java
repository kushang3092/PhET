/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.customsolution;

import edu.colorado.phet.acidbasesolutions.controls.CustomSolutionControl;
import edu.colorado.phet.acidbasesolutions.controls.ToolsControl;
import edu.colorado.phet.acidbasesolutions.controls.ViewControl;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * Control panel for the "Custom Solution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CustomSolutionControlPanel extends ControlPanel {

    public CustomSolutionControlPanel( Resettable resettable, ABSModel model ) {
        addControlFullWidth( new CustomSolutionControl( model ) );
        addControlFullWidth( new ToolsControl( model ) );
        addControlFullWidth( new ViewControl( model ) );
        addResetAllButton( resettable );
    }
}

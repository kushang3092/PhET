package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.util.ESPPaintImmediateDialog;

/**
 * Author: Sam Reid
 * Jun 29, 2007, 11:12:57 PM
 */
public class BarChartDialog extends ESPPaintImmediateDialog {
    private BarGraphCanvas barGraphCanvas;

    public BarChartDialog( PhetFrame phetFrame, String title, boolean modal, EnergySkateParkModule module ) {
        super( phetFrame, title, modal );
        barGraphCanvas = new BarGraphCanvas( module );
        setContentPane( barGraphCanvas );
    }

    public void reset() {
        setVisible( false );
        barGraphCanvas.reset();
    }
}

package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.util.ESPPaintImmediateDialog;

/**
 * Author: Sam Reid
 * Jun 29, 2007, 11:17:25 PM
 */
public class EnergyPositionPlotDialog extends ESPPaintImmediateDialog {
    private EnergyPositionPlot energyPosition;

    public EnergyPositionPlotDialog( PhetFrame phetFrame, String title, boolean modal, EnergySkateParkModule energySkateParkModule ) {
        super( phetFrame, title, modal );
        energyPosition = new EnergyPositionPlot( energySkateParkModule );
        setContentPane( energyPosition );
    }

    public void setVisible( boolean b ) {
        super.setVisible( b );
        energyPosition.reset();
    }

    public void reset() {
        energyPosition.reset();
        setVisible( false );
    }
}

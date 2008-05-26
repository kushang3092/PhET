package edu.colorado.phet.dischargelamps.view;

import edu.colorado.phet.lasers.view.AbstractLegend;

/**
 * Created by: Sam
 * May 25, 2008 at 11:23:40 PM
 */
public class DischargeLampsLegend extends AbstractLegend {
    public DischargeLampsLegend() {
        addForKey( getAtomImage(), "Legend.atom" );
        addForKey( getElectronImage(), "Legend.electron" );
//        addForKey( getPhotonImage( 400 ), "Legend.photon" );
        add3PhotonLegendItems();
    }
}

// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view.menu;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.SettableProperty;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {

    public OptionsMenu() {
        super( PhetCommonResources.getString( "Common.OptionsMenu" ) );
        setMnemonic( PhetCommonResources.getChar( "Common.OptionsMenu.mnemonic", 'O' ) );
    }

    /**
     * Adds a JCheckBoxMenu item that allows the user to select whether the sim should be shown with a white background.
     * This is used primarily in sims with black backgrounds to make it easier to make printouts.
     *
     * @param whiteBackgroundProperty the Property<Boolean> with which to synchronize.
     */
    public void addWhiteBackgroundCheckBoxMenuItem( SettableProperty<Boolean> whiteBackgroundProperty ) {
        add( new PropertyCheckBoxMenuItem( PhetCommonResources.getString( "Common.WhiteBackground" ), whiteBackgroundProperty ) );
    }
}

/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 25, 2006
 * Time: 12:30:28 AM
 * Copyright (c) Mar 25, 2006 by Sam Reid
 */

public class ReducedScreenControlPanel extends VerticalLayoutPanel {
    private ScreenNode screenNode;

    public ReducedScreenControlPanel( final ScreenNode screenNode ) {
        this.screenNode = screenNode;
        final JCheckBox enabled = new JCheckBox( "Show Screen", screenNode.isScreenEnabled() );
        enabled.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                screenNode.setScreenEnabled( enabled.isSelected() );
            }
        } );
        add( enabled );
    }
}

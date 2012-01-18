// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;

/**
 * CheckBoxMenuItem used in phetcommon for transmitting data on student usage of menus, see #3144
 *
 * @author Sam Reid
 */
public class SimSharingJCheckBoxMenuItem extends JCheckBoxMenuItem {

    private final IUserComponent userComponent;

    public SimSharingJCheckBoxMenuItem( IUserComponent userComponent ) {
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBoxMenuItem( IUserComponent userComponent, Icon icon ) {
        super( icon );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBoxMenuItem( IUserComponent userComponent, String text ) {
        super( text );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBoxMenuItem( IUserComponent userComponent, Action a ) {
        super( a );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBoxMenuItem( IUserComponent userComponent, String text, Icon icon ) {
        super( text, icon );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBoxMenuItem( IUserComponent userComponent, String text, boolean b ) {
        super( text, b );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBoxMenuItem( IUserComponent userComponent, String text, Icon icon, boolean b ) {
        super( text, icon, b );
        this.userComponent = userComponent;
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.checkBoxMenuItem, UserActions.pressed );
        super.fireActionPerformed( event );
    }
}

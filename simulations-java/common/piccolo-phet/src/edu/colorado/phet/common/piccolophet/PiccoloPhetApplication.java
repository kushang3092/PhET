/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14676 $
 * Date modified : $Date:2007-04-17 02:58:50 -0500 (Tue, 17 Apr 2007) $
 */
package edu.colorado.phet.common.piccolophet;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;

/**
 * PiccoloPhetApplication
 * <p/>
 * Allows for the use of components and graphics that require Piccolo support.
 * <p/>
 * Piccolo-dependent items that can be specified:
 * <ul>
 * <li>PhetTabbedPane is used in Module instances. (JTabbedPane can be specified in the constructor, if
 * desired.)
 * </ul>
 *
 * @author Ron LeMaster
 * @version $Revision:14676 $
 */
public class PiccoloPhetApplication extends PhetApplication {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods and inner classes
    //--------------------------------------------------------------------------------------------------

    // Graphical PhetTabbedPanes
    public static final PhetApplication.TabbedPaneType PHET_TABBED_PANE = new PhetApplication.TabbedPaneType() {
        public ITabbedModulePane createTabbedPane() {
            return new TabbedModulePanePiccolo();
        }
    };

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    public PiccoloPhetApplication( PhetApplicationConfig config ) {
        super( config, PHET_TABBED_PANE );
    }

}

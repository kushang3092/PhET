/**
 * Class: IdealGasModule
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.view.util.Animation;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.Strings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.view.BaseIdealGasApparatusPanel;
import edu.colorado.phet.idealgas.view.Box2DGraphic;
import edu.colorado.phet.idealgas.view.Mannequin;
import edu.colorado.phet.idealgas.model.*;

import java.awt.geom.Point2D;
import java.awt.*;
import java.io.IOException;

public class IdealGasModule extends Module {
    private IdealGasModel idealGasModel;
    private PressureSensingBox box;
    private Gravity gravity = new Gravity( 0 );

    public IdealGasModule( AbstractClock clock ) {
        super( Strings.idealGasModuleName );

        // Create the model
        idealGasModel = new IdealGasModel( clock.getDt() );
        setModel( idealGasModel );

//        idealGasModel.addModelElement( gravity );

        // Create the box
        double xOrigin = 132 + IdealGasConfig.X_BASE_OFFSET;
        double yOrigin = 252 + IdealGasConfig.Y_BASE_OFFSET;
        double xDiag = 434 + IdealGasConfig.X_BASE_OFFSET;
        double yDiag = 497 + IdealGasConfig.Y_BASE_OFFSET;
        box = new PressureSensingBox( new Point2D.Double( xOrigin, yOrigin ),
                                      new Point2D.Double( xDiag, yDiag ), idealGasModel, clock );
        idealGasModel.addBox( box );

        // Create the pump
        Pump pump = new Pump( this, box );

        setApparatusPanel( new BaseIdealGasApparatusPanel( this, box, pump ) );

        // Set up the box
        Box2DGraphic boxGraphic = new Box2DGraphic( getApparatusPanel(), box );
        addGraphic( boxGraphic, 10 );

        // Add the animated mannequin
        Mannequin pusher = new Mannequin( getApparatusPanel(), idealGasModel, box );
        addGraphic( pusher, 10 );

        // Set up the control panel
        PhetControlPanel controlPanel = new PhetControlPanel( this, new IdealGasControlPanel( this ));
        setControlPanel( controlPanel );

    }

    public void setCurrentSpecies( Class moleculeClass ) {
        idealGasModel.setCurrentGasSpecies( moleculeClass );
    }

    public void setCmLinesOn( boolean selected ) {
        System.out.println( "not implemented" );
    }

    public void setStove( int value ) {
        System.out.println( "not implemented" );
    }

    public void setGravity( Gravity gravity ) {
        System.out.println( "not implemented" );
    }

    public void setPressureSliceEnabled( boolean selected ) {
        System.out.println( "not implemented" );
    }

    public void setRulerEnabed( boolean selected ) {
        System.out.println( "not implemented" );
    }
}

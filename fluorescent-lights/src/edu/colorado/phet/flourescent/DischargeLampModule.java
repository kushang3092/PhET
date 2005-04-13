/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.flourescent.model.*;
import edu.colorado.phet.flourescent.view.ElectronGraphic;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.view.ResonatingCavityGraphic;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * DischargeLampModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampModule extends BaseLaserModule implements ElectronSource.ElectronProductionListener {
    private ElectronSink anode;
    private ElectronSource cathode;
    private double s_maxSpeed = 0.1;

    private int numEnergyLevels = 5;

    public static boolean DEBUG = false;
    // The scale to apply to graphics created in external applications so they appear properly
    // on the screen
    private double externalGraphicsScale;
    // AffineTransformOp that will scale graphics created in external applications so they appear
    // properly on the screen
    private AffineTransformOp externalGraphicScaleOp;
//    public static boolean DEBUG = true;

    /**
     * Constructor
     * @param clock
     */
    protected DischargeLampModule( AbstractClock clock ) {
        super( SimStrings.get( "ModuleTitle.ModuleA" ), clock );

        // Set up the basic stuff
        ApparatusPanel apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( Color.white );
        setApparatusPanel( apparatusPanel );

        FluorescentLightModel model = new FluorescentLightModel();
        setModel( model );
        setControlPanel( new ControlPanel( this ) );


        // Add the battery and wire graphic
        addCircuitGraphic( apparatusPanel );

        // Add the cathode to the model
        addCathode( model, apparatusPanel );

        // Add the anode to the model
        addAnode( model, apparatusPanel, cathode );

        // Set the cathode to listen for potential changes relative to the anode
        hookCathodeToAnode();

        // Add the tube
        ResonatingCavity tube = addTube( model, apparatusPanel );

        // Add some atoms
        if( DEBUG ) {
            addDebugAtoms( tube );
        }
        else {
            addAtoms( tube );
        }

        // Set up the control panel
        addControls();
    }

    /**
     * Scales an image graphic so it appears properly on the screen. This method depends on the image used by the
     * graphic to have been created at the same scale as the battery-wires graphic. The scale is based on the
     * distance between the electrodes in that image and the screen distance between the electrodes specified
     * in the configuration file.
     * @param imageGraphic
     */
    private void scaleImageGraphic( PhetImageGraphic imageGraphic ) {
        if( externalGraphicScaleOp == null ) {
            int cathodeAnodeScreenDistance = 550;
            determineExternalGraphicScale( FluorescentLightsConfig.ANODE_LOCATION,
                                           FluorescentLightsConfig.CATHODE_LOCATION,
                                           cathodeAnodeScreenDistance );
            AffineTransform scaleTx = AffineTransform.getScaleInstance( externalGraphicsScale, externalGraphicsScale );
            externalGraphicScaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        }
        imageGraphic.setImage( externalGraphicScaleOp.filter( imageGraphic.getImage(), null ) );
    }

    /**
     * Computes the scale to be applied to externally created graphics.
     * <p/>
     * Scale is determined by specifying a distance in the external graphics that should
     * be the same as the distance between two point on the screen.
     *
     * @param p1
     * @param p2
     * @param externalGraphicDist
     */
    private void determineExternalGraphicScale( Point p1, Point p2, int externalGraphicDist ) {
        externalGraphicsScale = p1.distance( p2 ) / externalGraphicDist;
    }

    /**
     * @param apparatusPanel
     */
    private void addCircuitGraphic( ApparatusPanel apparatusPanel ) {
        PhetImageGraphic circuitGraphic = new PhetImageGraphic( getApparatusPanel(), "images/battery-w-wires.png" );
        scaleImageGraphic( circuitGraphic );
        circuitGraphic.setRegistrationPoint( (int)( 120 * externalGraphicsScale ), (int)( 340 * externalGraphicsScale ) );
        circuitGraphic.setLocation( FluorescentLightsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( circuitGraphic, FluorescentLightsConfig.CIRCUIT_LAYER );
    }

    /**
     * Sets up the control panel
     */
    private void addControls() {
        final ModelSlider batterySlider = new ModelSlider( "Battery Voltage", "V", 0, .1, 0 );
        batterySlider.setPreferredSize( new Dimension( 250, 100 ) );
        ControlPanel controlPanel = (ControlPanel)getControlPanel();
        controlPanel.add( batterySlider );

        batterySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                cathode.setPotential( batterySlider.getValue() );
                anode.setPotential( 0 );
            }
        } );
    }

    /**
     * Creates the tube, adds it to the model and creates a graphic for it
     *
     * @param model
     * @param apparatusPanel
     * @return
     */
    private ResonatingCavity addTube( FluorescentLightModel model, ApparatusPanel apparatusPanel ) {
        double x = FluorescentLightsConfig.CATHODE_LOCATION.getX() - FluorescentLightsConfig.ELECTRODE_INSETS.left;
        double y = FluorescentLightsConfig.CATHODE_LOCATION.getY() - FluorescentLightsConfig.CATHODE_LENGTH / 2
                   - FluorescentLightsConfig.ELECTRODE_INSETS.top;
        double length = FluorescentLightsConfig.ANODE_LOCATION.getX() - FluorescentLightsConfig.CATHODE_LOCATION.getX()
                + FluorescentLightsConfig.ELECTRODE_INSETS.left + FluorescentLightsConfig.ELECTRODE_INSETS.right;
        double height = FluorescentLightsConfig.CATHODE_LENGTH
                        + FluorescentLightsConfig.ELECTRODE_INSETS.top + FluorescentLightsConfig.ELECTRODE_INSETS.bottom;
        Point2D tubeLocation = new Point2D.Double( x, y );
        ResonatingCavity tube = new ResonatingCavity( tubeLocation, length, height );
        model.addModelElement( tube );
        ResonatingCavityGraphic tubeGraphic = new ResonatingCavityGraphic( getApparatusPanel(), tube );
        apparatusPanel.addGraphic( tubeGraphic, FluorescentLightsConfig.TUBE_LAYER );
        return tube;
    }

    /**
     * Adds some atoms and their graphics
     *
     * @param tube
     */
    private void addAtoms( ResonatingCavity tube ) {
        DischargeLampAtom atom = null;
        ArrayList atoms = new ArrayList();
        Rectangle2D tubeBounds = tube.getBounds();
        int numAtoms = 30;
        for( int i = 0; i < numAtoms; i++ ) {
            atom = new DischargeLampAtom( (LaserModel)getModel(), numEnergyLevels );
            atom.setPosition( ( tubeBounds.getX() + ( Math.random() ) * ( tubeBounds.getWidth() - atom.getRadius() * 4 ) + atom.getRadius() * 2 ),
                              ( tubeBounds.getY() + ( Math.random() ) * ( tubeBounds.getHeight() - atom.getRadius() * 4 ) ) + atom.getRadius() * 2 );
            atom.setVelocity( (float)( Math.random() - 0.5 ) * s_maxSpeed,
                              (float)( Math.random() - 0.5 ) * s_maxSpeed );
            atoms.add( atom );
            addAtom( atom );
        }
    }

    /**
     * DEBUG: Adds some atoms and their graphics
     *
     * @param tube
     */
    private void addDebugAtoms( ResonatingCavity tube ) {

        getModel().removeModelElement( cathode );
        Point2D p0 = new Point2D.Double( FluorescentLightsConfig.CATHODE_LINE.getP1().getX(),
                                         ( FluorescentLightsConfig.CATHODE_LINE.getP1().getY() +
                                           FluorescentLightsConfig.CATHODE_LINE.getP2().getY() ) / 2 - 20 );
        cathode = new ElectronSource( getModel(), p0, p0 );
        cathode.addListener( this );
        cathode.setElectronsPerSecond( 0 );
        cathode.setPosition( FluorescentLightsConfig.CATHODE_LOCATION );
        getModel().addModelElement( cathode );
        addAnode( (FluorescentLightModel)getModel(), getApparatusPanel(), cathode );
        hookCathodeToAnode();

        DischargeLampAtom atom = null;
        ArrayList atoms = new ArrayList();
        Rectangle2D tubeBounds = tube.getBounds();
        int numAtoms = 1;
        for( int i = 0; i < numAtoms; i++ ) {
            atom = new DischargeLampAtom( (LaserModel)getModel(), numEnergyLevels );
            atom.setPosition( ( tubeBounds.getX() + 150 ),
                              ( tubeBounds.getY() + tubeBounds.getHeight() / 2 - atom.getRadius() ) );
            atom.setVelocity( 0, 0 );
            atoms.add( atom );
            addAtom( atom );
        }
    }

    /**
     * Creates a listener that manages the production rate of the cathode based on its potential
     * relative to the anode
     */
    private void hookCathodeToAnode() {
        anode.addStateChangeListener( new Electrode.StateChangeListener() {
            public void stateChanged( Electrode.StateChangeEvent event ) {
                double anodePotential = event.getElectrode().getPotential();
                cathode.setSinkPotential( anodePotential );
            }
        } );
    }

    /**
     * @param model
     * @param apparatusPanel
     * @param cathode
     */
    private void addAnode( FluorescentLightModel model, ApparatusPanel apparatusPanel, ElectronSource cathode ) {
        ElectronSink anode = new ElectronSink( model,
                                               FluorescentLightsConfig.ANODE_LINE.getP1(),
                                               FluorescentLightsConfig.ANODE_LINE.getP2() );
        model.addModelElement( anode );
        this.anode = anode;
        this.anode.setPosition( FluorescentLightsConfig.ANODE_LOCATION );
        PhetImageGraphic anodeGraphic = new PhetImageGraphic( getApparatusPanel(), "images/electrode.png" );
        // Scale the graphic
//        scaleImageGraphic( anodeGraphic );
        anodeGraphic.setRegistrationPoint( 0, (int)anodeGraphic.getBounds().getHeight() / 2 );
        anodeGraphic.setLocation( FluorescentLightsConfig.ANODE_LOCATION );
        apparatusPanel.addGraphic( anodeGraphic, FluorescentLightsConfig.CIRCUIT_LAYER );
        cathode.addListener( anode );
    }

    /**
     * @param model
     * @param apparatusPanel
     */
    private void addCathode( FluorescentLightModel model, ApparatusPanel apparatusPanel ) {
        cathode = new ElectronSource( model,
                                      FluorescentLightsConfig.CATHODE_LINE.getP1(),
                                      FluorescentLightsConfig.CATHODE_LINE.getP2() );
        model.addModelElement( cathode );
        cathode.addListener( this );
        cathode.setElectronsPerSecond( 0 );
        cathode.setPosition( FluorescentLightsConfig.CATHODE_LOCATION );
        PhetImageGraphic cathodeGraphic = new PhetImageGraphic( getApparatusPanel(), "images/electrode.png" );
        // Scale the graphic
//        scaleImageGraphic( cathodeGraphic );
        cathodeGraphic.setRegistrationPoint( (int)cathodeGraphic.getBounds().getWidth(),
                                             (int)cathodeGraphic.getBounds().getHeight() / 2 );
        cathodeGraphic.setLocation( FluorescentLightsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( cathodeGraphic, FluorescentLightsConfig.CIRCUIT_LAYER );
    }


    //----------------------------------------------------------------
    // Interface implementations
    //----------------------------------------------------------------

    public void electronProduced( ElectronSource.ElectronProductionEvent event ) {
        Electron electron = event.getElectron();

        // Create a graphic for the electron
        ElectronGraphic graphic = new ElectronGraphic( getApparatusPanel(), electron );
        getApparatusPanel().addGraphic( graphic, FluorescentLightsConfig.ELECTRON_LAYER );
        anode.addListener( new AbsorptionElectronAbsorptionListener( electron, graphic ) );
    }

    //-----------------------------------------------------------------
    // Inner classes
    //-----------------------------------------------------------------

    /**
     * Listens for the absorption of an electron. When such an event happens,
     * its graphic is taken off the apparatus panel
     */
    private class AbsorptionElectronAbsorptionListener implements ElectronSink.ElectronAbsorptionListener {
        private Electron electron;
        private ElectronGraphic graphic;

        AbsorptionElectronAbsorptionListener( Electron electron, ElectronGraphic graphic ) {
            this.electron = electron;
            this.graphic = graphic;
        }

        public void electronAbsorbed( ElectronSink.ElectronAbsorptionEvent event ) {
            if( event.getElectron() == electron ) {
                getApparatusPanel().removeGraphic( graphic );
                anode.removeListener( this );
            }
        }
    }
}

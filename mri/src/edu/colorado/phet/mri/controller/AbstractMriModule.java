/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.SampleChamber;
import edu.colorado.phet.mri.model.SampleMaterial;
import edu.colorado.phet.mri.view.ModelElementGraphicManager;
import edu.colorado.phet.mri.view.PlaneWaveGraphic;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.quantum.view.PhotonGraphic;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * AbstractMriModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class AbstractMriModule extends Module {
    protected static int delay = (int)( 1000 / MriConfig.FPS );
    protected static double dt = MriConfig.DT;
    protected ModelElementGraphicManager graphicsManager;
    public static EmRep PHOTON_VIEW = new EmRep();
    public static EmRep WAVE_VIEW = new EmRep();
    private PNode worldNode;

    public AbstractMriModule( String name, IClock clock ) {
        super( name, clock );

        MriModel model = new MriModel( getClock(), new Rectangle2D.Double( 0, 0, 1024, 768 ) );
        setModel( model );

        model.setSampleMaterial( SampleMaterial.HYDROGEN );

        // Control panel
        setControlPanel( new MriControlPanel( this ) );

        // Make the canvas, world node, and graphics manager
        PhetPCanvas simPanel = new PhetPCanvas( new Dimension( (int)( model.getBounds().getWidth() * MriConfig.scale ),
                                                               (int)( model.getBounds().getHeight() * MriConfig.scale ) ) );
        setSimulationPanel( simPanel );
        worldNode = new PNode();
        simPanel.addWorldChild( worldNode );

        graphicsManager = new ModelElementGraphicManager( simPanel, worldNode );
        graphicsManager.scanModel( model );
        model.addListener( graphicsManager );
    }

    public void setEmRep( EmRep emRep ) {
        if( emRep == WAVE_VIEW ) {
            graphicsManager.setAllOfTypeVisible( PhotonGraphic.class, false );
            graphicsManager.setAllOfTypeVisible( PlaneWaveGraphic.class, true );
        }
        else if( emRep == PHOTON_VIEW ) {
            graphicsManager.setAllOfTypeVisible( PhotonGraphic.class, true );
            graphicsManager.setAllOfTypeVisible( PlaneWaveGraphic.class, false );
        }
    }

    public static class EmRep {
        private EmRep() {
        }
    }

    protected PNode getWorldNode() {
        return worldNode;
    }

    protected abstract void createDipoles( int numDipoles, SampleChamber sampleChamber, MriModel model );
}

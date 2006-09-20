/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.module;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.control.AtomicModelSelector;
import edu.colorado.phet.hydrogenatom.control.GunControlPanel;
import edu.colorado.phet.hydrogenatom.control.HAClockControlPanel;
import edu.colorado.phet.hydrogenatom.control.ModeSwitch;
import edu.colorado.phet.hydrogenatom.energydiagrams.BohrEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.DeBroglieEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.SchrodingerEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.SolarSystemEnergyDiagram;
import edu.colorado.phet.hydrogenatom.enums.AtomicModel;
import edu.colorado.phet.hydrogenatom.model.HAClock;
import edu.colorado.phet.hydrogenatom.spectrometer.Spectrometer;
import edu.colorado.phet.hydrogenatom.view.*;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.colorado.phet.piccolo.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;


public class HAModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhetPCanvas _canvas;
    private PNode _rootNode;
    private HAController _controller;

    // Control panels
    private HAClockControlPanel _clockControlPanel;
    private ModeSwitch _modeSwitch;
    private AtomicModelSelector _atomicModelSelector;
    private GunControlPanel _gunControlPanel;

    // Box/beam/gun
    private PNode _boxBeamGunParent;
    private BoxOfHydrogen _boxOfHydrogen;
    private AnimationRegionNode _tinyBox;
    private BeamNode _beamNode;
    private GunNode _gunNode;

    // Animation region
    private AnimationRegionNode _animationRegionNode;
    private ZoomIndicatorNode _zoomIndicatorNode;
    private ExperimentAtomNode _experimentAtomNode;
    private BilliardBallAtomNode _billiardBallAtomNode;
    private BohrAtomNode _bohrAtomNode;
    private DeBroglieAtomNode _deBroglieAtomNode;
    private PlumPuddingAtomNode _plumPuddingAtomNode;
    private SchrodingerAtomNode _schrodingerAtomNode;
    private SolarSystemAtomNode _solarSystemAtomNode;

    // Spectrometer
    private JCheckBox _spectrometerCheckBox;
    private PSwing _spectrometerCheckBoxNode;
    private Spectrometer _spectrometer;
    private ArrayList _spectrometerSnapshots; // list of Spectrometer
    private int _spectrumSnapshotCounter; // incremented each time a spectrometer snapshot is taken

    // Energy Diagrams
    private JCheckBox _energyDiagramCheckBox;
    private PSwing _energyDiagramCheckBoxNode;
    private BohrEnergyDiagram _bohrEnergyDiagram;
    private DeBroglieEnergyDiagram _deBroglieEnergyDiagram;
    private SchrodingerEnergyDiagram _schrodingerEnergyDiagram;
    private SolarSystemEnergyDiagram _solarSystemEnergyDiagram;

    private HTMLNode _notToScaleLabel;

    private PhotonNode _samplePhotonNode;
    private AlphaParticleNode _sampleAlphaParticleNode;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public HAModule() {
        super( SimStrings.get( "HAModule.title" ), new HAClock() );

        // hide the PhET logo
        setLogoPanel( null );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( HAConstants.CANVAS_RENDERING_SIZE );
            _canvas.setBackground( HAConstants.CANVAS_BACKGROUND );
            setSimulationPanel( _canvas );

            _canvas.addComponentListener( new ComponentAdapter() {

                public void componentResized( ComponentEvent e ) {
                    // update the layout when the canvas is resized
                    updateCanvasLayout();
                }
            } );
        }

        // Root of our scene graph
        {
            _rootNode = new PNode();
            _canvas.addWorldChild( _rootNode );
        }

        // Mode switch (experiment/prediction)
        {
            _modeSwitch = new ModeSwitch();
            _rootNode.addChild( _modeSwitch );
        }

        // Atomic Model selector
        {
            _atomicModelSelector = new AtomicModelSelector();
            _rootNode.addChild( _atomicModelSelector );
        }

        //  Box of Hydrogen / Beam / Gun
        {
            // Parent node, used for layout
            _boxBeamGunParent = new PNode();
            _rootNode.addChild( _boxBeamGunParent );

            _boxOfHydrogen = new BoxOfHydrogen( HAConstants.BOX_OF_HYDROGEN_SIZE.width, HAConstants.BOX_OF_HYDROGEN_SIZE.height, HAConstants.BOX_OF_HYDROGEN_DEPTH );
            _tinyBox = new AnimationRegionNode( HAConstants.TINY_BOX_SIZE, HAConstants.ANIMATION_REGION_COLOR );
            _beamNode = new BeamNode( .75 * HAConstants.BOX_OF_HYDROGEN_SIZE.width, 100 );
            _gunNode = new GunNode();

            // Layering order
            _boxBeamGunParent.addChild( _beamNode );
            _boxBeamGunParent.addChild( _boxOfHydrogen );
            _boxBeamGunParent.addChild( _tinyBox );
            _boxBeamGunParent.addChild( _gunNode );
        }

        // Animation region
        {
            // animation box
            _animationRegionNode = new AnimationRegionNode( HAConstants.ANIMATION_REGION_SIZE, HAConstants.ANIMATION_REGION_COLOR );
            _rootNode.addChild( _animationRegionNode );

            // zoom indicator
            _zoomIndicatorNode = new ZoomIndicatorNode();
            _rootNode.addChild( _zoomIndicatorNode );

            // atoms
            _experimentAtomNode = new ExperimentAtomNode();
            _billiardBallAtomNode = new BilliardBallAtomNode();
            _bohrAtomNode = new BohrAtomNode();
            _deBroglieAtomNode = new DeBroglieAtomNode();
            _plumPuddingAtomNode = new PlumPuddingAtomNode();
            _schrodingerAtomNode = new SchrodingerAtomNode();
            _solarSystemAtomNode = new SolarSystemAtomNode();

            // layering order
            _animationRegionNode.addChild( _experimentAtomNode );
            _animationRegionNode.addChild( _billiardBallAtomNode );
            _animationRegionNode.addChild( _bohrAtomNode );
            _animationRegionNode.addChild( _deBroglieAtomNode );
            _animationRegionNode.addChild( _plumPuddingAtomNode );
            _animationRegionNode.addChild( _schrodingerAtomNode );
            _animationRegionNode.addChild( _solarSystemAtomNode );
        }

        // Gun control panel
        {
            _gunControlPanel = new GunControlPanel( _canvas );
            _rootNode.addChild( _gunControlPanel );
        }

        // Spectrometer
        {
            // Checkbox
            _spectrometerCheckBox = new JCheckBox( SimStrings.get( "label.showSpectrometer" ) );
            _spectrometerCheckBox.setOpaque( false );
            _spectrometerCheckBox.setForeground( HAConstants.CANVAS_LABELS_COLOR );
            _spectrometerCheckBox.setFont( HAConstants.CONTROL_FONT );
            _spectrometerCheckBoxNode = new PSwing( _canvas, _spectrometerCheckBox );
            _rootNode.addChild( _spectrometerCheckBoxNode );

            // Spectrometer
            String title = SimStrings.get( "label.photonsEmitted" );
            _spectrometer = new Spectrometer( _canvas, title, false /* isaSnapshot */);
            _rootNode.addChild( _spectrometer );

            // List of snapshots
            _spectrometerSnapshots = new ArrayList();
        }

        // Energy diagrams
        {
            // checkbox
            _energyDiagramCheckBox = new JCheckBox( SimStrings.get( "label.showEnergyDiagram" ) );
            _energyDiagramCheckBox.setOpaque( false );
            _energyDiagramCheckBox.setForeground( HAConstants.CANVAS_LABELS_COLOR );
            _energyDiagramCheckBox.setFont( HAConstants.CONTROL_FONT );
            _energyDiagramCheckBoxNode = new PSwing( _canvas, _energyDiagramCheckBox );
            _rootNode.addChild( _energyDiagramCheckBoxNode );

            // diagrams
            _bohrEnergyDiagram = new BohrEnergyDiagram();
            _deBroglieEnergyDiagram = new DeBroglieEnergyDiagram();
            _schrodingerEnergyDiagram = new SchrodingerEnergyDiagram();
            _solarSystemEnergyDiagram = new SolarSystemEnergyDiagram();

            _rootNode.addChild( _bohrEnergyDiagram );
            _rootNode.addChild( _deBroglieEnergyDiagram );
            _rootNode.addChild( _schrodingerEnergyDiagram );
            _rootNode.addChild( _solarSystemEnergyDiagram );
        }

        // "Not to scale" label
        {
            _notToScaleLabel = new HTMLNode( SimStrings.get( "label.notToScale" ) );
            _notToScaleLabel.setHTMLColor( HAConstants.CANVAS_LABELS_COLOR );
            _notToScaleLabel.setFont( new Font( HAConstants.FONT_NAME, Font.PLAIN, 14 ) );
            _notToScaleLabel.setPickable( false );
            _notToScaleLabel.setChildrenPickable( false );
            _rootNode.addChild( _notToScaleLabel );
        }

        // XXX Sample nodes
        {
            _samplePhotonNode = new PhotonNode();
            _sampleAlphaParticleNode = new AlphaParticleNode();
            _animationRegionNode.addChild( _samplePhotonNode );
            _animationRegionNode.addChild( _sampleAlphaParticleNode );
        }

        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Clock controls
        _clockControlPanel = new HAClockControlPanel( (HAClock) getClock() );
        setClockControlPanel( _clockControlPanel );

        _controller = new HAController( this, _modeSwitch, _atomicModelSelector, 
                _gunNode, _gunControlPanel, _energyDiagramCheckBox, 
                _spectrometer, _spectrometerCheckBox );

        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------

        if ( hasHelp() ) {
            //XXX add help items to the help pane
//            HelpPane helpPane = getDefaultHelpPane();
        }

        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------

        reset();
        updateCanvasLayout();
    }

    //----------------------------------------------------------------------------
    //
    //----------------------------------------------------------------------------

    private void reset() {
        _modeSwitch.setPredictionSelected();
        _atomicModelSelector.setSelection( AtomicModel.BILLIARD_BALL );
        _gunNode.setOn( false );
        _gunControlPanel.getGunTypeControl().setLightSelected();
        _gunControlPanel.getLightTypeControl().setMonochromaticSelected();
        _gunControlPanel.getLightIntensityControl().setValue( 100 );
        _gunControlPanel.getWavelengthControl().setWavelength( VisibleColor.MIN_WAVELENGTH );
        _gunControlPanel.getAlphaParticlesIntensityControl().setValue( 100 );
        _spectrometerCheckBox.setSelected( true );
        _energyDiagramCheckBox.setSelected( false );
    }

    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------

    public void updateCanvasLayout() {

        // Determine the visible bounds in world coordinates
        Dimension2D dim = new PDimension( _canvas.getWidth(), _canvas.getHeight() );
        _canvas.getPhetRootNode().screenToWorld( dim ); // this modifies dim!
        Dimension worldSize = new Dimension( (int) dim.getWidth(), (int) dim.getHeight() );

        // margins and spacing
        final double xMargin = 20;
        final double yMargin = 10;
        final double xSpacing = 20;
        final double ySpacing = 10;

        // reusable (x,y) coordinates, for setting offsets
        double x, y;

        // Mode Switch
        {
            // upper left corner
            _modeSwitch.setOffset( xMargin, yMargin );
        }

        // Atomic Model Selector
        {
            // below mode selector, left aligned
            PBounds msb = _modeSwitch.getFullBounds();
            x = msb.getX();
            y = msb.getY() + msb.getHeight() + ySpacing;
            _atomicModelSelector.setOffset( x, y );
        }

        // Box of Hydrogen / Beam / Gun
        {
            PBounds ab = _atomicModelSelector.getFullBounds();
            x = ab.getX() + ab.getWidth() + xSpacing;//XXX
            y = 350;//XXX
            _gunNode.setOffset( x, y );
        }

        // Animation box
        {
            PBounds gb = _gunNode.getFullBounds();
            PBounds ntsb = _notToScaleLabel.getFullBounds();
            x = gb.getX() + gb.getWidth() + xSpacing;
            y = 10 + ntsb.getHeight() + 10;
            _animationRegionNode.setOffset( x, y );
        }

        // Gun control panel
        {
            PBounds ab = _atomicModelSelector.getFullBounds();
            PBounds bb = _animationRegionNode.getFullBounds();
            x = ab.getX() + ab.getWidth() + xSpacing;
            y = bb.getY() + bb.getHeight() + ySpacing;
            _gunControlPanel.setOffset( x, y );
        }

        // Box of Hydrogen
        {
            PBounds gb = _gunNode.getFullBounds();
            PBounds ab = _atomicModelSelector.getFullBounds();
            PBounds bb = _boxOfHydrogen.getFullBounds();
            x = ab.getX() + ab.getWidth() + ( bb.getWidth() / 2 ) + 35;//XXX
            y = gb.getY() - 75; //XXX
            _boxOfHydrogen.setOffset( x, y );

            // Tiny box
            y = y - ( ( HAConstants.BOX_OF_HYDROGEN_SIZE.height - HAConstants.TINY_BOX_SIZE.height ) / 2 );
            _tinyBox.setOffset( x, y );
        }

        // Zoom Indicator
        {
            PBounds tb = _tinyBox.getFullBounds();
            PBounds ab = _animationRegionNode.getFullBounds();
            Point2D tp = new Point2D.Double( tb.getX(), tb.getY() );
            Point2D ap = new Point2D.Double( ab.getX(), ab.getY() );
            _zoomIndicatorNode.update( tp, HAConstants.TINY_BOX_SIZE, ap, HAConstants.ANIMATION_REGION_SIZE );
        }

        // Beam
        {
            _beamNode.setOffset( _boxOfHydrogen.getOffset() );
        }

        // "Drawings are not to scale" note, centered above black box.
        {
            PBounds bb = _animationRegionNode.getFullBounds();
            x = bb.getX() + ( ( bb.getWidth() - _notToScaleLabel.getFullBounds().getWidth() ) / 2 );
            y = ( bb.getY() - _notToScaleLabel.getFullBounds().getHeight() ) / 2;
            _notToScaleLabel.setOffset( x, y );
        }

        // Energy Diagram, to the right of the black box.
        {
            PBounds bb = _animationRegionNode.getFullBounds();
            x = bb.getX() + bb.getHeight() + 10;
            y = yMargin;
            _energyDiagramCheckBoxNode.setOffset( x, y );

            // Diagram is below checkbox, left aligned.
            PBounds b = _energyDiagramCheckBoxNode.getFullBounds();
            x = b.getX();
            y = b.getY() + b.getHeight() + 10;
            _bohrEnergyDiagram.setOffset( x, y );
            _deBroglieEnergyDiagram.setOffset( x, y );
            _schrodingerEnergyDiagram.setOffset( x, y );
            _solarSystemEnergyDiagram.setOffset( x, y );
        }

        // Spectrometer
        {
            // Spectrometer in bottom right corner.
            PBounds bb = _animationRegionNode.getFullBounds();
            PBounds gb = _gunControlPanel.getFullBounds();
            final double gunRightEdge = gb.getX() + gb.getWidth() + xSpacing;
            x = Math.max( gunRightEdge, worldSize.getWidth() - _spectrometer.getFullBounds().getWidth() - xMargin );
            y = bb.getY() + bb.getHeight() + ySpacing;
            _spectrometer.setOffset( x, y );

            // Checkbox above right of spectrometer
            PBounds sb = _spectrometer.getFullBounds();
            x = _energyDiagramCheckBoxNode.getFullBounds().getX();
            y = sb.getY() - _spectrometerCheckBoxNode.getFullBounds().getHeight() - 15;
            _spectrometerCheckBoxNode.setOffset( x + 10, y + 5 );
        }

        // Animation nodes
        {
            PBounds ab = _animationRegionNode.getFullBounds();

            x = ( ab.getWidth() - _experimentAtomNode.getFullBounds().getWidth() ) / 2;
            y = ( ab.getHeight() - _experimentAtomNode.getFullBounds().getHeight() ) / 2;
            _experimentAtomNode.setOffset( x, y );

            x = ab.getWidth() / 2;
            y = ab.getHeight() / 2;
            _billiardBallAtomNode.setOffset( x, y );
            _bohrAtomNode.setOffset( x, y );
            _deBroglieAtomNode.setOffset( x, y );
            _plumPuddingAtomNode.setOffset( x, y );
            _schrodingerAtomNode.setOffset( x, y );
            _solarSystemAtomNode.setOffset( x, y );
        }

        // Sample nodes
        {
            PBounds ab = _animationRegionNode.getFullBounds();

            x = ab.getWidth() - 50;
            y = ab.getHeight() - 100;
            _samplePhotonNode.setOffset( x, y );
            _sampleAlphaParticleNode.setOffset( x - 100, y );
        }
    }

    public void updateAtomicModelSelector() {
        _atomicModelSelector.setVisible( _modeSwitch.isPredictionSelected() );
    }

    public void updateAtomicModel() {

        AtomicModel atomicModel = _atomicModelSelector.getSelection();

        _experimentAtomNode.setVisible( false );
        _billiardBallAtomNode.setVisible( false );
        _bohrAtomNode.setVisible( false );
        _deBroglieAtomNode.setVisible( false );
        _plumPuddingAtomNode.setVisible( false );
        _schrodingerAtomNode.setVisible( false );
        _solarSystemAtomNode.setVisible( false );

        if ( _modeSwitch.isExperimentSelected() ) {
            _experimentAtomNode.setVisible( true );
        }
        else {
            if ( atomicModel == AtomicModel.BILLIARD_BALL ) {
                _billiardBallAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.BOHR ) {
                _bohrAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.DEBROGLIE ) {
                _deBroglieAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.PLUM_PUDDING ) {
                _plumPuddingAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.SCHRODINGER ) {
                _schrodingerAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.SOLAR_SYSTEM ) {
                _solarSystemAtomNode.setVisible( true );
            }
        }
    }

    public void updateEnergyDiagram() {

        AtomicModel atomicModel = _atomicModelSelector.getSelection();

        _energyDiagramCheckBoxNode.setVisible( false );
        _bohrEnergyDiagram.setVisible( false );
        _deBroglieEnergyDiagram.setVisible( false );
        _schrodingerEnergyDiagram.setVisible( false );
        _solarSystemEnergyDiagram.setVisible( false );

        if ( _modeSwitch.isPredictionSelected() ) {
            if ( atomicModel == AtomicModel.BOHR ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _bohrEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
            else if ( atomicModel == AtomicModel.DEBROGLIE ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _deBroglieEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
            else if ( atomicModel == AtomicModel.SCHRODINGER ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _schrodingerEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
            else if ( atomicModel == AtomicModel.SOLAR_SYSTEM ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _solarSystemEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
        }
    }

    public void updateBeam() {
        final boolean isOn = _gunNode.isOn();
        _beamNode.setVisible( isOn );
        if ( isOn ) {
            if ( _gunControlPanel.getGunTypeControl().isLightSelected() ) {
                Color color = null;
                if ( _gunControlPanel.getLightTypeControl().isWhiteSelected() ) {
                    color = Color.WHITE;
                }
                else {
                    color = _gunControlPanel.getWavelengthControl().getWavelengthColor();
                }
                int intensity = _gunControlPanel.getLightIntensityControl().getValue();
                _beamNode.setColor( color, intensity );
            }
            else {
                Color color = _gunControlPanel.getAlphaParticlesIntensityControl().getColor();
                int intensity = _gunControlPanel.getAlphaParticlesIntensityControl().getValue();
                _beamNode.setColor( color, intensity );
            }
        }
    }

    public void updateSpectrometer() {
        final boolean visible = _spectrometerCheckBox.isSelected();
        _spectrometer.setVisible( visible );
        Iterator i = _spectrometerSnapshots.iterator();
        while ( i.hasNext() ) {
            Spectrometer spectrometer = (Spectrometer) i.next();
            spectrometer.setVisible( visible );
        }
    }

    public void createSpectrometerSnapshot() {

        _spectrumSnapshotCounter++;

        String title = SimStrings.get( "label.snapshot" ) + " " + _spectrumSnapshotCounter + ": ";
        if ( _modeSwitch.isPredictionSelected() ) {
            //XXX replace this call, the title may contain HTML markup
            title += _atomicModelSelector.getSelectionName();
        }
        else {
            title += SimStrings.get( "title.spectrometer.experiment" );
        }

        final Spectrometer spectrometer = new Spectrometer( _canvas, title, true /* isaSnapshot */);

        _rootNode.addChild( spectrometer );
        _controller.addSpectrometerListener( spectrometer );
        _spectrometerSnapshots.add( spectrometer );

        PBounds sb = _spectrometer.getFullBounds();
        double x = sb.getX();
        double y = sb.getY() - spectrometer.getFullBounds().getHeight() - ( 10 * _spectrometerSnapshots.size() );
        spectrometer.setOffset( x, y );
    }

    public void deleteSpectrometerSnapshot( Spectrometer spectrometer ) {
        if ( spectrometer == _spectrometer ) {
            _spectrometerCheckBox.setSelected( false );
        }
        else {
            _rootNode.removeChild( spectrometer );
            _spectrometerSnapshots.remove( spectrometer );
        }
    }
}

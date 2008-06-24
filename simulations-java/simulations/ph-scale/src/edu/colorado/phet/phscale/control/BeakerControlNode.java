/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import edu.colorado.phet.phscale.control.ViewControlPanel.ViewControlPanelListener;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.PHScaleModel;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.view.BeakerNode;
import edu.colorado.phet.phscale.view.MoleculeCountNode;
import edu.colorado.phet.phscale.view.ProbeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class BeakerControlNode extends PNode {

    private static final double PROBE_LENGTH = 500;
    private static final PDimension BEAKER_SIZE = new PDimension( 350, 400 );
    
    private final PHScaleModel _model;
    private final LiquidListener _liquidListener;
    
    private final LiquidControlNode _liquidControlNode;
    private final WaterControlNode _waterControlNode;
    private final BeakerNode _beakerNode;
    private final ProbeNode _probeNode;
    private final MoleculeCountNode _moleculeCountNode;
    private final ViewControlPanel _viewControlPanel;
    private final DrainControlNode _drainControlNode;
    
    public BeakerControlNode( PSwingCanvas pSwingCanvas, PHScaleModel model ) {
        super();
        
        _model = model;
        Liquid liquid = model.getLiquid();
        
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                //XXX update everything
            }
        };
        liquid.addLiquidListener( _liquidListener );
        
        _probeNode = new ProbeNode( PROBE_LENGTH, liquid );
        
        _liquidControlNode = new LiquidControlNode( pSwingCanvas, liquid );
        
        _waterControlNode = new WaterControlNode( liquid );
        
        _drainControlNode = new DrainControlNode();

        _beakerNode = new BeakerNode( _model.getBeaker(), BEAKER_SIZE );
        
        _moleculeCountNode = new MoleculeCountNode( liquid );
        
        _viewControlPanel = new ViewControlPanel();
        PSwing viewControlPanelWrapper = new PSwing( _viewControlPanel );
        _viewControlPanel.addViewControlPanelListener( new ViewControlPanelListener() {

            public void countChanged( boolean selected ) {
                _moleculeCountNode.setVisible( selected );
            }

            public void ratioChanged( boolean selected ) {
                //XXX change visibility of ratio node
            }
            
        } );
        
        // Rendering order
        addChild( _liquidControlNode );
        addChild( _waterControlNode );
        addChild( _probeNode );
        addChild( _drainControlNode );
        addChild( _beakerNode );
        addChild( _moleculeCountNode );
        addChild( viewControlPanelWrapper );
        
        // Layout
        _beakerNode.setOffset( 75, 150 );//XXX
        _liquidControlNode.setOffset( 25, 0 );//XXX
        _waterControlNode.setOffset( 400, 0 );//XXX
        _drainControlNode.setOffset( 25, 575 );//XXX
        _probeNode.setOffset( 175, 50 );//XXX
        viewControlPanelWrapper.setOffset( 225, 575 );//XXX
        _moleculeCountNode.setOffset( 85, 250 );//XXX
    }
    
    public void cleanup() {
        _model.getLiquid().removeLiquidListener( _liquidListener );
    }
    
    public void setMoleculeCountSelected( boolean selected ) {
        _viewControlPanel.setCountSelected( selected );
    }
    
    public boolean isMoleculeCountSelected() {
        return _viewControlPanel.isCountSelected();
    }
    
    public void setRatioSelected( boolean selected ) {
        _viewControlPanel.setRatioSelected( selected );
    }
    
    public boolean isRatioSelected() {
        return _viewControlPanel.isRatioSelected();
    }
    
    public LiquidControlNode getLiquidControlNode() {
        return _liquidControlNode;
    }
}

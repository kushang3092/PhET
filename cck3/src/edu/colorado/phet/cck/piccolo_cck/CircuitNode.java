package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.*;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:12:37 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CircuitNode extends PhetPNode {
    private CCKModel cckModel;
    private Circuit circuit;
    private ArrayList junctionGraphics = new ArrayList();
    private ArrayList branchGraphics = new ArrayList();
    private PNode electronNode;

    public CircuitNode( CCKModel cckModel, Circuit circuit ) {
        this.cckModel = cckModel;
        this.circuit = circuit;
        circuit.addCircuitListener( new CircuitListenerAdapter() {
            public void branchAdded( Branch branch ) {
                BranchNode branchNode = createNode( branch );
                branchGraphics.add( branchNode );
                addChild( branchNode );
            }

            public void junctionAdded( Junction junction ) {
                JunctionNode node = createNode( junction );
                junctionGraphics.add( node );
                addChild( node );
            }

            public void junctionRemoved( Junction junction ) {
                for( int i = 0; i < junctionGraphics.size(); i++ ) {
                    JunctionNode junctionNode = (JunctionNode)junctionGraphics.get( i );
                    if( junctionNode.getJunction() == junction ) {
                        removeJunctionGraphic( junctionNode );
                    }
                }
            }

            public void selectionChanged() {
                for( int i = 0; i < branchGraphics.size(); i++ ) {
                    BranchNode pNode = (BranchNode)branchGraphics.get( i );
                    if( pNode.getBranch().isSelected() ) {
                        pNode.moveToFront();
                    }
                }
                for( int i = 0; i < junctionGraphics.size(); i++ ) {
                    ( (JunctionNode)junctionGraphics.get( i ) ).moveToFront();
                }
                electronNode.moveToFront();
            }

            public void branchRemoved( Branch branch ) {
                for( int i = 0; i < branchGraphics.size(); i++ ) {
                    BranchNode branchNode = (BranchNode)branchGraphics.get( i );
                    if( branchNode.getBranch() == branch ) {
                        removeBranchGraphic( branchNode );
                        i--;
                    }
                }
            }
        } );
        electronNode = new ElectronSetNode( cckModel );
        addChild( electronNode );
    }

    private void removeBranchGraphic( BranchNode branchNode ) {
        branchGraphics.remove( branchNode );
        removeChild( branchNode );
    }

    private void removeJunctionGraphic( JunctionNode junctionNode ) {
        junctionGraphics.remove( junctionNode );
        removeChild( junctionNode );
    }

    public JunctionNode createNode( Junction junction ) {
        return new JunctionNode( cckModel, junction, this );
    }

    public BranchNode createNode( Branch branch ) {
        if( branch instanceof Wire ) {
            return new WireNode( cckModel, (Wire)branch );
        }
        else if( branch instanceof Resistor ) {
            return new ComponentImageNode.ResistorNode( cckModel, (Resistor)branch );
        }
        else if( branch instanceof Battery ) {
            return new ComponentImageNode.BatteryNode( cckModel, (Battery)branch );
        }
        else if( branch instanceof Bulb ) {
            return new TotalBulbComponentNode( cckModel, (Bulb)branch );
        }
        else {
            throw new RuntimeException( "Unrecognized branch type: " + branch.getClass() );
        }
    }

    public Circuit getCircuit() {
        return cckModel.getCircuit();
    }
}

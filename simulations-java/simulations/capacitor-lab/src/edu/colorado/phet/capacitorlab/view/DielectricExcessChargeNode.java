// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.ICapacitor;
import edu.colorado.phet.capacitorlab.model.ICapacitor.CapacitorChangeListener;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Shows the excess dielectric charge (Q_excess_dielectric).
 * The number of charges is proportional to sqrt( Q_excess_dielectric ).
 * Charges appear on the surface of the dielectric where it contacts the plates,
 * so charges appear on the right face only when the dielectric is fully inserted.
 * <p/>
 * All model coordinates are relative to the dielectric's local coordinate frame,
 * where the origin is at the 3D geometric center of the dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricExcessChargeNode extends PhetPNode {

    private final ICapacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final PNode parentNode; // parent node for charges
    private final double maxExcessDielectricPlateCharge;

    public DielectricExcessChargeNode( ICapacitor capacitor, CLModelViewTransform3D mvt, double maxExcessDielectricPlateCharge ) {

        this.capacitor = capacitor;
        this.mvt = mvt;
        this.maxExcessDielectricPlateCharge = maxExcessDielectricPlateCharge;

        this.parentNode = new PComposite();
        addChild( parentNode );

        capacitor.addCapacitorChangeListener( new CapacitorChangeListener() {
            public void capacitorChanged() {
                if ( isVisible() ) {
                    update();
                }
            }
        } );

        update();
    }

    /**
     * Update the node when it becomes visible.
     */
    @Override
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            super.setVisible( visible );
            if ( visible ) {
                update();
            }
        }
    }

    private void update() {

        // remove existing charges
        parentNode.removeAllChildren();

        final double excessCharge = capacitor.getExcessDielectricPlateCharge();
        final double dielectricWidth = capacitor.getDielectricWidth();
        final double dielectricDepth = capacitor.getDielectricDepth();
        final double contactWidth = Math.max( 0, dielectricWidth - capacitor.getDielectricOffset() ); // contact between plate and dielectric

        if ( excessCharge != 0 && contactWidth > 0 ) {

            final int numberOfCharges = getNumberOfCharges( excessCharge );

            final double yMargin = mvt.viewToModelDelta( 0, new PositiveChargeNode().getFullBoundsReference().getHeight() ).getY();

            // distance between charges
            final double dx = contactWidth / numberOfCharges;
            final double dz = dielectricDepth / numberOfCharges;

            // offset to move us to the center of columns
            final double xOffset = dx / 2;
            final double zOffset = dz / 2;

            // front face
            for ( int i = 0; i < numberOfCharges; i++ ) {

                // add a pair of charges
                PNode topChargeNode = ( excessCharge > 0 ) ? new NegativeChargeNode() : new PositiveChargeNode();
                PNode bottomChargeNode = ( excessCharge > 0 ) ? new PositiveChargeNode() : new NegativeChargeNode();
                parentNode.addChild( topChargeNode );
                parentNode.addChild( bottomChargeNode );

                // position the charges at the top and bottom edges of the dielectric's front face
                double x = ( -dielectricWidth / 2 ) + xOffset + ( i * dx );
                double y = yMargin;
                double z = -( dielectricDepth / 2 );
                topChargeNode.setOffset( mvt.modelToView( x, y, z ) );
                y = capacitor.getDielectricHeight() - yMargin;
                bottomChargeNode.setOffset( mvt.modelToView( x, y, z ) );
            }

            // side face, charges only shown with dielectric fully inserted
            if ( capacitor.getDielectricOffset() == 0 ) {

                for ( int i = 0; i < numberOfCharges; i++ ) {

                    // add a pair of charges
                    PNode topChargeNode = ( excessCharge > 0 ) ? new NegativeChargeNode() : new PositiveChargeNode();
                    PNode bottomChargeNode = ( excessCharge > 0 ) ? new PositiveChargeNode() : new NegativeChargeNode();
                    parentNode.addChild( topChargeNode );
                    parentNode.addChild( bottomChargeNode );

                    // position the charges at the top and bottom edges of the dielectric's side face
                    double x = dielectricWidth / 2;
                    double y = yMargin;
                    double z = ( -dielectricDepth / 2 ) + zOffset + ( i * dz );
                    Point2D topOffset = mvt.modelToView( x, y, z );
                    topChargeNode.setOffset( topOffset );
                    y = capacitor.getDielectricHeight() - yMargin;
                    Point2D bottomOffset = mvt.modelToView( x, y, z );
                    bottomChargeNode.setOffset( bottomOffset );
                }
            }
        }
    }

    /*
     * Gets the number of charges, proportional to sqrt( Q_excess_dielectric ).
     * We use NUMBER_OF_PLATE_CHARGES as the range so that this view is related
     * to the plate charges view.
     */
    private int getNumberOfCharges( double excessCharge ) {
        double absCharge = Math.abs( excessCharge ); // don't take sqrt of absCharge, it's something like 1E-14 and will result in a *larger* number
        int numberOfCharges = (int) Math.sqrt( CLConstants.NUMBER_OF_PLATE_CHARGES.getMax() * absCharge / maxExcessDielectricPlateCharge ); // take sqrt here instead
        if ( absCharge > 0 && numberOfCharges < CLConstants.NUMBER_OF_PLATE_CHARGES.getMin() ) {
            numberOfCharges = CLConstants.NUMBER_OF_PLATE_CHARGES.getMin();
        }
        return numberOfCharges;
    }
}

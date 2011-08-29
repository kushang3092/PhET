// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.BiomoleculeBehaviorState;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.ShapeCreationUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.TranscribingDnaState;

/**
 * Class that represents RNA polymerase in the model.
 *
 * @author John Blanco
 */
public class RnaPolymerase extends MobileBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double WIDTH = 340;
    private static final double HEIGHT = 480;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final GeneExpressionModel model;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public RnaPolymerase() {
        this( new StubGeneExpressionModel(), new Point2D.Double( 0, 0 ) );
    }

    public RnaPolymerase( GeneExpressionModel model, Point2D position ) {
        super( createShape(), new Color( 0, 153, 210 ) );
        this.model = model;
        setPosition( position );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------


    @Override public void stepInTime( double dt ) {
        super.stepInTime( dt );
        if ( !userControlled.get() ) {
            // Get a list of potential attachment sites from the DNA and consider
            // whether to attach to any of them.
            List<AttachmentSite> potentialAttachmentSiteList = model.getDnaMolecule().getNearbyPolymeraseAttachmentSites( getPosition() );
            behaviorState = behaviorState.considerAttachment( potentialAttachmentSiteList, this );
        }
    }

    // Overridden to provide attachment behavior that is unique to polymerase.
    @Override public BiomoleculeBehaviorState getAttachmentPointReachedState( AttachmentSite attachmentSite ) {
        return new TranscribingDnaState( attachmentSite );
    }

    private static Shape createShape() {
        // Shape is meant to look like illustrations in "The Machinery of
        // Life" by David Goodsell.
        List<Point2D> points = new ArrayList<Point2D>();
        points.add( new Point2D.Double( 0, HEIGHT / 2 ) ); // Middle top.
        points.add( new Point2D.Double( WIDTH / 2, HEIGHT * 0.25 ) );
        points.add( new Point2D.Double( WIDTH * 0.35, -HEIGHT * 0.25 ) );
        points.add( new Point2D.Double( 0, -HEIGHT / 2 ) ); // Middle bottom.
        points.add( new Point2D.Double( -WIDTH * 0.35, -HEIGHT * 0.25 ) );
        points.add( new Point2D.Double( -WIDTH / 2, HEIGHT * 0.25 ) );
        return ShapeCreationUtils.createRoundedShapeFromPoints( points );
    }
}

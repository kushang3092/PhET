// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A site to which one biomolecule may attach to another.  Typically one
 * biomolecule (e.g. a DnaMolecule) owns the attachment site and proposes it to
 * a smaller biomolecule.
 *
 * @author John Blanco
 */
public class AttachmentSite {

    // Location of this attachment site.  It is a property so that it can be
    // followed in the event that the biomolecule upon which it exists is
    // moving.
    public Property<Point2D> locationProperty = new Property<Point2D>( new Point2D.Double( 0, 0 ) );

    // A value between 0 and 1 that represents the strength of the affinity
    // for this bond.
    private final double affinity;

    // A property that tracks the biomolecule which is "using" this attachment
    // site, meaning that it is either attached at this location or it is
    // moving towards it with the intention of attaching.
    public Property<MobileBiomolecule> inUseBy = new Property<MobileBiomolecule>( null );

    /**
     * Constructor.
     *
     * @param initialLocation
     * @param affinity
     */
    public AttachmentSite( Point2D initialLocation, double affinity ) {
        this.locationProperty.set( new Point2D.Double( initialLocation.getX(), initialLocation.getY() ) );
        this.affinity = affinity;
    }
}

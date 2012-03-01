// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.fractions.FractionsResources;
import edu.colorado.phet.fractionsintro.intro.view.beaker.BeakerNode;
import edu.colorado.phet.fractionsintro.intro.view.beaker.Solute;
import edu.colorado.phet.fractionsintro.intro.view.beaker.Solution;
import edu.colorado.phet.fractionsintro.intro.view.beaker.SolutionNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Shows a glass possibly with some water in it.
 *
 * @author Sam Reid
 */
public class WaterGlassNode extends RichPNode {

    // properties common to all 3 beakers
    private static final PhetFont BEAKER_LABEL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final PDimension BEAKER_LABEL_SIZE = new PDimension( 100, 50 );

    private static final DoubleRange SOLUTE_AMOUNT_RANGE = new DoubleRange( 0, 0.2, 0.05 ); // moles
    private static final DoubleRange DILUTION_VOLUME_RANGE = new DoubleRange( 0.2, 1, 0.5 ); // liters
    private static final DoubleRange CONCENTRATION_RANGE = new DoubleRange( SOLUTE_AMOUNT_RANGE.getMin() / DILUTION_VOLUME_RANGE.getMax(),
                                                                            SOLUTE_AMOUNT_RANGE.getMax() / DILUTION_VOLUME_RANGE.getMin() ); // M

    public WaterGlassNode( Integer numerator, Integer denominator, final Color color, double width, double height ) {
        double sx = width / FractionsResources.Images.WATER_GLASS_FRONT.getWidth();
        double sy = height / FractionsResources.Images.WATER_GLASS_FRONT.getHeight();

        final BeakerNode waterBeakerNode = new BeakerNode( 1, sx, sy, null, BEAKER_LABEL_SIZE, BEAKER_LABEL_FONT, 1.0 / denominator, 2, FractionsResources.Images.WATER_GLASS_FRONT );
        waterBeakerNode.setLabelVisible( false );
        final PDimension cylinderSize = waterBeakerNode.getCylinderSize();

        Solute solute = new Solute( "solute", "?", CONCENTRATION_RANGE.getMax(), new Color( 0xE0FFFF ), 5, 200 ); // hypothetical solute with unknown formula
        Solution solution = new Solution( solute, SOLUTE_AMOUNT_RANGE.getDefault(), numerator / (double) denominator );
        // Water beaker, with water inside of it
        SolutionNode waterNode = new SolutionNode( cylinderSize, waterBeakerNode.getCylinderEndHeight(), solution, new DoubleRange( 0, 1 ) ) {
            @Override protected Color getColor() {
                return color;
            }
        };

        BeakerNode waterBeakerBackgroundNode = new BeakerNode( 1, sx, sy, null, BEAKER_LABEL_SIZE, BEAKER_LABEL_FONT, 1.0 / denominator, 2, FractionsResources.Images.WATER_GLASS_BACK );
        waterBeakerBackgroundNode.setLabelVisible( false );

        //Show a background layer so that the water appears between the layers (for showing the surface of the water)
        addChild( waterBeakerBackgroundNode );
        addChild( waterNode );
        addChild( waterBeakerNode );

        scale( 0.67 );

        // this node not interactive
        // make pickable here instead of in BeakerNode to maintain compatibility with dilutions implementation.
        waterBeakerNode.setPickable( false );
        waterBeakerNode.setChildrenPickable( false );

        waterBeakerBackgroundNode.setPickable( true );
        waterBeakerBackgroundNode.setChildrenPickable( true );
    }


}
// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.tests;

import junit.framework.TestCase;

import edu.colorado.phet.fractionsintro.intro.model.containerset.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.containerset.Container;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.FactorySet;

/**
 * @author Sam Reid
 */
public class TestSuite extends TestCase {

    public void testAnimateBucketSliceToPie() {
        ContainerSet containerSet = new ContainerSet( 1, new Container[] { new Container( 1, new int[0] ), new Container( 1, new int[0] ), new Container( 1, new int[0] ) } );
        PieSet pieSet = FactorySet.IntroTab().circularSliceFactory.fromContainerSetState( containerSet );
        pieSet = pieSet.animateBucketSliceToPie( new CellPointer( 0, 0 ) );
        pieSet = pieSet.animateBucketSliceToPie( new CellPointer( 1, 0 ) );
        assertEquals( pieSet.toContainerSet(), new ContainerSet( 1, new Container[] {
                new Container( 1, new int[] { 0 } ), new Container( 1, new int[] { 0 } ), new Container( 1, new int[0] ),
                new Container( 1, new int[0] ), new Container( 1, new int[0] ), new Container( 1, new int[0] ) } ) );
    }
}

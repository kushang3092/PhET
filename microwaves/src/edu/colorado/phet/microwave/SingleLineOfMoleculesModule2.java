/**
 * Class: ManyMoleculesModule
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Jun 25, 2003
 */
package edu.colorado.phet.microwave;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.coreadditions.MessageFormatter;
import edu.colorado.phet.coreadditions.chart.StripChartDelegate;
import edu.colorado.phet.coreadditions.collision.Box2D;
import edu.colorado.phet.microwave.model.Microwave;
import edu.colorado.phet.microwave.model.WaterMolecule;
import edu.colorado.phet.microwave.view.DipoleStripChartSubject;
import edu.colorado.phet.microwave.view.MicrowaveStripCharSubject;
import edu.colorado.phet.microwave.view.WaterMoleculeGraphic;
import edu.colorado.phet.util.StripChart;

import javax.swing.*;
import java.awt.*;

public class SingleLineOfMoleculesModule2 extends MicrowaveModule {

    private int fieldWidth = 1000;
    private int fieldHeight = 700;
//    private MicrowaveModel model;
    private Microwave muWave;
    private WaterMolecule molecule;


    public SingleLineOfMoleculesModule2() {
        super( MessageFormatter.format( "Single Line\nof Molecules" ) );
    }

    protected void init() {
        super.init();

        // Put a line of water molecules across the middle of the screen
        Box2D oven = this.getMicrowaveModel().getOven();
        for( int x = (int)( oven.getMinX() + WaterMolecule.s_oxygenRadius + WaterMolecule.s_hydrogenRadius * 2 );
             x < (int)( oven.getMaxX() - WaterMolecule.s_oxygenRadius - WaterMolecule.s_hydrogenRadius * 2 );
             x += WaterMolecule.s_oxygenRadius * 2 + WaterMolecule.s_hydrogenRadius ) {

            molecule = new WaterMolecule();
            molecule.setLocation( x, 200 );
            molecule.setDipoleOrientation( Math.random() * Math.PI * 2 );
            getMicrowaveModel().addPolarBody( molecule );
            WaterMoleculeGraphic moleculeGraphic = new WaterMoleculeGraphic( molecule, getModelViewTransform() );
            getApparatusPanel().addGraphic( moleculeGraphic, 5 );
            molecule.setVisible( true );
        }

        // Add some invisible water molecules to make the temperature more stable
//        for( int x = (int)( oven.getMinX() + WaterMolecule.s_oxygenRadius + WaterMolecule.s_hydrogenRadius * 2 );
//             x < (int)( oven.getMaxX() - WaterMolecule.s_oxygenRadius - WaterMolecule.s_hydrogenRadius * 2 );
//             x += WaterMolecule.s_oxygenRadius * 2 + WaterMolecule.s_hydrogenRadius ) {
//
//            molecule = new WaterMolecule();
//            for( int i = 0; i < 5; i++ ) {
//                molecule.setLocation( x, 250 + i * 20 * ( -1 * i % 2 ) );
//                molecule.setVisible( true );
//                molecule.setDipoleOrientation( Math.random() * Math.PI * 2 );
//                getMicrowaveModel().addPolarBody( molecule );
//            }
//        }
    }

    public void activate( PhetApplication app ) {

        // Create and display a dialog with strip charts for the microwave intensity
        // and the orientation of the water molecule
        JFrame frame = app.getApplicationView().getPhetFrame();
//        StripChartDialog stripChartDialog = new StripChartDialog( frame, muWave, molecule );
//        GraphicsUtil.centerDialogInParent( stripChartDialog );
//        stripChartDialog.show();
    }

    public void deactivate( PhetApplication app ) {
    }

//    public void toggleMicrowave() {
//        if( muWave.getFrequency() == 0 ) {
//            muWave.setFrequency( (float)MicrowaveConfig.s_initFreq );
//            muWave.setMaxAmplitude( 1.0f );
//        }
//        else {
//            muWave.setFrequency( 0f );
//            muWave.setMaxAmplitude( 0f );
//        }
//    }
//

    //
    // Inner classes
    //

    private class StripChartDialog extends JDialog {

        StripChartDialog( JFrame frame, Microwave muWave, WaterMolecule molecule ) {
            super( frame );
            setTitle( "Water molecule orientation" );
            StripChart waterChart = new StripChart( 200, 100, 0, 100, 0, Math.PI * 2, 0.01 );

            DipoleStripChartSubject dscs = new DipoleStripChartSubject( molecule );
            new StripChartDelegate( dscs, waterChart );

            StripChart waveChart = new StripChart( 200, 100, 0, 100, -muWave.getMaxAmplitude(), muWave.getMaxAmplitude(), 0.01 );
//            StripChart waveChart = new StripChart( 200, 100, 0, 100, -Math.PI, Math.PI, 0.01 );
            MicrowaveStripCharSubject escs = new MicrowaveStripCharSubject( muWave );
            new StripChartDelegate( escs, waveChart );

            getContentPane().setLayout( new GridLayout( 2, 1, 10, 10 ) );
            getContentPane().add( waveChart );
            getContentPane().add( waterChart );
            pack();
        }

    }

}

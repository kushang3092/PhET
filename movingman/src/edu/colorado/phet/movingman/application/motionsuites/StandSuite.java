/** Sam Reid*/
package edu.colorado.phet.movingman.application.motionsuites;

import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.elements.Man;
import edu.colorado.phet.movingman.elements.stepmotions.StepMotion;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Aug 13, 2004
 * Time: 10:17:33 AM
 * Copyright (c) Aug 13, 2004 by Sam Reid
 */
public class StandSuite extends MotionSuite {
    public StandSuite( MovingManModule module ) {
        super( module, "Stand Very Still" );
        StepMotion stay = new StepMotion() {
            public double stepInTime( Man man, double dt ) {
                return man.getX();
            }
        };

        JPanel standStillPanel = new JPanel();
        standStillPanel.add( new JLabel( "<html>No controls.<br>Click 'Run Motion' <br>to start standing still.</html>" ) );

        super.setMotion( stay );
        super.setControlPanel( standStillPanel );
    }

    public void initialize( Man man ) {
    }

    public void collidedWithWall() {
    }
}

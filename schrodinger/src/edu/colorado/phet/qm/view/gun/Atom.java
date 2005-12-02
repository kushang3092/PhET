/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.piccolo.pswing.PSwing;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:48 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class Atom extends GunParticle {
    private JSlider mass;
    private PSwing massGraphic;
    private JSlider velocity;
    private PSwing velocityGraphic;

    public Atom( AbstractGun gun, String label, String imageLocation ) {
        super( gun, label, imageLocation );
        mass = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        mass.setBorder( BorderFactory.createTitledBorder( "Mass" ) );
        massGraphic = new PSwing( gun.getComponent(), mass );

        velocity = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        velocity.setBorder( BorderFactory.createTitledBorder( "Velocity" ) );
        velocityGraphic = new PSwing( gun.getComponent(), velocity );
    }

    public void setup( AbstractGun gun ) {
        gun.getSchrodingerModule().getDiscreteModel().setPropagatorModifiedRichardson();

        gun.addChild( massGraphic );
        massGraphic.setOffset( -massGraphic.getWidth() - 2, gun.getComboBoxGraphic().getFullBounds().getMaxY() );

        gun.addChild( velocityGraphic );
        velocityGraphic.setOffset( -velocityGraphic.getWidth() - 2, massGraphic.getFullBounds().getMaxY() );
    }

    public void deactivate( AbstractGun abstractGun ) {
        abstractGun.removeChild( massGraphic );
        abstractGun.removeChild( velocityGraphic );
    }

    public double getStartPy() {
        double velocityValue = new Function.LinearFunction( 0, 1000, 0, 1.5 ).evaluate( velocity.getValue() );
        double massValue = new Function.LinearFunction( 0, 1000, 0, 1 ).evaluate( mass.getValue() );
        return -velocityValue * massValue;
    }

    protected void detachListener( ChangeHandler changeHandler ) {
        mass.removeChangeListener( changeHandler );
        velocity.removeChangeListener( changeHandler );
    }

    protected void hookupListener( ChangeHandler changeHandler ) {
        mass.addChangeListener( changeHandler );
        velocity.addChangeListener( changeHandler );
    }

    public Point getGunLocation() {
        Point p = super.getGunLocation();
        p.y -= AbstractGun.GUN_PARTICLE_OFFSET;
        return p;
    }
}

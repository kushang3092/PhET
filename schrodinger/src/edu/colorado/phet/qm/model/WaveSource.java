/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 15, 2005
 * Time: 9:33:23 AM
 * Copyright (c) Jun 15, 2005 by Sam Reid
 */

public class WaveSource extends DiscreteModel.Adapter {
    private Rectangle region;
    private BoundaryCondition boundaryCondition;
//    private double norm = 1.0;

    public WaveSource( Rectangle region, BoundaryCondition boundaryCondition ) {
        this.region = region;
        this.boundaryCondition = boundaryCondition;
    }

//    public double getNorm() {
//        return norm;
//    }
//
//    public void setNorm( double norm ) {
//        this.norm = norm;
//    }

    public void beforeTimeStep( DiscreteModel model ) {
        for( int i = region.x; i < region.x + region.width; i++ ) {
            for( int k = region.y; k < region.y + region.height; k++ ) {
                if( model.getWavefunction().containsLocation( i, k ) ) {
                    Complex value = boundaryCondition.getValue( i, k, model.getSimulationTime() );
//                    System.out.println( "i="+i+", k="+k+", t="+model.getSimulationTime()+", , value = " + value );
                    model.getWavefunction().setValue( i, k, value );
                }
            }
        }
//        model.getWavefunction().setNorm( norm );
//        Wavefunction.setNorm( model.getWavefunction(), norm );
    }
}

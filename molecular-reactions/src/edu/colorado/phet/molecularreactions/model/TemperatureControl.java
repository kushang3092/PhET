/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.DefaultBody;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.List;

/**
 * TemperatureControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TemperatureControl extends DefaultBody {
    private MRModel model;
    private double setting;

    public TemperatureControl( MRModel model ) {
        this.model = model;
    }

    public void stepInTime( double dt ) {
        if( setting != 0 ) {
            List modelElements = model.getModelElements();
            double scaleFactor = 1 + setting / ( 10 * modelElements.size() );
            for( int i = 0; i < modelElements.size(); i++ ) {
                Object o = modelElements.get( i );
                if( o instanceof SimpleMolecule ) {
                    SimpleMolecule molecule = (SimpleMolecule)o;
                    molecule.setVelocity( molecule.getVelocity().scale( scaleFactor) );
                }
                if( o instanceof CompositeMolecule ) {
                    CompositeMolecule compositeMolecule = (CompositeMolecule)o;
                    compositeMolecule.setOmega( compositeMolecule.getOmega() *  scaleFactor );
                }
            }
        }
    }

    public void setPosition( double x, double y ) {
        super.setPosition( x, y );
        changeListenerProxy.positionChanged( getPosition() );
    }

    public void setPosition( Point2D position ) {
        super.setPosition( position );
        changeListenerProxy.positionChanged( getPosition() );
    }

    public double getSetting() {
        return setting;
    }

    public void setSetting( double setting ) {
        this.setting = setting;
        changeListenerProxy.settingChanged( setting );
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------

    public interface ChangeListener extends EventListener {
        void settingChanged( double setting );
        void positionChanged( Point2D newPosition );
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}

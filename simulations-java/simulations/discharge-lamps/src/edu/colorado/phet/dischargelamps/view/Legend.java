/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.quantum.model.Photon;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.dischargelamps.model.HydrogenProperties;
import edu.colorado.phet.lasers.view.AnnotatedAtomGraphic;
import edu.colorado.phet.lasers.view.PhotonGraphic;

/*
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Legend extends JPanel {
    private GridBagConstraints iconGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                 1, 1, 1, 1,
                                                                 GridBagConstraints.CENTER,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets( 0, 60, 5, 10 ),
                                                                 0, 0 );
    private GridBagConstraints labelGbc = new GridBagConstraints( 1, GridBagConstraints.RELATIVE,
                                                                  1, 1, 1, 1,
                                                                  GridBagConstraints.WEST,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets( 0, 0, 5, 10 ),
                                                                  0, 0 );

    public Legend() {
        super( new GridBagLayout() );
        setBorder( new TitledBorder( SimStrings.getInstance().getString( "Legend.title" ) ) );

        add( getAtomImage(), "Legend.atom" );
        add( getElectronImage(), "Legend.electron" );
        add( getPhotonImage(), "Legend.photon" );
    }

    private BufferedImage getElectronImage() {
        BufferedImage electronImage = null;
        try {
            electronImage = ImageLoader.loadBufferedImage( DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return electronImage;
    }

    private BufferedImage getPhotonImage() {
        Photon photon = new Photon( 400, new Point2D.Double(), new Vector2D.Double() );
        BufferedImage photonImage = PhotonGraphic.getInstance( this, photon ).getImage();
        return photonImage;
    }

    private BufferedImage getAtomImage() {
        DischargeLampAtom atom = new DischargeLampAtom( new DischargeLampModel(), new HydrogenProperties() );
        AnnotatedAtomGraphic atomGraphic = new AnnotatedAtomGraphic( this, atom );
        BufferedImage atomBI = new BufferedImage( atomGraphic.getWidth(), atomGraphic.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D g2BI = (Graphics2D) atomBI.getGraphics();
        g2BI.translate( atomGraphic.getWidth() / 2, atomGraphic.getHeight() / 2 );
        g2BI.scale( .8, .8 );
        atomGraphic.paint( g2BI );
        return atomBI;
    }

    public void add( Image image, String key ) {
        add( new JLabel( new ImageIcon( image ) ), iconGbc );
        add( new JLabel( SimStrings.getInstance().getString( key ) ), labelGbc );
    }

}
/**
 * Class: SingleSourceApparatusPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.model.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SingleSourceApparatusPanel extends SoundApparatusPanel {

    private PhetImageGraphic speakerFrame;
    private PhetImageGraphic speakerCone;
    private WavefrontGraphic wavefrontGraphic = null;
    private BufferedImage speakerFrameImg;
    private BufferedImage speakerConeImg;
    private int audioSource = SPEAKER_SOURCE;
    private boolean audioEnabledOnActivation;


    /**
     * @param model
     * @param name
     */
    public SingleSourceApparatusPanel( SoundModel model, String name ) {
        super( model );
        this.model = model;
        final WaveMedium waveMedium = model.getWaveMedium();
        wavefrontGraphic = new WavefrontGraphic( waveMedium, this );
        this.addGraphic( wavefrontGraphic, 7 );
        Point2D.Double audioSource = new Point2D.Double( SoundConfig.s_wavefrontBaseX,
                                                         SoundConfig.s_wavefrontBaseY );
        wavefrontGraphic.initLayout( audioSource,
                                     SoundConfig.s_wavefrontHeight,
                                     SoundConfig.s_wavefrontRadius );

        // Set up the octave wavefront and graphic
        WavefrontGraphic wgB = new WavefrontGraphic( waveMedium, this );
        //        wavefrontGraphic.init( waveMedium );
        this.addGraphic( wavefrontGraphic, 7 );
        Point2D.Double audioSourceB = new Point2D.Double( SoundConfig.s_wavefrontBaseX,
                                                          SoundConfig.s_wavefrontBaseY );
        wavefrontGraphic.initLayout( audioSourceB,
                                     SoundConfig.s_wavefrontHeight,
                                     SoundConfig.s_wavefrontRadius );

        setWavefrontType( new SphericalWavefront() );
        this.setBackground( SoundConfig.MIDDLE_GRAY );

        // Set up the speaker
        try {
            speakerFrameImg = ImageLoader.loadBufferedImage( SoundConfig.SPEAKER_FRAME_IMAGE_FILE );
            speakerConeImg = ImageLoader.loadBufferedImage( SoundConfig.SPEAKER_CONE_IMAGE_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( "Image files not found" );
        }
        speakerFrame = new PhetImageGraphic( this, speakerFrameImg );
        speakerFrame.setPosition( SoundConfig.s_speakerBaseX,
                                  SoundConfig.s_wavefrontBaseY - speakerFrameImg.getHeight( null ) / 2 );
        speakerCone = new PhetImageGraphic( this, speakerConeImg );
        speakerCone.setPosition( SoundConfig.s_speakerBaseX + s_speakerConeOffsetX,
                                 SoundConfig.s_wavefrontBaseY - speakerConeImg.getHeight( null ) / 2 );
        this.addGraphic( speakerFrame, 8 );
        this.addGraphic( speakerCone, 9 );
        waveMedium.addObserver( new SimpleObserver() {
            private int s_maxSpeakerConeExcursion = 6;

            public void update() {
                int coneOffset = (int)( waveMedium.getAmplitudeAt( 0 ) / SoundConfig.s_maxAmplitude * s_maxSpeakerConeExcursion );
                speakerCone.setPosition( SoundConfig.s_speakerBaseX + s_speakerConeOffsetX + coneOffset,
                                         SoundConfig.s_wavefrontBaseY - speakerConeImg.getHeight( null ) / 2 );
            }
        } );

        // Initialize the head
        BufferedImage headImg = null;
        try {
            headImg = ImageLoader.loadBufferedImage( SoundConfig.HEAD_IMAGE_FILE );
            PhetImageGraphic head = new PhetImageGraphic( this, headImg );
            head.setPosition( SoundConfig.s_headBaseX, SoundConfig.s_headBaseY );
            ListenerGraphic listener = new ListenerGraphic( model, new Listener(), head,
                                                            SoundConfig.s_headBaseX, SoundConfig.s_headBaseY,
                                                            SoundConfig.s_headBaseX - 150, SoundConfig.s_headBaseY,
                                                            SoundConfig.s_headBaseX + 150, SoundConfig.s_headBaseY );
            this.addGraphic( listener, 9 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

    }

    /**
     *
     */
    public void init() {
    }

    /**
     * @return
     */
    protected Image getSpeakerFrameImg() {
        return speakerFrameImg;
    }

    /**
     * @return
     */
    protected Image getSpeakerConeImg() {
        return speakerConeImg;
    }

    /**
     *
     */
    public void setWavefrontType( WavefrontType wavefrontType ) {
        wavefrontGraphic.setPlanar( wavefrontType instanceof PlaneWavefront );
    }

    /**
     * @return
     */
    protected WavefrontGraphic getWavefrontGraphic() {
        return wavefrontGraphic;
    }

    /**
     * TODO: refactor this class so that we don't need this to make interference work
     *
     * @return
     */
    protected PhetGraphic getSpeakerFrame() {
        return speakerFrame;
    }

    /**
     * TODO: refactor this class so that we don't need this to make interference work
     *
     * @return
     */
    protected PhetGraphic getSpeakerCone() {
        return speakerCone;
    }

    /**
     * Gets the amplitude at the speaker or the listener, depending on what is
     * specified by the control panel
     *
     * @param waveFront
     * @return
     */
    public double getCurrentMaxAmplitude( Wavefront waveFront ) {
        return waveFront.getMaxAmplitude();
    }

    /**
     *
     */
    public void setAudioSource( int audioSource ) {
        this.audioSource = audioSource;
    }

    /**
     * @return
     */
    protected int getAudioSource() {
        return audioSource;
    }
}

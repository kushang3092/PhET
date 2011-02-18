// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Sam Reid
 */
public class LightReflectionAndRefractionCanvas<T extends LRRModel> extends PhetPCanvas {
    public static final PhetFont labelFont = new PhetFont( 16 );
    private PNode rootNode;
    public final BooleanProperty showNormal = new BooleanProperty( true );
    public final BooleanProperty showProtractor = new BooleanProperty( false );
    public final Property<LaserView> laserView = new Property<LaserView>( LaserView.RAY );
    protected final PNode mediumNode;
    protected final T model;
    protected final ModelViewTransform transform;
    protected final PDimension stageSize;
    protected final PNode rayLayer = new PNode() {
        @Override
        public void fullPaint( PPaintContext paintContext ) {
            Composite c = paintContext.getGraphics().getComposite();
            paintContext.getGraphics().setComposite( new Composite() {
                public CompositeContext createContext( ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints ) {
                    return new CompositeContext() {
                        public void dispose() {
                        }

                        //See http://www.java-gaming.org/index.php?topic=22467.0
                        public void compose( Raster src, Raster dstIn, WritableRaster dstOut ) {
                            int chan1 = src.getNumBands();
                            int chan2 = dstIn.getNumBands();

                            int minCh = Math.min( chan1, chan2 );

                            int[] pxSrc = null;
                            int[] pxDst = null;

//                            System.out.println( "dstIn.getWidth() = " + dstIn.getWidth() + ", h = " + dstIn.getHeight() );
                            //This bit is horribly inefficient,
                            //getting individual pixels rather than all at once.
                            for ( int x = 0; x < dstIn.getWidth(); x++ ) {
                                for ( int y = 0; y < dstIn.getHeight(); y++ ) {

                                    pxSrc = src.getPixel( x, y, pxSrc );
                                    pxDst = dstIn.getPixel( x, y, pxDst );

                                    for ( int i = 0; i < 3 && i < minCh; i++ ) {
                                        pxDst[i] = Math.min( 255, pxSrc[i] + pxDst[i] );
                                    }
                                    dstOut.setPixel( x, y, pxDst );
                                }
                            }
                        }
                    };
                }
            } );
            super.fullPaint( paintContext );
            paintContext.getGraphics().setComposite( c );
        }
    };

    public LightReflectionAndRefractionCanvas( final T model, final Function1<Double, Double> clampDragAngle, final Function1<Double, Boolean> clockwiseArrowNotAtMax, final Function1<Double, Boolean> ccwArrowNotAtMax ) {
        this.model = model;
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBackground( Color.black );
        final int stageWidth = 1008;
        stageSize = new PDimension( stageWidth, stageWidth * model.getHeight() / model.getWidth() );

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, stageSize ) );

        final double scale = stageSize.getHeight() / model.getHeight();
        transform = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                               new Point2D.Double( stageSize.getWidth() / 2 - 150, stageSize.getHeight() / 2 ),
                                                                               scale );
        mediumNode = new PNode();
        addChild( mediumNode );

        final BooleanProperty showDragHandles = new BooleanProperty( false );
        addChild( new LaserNodeDragHandle( transform, model.getLaser(), 10, showDragHandles, clockwiseArrowNotAtMax ) );
        addChild( new LaserNodeDragHandle( transform, model.getLaser(), -10, showDragHandles, ccwArrowNotAtMax ) );
        addChild( new LaserNode( transform, model.getLaser(), showDragHandles, clampDragAngle ) );

        laserView.addObserver( new SimpleObserver() {
            public void update() {
                model.updateModel();//TODO: Maybe it would be better just to regenerate view, but now we just do this by telling the model to recompute and repopulate
            }
        } );

        final VoidFunction1<LightRay> addLightRayNode = new VoidFunction1<LightRay>() {
            public void apply( LightRay lightRay ) {
                final PNode node = laserView.getValue().createNode( transform, lightRay );
                rayLayer.addChild( node );
                lightRay.addMoveToFrontListener( new VoidFunction0() {
                    public void apply() {
                        node.moveToFront();
                    }
                } );//TODO: memory leak
                lightRay.addRemovalListener( new VoidFunction0() {
                    public void apply() {
                        rayLayer.removeChild( node );
                    }
                } );
            }
        };
        for ( LightRay lightRay : model.getRays() ) {
            addLightRayNode.apply( lightRay );
        }
        model.addRayAddedListener( new VoidFunction1<LightRay>() {
            public void apply( final LightRay lightRay ) {
                addLightRayNode.apply( lightRay );
            }
        } );

        //No time readout
        addChild( new FloatingClockControlNode( new BooleanProperty( true ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    if ( getValue() ) { model.getClock().start(); }
                    else { model.getClock().pause(); }
                }
            } );
        }}, null, model.getClock(), "Reset", new Property<Color>( Color.white ) ) {{
            setOffset( stageSize.width * 3 / 4 - getFullBounds().getWidth() / 2, stageSize.getHeight() - getFullBounds().getHeight() );
            laserView.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( laserView.getValue().equals( LaserView.WAVE ) );
                }
            } );
        }} );
        addChild( rayLayer );

        //Debug for showing stage
//        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, STAGE_SIZE.getWidth(), STAGE_SIZE.getHeight() ), new BasicStroke( 2 ), Color.red ) );
    }

    public void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    public void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }
}

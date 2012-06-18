package edu.colorado.phet.fractionsintro.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory.Listener;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.ToggleButtonNode;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.view.SpinnerButtonNode;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.Scene;
import edu.colorado.phet.fractionsintro.buildafraction.view.numbers.NumberSceneContext;
import edu.colorado.phet.fractionsintro.buildafraction.view.numbers.NumberSceneNode;
import edu.colorado.phet.fractionsintro.buildafraction.view.pictures.PictureSceneNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.common.view.Colors;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.PaddedIcon;
import edu.colorado.phet.fractionsintro.matchinggame.view.PatternNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the build a fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas implements NumberSceneContext {

    private static final int rgb = 240;
    public static final Color CONTROL_PANEL_BACKGROUND = new Color( rgb, rgb, rgb );

    public static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    public static final Stroke controlPanelStroke = new BasicStroke( 2 );

    //Layer that is panned over vertically to switch between numbers and pictures.
    //Couldn't use camera for this since it is difficult to make camera objects scale up and down with the canvas but not translate with the screens
    private final PNode sceneLayer = new PNode();
    private final SceneNode numberScene = new SceneNode();
    private final SceneNode pictureScene = new SceneNode();
    private final BuildAFractionModel model;

    //Flag is just used for debugging, so the first scene change is not done in animation
    private boolean initialized = false;
    private static final int FADE_IN_TIME = 200;
    private static final int FADE_OUT_TIME = 1000;
    private static final PhetFont scoreboardFont = new PhetFont( 26, true );

    public BuildAFractionCanvas( final BuildAFractionModel model, final boolean dev ) {
        this.model = model;
        addChild( sceneLayer );
        pictureScene.addChild( new PictureSceneNode( model.numberLevel.get(), rootNode, model, STAGE_SIZE, this ) );
        numberScene.addChild( new NumberSceneNode( model.numberLevel.get(), rootNode, model, STAGE_SIZE, this ) {{
            setOffset( 0, STAGE_SIZE.height );
        }} );
        sceneLayer.addChild( numberScene );
        sceneLayer.addChild( pictureScene );

        //Add reset button to a layer that won't pan
        final ResetAllButtonNode resetButton = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
                reloadViewsAfterReset( model );
            }
        }, this, 18, Color.black, Color.orange ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - INSET, STAGE_SIZE.getHeight() - getFullBounds().getHeight() - INSET );
            setConfirmationEnabled( false );
        }};
        addChild( resetButton );

        //Set a really light blue because there is a lot of white everywhere
        setBackground( new Color( 236, 251, 251 ) );

        if ( dev ) {

            addChild( new VBox( new TextButtonNode( "Resample", resetButton.getFont(), Color.red ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( final ActionEvent e ) {
                        model.resample();
                        reloadViewsAfterReset( model );
                    }
                } );

            }}, new TextButtonNode( "Background", resetButton.getFont(), Color.green ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( final ActionEvent e ) {
                        ColorChooserFactory.showDialog( "background", BuildAFractionCanvas.this, BuildAFractionCanvas.this.getBackground(), new Listener() {
                            public void colorChanged( final Color color ) {
                                BuildAFractionCanvas.this.setBackground( color );
                            }

                            public void ok( final Color color ) {
                                BuildAFractionCanvas.this.setBackground( color );
                            }

                            public void cancelled( final Color originalColor ) {
                                BuildAFractionCanvas.this.setBackground( originalColor );
                            }
                        }, true );
                    }
                } );
            }}
            ) {{
                setOffset( resetButton.getFullBounds().getX(), resetButton.getFullBounds().getY() - getFullBounds().getHeight() - INSET );
            }} );
        }

        final FractionNode node1 = new FractionNode( Fraction.fraction( 1, 1 ), 0.2 );
        final PatternNode node2 = new PatternNode( FilledPattern.sequentialFill( Pattern.pie( 1 ), 1 ), Colors.LIGHT_BLUE ) {{scale( 0.5 );}};
        double maxWidth = Math.max( node1.getFullBounds().getWidth(), node2.getFullBounds().getWidth() );
        double maxHeight = Math.max( node1.getFullBounds().getHeight(), node2.getFullBounds().getHeight() );
        double maxSize = Math.max( maxWidth, maxHeight );

        addChild( new VBox( new ToggleButtonNode( new PaddedIcon( maxSize, maxSize, node2 ), model.selectedScene.valueEquals( Scene.pictures ), new VoidFunction0() {
            public void apply() {
                model.selectedScene.set( Scene.pictures );
            }
        } ), new ToggleButtonNode( new PaddedIcon( maxSize, maxSize, node1 ), model.selectedScene.valueEquals( Scene.numbers ), new VoidFunction0() {
            public void apply() {
                model.selectedScene.set( Scene.numbers );
            }
        } ) ) {{
            setOffset( INSET, INSET / 2 );//Inset manually tuned to line up mode, level and score
        }} );

        final SpinnerButtonNode leftButtonNode = new SpinnerButtonNode( spinnerImage( Images.LEFT_BUTTON_UP ), spinnerImage( Images.LEFT_BUTTON_PRESSED ), spinnerImage( Images.LEFT_BUTTON_GRAY ), new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                goToNumberLevel( model.numberLevel.get() - 1 );
            }
        }, model.numberLevel.greaterThan( 0 ) );
        final SpinnerButtonNode rightButtonNode = new SpinnerButtonNode( spinnerImage( Images.RIGHT_BUTTON_UP ), spinnerImage( Images.RIGHT_BUTTON_PRESSED ), spinnerImage( Images.RIGHT_BUTTON_GRAY ), new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                goToNumberLevel( model.numberLevel.get() + 1 );
            }
        }, model.numberLevel.lessThan( model.numberLevels.size() - 1 ) );
        addChild( rightButtonNode );

        //Level indicator and navigation buttons for number mode
        addChild( new HBox( 30, leftButtonNode, new PhetPText( "Level 100", scoreboardFont ) {{
            model.numberLevel.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer integer ) {
                    setText( "Level " + ( integer + 1 ) );
                }
            } );
        }}, rightButtonNode ) {{
            setOffset( 300, INSET );
        }} );

        model.selectedScene.addObserver( new VoidFunction1<Scene>() {
            public void apply( final Scene scene ) {
                if ( scene == Scene.pictures ) {
                    numberScene.animateToTransparency( 0.0f, FADE_OUT_TIME );
                    pictureScene.animateToTransparency( 1f, FADE_IN_TIME );
                    sceneLayer.animateToPositionScaleRotation( 0, 0, 1, 0, initialized ? 1000 : 0 );
                }
                else if ( scene == Scene.numbers ) {
                    numberScene.animateToTransparency( 1.0f, FADE_IN_TIME );
                    pictureScene.animateToTransparency( 0.0f, FADE_OUT_TIME );
                    sceneLayer.animateToPositionScaleRotation( 0, -STAGE_SIZE.height, 1, 0, initialized ? 1000 : 0 );
                }
            }
        } );
        initialized = true;
    }

    private void reloadViewsAfterReset( final BuildAFractionModel model ) {
        numberScene.reset();
        numberScene.addChild( new NumberSceneNode( model.numberLevel.get(), rootNode, model, STAGE_SIZE, BuildAFractionCanvas.this ) {{
            setOffset( 0, STAGE_SIZE.height );
        }} );
        pictureScene.reset();
        pictureScene.addChild( new PictureSceneNode( model.numberLevel.get(), rootNode, model, STAGE_SIZE, BuildAFractionCanvas.this ) );
    }

    private BufferedImage spinnerImage( final BufferedImage image ) { return BufferedImageUtils.multiScaleToHeight( image, 30 ); }

    public void goToNumberLevel( int level ) {
        model.goToNumberLevel( level );
        for ( Object node : numberScene.getChildrenReference() ) {
            PNode n2 = (PNode) node;
            //Fade out the other levels, but not the target one (if it already exists)
            if ( n2 != getNumberSceneNode( level ) ) {
                n2.animateToTransparency( 0.0f, FADE_OUT_TIME );
            }
        }
        if ( getNumberSceneNode( level ) == null ) {
            numberScene.addChild( new NumberSceneNode( model.numberLevel.get(), rootNode, model, STAGE_SIZE, this ) {{
                setOffset( STAGE_SIZE.width * model.numberLevel.get(), STAGE_SIZE.height );
            }} );
        }
        else {
            getNumberSceneNode( level ).animateToTransparency( 1.0f, FADE_IN_TIME );
        }
        numberScene.animateToTransform( AffineTransform.getTranslateInstance( -STAGE_SIZE.getWidth() * model.numberLevel.get(), 0 ), 1000 );
    }

    private NumberSceneNode getNumberSceneNode( final int level ) {
        for ( Object child : numberScene.getChildrenReference() ) {
            PNode node = (PNode) child;
            if ( node instanceof NumberSceneNode && ( (NumberSceneNode) node ).level == level ) {
                return (NumberSceneNode) node;
            }
        }
        return null;
    }

    public void nextNumberLevel() { goToNumberLevel( model.numberLevel.get() + 1 ); }
}
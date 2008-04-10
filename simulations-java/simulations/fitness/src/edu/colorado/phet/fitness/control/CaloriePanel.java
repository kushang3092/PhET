package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.bargraph.BarGraph;
import edu.colorado.phet.fitness.FitnessResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Created by: Sam
 * Apr 9, 2008 at 8:59:34 PM
 */
public class CaloriePanel extends PNode {
    private CalorieSlider calorieSlider;
    private CalorieSlider burnSlider;

    public CaloriePanel() {
        PNode foodStrip = new IconStrip( new IconStrip.IconItem[]{
                new IconStrip.IconItem( "burger.png" ),
                new IconStrip.IconItem( "strawberry.png" ),
                new IconStrip.IconItem( "bananasplit.png" ),
                new IconStrip.IconItem( "grapefruit.png" ),
                new IconStrip.IconItem( "burger.png" ),
                new IconStrip.IconItem( "strawberry.png" ),
                new IconStrip.IconItem( "bananasplit.png" ),
                new IconStrip.IconItem( "grapefruit.png" ),
        } );
        calorieSlider = new CalorieSlider( "Caloric Intake", Color.green, true );
        addChild( calorieSlider );

        burnSlider = new CalorieSlider( "Caloric Burn", Color.red, false );
        burnSlider.addRegion( 0, 50, new Color( 225, 138, 138 ) );
        addChild( burnSlider );

        PImage plateNode = new PImage( FitnessResources.getImage( "plate.png" ) );
//            im = new PImage( ImageLoader.loadBufferedIm
// age( "fitness/images/plate.png" ) );

        plateNode.setOffset( 0, 70 );
        addChild( plateNode );
        addChild( foodStrip );
        calorieSlider.setOffset( 0, 175 );
        burnSlider.setOffset( 0, calorieSlider.getFullBounds().getMaxY() + 5 );

        BarGraph foodCompositionBarChart = new BarGraph( "Food Composition", 200, Color.white, 200 );
        foodCompositionBarChart.setVariables( new BarGraph.Variable[]{
                new BarGraph.Variable( "Carbs", 0.7, Color.green ),
                new BarGraph.Variable( "Proteins", 0.3, Color.red ),
                new BarGraph.Variable( "Lipids", 0.5, Color.blue ),
        } );
        foodCompositionBarChart.setOffset( calorieSlider.getFullBounds().getMaxX() + 10, foodStrip.getFullBounds().getMaxY() + 5 );
        addChild( foodCompositionBarChart );


        BarGraph exerciseChart = new BarGraph( "Exercise", 200 / 24, Color.white, 200 );//hours
        exerciseChart.setVariables( new BarGraph.Variable[]{
                new BarGraph.Variable( "Activity", 10, Color.red ),
                new BarGraph.Variable( "Relaxing", 12, Color.blue ),
                new BarGraph.Variable( "Sleep", 8, Color.green ),
        } );
//        exerciseChart.setOffset( foodCompositionBarChart.getFullBounds().getX(), burnSlider.getFullBounds().getY() );
        exerciseChart.setOffset( foodCompositionBarChart.getFullBounds().getMaxX() + 50, foodCompositionBarChart.getFullBounds().getY() );
        addChild( exerciseChart );

        PhetPPath activityAcceptor = new PhetPPath( new Rectangle2D.Double( 0, 0, 50, 50 ), new BasicStroke( 2 ), Color.black );
        PText activityText = new PText( "Activity" );
        activityText.setOffset( 3, 1 );
        PNode activityAcceptorNode = new PNode();
        activityAcceptorNode.addChild( activityAcceptor );
        activityAcceptorNode.addChild( activityText );
        activityAcceptorNode.setOffset( burnSlider.getFullBounds().getCenterX() - activityAcceptorNode.getFullBounds().getWidth() / 2, burnSlider.getFullBounds().getMaxY() + 5 );
        addChild( activityAcceptorNode );

        PNode exerciseStrip = new IconStrip( new IconStrip.IconItem[]{
                new IconStrip.IconItem( "bike.png" ),
                new IconStrip.IconItem( "swim.png" ),
                new IconStrip.IconItem( "bike.png" ),
                new IconStrip.IconItem( "swim.png" ),
                new IconStrip.IconItem( "bike.png" ),
                new IconStrip.IconItem( "swim.png" ),
        } );
        exerciseStrip.setOffset( 0, activityAcceptorNode.getFullBounds().getMaxY() + 10 );
        addChild( exerciseStrip );

    }

    private static class CalorieSlider extends PNode {
        public static double WIDTH = 200;
        public static double HEIGHT = 30;
        private PhetPPath arrowNode;
        private PText titleNode;
        private PhetPPath borderNode;

        public CalorieSlider( String title, Color color, boolean arrowDown ) {
            titleNode = new PText( title );
            addChild( titleNode );
            borderNode = new PhetPPath( new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT ), new BasicStroke( 1.5f ), Color.black );
            addChild( borderNode );
            arrowNode = new PhetPPath( new Arrow( new Point2D.Double( 0, arrowDown ? 0 : HEIGHT ), new Point2D.Double( 0, arrowDown ? HEIGHT : 0 ), 12, 12, 7 ).getShape(), color );

            arrowNode.addInputEventListener( new CursorHandler() );
            arrowNode.addInputEventListener( new PDragEventHandler() {
                protected void drag( PInputEvent event ) {
//                    super.drag( event );
                    PDimension d = event.getDeltaRelativeTo( getDraggedNode() );
                    getDraggedNode().localToParent( d );
                    getDraggedNode().offset( d.getWidth(), 0 );
                    if ( getDraggedNode().getOffset().getX() < 0 ) {
                        getDraggedNode().setOffset( 0, getDraggedNode().getOffset().getY() );
                    }
                    else if ( getDraggedNode().getOffset().getX() > WIDTH ) {
                        getDraggedNode().setOffset( WIDTH, getDraggedNode().getOffset().getY() );
                    }
                }
            } );
//            titleNode.setOffset( 0, -titleNode.getHeight() );
            borderNode.setOffset( 0, titleNode.getHeight() );
            arrowNode.setOffset( 0, titleNode.getHeight() );

            PText low = new PText( "None" );
            addChild( low );
            low.setOffset( -low.getFullBounds().getWidth(), borderNode.getFullBounds().getCenterY() - low.getFullBounds().getHeight() / 2 );
            PText lots = new PText( "Lots" );
            addChild( lots );
            lots.setOffset( borderNode.getFullBounds().getWidth(), borderNode.getFullBounds().getCenterY() - low.getFullBounds().getHeight() / 2 );

            addChild( arrowNode );
        }

        public void addRegion( int x0, int x1, Color color ) {
            PhetPPath path = new PhetPPath( new Rectangle2D.Double( x0, 0, x1, HEIGHT ), color );
            path.setOffset( 0, titleNode.getHeight() );
            addChild( path );
            borderNode.moveToFront();
            arrowNode.moveToFront();
        }
    }

    static class BoxedImage extends PImage {
        private boolean drawBorder = true;

        public BoxedImage( BufferedImage image ) {
            super( image );
        }

        public boolean isDrawBorder() {
            return drawBorder;
        }

        public void setDrawBorder( boolean drawBorder ) {
            this.drawBorder = drawBorder;
        }

        protected void paint( PPaintContext paintContext ) {
            if ( drawBorder ) {
                Graphics2D g2 = paintContext.getGraphics();
                g2.setPaint( Color.black );
                g2.setStroke( new BasicStroke( 1 ) );
                g2.draw( getBoundsReference() );
            }
            super.paint( paintContext );
        }
    }

    private static class IconStrip extends PNode {
        double height = 60;


        private IconStrip( IconItem[] iconItems ) {
            final BoxedImage[] im = new BoxedImage[iconItems.length];
            for ( int i = 0; i < iconItems.length; i++ ) {
                final IconItem foodItem = iconItems[i];
                im[i] = createNode( foodItem, i, im );
                final int i1 = i;
                im[i].addInputEventListener( new PDragEventHandler() {
                    BoxedImage createdNode = null;

                    protected void startDrag( PInputEvent event ) {
                        super.startDrag( event );
                        createdNode = createNode( foodItem, i1, im );
                        createdNode.addInputEventListener( new PDragEventHandler() {
                            protected void startDrag( PInputEvent event ) {
                                super.startDrag( event );
                                createdNode.moveToFront();
                            }
                        } );
                        createdNode.setDrawBorder( false );
                        IconStrip.this.addChild( createdNode );
                        createdNode.moveToFront();
                    }

                    protected void drag( PInputEvent event ) {
                        PDimension d = event.getDeltaRelativeTo( im[i1] );
                        createdNode.localToParent( d );
                        createdNode.offset( d.getWidth(), d.getHeight() );
                    }
                } );
            }
        }

        private BoxedImage createNode( IconItem foodItem, int index, BoxedImage[] others ) {
            BoxedImage imx = new BoxedImage( FitnessResources.getImage( foodItem.getImageName() ) );
            imx.scale( height / imx.getFullBounds().getHeight() );
            addChild( imx );
            imx.addInputEventListener( new CursorHandler() );
            if ( index > 0 ) {
                imx.setOffset( others[index - 1].getFullBounds().getMaxX(), others[index - 1].getFullBounds().getY() );
            }

            return imx;
        }

        private static class IconItem {
            private String image;

            public IconItem( String image ) {
                this.image = image;
            }

            public String getImageName() {
                return image;
            }
        }
    }
}

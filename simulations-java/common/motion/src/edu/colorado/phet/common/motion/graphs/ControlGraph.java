package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.DynamicJFreeChartNode;
import edu.colorado.phet.common.motion.model.SimulationVariable;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * The ControlGraph displays a graph of data for (multiple) time series data,
 * including horziontal and vertical zoom in/out and (optionally) a slider control for changing the data.
 *
 * @author Sam Reid
 */
public class ControlGraph extends PNode {
    private JFreeChart jFreeChart;
    private DynamicJFreeChartNode dynamicJFreeChartNode;

    private GraphTimeControlNode graphTimeControlNode;
    private JFreeChartSliderNode JFreeChartSliderNode;
    private ZoomSuiteNode zoomControl;
    private PNode titleLayer = new PNode();

    private int minDomainValue = 1000;
    private double ZOOM_FRACTION = 1.1;
    private Layout layout = new FlowLayout();
    private ArrayList series = new ArrayList();
    private ArrayList listeners = new ArrayList();

    public ControlGraph( PhetPCanvas pSwingCanvas, final SimulationVariable simulationVariable,
                         String abbr, String title, double minY, double maxY, TimeSeriesModel timeSeriesModel ) {
        this( pSwingCanvas, simulationVariable, abbr, title, minY, maxY, Color.black, new PText( "THUMB" ), timeSeriesModel );
    }

    public ControlGraph( PhetPCanvas pSwingCanvas, final SimulationVariable simulationVariable,
                         String abbr, String title, double minY, final double maxY, Color color, PNode thumb, TimeSeriesModel timeSeriesModel ) {
        XYDataset dataset = new XYSeriesCollection( new XYSeries( "dummy series" ) );
        jFreeChart = ChartFactory.createXYLineChart( title + ", " + abbr, null, null, dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChart.setTitle( (String)null );
        jFreeChart.getXYPlot().getRangeAxis().setRange( minY, maxY );
        jFreeChart.getXYPlot().getDomainAxis().setRange( 0, minDomainValue );
        jFreeChart.setBackgroundPaint( null );

        dynamicJFreeChartNode = new DynamicJFreeChartNode( pSwingCanvas, jFreeChart );
        dynamicJFreeChartNode.setBuffered( true );
        dynamicJFreeChartNode.setBounds( 0, 0, 300, 400 );
        dynamicJFreeChartNode.setPiccoloSeries();
//        dynamicJFreeChartNode.setJFreeChartSeries();
//        dynamicJFreeChartNode.setBufferedSeries();
//        dynamicJFreeChartNode.setBufferedImmediateSeries();

        graphTimeControlNode = new GraphTimeControlNode( timeSeriesModel );
        addSeries( title, color, abbr, simulationVariable );
        JFreeChartSliderNode = new JFreeChartSliderNode( dynamicJFreeChartNode, thumb );
        zoomControl = new ZoomSuiteNode();
        zoomControl.addVerticalZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                zoomVertical( ZOOM_FRACTION );
            }

            public void zoomedIn() {
                zoomVertical( 1.0 / ZOOM_FRACTION );
            }
        } );
        zoomControl.addHorizontalZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                zoomHorizontal( ZOOM_FRACTION );
            }

            public void zoomedIn() {
                zoomHorizontal( 1.0 / ZOOM_FRACTION );
            }
        } );

        addChild( graphTimeControlNode );
        addChild( JFreeChartSliderNode );
        addChild( dynamicJFreeChartNode );
        addChild( zoomControl );
        addChild( titleLayer );

        simulationVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                JFreeChartSliderNode.setValue( simulationVariable.getValue() );
            }
        } );
        JFreeChartSliderNode.addListener( new JFreeChartSliderNode.Listener() {
            public void valueChanged() {
                simulationVariable.setValue( JFreeChartSliderNode.getValue() );
            }

            public void sliderThumbGrabbed() {
                notifyControlGrabbed();
            }
        } );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                notifyListeners();
            }
        } );
        dynamicJFreeChartNode.updateChartRenderingInfo();
        relayout();
        updateZoomEnabled();

        //for debugging, attach listeners that allow change of rendering style.
        addInputEventListener( new PBasicInputEventHandler() {
            public void keyPressed( PInputEvent event ) {
                if( event.getKeyCode() == KeyEvent.VK_1 ) {
                    dynamicJFreeChartNode.setJFreeChartSeries();
                }
                else if( event.getKeyCode() == KeyEvent.VK_2 ) {
                    dynamicJFreeChartNode.setPiccoloSeries();
                }
                else if( event.getKeyCode() == KeyEvent.VK_3 ) {
                    dynamicJFreeChartNode.setBufferedSeries();
                }
                else if( event.getKeyCode() == KeyEvent.VK_4 ) {
                    dynamicJFreeChartNode.setBufferedImmediateSeries();
                }
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                event.getInputManager().setKeyboardFocus( event.getPath() );
            }
        } );
    }

    public DynamicJFreeChartNode getDynamicJFreeChartNode() {
        return dynamicJFreeChartNode;
    }

    private void notifyControlGrabbed() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.controlFocusGrabbed();
        }
    }

    public void addHorizontalZoomListener( ZoomControlNode.ZoomListener zoomListener ) {
        zoomControl.addHorizontalZoomListener( zoomListener );
    }

    static class TitleNode extends PNode {

        public TitleNode( String title, String abbr, Color color ) {
            ShadowPText titlePText = new ShadowPText( title + ", " + abbr );
            titlePText.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
            titlePText.setTextPaint( color );
            addChild( new PhetPPath( RectangleUtils.expand( titlePText.getFullBounds(), 2, 2 ), Color.white, new BasicStroke(), Color.black ) );
            addChild( titlePText );
        }
    }

    private void zoomHorizontal( double v ) {
        double currentValue = jFreeChart.getXYPlot().getDomainAxis().getUpperBound();
        double newValue = currentValue * v;
        if( newValue > minDomainValue ) {
            newValue = minDomainValue;
        }
        jFreeChart.getXYPlot().getDomainAxis().setUpperBound( newValue );
        updateZoomEnabled();
    }

    private void zoomVertical( double v ) {
        double currentRange = jFreeChart.getXYPlot().getRangeAxis().getUpperBound() - jFreeChart.getXYPlot().getRangeAxis().getLowerBound();
        double newRange = currentRange * v;
        double diff = newRange - currentRange;
        jFreeChart.getXYPlot().getRangeAxis().setRange( jFreeChart.getXYPlot().getRangeAxis().getLowerBound() - diff / 2, jFreeChart.getXYPlot().getRangeAxis().getUpperBound() + diff / 2 );
        updateZoomEnabled();
    }

    private void updateZoomEnabled() {
        zoomControl.setHorizontalZoomOutEnabled( jFreeChart.getXYPlot().getDomainAxis().getUpperBound() != minDomainValue );
    }

    public void addSeries( String title, Color color, String abbr, SimulationVariable simulationVariable ) {
        series.add( simulationVariable );
        dynamicJFreeChartNode.addSeries( title, color );

        TitleNode titleNode = new TitleNode( title, abbr, color );
        titleNode.setOffset( titleLayer.getFullBounds().getWidth(), 0 );
        titleLayer.addChild( titleNode );

        graphTimeControlNode.addVariable( title, abbr, color, simulationVariable );
    }

    public int getSeriesIndex( SimulationVariable title ) {
        return series.indexOf( title );
    }

    public double getMaxDataX() {
        return jFreeChart.getXYPlot().getDomainAxis().getUpperBound();
    }

    public void setDomainUpperBound( double maxDataX ) {
        jFreeChart.getXYPlot().getDomainAxis().setUpperBound( maxDataX );
        updateZoomEnabled();
    }

    public void setFlowLayout() {
        setLayout( new FlowLayout() );
    }

    public void setAlignedLayout( MinimizableControlGraph[] minimizableControlGraphs ) {
        setLayout( new AlignedLayout( minimizableControlGraphs ) );
    }

    public JFreeChartNode getJFreeChartNode() {
        return dynamicJFreeChartNode;
    }

    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        super.internalUpdateBounds( x, y, width, height );
        relayout();
    }

    public interface Layout {
        void layout();
    }

    public class FlowLayout implements Layout {
        public void layout() {
            double dx = 5;
            graphTimeControlNode.setOffset( 0, 0 );
            JFreeChartSliderNode.setOffset( graphTimeControlNode.getFullBounds().getMaxX() + dx, 0 );

//            jFreeChartNode.setBounds( chartSlider.getFullBounds().getMaxX(), 0, getBounds().getWidth() - zoomControl.getFullBounds().getWidth() - chartSlider.getFullBounds().getMaxX(), getBounds().getHeight() );
            //todo: putting everything in setBounds fails, for some reason setOffset as a separate operation succeeds
            dynamicJFreeChartNode.setBounds( 0, 0, getBounds().getWidth() - zoomControl.getFullBounds().getWidth() - JFreeChartSliderNode.getFullBounds().getMaxX(), getBounds().getHeight() );
            dynamicJFreeChartNode.setOffset( JFreeChartSliderNode.getFullBounds().getMaxX(), 0 );
            dynamicJFreeChartNode.updateChartRenderingInfo();
            zoomControl.setOffset( dynamicJFreeChartNode.getFullBounds().getMaxX(), dynamicJFreeChartNode.getFullBounds().getCenterY() - zoomControl.getFullBounds().getHeight() / 2 );
            Rectangle2D d = dynamicJFreeChartNode.plotToNode( getDataArea() );
            titleLayer.setOffset( d.getX() + dynamicJFreeChartNode.getOffset().getX(), d.getY() + dynamicJFreeChartNode.getOffset().getY() );

        }
    }

    static interface LayoutFunction {
        double getValue( MinimizableControlGraph minimizableControlGraph );
    }

    public class AlignedLayout implements Layout {
        private MinimizableControlGraph[] minimizableControlGraphs;

        public AlignedLayout( MinimizableControlGraph[] minimizableControlGraphs ) {
            this.minimizableControlGraphs = minimizableControlGraphs;
        }

        public double[] getValues( LayoutFunction layoutFunction ) {
            ArrayList values = new ArrayList();
            for( int i = 0; i < minimizableControlGraphs.length; i++ ) {
                if( !minimizableControlGraphs[i].isMinimized() ) {
                    values.add( new Double( layoutFunction.getValue( minimizableControlGraphs[i] ) ) );
                }
            }
            double[] val = new double[values.size()];
            for( int i = 0; i < val.length; i++ ) {
                val[i] = ( (Double)values.get( i ) ).doubleValue();
            }
            return val;
        }

        public void layout() {
            double dx = 5;
            graphTimeControlNode.setOffset( 0, 0 );
            LayoutFunction controlNodeMaxX = new LayoutFunction() {
                public double getValue( MinimizableControlGraph minimizableControlGraph ) {
                    return minimizableControlGraph.getControlGraph().graphTimeControlNode.getFullBounds().getWidth();
                }
            };
            if( getNumberMaximized() == 0 ) {
                return;
            }
            JFreeChartSliderNode.setOffset( max( getValues( controlNodeMaxX ) ) + dx, 0 );

            //compact the jfreechart node in the x direction by distance from optimal.

            LayoutFunction chartInset = new LayoutFunction() {
                public double getValue( MinimizableControlGraph minimizableControlGraph ) {
                    return getInsetX( minimizableControlGraph.getControlGraph().getJFreeChartNode() );
                }
            };
            double maxInset = max( getValues( chartInset ) );
//            System.out.println( "maxInset = " + maxInset );
            //todo: this layout code looks like it depends on layout getting called twice for each graph
            double diff = maxInset - getInsetX( getJFreeChartNode() );
            //todo: putting everything in setBounds fails, for some reason setOffset as a separate operation succeeds
            dynamicJFreeChartNode.setBounds( 0, 0, getBounds().getWidth() - zoomControl.getFullBounds().getWidth() - JFreeChartSliderNode.getFullBounds().getMaxX() - diff, getBounds().getHeight() );
            dynamicJFreeChartNode.setOffset( JFreeChartSliderNode.getFullBounds().getMaxX() + diff, 0 );
            dynamicJFreeChartNode.updateChartRenderingInfo();
            zoomControl.setOffset( dynamicJFreeChartNode.getFullBounds().getMaxX(), dynamicJFreeChartNode.getFullBounds().getCenterY() - zoomControl.getFullBounds().getHeight() / 2 );
            Rectangle2D d = dynamicJFreeChartNode.plotToNode( getDataArea() );
            titleLayer.setOffset( d.getX() + dynamicJFreeChartNode.getOffset().getX(), d.getY() + dynamicJFreeChartNode.getOffset().getY() );
        }

        private int getNumberMaximized() {
            int count = 0;
            for( int i = 0; i < minimizableControlGraphs.length; i++ ) {
                MinimizableControlGraph minimizableControlGraph = minimizableControlGraphs[i];
                if( !minimizableControlGraph.isMinimized() ) {
                    count++;
                }
            }
            return count;
        }

        private double max( double[] values ) {
            double max = values[0];
            for( int i = 1; i < values.length; i++ ) {
                if( values[i] > max ) {
                    max = values[i];
                }
            }
            return max;
        }
    }

    private static double getInsetX( JFreeChartNode jFreeChartNode ) {
        Rectangle2D bounds = jFreeChartNode.getBounds();
        Rectangle2D dataBounds = jFreeChartNode.getDataArea();
        return dataBounds.getX() - bounds.getX();
    }

    public void setLayout( Layout layout ) {
        this.layout = layout;
        relayout();
    }

    void relayout() {
        layout.layout();
    }

    private Rectangle2D.Double getDataArea() {
        double xMin = dynamicJFreeChartNode.getChart().getXYPlot().getDomainAxis().getLowerBound();
        double xMax = dynamicJFreeChartNode.getChart().getXYPlot().getDomainAxis().getUpperBound();
        double yMin = dynamicJFreeChartNode.getChart().getXYPlot().getRangeAxis().getLowerBound();
        double yMax = dynamicJFreeChartNode.getChart().getXYPlot().getRangeAxis().getUpperBound();
        Rectangle2D.Double r = new Rectangle2D.Double();
        r.setFrameFromDiagonal( xMin, yMin, xMax, yMax );
        return r;
    }

    public void clear() {
        dynamicJFreeChartNode.clear();
    }

    public void addValue( double time, double value ) {
        addValue( 0, time, value );
    }

    public void addValue( int series, double time, double value ) {
        dynamicJFreeChartNode.addValue( series, time, value );
        System.out.println( "series = " + series + " time=" + time + ", value=" + value );
    }

    public void setEditable( boolean editable ) {
        JFreeChartSliderNode.setVisible( editable );
        JFreeChartSliderNode.setPickable( editable );
        JFreeChartSliderNode.setChildrenPickable( editable );

        graphTimeControlNode.setEditable( editable );
    }

    public static interface Listener {
        void mousePressed();

        void valueChanged();

        //This method is called when the user makes an input event that indicates
        //that this component should be "in control" of the simulation
        void controlFocusGrabbed();
    }

    public static class Adapter implements Listener {

        public void mousePressed() {
        }

        public void valueChanged() {
        }

        public void controlFocusGrabbed() {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.mousePressed();
        }
    }
}

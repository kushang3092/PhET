package edu.colorado.phet.jfreechart.piccolo;

import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * This component contains both a JFreeChartNode (supplied by the caller) and a vertical Slider node, which spans the data range of the JFreeChart.
 * This component can be used to display and control an xy dataset.
 *
 * @author Sam Reid
 */
public class VerticalChartControl extends PNode {
    private PhetPPath trackPPath;
    private PNode sliderThumb;
    private JFreeChartNode jFreeChartNode;
    private double value = 0.0;
    private ArrayList listeners = new ArrayList();

    /**
     * Constructs a VerticalChartControl to use the data area of the specified JFreeChartNode and the specified graphic for the thumb control.
     *
     * @param jFreeChartNode the chart to control.
     * @param sliderThumb    the PNode that displays the thumb of the slider.
     */
    public VerticalChartControl( final JFreeChartNode jFreeChartNode, final PNode sliderThumb ) {
        this.jFreeChartNode = jFreeChartNode;
        this.sliderThumb = sliderThumb;
        jFreeChartNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLayout();
            }
        } );
        jFreeChartNode.getChart().addChangeListener( new ChartChangeListener() {
            public void chartChanged( ChartChangeEvent chartChangeEvent ) {
                updateLayout();
            }
        } );

        trackPPath = new PhetPPath( new BasicStroke( 1 ), Color.black );
        addChild( trackPPath );
        addChild( sliderThumb );
        sliderThumb.addInputEventListener( new PBasicInputEventHandler() {
            Point2D initDragPoint = null;
            double origY;

            public void mousePressed( PInputEvent event ) {
                initDragPoint = event.getPositionRelativeTo( sliderThumb.getParent() );
                origY = value;
            }

            public void mouseReleased( PInputEvent event ) {
                initDragPoint = null;
            }

            public void mouseDragged( PInputEvent event ) {
                if( initDragPoint == null ) {
                    mousePressed( event );
                }
                double yCurrent = event.getPositionRelativeTo( sliderThumb.getParent() ).getY();
                double nodeDY = yCurrent - initDragPoint.getY();
                Point2D plot1 = jFreeChartNode.nodeToPlot( new Point2D.Double( 0, 0 ) );
                Point2D plot2 = jFreeChartNode.nodeToPlot( new Point2D.Double( 0, nodeDY ) );
                double plotDY = plot2.getY() - plot1.getY();
                setValue( clamp( origY + plotDY ) );
            }
        } );

        addChild( jFreeChartNode );
        jFreeChartNode.setOffset( 50, 0 );
        jFreeChartNode.updateChartRenderingInfo();
        updateLayout();
    }

    private double clamp( double v ) {
        if( v > getMaxRangeValue() ) {
            v = getMaxRangeValue();
        }
        if( v < getMinRangeValue() ) {
            v = getMinRangeValue();
        }
        return v;
    }

    /**
     * Sets the value of the slider for this chart.
     *
     * @param value the value to set for this controller.
     */
    public void setValue( double value ) {
        if( this.value != value ) {
            this.value = value;
            updateThumbLocation();
            notifyValueChanged();
        }
    }

    /**
     * Gets the value of the control slider.
     *
     * @return the value of the control slider.
     */
    public double getValue() {
        return value;
    }

    private double getMaxRangeValue() {
        return jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getUpperBound();
    }

    private double getMinRangeValue() {
        return jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getLowerBound();
    }

    private void updateLayout() {
        Rectangle2D dataArea = jFreeChartNode.getDataArea();
        trackPPath.setPathTo( new Rectangle2D.Double( 0, dataArea.getY(), 5, dataArea.getHeight() ) );
        updateThumbLocation();
    }

    private void updateThumbLocation() {
        Point2D nodeLocation = jFreeChartNode.plotToNode( new Point2D.Double( 0, value ) );
        sliderThumb.setOffset( trackPPath.getFullBounds().getCenterX() - sliderThumb.getFullBounds().getWidth() / 2.0,
                               nodeLocation.getY() - sliderThumb.getFullBounds().getHeight() / 2.0 );
    }

    /**
     * Clients can listen for value change events, whether generated by the slider or another call.
     */
    public static interface Listener {
        void valueChanged();
    }

    /**
     * Adds a listener for value change events.
     *
     * @param listener the value change listener.
     */
    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void notifyValueChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.valueChanged();
        }
    }
}

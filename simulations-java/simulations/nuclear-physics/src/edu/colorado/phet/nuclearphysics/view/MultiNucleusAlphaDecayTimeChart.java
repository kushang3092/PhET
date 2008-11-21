/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayAdapter;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayControl;
import edu.colorado.phet.nuclearphysics.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusAlphaDecayCanvas;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusAlphaDecayModel;
import edu.colorado.phet.nuclearphysics.util.PhetButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


/**
 * This class displays a "strip chart" of the decay time for multiple nuclei,
 * and also allows the user to adjust the half life when it is possible to do
 * so.
 *
 * @author John Blanco
 */
public class MultiNucleusAlphaDecayTimeChart extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Total amount of time in milliseconds represented by this chart.
    private static final double TIME_SPAN = 3200;
    
    // Minimum allowable half life.
    private static final double MIN_HALF_LIFE = 10; // In milliseconds.

    // Constants for controlling the appearance of the chart.
    private static final Color  BORDER_COLOR = Color.DARK_GRAY;
    private static final float  BORDER_STROKE_WIDTH = 6f;
    private static final Stroke BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final float  AXES_LINE_WIDTH = 0.5f;
    private static final Stroke AXES_STROKE = new BasicStroke( AXES_LINE_WIDTH );
    private static final Color  AXES_LINE_COLOR = Color.BLACK;
    private static final double TICK_MARK_LENGTH = 3;
    private static final float  TICK_MARK_WIDTH = 2;
    private static final Stroke TICK_MARK_STROKE = new BasicStroke( TICK_MARK_WIDTH );
    private static final Font   TICK_MARK_LABEL_FONT = new PhetFont( Font.PLAIN, 12 );
    private static final Color  TICK_MARK_COLOR = AXES_LINE_COLOR;
    private static final Font   SMALL_LABEL_FONT = new PhetFont( Font.PLAIN, 14 );
    private static final Font   LARGE_LABEL_FONT = new PhetFont( Font.BOLD, 20 );
    private static final float  HALF_LIFE_LINE_STROKE_WIDTH = 2.0f;
    private static final Stroke HALF_LIFE_LINE_STROKE = new BasicStroke( HALF_LIFE_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3.0f, 3.0f }, 0 );
    private static final Color  HALF_LIFE_LINE_COLOR = new Color (0x990000);
    private static final Color  HALF_LIFE_TEXT_COLOR = HALF_LIFE_LINE_COLOR;
    private static final Font   HALF_LIFE_FONT = new PhetFont( Font.BOLD, 16 );

    // Constants that control the location of the origin.
    private static final double X_ORIGIN_PROPORTION = 0.20;
    private static final double Y_ORIGIN_PROPORTION = 0.50;

    // Tweakable values that can be used to adjust where the nuclei appear on
    // the chart.
    private static final double PRE_DECAY_TIME_LINE_POS_FRACTION = 0.15;
    private static final double POST_DECAY_TIME_LINE_POS_FRACTION = 0.40;
    private static final double TIME_ZERO_OFFSET = 100; // In milliseconds
    private static final int FALL_COUNT = 5; // Number of clock ticks for nucleus to fall from upper to lower line.

    // Constants that control the way the nuclei look.
    private static final double NUCLEUS_SIZE_PROPORTION = 0.1;  // Fraction of the overall height of the chart.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Reference to the model containing the nuclei that are being plotted.
    MultiNucleusAlphaDecayModel _model;
    
    // Reference to the canvas on which this chart resides.  Needed for
    // certain interactions.
    MultiNucleusAlphaDecayCanvas _canvas;
    
    // Maps and lists for keeping track of nuclei and corresponding nodes.
    private HashMap _mapNucleiToNodes = new HashMap();
    private ArrayList _inactiveNuclei = new ArrayList();
    private ArrayList _preDecayNuclei = new ArrayList();
    private ArrayList _postDecayNuclei = new ArrayList();
    private HashMap _mapNucleiToFallCount = new HashMap();

    // References to the various components of the chart.
    private PPath _borderNode;
    private PPath _halfLifeMarkerLine;
    private ResizeArrowNode _halfLifeHandleNode;
    private PText _halfLifeLabel;
    private ArrowNode _xAxisOfGraph;
    private ArrayList _xAxisTickMarks;
    private ArrayList _xAxisTickMarkLabels;
    private ArrayList _yAxisTickMarks;
    private ArrayList _yAxisTickMarkLabels;
    private PText _xAxisLabel;
    private PText _yAxisLabel1;
    private PText _yAxisLabel2;
    private ShadowPText _numUndecayedNucleiLabel;
    private PText _numUndecayedNucleiText;
    private ShadowPText _numDecayedNucleiLabel;
    private PText _numDecayedNucleiText;
    private PNode _arrowNode;
    private PText _dummyText;

    // Parent node that will be non-pickable and will contain all of the
    // non-interactive portions of the chart.
    private PComposite _nonPickableChartNode;
    
    // Parent node that will have interactive portions of graph.
    private PNode _pickableChartNode;

    // Variables used for positioning nodes within the graph.
    double _usableAreaOriginX;
    double _usableAreaOriginY;
    double _usableWidth;
    double _usableHeight;
    double _graphOriginX;
    double _graphOriginY;
    double _nucleusNodeRadius;

    // Factor for converting milliseconds to pixels.
    double _msToPixelsFactor = 1; // Arbitrary init val, updated later.

    // Clock that we listen to for moving the nuclei and performing resets.
    // TODO: JPB TBD - Do we really listen to this for resets?  Update comment if not.
    ConstantDtClock _clock;

    // Flag for tracking if chart is cleared.
    boolean _chartCleared = false;

    // Button for resetting this chart.
    PhetButtonNode _resetButtonNode;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public MultiNucleusAlphaDecayTimeChart( MultiNucleusAlphaDecayModel model, MultiNucleusAlphaDecayCanvas canvas ) {

        _clock = model.getClock();
        _model = model;
        _canvas = canvas;

        // Register as a clock listener.
        _clock.addClockListener( new ClockAdapter() {

            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTick( clockEvent );
            }

            public void simulationTimeReset( ClockEvent clockEvent ) {
                _chartCleared = false;
            }
        } );
        
        // Listen to the model for notifications of relevant events.
        _model.addListener( new AlphaDecayAdapter(){
            public void modelElementAdded(Object modelElement){
            	handleModelElementAdded(modelElement);
            };

            public void modelElementRemoved(Object modelElement){
            	handleModelElementRemoved(modelElement);
            };
            
            public void nucleusTypeChanged(){
            	update();
            };
            
            public void halfLifeChanged(){
            	positionHalfLifeMarker();
            }
        });

        // Set up the parent node that will contain the non-interactive
        // portions of the chart.
        _nonPickableChartNode = new PComposite();
        _nonPickableChartNode.setPickable( false );
        _nonPickableChartNode.setChildrenPickable( false );
        addChild( _nonPickableChartNode );

        // Set up the parent node that will contain the interactive portions
        // of the chart.
        _pickableChartNode = new PNode();
        _pickableChartNode.setPickable( true );
        _pickableChartNode.setChildrenPickable( true );
        addChild( _pickableChartNode );

        // Create the border for this chart.
        _borderNode = new PPath();
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( NuclearPhysicsConstants.ALPHA_DECAY_CHART_COLOR );
        _nonPickableChartNode.addChild( _borderNode );

        // Create the x axis of the graph.  The initial position is arbitrary
        // and the actual positioning will be done by the update function(s).
        _xAxisOfGraph = new ArrowNode( new Point2D.Double( 10, 10 ), new Point2D.Double( 20, 20 ), 9, 7, 1 );
        _xAxisOfGraph.setStroke( AXES_STROKE );
        _xAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
        _xAxisOfGraph.setPaint( AXES_LINE_COLOR );
        _nonPickableChartNode.addChild( _xAxisOfGraph );

        // Add the tick marks and their labels to the X axis.
        int numTicksOnX = (int) Math.round( ( TIME_SPAN / 1000 ) + 1 );
        _xAxisTickMarks = new ArrayList( numTicksOnX );
        _xAxisTickMarkLabels = new ArrayList( numTicksOnX );
        DecimalFormat formatter = new DecimalFormat( "0.0" );
        for ( int i = 0; i < numTicksOnX; i++ ) {
            // Create the tick mark.  It will be positioned later.
            PPath tickMark = new PPath();
            tickMark.setStroke( TICK_MARK_STROKE );
            tickMark.setStrokePaint( TICK_MARK_COLOR );
            _xAxisTickMarks.add( tickMark );
            _nonPickableChartNode.addChild( tickMark );

            // Create the label for the tick mark.
            PText tickMarkLabel = new PText( formatter.format( i ) );
            tickMarkLabel.setFont( TICK_MARK_LABEL_FONT );
            _xAxisTickMarkLabels.add( tickMarkLabel );
            _nonPickableChartNode.addChild( tickMarkLabel );
        }

        // Add the tick marks and their labels to the Y axis.  There are only
        // two, one for the weight of Polonium and one for the weight of Lead.

        _yAxisTickMarks = new ArrayList( 2 );

        PPath yTickMark1 = new PPath();
        yTickMark1.setStroke( TICK_MARK_STROKE );
        yTickMark1.setStrokePaint( TICK_MARK_COLOR );
        _yAxisTickMarks.add( yTickMark1 );
        _nonPickableChartNode.addChild( yTickMark1 );

        PPath yTickMark2 = new PPath();
        yTickMark2.setStroke( TICK_MARK_STROKE );
        yTickMark2.setStrokePaint( TICK_MARK_COLOR );
        _yAxisTickMarks.add( yTickMark2 );
        _nonPickableChartNode.addChild( yTickMark2 );

        _yAxisTickMarkLabels = new ArrayList( 2 );

        PText yTickMarkLabel1 = new PText( NuclearPhysicsStrings.LEAD_207_ISOTOPE_NUMBER );
        yTickMarkLabel1.setFont( TICK_MARK_LABEL_FONT );
        _yAxisTickMarkLabels.add( yTickMarkLabel1 );
        _nonPickableChartNode.addChild( yTickMarkLabel1 );

        PText yTickMarkLabel2 = new PText( NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER );
        yTickMarkLabel2.setFont( TICK_MARK_LABEL_FONT );
        _yAxisTickMarkLabels.add( yTickMarkLabel2 );
        _nonPickableChartNode.addChild( yTickMarkLabel2 );

        // Add the text for the X & Y axes.
        _xAxisLabel = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_X_AXIS_LABEL + " (" + NuclearPhysicsStrings.DECAY_TIME_UNITS + ")" );
        _xAxisLabel.setFont( SMALL_LABEL_FONT );
        _nonPickableChartNode.addChild( _xAxisLabel );
        _yAxisLabel1 = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_Y_AXIS_LABEL1 );
        _yAxisLabel1.setFont( SMALL_LABEL_FONT );
        _yAxisLabel1.rotate( 1.5 * Math.PI );
        _nonPickableChartNode.addChild( _yAxisLabel1 );
        _yAxisLabel2 = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_Y_AXIS_LABEL2 );
        _yAxisLabel2.setFont( SMALL_LABEL_FONT );
        _yAxisLabel2.rotate( 1.5 * Math.PI );
        _nonPickableChartNode.addChild( _yAxisLabel2 );
        
        // Add the text for labeling the pre- and post-decay quantities of the
        // nuclei.
        _numUndecayedNucleiLabel = new ShadowPText();
        _numUndecayedNucleiLabel.setFont(LARGE_LABEL_FONT);
        _numUndecayedNucleiLabel.setTextPaint(Color.YELLOW);
        _nonPickableChartNode.addChild(_numUndecayedNucleiLabel);
        _numUndecayedNucleiText = new PText("0");
        _numUndecayedNucleiText.setFont(LARGE_LABEL_FONT);
        _nonPickableChartNode.addChild(_numUndecayedNucleiText);
        _numDecayedNucleiLabel = new ShadowPText();
        _numDecayedNucleiLabel.setFont(LARGE_LABEL_FONT);
        _nonPickableChartNode.addChild(_numDecayedNucleiLabel);
        _numDecayedNucleiText = new PText("0");
        _numDecayedNucleiText.setFont(LARGE_LABEL_FONT);
        _nonPickableChartNode.addChild(_numDecayedNucleiText);
        
        // Create a dummy text value for consistent positioning of the real
        // numerical values.
        _dummyText = new PText("000");
        _dummyText.setFont(LARGE_LABEL_FONT);

        // Add the little arrow that signifies decay from one nucleus type to another.
        _arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(0, 8), 5, 10, 3 );
        _arrowNode.setPaint( Color.BLACK );
        _nonPickableChartNode.addChild(_arrowNode);

        // Create the line that will illustrate where the half life is.
        _halfLifeMarkerLine = new PPath();
        _halfLifeMarkerLine.setStroke( HALF_LIFE_LINE_STROKE );
        _halfLifeMarkerLine.setStrokePaint( HALF_LIFE_LINE_COLOR );
        _halfLifeMarkerLine.setPaint( NuclearPhysicsConstants.ALPHA_DECAY_CHART_COLOR );
        _nonPickableChartNode.addChild( _halfLifeMarkerLine );
        
        // Create the handle that will allow the user to control the half life.
        _halfLifeHandleNode = new ResizeArrowNode(25, 0);
        _pickableChartNode.addChild( _halfLifeHandleNode );
        _halfLifeHandleNode.addInputEventListener(new PBasicInputEventHandler(){
        	boolean halfLifeChanged;
        	public void mousePressed(PInputEvent event) {
        		halfLifeChanged = false;
        		_model.setPaused(true);
        	}
        	public void mouseReleased(PInputEvent event) {
        		_model.setPaused(false);
        		if (halfLifeChanged){
        			_canvas.autoPressResetNucleiButton();
        		}
        	}
            public void mouseDragged(PInputEvent event) {
                PNode draggedNode = event.getPickedNode();
                PDimension d = event.getDeltaRelativeTo(draggedNode);
                draggedNode.localToParent(d);
                double newHalfLife = _model.getHalfLife() + (d.width / _msToPixelsFactor) / 1000;
                if (newHalfLife >= (MIN_HALF_LIFE / 1000) && newHalfLife <= ((TIME_SPAN * 0.95) / 1000)){
	                _model.setHalfLife(newHalfLife);
	        		halfLifeChanged = true;
                }
            }
        });


        // Create the label for the half life line.
        _halfLifeLabel = new PText( NuclearPhysicsStrings.DECAY_TIME_CHART_HALF_LIFE);
        _halfLifeLabel.setFont( HALF_LIFE_FONT );
        _halfLifeLabel.setTextPaint( HALF_LIFE_TEXT_COLOR );
        _nonPickableChartNode.addChild( _halfLifeLabel );
        
        // Add the button for resetting the chart.
        _resetButtonNode = new PhetButtonNode( NuclearPhysicsStrings.DECAY_TIME_CLEAR_CHART );
        _resetButtonNode.setPickable( true );
        _pickableChartNode.addChild( _resetButtonNode );

        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                handleResetChartButtonPressed();
            }
        } );
        
        updateNucleusGraphLabels();
    }

	//------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    /**
     * This method is called to re-scale the chart, which generally occurs
     * when the overall size of the simulation is changed.
     * 
     * @param 
     */
    private void updateBounds( Rectangle2D rect ) {

        // Recalculate the usable area and origin for the chart.
        _usableAreaOriginX = rect.getX() + BORDER_STROKE_WIDTH;
        _usableAreaOriginY = rect.getY() + BORDER_STROKE_WIDTH;
        _usableWidth = rect.getWidth() - ( BORDER_STROKE_WIDTH * 2 );
        _usableHeight = rect.getHeight() - ( BORDER_STROKE_WIDTH * 2 );

        // Decide where the origin is located.
        _graphOriginX = _usableAreaOriginX + ( X_ORIGIN_PROPORTION * _usableWidth );
        _graphOriginY = _usableAreaOriginY + ( Y_ORIGIN_PROPORTION * _usableHeight );

        // Update the multiplier used for converting from pixels to
        // milliseconds.  Use the multiplier to tweak the span of the x axis.
        _msToPixelsFactor = 0.75 * _usableWidth / TIME_SPAN;
        
        // Update the radius value used to position nucleus nodes so that they
        // are centered at the desired location.
        _nucleusNodeRadius = _usableHeight * NUCLEUS_SIZE_PROPORTION / 2;

        // Redraw the chart based on these recalculated values.
        update();
    }

    /**
     * Redraw the chart based on the current state.
     */
    private void update() {
    	
        // Set up the border for the chart.
        _borderNode.setPathTo( new RoundRectangle2D.Double( _usableAreaOriginX, _usableAreaOriginY, _usableWidth, _usableHeight, 20, 20 ) );

        // Position the x and y axes.
        _xAxisOfGraph.setTipAndTailLocations( 
        		new Point2D.Double( _graphOriginX + ( TIME_SPAN * _msToPixelsFactor ) + 10, _graphOriginY ), 
        		new Point2D.Double( _graphOriginX, _graphOriginY ) );

        // Position the tick marks and their labels on the X axis.
        for ( int i = 0; i < _xAxisTickMarks.size(); i++ ) {

            // Position the tick mark itself.
            PPath tickMark = (PPath) _xAxisTickMarks.get( i );
            double tickMarkPosX = _graphOriginX + (TIME_ZERO_OFFSET * _msToPixelsFactor) 
                    + ( i * 1000 * _msToPixelsFactor );
            tickMark.setPathTo( new Line2D.Double( tickMarkPosX, _graphOriginY, tickMarkPosX, _graphOriginY - TICK_MARK_LENGTH ) );

            // Position the label for the tick mark.
            PText tickMarkLabel = (PText) _xAxisTickMarkLabels.get( i );
            double tickMarkLabelPosX = tickMarkPosX - ( tickMarkLabel.getWidth() / 2 );
            tickMarkLabel.setOffset( tickMarkLabelPosX, _graphOriginY );
        }

        // Position the tick marks and their labels on the Y axis.
        double preDecayPosY = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION );
        double postDecayPosY = _usableAreaOriginY + ( _usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION );
        PPath yAxisLowerTickMark = (PPath) _yAxisTickMarks.get( 0 );
        yAxisLowerTickMark.setPathTo( new Line2D.Double( _graphOriginX - TICK_MARK_LENGTH, postDecayPosY, 
        		_graphOriginX, postDecayPosY ));

        PPath yAxisUpperTickMark = (PPath) _yAxisTickMarks.get( 1 );
        yAxisUpperTickMark.setPathTo( new Line2D.Double( _graphOriginX - TICK_MARK_LENGTH, preDecayPosY, 
        		_graphOriginX, preDecayPosY ) );

        PText yAxisLowerTickMarkLabel = (PText) _yAxisTickMarkLabels.get( 0 );
        yAxisLowerTickMarkLabel.setOffset( _graphOriginX - ( 1.15 * yAxisLowerTickMarkLabel.getWidth() ), yAxisLowerTickMark.getY() - ( 0.5 * yAxisLowerTickMarkLabel.getHeight() ) );

        PText yAxisUpperTickMarkLabel = (PText) _yAxisTickMarkLabels.get( 1 );
        yAxisUpperTickMarkLabel.setOffset( _graphOriginX - ( 1.15 * yAxisUpperTickMarkLabel.getWidth() ), yAxisUpperTickMark.getY() - ( 0.5 * yAxisUpperTickMarkLabel.getHeight() ) );

        // Position the half life marker.
        positionHalfLifeMarker();

        // Position the labels for the axes.
        _xAxisLabel.setOffset( _usableAreaOriginX + _usableWidth - (_xAxisLabel.getWidth() * 1.2), 
        		((PNode)_xAxisTickMarkLabels.get(0)).getFullBoundsReference().getMaxY() );
        double yAxisLabelCenter = yAxisUpperTickMark.getY() 
                + ((yAxisLowerTickMark.getY() - yAxisUpperTickMark.getY()) / 2);
        _yAxisLabel2.setOffset( yAxisUpperTickMarkLabel.getOffset().getX() - ( 2.0 * _yAxisLabel1.getFont().getSize() ),
        		yAxisLabelCenter + (_yAxisLabel2.getFullBounds().height / 2) );
        _yAxisLabel1.setOffset( _yAxisLabel2.getOffset().getX() - ( 1.1 * _yAxisLabel2.getFont().getSize() ),
        		yAxisLabelCenter + (_yAxisLabel1.getFullBounds().height / 2) );
        
        // Position the labels for the quantities of the various nuclei.
        _numUndecayedNucleiLabel.setOffset( 
        		_yAxisLabel1.getFullBoundsReference().x - _dummyText.getFullBoundsReference().width * 1.1 - _numUndecayedNucleiLabel.getFullBoundsReference().width,
        		preDecayPosY - (_dummyText.getFullBoundsReference().height * 0.5));
        _numDecayedNucleiLabel.setOffset(
        		_yAxisLabel1.getFullBoundsReference().x - _dummyText.getFullBoundsReference().width * 1.1 - _numDecayedNucleiLabel.getFullBoundsReference().width,
        		postDecayPosY - (_dummyText.getFullBoundsReference().height * 0.5));

        updateNucleiNumberText();
        
        // Position the label for the half life.
        _halfLifeLabel.setOffset( _halfLifeMarkerLine.getX() - (_halfLifeLabel.getFullBoundsReference().width / 2),
        		((PNode)_xAxisTickMarkLabels.get(0)).getFullBoundsReference().getMaxY() );

        // Position the reset button.
        _resetButtonNode.setOffset( _usableAreaOriginX + 10, 
        		_usableAreaOriginY + _usableHeight - _resetButtonNode.getFullBoundsReference().height - 5);
        
        // Rescale the nucleus nodes and set their positions.
        Set entries = _mapNucleiToNodes.entrySet();
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            AlphaDecayControl nucleus = (AlphaDecayControl)entry.getKey();
            PNode nucleusNode = (PNode)_mapNucleiToNodes.get(nucleus);
            if (nucleusNode != null){
            	nucleusNode.setScale(1);
            	nucleusNode.setScale((_nucleusNodeRadius * 2) / nucleusNode.getFullBoundsReference().height);
                positionNucleusOnChart(nucleus);
            }
        }
    }

    /**
     * This method causes the chart to resize itself based on the (presumably
     * different) size of the overall canvas on which it appears.
     * 
     * @param rect - Position on the canvas where this chart should appear.
     */
    public void componentResized( Rectangle2D rect ) {
        updateBounds( rect );
    }

    /**
     * Update the chart by moving the active nuclei or any other time-
     * dependent visual representation.
     * 
     * @param clockEvent
     */
    private void handleClockTick( ClockEvent clockEvent ) {
    	
    	// See if any of the inactive nuclei have become active.
        for (Iterator it = _inactiveNuclei.iterator (); it.hasNext (); ) {
            AlphaDecayControl nucleus = (AlphaDecayControl)it.next();
            if (nucleus.isDecayActive()){
            	// This nucleus is active - transfer it to the appropriate list.
            	_preDecayNuclei.add(nucleus);
            	createNodeForNucleus((AtomicNucleus)nucleus);
            	it.remove();
            	updateNucleiNumberText();
            }
        }
        
        // Update the position of any active nuclei and check if any have
        // decayed.
        for (Iterator it = _preDecayNuclei.iterator(); it.hasNext (); ) {
            AlphaDecayControl nucleus = (AlphaDecayControl)it.next();
            if (nucleus.isDecayActive()){
            	// This nucleus is currently active, so position it on the chart.
            	positionNucleusOnChart(nucleus);
            }
            else{
            	// The nucleus must have decayed.  Replace its node with a new
            	// representation, position the new node, then remove the
            	// nucleus from the list of active nuclei.
            	removeNodeForNucleus((AtomicNucleus)nucleus);
            	createNodeForNucleus((AtomicNucleus)nucleus);
            	it.remove();
            	_postDecayNuclei.add(nucleus);
            	_mapNucleiToFallCount.put(nucleus, new MyIntegerHolder(FALL_COUNT));
            	positionNucleusOnChart(nucleus);
            	updateNucleiNumberText();
            }
        }
        
        // Check the decayed nuclei to see if anything needs to be done with them.
        for (Iterator it = _postDecayNuclei.iterator(); it.hasNext (); ) {
            AlphaDecayControl nucleus = (AlphaDecayControl)it.next();
            if (nucleus.isDecayActive()){
            	// This nucleus has been reset, so move it back to the active
            	// list.
            	removeNodeForNucleus((AtomicNucleus)nucleus);
            	createNodeForNucleus((AtomicNucleus)nucleus);
            	positionNucleusOnChart(nucleus);
            	it.remove();
            	_preDecayNuclei.add(nucleus);
            	updateNucleiNumberText();
            }
            else if (_mapNucleiToFallCount.containsKey(nucleus)){
            	// This nucleus is in the process of visually falling to the
           	    // the lower line.
            	MyIntegerHolder fallCounter = (MyIntegerHolder)_mapNucleiToFallCount.get(nucleus);
            	fallCounter.decrement();
            	if (fallCounter.getValue() == 0){
            		// The node representing this nucleus is done falling, so
            		// remove it from the map.
            		_mapNucleiToFallCount.remove(nucleus);
            	}
            	else{
            		positionNucleusOnChart(nucleus);
            	}
            }
        }
    }

    /**
     * Create a labeled node for the specified nucleus and put it on the chart
     * at the appropriate location.
     */
    private void createNodeForNucleus(AtomicNucleus nucleus) {
    	
    	LabeledNucleusNode nucleusNode;

    	switch (nucleus.getNumProtons()){
    	case 84:
    		// Create a labeled nucleus representing Polonium.
    		nucleusNode = new LabeledNucleusNode("Polonium Nucleus Small.png",
                    NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER, 
                    NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL, 
                    NuclearPhysicsConstants.POLONIUM_LABEL_COLOR );
    		break;
    		
    	case 83:
    		// This nucleus is bismuth, which we use as the pre-decay custom
    		// nucleus.
    		nucleusNode = new LabeledNucleusNode("Polonium Nucleus Small.png", 
    				"", // No isotope number.
                    NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL, 
                    NuclearPhysicsConstants.CUSTOM_NUCLEUS_LABEL_COLOR );
    		break;
    		
    	case 82:
    		// Create a labeled nucleus representing Lead.
    		nucleusNode = new LabeledNucleusNode("Lead Nucleus Small.png",
                    NuclearPhysicsStrings.LEAD_207_ISOTOPE_NUMBER, 
                    NuclearPhysicsStrings.LEAD_207_CHEMICAL_SYMBOL, 
                    NuclearPhysicsConstants.LEAD_LABEL_COLOR );
    		break;
    		
    	case 81:
    		// This is thallium, which we use as the post-decay custom nucleus.
    		nucleusNode = new LabeledNucleusNode("Lead Nucleus Small.png",
    				"", // No isotope number.
                    NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL, 
                    NuclearPhysicsConstants.DECAYED_CUSTOM_NUCLEUS_LABEL_COLOR );
    		break;
    		
    	default:
    		assert false;  // This is not a nucleus type that we know how to handle.
    		throw new InvalidParameterException("Unrecognized nucleus type.");
    	}
    	
    	// Add the new node to the map.
    	_mapNucleiToNodes.put(nucleus, nucleusNode);
    	
    	// Add the node to the chart.
    	nucleusNode.setScale((_nucleusNodeRadius * 2) / nucleusNode.getFullBoundsReference().height);
    	_nonPickableChartNode.addChild(nucleusNode);
    	
    	// Position the nucleus on the chart.
    	if (nucleus instanceof AlphaDecayControl){
        	positionNucleusOnChart((AlphaDecayControl)nucleus);
    	}
    	else{
    		System.err.println("Error: Nucleus doesn't implement needed interface.");
    		assert false;  // Shouldn't happen, debug it if it does.
    	}
	}
    
    private void updateNucleusGraphLabels(){
    	if (_model.getNucleusType() == MultiNucleusAlphaDecayModel.NUCLEUS_TYPE_POLONIUM){
    		_numUndecayedNucleiLabel.setText("#" + NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL);
    		_numUndecayedNucleiLabel.setTextPaint(NuclearPhysicsConstants.POLONIUM_LABEL_COLOR);
    		_numUndecayedNucleiLabel.setShadowColor(Color.BLACK);
    		_numDecayedNucleiLabel.setText("#" + NuclearPhysicsStrings.LEAD_207_CHEMICAL_SYMBOL);
    		_numDecayedNucleiLabel.setTextPaint(NuclearPhysicsConstants.LEAD_LABEL_COLOR);
    		_numDecayedNucleiLabel.setShadowColor(Color.WHITE);
    	}
    	else {
    		_numUndecayedNucleiLabel.setText("#" + NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL);
    		_numUndecayedNucleiLabel.setTextPaint(NuclearPhysicsConstants.CUSTOM_NUCLEUS_LABEL_COLOR);
    		_numUndecayedNucleiLabel.setShadowColor(Color.BLACK);
    		_numDecayedNucleiLabel.setText("#" + NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL);
    		_numDecayedNucleiLabel.setTextPaint(NuclearPhysicsConstants.DECAYED_CUSTOM_NUCLEUS_LABEL_COLOR);
    		_numDecayedNucleiLabel.setShadowColor(Color.WHITE);
    	}
    }
    
    /**
     * Update the labels that indicate the number of undecayed and decayed nuclei.
     */
    private void updateNucleiNumberText(){
    	
    	// Update the values.
    	_numUndecayedNucleiText.setText(Integer.toString(_preDecayNuclei.size()));
    	_numDecayedNucleiText.setText(Integer.toString(_postDecayNuclei.size()));
    	
    	// Update the positions so that they remain centered in their area.
		double preDecayPosY = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION );
        double postDecayPosY = _usableAreaOriginY + ( _usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION );
        double labelHeight = _dummyText.getFullBoundsReference().height;
        double numberTextWidth = _dummyText.getFullBoundsReference().width;
        double labelMiddleX = _yAxisLabel1.getFullBoundsReference().x - (numberTextWidth * 0.6);
        PBounds undecayedTextBounds = _numUndecayedNucleiText.getFullBoundsReference();
        PBounds decayedTextBounds = _numDecayedNucleiText.getFullBoundsReference();
        
        _numUndecayedNucleiText.setOffset(labelMiddleX - undecayedTextBounds.width / 2, 
        		preDecayPosY - (labelHeight * 0.5));
        _numDecayedNucleiText.setOffset(labelMiddleX - decayedTextBounds.width / 2,
        		postDecayPosY - (labelHeight * 0.5));

        _arrowNode.setOffset( 
        		_numUndecayedNucleiText.getFullBoundsReference().x + _numUndecayedNucleiText.getFullBoundsReference().width / 2,
        		decayedTextBounds.y - ((decayedTextBounds.y - undecayedTextBounds.getMaxY()) / 2));
        _arrowNode.setOffset( 
        		_numUndecayedNucleiText.getFullBoundsReference().x + _numUndecayedNucleiText.getFullBoundsReference().width / 2,
        		undecayedTextBounds.getMaxY());
    }
    
    /**
     * Remove the node associated with the given nucleus from the chart and
     * the internal data structures.
     * 
     * @param nucleus
     */
    private void removeNodeForNucleus(AtomicNucleus nucleus){
    	
    	PNode nucleusNode = (PNode)_mapNucleiToNodes.get(nucleus);
    	
    	if (nucleusNode == null){
    		// Not sure if this case is valid or not.  For now, print a warning.
    		System.err.println("Warning: Attempt to remove non-existent node.");
    		return;
    	}
    	
    	_nonPickableChartNode.removeChild(nucleusNode);
    	_mapNucleiToNodes.put(nucleus, null);
    }

	private void handleModelElementAdded(Object modelElement) {
    	
    	if (modelElement instanceof AtomicNucleus){
    		// At least for now, it is expected that all nuclei added to this
    		// chart are alpha decayers that are not moving towards decay yet.
    		assert (modelElement instanceof AlphaDecayControl);
    		assert (((AlphaDecayControl)modelElement).isDecayActive() == false);
    		
    		// Add the nuclei to the appropriate internal data structures.
    		// No node is added until this nucleus becomes active.
    		_mapNucleiToNodes.put(modelElement, null);
    		_inactiveNuclei.add(modelElement);
    	}
	}

    private void handleModelElementRemoved(Object modelElement) {
    	
    	if (modelElement instanceof AtomicNucleus){
    		LabeledNucleusNode nucleusNode = (LabeledNucleusNode)_mapNucleiToNodes.get(modelElement);
    		if (nucleusNode != null){
    			removeNodeForNucleus((AtomicNucleus) modelElement);
    			_preDecayNuclei.remove(modelElement);
    			_postDecayNuclei.remove(modelElement);
    		}
    		if (_mapNucleiToNodes.containsKey(modelElement)){
    			_mapNucleiToNodes.remove(modelElement);
    		}
    		else{
    			System.err.println("Error: Unable to locate nucleus in map.");
    		}
    	}
	}
    
    private void positionHalfLifeMarker(){
        // Position the marker for the half life.
    	double halfLife = _model.getHalfLife() * 1000;  // Get half life and convert to ms.
        _halfLifeMarkerLine.reset();
        _halfLifeMarkerLine.moveTo( (float) ( _graphOriginX + (TIME_ZERO_OFFSET + halfLife) * _msToPixelsFactor ),
        		(float) _graphOriginY );
        _halfLifeMarkerLine.lineTo( (float) ( _graphOriginX + (TIME_ZERO_OFFSET + halfLife) * _msToPixelsFactor ),
        		(float) ( _usableAreaOriginY + ( 0.1 * _usableHeight ) ) );
        
        // If it is a custom nucleus, position and show the handle.
        if (_model.getNucleusType() == MultiNucleusAlphaDecayModel.NUCLEUS_TYPE_CUSTOM){
        	_halfLifeHandleNode.setVisible(true);
        	_halfLifeHandleNode.setOffset( _halfLifeMarkerLine.getX(), _halfLifeMarkerLine.getY() + _halfLifeMarkerLine.getHeight() / 2 );
        }
        else{
        	_halfLifeHandleNode.setVisible(false);
        }
    }
    
    /**
     * Position the specified nucleus at the appropriate location on the
     * chart based on how long it has been around without decaying.
     * 
     * @param nucleus
     */
    private void positionNucleusOnChart(AlphaDecayControl nucleus){
    	
    	PNode nucleusNode = (PNode)_mapNucleiToNodes.get(nucleus);
    	double xAxisPos, yAxisPos;
    	
    	if (nucleusNode == null){
    		// This nucleus does not have a node, probably because it has not
    		// been activated.  That's okay - just ignore the positioning
    		// request.
    		return;
    	}
    	
    	if (!nucleus.hasDecayed()){
    		// The nucleus has not yet decayed, so position it on the upper line.
        	yAxisPos = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION ) - _nucleusNodeRadius;
    	}
    	else{
    		// The nucleus has decayed.  See if it is still falling.
    		MyIntegerHolder fallCounter = (MyIntegerHolder)_mapNucleiToFallCount.get(nucleus);
    		if (fallCounter != null){
    			// The nucleus is falling.  Position it in the space between
    			// the upper and lower lines.
    			double interLineDistance = _usableHeight * 
    			        (POST_DECAY_TIME_LINE_POS_FRACTION - PRE_DECAY_TIME_LINE_POS_FRACTION);
            	yAxisPos = _usableAreaOriginY + ( _usableHeight * PRE_DECAY_TIME_LINE_POS_FRACTION ) 
            	        + (interLineDistance * (1 - (double)fallCounter.getValue() / (double)FALL_COUNT))
            	        - _nucleusNodeRadius;
    		}
    		else{
    			// The nucleus is not falling, so put it on the lower line.
            	yAxisPos = _usableAreaOriginY + ( _usableHeight * POST_DECAY_TIME_LINE_POS_FRACTION ) 
            	        - _nucleusNodeRadius;
    		}
    	}
    	
    	xAxisPos = _graphOriginX + (nucleus.getActivatedTime() + TIME_ZERO_OFFSET) * _msToPixelsFactor 
    	        - _nucleusNodeRadius;
    	nucleusNode.setOffset(xAxisPos, yAxisPos);
    }

    /**
     * Reset the chart.
     */
    public void reset() {

        // Clear the flag that holds off updates after the chart is cleared.
        _chartCleared = false;

        // Redraw the chart.
        update();
    }

    private void handleResetChartButtonPressed() {
    	// TODO: JPB TBD
    }
    
    // TODO: JPB TBD - Talk with Sam and Chris about this.  It seems stupid, but
    // I'm not sure what else to do.
    private class MyIntegerHolder {
    	
    	int m_value;
    	
    	public MyIntegerHolder(int initialValue) {
			m_value = initialValue;
		}
    	
    	public void setValue(int value){
    		m_value = value;
    	}
    	
    	private int getValue(){
    		return m_value;
    	}
    	
    	public void decrement(){
    		m_value--;
    	}
    }
}

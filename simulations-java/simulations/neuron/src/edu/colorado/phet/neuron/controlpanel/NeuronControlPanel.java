/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.NeuronResources;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.model.AbstractLeakChannel;
import edu.colorado.phet.neuron.model.AbstractMembraneChannel;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.model.MembraneChannelTypes;
import edu.colorado.phet.neuron.model.ParticleType;
import edu.colorado.phet.neuron.model.PotassiumIon;
import edu.colorado.phet.neuron.model.PotassiumLeakageChannel;
import edu.colorado.phet.neuron.model.SodiumIon;
import edu.colorado.phet.neuron.model.SodiumLeakageChannel;
import edu.colorado.phet.neuron.view.MembraneChannelNode;
import edu.colorado.phet.neuron.view.NeuronCanvas;
import edu.colorado.phet.neuron.view.ParticleNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Control panel for the neuron sim.
 *
 * @author John Blanco
 */
public class NeuronControlPanel extends ControlPanel {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final Dimension2D OVERALL_SIZE_OF_LEAK_CHANNEL_ICON = new PDimension(38, 50);
	private static final Dimension2D CHANNEL_SIZE_OF_LEAK_CHANNEL_ICON = new PDimension(15, 30);
	
	// The model-view transform below is used to make nodes that typically
	// reside on the canvas be of an appropriate size for inclusion on the
	// control panel.
	private static final ModelViewTransform2D MVT = new ModelViewTransform2D(
			new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0), new Rectangle2D.Double(-10, -10, 20, 20));

	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	private AxonModel axonModel;
	private NeuronCanvas neuronCanvas;
	private LeakChannelSlider sodiumLeakChannelControl;
	private LeakChannelSlider potassiumLeakChannelControl;
	private ConcentrationSlider sodiumConcentrationControl;
	private ConcentrationSlider potassiumConcentrationControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public NeuronControlPanel( PiccoloModule module, Frame parentFrame, AxonModel model, NeuronCanvas canvas ) {
        super();
        
        this.axonModel = model;
        this.neuronCanvas = canvas;
        
        // Listen to the model for changes that affect this control panel.
        model.addListener(new AxonModel.Adapter(){
        	@Override
    		public void channelAdded(AbstractMembraneChannel channel) {
    			updateChannelControlSliders();
    		}
        	
        	@Override
			public void channelRemoved(AbstractMembraneChannel channel) {
        		updateChannelControlSliders();
			}

			@Override
    		public void concentrationRatioChanged(ParticleType atomType) {
    			updateConcentrationControlSliders();
    		}
        });
        
        // Set the control panel's minimum width.
        int minimumWidth = NeuronResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the control for the number of sodium leakage channels.
        sodiumLeakChannelControl = new LeakChannelSlider(NeuronStrings.SODIUM_LEAK_CHANNELS, axonModel,
        		ParticleType.SODIUM_ION); 
        addControlFullWidth(sodiumLeakChannelControl);
        
        // Add the control for the number of potassium leakage channels.
        potassiumLeakChannelControl = new LeakChannelSlider(NeuronStrings.POTASSIUM_LEAK_CHANNELS, axonModel, 
        		ParticleType.POTASSIUM_ION); 
        addControlFullWidth(potassiumLeakChannelControl);
        
        // Add the control for sodium concentration.
        sodiumConcentrationControl = new ConcentrationSlider(NeuronStrings.SODIUM_CONCENTRATION, axonModel,
        		ParticleType.SODIUM_ION);
        addControlFullWidth(sodiumConcentrationControl);
        
        // Add the control for potassium concentration.
        potassiumConcentrationControl = new ConcentrationSlider(NeuronStrings.POTASSIUM_CONCENTRATION, axonModel, 
        		ParticleType.POTASSIUM_ION);
        addControlFullWidth(potassiumConcentrationControl);
        
        // Add the check box for hiding/showing the membrane potential chart.
        addControlFullWidth(createVerticalSpacingPanel(30));
        final JCheckBox chartControlCheckbox = new JCheckBox(NeuronStrings.POTENTIAL_CHART);
        chartControlCheckbox.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				neuronCanvas.setMembranePotentialChartVisible(chartControlCheckbox.isSelected());
			}
		});
        chartControlCheckbox.setAlignmentX(CENTER_ALIGNMENT);
        addControlFullWidth(chartControlCheckbox);
        
        // Add the reset all button.
        addControlFullWidth(createVerticalSpacingPanel(30));
        addResetAllButton( module );
        
        // Update the states of the controls.
        updateChannelControlSliders();
        updateConcentrationControlSliders();
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    private void updateChannelControlSliders(){
    	
    	if (sodiumLeakChannelControl.getValue() != 
    		axonModel.getNumMembraneChannels(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL)){
    		
    		sodiumLeakChannelControl.setValue(
    				axonModel.getNumMembraneChannels(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL));
    	}
    	if (potassiumLeakChannelControl.getValue() != 
    		axonModel.getNumMembraneChannels(MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL)){
    		
    		potassiumLeakChannelControl.setValue(
    				axonModel.getNumMembraneChannels(MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL));
    	}
    }
    
    private void updateConcentrationControlSliders(){
    	
    	if (sodiumConcentrationControl.getValue() != axonModel.getProportionOfAtomsInside(ParticleType.SODIUM_ION)){
    		sodiumConcentrationControl.setValue( axonModel.getProportionOfAtomsInside(ParticleType.SODIUM_ION));
    	}
    	if (potassiumConcentrationControl.getValue() != axonModel.getProportionOfAtomsInside(ParticleType.POTASSIUM_ION)){
    		potassiumConcentrationControl.setValue( axonModel.getProportionOfAtomsInside(ParticleType.POTASSIUM_ION));
    	}
    }
    
    private JPanel createVerticalSpacingPanel(int space){
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
    
    private static class LeakChannelSlider extends LinearValueControl{
    	
        private static final Font LABEL_FONT = new PhetFont(12);

        public LeakChannelSlider(String title, final AxonModel axonModel, ParticleType atomType) {
        	
            super( 0, NeuronConstants.MAX_CHANNELS_PER_TYPE, title, "0", "");
            setUpDownArrowDelta( 1 );
            setTextFieldVisible(false);
            setMinorTicksVisible(false);
            setBorder( BorderFactory.createEtchedBorder() );
            setSnapToTicks(false);

            // Put in the labels for the left and right bottom portion of the
            // slider.
            Hashtable<Double, JLabel> leakChannelSliderLabelTable = new Hashtable<Double, JLabel>();
            JLabel leftLabel = new JLabel(NeuronStrings.NONE);
            leftLabel.setFont( LABEL_FONT );
            leakChannelSliderLabelTable.put( new Double( 0 ), leftLabel );
            JLabel rightLabel = new JLabel(NeuronStrings.LOTS);
            rightLabel.setFont( LABEL_FONT );
            leakChannelSliderLabelTable.put( new Double( NeuronConstants.MAX_CHANNELS_PER_TYPE ), rightLabel );
            setTickLabels( leakChannelSliderLabelTable );

            // Set up the variables that will differ based on the type.
            AbstractLeakChannel leakChannel;
            final MembraneChannelTypes channelType;
            switch (atomType){
            case SODIUM_ION:
            	leakChannel = new SodiumLeakageChannel();
            	channelType = MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL;
            	break;
            case POTASSIUM_ION:
            	leakChannel = new PotassiumLeakageChannel();
            	channelType = MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL;
            	break;
            	
            default:
            	System.err.println(getClass().getName() + " - Error: Unknown leak channel type.");
            	assert false;
            	leakChannel = new SodiumLeakageChannel();  // Just in case.
            	channelType = MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL; // Just in case.
            }
            leakChannel.setDimensions(OVERALL_SIZE_OF_LEAK_CHANNEL_ICON, CHANNEL_SIZE_OF_LEAK_CHANNEL_ICON);
            leakChannel.setRotationalAngle(-Math.PI / 2);
            
            // Create and set the icon image.
            PNode iconNode = new MembraneChannelNode(leakChannel, MVT);
            JLabel _valueLabel = getValueLabel();
            _valueLabel.setIcon( new ImageIcon(iconNode.toImage(40, 40, new Color(0,0,0,0))) );
            _valueLabel.setVerticalTextPosition( JLabel.CENTER );
            _valueLabel.setHorizontalTextPosition( JLabel.LEFT );

            // Register a listener to handle changes.
            addChangeListener(new ChangeListener() {
    			public void stateChanged(ChangeEvent e) {
    				int value = (int)Math.round(getValue());
    				if ( value != axonModel.getNumMembraneChannels(channelType) ){
    					axonModel.setNumMembraneChannels(channelType, value);
    				}
    			}
    		});
		}
    }

    private static class ConcentrationSlider extends LinearValueControl{
    	
        private static final Font LABEL_FONT = new PhetFont(12);

		public ConcentrationSlider(String title, final AxonModel axonModel, final ParticleType atomType) {
            super( 0, 1, title, "0", "");
            setUpDownArrowDelta( 0.01 );
            setTextFieldVisible(false);
            setMinorTicksVisible(false);
            setBorder( BorderFactory.createEtchedBorder() );
            setSnapToTicks(false);
            
            // Put in the labels for the left and right bottom portion of the
            // slider.
            Hashtable<Double, JLabel> concentrationSliderLabelTable = new Hashtable<Double, JLabel>();
            JLabel leftLabel = new JLabel(NeuronStrings.OUTSIDE);
            leftLabel.setFont( LABEL_FONT );
            concentrationSliderLabelTable.put( new Double( 0 ), leftLabel );
            JLabel rightLabel = new JLabel(NeuronStrings.INSIDE);
            rightLabel.setFont( LABEL_FONT );
            concentrationSliderLabelTable.put( new Double( 1 ), rightLabel );
            setTickLabels( concentrationSliderLabelTable );

            // Set up the variables that will differ based on the atom type.
            ParticleNode atomNode;
            switch (atomType){
            case SODIUM_ION:
            	atomNode = new ParticleNode(new SodiumIon(), MVT);
            	break;
            case POTASSIUM_ION:
            	atomNode = new ParticleNode(new PotassiumIon(), MVT);
            	break;
            	
            default:
            	System.err.println(getClass().getName() + " - Error: Unknown atom type.");
            	assert false;
            	atomNode = new ParticleNode(new SodiumIon(), MVT);  // Just in case.
            }
            
            // TODO: This is a workaround for a problem where the icon was
            // being cut off on the edges - basically the stroke was being
            // removed.  Figure out why this is needed and fix it.
//            PNode atomNodeComposite = new PNode();
//            double size = atomNode.getFullBoundsReference().width * 1.4;
//            PhetPPath atomNodeBackground = new PhetPPath(new Rectangle2D.Double(-size / 2, -size / 2, size, size),
//            		new Color(200,200,200,255));
//            atomNodeComposite.addChild(atomNodeBackground);
//            atomNodeComposite.addChild(atomNode);
            
            atomNode.setStrokeOn(false);
            
            // Create and add the icon.
            JLabel _valueLabel = getValueLabel();
            System.out.println(atomNode.getFullBoundsReference());
            Image atomImage = atomNode.toImage(25, 25, new Color(0,0,0,0));
            BufferedImage bufferedAtomImage = BufferedImageUtils.toBufferedImage(atomImage);
            try {
            	
				ImageIO.write(bufferedAtomImage, "png", new File("c:/temp/atom-image-" + atomType.toString() + ".png"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            _valueLabel.setIcon( new ImageIcon(atomImage) );
            _valueLabel.setVerticalTextPosition( JLabel.CENTER );
            _valueLabel.setHorizontalTextPosition( JLabel.LEFT );
            
            // Set up the change listener for this control.
            addChangeListener(new ChangeListener() {
            	public void stateChanged(ChangeEvent e) {
            		double value = getValue();
            		if ( value != axonModel.getProportionOfAtomsInside(atomType) ){
            			axonModel.setConcentration(atomType, value);
            		}
            	}
            });
		}
    }
}

/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Abstract base class for membrane channels, which represent any channel
 * through which atoms can go through to cross a membrane.
 * 
 * @author John Blanco
 */
public abstract class MembraneChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double SIDE_HEIGHT_TO_CHANNEL_HEIGHT_RATIO = 1.3;
	protected static final Random RAND = new Random();
	
	private static final double DEFAULT_PARTICLE_VELOCITY = 40000; // In nanometers per sec of sim time.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// Reference to the model that contains that particles that will be moving
	// through this channel.
	private IParticleCapture modelContainingParticles;
	
	/**
	 * List of the atoms "owned" (meaning that their motion is controlled by)
	 * this channel.
	 */
	private ArrayList<Particle> ownedAtoms = new ArrayList<Particle>();
	
	// Member variables that control the size and position of the channel.
	private Point2D centerLocation = new Point2D.Double();
	private double rotationalAngle = 0; // In radians.
	private Dimension2D channelSize = new PDimension(); // Size of channel only, i.e. where the atoms pass through.
	private Dimension2D overallSize = new PDimension(); // Size including edges.
	
	// Variable that defines how open the channel is.
	private double openness = 0;  // Valid range is 0 to 1, 0 means fully closed, 1 is fully open.
	
	// Variable that defines how inactivated the channel is, which is distinct
	// from openness.
	private double inactivationAmt = 0;  // Valid range is 0 to 1, 0 means completely active, 1 is completely inactive.
	
	// Location of inner and outer openings of the channel.
	private Point2D outerOpeningLocation = new Point2D.Double();
	private Point2D innerOpeningLocation = new Point2D.Double();
	
	// Array of listeners.
	private ArrayList<Listener> listeners = new ArrayList<Listener>();
	
	// Capture zone, which is where particles can be captured by this channel.
	private CaptureZone captureZone = new NullCaptureZone();
	
	// Time values that control how often this channel requests an ion to move
	// through it.  These are initialized here to values that will cause the
	// channel to never request any ions and must be set by the base classes
	// in order to make capture events occur.
	private double captureCountdownTimer = Double.POSITIVE_INFINITY;
	private double minInterCaptureTime = Double.POSITIVE_INFINITY;
	private double maxInterCaptureTime = Double.POSITIVE_INFINITY;
	
	// Velocity for particles that move through this channel.
	private double particleVelocity = DEFAULT_PARTICLE_VELOCITY;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

	public MembraneChannel(double channelWidth, double channelHeight, IParticleCapture modelContainingParticles){
		channelSize.setSize(channelWidth, channelHeight);
		overallSize.setSize(channelWidth * 2.1, channelHeight * SIDE_HEIGHT_TO_CHANNEL_HEIGHT_RATIO);
		this.modelContainingParticles = modelContainingParticles;

		updateTraversalReferencePoints();
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	/**
	 * Static factory method for creating a membrane channel of the specified
	 * type.
	 */
	public static MembraneChannel createMembraneChannel(MembraneChannelTypes channelType, IParticleCapture particleModel,
			IHodgkinHuxleyModel hodgkinHuxleyModel){
		
		MembraneChannel membraneChannel = null;
		
    	switch (channelType){
    	case SODIUM_LEAKAGE_CHANNEL:
    		membraneChannel = new SodiumLeakageChannel(particleModel);
    		break;
    		
    	case SODIUM_GATED_CHANNEL:
    		membraneChannel = new SodiumDualGatedChannel(hodgkinHuxleyModel, particleModel);
    		break;
    		
    	case POTASSIUM_LEAKAGE_CHANNEL:
    		membraneChannel = new PotassiumLeakageChannel(particleModel);
    		break;
    		
		case POTASSIUM_GATED_CHANNEL:
			membraneChannel = new PotassiumGatedChannel(hodgkinHuxleyModel, particleModel);
			break;
    	}
    	
    	assert membraneChannel != null; // Should be able to create all types of channels.
    	return membraneChannel;
	}
	
	/**
	 * Check the supplied list of atoms to see if any are in a location where
	 * this channel wants to take control of them.
	 * 
	 * @param freeAtoms - List of atoms that can be potentially taken.  Any
	 * atoms that are taken are removed from the list.
	 * @return List of atoms for which this channel is taking control.
	 */
	/*
	 * TODO: Feb 12 2010 - The paradigm for moving particles around is changing from having
	 * them controlled by the AxonModel and the channels to having a motion strategy set on
	 * them and have them move themselves.  This routine is being removed as part of that
	 * effort, and should be deleted or reinstated at some point in time.

	abstract public ArrayList<Particle> checkTakeControlParticles(final ArrayList<Particle> freeAtoms);
	*/

	/**
	 * Add any atoms that this channel no longer wants to control to the
	 * provided list.
	 * 
	 * @param freeAtoms - List of atoms that are "free", meaning that they
	 * are not controlled by this channel (nor probably any other).  Atoms
	 * that this membrane no longer wants to control will be added to this
	 * list.
	 * @return List of atoms that this channel is releasing.
	 */
	abstract public ArrayList<Particle> checkReleaseControlParticles(final ArrayList<Particle> freeAtoms);
	
	abstract protected ParticleType getParticleTypeToCapture();
	
	/**
	 * Reset the channel.  If the channel doesn't open or close, or if there
	 * is no internal state, nothings needs to be done, so this defaults to
	 * doing nothing.  Override in descendant classes as needed.
	 */
	public void reset(){
		return;
	}
	
	/**
	 * Returns a boolean value that says whether or not the channel should be
	 * considered open.
	 * 
	 * @return
	 */
	protected boolean isOpen(){
		// The threshold values used here are arbitrary, and can be changed if
		// necessary.
		return (getOpenness() > 0.3 && getInactivationAmt() < 0.7);
	}
	
	/**
	 * Determine whether the provided point is inside the channel.
	 * 
	 * @param pt
	 * @return
	 */
	public boolean isPointInChannel(Point2D pt){
		// Note: A rotational angle of zero is considered to be lying on the
		// side.  Hence the somewhat odd-looking use of height and width in
		// the determination of the channel shape.
		Shape channelShape = new Rectangle2D.Double(
				centerLocation.getX() - channelSize.getHeight() / 2,
				centerLocation.getY() - channelSize.getWidth() / 2,
				channelSize.getHeight(),
				channelSize.getWidth());
		AffineTransform transform = AffineTransform.getRotateInstance(rotationalAngle, centerLocation.getX(), centerLocation.getY());
		Shape rotatedChannelShape = transform.createTransformedShape(channelShape);
		return rotatedChannelShape.contains(pt);
	}
	
	/**
	 * Gets a values that indicates whether this channel has an inactivation
	 * gate.  Most of the channels in this sim do not have these, so the
	 * default is to return false.  This should be overridden in subclasses
	 * that add inactivation gates to the channels.
	 * 
	 * @return
	 */
	public boolean getHasInactivationGate(){
		return false;
	}
	
	/**
	 * TODO: This is a temp routine for debug.
	 */
	public Shape getChannelTestShape(){
		Shape channelShape = new Rectangle2D.Double(
				centerLocation.getX() - channelSize.getHeight() / 2,
				centerLocation.getY() - channelSize.getWidth() / 2,
				channelSize.getHeight(),
				channelSize.getWidth());
		AffineTransform transform = AffineTransform.getRotateInstance(rotationalAngle, centerLocation.getX(), centerLocation.getY());
		Shape rotatedChannelShape = transform.createTransformedShape(channelShape);
		return rotatedChannelShape;
	}
	
	/**
	 * Implements the time-dependent behavior of the gate.
	 * 
	 * @param dt - Amount of time step, in milliseconds.
	 */
	public void stepInTime(double dt){
		if (captureCountdownTimer != Double.POSITIVE_INFINITY){
			if (isOpen()){
				captureCountdownTimer -= dt;
				if (captureCountdownTimer <= 0){
					modelContainingParticles.requestParticleThroughChannel(getParticleTypeToCapture(), this, particleVelocity);
					restartCaptureCountdownTimer();
				}
			}
			else{
				// If the channel is closed the countdown timer shouldn't be
				// running, so this code is generally hit when the membrane
				// just became closed.  Turn off the countdown timer by
				// setting it to infinity.
				captureCountdownTimer = Double.POSITIVE_INFINITY;
			}
		}
	}
	
	/**
	 * Set the motion strategy for a particle that will cause the particle to
	 * traverse the channel.
	 * 
	 * @param particle
	 */
	public void createAndSetTraversalMotionStrategy(Particle particle){
		particle.setMotionStrategy(new MembraneChannelTraversalMotionStrategy(this, particle.getPositionReference()));
	}
	
	protected double getParticleVelocity() {
		return particleVelocity;
	}

	protected void setParticleVelocity(double particleVelocity) {
		this.particleVelocity = particleVelocity;
	}
	
	/**
	 * Start or restart the countdown timer which is used to time the event
	 * where a particle is captured for movement across the membrane.
	 */
	protected void restartCaptureCountdownTimer(){
		if (minInterCaptureTime != Double.POSITIVE_INFINITY && maxInterCaptureTime != Double.POSITIVE_INFINITY){
			assert maxInterCaptureTime > minInterCaptureTime;
			captureCountdownTimer = minInterCaptureTime + RAND.nextDouble() * (maxInterCaptureTime - minInterCaptureTime);
		}
		else{
			captureCountdownTimer = Double.POSITIVE_INFINITY;
		}
	}
	
	/**
	 * Get the identifier for this channel type.
	 */
	abstract public MembraneChannelTypes getChannelType();
	
	/**
	 * Get the "capture zone", which is a shape that represents the space
	 * from which particles may be captured.  If null is returned, this
	 * channel has no capture zone.
	 */
	public CaptureZone getCaptureZone(){
		return captureZone;
	}
	
	protected void setCaptureZone(CaptureZone captureZone){
		this.captureZone = captureZone;
	}
	
	/**
	 * Return a list of the atoms "owned" (meaning that their motion is
	 * controlled by) this channel.  Getting this list does NOT cause the
	 * atoms to be released by the channel.
	 * 
	 * @return a copy of the list of owned atoms.
	 */
	public ArrayList<Particle> getOwnedParticles(){
		return new ArrayList<Particle>(ownedAtoms);
	}
	
	public ArrayList<Particle> forceReleaseAllParticles(final ArrayList<Particle> freeAtoms){
		ArrayList<Particle> releasedAtoms = null;
		if (ownedAtoms.size() > 0){
			releasedAtoms = new ArrayList<Particle>(ownedAtoms);
		}
		ownedAtoms.clear();
		return releasedAtoms;
	}
	
	public Dimension2D getChannelSize(){
		return new PDimension(channelSize);
	}
	
	public Point2D getCenterLocation(){
		return new Point2D.Double(centerLocation.getX(), centerLocation.getY());
	}
	
	public void setCenterLocation(Point2D newCenterLocation) {
		centerLocation.setLocation(newCenterLocation);
		captureZone.setOriginPoint(newCenterLocation);
		updateTraversalReferencePoints();
	}

	public void setRotationalAngle(double rotationalAngle){
		this.rotationalAngle = rotationalAngle;
		captureZone.setRotationalAngle(rotationalAngle);
		updateTraversalReferencePoints();
	}
	
	public double getRotationalAngle(){
		return rotationalAngle;
	}
	
	/**
	 * Get the overall 2D size of the channel, which includes both the part
	 * that the particles travel through as well as the edges.
	 * 
	 * @return
	 */
	public Dimension2D getOverallSize(){
		return overallSize;
	}
	
	public void setDimensions( Dimension2D overallSize, Dimension2D channelSize ){
		this.overallSize.setSize(overallSize);
		this.channelSize.setSize(channelSize);
		updateTraversalReferencePoints();
	}
	
	public double getOpenness() {
		return openness;
	}
	
	protected void setOpenness(double openness) {
		if (this.openness != openness){
			this.openness = openness;
			notifyOpennessChanged();
		}
	}
	
	public double getInactivationAmt(){
		return inactivationAmt;
	}

	protected void setInactivationAmt(double inactivationAmt) {
		if (this.inactivationAmt != inactivationAmt){
			this.inactivationAmt = inactivationAmt;
			notifyInactivationAmtChanged();
		}
	}
	
	/**
	 * Get the set of points through which a particle at the specified
	 * location should travel when traversing this channel.  In this base
	 * class implementation, two points are provided, one at the entrance of
	 * the channel and one at the exit.  Which point is which for the given
	 * particle is based on which one it is closet to.
	 * 
	 * @param startingLocation
	 * @return
	 */
	public ArrayList<Point2D> getTraversalPoints(Point2D startingLocation){
		
		ArrayList<Point2D> traversalPoints = new ArrayList<Point2D>();
		if (startingLocation.distance(innerOpeningLocation) < startingLocation.distance(outerOpeningLocation)){
			traversalPoints.add(innerOpeningLocation);
			traversalPoints.add(outerOpeningLocation);
		}
		else{
			traversalPoints.add(outerOpeningLocation);
			traversalPoints.add(innerOpeningLocation);
		}
		return traversalPoints;
	}
	
	/**
	 * Default implementation, should be overridden in most base classes.
	 */
	private void updateTraversalReferencePoints(){
		Point2D ctr = getCenterLocation();
		double r = getChannelSize().getHeight() * 0.6; // Make the point a little outside the channel.
		outerOpeningLocation = new Point2D.Double(ctr.getX() + Math.cos(rotationalAngle) * r,
				ctr.getY() + Math.sin(rotationalAngle) * r);
		innerOpeningLocation = new Point2D.Double(ctr.getX() - Math.cos(rotationalAngle) * r,
				ctr.getY() - Math.sin(rotationalAngle) * r);
	}

	public Color getChannelColor(){
		return Color.MAGENTA;
	}
	
	public Color getEdgeColor(){
		return Color.RED;
	}
	
	/**
	 * Get a reference to the list of owned atoms.
	 * @return
	 */
	protected ArrayList<Particle> getOwnedAtomsRef(){
		return ownedAtoms;
	}
	
	public void addListener(Listener listener){
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener){
		listeners.remove(listener);
	}
	
	public void remove(){
		notifyRemoved();
	}
	
	private void notifyRemoved(){
		for (Listener listener : listeners){
			listener.removed();
		}
	}
	
	private void notifyOpennessChanged(){
		for (Listener listener : listeners){
			listener.opennessChanged();
		}
	}
	
	private void notifyInactivationAmtChanged(){
		for (Listener listener : listeners){
			listener.inactivationAmtChanged();
		}
	}
	
	public static interface Listener{
		void removed();
		void opennessChanged();
		void inactivationAmtChanged();
	}
	
	public static class Adapter implements Listener {
		public void removed() {}
		public void opennessChanged() {}
		public void inactivationAmtChanged() {}
	}
	
	protected double getMaxInterCaptureTime() {
		return maxInterCaptureTime;
	}

	protected void setMaxInterCaptureTime(double maxInterCaptureTime) {
		this.maxInterCaptureTime = maxInterCaptureTime;
	}

	protected double getMinInterCaptureTime() {
		return minInterCaptureTime;
	}

	protected void setMinInterCaptureTime(double minInterCaptureTime) {
		this.minInterCaptureTime = minInterCaptureTime;
	}
	
	protected double getCaptureCountdownTimer() {
		return captureCountdownTimer;
	}
}

/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a strand of DNA in the model, which includes its
 * size, shape, location, and the locations of genes upon it.
 * 
 * @author John Blanco
 */
public class DnaStrand {
	
	// Length of one "cycle" of the DNA strand, in nanometers.
	private static final double DNA_WAVE_LENGTH = 5;
	
	// Spacing between each "sample" that defines the DNA strand shape.
	// Larger numbers mean finer resolution, and thus a curvier shape.  This
	// is in nanometers.
	private static final double DNA_SAMPLE_SPACING = 1;
	
	// Offset in the x direction between the two DNA strands.
	private static final double DNA_INTER_STRAND_OFFSET = 4;

	private DoubleGeneralPath strand1Shape = new DoubleGeneralPath();
	private Line2D strand2Shape;
	private final Dimension2D size;
	private Point2D position = new Point2D.Double();
	private ArrayList<GeneSegmentShape> geneSectionShapeList = new ArrayList<GeneSegmentShape>();
	
	public DnaStrand(Dimension2D size, Point2D initialPosition){
		this.size = new PDimension(size);
		setPosition(initialPosition);
		
		updateStrandShapes();
		
		// Create the gene segments, which are the places where genes will
		// eventually be placed.
		Shape LacIGeneShape = new LacIGene(null).getShape();
		geneSectionShapeList.add( new GeneSegmentShape( LacIGeneShape, new Point2D.Double( -20, 0) ) );
	}
	
	public Point2D getPositionRef() {
		return position;
	}

	public void setPosition(double x, double y) {
		position.setLocation(x, y);
	}
	
	public void setPosition(Point2D newPosition) {
		setPosition(newPosition.getX(), newPosition.getY());
	}

	public Dimension2D getSize() {
		return size;
	}
	
	public Shape getStrand1Shape(){
		return strand1Shape.getGeneralPath();
	}
	
	/**
	 * Get a list of the shapes that represent the spaces on the DNA strand
	 * where the genes should go.
	 */
	public ArrayList<GeneSegmentShape> getGeneSegmentShapeList(){
		return new ArrayList<GeneSegmentShape>(geneSectionShapeList);
	}
	
	private void updateStrandShapes(){
		double startPosX = -size.getWidth() / 2;
		double startPosy = 0;
		strand1Shape.moveTo(startPosX, startPosy);
		double angle = 0;
		double angleIncrement = Math.PI * 2 / DNA_WAVE_LENGTH;
		for (double xPos = startPosX; xPos - startPosX < size.getWidth(); xPos += DNA_SAMPLE_SPACING){
			strand1Shape.lineTo( (float)xPos, (float)(Math.sin(angle) * size.getHeight() / 2));
			angle += angleIncrement;
		}
	}
	
	public static class GeneSegmentShape extends Area {

		private final Point2D offsetFromDnaStrandPos = new Point2D.Double();
		
		public GeneSegmentShape(Shape s, Point2D offsetFromDnaStrandPos) {
			super(s);
			this.offsetFromDnaStrandPos.setLocation(offsetFromDnaStrandPos);
		}
		
		public Point2D getOffsetFromDnaStrandPosRef(){
			return offsetFromDnaStrandPos;
		}
	}
}

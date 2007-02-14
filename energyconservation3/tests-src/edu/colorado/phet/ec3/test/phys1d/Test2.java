package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Feb 13, 2007
 * Time: 11:12:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class Test2 extends JFrame {
    private JFrame controlFrame;

    public Test2() {
        PSwingCanvas pSwingCanvas = new PSwingCanvas();
        pSwingCanvas.setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setContentPane( pSwingCanvas );

        CubicSpline2D cubicSpline = CubicSpline2D.interpolate( new Point2D[]{
                new Point2D.Double( 100, 50 ),
                new Point2D.Double( 200, 100 ),
                new Point2D.Double( 300, 50 )
        } );
        MyCubicCurve2DGraphic mySplineGraphic = new MyCubicCurve2DGraphic( cubicSpline );
        pSwingCanvas.getLayer().addChild( mySplineGraphic );
        setSize( 800, 600 );

        final Particle1D particle1d = new Particle1D( cubicSpline );
        ParticleGraphic particleGraphic = new ParticleGraphic( particle1d );
        pSwingCanvas.getLayer().addChild( particleGraphic );

        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Point2D origLoc = particle1d.getLocation();
                double origA = particle1d.alpha;
                particle1d.stepInTime( 0.001 );

                Point2D newLoc = particle1d.getLocation();
                double dist = newLoc.distance( origLoc );
                double da = particle1d.alpha - origA;
                System.out.println( "dA = " + da + " root(dx^2+dy^2)=" + dist );
            }
        } );
        timer.start();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        controlFrame = new JFrame();
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        JRadioButton verlet = new JRadioButton( "Verlet", particle1d.getUpdateStrategy() instanceof Particle1D.Verlet );
        verlet.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle1d.setVelocity( 0 );
                particle1d.setUpdateStrategy( particle1d.createVerlet() );
            }
        } );
        JRadioButton constantVel = new JRadioButton( "Constant Velocity", particle1d.getUpdateStrategy() instanceof Particle1D.ConstantVelocity );
        constantVel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle1d.setVelocity( 10 );
                particle1d.setUpdateStrategy( particle1d.createConstantVelocity() );
            }
        } );
        controlPanel.add( verlet, gridBagConstraints );
        controlPanel.add( constantVel, gridBagConstraints );
        controlFrame.setContentPane( controlPanel );
        controlFrame.pack();
        controlFrame.setLocation( this.getX() + this.getWidth(), this.getY() );
        controlFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        ButtonGroup buttonGroup=new ButtonGroup();
        buttonGroup.add( verlet );
        buttonGroup.add( constantVel );
    }

    static class ParticleGraphic extends PNode {
        private Particle1D particle1d;
        private PhetPPath phetPPath;

        public ParticleGraphic( Particle1D particle1d ) {
            //To change body of created methods use File | Settings | File Templates.
            this.particle1d = particle1d;
            phetPPath = new PhetPPath( new BasicStroke( 1 ), Color.red );
            phetPPath.setPathTo( new Ellipse2D.Double( 0, 0, 10, 10 ) );
            addChild( phetPPath );
            particle1d.addListener( this );
            update();
        }

        private void update() {
            phetPPath.setOffset( particle1d.getX() - phetPPath.getWidth() / 2, particle1d.getY() - phetPPath.getHeight() / 2 );
        }
    }

    public static class Particle1D {

        private double alpha = 0.25;
        private CubicSpline2D cubicSpline;
        private double velocity = 0;
        private ArrayList listeners = new ArrayList();
        private UpdateStrategy updateStrategy = new Verlet();
        private double g = 9.8 * 100000;//in pixels per time squared

        public Particle1D( CubicSpline2D cubicSpline ) {
            this.cubicSpline = cubicSpline;
        }

        public double getX() {
            return cubicSpline.evaluate( alpha ).getX();
        }

        public double getY() {
            return cubicSpline.evaluate( alpha ).getY();
        }

        public void stepInTime( double dt ) {
            updateStrategy.stepInTime( dt );

            for( int i = 0; i < listeners.size(); i++ ) {
                ParticleGraphic particleGraphic = (ParticleGraphic)listeners.get( i );
                particleGraphic.update();
            }
        }

        public void addListener( ParticleGraphic particleGraphic ) {
            listeners.add( particleGraphic );
        }

        public Point2D getLocation() {
            return new Point2D.Double( getX(), getY() );
        }

        public UpdateStrategy getUpdateStrategy() {
            return updateStrategy;
        }

        public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
            this.updateStrategy = updateStrategy;
        }

        public UpdateStrategy createVerlet() {
            return new Verlet();
        }

        public UpdateStrategy createConstantVelocity() {
            return new ConstantVelocity();
        }

        public void setVelocity( double v ) {
            this.velocity = v;
        }

        public interface UpdateStrategy {
            void stepInTime( double dt );
        }

        public class Verlet implements UpdateStrategy {

            public void stepInTime( double dt ) {
                double origAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
                double ds = velocity * dt - 0.5 * g * Math.cos( origAngle ) * dt * dt;

                alpha += cubicSpline.getFractionalDistance( alpha, ds );
                double newAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
                velocity = velocity + g * ( Math.cos( origAngle ) + Math.cos( newAngle ) ) / 2.0 * dt;

                alpha = MathUtil.clamp( 0, alpha, 1.0 );

                if( alpha <= 0 ) {
                    velocity *= -1;
                }
                if( alpha >= 1 ) {
                    velocity *= -1;
                }
            }
        }

        public class ConstantVelocity implements UpdateStrategy {

            public void stepInTime( double dt ) {
                alpha += velocity * dt;
                alpha += cubicSpline.getFractionalDistance( alpha, velocity * dt );

                alpha = MathUtil.clamp( 0, alpha, 1.0 );

                if( alpha <= 0 ) {
                    velocity *= -1;
                }
                if( alpha >= 1 ) {
                    velocity *= -1;
                }
            }
        }
    }

    static class MyCubicCurve2DGraphic extends PNode {
        private CubicSpline2D cubicSpline2D;
        private PhetPPath phetPPath;

        public MyCubicCurve2DGraphic( CubicSpline2D splineSurface ) {
            this.cubicSpline2D = splineSurface;
            //To change body of created methods use File | Settings | File Templates.
            phetPPath = new PhetPPath( new BasicStroke( 1 ), Color.blue );
            addChild( phetPPath );
            update();
        }

        private void update() {
            DoubleGeneralPath doubleGeneralPath = new DoubleGeneralPath( cubicSpline2D.evaluate( 0 ) );
            double ds = 0.01;
            for( double s = ds; s <= 1.0; s += ds ) {
                doubleGeneralPath.lineTo( cubicSpline2D.evaluate( s ) );
            }
            phetPPath.setPathTo( doubleGeneralPath.getGeneralPath() );
        }
    }

    static class MySplineGraphic extends PNode {
        private SplineSurface splineSurface;
        private PhetPPath phetPPath;

        public MySplineGraphic( SplineSurface splineSurface ) {
            this.splineSurface = splineSurface;
            //To change body of created methods use File | Settings | File Templates.
            phetPPath = new PhetPPath( new BasicStroke( 1 ), Color.blue );
            addChild( phetPPath );
            update();
        }

        private void update() {
            phetPPath.setPathTo( splineSurface.getSpline().getInterpolationPath() );
        }
    }

    public static void main( String[] args ) {
        new Test2().start();
    }

    private void start() {
        //To change body of created methods use File | Settings | File Templates.
        setVisible( true );
        controlFrame.setVisible( true );
    }
}

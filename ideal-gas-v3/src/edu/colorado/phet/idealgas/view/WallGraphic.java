/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.collision.Wall;
import edu.colorado.phet.common.util.Translatable;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

/**
 * WallGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class WallGraphic extends PhetShapeGraphic implements Wall.ChangeListener {
    public static final int ALL = 0, EAST_WEST = 1, NORTH_SOUTH = 2;
    public static final Object NORTH = new Object();
    private Wall wall;
//    private List resizableDirections = new ArrayList();
    private boolean isResizable = false;
    private boolean isResizingEast = false;
    private boolean isResizingWest = false;
    private boolean isResizingNorth = false;
    private boolean isResizingSouth = false;

    private double hotSpotRadius = 2;

    /**
     * @param wall
     * @param component
     * @param fill
     * @param borderPaint
     * @param translationDirection
     */
    public WallGraphic( Wall wall, Component component, Paint fill, Paint borderPaint,
                        int translationDirection ) {
        this( wall, component, fill, translationDirection );
        setStroke( new BasicStroke( 1f ) );
        setBorderPaint( borderPaint );
    }

    /**
     * @param wall
     * @param component
     * @param fill
     * @param translationDirection
     */
    public WallGraphic( final Wall wall, Component component, Paint fill,
                        int translationDirection ) {
        super( component, wall.getBounds(), fill );
        this.wall = wall;
        wall.addChangeListener( this );

        setCursorHand();

        // Add a listener for resize events
        addTranslationListener( new Resizer() );

        // Add mouseable behavior
        if( translationDirection == EAST_WEST ) {
            addTranslationListener( new EastWestTranslator( wall ) );
        }
        if( translationDirection == NORTH_SOUTH ) {
            addTranslationListener( new NorthSouthTranslator( wall ) );
        }
        if( translationDirection == ALL ) {
            addTranslationListener( new NorthSouthTranslator( wall ) );
            addTranslationListener( new EastWestTranslator( wall ) );
        }

        // Add a listener that will manage the type of cursor shown
        component.addMouseMotionListener( new CursorManager() );

        // Add a listener that will detect if the user wants to resize the wall
        component.addMouseListener( new ResizingDetector( wall ) );
    }

    /**
     * Sets the wall to be resizable in a specified direction.
     *
     * @param isResizable
     */
    public void setIsResizable( boolean isResizable ) {
        this.isResizable = isResizable;
    }

    //-----------------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------------

    public void wallChanged( Wall.ChangeEvent event ) {
        Wall wall = event.getWall();
        setBounds( new Rectangle( (int)wall.getBounds().getX(), (int)wall.getBounds().getY(),
                                  (int)wall.getBounds().getWidth(), (int)wall.getBounds().getHeight() ) );
        setBoundsDirty();
        repaint();
    }

    //----------------------------------------------------------------
    // Translation listeners
    //----------------------------------------------------------------

    private class EastWestTranslator implements TranslationListener {
        private Translatable translatable;

        public EastWestTranslator( Translatable translatable ) {
            this.translatable = translatable;
        }

        public void translationOccurred( TranslationEvent translationEvent ) {
            // If the control key is down, it means to resize
            if( !( isResizingEast || isResizingWest ) ) {
//            if( !translationEvent.getMouseEvent().isControlDown() ) {
                translatable.translate( translationEvent.getDx(), 0 );
            }
        }
    }

    private class NorthSouthTranslator implements TranslationListener {
        private Translatable translatable;

        public NorthSouthTranslator( Translatable translatable ) {
            this.translatable = translatable;
        }

        public void translationOccurred( TranslationEvent translationEvent ) {
            // If the control key is down, it means to resize
            if( !( isResizingNorth || isResizingSouth ) ) {
//            if( !translationEvent.getMouseEvent().isControlDown() ) {
                translatable.translate( 0, translationEvent.getDy() );
            }
        }
    }

    /**
     * Resizes the wall
     */
    private class Resizer implements TranslationListener {

        public void translationOccurred( TranslationEvent translationEvent ) {
            if( isResizable /* && translationEvent.getMouseEvent().isControlDown()*/ ) {
                double minX = wall.getBounds().getMinX();
                double maxX = wall.getBounds().getMaxX();
                double minY = wall.getBounds().getMinY();
                double maxY = wall.getBounds().getMaxY();
                Point mouseLoc = translationEvent.getMouseEvent().getPoint();

                if( isResizingNorth ) {
                    minY = mouseLoc.y;
                }
                if( isResizingSouth ) {
                    maxY = mouseLoc.y;
                }
                if( isResizingWest ) {
                    minX = mouseLoc.x;
                }
                if( isResizingEast ) {
                    maxX = mouseLoc.x;
                }

                wall.setBounds( new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY ) );
            }
        }
    }

    /**
     * Detects that the user wishes to resize the wall.
     * <p/>
     * Sets the cursor and an internal flag
     */
    private class ResizingDetector extends MouseAdapter {
        private final Wall wall;
        private Cursor savedCursor;

        public ResizingDetector( Wall wall ) {
            this.wall = wall;
        }

        public void mousePressed( MouseEvent e ) {
            if( isResizable /*&& e.isControlDown()*/ ) {

                double minX = wall.getBounds().getMinX();
                double maxX = wall.getBounds().getMaxX();
                double minY = wall.getBounds().getMinY();
                double maxY = wall.getBounds().getMaxY();
                Point mouseLoc = e.getPoint();

                savedCursor = getComponent().getCursor();

                if( Math.abs( mouseLoc.y - minY ) <= hotSpotRadius ) {
                    isResizingNorth = true;
                    getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
                }
                else if( Math.abs( mouseLoc.y - maxY ) <= hotSpotRadius ) {
                    isResizingSouth = true;
                    getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.S_RESIZE_CURSOR ) );
                }
                else if( Math.abs( mouseLoc.x - minX ) <= hotSpotRadius ) {
                    isResizingWest = true;
                    getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR ) );
                }
                else if( Math.abs( mouseLoc.x - maxX ) <= hotSpotRadius ) {
                    isResizingEast = true;
                    getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
                }
            }
        }

        public void mouseReleased( MouseEvent e ) {
            // Add a listener that will cancel the resize mode when the mouse is released
            getComponent().addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent e ) {
                    isResizingEast = false;
                    isResizingWest = false;
                    isResizingNorth = false;
                    isResizingSouth = false;

                    getComponent().setCursor( savedCursor );
                }
            } );
        }
    }

    private class CursorManager implements MouseMotionListener {

        public void mouseDragged( MouseEvent e ) {
            // noop
        }

        public void mouseMoved( MouseEvent e ) {
            Point mouseLoc = e.getPoint();
            if( contains( mouseLoc.x, mouseLoc.y ) ) {
                if( isResizable ) {
                    double minX = wall.getBounds().getMinX();
                    double maxX = wall.getBounds().getMaxX();
                    double minY = wall.getBounds().getMinY();
                    double maxY = wall.getBounds().getMaxY();

                    if( Math.abs( mouseLoc.y - minY ) <= hotSpotRadius ) {
                        getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
                    }
                    else if( Math.abs( mouseLoc.y - maxY ) <= hotSpotRadius ) {
                        getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.S_RESIZE_CURSOR ) );
                    }
                    else if( Math.abs( mouseLoc.x - minX ) <= hotSpotRadius ) {
                        getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR ) );
                    }
                    else if( Math.abs( mouseLoc.x - maxX ) <= hotSpotRadius ) {
                        getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
                    }
                    else {
                        getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                    }
                }
                else {
                    getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                }
            }
        }
    }
}


package edu.colorado.phet.translationutility.userinterface;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.util.HTMLValidator;
import edu.colorado.phet.translationutility.util.MessageFormatValidator;


public class TargetTextPanel extends JPanel {

    private static final Color OK_COLOR = Color.WHITE;
    private static final Color INVALID_COLOR = Color.RED;

    private final TargetTextArea textArea;
    private final JLabel errorIcon;
    private final MessageFormatValidator messageFormatValidator;
    private final HTMLValidator htmlValidator;
    private boolean isValid;
    private String errorMessage;

    public TargetTextPanel( String key, String sourceValue, String value ) {
        super();

        // target text area
        textArea = new TargetTextArea( key, sourceValue, value );
        textArea.setBackground( OK_COLOR );
        textArea.addFocusListener( new FocusAdapter() {

            public void focusLost( FocusEvent e ) {
                validateValue();
            }
        } );
        textArea.addKeyListener( new KeyAdapter() {

            public void keyPressed( KeyEvent e ) {
                clearError();
            }
        } );

        // icon that provides access to error message
        errorIcon = new JLabel( TUResources.getIcon( "errorIcon.png" ) );
        errorIcon.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        errorIcon.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent event ) {
                JOptionPane.showMessageDialog( TargetTextPanel.this, errorMessage, "Details", JOptionPane.ERROR_MESSAGE );
            }
        } );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.addComponent( textArea, 0, 1 );
        layout.addComponent( errorIcon, 0, 2 );
        layout.setMinimumWidth( 2, errorIcon.getPreferredSize().width + 20 );

        messageFormatValidator = new MessageFormatValidator( sourceValue );
        htmlValidator = new HTMLValidator( sourceValue );
        validateValue();
    }
    
    public TargetTextArea getTextArea() {
        return textArea;
    }
    
    public boolean isValid() {
        validateValue();
        return isValid;
    }

    private void clearError() {
        isValid = true;
        errorMessage = null;
        textArea.setBackground( OK_COLOR );
        errorIcon.setVisible( false );
    }
    
    /**
     * Validates the text area's value, comparing it to the source value.
     * Notifies interested listeners if there are validation errors.
     */
    public void validateValue() {

        clearError();

        // validate
        String targetString = textArea.getText();
        ArrayList<String> missingPlaceholders = messageFormatValidator.validate( targetString );
        ArrayList<String> missingTags = htmlValidator.validate( targetString );

        // report errors
        if ( missingPlaceholders != null || missingTags != null ) {
            isValid = false;
            errorMessage = createErrorMessage( missingPlaceholders, missingTags );
            textArea.setBackground( INVALID_COLOR );
            errorIcon.setVisible( true );
        }
    }

    private String createErrorMessage( ArrayList<String> missingPlaceholders, ArrayList<String> missingTags ) {

        String message = "This translation has the following errors:";

        if ( missingPlaceholders != null ) {
            message += "\n";
            message += "missing MessageFormat placeholders:";
            for ( String placeholder : missingPlaceholders ) {
                message += " " + placeholder;
            }
        }

        if ( missingTags != null ) {
            message += "\n";
            message += "missing HTML tags:";
            for ( String tag : missingTags ) {
                message += " " + tag;
            }
        }

        return message;
    }

}

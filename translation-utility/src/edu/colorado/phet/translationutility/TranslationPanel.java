/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * TranslationPanel is a panel that consists of 3 columns for localizing strings.
 * From left-to-right, the columns are: key, source language value, target language value.
 * The target language value is editable.
 * Buttons at the bottom of the panel support various actions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TranslationPanel extends JPanel {
   
    private static final Font DEFAULT_FONT = new JLabel().getFont();
    private static final Font TITLE_FONT = new Font( DEFAULT_FONT.getName(), Font.BOLD,  DEFAULT_FONT.getSize() + 4 );
    private static final Font KEY_FONT = new Font( DEFAULT_FONT.getName(), Font.PLAIN, DEFAULT_FONT.getSize() );
    private static final Font SOURCE_VALUE_FONT = new Font( DEFAULT_FONT.getName(), Font.PLAIN, DEFAULT_FONT.getSize() );
    private static final Font TARGET_VALUE_FONT = new Font( DEFAULT_FONT.getName(), Font.PLAIN, DEFAULT_FONT.getSize() );
    
    private static final int KEY_COLUMN = 0;
    private static final int SOURCE_COLUMN = 1;
    private static final int TARGET_COLUMN = 2;
    
    private static final int TEXT_AREA_COLUMNS = 20;
    private static final Border TEXT_AREA_BORDER = BorderFactory.createCompoundBorder( 
            /* outside */ BorderFactory.createLineBorder( Color.BLACK, 1 ), 
            /* inside */ BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
    
    /*
     * Associates a key with a JTextArea.
     */
    private static class TargetTextArea extends JTextArea {

        private final String _key;

        public TargetTextArea( String key, String value ) {
            super( value );
            _key = key;
        }

        public String getKey() {
            return _key;
        }
    }
    
    private JarFileManager _jarFileManager;
    private final String _targetCountryCode;
    private ArrayList _targetTextAreas; // array of TargetTextArea

    public TranslationPanel( JarFileManager jarFileManager, String targetCountryCode ) {
        super();
        
        _jarFileManager = jarFileManager;
        _targetCountryCode = targetCountryCode;
        _targetTextAreas = new ArrayList();
        
        JPanel inputPanel = createInputPanel();
        JPanel buttonPanel = createButtonPanel();
        JScrollPane scrollPane = new JScrollPane( inputPanel );
        
        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( buttonPanel, BorderLayout.CENTER );

        setLayout( new BorderLayout() );
        setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        add( scrollPane, BorderLayout.CENTER );
        add( bottomPanel, BorderLayout.SOUTH );
    }
    
    private JPanel createInputPanel() {
        
        JPanel inputPanel = new JPanel();
        
        String projectName = _jarFileManager.getProjectName();
        Properties sourceProperties = _jarFileManager.readSourceProperties();
        
        EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setInsets( new Insets( 2, 5, 2, 5 ) ); // top, left, bottom, right
        int row = 0;
        
        JLabel projectNameLabel = new JLabel( projectName );
        projectNameLabel.setFont( TITLE_FONT );
        layout.addAnchoredComponent( projectNameLabel, row, KEY_COLUMN, GridBagConstraints.WEST );
        JLabel sourceLocaleLable = new JLabel( "en" );
        sourceLocaleLable.setFont( TITLE_FONT );
        layout.addAnchoredComponent( sourceLocaleLable, row, SOURCE_COLUMN, GridBagConstraints.WEST );
        JLabel targetLocaleLable = new JLabel( _targetCountryCode );
        targetLocaleLable.setFont( TITLE_FONT );
        layout.addAnchoredComponent( targetLocaleLable, row, TARGET_COLUMN, GridBagConstraints.WEST );
        row++;
        layout.addComponent( new JSeparator(), row, 0, 3, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL );
        row++;

        // sort the keys in lexographically acending order
        Enumeration keys = sourceProperties.propertyNames();
        TreeSet sortedSet = new TreeSet();
        while ( keys.hasMoreElements() ) {
            String key = (String) keys.nextElement();
            sortedSet.add( key );
        }
        
        Iterator i = sortedSet.iterator();
        while ( i.hasNext() ) {

            String key = (String) i.next();
            String sourceValue = sourceProperties.getProperty( key );

            JLabel keyLabel = new JLabel( key );
            keyLabel.setFont( KEY_FONT );

            JTextArea sourceTextArea = new JTextArea( sourceValue );
            sourceTextArea.setFont( SOURCE_VALUE_FONT );
            sourceTextArea.setColumns( TEXT_AREA_COLUMNS );
            sourceTextArea.setLineWrap( true );
            sourceTextArea.setWrapStyleWord( true );
            sourceTextArea.setEditable( false );
            sourceTextArea.setBorder( TEXT_AREA_BORDER );
            sourceTextArea.setBackground( this.getBackground() );

            TargetTextArea targetTextArea = new TargetTextArea( key, sourceValue );
            targetTextArea.setFont( TARGET_VALUE_FONT );
            targetTextArea.setColumns( sourceTextArea.getColumns() );
            targetTextArea.setRows( sourceTextArea.getLineCount() );
            targetTextArea.setLineWrap( true );
            targetTextArea.setWrapStyleWord( true );
            targetTextArea.setEditable( true );
            targetTextArea.setBorder( TEXT_AREA_BORDER );
            _targetTextAreas.add( targetTextArea );

            layout.addAnchoredComponent( keyLabel, row, KEY_COLUMN, GridBagConstraints.EAST );
            layout.addComponent( sourceTextArea, row, SOURCE_COLUMN );
            layout.addComponent( targetTextArea, row, TARGET_COLUMN );
            row++;
        }
        
        return inputPanel;
    }
    
    private JPanel createButtonPanel() {
        
        JButton testButton = new JButton( "Test simulation" );
        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                Properties targetProperties = getTargetProperties();
                _jarFileManager.writeTargetProperties( targetProperties, _targetCountryCode );
                _jarFileManager.runJarFile( _targetCountryCode );
            }
        } );
        
        JButton submitButton = new JButton( "Submit translation...");
        submitButton.setEnabled( false );//XXX
        
        JButton helpButton = new JButton( "Help..." );
        helpButton.setEnabled( false );//XXX
        
        JPanel buttonPanel = new JPanel( new GridLayout( 1, 5 ) );
        buttonPanel.add( testButton );
        buttonPanel.add( submitButton );
        buttonPanel.add( helpButton );

        JPanel panel = new JPanel();
        panel.add( buttonPanel );
        return panel;
    }
    
    public Properties getTargetProperties() {
        Properties properties = new Properties();
        Iterator i = _targetTextAreas.iterator();
        while ( i.hasNext() ) {
            TargetTextArea targetTextArea = (TargetTextArea) i.next();
            String key = targetTextArea.getKey();
            String targetValue = targetTextArea.getText();
            properties.put( key, targetValue );
        }
        return properties;
    }
}

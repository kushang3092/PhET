
package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.view.RightArrowNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.game.*;
import edu.colorado.phet.reactantsproductsandleftovers.view.game.DevValuesNode.DevAfterValuesNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.game.DevValuesNode.DevBeforeValuesNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.realreaction.RealReactionEquationNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Game" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameCanvas extends RPALCanvas {
    
    // node collection names, for managing visibility
    private static final String GAME_SETTINGS_STATE = "gameSetting";
    private static final String GAME_SUMMARY_STATE = "gameSummary";
    private static final String FIRST_ATTEMPT_STATE = "firstAttempt";
    private static final String FIRST_ATTEMPT_CORRECT_STATE = "firstAttemptCorrect";
    private static final String FIRST_ATTEMPT_WRONG_STATE = "firstAttemptWrong";
    private static final String SECOND_ATTEMPT_STATE = "secondAttempt";
    private static final String SECOND_ATTEMPT_CORRECT_STATE = "secondAttemptCorrect";
    private static final String SECOND_ATTEMPT_WRONG_STATE = "secondAttemptWrong";
    private static final String ANSWER_SHOWN_STATE = "answerShown";
    
    private static final double BUTTON_X_SPACING = 20;
    
    private final GameModel model;
    private final NodeVisibilityManager visibilityManager;
    
    // nodes allocated once, always visible
    private final PhetPNode parentNode;
    private final PhetPNode buttonsParentNode;
    private final ScoreboardNode scoreboardNode;
    private final RightArrowNode arrowNode;
    private final ReactionNumberLabelNode reactionNumberLabelNode;
    
    // nodes allocated once, visibility changes
    private final PhetPNode gameSettingsNode;
    private final FaceNode faceNode;
    private final GradientButtonNode checkButton, nextButton, tryAgainButton, showAnswerButton;
    private final GameInstructionsNode instructionsNode;
    private final GameSummaryNode gameSummaryNode;
    
    // developer nodes, allocated once, always visible
    private final DevBeforeValuesNode devBeforeValuesNode;
    private final DevAfterValuesNode devAfterValuesNode;

    // these nodes are mutable, allocated when reaction changes, always visible
    private RealReactionEquationNode equationNode;
    private GameBeforeNode beforeNode;
    private GameAfterNode afterNode;

    public GameCanvas( final GameModel model, Resettable resettable ) {
        super();
        
        // game settings
        gameSettingsNode = new GameSettingsNode( model );
        gameSettingsNode.scale( 1.5 ); //XXX scale
        addChild( gameSettingsNode );

        // game summary
        gameSummaryNode = new GameSummaryNode( model );
        gameSummaryNode.scale( 1.5 ); //XXX scale
        addChild( gameSummaryNode );

        // all other nodes are children of this node
        parentNode = new PhetPNode();
        addChild( parentNode );
        parentNode.moveToBack();

        // right-pointing arrow
        arrowNode = new RightArrowNode();
        parentNode.addChild( arrowNode );

        // reaction number label
        reactionNumberLabelNode = new ReactionNumberLabelNode( model );
        parentNode.addChild( reactionNumberLabelNode );

        // scoreboard
        scoreboardNode = new ScoreboardNode( model );
        scoreboardNode.scale( 1.5 ); //XXX scale
        parentNode.addChild( scoreboardNode );

        // face, for indicating correct/incorrect answers
        faceNode = new FaceNode();
        parentNode.addChild( faceNode );

        // buttons, all under the same parent, to facilitate moving between Before & After boxes
        buttonsParentNode = new PhetPNode();
        parentNode.addChild( buttonsParentNode );
        checkButton = new GradientButtonNode( RPALStrings.BUTTON_CHECK, 20, Color.YELLOW );
        buttonsParentNode.addChild( checkButton );
        nextButton = new GradientButtonNode( RPALStrings.BUTTON_NEXT, 20, Color.YELLOW );
        buttonsParentNode.addChild( nextButton );
        tryAgainButton = new GradientButtonNode( RPALStrings.BUTTON_TRY_AGAIN, 20, Color.YELLOW );
        buttonsParentNode.addChild( tryAgainButton );
        showAnswerButton = new GradientButtonNode( RPALStrings.BUTTON_SHOW_ANSWER, 20, Color.YELLOW );
        buttonsParentNode.addChild( showAnswerButton );

        // instructions
        instructionsNode = new GameInstructionsNode( "?" ); // text will be set based on challenge type
        parentNode.addChild( instructionsNode );

        // dev nodes
        devBeforeValuesNode = new DevBeforeValuesNode( model );
        devAfterValuesNode = new DevAfterValuesNode( model );
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            parentNode.addChild( devBeforeValuesNode );
            parentNode.addChild( devAfterValuesNode );
        }

        this.model = model;
        model.addGameListener( new GameAdapter() {

            @Override
            public void newGame() {
                handleNewGame();
            }

            @Override
            public void gameStarted() {
                handleGameStarted();
            }

            @Override
            public void gameCompleted() {
                handleGameCompleted();
            }

            @Override
            public void gameAborted() {
                handleGameAborted();
            }

            @Override
            public void challengeChanged() {
                handleChallengeChanged();
            }

        } );

        // when any button's visibility changes, update the layout of the buttons
        PropertyChangeListener buttonVisibilityListener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName() == PNode.PROPERTY_VISIBLE ) {
                    updateButtonsLayout();
                }
            }
        };
        
        // Check button checks the user's solution
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                checkButtonPressed();
            }
        } );
        checkButton.addPropertyChangeListener( buttonVisibilityListener );

        // Next button advanced to the next challenge
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                nextButtonPressed();
            }
        } );
        nextButton.addPropertyChangeListener( buttonVisibilityListener );

        // Try Again button lets the user make another attempt
        tryAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                tryAgainButtonPressed();
            }
        } );
        tryAgainButton.addPropertyChangeListener( buttonVisibilityListener );

        // Show Answer button shows the answer
        showAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showAnswerButtonPressed();
            }
        } );
        showAnswerButton.addPropertyChangeListener( buttonVisibilityListener );
        
        // visibility management
        PNode[] allNodes = { gameSettingsNode, gameSummaryNode, parentNode, checkButton, nextButton, tryAgainButton, showAnswerButton, faceNode, instructionsNode };
        visibilityManager = new NodeVisibilityManager( allNodes );
        initVisibilityManager();
        
        // initial state
        updateNodes();
        updateButtonsLayout();
        visibilityManager.setVisibility( GAME_SETTINGS_STATE );
    }
    
    private void initVisibilityManager() {
        visibilityManager.add( GAME_SETTINGS_STATE, gameSettingsNode );
        visibilityManager.add( FIRST_ATTEMPT_STATE, parentNode, checkButton, instructionsNode );
        visibilityManager.add( FIRST_ATTEMPT_CORRECT_STATE, parentNode, nextButton, faceNode );
        visibilityManager.add( FIRST_ATTEMPT_WRONG_STATE, parentNode, tryAgainButton, faceNode );
        visibilityManager.add( SECOND_ATTEMPT_STATE, parentNode, checkButton, instructionsNode );
        visibilityManager.add( SECOND_ATTEMPT_CORRECT_STATE, parentNode, nextButton, faceNode );
        visibilityManager.add( SECOND_ATTEMPT_WRONG_STATE, parentNode, nextButton, showAnswerButton, faceNode );
        visibilityManager.add( ANSWER_SHOWN_STATE, parentNode, nextButton );
        visibilityManager.add( GAME_SUMMARY_STATE, gameSummaryNode, parentNode );
    }
    
    private void handleNewGame() {
        visibilityManager.setVisibility( GAME_SETTINGS_STATE );
    }
    
    private void handleGameStarted() {
        visibilityManager.setVisibility( FIRST_ATTEMPT_STATE );
    }
    
    private void handleGameCompleted() {
        visibilityManager.setVisibility( GAME_SUMMARY_STATE );
    }

    private void handleGameAborted() {
        gameSettingsNode.setVisible( true );
        gameSummaryNode.setVisible( false );
        parentNode.setVisible( false );
    }
    
    // When the challenge changes, rebuild dynamic nodes.
    private void handleChallengeChanged() {
        visibilityManager.setVisibility( FIRST_ATTEMPT_STATE );
        updateNodes();
    }
    
    private void checkButtonPressed() {
        boolean correct = model.checkAnswer();
        if ( correct ) {
            faceNode.smile();
            if ( model.getAttempts() == 1 ) {
                visibilityManager.setVisibility( FIRST_ATTEMPT_CORRECT_STATE );
            }
            else {
                visibilityManager.setVisibility( SECOND_ATTEMPT_CORRECT_STATE );
            }
        }
        else {
            faceNode.frown();
            if ( model.getAttempts() == 1 ) {
                visibilityManager.setVisibility( FIRST_ATTEMPT_WRONG_STATE );
            }
            else {
                visibilityManager.setVisibility( SECOND_ATTEMPT_WRONG_STATE );
            }
        }
    }
    
    private void nextButtonPressed() {
        model.nextChallenge();
    }
    
    private void tryAgainButtonPressed() {
        visibilityManager.setVisibility( SECOND_ATTEMPT_STATE );
    }
    
    private void showAnswerButtonPressed() {
        visibilityManager.setVisibility( ANSWER_SHOWN_STATE );
    }
    
    private void updateNodes() {

        //XXX call cleanup on these dynamic nodes so we don't have memory leaks

        parentNode.removeChild( equationNode );
        equationNode = new RealReactionEquationNode( model.getReaction() );
        parentNode.addChild( equationNode );

        if ( beforeNode != null ) {
            beforeNode.cleanup();
        }
        parentNode.removeChild( beforeNode );
        beforeNode = new GameBeforeNode( model );
        parentNode.addChild( beforeNode );

        if ( afterNode != null ) {
            afterNode.cleanup();
        }
        parentNode.removeChild( afterNode );
        afterNode = new GameAfterNode( model );
        parentNode.addChild( afterNode );

        // move a bunch of static nodes to the front
        devBeforeValuesNode.moveToFront();
        devAfterValuesNode.moveToFront();
        buttonsParentNode.moveToFront();
        faceNode.moveToFront();
        instructionsNode.moveToFront();

        updateNodesLayout();
    }

    private void updateNodesLayout() {

        double x = 0;
        double y = 0;

        // reaction number label in upper right
        reactionNumberLabelNode.setOffset( x, y );

        // equation to right of label, vertically centered
        x = reactionNumberLabelNode.getFullBoundsReference().getWidth() + 35;
        y = reactionNumberLabelNode.getYOffset();
        equationNode.setOffset( x, y );

        // Before box below reaction number label, left justified
        x = reactionNumberLabelNode.getFullBoundsReference().getMinX();
        y = reactionNumberLabelNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beforeNode ) + 30;
        beforeNode.setOffset( x, y );

        // arrow to the right of Before box, vertically centered with box
        final double arrowXSpacing = 20;
        x = beforeNode.getFullBoundsReference().getMaxX() + arrowXSpacing;
        y = beforeNode.getYOffset() + ( beforeNode.getBoxHeight() / 2 );
        arrowNode.setOffset( x, y );

        // After box to the right of arrow, top aligned with Before box
        x = arrowNode.getFullBoundsReference().getMaxX() + arrowXSpacing;
        y = beforeNode.getYOffset();
        afterNode.setOffset( x, y );

        // scoreboard, at bottom center of play area
        x = arrowNode.getFullBoundsReference().getCenterX() - ( scoreboardNode.getFullBoundsReference().getWidth() / 2 );
        y = Math.max( beforeNode.getFullBoundsReference().getMaxY(), afterNode.getFullBoundsReference().getMaxY() ) + 10;
        scoreboardNode.setOffset( x, y );

        // face centered in proper box
        {
            GameBoxNode boxNode = null;
            if ( model.getChallengeType() == ChallengeType.HOW_MANY_PRODUCTS_AND_LEFTOVERS ) {
                boxNode = afterNode;
            }
            else {
                boxNode = beforeNode;
            }
            x = boxNode.getXOffset() + ( ( boxNode.getBoxWidth() - faceNode.getFullBoundsReference().getWidth() ) / 2 );
            y = boxNode.getYOffset() + ( ( boxNode.getBoxHeight() - faceNode.getFullBoundsReference().getHeight() ) / 2 );
            faceNode.setOffset( x, y );
        }
       
        // instructions centered in proper box
        {
            GameBoxNode boxNode = null;
            if ( model.getChallengeType() == ChallengeType.HOW_MANY_PRODUCTS_AND_LEFTOVERS ) {
                instructionsNode.setText( RPALStrings.QUESTION_HOW_MANY_PRODUCTS_AND_LEFTOVERS );
                boxNode = afterNode;
            }
            else {
                instructionsNode.setText( RPALStrings.QUESTION_HOW_MANY_REACTANTS );
                boxNode = beforeNode;
            }
            x = boxNode.getXOffset() + ( ( boxNode.getBoxWidth() - instructionsNode.getFullBoundsReference().getWidth() ) / 2 );
            y = boxNode.getYOffset() + ( ( boxNode.getBoxHeight() - instructionsNode.getFullBoundsReference().getHeight() ) / 2 );
            instructionsNode.setOffset( x, y );
        }

        // dev values in upper-left of Before box
        x = beforeNode.getXOffset() + 10;
        y = beforeNode.getYOffset() + 10;
        devBeforeValuesNode.setOffset( x, y );

        // dev values in upper-left of After box
        x = afterNode.getXOffset() + 10;
        y = afterNode.getYOffset() + 10;
        devAfterValuesNode.setOffset( x, y );

        // game summmary, horizontally and vertically centered on everything else
        x = parentNode.getFullBoundsReference().getCenterX() - ( gameSummaryNode.getFullBoundsReference().getWidth() / 2 );
        y = parentNode.getFullBoundsReference().getCenterY() - ( gameSummaryNode.getFullBoundsReference().getHeight() / 2 );
        gameSummaryNode.setOffset( x, y );

        // game settings, horizontally and vertically centered on everything else
        x = parentNode.getFullBoundsReference().getCenterX() - ( gameSettingsNode.getFullBoundsReference().getWidth() / 2 );
        y = parentNode.getFullBoundsReference().getCenterY() - ( gameSettingsNode.getFullBoundsReference().getHeight() / 2 );
        gameSettingsNode.setOffset( x, y );
    }
    
    private void updateButtonsLayout() {
        
        // arrange all visible buttons in a row
        double x = 0;
        double y = 0;
        double buttonMaxX = 0;
        double buttonMaxY = 0;
        for ( int i = 0; i < buttonsParentNode.getChildrenCount(); i++ ) {
            PNode child = buttonsParentNode.getChild( i );
            if ( child.getVisible() ) {
                child.setOffset( x, y );
                x += child.getFullBoundsReference().getWidth() + BUTTON_X_SPACING;
                buttonMaxX = child.getFullBoundsReference().getMaxX();
                buttonMaxY = child.getFullBoundsReference().getMaxY();
            }
        }
        
        // put visible buttons at bottom center of the proper box
        GameBoxNode boxNode = null;
        if ( model.getChallengeType() == ChallengeType.HOW_MANY_PRODUCTS_AND_LEFTOVERS ) {
            boxNode = afterNode;
        }
        else {
            boxNode = beforeNode;
        }
        x = boxNode.getXOffset() + ( ( boxNode.getBoxWidth() - buttonMaxX ) / 2 );
        y = boxNode.getYOffset() + boxNode.getBoxHeight() - buttonMaxY - 10;
        buttonsParentNode.setOffset( x, y );
    }

    /*
     * Centers the root node on the canvas when the canvas size changes.
     */
    @Override
    protected void updateLayout() {
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
            centerRootNode();
        }
    }
}

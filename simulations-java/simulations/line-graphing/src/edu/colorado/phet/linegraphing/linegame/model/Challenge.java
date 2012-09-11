// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.view.SI_EG_Intercept_ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.SI_EG_Points_ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.SI_EG_SlopeIntercept_ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.SI_EG_Slope_ChallengeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A game challenge where the user is trying to match some "given" line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Challenge {

    public final Line answer; // the correct answer
    public final Property<Line> guess; // the user's current guess
    public final ModelViewTransform mvt; // transform between model and view coordinate frames

    public Challenge( Line answer, Line guess, ModelViewTransform mvt ) {
        this.answer = answer;
        this.guess = new Property<Line>( guess );
        this.mvt = mvt;
    }

    // Correct if the guess and answer are descriptions of the same line.
    public boolean isCorrect() {
        return answer.same( guess.get() );
    }

    // Creates the view component for the challenge.
    public abstract PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize );

}

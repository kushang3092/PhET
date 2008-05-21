package edu.colorado.phet.fitness.control;

import edu.colorado.phet.fitness.FitnessResources;

/**
 * Created by: Sam
 * Apr 24, 2008 at 2:43:05 AM
 */
public class Activity {
    private String name;
    private double activityLevel;
    public static final Activity DEFAULT_ACTIVITY_LEVEL = new Activity( FitnessResources.getString( "moderate.activity" ), 1.5 );

    public static final Activity[] DEFAULT_ACTIVITY_LEVELS = {
            new Activity( FitnessResources.getString( "very.sedentary" ), 1.3 ),
            new Activity( FitnessResources.getString( "sedentary" ), 1.4 ),
            DEFAULT_ACTIVITY_LEVEL,
            new Activity( FitnessResources.getString( "very.active" ), 1.6 ),
            new Activity( FitnessResources.getString( "athletic.lifestyle" ), 1.7 )
    };


    public Activity( String name, double activityLevel ) {
        this.name = name;
        this.activityLevel = activityLevel;
    }

    public String toString() {
        return name;
    }

    public double getValue() {
        return activityLevel - 1;
    }
}

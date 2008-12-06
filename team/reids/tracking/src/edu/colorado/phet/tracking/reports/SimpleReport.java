package edu.colorado.phet.tracking.reports;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

public class SimpleReport {
    private ArrayList lines = new ArrayList();

    public static void main( String[] args ) {
        String text = args[0];

        StringTokenizer st = new StringTokenizer( text, "\n" );
        SimpleReport report = new SimpleReport();
        while ( st.hasMoreTokens() ) {
            String line = st.nextToken();

            report.addLine( line );

            if ( line.indexOf( "3.16" ) >= 0 && line.indexOf( "circuit" ) >= 0 && line.indexOf( "started" ) >= 0 ) {
                String sub = line.substring( 0, line.indexOf( "session-id" ) );
                String user_timezone = getValue( line, "user_timezone" );
                System.out.println( "CCK started at: " + sub + " in " + user_timezone +" by: "+getValue( line, "preferences-file-creation-time_milliseconds"));
//                System.out.println( line );
            }
        }

        report.report( "preferences-file-creation-time_milliseconds" );
        report.report( "session-id" );
        report.report( "user_timezone" );

        report.report( "locale-language" );
        report.report( "os_name" );
        report.report( "java_version" );

    }

    public static String getValue( String line, String key ) {
        StringTokenizer st = new StringTokenizer( line, "&" );
        while ( st.hasMoreTokens() ) {
            String a = st.nextToken();
            if ( a.startsWith( key ) ) {
                return a;
            }
        }
        return null;
    }

    private void report( String s ) {
        HashSet t = getValues( s );
        System.out.println( s + " (" + t.size() + " unique): " + t );
    }

    private void addLine( String line ) {
        lines.add( line );
    }

    private HashSet getValues( String key ) {
        HashSet timeZones = new HashSet();
        for ( int i = 0; i < lines.size(); i++ ) {
            String line = (String) lines.get( i );
            int start = line.indexOf( "session-id" );
//            String startTime = line.substring( 0, start );
            String suffix = line.substring( start );
//            System.out.println( "startTime = " + startTime );

            StringTokenizer st = new StringTokenizer( suffix, "&" );


            while ( st.hasMoreTokens() ) {
                String t = st.nextToken();
//                System.out.println( "t = " + t );
                if ( t.startsWith( key + "=" ) ) {
                    timeZones.add( t.substring( ( key + "=" ).length() ) );
                }

            }

        }
        return timeZones;
    }
}

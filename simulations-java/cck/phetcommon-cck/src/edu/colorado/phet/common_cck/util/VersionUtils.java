/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common_cck.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 15, 2003
 * Time: 2:53:11 AM
 * Copyright (c) Sep 15, 2003 by Sam Reid
 */
public class VersionUtils {
    public static class VersionInfo {
        int buildNumber;
        String buildTime;
        String name;

        public VersionInfo( String name, int buildNumber, String buildTime ) {
            this.name = name;
            this.buildNumber = buildNumber;
            this.buildTime = buildTime;
        }

        public int getBuildNumber() {
            return buildNumber;
        }

        public String getBuildTime() {
            return buildTime;
        }

//        public String toString() {
//            return "Name = " + name + ", Build Number = " + buildNumber + ", Build Time = " + buildTime;
//        }

        public String toString() {
            return name + " #" + buildNumber + ": " + buildTime;
        }
    }

    /* To be used in conjuction with this ant code:

    <property name="build.number.txt" value="${data}/${distname}.build.number"/>
    <property name="timestamp.name" value="${data}/${distname}.build.time.stamp"/>

    <target name="Generate Build Info">
        <buildnumber file="${build.number.txt}"/>
        <tstamp>
            <format property="jar.creation.time" pattern="d-MMMM-yyyy h:mm aa" locale="en" timezone="MST"></format>
        </tstamp>
        <echo file="${timestamp.name}" message="${jar.creation.time}"></echo>
    </target>

    */

    public static VersionInfo[] readVersionInfo( String name ) throws IOException {
        if( name == null ) {
//            new RuntimeException( "Cannot read version info for name=" + name ).printStackTrace( );
            return new VersionInfo[0];
        }
        VersionUtils vu = new VersionUtils();
        ClassLoader cl = vu.getClass().getClassLoader();


        ArrayList vall = new ArrayList();
        VersionInfo rootInfo = readVersionInfo( name, cl );
        if( rootInfo != null ) {
            vall.add( rootInfo );
        }
        URL resourceList = cl.getResource( name + ".resources" );
        if( resourceList == null ) {
            return new VersionInfo[0];
        }
        BufferedReader br = new BufferedReader( new InputStreamReader( resourceList.openStream() ) );
        String line = br.readLine();
        if( line != null ) {
            line = line.trim();
        }
        while( line != null ) {
            if( line.trim().startsWith( "#" ) ) {
                //ignore.
            }
            else {
                VersionInfo vi = readVersionInfo( line, cl );
                vall.add( vi );
            }
            line = br.readLine();
            if( line != null ) {
                line = line.trim();
            }
        }

        return (VersionInfo[])vall.toArray( new VersionInfo[0] );
    }

    public static VersionInfo readVersionInfo( String name, ClassLoader cl ) throws IOException {
        String buildnumberName = name + ".build.number";
        URL buildNumberURL = cl.getResource( buildnumberName );
        if( buildNumberURL == null ) {
            return new VersionInfo( name, 0, "" );
//            throw new IOException( "No resource found: " + buildnumberName );
        }
//        System.out.println( "loading resource info=" + name + ", BuildURL = " + buildNumberURL );
        int buildNum = -1;
        BufferedReader br = new BufferedReader( new InputStreamReader( buildNumberURL.openStream() ) );
        String line = br.readLine();
        while( line != null ) {
            if( line.toLowerCase().startsWith( "build.number=" ) ) {
                String number = line.substring( "build.number=".length() );
                buildNum = Integer.parseInt( number );
            }
            line = br.readLine();
        }

        InputStream buildTimeURL = cl.getResourceAsStream( name + ".build.time.stamp" );
        String buildTimeStr = "-1";
        buildTimeStr = new BufferedReader( new InputStreamReader( buildTimeURL ) ).readLine();

        VersionInfo vi = new VersionInfo( name, buildNum, buildTimeStr );
//        System.out.println( "resource info loaded: " + vi );
        return vi;
    }
}

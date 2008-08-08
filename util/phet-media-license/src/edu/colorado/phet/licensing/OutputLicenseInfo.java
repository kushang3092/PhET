package edu.colorado.phet.licensing;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.build.PhetProject;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class OutputLicenseInfo extends ProcessData {

    protected void visitFile( PhetProject phetProject, ResourceAnnotationList resourceAnnotationList, File file ) {
        ResourceAnnotation entry = resourceAnnotationList.getEntry( file.getName() );
        if ( !hideEntry( entry ) ) {
            System.out.println( entry.toText() );
        }
    }

    private boolean hideEntry( ResourceAnnotation entry ) {
        return entry.getAuthor() != null && entry.getAuthor().equalsIgnoreCase( "phet" )
               ||
               entry.getSource() != null && entry.getSource().toLowerCase().startsWith( "microsoft" )
               ||
               entry.getLicense() != null && entry.getLicense().equalsIgnoreCase( "PUBLIC DOMAIN" );
    }

    public static void main( String[] args ) throws IOException {
        new OutputLicenseInfo().start();
    }

}
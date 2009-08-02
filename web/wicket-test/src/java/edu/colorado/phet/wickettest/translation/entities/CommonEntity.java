package edu.colorado.phet.wickettest.translation.entities;

public class CommonEntity extends TranslationEntity {
    public CommonEntity() {
        addString( "language.dir", "This should be either 'ltr' for left-to-right languages, or 'rtl' for right-to-left languages (both without the quotes)." );
    }

    public String getDisplayName() {
        return "Common";
    }
}

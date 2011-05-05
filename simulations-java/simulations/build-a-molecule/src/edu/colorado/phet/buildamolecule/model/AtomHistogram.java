package edu.colorado.phet.buildamolecule.model;

import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.molecule.MoleculeSDFCombinedParser;

/**
 * Count of each atom type, and allows fast comparison
 */
public class AtomHistogram {
    public final Map<String, Integer> quantities = new HashMap<String, Integer>() {{
        for ( String symbol : MoleculeSDFCombinedParser.ALLOWED_CHEMICAL_SYMBOLS ) {
            put( symbol, 0 );
        }
    }};

    public AtomHistogram() {
    }

    public AtomHistogram( MoleculeStructure molecule ) {
        add( molecule );
    }

    public void add( Atom atom ) {
        quantities.put( atom.getSymbol(), quantities.get( atom.getSymbol() ) + 1 );
    }

    public void add( MoleculeStructure molecule ) {
        for ( Atom atom : molecule.getAtoms() ) {
            add( atom );
        }
    }

    public boolean containsAsSubset( AtomHistogram otherHistogram ) {
        for ( String symbol : MoleculeSDFCombinedParser.ALLOWED_CHEMICAL_SYMBOLS ) {
            if ( quantities.get( symbol ) < otherHistogram.quantities.get( symbol ) ) {
                return false;
            }
        }
        return true;
    }

    public String getHashString() {
        String hashString = "";
        for ( String symbol : MoleculeSDFCombinedParser.ALLOWED_CHEMICAL_SYMBOLS ) {
            hashString += quantities.get( symbol );
        }
        return hashString;
    }

    @Override
    public int hashCode() {
        return getHashString().hashCode();
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof AtomHistogram ) {
            AtomHistogram otherHistogram = (AtomHistogram) obj;
            for ( String symbol : MoleculeSDFCombinedParser.ALLOWED_CHEMICAL_SYMBOLS ) {
                if ( !quantities.get( symbol ).equals( otherHistogram.quantities.get( symbol ) ) ) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
}

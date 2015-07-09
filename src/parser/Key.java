package parser;

import sound.Pitch;

/**
 * Represents the key signature of a musical piece.
 */
public class Key {
    /**
     * Major or minor scale.
    */
    public enum Scale {
        Major,
        Minor
    }
    
    /**
     * Allowed accidentals on a key signaure.
     */
    public enum Accidental {
        Normal,
        Sharp,
        Flat
    }

    // The base pitch
    private char pitch;
    private Scale scale;
    private Accidental accidental;

    /**
     * Constructs a normal major scale key based on the given note.
     * @param p The note/pitch. Must be A, B, C, D, E, F, or G.
     */
    public Key(char p) {
        pitch = p;
        scale = Scale.Major;
        accidental = Accidental.Normal;
    }

    /**
     * Constructs a key.
     * @param p The note/pitch. Must be A, B, C, D, E, F, or G.
     * @param s The scale, major or minor.
     * @param a The accidental to apply to the the note p.
     */
    public Key(char p, Scale s, Accidental a) {
        pitch = p;
        scale = s;
        accidental = a;
    }

    public char getPitch() {
        return pitch;
    }

    public Scale getScale() {
        return scale;
    }

    public Accidental getAccidental() {
        return accidental;
    }
        
    /**
     * Convert current key to one of the fourteen unique keys.
     * These keys are C, G, D, A, E, B, Gb, Db, Ab, Eb, Bb and F.
     * @return A key from the above list which is equivalent to this key.
     */
    public Key equivalentKey() {
        //Converts key to one of the 12 unique keys based on docs/KeyRule.txt
    	Key C = new Key('C', Scale.Major, Accidental.Normal);
    	Key G = new Key('G', Scale.Major, Accidental.Normal);
    	Key D = new Key('D', Scale.Major, Accidental.Normal);
    	Key A = new Key('A', Scale.Major, Accidental.Normal);
    	Key E = new Key('E', Scale.Major, Accidental.Normal);
    	Key B = new Key('B', Scale.Major, Accidental.Normal);
    	
    	Key CSharp = new Key('C', Scale.Major, Accidental.Sharp);
    	
    	Key Cflat = new Key('C', Scale.Major, Accidental.Flat);
    	Key Gflat = new Key('G', Scale.Major, Accidental.Flat);
    	Key Dflat = new Key('D', Scale.Major, Accidental.Flat);
    	Key Aflat = new Key('A', Scale.Major, Accidental.Flat);
    	Key Eflat = new Key('E', Scale.Major, Accidental.Flat);
    	Key Bflat = new Key('B', Scale.Major, Accidental.Flat);
    	
    	Key F = new Key('F', Scale.Major, Accidental.Normal);
    	
    	if (this.scale == Scale.Minor){
    		if (this.pitch == 'A' && this.accidental == Accidental.Normal){ // C == a minor
    			return C;
    		} else if (this.pitch == 'E' && this.accidental == Accidental.Normal){ //G == e minor:
    			return G;
    		} else if (this.pitch == 'B' && this.accidental == Accidental.Normal){ // D == b minor:
    			return D;
    		} else if (this.pitch == 'F' && this.accidental == Accidental.Sharp){ // A == f# minor
    			return A;
    		} else if (this.pitch == 'C' && this.accidental == Accidental.Sharp){ //E == c# minor:
    			return E;
    		} else if (this.pitch == 'G' && this.accidental == Accidental.Sharp){ //B == g# minor
    			return B;
    		} else if (this.pitch == 'E' && this.accidental == Accidental.Flat){ //G_ == e_ minor == d# minor:
    			return Gflat;
    		} else if (this.pitch == 'D' && this.accidental == Accidental.Sharp){ //G_ == e_ minor == d# minor:
    			return Gflat;
    		} else if (this.pitch == 'B' && this.accidental == Accidental.Flat){ // D_ = b_ minor
    			return Dflat;
    		} else if (this.pitch == 'F' && this.accidental == Accidental.Normal){ // A_ == f minor:
    			return Aflat;
    		} else if (this.pitch == 'C' && this.accidental == Accidental.Normal){ // E_ == c minor:
    			return Eflat;
    		} else if (this.pitch == 'G' && this.accidental == Accidental.Normal){ // B_ == g minor
    			return Bflat;
    		} else if (this.pitch == 'D' && this.accidental == Accidental.Normal) { // F == d minor:
    			return F;
    		} else if (this.pitch == 'A' && this.accidental == Accidental.Sharp) { // a#m: ==C# major
    			return CSharp;
    			
    			//a#m: a sharp minor: 7 sharps == C# major: 7 sharps
       		} else if (this.pitch == 'A' && this.accidental == Accidental.Flat) { //a_m == C_ major
    			return Cflat;	
    			//a_m: a flat minor: 7 flats == C_ major : 7 flats
    		} else {
    			//System.out.println("Invalid minor key");
    			return this;
    		}
    		
    		
    		
    		
    	} else {// these are all majors
    		// if it is F# major
    		if (this.pitch == 'F' && this.accidental == Accidental.Sharp){ //
    			return Gflat;
    		} else {
    			//System.out.println("Same key");
    			return this;
    		}
    	}
    }
    
    /////////////////////////////
    // The unique major scales //
    /////////////////////////////
    public static final Key AMajor = new Key('A');
    public static final Key BMajor = new Key('B');
    public static final Key CMajor = new Key('C');
    public static final Key DMajor = new Key('D');
    public static final Key EMajor = new Key('E');
    public static final Key FMajor = new Key('F');
    public static final Key GMajor = new Key('G');

    public static final Key AFlatMajor = new Key('A', Scale.Major, Accidental.Flat);
    public static final Key BFlatMajor = new Key('B', Scale.Major, Accidental.Flat);
    public static final Key CFlatMajor = new Key('B', Scale.Major, Accidental.Flat);
    public static final Key DFlatMajor = new Key('D', Scale.Major, Accidental.Flat);
    public static final Key EFlatMajor = new Key('E', Scale.Major, Accidental.Flat);
    public static final Key GFlatMajor = new Key('G', Scale.Major, Accidental.Flat);

    public static final Key CSharpMajor = new Key('G', Scale.Major, Accidental.Sharp);

    @Override
    public String toString() {
        String accidentalString = "";
        if (accidental == Accidental.Sharp) {
            accidentalString = "#";
        } else if (accidental == Accidental.Flat) {
            accidentalString = "\u266d";
        }
        return String.format("%s%s %s", pitch, accidentalString, scale);
    }
    
    @Override
    public int hashCode() {
        Key key = this.equivalentKey();
        final int prime = 7;
        int result = 1;
        result = prime * result + key.pitch;
        result = prime * result + key.accidental.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Key) {
            Key a = this.equivalentKey();
            Key b = (((Key)other)).equivalentKey();
            return a.pitch == b.pitch
                && a.accidental.equals(b.accidental);
        }
        return false;
    }
}

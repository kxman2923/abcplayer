package lexer;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Represents the smallest unit of text that has meaning in an ABC file.
 * Tokens without values are tokens such as BeginChord or Flat.
 * Tokens with values are tokens that have additional information, like the pitch of a BaseNote, or the composer in a ComposerField.
 */
public class Token {
    public static enum Type {
        NumberField,
        TitleField,
        ComposerField,
        DefaultLengthField,
        MeterField,
        TempoField,
        VoiceField,
        KeyField,
        
        BaseNote,
        NoteLength,

        Sharp,
        DoubleSharp,
        Flat,
        DoubleFlat,
        Natural,
        Rest,
        
        OctaveUp,
        OctaveDown,
        
        BeginDoublet,
        BeginTriplet,
        BeginQuadruplet,

        BeginChord,
        EndChord,
        
        BeginMajorSection,
        EndMajorSection,

        BeginRepeat,
        EndRepeat,
        
        FirstRepeat,
        SecondRepeat,
        
        BarLine
    }

    public final Type type;
    public final String value;

    /**
     * Constructs a value-less token.
     */
    public Token(Type type) {
        this.type = type;
        this.value = ""; 
        checkRep();
    }

    /**
     * Constructs a token with a value.
     */
    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
        checkRep();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Token) {
            Token otherToken = (Token)other;
            return (this.type == otherToken.type) && otherToken.value.equals(this.value);
        }
        return false;
    }

    @Override
    public String toString() {
        if (this.value == "") {
            return this.type.toString();
        }
        return String.format("%s: %s", this.type, this.value);
    }

    // Makes sure that the token type is internally consistent
    private void checkRep() {
        if (this.value == "") {
            assert !typesWithValues.contains(this.type);
        } else {
            assert typesWithValues.contains(this.type);
        }
    }

    private static Type[] typesWithValuesList = {
                    Type.NumberField,
                    Type.TitleField,
                    Type.ComposerField,
                    Type.DefaultLengthField,
                    Type.MeterField,
                    Type.TempoField,
                    Type.VoiceField,
                    Type.KeyField,
                    
                    Type.BaseNote,
                    Type.NoteLength
    };

    // Keeps track of which token types should have a value associated with it.
    private static Set<Type> typesWithValues
        = new HashSet<Type>(Arrays.asList(typesWithValuesList));
    
    
}

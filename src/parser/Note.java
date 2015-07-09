package parser;

import player.Visitor;

/**
 * Represents the smallest element of music, a note or a rest.
 */
public class Note extends Node {
    public enum AccidentalType {
        Normal,
        Natural,
        Sharp,
        DoubleSharp,
        Flat,
        DoubleFlat
    }

    // A-G
    private final char pitch;
    // The number of octaves up or down the note is transposed.
    private final int octave;
    // The accidental applied to the note, or Normal.
    private final AccidentalType accidental;
    // The duration of the note / rest.
    private final Duration duration;
    // Whether or not the note is a rest.
    private final boolean isRest;

    /**
     * Constructs a Note with a Normal accidental and no transposing.
     * @param pitch The note's pitch, from A-G.
     * @param dur The duration of the note, relative to a whole note.
     */
    public Note (char pitch, Duration dur) {
        this.pitch=pitch;
        this.duration=new Duration(dur.getNumerator(), dur.getDenominator());
        this.accidental= AccidentalType.Normal;
        this.isRest=false;
        this.octave =0;
    }
    
    /**
     * Constructs a Note.
     * @param pitch The note's pitch, from A-G.
     * @param dur The duration of the note, relative to a whole note.
     * @param oct The number of octaves the note should be tranposed.
     * @param acc The accidental to apply to the note.
     * @param rest Whether or not the note is a rest.
     */
    public Note(char pitch, Duration dur, int oct, AccidentalType acc, boolean rest){
    	this.pitch=pitch;
        this.duration=new Duration(dur.getNumerator(), dur.getDenominator());
        this.accidental= acc;
        this.isRest=rest;
        this.octave =oct;
    }

    /**
     * The note's pitch.
     */
    public char getPitch() {
        return pitch;
    }

    /**
     * The number of octaves up/down the note is transposed.
     */
    public int getOctave() {
        return octave;
    }

    /**
     * The accidental applied to the note.
     */
    public AccidentalType getAccidental() {
        return accidental;
    }

    /**
     * The duration of the note, relative to a whole note.
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Whether or not the note is a rest.
     */
    public boolean isRest() {
        return isRest;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Note) {
            Note otherNote = (Note) other;
            if (isRest) {
                return otherNote.isRest
                    && this.duration.equals(otherNote.duration);
            } else {
                return this.pitch == otherNote.pitch
                    && this.octave == otherNote.octave
                    && this.accidental == otherNote.accidental
                    && this.duration.equals(otherNote.duration)
                    && !otherNote.isRest;
            }
        }
        return false;
    }
    
    @Override
    public String toString(){
    	if(isRest){
            return "Rest " + duration.toString();
    	}
        String accidentalString = "";
        switch (accidental) {
        case Natural:
            accidentalString = "=";
            break;
        case Sharp:
            accidentalString = "#";
            break;
        case DoubleSharp:
            accidentalString = "##";
            break;
        case Flat:
            accidentalString = "\u266d";
            break;
        case DoubleFlat:
            accidentalString = "\u266d\u266d";
            break;
        }
        String octaveString = "";
        if (octave != 0) {
            octaveString = String.format("%+d", octave);
        }
        return String.format("%s%s%s %s", accidentalString, pitch, octaveString, duration);
    }
}

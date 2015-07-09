package player;

import sound.Pitch;
import sound.SequencePlayer;

import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;

import parser.Chord;
import parser.Duration;
import parser.Key;
import parser.Measure;
import parser.Note;
import parser.Piece;
import parser.Tuplet;
import parser.Key.Accidental;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

/**
 * This class takes a Piece object and converts it into a sequence of MIDI notes.
 */
public class Player implements Visitor<Void> {
    // Allows for 1/144th note as shortest possible note.
    final static int TICS_PER_QUARTER_NOTE = 48;
    final static int TICS_PER_WHOLE_NOTE = 4*TICS_PER_QUARTER_NOTE;

    /**
     * Constructs a Player and converts the given piece to a MIDI sequence.
     * @param piece the Piece to sequence. Piece must be well-formed.
     */
    public Player(Piece piece) {
        piece.accept(this);
    }

    /**
     * Plays the sequenced piece.
     */
    public void play() throws MidiUnavailableException {
        sequencePlayer.play();
    }

    /**
     * Sequences the piece.
     */
    public Void visit(Piece p) {
        this.piece = p;
        positions = new HashMap<String, Integer>();
        // Start off each voice's track at 0
        for (String voice : p.getVoices()) {
            positions.put(voice, 0);
        }
        if (positions.isEmpty()) {
            singleVoice = true;
            positions.put(DEFAULT_VOICE, 0);
        } else {
            singleVoice = false;
            // Assume current voice is first voice.
            currentVoice = p.getVoices().get(0);
        }
        // Set of default accidentals for the givne key signature
        currentDefaultAccidentals = defaultAccidentalsForKey.get(p.getKey());
        // Initialize sequence player with tempo from piece, and TICS_PER_QUARTER_NOTE
        try {
            // Tempo in ABC file is specified as # of default notes per minute
            // Here, we convert to # of quarter notes per minute
            int tempo = 4 * p.getTempo() * p.getDefaultLength().getNumerator() / p.getDefaultLength().getDenominator();
            sequencePlayer = new SequencePlayer(tempo, TICS_PER_QUARTER_NOTE);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Malformed Piece object provided!.");
        } catch (MidiUnavailableException e) {
            throw new RuntimeException("MIDI unavailable.");
        }
        for (Measure measure : p.getMeasures()) {
            measure.accept(this);
        }
        return null;
    }

    /**
     * Sequences the measure.
     */
    public Void visit(Measure m) {
        // Reset accidentals because we're in new measure
        currentAccidentals = new HashMap<Pair<Character, Integer>, Note.AccidentalType>();
        if (singleVoice) {
            currentVoice = DEFAULT_VOICE;
        } else {
            String newVoice = m.getVoice();
            if (!newVoice.isEmpty()) {
                currentVoice = newVoice;
            }
        }
        for (Tuplet tuplet : m.getElements()) {
            tuplet.accept(this);
        }
        return null;
    }

    /**
     * Sequences the tuplet.
     */
    public Void visit(Tuplet t) {
        for (Chord chord : t.getChords()) {
            chord.accept(this);
        }
        return null;
    }

    /**
     * Sequences the chord.
     */
    public Void visit(Chord c) {
        // Get the current position for the current voice
        int currentPos = positions.get(currentVoice);
        int tics = 0;
        for (Note note : c.getNotes()) {
            Duration duration = note.getDuration();
            // Calculate the number of MIDI tics for the note
            tics = (TICS_PER_WHOLE_NOTE * duration.getNumerator()) / duration.getDenominator();
            if (!note.isRest()) {
                // If the current note has an accidental on it, update currentAccidentals
                if (note.getAccidental() != Note.AccidentalType.Normal) {
                    currentAccidentals.put(makePair(note.getPitch(), note.getOctave()), note.getAccidental());
                }
                // Add it to the sequencer
                sequencePlayer.addNote(noteToMidi(note), currentPos, tics);
            }
        }
        // Advance the position of the current voice's track
        positions.put(currentVoice, currentPos + tics);
        return null;
    }

    /**
     * This function should not be called, sequencing of Notes is handled inside visit(Chord c).
     */
    public Void visit(Note n) {
        throw new RuntimeException("This should not be called.");
    }

    /**
     * Takes a Note object and returns the corresponding MIDI note.
     * This function takes into account the piece's key, and any accidentals applied to the note.
     * @param note The Note object co convert.
     */
    private int noteToMidi(Note note) {
        // TODO: implement this
        Pitch pitch = new Pitch(note.getPitch());
        // Transpose up/down octaves
        pitch = pitch.octaveTranspose(note.getOctave());
        // # of semitones to transpose
        int semitones = accidentalSemitones.get(getAccidental(note.getPitch(), note.getOctave()));
        pitch = pitch.accidentalTranspose(semitones);
        return pitch.toMidiNote();
    }

    /**
     * Helper function to make a Pair<Character, Integer>.
     */
    private static Pair<Character, Integer> makePair(char pitch, int octave) {
        return new Pair<Character, Integer>(pitch, octave);
    }

    /**
     * Looks up the current accidental applied to a note.
     */
    private Note.AccidentalType getAccidental(char note, int octaves) {
        Pair<Character, Integer> p = makePair(note, octaves);
        Note.AccidentalType accidental = Note.AccidentalType.Normal;
        if (currentAccidentals.containsKey(p)) {
            accidental = currentAccidentals.get(p);
        }
        // If the note is normal, look up the key signature effects using currentDefaultAccidentals
        if (accidental.equals(Note.AccidentalType.Normal)) {
            if (currentDefaultAccidentals.containsKey(note)) {
                return currentDefaultAccidentals.get(note);
            }
        }
        return accidental;
    }

    // The current piece
    private Piece piece;
    
    // MIDI sequencer
    private SequencePlayer sequencePlayer;
    
    // The current position of each voice's track
    private Map<String, Integer> positions = new HashMap<String, Integer>();

    // Keeps track of default accidentals for the current key
    private Map<Character, Note.AccidentalType> currentDefaultAccidentals;

    // Keeps track of what accidentals are applied to each pitch & octave
    private Map<Pair<Character, Integer>, Note.AccidentalType> currentAccidentals;

    // Whether or not the piece only has the default voice.
    private boolean singleVoice;

    // The current voice
    private String currentVoice;

    private static String DEFAULT_VOICE = "DEFAULT";

    // Maps each unique major key to a set of default accidentals
    private static Map<Key, Map<Character, Note.AccidentalType>> defaultAccidentalsForKey =
        new HashMap<Key, Map<Character, Note.AccidentalType>>();

    // Generate defaultAccidentalsForKey
    static {
        Note.AccidentalType sharp = Note.AccidentalType.Sharp;
        Note.AccidentalType flat = Note.AccidentalType.Flat;
        Map<Character, Note.AccidentalType> accidentals = new HashMap<Character, Note.AccidentalType>();
        
        /////////////////////////
        // Simple major scales //
        /////////////////////////

        defaultAccidentalsForKey.put(Key.CMajor, accidentals);

        // G Major
        accidentals = new HashMap<Character, Note.AccidentalType>();
        accidentals.put('F', sharp);
        defaultAccidentalsForKey.put(Key.GMajor, accidentals);

        // D Major
        accidentals = new HashMap<Character, Note.AccidentalType>(accidentals);
        accidentals.put('C', sharp);
        defaultAccidentalsForKey.put(Key.DMajor, accidentals);

        // A Major
        accidentals = new HashMap<Character, Note.AccidentalType>(accidentals);
        accidentals.put('G', sharp);
        defaultAccidentalsForKey.put(Key.AMajor, accidentals);

        // E Major
        accidentals = new HashMap<Character, Note.AccidentalType>(accidentals);
        accidentals.put('D', sharp);
        defaultAccidentalsForKey.put(Key.EMajor, accidentals);

        // B Major
        accidentals = new HashMap<Character, Note.AccidentalType>(accidentals);
        accidentals.put('A', sharp);
        defaultAccidentalsForKey.put(Key.BMajor, accidentals);

        // C# Major
        accidentals = new HashMap<Character, Note.AccidentalType>(accidentals);
        accidentals.put('E', sharp);
        defaultAccidentalsForKey.put(Key.CSharpMajor, accidentals);

        /////////////////
        // Flat scales //
        /////////////////
        accidentals = new HashMap<Character, Note.AccidentalType>();
        
        // F Major
        accidentals.put('B', flat);
        defaultAccidentalsForKey.put(Key.FMajor, accidentals);

        // B_
        accidentals = new HashMap<Character, Note.AccidentalType>(accidentals);
        accidentals.put('E', flat);
        defaultAccidentalsForKey.put(Key.BFlatMajor, accidentals);

        // E_
        accidentals = new HashMap<Character, Note.AccidentalType>(accidentals);
        accidentals.put('A', flat);
        defaultAccidentalsForKey.put(Key.EFlatMajor, accidentals);

        // A__
        accidentals = new HashMap<Character, Note.AccidentalType>(accidentals);
        accidentals.put('D', flat);
        defaultAccidentalsForKey.put(Key.AFlatMajor, accidentals);

        // D_
        accidentals = new HashMap<Character, Note.AccidentalType>(accidentals);
        accidentals.put('E', flat);
        defaultAccidentalsForKey.put(Key.DFlatMajor, accidentals);

        // G_
        accidentals = new HashMap<Character, Note.AccidentalType>(accidentals);
        accidentals.put('C', flat);
        defaultAccidentalsForKey.put(Key.GFlatMajor, accidentals);

        // C_
        accidentals = new HashMap<Character, Note.AccidentalType>(accidentals);
        accidentals.put('F', flat);
        defaultAccidentalsForKey.put(Key.CFlatMajor, accidentals);
    }

    // Keeps track of how many semitones each accidental should transpose a note
    private static Map<Note.AccidentalType, Integer> accidentalSemitones = new HashMap<Note.AccidentalType, Integer>();

    static {
        accidentalSemitones.put(Note.AccidentalType.Normal, 0);
        accidentalSemitones.put(Note.AccidentalType.Natural, 0);
        accidentalSemitones.put(Note.AccidentalType.Sharp, 1);
        accidentalSemitones.put(Note.AccidentalType.DoubleSharp, 2);
        accidentalSemitones.put(Note.AccidentalType.Flat, -1);
        accidentalSemitones.put(Note.AccidentalType.DoubleFlat, -2);
    }
}

package parser;

import java.util.List;
import java.util.ArrayList;

import player.Visitor;

/**
 * Represents a group of notes that are played at the same time.
 */
public class Chord extends Node {
    private final List<Note> notes;

    /**
     * Constructs a chord.
     * @param n The notes in the chord.
     */
    public Chord(List<Note> n) {
        notes = new ArrayList<Note>(n);
    }
    
    /**
     * Constructs a chord with a single note.
     * @param n The single note.
     */
    public Chord(Note n){
    	notes = new ArrayList<Note>();
    	notes.add(n);
    }
    
    /**
     * The notes of the chord.
     */
    public List<Note> getNotes(){
    	return notes;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Chord) {
            Chord otherChord = (Chord)other;
            return this.notes.equals(otherChord.notes);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%d-Chord:%s", notes.size(), notes);
    }
}

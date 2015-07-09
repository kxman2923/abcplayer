package parser;

import java.util.List;
import java.util.ArrayList;

import player.Visitor;

/**
 * Represents a sequence of chords to be played one after another.
 * Singlets are played in the time of one chord.
 * Doublets are played in the time of three chords.
 * Triplets are played in the time of two chords.
 * Quadruplets are played in the time of three chords.
 */
public class Tuplet extends Node {
    private final List<Chord> chords;

    /**
     * Constructs a tuplet.
     * @param c The chords composing the tuplet.
     */
    public Tuplet(List<Chord> c) {
        chords = new ArrayList<Chord>(c);
    }
    
    /**
     * Constructs a singlet.
     * @param c The single chord in the tuplet.
     */
    public Tuplet(Chord c){
    	chords = new ArrayList<Chord>();
    	chords.add(c);
    }
    
    /**
     * The chords composing the tuplet. 
     */
    public List<Chord> getChords(){
    	return chords;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Tuplet) {
            Tuplet otherTuplet = (Tuplet) other;
            return this.chords.equals(otherTuplet.chords);
        }
        return false;
    }

    @Override
    public String toString() {
        String tupletType = "Tuplet";
        switch (chords.size()) {
        case 1:
            tupletType = "Singlet";
            break;
        case 2:
            tupletType = "Doublet";
            break;
        case 3:
            tupletType = "Triplet";
            break;
        case 4:
            tupletType = "Quadruplet";
            break;
        }
        return String.format("%s:%s", tupletType, chords);
    }
}

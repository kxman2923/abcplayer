package parser;

import java.util.*;

import player.Visitor;

/**
 * Represents a series of tuplets to be played one after another.
 * Accidentals applied to notes in previous measures do not carry over to the current one.
 */
public class Measure extends Node {
    private final List<Tuplet> elements;
    private final String voice;

    /**
     * Constructs a measure in the default voice.
     * @param t The tuplets of the measure.
     */
    public Measure(List<Tuplet> t) {
        elements= new ArrayList<Tuplet>(t);
        voice= new String("");
    }
    
    /**
     * Constructs a measure associated with a voice.
     * @param t The tuplets of the measure.
     * @param v The voice the measured is associated with.
     */
    public Measure(List<Tuplet> t, String v){
    	elements= new ArrayList<Tuplet>(t);
        voice= new String(v);
    }
    
    /**
     * The tuplets of the measure.
     */
    public List<Tuplet> getElements(){
    	return elements;
    }
    
    /**
     * The voice this measure belongs to.
     */
    public String getVoice(){
    	return voice;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return String.format("Measure(%s):%s", voice, elements);
    }
}

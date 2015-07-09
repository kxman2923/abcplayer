package parser;

import java.util.ArrayList;
import java.util.List;

import player.Visitor;
/**
 * Represents a piece of music specified by an ABC file.
 */
public class Piece extends Node {
	private int index;                                         // The index number of the ABC file.
        private String title;                                      // The title of the piece
        private String composer;                                   // The composer's name
        private Duration defaultLength;                            // The default note length.
        private Duration meter;                                    // The meter
        private int tempo;                                         // Tempo, in defaultnotes per minute.
        private List<String> voices = new ArrayList<String>();     //The voices
        private List<Measure> measures = new ArrayList<Measure>(); //the musical elements
        private Key key;                                           // The key signature
        
	public int getIndex(){
		return index;
	}
	
	public void setIndex(int value){
		index = value;
	}
	
	public int getTempo(){
		return tempo;
	}
	
	public void setTempo(int value){
		tempo = value;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String value){
		title = new String(value);
	}
	public String getComposer(){
		return composer;
	}
	
	public void setComposer(String value){
		composer = new String(value);
	}
	
	public Duration getDefaultLength(){
		return defaultLength;
	}
	public void setDefaultLength(Duration d){
                defaultLength = new Duration(d.getNumerator(), d.getDenominator());
	}
	public Duration getMeter(){
		return meter;
	}
	public void setMeter(Duration d){
		meter= new Duration(d.getNumerator(),d.getDenominator());
	}
	public List<Measure> getMeasures(){
		return measures;
	}
	
	public void setMeasures(List<Measure> measuresInput){
		measures = measuresInput;
	}
	public Key getKey(){
		return key;
	}
	public void setKey(String value){
		String input = value;
		if (input.length() == 1) { // A
			//Pitch keyPitch = new Pitch(input.charAt(0));
			key = new Key(input.charAt(0), Key.Scale.Major, Key.Accidental.Normal);
		} else if (input.length() == 2){
			char c = input.charAt(1);		
			if (c == 'b'){ // Ab
				//Pitch keyPitch = new Pitch(input.charAt(0));
				key = new Key(input.charAt(0), Key.Scale.Major, Key.Accidental.Flat);
			} else if (c == '#'){ // A#
				//Pitch keyPitch = new Pitch(input.charAt(0));
				key = new Key(input.charAt(0), Key.Scale.Major, Key.Accidental.Sharp);
			} else if (c == 'm'){ // Am
				//Pitch keyPitch = new Pitch(input.charAt(0));
				key = new Key(input.charAt(0), Key.Scale.Minor, Key.Accidental.Normal);

			}
		} else if (input.length() == 3) { // i.e Abm
			//Pitch keyPitch = new Pitch(input.charAt(0));
			char a = input.charAt(1);	// accidental
			//char s = input.charAt(2);	// scale always 'm'
			
			if (a == 'b'){
				key = new Key(input.charAt(0), Key.Scale.Minor, Key.Accidental.Flat);
			} else { // a = '#';
				key = new Key(input.charAt(0), Key.Scale.Minor, Key.Accidental.Sharp);
			}
			
		} else {// invalid key input => Consider C major
			key = new Key('C', Key.Scale.Major, Key.Accidental.Normal);
		}
	}
	public List<String> getVoices(){
		return voices;
	}
	
	public void addVoices(String value){
		voices.add(value);
	}

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}

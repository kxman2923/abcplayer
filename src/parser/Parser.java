package parser;

import java.util.*;

import lexer.Token;


/**
   Converts a list of tokens into a Piece object.
 */
public class Parser {
	private ListIterator<Token> toks;
	private List<Token> tokens;
	private Token next, previous;
	private Piece piece = new Piece();
	private String currVoice;
	/**
         * Constructs a parser to parse a list of tokens.
         * @param tokens The list of tokens.
	 */
	public Parser(List<Token> tokens){
                this.tokens = tokens;
		this.toks= this.tokens.listIterator();
		this.currVoice=new String("");
	}

        /**
         * Parses the list of tokens.
         */
        public void parse() {
            parseHeaderAll();
        }

	/**
	 * parseHeader parses tokens in toks and sets instance variables for piece
	 * These variables include: Index, Title, DefaultLength, Voices, Composer, Tempo, Meter, and Key
	 * It also starts the parseBody method and sets the returned measures into the stored Piece object.
	 */
         private void parseHeaderAll(){
		if (toks.hasNext()){
			next=toks.next();
			previous=new Token(next.type,next.value);
			if(next.type==Token.Type.NumberField){
				try{
					piece.setIndex(Integer.parseInt(next.value));
				}
				catch(NumberFormatException n){
					throw new RuntimeException("Expected a number for the number field token!");
				}				
			}
			else{
				throw new RuntimeException("The first token must be a NumberField (index number) token!");
			}
			if(toks.hasNext()){
				previous=new Token(next.type,next.value);
				next=toks.next();
				if(next.type==Token.Type.TitleField){
					piece.setTitle(next.value);
				}
				else{
					throw new RuntimeException("The second token must be a TitleField (title) token!");
				}
			}
			else{
				throw new RuntimeException("Expected more header fields!");
			}
		}
		else{
			throw new RuntimeException("Expected more header fields!");
		}
		while (toks.hasNext()) {
			previous= new Token(next.type, next.value);
			next= toks.next();
			if (next.type == Token.Type.NumberField){
				try{
					piece.setIndex(Integer.parseInt(next.value));
				}
				catch(NumberFormatException n){
					throw new RuntimeException("Expected a number for the number field token!");
				}	
			} else if (next.type == Token.Type.TitleField){
				piece.setTitle(next.value);
			} else if (next.type == Token.Type.TempoField){
				try{
					piece.setTempo(Integer.parseInt(next.value));
				}
				catch(NumberFormatException n){
					throw new RuntimeException("Expected a number for the tempo field token!");
				}	
			} else if (next.type == Token.Type.MeterField){
				if (next.value.equals("C")) {
	                piece.setMeter(new Duration(4, 4));
	            } else if (next.value.equals("C|")) {
	                piece.setMeter(new Duration(2, 2));
	            } else {
	                piece.setMeter(Duration.fromString(next.value));
	            }
			} else if (next.type == Token.Type.ComposerField){
				piece.setComposer(next.value);
			} else if (next.type == Token.Type.DefaultLengthField){
				piece.setDefaultLength(Duration.fromString(next.value));
			} else if (next.type == Token.Type.KeyField){
				piece.setKey(next.value);
				break; //Since the keyField should be the last field in the header
			} else if (next.type == Token.Type.VoiceField){
				if (this.piece.getVoices().contains(next.value)){ 
					break;
				} else {
					piece.addVoices(next.value);
				}
			} else { // call parseBodyAll to parse measures, set measures to piece
				break;
			}
		}
		if(piece.getComposer()==null){
			piece.setComposer("Unknown");
		}
		if(piece.getDefaultLength()==null){
			piece.setDefaultLength(new Duration(1,8));
		}
		if(piece.getMeter()==null){
			piece.setMeter(new Duration(4,4));
		}
		if(piece.getTempo()==0){
			piece.setTempo(100);
		}
		if(piece.getKey()==null){
			throw new RuntimeException("The header needs a keyField Token!");
		}
		if(toks.hasNext()){
			int index= toks.nextIndex();
			toks= this.tokens.listIterator(index);
			List<Measure> measures = new ArrayList<Measure>();
			measures = parseBodyAll();
			piece.setMeasures(measures);
		}
		else{
			piece.setMeasures(new ArrayList<Measure>());
		}
		
	}
		
	/**
	 * Retrieves the parsed piece. This must only be called after parse().
	 * @return The parsed piece.
	 */
	public Piece getPiece(){
		return piece;
	}
	
	/**
	 * Begin body parser
	 */
	private List<Measure> parseBodyAll(){
		if(toks.hasNext()){
			next= toks.next();
			previous= new Token(next.type, next.value);
			return parseABCMusicAll();
		}
		//In the case of an empty ABC music body.
		return new ArrayList<Measure>();
	}
	
	/*
	 * Parses over the abc musical notes (second part of the abc file)
	 * @return a Piece object that contains the second part of the abc file
	 */
	private List<Measure> parseABCMusicAll(){
		ArrayList<Measure> measures= new ArrayList<Measure>();
		/*
		 * The second array list is to handle the case of an endRepeat token (:|)
		 * without a begin repeat token
		 */
		ArrayList<Measure> track= new ArrayList<Measure>();
		Measure first, second;
		boolean inVoice=false;
		/*
		 * Must handle two cases:
		 * Case 1: The piece contains no voices.
		 * 			Parse over the ABC music by parsing the measures
		 * Case 2: The piece contains at least one voice.
		 * 			Parse over the measures in each voice sequentially.
		 */
		if(this.piece.getVoices().size()<=1){
			if(this.piece.getVoices().size()==1){
				currVoice= new String(this.piece.getVoices().get(0));
			}
			//Case 1
			while (next.type != Token.Type.EndMajorSection ){
				//If a beginRepeat token (|:) is seen, parse the following measures as a repeat
				if(next.type==Token.Type.BeginRepeat){
					measures.addAll(parseRepeatAll().getRepeatMeasures());
				}
				//If a voicefield is read, an exception must be thrown if it doesn't match the one stored (if any)
				else if(next.type==Token.Type.VoiceField){
					if(!next.value.equals(currVoice)){
						throw new RuntimeException("The voice "+next.value+" was not specified in header!");
					}
				}
				//Parse the measure and keep track of that measure
				else{
					Measure m= parseMeasureAll();
					measures.add(m);
					track.add(m);
				}
				/*If an endRepeat token is seen without a begin repeat token
				 * add the tracked measures (which should count as a repeat).
				 */
				if(next.type==Token.Type.EndRepeat){
					measures.addAll(track);
					track.clear();
				}
				if(toks.hasNext()){
					previous= new Token(next.type, next.value);
					next= toks.next();	
					/*
					 * If a firstRepeat token is seen without a beginRepeat token
					 * parse the first measure seen, then the second measure seen
					 */
					if(next.type==Token.Type.FirstRepeat){
						previous= new Token(next.type, next.value);
						next= toks.next();	
						first= parseMeasureAll();
						//To prepare for the secondRepeat
						previous= new Token(next.type, next.value);
						next= toks.next();	
						if(next.type==Token.Type.SecondRepeat){
							previous= new Token(next.type, next.value);
							next= toks.next();	
							second=parseMeasureAll();
							//Add the parsed repeat measures in the correct order
							measures.add(first);
							measures.addAll(track);
							track.clear();
							measures.add(second);
						}
						/*
						 * A secondRepeat token must come after the first ending 
						 * of the repeated section of music with a firstRepeat token read previously
						 */
						else{
							throw new RuntimeException("Expected a secondRepeat token!");
						}
					}
				}
				/*
				 * If there are no other tokens to end, the parser has reached the end of the file
				 * and can return the AST it has produced so far.
				 */
				
				else{
					break;
				}
			}
		}
		else{
			//Case 2
			for(int i=0; i <this.piece.getVoices().size(); i++){
				//Retrieve the voices stored in the piece
				currVoice= new String(this.piece.getVoices().get(i));
				/*
				 * Store the current index of the iterator
				 */
				int place= toks.nextIndex()-1;
				//Reset toks to be a iterator starting from the place variable on the tokens list.
				toks= this.tokens.listIterator(place);
				while(next.type!=Token.Type.EndMajorSection ||
						toks.hasNext()){
					/*
					 * If a VoiceField token is read, compare its value to currVoice.
					 * If the values match, set inVoice to true. Else , set inVoice to false.
					 */
					if(next.type==Token.Type.VoiceField){
						if(!this.piece.getVoices().contains(next.value)){
							throw new RuntimeException("Voice "+next.value+ " is not specified in the header!");
						}
						if(next.value.equals(currVoice)){
							inVoice=true;
						}
						else{
							inVoice=false;
						}
						if(toks.hasNext()){
							previous= new Token(next.type, next.value);
							next= toks.next();	
						}
						//The parser has reached the end of the token list, so output what it has.
						else{
							measures.add(new Measure(new ArrayList<Tuplet>()));
						}
					}
					/*
					 * If the token(s) currently being read is in the current Voice,
					 * parse them regularly (just like in case 1)
					 */
					if(inVoice){
						if(next.type==Token.Type.BeginRepeat){
							measures.addAll(parseRepeatAll().getRepeatMeasures());
						}
						else{
							Measure m= parseMeasureAll();
							measures.add(m);
							track.add(m);
						}
						if(next.type==Token.Type.EndRepeat){
							measures.addAll(track);
							track.clear();
						}
						if(toks.hasNext()){
							previous= new Token(next.type, next.value);
							next= toks.next();	
							if(next.type==Token.Type.FirstRepeat){
								previous= new Token(next.type, next.value);
								next= toks.next();	
								first= parseMeasureAll();
								//To prepare for the secondRepeat
								previous= new Token(next.type, next.value);
								next= toks.next();	
								if(next.type==Token.Type.SecondRepeat){
									previous= new Token(next.type, next.value);
									next= toks.next();	
									second=parseMeasureAll();
									measures.add(first);
									measures.addAll(track);
									track.clear();
									measures.add(second);
								}
								else{
									throw new RuntimeException("Expected a secondRepeat token!");
								}
							}
						}
						else{
							break;
						}
					}
					else{
						if(toks.hasNext()){
							previous= new Token(next.type, next.value);
							next= toks.next();
						}
						else{
							break;
						}					
					}
				}
				/*
				 * For safe measure, reset toks and the next and previous variables
				 * so that the parser starts at the beginning of the ABC music body
				 */
				toks= this.tokens.listIterator(place);
				if(toks.hasNext()){
					previous= new Token(next.type, next.value);
					next= toks.next();
				}
				else{
					break;
				}
			}
		}		
		return measures;
	}
	
	/*
	 * Parses measures being repeated (up to 2) 
	 * @return a Repeat object representing the measures being repeated (up to 2)
	 */
	private Repeat parseRepeatAll(){
		ArrayList<Measure> measures= new ArrayList<Measure>();
		Measure first= null;
		Measure second= null;
		if(toks.hasNext()){
			previous= new Token(next.type, next.value);
			next= toks.next();
		}
		/*
		 * Throw an error if the parser reaches the end of the 
		 * token list without parsing any notes/chords/tuplets etc. 
		 */
		else{
			throw new RuntimeException("Expected notes/chords/tuplets for the repeat!");
		}
		while(next.type!=Token.Type.EndRepeat &&
				next.type!=Token.Type.EndMajorSection){
			/*
			 * Parse over the next two measures, the first
			 * measure for the first ending of the repeat,
			 * the second measure being the second ending of the repeat
			 */
			if(next.type==Token.Type.FirstRepeat){
				if(toks.hasNext()){
					previous= new Token(next.type, next.value);
					next= toks.next();					
					first= parseMeasureAll();					
				}
				/*
				 * If there is no tokens after the firstRepeat token is read,
				 * throw an exception since the parser expects a secondRepeat token 
				 * to occur somewhere after the firstRepeat token.
				 */
				else{
					throw new RuntimeException("Expected a secondRepeat token after the firstRepeat token!");
				}
				/*
				 * If there is no tokens after the first ending is parsed,
				 * throw an exception since the parser expects a second ending
				 * to occur somewhere after the first ending.
				 */
				if(toks.hasNext()){
					previous= new Token(next.type, next.value);
					next= toks.next();
				}
				else{
					throw new RuntimeException("Expected a second ending after the first ending!");
				}
				//Parses over the second ending
				if(next.type==Token.Type.SecondRepeat){
					second=parseMeasureAll();
				}
				/*
				 * If a VoiceField token is read, check to see if it 
				 * matches the current voice. If it does parse the following measures
				 * into the repeat object. Else, skip those measures until a VoiceField
				 * token in the current voice is read. If it reaches the end of the token list,
				 * throw an exception since a second ending is expected.
				 */
				else if(next.type==Token.Type.VoiceField){
					if(!next.value.equals(currVoice)){
						//Skip over the mesaures in that voice
						while(!(next.type==Token.Type.VoiceField &&
								next.value.equals(currVoice))){
							if(toks.hasNext()){
								previous= new Token(next.type, next.value);
								next= toks.next();
							}
							else{
								break;
							}
						}
						if(toks.hasNext()){
							previous= new Token(next.type, next.value);
							next= toks.next();
							if(next.type==Token.Type.SecondRepeat){
								previous= new Token(next.type, next.value);
								next= toks.next();
								second=parseMeasureAll();
							}
							else{
								throw new RuntimeException("Expected a secondRepeat token after the firstRepeat token!");
							}
						}
						else{
							throw new RuntimeException("Expected a second ending after the first ending!");
						}
					}
				}
				else{
					throw new RuntimeException("Expected a secondRepeat token not " +next.type);
				}
			}
			/*
			 * Checks to see if the VoiceField token's value matches the 
			 * current voice. If it doesn't, skip over the next tokens
			 * until a VoiceField token with a the same value of currVoice is read.
			 */
			else if(next.type==Token.Type.VoiceField){
				if(!next.value.equals(currVoice)){
					//Skip over the measures in that voice
					while(!(next.type==Token.Type.VoiceField&&
							next.value.equals(currVoice))){
						if(toks.hasNext()){
							previous= new Token(next.type, next.value);
							next= toks.next();
						}
						else{
							break;
						}
					}
				}
			}
			else{
				measures.add(parseMeasureAll());
			}
			if(toks.hasNext()){
				previous= new Token(next.type, next.value);
				next= toks.next();				
			}
			else{
				break;
			}
		}
		//Return a repeat object with the list of measures
		return new Repeat(measures, first, second);
	}
	
	/*
	 * Parses a single measure
	 * @return a Measure object representing the measure being read next
	 */
	private Measure parseMeasureAll(){
		List<Tuplet> elements= new ArrayList<Tuplet>();
		/*
		 * A measure ends on a barline, firstRepeat, secondRepeat,
		 * endRepeat, and endMajorSection tokens.
		 */
		while(next.type!=Token.Type.BarLine &&
				next.type!=Token.Type.FirstRepeat &&
				next.type!=Token.Type.SecondRepeat &&
				next.type!=Token.Type.EndRepeat &&
				next.type!=Token.Type.EndMajorSection){
			if (next.type==Token.Type.BaseNote||
					next.type==Token.Type.Rest||
					next.type==Token.Type.Sharp||
					next.type==Token.Type.DoubleSharp||
					next.type==Token.Type.Flat||
					next.type==Token.Type.DoubleFlat||
					next.type==Token.Type.Natural||
					next.type==Token.Type.BeginChord||
					next.type==Token.Type.BeginDoublet ||
					next.type==Token.Type.BeginTriplet ||
					next.type==Token.Type.BeginQuadruplet){
				elements.add(parseElementAll());
			}
			//Skip over voiceField or beginMajorSection tokens being read
			else if(next.type==Token.Type.VoiceField ||
					next.type==Token.Type.BeginMajorSection){
			}
			/*
			 * Only expect baseNotes, rests, accidentals, beginChords, or
			 * beging tuplet tokens to be read in this method
			 */
			else{
				throw new RuntimeException("Invalid Input: "+next.type + " "+ next.value+
						" cannot be in a Measure!");
			}
			if (toks.hasNext()){
				previous= new Token(next.type, next.value);
				next= toks.next();	
			}
			//If the end of the token list, return what is already stored in measure.
			else{
				break;
			}
		}
		//Return a Measure object representing the notes, chords, and tuplets read from the lexer
		return new Measure(elements,currVoice);
	}
	
	/*
	 * Parses over a note, rest, chord, or tuplet
	 * @return a Tuplet containing the notes for the note, chord or tuplet.
	 */
	private Tuplet parseElementAll(){
		if(next.type==Token.Type.BaseNote||
				next.type==Token.Type.Rest||
				next.type==Token.Type.Sharp||
				next.type==Token.Type.DoubleSharp||
				next.type==Token.Type.Flat||
				next.type==Token.Type.DoubleFlat||
				next.type==Token.Type.Natural||
				next.type==Token.Type.BeginChord){
			return new Tuplet(parseNoteElementAll());
		}
		else if (next.type==Token.Type.BeginDoublet ||
				next.type==Token.Type.BeginTriplet ||
				next.type==Token.Type.BeginQuadruplet){
			return parseTupletAll();
		}
		else{
			throw new RuntimeException("Invalid Input: Not a note, rest, or tuplet");
		}
	}
	
	/*
	 * Parses over a note, rest, or chord
	 * @return a Chord object representing a note (chord with one note) or a chord 
	 */
	private Chord parseNoteElementAll(){
		if (next.type==Token.Type.BaseNote||
				next.type==Token.Type.Rest||
				next.type==Token.Type.Sharp||
				next.type==Token.Type.DoubleSharp||
				next.type==Token.Type.Flat||
				next.type==Token.Type.DoubleFlat||
				next.type==Token.Type.Natural){
			return new Chord(parseNoteAll());
		}
		else if (next.type==Token.Type.BeginChord){
			return parseChordAll();
		}
		else{
			throw new RuntimeException("Invalid Input: Not a BaseNote/Rest or a Chord");
		}
	}	

	/*
	 * Parses a single tuplet
	 * @return a Tuplet object representing the tuplet being read next
	 */
	private Tuplet parseTupletAll(){
		ArrayList<Chord> chords= new ArrayList<Chord>();
		int count=0;
		int num=0;
		Duration d;
		 //Create a certain ratio to be used later based on the tuplet
		if(next.type==Token.Type.BeginDoublet){
			count=2;
			num=3;
			d= new Duration(num, count);
		}
		else if(next.type==Token.Type.BeginTriplet){
			count=3;
			num=2;
			d= new Duration(num, count);
		}
		else if(next.type==Token.Type.BeginQuadruplet){
			count=4;
			num=3;
			d= new Duration(num, count);
		}
		else{
			throw new RuntimeException("Expected a begin Tuplet token");
		}
		//Iterate over the next "count" notes/chords and output them in the tuplet object
		for(int i=0; i<count; i++){
			if(toks.hasNext()){
				previous= new Token(next.type, next.value);
				next= toks.next();
			}
			else{
				throw new RuntimeException("Expected some notes/rests in this tuplet!");
			}
			if(next.type==Token.Type.BeginChord){
				chords.add(parseChordAll());
			}
			else if(next.type==Token.Type.BaseNote||
					next.type==Token.Type.Rest||
					next.type==Token.Type.Sharp||
					next.type==Token.Type.DoubleSharp||
					next.type==Token.Type.Flat||
					next.type==Token.Type.DoubleFlat||
					next.type==Token.Type.Natural){
				Note n= parseNoteAll();
				Note m= new Note(n.getPitch(),
						n.getDuration().multiply(d),
						n.getOctave(), n.getAccidental(), n.isRest());
				chords.add(new Chord(m));
			}	
			else{
				throw new RuntimeException("Invalid Input: Tuplet can only contain Chords or Notes/Rests " +next.type);
			}
		}
		//Return the set of chords in a Tuplet object
		return new Tuplet(chords);
	}
	
	/*
	 * Parses a single chord
	 * @return a Chord object representing the chord being read next
	 */
	
	private Chord parseChordAll(){
		if(next.type!=Token.Type.BeginChord){
			throw new RuntimeException("Expected a beginChord token");
		}
		if(toks.hasNext()){
			previous= new Token(next.type, next.value);
			next= toks.next();
		}
		else{
			throw new RuntimeException("Expected some type of notes/rests in this chord!");
		}
		ArrayList<Note> notes= new ArrayList<Note>();
		while(next.type!=Token.Type.EndChord){
			if(next.type==Token.Type.BaseNote||
				next.type==Token.Type.Rest||
				next.type==Token.Type.Sharp||
				next.type==Token.Type.DoubleSharp||
				next.type==Token.Type.Flat||
				next.type==Token.Type.DoubleFlat||
				next.type==Token.Type.Natural){
				notes.add(parseNoteAll());
			}
			else{
				throw new RuntimeException("Invalid Input: "+next.type+ " is not a baseNote/Rest or accidental!");
			}
			if(toks.hasNext()){
				previous= new Token(next.type, next.value);
				next= toks.next();
			}
			else{
				throw new RuntimeException("Expected a finished chord!");
			}
		}
		//Return the list of notes as a Chord object
		return new Chord(notes);
	}
	
	/*
	 * Parses a single note
	 * @return a Note object representing the note being read next
	 */
	private Note parseNoteAll(){
		Note.AccidentalType acc=Note.AccidentalType.Normal;
		int oct=0;
		char pitch='z';
		boolean isRest=false;
		Duration dur= this.piece.getDefaultLength();
		//Accidentals are optional
		//Set acc to some accidental if an accidental is read next
		if (next.type==Token.Type.Sharp){
			if(toks.hasNext()){
				previous= new Token(next.type, next.value);
				next= toks.next();
			}
			else{
				throw new RuntimeException("Expected a baseNote after this accidental!");
			}
			acc=Note.AccidentalType.Sharp;
		}
		else if(next.type==Token.Type.DoubleSharp){
			if(toks.hasNext()){
				previous= new Token(next.type, next.value);
				next= toks.next();
			}
			else{
				throw new RuntimeException("Expected a baseNote after this accidental!");
			}
			acc=Note.AccidentalType.DoubleSharp;
		}
		else if(next.type==Token.Type.Flat){
			if(toks.hasNext()){
				previous= new Token(next.type, next.value);
				next= toks.next();
			}
			else{
				throw new RuntimeException("Expected a baseNote after this accidental!");
			}
			acc=Note.AccidentalType.Flat;
		}
		else if(next.type==Token.Type.DoubleFlat){
			if(toks.hasNext()){
				previous= new Token(next.type, next.value);
				next= toks.next();
			}
			else{
				throw new RuntimeException("Expected a baseNote after this accidental!");
			}
			acc=Note.AccidentalType.DoubleFlat;
		}
		else if(next.type==Token.Type.Natural){
			if(toks.hasNext()){
				previous= new Token(next.type, next.value);
				next= toks.next();
			}
			else{
				throw new RuntimeException("Expected a baseNote after this accidental!");
			}
			acc=Note.AccidentalType.Natural;
		}
		
		//Expect a baseNote or rest next. If not, throw an exception.
		if(next.type==Token.Type.BaseNote){
			pitch= next.value.charAt(0);
			if (Character.isLowerCase(pitch)){
				pitch=Character.toUpperCase(pitch);
				oct++;
			}
		}
		else if(next.type==Token.Type.Rest){
			isRest=true;
		}
		else{
			throw new RuntimeException("Invalid Input: Expected a BaseNote/Rest");
		}
		//If there are no other fields after this, output what is currently stored.
		if(toks.hasNext()){
			previous= new Token(next.type, next.value);
			next= toks.next();
		}
		else{
			return new Note(pitch, this.piece.getDefaultLength(), oct, acc, isRest);
		}
		/*
		 * If an octaveUp/octaveDown is read, increment/decrement oct appropiately.
		 * 
		 */
		if(next.type==Token.Type.OctaveUp){
			//Can only have an OctaveUp/OctaveDown after a baseNote.
			if(previous.type!=Token.Type.BaseNote){
				throw new RuntimeException("Invalid Input: Octave before note");
			}
			while(next.type==Token.Type.OctaveUp||
					next.type==Token.Type.OctaveDown){
				if(next.type==Token.Type.OctaveUp){
					oct++;
				}
				//Throw an exception if an OctaveDown if read after an OctaveUp
				else if(next.type==Token.Type.OctaveDown){
					throw new RuntimeException("Invalid Input: Cannot have , after '");
				}
				if(toks.hasNext()){
					previous= new Token(next.type, next.value);
					next= toks.next();	
				}
				//If nothing is next, return what is currently stored.
				else{
					return new Note(pitch, this.piece.getDefaultLength(), oct, acc, isRest);
				}
			}
			//Move the index of the iterator back to prepare for a potential noteLength
			next=toks.previous();
		}
		else if(next.type==Token.Type.OctaveDown){
			//Can only have an OctaveUp/OctaveDown after a baseNote.
			if(previous.type!=Token.Type.BaseNote){
				throw new RuntimeException("Invalid Input: Octave before note");
			}
			while(next.type==Token.Type.OctaveDown||
					next.type==Token.Type.OctaveUp){
				if(next.type==Token.Type.OctaveDown){
					oct--;
				}
				//Throw an exception if an OctaveUp if read after an OctaveDown
				else if(next.type==Token.Type.OctaveUp){
					throw new RuntimeException("Invalid Input: Cannot have ' after ,");
				}
				if(toks.hasNext()){
					previous= new Token(next.type, next.value);
					next= toks.next();	
				}
				//If nothing is next, return what is currently stored.
				else{
					return new Note(pitch, this.piece.getDefaultLength(), oct, acc, isRest);
				}				
			}
			//Move the index of the iterator back to prepare for a potential noteLength
			next= toks.previous();
		}
		//Move the index of the iterator back to prepare for a potential noteLength
		else{
			next=toks.previous();
		}
		
		if(toks.hasNext()){
			next= toks.next();
			if(next.type==Token.Type.NoteLength){
				if(previous.type==Token.Type.BaseNote ||
						previous.type==Token.Type.Rest ||
						previous.type==Token.Type.OctaveDown||
						previous.type==Token.Type.OctaveUp){
					//Must modify the duration object since the fraction in the token must be multiplied by defaultNoteLength
					dur= dur.multiply(Duration.fromString(next.value));
				}
				//Can only have a noteLength token after a baseNote, rest, octaveUp, or octaveDown.
				else{
					throw new RuntimeException("Invalid Input: NoteLength before note/rest or octave change!");
				}
				
			}
			//Move back since the next token cannot be a part of the note.
			else{
				next=toks.previous();
			}
			//return a Note object with the given parameters.
			return new Note(pitch, dur, oct, acc, isRest);
		}
		else{
			return new Note(pitch, this.piece.getDefaultLength(), oct, acc, isRest);
		}
	}
}

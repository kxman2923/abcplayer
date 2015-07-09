package test;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import lexer.Lexer;

import org.junit.*;

import parser.Measure;
import parser.Parser;
import parser.Piece;

public class ParserTest {
	
    private String readFile(String fileName) throws IOException {
        String out = "";
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            out = out + currentLine + "\n";
        }
        br.close();
        return out;
    }
    
    /**
     * testHeader1: test a file with all parts of the header
     * @String input: read from abctestfur_elise.abc that contains only the header of the piece
     * @return: print out title, index, tempo, voices, defaultlength, composer and key of the piece
     */
	@Test
	public void testHeader1() throws IOException{
        String input = readFile("sample_abc/abctestfur_elise.abc");
        //System.out.println(input);
        Parser parse= new Parser(Lexer.lex(input));
        parse.parse();
    	Piece piece = parse.getPiece();
        System.out.println(piece.getTitle() + " title");
        System.out.println(piece.getIndex() + " Index");
        System.out.println(piece.getTempo() + " Tempo");
        System.out.println(piece.getVoices() + " Voices");
        System.out.println(piece.getDefaultLength() + " DefaultLength");
        System.out.println(piece.getComposer() + " Composer");
        System.out.println(piece.getKey() + " Key");
        
	}
	/**
	 * testHeader2: modify invention.abc such that it no longer contains optional fields for
	 * meter, composer, defaultlength, and tempo. should return default values for each field
	 * @throws IOException
	 */
	@Test
	public void testHeader2() throws IOException{
        String input = readFile("sample_abc/abctestInventionHeader.abc");
        System.out.print(Lexer.lex(input));
        //System.out.println(input);
        Parser parse= new Parser(Lexer.lex(input));
        parse.parse();
        Piece piece = parse.getPiece();
        System.out.println(piece.getTitle() + " title");
        System.out.println(piece.getIndex() + " Index");
        System.out.println(piece.getTempo() + " Tempo");
        System.out.println(piece.getVoices() + " Voices");
        System.out.println(piece.getDefaultLength() + " DefaultLength");
        System.out.println(piece.getComposer() + " Composer");
        System.out.println(piece.getKey() + " Key");
	}
	/**
	 * testBody: test with different notes: lowercase, uppercase, different notelength notations, and tuplets
	 */
	@Test
	public void testBody(){
		String input= "X:1\nT:Test Body\nK:C\nC C C3/4 D/4 E | E3/4 D/4 E3/4 F/4 G2 | (3c/2c/2c/2 (3G/2G/2G/2 (3E/2E/2E/2 (3C/2C/2C/2 | G3/4 F/4 E3/4 D/4 C2 |]";
		Parser parse= new Parser(Lexer.lex(input));
		System.out.println("Test Body: ");
		parse.parse();
    	Piece piece = parse.getPiece();
    	List<Measure> m = piece.getMeasures();
		for(Measure meas: m){
			System.out.println(meas.toString());
		}
	}
	
	@Test
	public void testSingleNote(){
		String input= "X:1\nT:Test Single Note\nK:C\nC |]";
		Parser parse= new Parser(Lexer.lex(input));
		System.out.println("Test Single Note: ");
		parse.parse();
    	Piece piece = parse.getPiece();
    	List<Measure> m = piece.getMeasures();
		for(Measure meas: m){
			System.out.println(meas.toString());
		}
	}
	
	@Test
	public void testSingleChord(){
		String input= "X:1\nT:Test Single Chord\nK:C\n[C G A]|]";
		Parser parse= new Parser(Lexer.lex(input));
		System.out.println("Test Single Chord: ");
		parse.parse();
    	Piece piece = parse.getPiece();
    	List<Measure> m = piece.getMeasures();
		for(Measure meas: m){
			System.out.println(meas.toString());
		}
	}
	
	@Test
	public void testSingleTuplet(){
		String input= "X:1\nT:Test Single Tuplet\nK:C\n(3C/2C/2C/2|]";
		Parser parse= new Parser(Lexer.lex(input));
		System.out.println("Test Single Tuplet: ");
		parse.parse();
    	Piece piece = parse.getPiece();
    	List<Measure> m = piece.getMeasures();
		for(Measure meas: m){
			System.out.println(meas.toString());
		}
	}
	
	@Test
	public void testSingleRepeat(){
		String input= "X:1\nT:Test Single Repeat\nK:C\n|: C D E F | G A B c |]";
		Parser parse= new Parser(Lexer.lex(input));
		System.out.println("Test Single Repeat: ");
		parse.parse();
    	Piece piece = parse.getPiece();
    	List<Measure> m = piece.getMeasures();
		for(Measure meas: m){
			System.out.println(meas.toString());
		}
	}
	
	/**
	 * repeat and alternative endings
	 */
         @Test
         public void testRepeats(){
         	String input= "X:1\nT:Test Repeats\nK:C\n|: C D E F | [1 G A B c :|[2 F E D C |]";
         	Parser parse= new Parser(Lexer.lex(input));
         	System.out.println("Test Repeats: ");
    		parse.parse();
        	Piece piece = parse.getPiece();
        	List<Measure> m = piece.getMeasures();
    		for(Measure meas: m){
    			System.out.println(meas.toString());
    		}
         }
    /**
     * testRepeats2: without having a beginrepeat token
     */
     @Test
     public void testRepeats2(){
     	String input="X:1\nT:Test Repeats 2\nK:C\nfAA eAA| def gfe|faf gfe|dfe dBA:|";
     	Parser parse= new Parser(Lexer.lex(input));
     	System.out.println("Test Repeats(2): ");
		parse.parse();
    	Piece piece = parse.getPiece();
    	List<Measure> m = piece.getMeasures();
		for(Measure meas: m){
			System.out.println(meas.toString());
		}
     }
    /**
     * repeat and alternative endings, no beginrepeat token
     */
     @Test
     public void testRepeats3(){
     	String input= "X:1\nT:Test Repeats 3\nK:C\ndff cee|aef gfe|baf gfe|[1 efe dBA:|[2 cfe dcB|]";
     	Parser parse= new Parser(Lexer.lex(input));
     	System.out.println("Test Repeats(3): ");
		parse.parse();
    	Piece piece = parse.getPiece();
    	List<Measure> m = piece.getMeasures();
		for(Measure meas: m){
			System.out.println(meas.toString());
		}
     }
	
    
    /**
     * Test empty body
     * @return: print out the measure, should be empty string
     * 
     */
	@Test
	public void testEmptyBody(){
		String input= "X:1\nT:Test Empty\nK:C";
		Parser parse= new Parser(Lexer.lex(input));
		System.out.println("Test Empty: ");
    	parse.parse();
    	Piece piece = parse.getPiece();
    	List<Measure> m = piece.getMeasures();
		for(Measure meas: m){
			System.out.println(meas.toString());
		}
	}
	
    
    /**
     * testVoices: test if multiple voices work here
     * 
     */
	@Test
	public void testVoices(){
		String input="X:1\nT:Test Voices\n\nV:1\nV:2\nV:3\nK:C\n"+
				"V:1 \n" +
				"z2 Gc eGce z2 Gc eGce| z2 Ad fAdf z2 Ad fAdf| z2 Gd fGdf z2 Gd fGdf|]\n" +
				"V:2 \n" +
				"z E7 z E7|z D7 z D7|z D7 z D7|]\n" +
				"V:3\n" +
				"C8 C8|C8 C8|B,8 B,8|]";
		Parser parse= new Parser(Lexer.lex(input));
		System.out.println("Test Voices: ");
    	parse.parse();
    	Piece piece = parse.getPiece();
    	List<Measure> m = piece.getMeasures();
		for(Measure meas: m){
			System.out.println(meas.toString());
		}
	}
	
    /**
     * testOctaves: test if octaveDown or multiple of octaveDown's work
     * @String input: G with two octaveDowns and G with one octaveDown followed by a NoteLength token
     * @return: print out the measure and check if the octaves were handled well
     * Result:
     * Test Octaves: 
     *	Measure():[Singlet:[1-Chord:[G-2 1/8]], Singlet:[1-Chord:[Rest 2/8]], Singlet:[1-Chord:[Rest 1/16]], Singlet:[1-Chord:[G-1 1/16]], Singlet:[1-Chord:[A 1/8]]]

     */
    @Test
    public void testOctaves(){
    	String input= "X:1\nT:Test Octaves\nK:C\nG,, z2 z/ G,/ A|]";
    	Parser parse= new Parser(Lexer.lex(input));
    	System.out.println("Test Octaves: ");
    	parse.parse();
    	Piece piece = parse.getPiece();
    	List<Measure> m = piece.getMeasures();
		for(Measure meas: m){
			System.out.println(meas.toString());
		}
    }
    
    
    /**
     * testRest: test if rest works
     * @ String input: a rest note and end of bar line
     * @ return: print out the measure, check if rest appears. Here is the result:
     * Test Rest: 
	 * Measure():[Singlet:[1-Chord:[Rest 1/8]]]
     */
    @Test
    public void testRest(){
    	String input= "X:1\nT:Test Rest\nK:C\nz |]";
    	System.out.println(Lexer.lex(input));
    	Parser parse= new Parser(Lexer.lex(input));
    	System.out.println("Test Rest: ");
    	parse.parse();
    	Piece piece = parse.getPiece();
    	List<Measure> m = piece.getMeasures();
		for(Measure meas: m){
			System.out.println(meas.toString());
		}
    }
    
    
    /**
     * testOctaves2: test if octaveUp works in chords
     * @ String input is two chords with octaveUp
     * @ return: print out the measure and check the value of octave. Here is the result:
     * Test Octaves(2): 
	 * Measure():[Singlet:[2-Chord:[D+2 2/8, F+1 2/8]], Singlet:[2-Chord:[D+2 2/8, F+1 2/8]]]
     * 
     */
    @Test
    public void testOctaves2(){
    	String input= "X:1\nT:Test Octaves 2\nK:C\n[d'2f2] [d'2f2] |]";
    	Parser parse= new Parser(Lexer.lex(input));
    	System.out.println("Test Octaves(2): ");
    	parse.parse();
    	Piece piece = parse.getPiece();
    	List<Measure> meas = piece.getMeasures();
    	for(Measure m: meas){
    		System.out.println(m.toString());
    	}
    }
	@Test
	public void testParseAll1() throws IOException{
		//String input = readFile("sample_abc/piece1.abc");
		//String input = readFile("sample_abc/piece2.abc");
        String input = readFile("sample_abc/fur_elise.abc");
		//String input = readFile("sample_abc/invention.abc");
		//String input = readFile("sample_abc/paddy.abc");
		//String input = readFile("sample_abc/prelude.abc");
		//String input = readFile("sample_abc/scale.abc");
        //String input= readFile("sample_abc/pokemon.abc");
		//String input= readFile("sample_abc/LesLignes.abc");
		//String input= readFile("sample_abc/LesLignes_NoVoices.abc");
		/*
		 * piece1: good
		 * piece2: invalid input: good
		 * invention.abc and prelude.abc: good
		 * fur_elise: line 204: good
		 * paddy: good
		 * scale: good
		 * little_night_music: good
		 * pokemon: good
		 * LesLignes: good
		 * LesLignes_NoVoices: good (expected an error showing that a voice is not found in the header)
		 */
        Parser parse = new Parser(Lexer.lex(input));
        parse.parse();
        Piece piece = parse.getPiece();
        System.out.println(piece.getTitle() + " title");
        System.out.println(piece.getIndex() + " Index");
        System.out.println(piece.getTempo() + " Tempo");
        System.out.println(piece.getVoices() + " Voices");
        System.out.println(piece.getDefaultLength() + " DefaultLength");
        System.out.println(piece.getComposer() + " Composer");
        System.out.println(piece.getMeasures() + " Measures");
        System.out.println(piece.getKey() + " Key");
	}
}

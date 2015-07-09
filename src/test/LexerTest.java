package test;

import static lexer.Token.Type.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import lexer.Lexer;
import lexer.Token;


import org.junit.*;

public class LexerTest {
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
     * Test scale: all header fields, basenote, notelengths, barline, tuplet, chord
     * @throws IOException
     */
    @Test
    public void simpleScaleTest() throws IOException {
        String input = readFile("sample_abc/scale.abc");        
        
        List<Token> expectedOutput = new ArrayList<Token>();
        expectedOutput.add(new Token(NumberField, "1"));
        expectedOutput.add(new Token(TitleField, "Simple scale"));
        expectedOutput.add(new Token(ComposerField, "Unknown"));
        expectedOutput.add(new Token(MeterField, "4/4"));
        expectedOutput.add(new Token(DefaultLengthField, "1/4"));
        expectedOutput.add(new Token(TempoField, "120"));
        expectedOutput.add(new Token(KeyField, "C"));
        
        expectedOutput.add(new Token(BaseNote, "C"));
        expectedOutput.add(new Token(BaseNote, "D"));
        expectedOutput.add(new Token(BaseNote, "E"));
        expectedOutput.add(new Token(BaseNote, "F"));

        expectedOutput.add(new Token(BarLine));

        expectedOutput.add(new Token(BaseNote, "G"));
        expectedOutput.add(new Token(BaseNote, "A"));
        expectedOutput.add(new Token(BaseNote, "B"));
        expectedOutput.add(new Token(BaseNote, "c"));

        expectedOutput.add(new Token(BarLine));

        expectedOutput.add(new Token(BaseNote, "c"));
        expectedOutput.add(new Token(BaseNote, "B"));
        expectedOutput.add(new Token(BaseNote, "A"));
        expectedOutput.add(new Token(BaseNote, "G"));
        expectedOutput.add(new Token(BaseNote, "F"));
        expectedOutput.add(new Token(BaseNote, "E"));
        expectedOutput.add(new Token(BaseNote, "D"));
        expectedOutput.add(new Token(BaseNote, "C"));

        expectedOutput.add(new Token(BarLine));        
        
        List<Token> tokens = Lexer.lex(input);
        assertEquals(expectedOutput,
                     tokens);
    }
    /**
     * Test piece1: all header fields, basenote, notelengths, barline, tuplet, chord
     * @throws IOException
     */
    @Test
    public void piece1Test() throws IOException {
        List<Token> expectedOutput = new ArrayList<Token>();
        
        expectedOutput.add(new Token(NumberField, "1"));
        expectedOutput.add(new Token(TitleField, "Piece No.1"));
        expectedOutput.add(new Token(MeterField, "4/4"));
        expectedOutput.add(new Token(DefaultLengthField, "1/4"));
        expectedOutput.add(new Token(TempoField, "140"));
        expectedOutput.add(new Token(KeyField, "C"));

        expectedOutput.add(new Token(BaseNote, "C"));
        expectedOutput.add(new Token(BaseNote, "C"));
        expectedOutput.add(new Token(BaseNote, "C"));
        expectedOutput.add(new Token(NoteLength, "3/4"));
        expectedOutput.add(new Token(BaseNote, "D"));
        expectedOutput.add(new Token(NoteLength, "/4"));
        expectedOutput.add(new Token(BaseNote, "E"));

        expectedOutput.add(new Token(BarLine));

        expectedOutput.add(new Token(BaseNote, "E"));
        expectedOutput.add(new Token(NoteLength, "3/4"));
        expectedOutput.add(new Token(BaseNote, "D"));
        expectedOutput.add(new Token(NoteLength, "/4"));
        expectedOutput.add(new Token(BaseNote, "E"));
        expectedOutput.add(new Token(NoteLength, "3/4"));
        expectedOutput.add(new Token(BaseNote, "F"));
        expectedOutput.add(new Token(NoteLength, "/4"));
        expectedOutput.add(new Token(BaseNote, "G"));
        expectedOutput.add(new Token(NoteLength, "2"));

        expectedOutput.add(new Token(BarLine));

        expectedOutput.add(new Token(BeginTriplet));
        expectedOutput.add(new Token(BaseNote, "c"));
        expectedOutput.add(new Token(NoteLength, "/2"));
        expectedOutput.add(new Token(BaseNote, "c"));
        expectedOutput.add(new Token(NoteLength, "/2"));
        expectedOutput.add(new Token(BaseNote, "c"));
        expectedOutput.add(new Token(NoteLength, "/2"));

        expectedOutput.add(new Token(BeginTriplet));
        expectedOutput.add(new Token(BaseNote, "G"));
        expectedOutput.add(new Token(NoteLength, "/2"));
        expectedOutput.add(new Token(BaseNote, "G"));
        expectedOutput.add(new Token(NoteLength, "/2"));
        expectedOutput.add(new Token(BaseNote, "G"));
        expectedOutput.add(new Token(NoteLength, "/2"));

        expectedOutput.add(new Token(BeginTriplet));
        expectedOutput.add(new Token(BaseNote, "E"));
        expectedOutput.add(new Token(NoteLength, "/2"));
        expectedOutput.add(new Token(BaseNote, "E"));
        expectedOutput.add(new Token(NoteLength, "/2"));
        expectedOutput.add(new Token(BaseNote, "E"));
        expectedOutput.add(new Token(NoteLength, "/2"));

        expectedOutput.add(new Token(BeginTriplet));
        expectedOutput.add(new Token(BaseNote, "C"));
        expectedOutput.add(new Token(NoteLength, "/2"));
        expectedOutput.add(new Token(BaseNote, "C"));
        expectedOutput.add(new Token(NoteLength, "/2"));
        expectedOutput.add(new Token(BaseNote, "C"));
        expectedOutput.add(new Token(NoteLength, "/2"));

        expectedOutput.add(new Token(BarLine));
        expectedOutput.add(new Token(BaseNote, "G"));
        expectedOutput.add(new Token(NoteLength, "3/4"));
        expectedOutput.add(new Token(BaseNote, "F"));;
        expectedOutput.add(new Token(NoteLength, "/4"));
        expectedOutput.add(new Token(BaseNote, "E"));
        expectedOutput.add(new Token(NoteLength, "3/4"));
        expectedOutput.add(new Token(BaseNote, "D"));
        expectedOutput.add(new Token(NoteLength, "/4"));
        expectedOutput.add(new Token(BaseNote, "C"));
        expectedOutput.add(new Token(NoteLength, "2"));

        expectedOutput.add(new Token(EndMajorSection));

        String input = readFile("sample_abc/piece1.abc");

        assertEquals(expectedOutput, Lexer.lex(input));
    }
    /**
     * Test the rest of tokens: sharp, flat, octave down and up, repeats, end of major section
     * @throws IOException
     */
    @Test
    public void abcTestLexerTest() throws IOException {
        List<Token> expectedOutput = new ArrayList<Token>();
        String input = "^ _ ' , [1 [2 :| |]";
        //System.out.println(Lexer.lex(input));
        expectedOutput.add(new Token(Sharp));
        expectedOutput.add(new Token(Flat));
        expectedOutput.add(new Token(OctaveUp));
        expectedOutput.add(new Token(OctaveDown));
        expectedOutput.add(new Token(FirstRepeat));
        expectedOutput.add(new Token(SecondRepeat));
        expectedOutput.add(new Token(EndRepeat));
        expectedOutput.add(new Token(EndMajorSection));
        //System.out.println(expectedOutput);
        assertEquals(expectedOutput, Lexer.lex(input));
    }
}

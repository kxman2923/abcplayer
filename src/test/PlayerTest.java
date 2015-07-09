package test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import javax.sound.midi.MidiUnavailableException;

import lexer.Lexer;

import org.junit.*;

import parser.Parser;
import parser.Piece;
import player.Player;

/**
 * Tests the player by playing a few sample pieces to listen to by ear.
 * @category no_didit
 */
public class PlayerTest {
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
     * Plays a piece from the given file name.
     */
    private void playPiece(String fileName) throws IOException, MidiUnavailableException {
        String input = readFile(fileName);
        Parser parser = new Parser(Lexer.lex(input));
        parser.parse();
        Piece piece = parser.getPiece();
        Player player = new Player(piece);
        player.play();
    }

    @Test
    public void scaleTest() throws IOException, MidiUnavailableException {
        playPiece("sample_abc/fur_elise.abc");
    }

    @Test
    public void piece1Test() throws IOException, MidiUnavailableException {
        playPiece("sample_abc/piece1.abc");
    }

    @Test
    public void piece2Test() throws IOException, MidiUnavailableException {
        playPiece("sample_abc/piece2.abc");
    }
    
    /**
     * These 3 abc files are for testing
     * @throws IOException
     * @throws MidiUnavailableException
     */
    		
    @Test
    public void AddingtonHighlandTest() throws IOException, MidiUnavailableException {
        playPiece("sample_abc/AddingtonHighland.abc");
    }
    
    @Test
    public void afterMidnightWaltzTest() throws IOException, MidiUnavailableException {
        playPiece("sample_abc/afterMidnightWaltz.abc");
    }
    
    @Test
    public void LesLignesTest() throws IOException, MidiUnavailableException {
        playPiece("sample_abc/LesLignes.abc");
    }
    
    /**
     * LesLignes_NoVoices has 3 voices in body, but does not specify that in header
     * therefore, parser should throw an error
     * @throws IOException
     * @throws MidiUnavailableException
     */
    @Test
    public void LesLignesNoVoicesTest() throws IOException, MidiUnavailableException {
        playPiece("sample_abc/LesLignes_NoVoices.abc");
    }
}

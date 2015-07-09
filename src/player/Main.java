package player;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import javax.sound.midi.MidiUnavailableException;

import parser.Parser;
import parser.Piece;

import java.util.Arrays;

import lexer.Lexer;

/**
 * Main entry point of your application.
 */
public class Main {

    /**
     * Plays the input file using Java MIDI API and displays
     * header information to the standard output stream.
     * 
     * (Your code should not exit the application abnormally using
     * System.exit().)
     * 
     * @param file the name of input abc file
     */
    public static void play(String file) throws IOException, MidiUnavailableException {
        String input = readFile(file);
        Parser parser = new Parser(Lexer.lex(input));
        parser.parse();
        Piece piece = parser.getPiece();
        String text = "Now playing:\n"
            + "%s by %s\n"
            + "Part: %d\n" 
            + "Tempo: %d BPM\n"
            + "Meter: %s\n"
            + "Default Note Length: %s\n"
            + "Key: %s\n";
        System.out.println(String.format(text,
                                         piece.getTitle(),
                                         piece.getComposer(),
                                         piece.getIndex(),
                                         piece.getTempo(),
                                         piece.getMeter(),
                                         piece.getDefaultLength(),
                                         piece.getKey()));
        Player player = new Player(piece);
        player.play();
    }

    public static void main(String[] args) throws IOException, MidiUnavailableException {
        if (args.length < 1) {
            System.out.println("Please specify an ABC file.");
            return;
        }
        for (String fileName : args) {
            play(fileName);
        }
    }

    private static String readFile(String fileName) throws IOException, MidiUnavailableException {
        String out = "";
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            out = out + currentLine + "\n";
        }
        br.close();
        return out;
    }
}

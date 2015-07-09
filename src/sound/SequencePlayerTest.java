package sound;


import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.Test;

/**
 * Tests our understanding of sequencing and MIDI notes.
 * @category no_didit
 */
public class SequencePlayerTest {
	@Test
	public void testPiece1() throws MidiUnavailableException, InvalidMidiDataException{
		
		SequencePlayer player;

        // Create a new player, with 120 beats (i.e., quarter note) per
        // minute, with 2 tick per quarter note.
        player = new SequencePlayer(100, 12);
        
           

        try {
        	//C C C3/4 D/4 E |
        player.addNote(new Pitch('C').toMidiNote(), 0, 12);
        player.addNote(new Pitch('C').toMidiNote(), 12, 12);
        player.addNote(new Pitch('C').toMidiNote(), 24, 9);
        player.addNote(new Pitch('D').toMidiNote(), 33, 3);
        player.addNote(new Pitch('E').toMidiNote(), 36, 12);       
        	// E3/4 D/4 E3/4 F/4 G2 |
        player.addNote(new Pitch('E').toMidiNote(), 48, 9);        
        player.addNote(new Pitch('D').toMidiNote(), 57, 3);
        player.addNote(new Pitch('E').toMidiNote(), 60, 9);		//transpose(Pitch.OCTAVE).toMidiNote(), 7, 1);
        player.addNote(new Pitch('F').toMidiNote(), 69, 3);
        player.addNote(new Pitch('G').toMidiNote(), 72, 24);
        	// (3c/2c/2c/2 (3G/2G/2G/2 (3E/2E/2E/2 (3C/2C/2C/2 
        player.addNote(new Pitch('C').transpose(Pitch.OCTAVE).toMidiNote(), 96, 4);
        player.addNote(new Pitch('C').transpose(Pitch.OCTAVE).toMidiNote(), 100, 4);
        player.addNote(new Pitch('C').transpose(Pitch.OCTAVE).toMidiNote(), 104, 4);
        player.addNote(new Pitch('G').toMidiNote(), 108, 4);
        player.addNote(new Pitch('G').toMidiNote(), 112, 4);
        player.addNote(new Pitch('G').toMidiNote(), 116, 4);
        player.addNote(new Pitch('E').toMidiNote(), 120, 4);
        player.addNote(new Pitch('E').toMidiNote(), 124, 4);
        player.addNote(new Pitch('E').toMidiNote(), 128, 4);
        player.addNote(new Pitch('C').toMidiNote(), 132, 4);
        player.addNote(new Pitch('C').toMidiNote(), 136, 4);
        player.addNote(new Pitch('C').toMidiNote(), 140, 4);  
        	// G3/4 F/4 E3/4 D/4 C2 
        player.addNote(new Pitch('G').toMidiNote(), 144, 9);
        player.addNote(new Pitch('F').toMidiNote(), 153, 3);
        player.addNote(new Pitch('E').toMidiNote(), 156, 9);
        player.addNote(new Pitch('D').toMidiNote(), 165, 3);
        player.addNote(new Pitch('C').toMidiNote(), 168, 24);
        // play!
        player.play();
		//String result = player.toString();
		//System.out.println(result);
		//assertEquals(result.equals("A"), true);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
	}
	
	
	@Test
	public void testPiece2() throws MidiUnavailableException, InvalidMidiDataException{
		
		SequencePlayer player;

        // Create a new player, with 120 beats (i.e., quarter note) per
        // minute, with 2 tick per quarter note.
        

        try {
        	player = new SequencePlayer(200, 12);
        	
          // [^F/2e/2] [F/2e/2] z/2 [F/2e/2] z/2 [F/2c/2] [Fe] |
        	player.addNote(new Pitch('F').accidentalTranspose(1).toMidiNote(), 0, 6);
            player.addNote(new Pitch('E').transpose(Pitch.OCTAVE).toMidiNote(), 0, 6);
            player.addNote(new Pitch('F').toMidiNote(), 6, 6);
            player.addNote(new Pitch('E').transpose(Pitch.OCTAVE).toMidiNote(), 6, 6);
            player.addNote(new Pitch('F').toMidiNote(), 18, 6);
            player.addNote(new Pitch('E').transpose(Pitch.OCTAVE).toMidiNote(), 18, 6);
            player.addNote(new Pitch('F').toMidiNote(), 30, 6);
            player.addNote(new Pitch('C').transpose(Pitch.OCTAVE).toMidiNote(), 30, 6);
            player.addNote(new Pitch('F').toMidiNote(), 36, 12);
            player.addNote(new Pitch('E').transpose(Pitch.OCTAVE).toMidiNote(), 36, 12);
        	//[Gbg] z G z |
            player.addNote(new Pitch('G').toMidiNote(), 48, 12);
            player.addNote(new Pitch('B').transpose(Pitch.OCTAVE).toMidiNote(), 48, 12);
            player.addNote(new Pitch('G').transpose(Pitch.OCTAVE).toMidiNote(), 48, 12);
            player.addNote(new Pitch('G').toMidiNote(), 72, 12);
        	//c3/2 G/2 z E |
            player.addNote(new Pitch('C').transpose(Pitch.OCTAVE).toMidiNote(), 86, 18);
            player.addNote(new Pitch('G').toMidiNote(), 104, 6);
            player.addNote(new Pitch('E').toMidiNote(), 122, 12);
        	//E/2 a b  _b/2 a |
            player.addNote(new Pitch('E').toMidiNote(), 134, 6);
            player.addNote(new Pitch('A').transpose(Pitch.OCTAVE).toMidiNote(), 140, 12);
            player.addNote(new Pitch('B').transpose(Pitch.OCTAVE).toMidiNote(), 152, 12);
            player.addNote(new Pitch('B').transpose(Pitch.OCTAVE).accidentalTranspose(-1).toMidiNote(), 164, 6);
            player.addNote(new Pitch('A').transpose(Pitch.OCTAVE).toMidiNote(), 170, 12); 	
            //(3Geg a' f/2 g/2 |
            player.addNote(new Pitch('G').toMidiNote(), 182, 8);
            player.addNote(new Pitch('E').transpose(Pitch.OCTAVE).toMidiNote(), 190, 8);
            player.addNote(new Pitch('G').transpose(Pitch.OCTAVE).toMidiNote(), 198, 8);
            player.addNote(new Pitch('A').transpose(Pitch.OCTAVE).transpose(Pitch.OCTAVE).toMidiNote(), 206, 12);
            player.addNote(new Pitch('F').transpose(Pitch.OCTAVE).toMidiNote(), 218, 6);
            player.addNote(new Pitch('G').transpose(Pitch.OCTAVE).toMidiNote(), 224, 6);   
        	//z/2 e c/2 d/2 b3/4 |
            player.addNote(new Pitch('E').transpose(Pitch.OCTAVE).toMidiNote(), 230, 12);
            player.addNote(new Pitch('C').transpose(Pitch.OCTAVE).toMidiNote(), 242, 6);
            player.addNote(new Pitch('D').transpose(Pitch.OCTAVE).toMidiNote(), 248, 6);
            player.addNote(new Pitch('B').transpose(Pitch.OCTAVE).toMidiNote(), 254, 9);
        // play!
        player.play();
		//String result = player.toString();
		//System.out.println(result);
		//assertEquals(result.equals("A"), false);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
	}
}

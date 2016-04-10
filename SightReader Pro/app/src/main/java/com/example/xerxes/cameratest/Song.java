package Song;

import java.util.ArrayList;

class Song {
	private ArrayList<Note> notes;
	private int tempo;
	private char clef;
	
	/**
	The argument input is a String of the form "Q3 Q2 Q5 Q8 HR HR QR Q10 Q11 QR" for example
	
	This constructor will create an ArrayList of Note objects according to input.	
	*/
	public Song(String input) {
        notes = new ArrayList<Note>();							
		tempo = 100;											//default tempo will be 100 bpm
		clef = 'T';												//default clef is T for treble
		
		Note temp;												//to store length of each note temporarily for the song
		String[] input_data = input.split(" ");	
		for (int i=0; i< input_data.length; i++){
			temp = new Note(input_data[i]);				
			notes.add(temp);
		}
    }
	
	public void change_clef(char new_clef) {
		clef = new_clef;
	}
	
	public void change_tempo(int new_tempo) {
		tempo = new_tempo;
	}
	
	
	public String convert_to_midi() {
		String note_text = "";
		
		// File Header
		// Note: Java uses 16 bit unicode chars
		note_text += "MThd";									// File designation
		note_text += (char)0;									// Length of header (first byte)
		note_text += (char)0;									// Length of header (second byte)
		note_text += (char)0;									// Length of header (third byte)
		note_text += (char)6;									// Length of header (last byte)
		note_text += (char)0;									// Format (single track)
		note_text += (char)0;									// Format (single track)
		note_text += (char)0;									// Number of track chunks
		note_text += (char)1;									// Number of track chunks (single track)
		note_text += (char)0;									// Pulses per Quarter note
		note_text += (char)96; 									// Pulses per Quarter note (96 PPQ)
		
		note_text += "MTrk";									// Track designation
		int length = notes.size()*12 + 6;						// On/Off for each note (6B ea) + tempo set (6B)
		note_text += (char)(length>>>24);						// Num track events (first byte)
		note_text += (char)(length<<8>>>24);					// Num track events (second byte)
		note_text += (char)(length<<16>>>24);					// Num track events (third byte)
		note_text += (char)(length&0xFF);						// Num track events (fourth byte)
		
		// First track event is setting tempo
		note_text += (char)0xFF;								// Event type meta event
		note_text += (char)0x51;								// Event type tempo setting
		int ms_tempo = 60000000/tempo;							// Convert BPM to microseconds/beat
		note_text += (char)3;									// Start of tempo set
		note_text += (char)(ms_tempo<<8>>>24);					// Second byte of ms_tempo
		note_text += (char)(ms_tempo<<16>>>24);					// Third byte of ms_tempo
		note_text += (char)(ms_tempo&0xFF);						// Fourth byte of ms_tempo
		
		// Note track events
		for (Note note : notes) {
			note_text += note.get_midi(clef);
		}
		
		note_text += (char)0xFF;								// Event type meta event
		note_text += (char)0x2F;								// End of Track event
		note_text += (char)0;									// End of Track
		
		return note_text;
	}
}

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
		note_text += '\u0000';									// Length of header (first 2 bytes)
		note_text += '\u0006';									// Length of header (second 2 bytes)
		note_text += '\u0000';									// Format (single track)
		note_text += '\u0001';									// Number of track chunks (single track)
		note_text += '\u0060'; 									// Pulses per Quarter note (96 PPQ)
		
		note_text += "MTrk";									// Track designation
		int length = notes.size()*12 + 6;						// On/Off for each note (6B ea) + tempo set (6B)
		note_text += (char)(length>>>16);						// Num track events (first 2 bytes)
		note_text += (char)(length&0xFFFF);						// Num track events (second 2 bytes)
		
		// First track event is setting tempo
		note_text += '\uFF51';									// Event type tempo setting
		int ms_tempo = 60000000/tempo;							// Convert BPM to microseconds/beat
		note_text += (char)((ms_tempo>>16)&0x00FF|0x0300);		// 0x03 and second byte of ms_tempo
		note_text += (char)(ms_tempo&0xFFFF);					// Third and fourth byte of ms_tempo
		
		// Note track events
		for (Note note : notes) {
			note_text += note.get_midi(clef);
		}
		
		note_text += '\uFF2F';						// First two bytes of End of Track
		note_text += '\u0000';						// End of Track
		
		return note_text;
	}
}

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
			temp = new Note(input_data[i], 'C', clef);			//perhaps key and clef don't need to be included in the notes
			notes.add(temp);
		}
    }
	
	public void change_clef(char new_clef) {
		clef = new_clef;
	}
	
	public void change_tempo(int new_tempo) {
		tempo = new_tempo;
	}
	
	
	String convert_to_midi() {
		String note_text = "";
		
		// File Header
		// Note: Java uses 16 bit unicode chars
		note_text += "MThd";									// File designation
		note_text += '\u0000' + '\u0006';						// Length of header
		note_text += '\u0000';									// Format (single track)
		note_text += '\u0001';									// Number of track chunks (single track)
		note_text += '\u0060'; 									// Pulses per Quarter note (96 PPQ)
		
		note_text += "MTrk";									// Track designation
		int length = notes.size()*12 + 6;						// On/Off for each note (6B ea) + tempo set (6B)
		note_text += (char)(length>>>16) + (char)(length&0xFFFF);	// Number of track events (4-byte int to two 2-byte chars)
		
		// First track event is setting tempo
		note_text += '\uFF51';									// Event type tempo setting
		int ms_tempo = 60000000/tempo;							// Convert BPM to microseconds/beat
		note_text += (char)((ms_tempo>>16)&0x00FF|0x0300);		// 0x03 and second byte of ms_tempo
		note_text += (char)(ms_tempo&0xFFFF);					// Third and fourth byte of ms_tempo
		
		// Note track events
		for (Note note : notes) {
			note_text += note.get_midi();
		}
		
		return note_text;
	}
}
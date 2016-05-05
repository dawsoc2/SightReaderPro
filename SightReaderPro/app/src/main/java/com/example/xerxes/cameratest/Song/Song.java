package com.example.xerxes.cameratest.Song;

import java.util.ArrayList;
import com.leff.midi.*;
import com.leff.midi.event.meta.*;

public class Song {
	private ArrayList<Note> notes;
	private int tempo;
	private char clef;
	private int instrument;
	private int octave;
	
	/**
	The argument input is a String of the form "Q3_ Q2_ Q5_ Q8_ HR_ HR_ QR_ Q10_ Q11_ QR_" for example
	 or "CT_T4/4_Q3_ Q2_    Q5_ Q8_HR_HR_QR_ Q10_ Q11_ QR_". Whitespace is variable, beginning stuff is optional. No key signatures.
	
	This constructor will create an ArrayList of Note objects according to input.	
	*/
	public Song(String input) {
        notes = new ArrayList<Note>();							
		tempo = 100;											//default tempo will be 100 bpm
		clef = 'T';												//default clef is T for treble
		instrument = 0;											//default instrument of C
		octave = 0;												//default octave of no transposition
		char[] valid_values = {'S', 'E', 'Q', 'H', 'W'};

		Note temp;											//to store length of each note temporarily for the song
		String temp_input = input.replaceAll("\\s","");	    //temporary string to strip whitespace out.
		String[] input_data = temp_input.split("_");
		for (int i=0; i< input_data.length; i++){
			String next_str = input_data[i];
			if (next_str.charAt(0) == 'C') {
				if (next_str.charAt(1) == 'T' || next_str.charAt(1) == 'B') {
					clef = next_str.charAt(1);
				}
			}
			else if (next_str.charAt(0) == 'T') {/* this might do something eventually. Checks time signature. */}
			else {
				for(int j = 0; j < valid_values.length; j++) {
					if (next_str.charAt(0) == valid_values[j]) {
						temp = new Note(next_str);
						notes.add(temp);
					}
				}
			}
		}
    }

	private int typeToDuration(char note_type) {
		int duration;
        if (note_type == 'S') {
            duration = MidiFile.DEFAULT_RESOLUTION / 4;
        }
		else if (note_type == 'E') {
			duration = MidiFile.DEFAULT_RESOLUTION / 2;
		}
		else if (note_type == 'Q') {
			duration = MidiFile.DEFAULT_RESOLUTION;
		}
		else if (note_type == 'H') {
			duration = MidiFile.DEFAULT_RESOLUTION * 2;
		}
		else if (note_type == 'W') {
			duration = MidiFile.DEFAULT_RESOLUTION * 4;
		}
		else {
			duration = 0;
		}
		return duration;
	}

	public void changeClef(char new_clef) {
		clef = new_clef;
	}
	
	public void changeTempo(int new_tempo) {
		tempo = new_tempo;
	}

	public void changeOctave(int new_octave) {
		octave = new_octave;
	}

	public void changeInstrument(String inst) {instrument = instToInt(inst);}

    //adds accidental "acc" to every note that has the same base value as "val"
    private void addAccidentals(int val, char acc) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).noteValue() % 7 == val) {
                notes.get(i).addAccidental(acc);
            }
        }
    }

    private int keyToInt(String key) {
        switch(key) {
            case "C♭" :
                return -7;
            case "G♭" :
                return -6;
            case "D♭" :
                return -5;
            case "A♭" :
                return -4;
            case "E♭" :
                return -3;
            case "B♭" :
                return -2;
            case "F" :
                return -1;
            case "G" :
                return 1;
            case "D" :
                return 2;
            case "A" :
                return 3;
            case "E" :
                return 4;
            case "B" :
                return 5;
            case "F#" :
                return 6;
            case "C#" :
                return 7;
        }
        return 0;
    }

	int instToInt(String inp) {
		switch(inp) {
			case "D♭" :
				return 1;
			case "D" :
				return 2;
			case "E♭" :
				return 3;
			case "E" :
				return 4;
			case "F" :
				return 5;
			case "G" :
				return 7;
			case "A♭" :
				return -3;
			case "B♭" :
				return -2;
			case "B" :
				return -1;
		}
		return 0;
	}

    // has a whole bunch of key sig strings and does these.
    public void changeKey(String key) {
        //"C♯", "F♯", "B", "E", "A", "D", "G", "C", "F", "B♭", "E♭", "A♭", "D♭" "G♭"
        //Sharps: F, C, G, D, A, E, B
        //Flats: B, E, A, D, G, C, F
        //these are the keys
        int [] sharps = {3, 0, 4, 1, 5, 2, 6};
        int [] flats = {6, 2, 5, 1, 4, 0, 3};
        int [] bass_sharps = {1, 5, 2, 6, 3, 0, 4};
        int [] bass_flats = {4, 0, 3, 6, 2, 5, 1};
		if (clef == 'B') {
            sharps = bass_sharps;
            flats = bass_flats;
        }

        //technically you could just iterate through one list backwards or forwards but I'm lazzzyy.
        if (keyToInt(key) > 0) {          //sharp
            for (int i = 0; i < keyToInt(key); i++) {
                addAccidentals(sharps[i], 's');
            }
        }
        else if (keyToInt(key) < 0) {     //flats
            for (int i = 0; i < -keyToInt(key); i++) {
                addAccidentals(flats[i], 'f');
            }
        }
        // shouldn't need to check for 0. keyToInt() can only return -6 through 6 so no error checking is necessary either
    }
	
	public MidiFile convertToMidi() {
		//initialize some tracks
		MidiTrack tempo_track = new MidiTrack();
		MidiTrack note_track = new MidiTrack();

		//with time signature
		TimeSignature ts = new TimeSignature();
		ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

		//with tempo
		Tempo midi_tempo = new Tempo();
		midi_tempo.setBpm(tempo);

		//insert these values
		tempo_track.insertEvent(ts);
		tempo_track.insertEvent(midi_tempo);

		//keep track of the tick and start adding notes.
		final int NOTE_COUNT = notes.size();
		long tick = 0;
		for (int i = 0; i < NOTE_COUNT; i++) {
			char note_type = notes.get(i).noteType();

			long duration = typeToDuration(note_type);
			int pitch = notes.get(i).staffToValue(clef) + instrument + 12*octave;
			// only actually play a note if there's a value
			//right now the note just plays for its full duration (probably not very natural-sounding)
			if (pitch != -20) {
				int channel = 0;
				int velocity = 100;
				note_track.insertNote(channel, pitch, velocity, tick, duration);
			}
			//always increment the tick so we don't overlap notes
			tick = tick + duration;
		}

		//have to add the tracks to a list to create the MidiFile
		ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
		tracks.add(tempo_track);
		tracks.add(note_track);



		//now create a MidiFile and add them in
		//thank god for DEFAULT_RESOLUTION
		//we leave the writing to a file for a parent class.
		return new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
	}
}

package com.example.xerxes.cameratest.Song;

import java.util.ArrayList;
import com.leff.midi.*;
import com.leff.midi.event.meta.*;

public class Song {
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

	private int type_to_duration(char note_type) {
		int duration;
		if (note_type == 'Q') {
			duration = MidiFile.DEFAULT_RESOLUTION;
		}
		else if (note_type == 'H') {
			duration = MidiFile.DEFAULT_RESOLUTION * 2;
		}
		else {
			duration = 0;
		}
		return duration;
	}

	public void change_clef(char new_clef) {
		clef = new_clef;
	}
	
	public void change_tempo(int new_tempo) {
		tempo = new_tempo;
	}

	
	public MidiFile convert_to_midi() {
		//initialize some tracks
		MidiTrack tempoTrack = new MidiTrack();
		MidiTrack noteTrack = new MidiTrack();

		//with time signature
		TimeSignature ts = new TimeSignature();
		ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

		//with tempo
		Tempo midi_tempo = new Tempo();
		midi_tempo.setBpm(tempo);

		//insert these values
		tempoTrack.insertEvent(ts);
		tempoTrack.insertEvent(midi_tempo);

		//keep track of the tick and start adding notes.
		final int NOTE_COUNT = notes.size();
		long tick = 0;
		for (int i = 0; i < NOTE_COUNT; i++) {
			char note_type = notes.get(i).note_type();

			long duration = type_to_duration(note_type);
			int pitch = notes.get(i).staff_to_value(clef);
			// only actually play a note if there's a value
			//right now the note just plays for its full duration (probably not very natural-sounding)
			if (pitch != -20) {
				int channel = 0;
				int velocity = 100;
				noteTrack.insertNote(channel, pitch, velocity, tick, duration);
			}
			//always increment the tick so we don't overlap notes
			tick = tick + duration;
		}

		//have to add the tracks to a list to create the MidiFile
		ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
		tracks.add(tempoTrack);
		tracks.add(noteTrack);

		//now create a MidiFile and add them in
		MidiFile mf = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
		//thank god for DEFAULT_RESOLUTION

		//we leave the writing to a file for a parent class.
		return mf;
	}
}

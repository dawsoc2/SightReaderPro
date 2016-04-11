package Song;

class Note {
	
	private int staff_position;
	private char note_type;
	
	public Note(String note_properties) {
		note_type = note_properties.charAt(0);
		String temp_position = note_properties.substring(1);
		if (temp_position.equals("R")){
			staff_position = -20;
		}else{
			staff_position = Integer.parseInt(temp_position);
		}
	}
	
	private int staff_to_value(char clef)	{
		if (clef == 'T') {
			int[] tclef =  {24,26,28,29,31,33,35,
					36,38,40,41,43,45,47,
					48,50,52,53,55,57,59,
					60,62,64,65,67,69,71,
					72,74,76,77,79,81,83,
					84,86,88,89,91,93,95,
					96,98,100,101,103,105,107};
			return tclef[staff_position + 21];
		}
		if (clef == 'B') {
			int[] bclef =  { 4, 5, 7, 9,11,12,14,
					16,17,19,21,23,24,26,
					28,29,31,33,35,36,38,
					40,41,43,45,47,48,50,
					52,53,55,57,59,60,62,
					64,65,67,69,71,72,74,
					76,77,79,81,83,84,86};
			return bclef[staff_position + 21];
		}
		return 0;
	}

	public String get_midi(char clef) {
		// The relatively hard part
		// <v_time> + <midi_event>
		// v_time = elapsed time since last event (variable size - we'll do 3 bytes)
		// midi_event = status_byte + data_byte1 + data_byte2
		// status_byte = 8x for off, 9x for on (x = channel number)
		// data1 = key number (MIDI has a standard set of numbers)
		// data2 = note on/off velocity (how "hard" the note is hit)
		
		String note_text = "";
		
		if (staff_position > -20) {								// Note is not a rest
			note_text += (char)0x80;							// Time elapsed (var size)
			note_text += (char)0x80;							// Time elapsed (var size)
			note_text += (char)0x00;							// Time elapsed (var size)
			note_text += (char)0x90;							// Note ON, channel 1 (value 0)
			note_text += (char)(staff_to_value(clef)&0xFF);		// Note num
			note_text += (char)0x40;							// Note velocity
		
			int wait_time = 0;
			// Add to this list when we add more supported note lengths
			if (note_type == 'Q') {								// Quarter note
				wait_time = 96;									// Wait one beat
			} else if (note_type == 'H') {						// Half note
				wait_time = 192;								// Wait 2 beats
			} else if (note_type == 'W') {						// Whole note
				wait_time = 384;								// Wait 4 beats
			}else{
				System.out.println("ERROR: UNRECOGNIZED NOTE TYPE!");
			}

			// Implementation of variable sized numbers (using a consistent 3 bytes)
			note_text += (char) ((char)((wait_time>>14)&0x7F) | 0x80);	// Note length (first byte)
			note_text += (char) ((char)((wait_time>>7)&0x7F) | 0x80);	// Note length (second byte)
			note_text += (char)(wait_time&0x7F);				// Note length (third byte)
			
			note_text += (char)0;
			
			note_text += (char)0x80;							// Note OFF, channel 1
			note_text += (char)(staff_to_value(clef)&0xFF);		// Note num
			note_text += (char)0x40;							// Note velocity
			
			note_text += (char)0;
		}
		
		else {													// Note is a rest
			int wait_time = 0;
			// Add to this list when we add more supported rest lengths
			if (note_type == 'Q') {								// Quarter rest
				wait_time = 96;									// Wait one beat
			} else if (note_type == 'H') {						// Half rest
				wait_time = 192;								// Wait 2 beats
			} else if (note_type == 'W') {						// Whole rest
				wait_time = 384;								// Wait 4 beats
			}else{
				System.out.println("ERROR: UNRECOGNIZED REST TYPE!");
			}
			
			// Implementation of variable sized numbers (using a consistent 3 bytes)
			note_text += (char) ((char)((wait_time>>14)&0x7F) | 0x80);	// Note length(first byte)
			note_text += (char) ((char)((wait_time>>7)&0x7F) | 0x80);	// Note length (second byte)
			note_text += (char)(wait_time&0x7F);				// Note length (third byte)
			
			note_text += (char)0x90;							// Note ON, channel 1
			note_text += (char)0;								// Note num 0
			note_text += (char)0;								// Note velocity 0
			
			note_text += (char)0;
			
			note_text += (char)0x80;							// 0 time elapsed
			note_text += (char)0x80;							// 0 time elapsed
			note_text += (char)0;								// 0 time elapsed
			note_text += (char)0x80;							// Note OFF, channel 1
			note_text += (char)0;								// Note num 0
			note_text += (char)0x7F;							// Note velocity MAX
			
			note_text += (char)0;
		}
		
		return note_text;
	}
	
}

















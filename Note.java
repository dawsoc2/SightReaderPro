package Song;

class Note {
	
	private int staff_position;
	private String note_type;
	private char modifier;
	private char clef;
	
	public Note(int new_staff_position, String new_note_type, char new_modifier, char new_clef) {
		staff_position = new_staff_position;
		note_type = new_note_type;
		modifier = new_modifier;
		clef = new_clef;
	}
	
	public void change_clef(char new_clef) {
		clef = new_clef;
	}
	
	public void change_key(char new_modifier) {
		modifier = new_modifier;
	}
	
	public String get_midi() {
		// The relatively hard part
		// <v_time> + <midi_event>
		// v_time = elapsed time since last event (variable size - we'll do 3 bytes)
		// midi_event = status_byte + data_byte1 + data_byte2
		// status_byte = 8x for off, 9x for on (x = channel number)
		// data1 = key number (MIDI has a standard set of numbers)
		// data2 = note on/off velocity (how "hard" the note is hit)
		
		String note_text = "";
		
		if (note_type.charAt(1) == 'n') {								// Note is not a rest
			note_text += '\u8080';								// 0 time elapsed (first 2 bytes)
			note_text += '\u0090';								// 0 time elapsed; note ON, channel 1
			note_text += (char)((staff_position<<8)&0xFF00 + 0x40);	// note num and velocity (use 40)
		
			int wait_time = 0;
			// Add to this list when we add more supported note lengths
			if (note_type.equals("qn")) {							// Quarter note
				wait_time = 96;									// Wait one beat
			} else if (note_type.equals("hn")) {						// Half note
				wait_time = 192;								// Wait 2 beats
			} else if (note_type.equals("wn")) {						// Whole note
				wait_time = 384;								// Wait 4 beats
			}

                        // fuck variable size numbers
                        char w1 = (char) ((char)((wait_time>>14)&0x7F) | 0x80);
                        char w2 = (char) ((char)((wait_time>>7)&0x7F) | 0x80);
                        char w3 = (char)(wait_time&0x7F);
                        
                        note_text += (char)(w1<<8 + w2);                        // wait_time elapsed (first 2 bytes)
                        note_text += (char)(w3<<8 + 0x90);                      // wait_time elapsed; note ON, channel 1
			note_text += (char)((staff_position<<8)&0xFF00 + 0x40);	// note num and velocity (40)
		}
		
		else {													// Note is a rest
			int wait_time = 0;
			// Add to this list when we add more supported rest lengths
			if (note_type.equals("qr")) {							// Quarter rest
				wait_time = 96;									// Wait one beat
			} else if (note_type.equals("hr")) {						// Half rest
				wait_time = 192;								// Wait 2 beats
			} else if (note_type.equals("wr")) {						// Whole rest
				wait_time = 384;								// Wait 4 beats
			}
			
                        // fuck variable size numbers
                        char w1 = (char) ((char)((wait_time>>14)&0x7F) | 0x80);
                        char w2 = (char) ((char)((wait_time>>7)&0x7F) | 0x80);
                        char w3 = (char)(wait_time&0x7F);
                        
                        note_text += (char)(w1<<8 + w2);                        // wait_time elapsed (first 2 bytes)
                        note_text += (char)(w3<<8 + 0x90);                      // wait_time elapsed; note ON, channel 1
                        note_text += '\u0040';								// note num (0) and velocity (40)
			
			note_text += '\u0000';								// 0 time elapsed (first 2 bytes)
			note_text += '\u0080';								// 0 time elapsed; note OFF, channel 1
			note_text += '\u0040';								// note num (0) and velocity (40)
		}
		
		return note_text;
	}
	
}

















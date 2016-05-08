package com.example.xerxes.cameratest.Song;

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

	//note_type() and staffToValue(char) are public now because the note class can't
	//actually do any of the midi creation :(. Or at least it would be very very difficult
	public char note_type() { return note_type; }

	public int staffToValue(char clef)	{
		if (clef == 'T') {
			int[] tclef =  {24,26,28,29,31,33,35,
					36,38,40,41,43,45,47,
					48,50,52,53,55,57,59,
					60,62,64,65,67,69,71,
					72,74,76,77,79,81,83,
					84,86,88,89,91,93,95,
					96,98,100,101,103,105,107};
			if (staff_position == -20) {return staff_position;}
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
			if (staff_position == -20) {return staff_position;}
			return bclef[staff_position + 21];
		}
		return 0;
	}
	
}

















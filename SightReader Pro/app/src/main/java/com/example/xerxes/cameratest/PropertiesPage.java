package com.example.xerxes.cameratest;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.util.*;
import com.leff.midi.*;
import com.leff.midi.util.*;
import com.leff.midi.event.*;
import com.leff.midi.event.meta.*;

public class PropertiesPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_page);

        //set variables
        ImageView photoDisplay = (ImageView)findViewById(R.id.prop_imageView);
        final MediaPlayer mp = new MediaPlayer();
        //create clef spinner
        String clefArray[] = {"Treble", "Bass"};
        final Spinner spinnerClef = (Spinner) findViewById(R.id.spinnerClef);
        ArrayAdapter<String> clefsSpinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, clefArray);
        clefsSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinnerClef.setAdapter(clefsSpinnerArrayAdapter);
        //spinnerClef.setSelection(getIndex(spinnerClef, "Treble"));
        int clefSpinnerPosition = clefsSpinnerArrayAdapter.getPosition("Treble");
        spinnerClef.setSelection(clefSpinnerPosition);

        //create key signature spinner
        String keySigArray[] = {"B/C♭", "E", "A", "D", "G", "C", "F", "B♭", "E♭", "A♭", "D♭/C♯", "G♭/F♯"};
        final Spinner spinnerKeySig = (Spinner) findViewById(R.id.spinnerKeySignature);
        ArrayAdapter<String> keySigSpinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, keySigArray);
        keySigSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinnerKeySig.setAdapter(keySigSpinnerArrayAdapter);
        int keySigSpinnerPosition = keySigSpinnerArrayAdapter.getPosition("C");
        spinnerKeySig.setSelection(keySigSpinnerPosition);

        //create instrument key spinner
        String instKeyArray[] = {"B/C♭", "E", "A", "D", "G", "C", "F", "B♭", "E♭", "A♭", "D♭/C♯", "G♭/F♯"};
        final Spinner spinnerInst = (Spinner) findViewById(R.id.spinnerInstrument);
        ArrayAdapter<String> instKeySpinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, instKeyArray);
        instKeySpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinnerInst.setAdapter(instKeySpinnerArrayAdapter);
        int instKeySpinnerPosition = instKeySpinnerArrayAdapter.getPosition("C");
        spinnerInst.setSelection(instKeySpinnerPosition);

        //
        final EditText tempoEditText = (EditText)findViewById(R.id.editTempo);
        tempoEditText.setText("120");

        //create process button that will read data from the spinners
        final Button procButton = (Button) findViewById(R.id.processButton);
        procButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String clefVal = spinnerClef.getSelectedItem().toString();
                String keyVal = spinnerKeySig.getSelectedItem().toString();
                String instVal = spinnerInst.getSelectedItem().toString();
                int text_tempo = Integer.parseInt(tempoEditText.getText().toString());

                //let's actually take that tempo and create a test midi file.
                // 1. Create some MidiTracks
                MidiTrack tempoTrack = new MidiTrack();
                MidiTrack noteTrack = new MidiTrack();

// 2. Add events to the tracks
// Track 0 is the tempo map
                TimeSignature ts = new TimeSignature();
                ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

                Tempo tempo = new Tempo();
                tempo.setBpm(text_tempo);

                tempoTrack.insertEvent(ts);
                tempoTrack.insertEvent(tempo);

// Track 1 will have some notes in it
                final int NOTE_COUNT = 40;

                for (int i = 0; i < NOTE_COUNT; i++) {
                    int channel = 0;
                    int pitch = 1 + i;
                    int velocity = 100;
                    long tick = i * 480;
                    long duration = 120;

                    noteTrack.insertNote(channel, pitch, velocity, tick, duration);
                }

// 3. Create a MidiFile with the tracks we created
                ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
                tracks.add(tempoTrack);
                tracks.add(noteTrack);

                MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);

// 4. Write the MIDI data to a file
                File output = new File("sdcard/exampleout.mid");
                try {
                    midi.writeToFile(output);
                } catch (IOException e) {
                    System.err.println(e);
                }


                try {
                    mp.reset();
                    mp.setDataSource("sdcard/exampleout.mid");
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                }
            }
        });

        //creates back button that will take it back to the first page
        final Button backButton = (Button) findViewById(R.id.prop_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(PropertiesPage.this, MainActivity.class));
            }
        });


                //dummy intent just to get variables passed
                Intent intent = getIntent();
                String imagePath = intent.getStringExtra("imagePath");

                //load bitmap
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

                //display bitmap
                photoDisplay.setImageBitmap(bitmap);
            }
        }

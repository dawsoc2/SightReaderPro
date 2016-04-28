package com.example.xerxes.cameratest;

//android imports
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.text.InputFilter;

//java imports
import java.io.File;
import java.io.IOException;
import java.util.*;

//android-midi imports
import com.googlecode.tesseract.android.TessBaseAPI;
import com.leff.midi.*;
import com.leff.midi.util.*;
import com.leff.midi.event.*;
import com.leff.midi.event.meta.*;

//song imports
import com.example.xerxes.cameratest.Song.*;

//misc. imports
import com.example.xerxes.cameratest.InputFilterMinMax;
import com.example.xerxes.cameratest.BlobCut;

public class PropertiesPage extends AppCompatActivity {

    //WHY IS THERE SO MUCH YELLOW IN THIS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_page);

        //dummy intent just to get variables passed
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");

        //load bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        final Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

        //set variables
        final ImageView photoDisplay = (ImageView)findViewById(R.id.prop_imageView);
        final MediaPlayer mp = new MediaPlayer();
        final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/CameraTest/";
        final String lang = "mus";


        //display bitmap
        photoDisplay.setImageBitmap(bitmap);

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

        //create tempo text box
        final EditText tempoEditText = (EditText)findViewById(R.id.editTempo);
        tempoEditText.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "1000")});
        tempoEditText.setText("120");

        final EditText songEditText = (EditText)findViewById(R.id.editSong);

        //create process button that will read data from the spinners
        final Button procButton = (Button) findViewById(R.id.processButton);
        procButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String clefVal = spinnerClef.getSelectedItem().toString();
                String keyVal = spinnerKeySig.getSelectedItem().toString();
                String instVal = spinnerInst.getSelectedItem().toString();
                String songVal = songEditText.getText().toString();
                int text_tempo = Integer.parseInt(tempoEditText.getText().toString());

                //let's actually take that tempo and song and create a test midi file

                //now create a Song object
                Song user_song = new Song(songVal);
                user_song.change_tempo(text_tempo);

                //force that object into providing a MidiFile for us.

                MidiFile midi = user_song.convert_to_midi();

                // Write the MIDI data to a file
                File output = new File("sdcard/SightReaderPro_song.mid");
                try {
                    midi.writeToFile(output);
                } catch (IOException e) {
                    System.err.println(e);
                }


                try {
                    mp.reset();
                    mp.setDataSource("sdcard/SightReaderPro_song.mid");
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                }
            }
        });

        final Button maryButton = (Button) findViewById(R.id.maryButton);
        maryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int text_tempo = Integer.parseInt(tempoEditText.getText().toString());
                String songVal = songEditText.getText().toString();
                songEditText.setText(songVal.replaceAll("\\s",""));

                //blob the bitmap
                BlobCut blobber = new BlobCut();
                Bitmap blobbed_bitmap = blobber.blob_cut(bitmap);
                photoDisplay.setImageBitmap(blobbed_bitmap);

                TessBaseAPI baseApi = new TessBaseAPI();
                baseApi.setDebug(true);
                baseApi.init(DATA_PATH, lang);
                baseApi.setImage(bitmap);

                String recognizedText = baseApi.getUTF8Text();

                baseApi.end();
/*

                //let's actually take that tempo and create a test midi file.
                //first we need a song. Let's use "Mary had a lamb"
                String mary = "Q2_ Q1_ Q0_ Q1_ Q2_ Q2_ Q2_ QR_ Q1_ Q1_ Q1_ QR_ Q2_ Q4_ H4_ Q2_ Q1_ Q0_ Q1_ Q2_ Q2_ Q2_ Q2_ Q1_ Q1_ Q2_ Q1_ H0_ HR_"; //pretty sure this is right.

                //now create a Song object
                Song mary_song = new Song(mary);
                mary_song.change_tempo(text_tempo);

                //force that object into providing a MidiFile for us.

                MidiFile midi = mary_song.convert_to_midi();

                // Write the MIDI data to a file
                File output = new File("sdcard/mary_had_a_little_lamb.mid");
                try {
                    midi.writeToFile(output);
                } catch (IOException e) {
                    System.err.println(e);
                }


                try {
                    mp.reset();
                    mp.setDataSource("sdcard/mary_had_a_little_lamb.mid");
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                }
*/
            }
        });

        //creates back button that will take it back to the first page
        final Button backButton = (Button) findViewById(R.id.prop_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(PropertiesPage.this, MainActivity.class));
            }
        });



            }
        }


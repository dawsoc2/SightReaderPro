package com.example.xerxes.cameratest;

//android imports
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.text.InputFilter;

//java imports
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/SightReaderPro/";
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

                String TAG = "SightReaderPro";
                String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

                for (String path : paths) {
                    File dir = new File(path);
                    if (!dir.exists()) {
                        if (!dir.mkdirs()) {
                            Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                            return;
                        } else {
                            Log.v(TAG, "Created directory " + path + " on sdcard");
                        }
                    }

                }

                // lang.traineddata file with the app (in assets folder)
                // You can get them at:
                // http://code.google.com/p/tesseract-ocr/downloads/list
                // This area needs work and optimization
                if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
                    try {

                        AssetManager assetManager = getAssets();
                        InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                        //GZIPInputStream gin = new GZIPInputStream(in);
                        OutputStream out = new FileOutputStream(DATA_PATH
                                + "tessdata/" + lang + ".traineddata");

                        // Transfer bytes from in to out
                        byte[] buf = new byte[1024];
                        int len;
                        //while ((lenf = gin.read(buff)) > 0) {
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        in.close();
                        //gin.close();
                        out.close();

                        Log.v(TAG, "Copied " + lang + " traineddata");
                    } catch (IOException e) {
                        Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
                    }
                }


                // This is the code for using tesseract, according to some random sample code that I found..
                //Technically this should be able to be copy-pasta'd anywhere because it relies solely on final variables.
                //No idea if it works because I wrote the whole thing while doing stress tests of the blob cutter :(

                TessBaseAPI baseApi = new TessBaseAPI();            // tesseract object
                baseApi.setDebug(true);
                baseApi.init(DATA_PATH, lang);                      //DATA_PATH = our app. lang = "mus".
                baseApi.setImage(bitmap);                           //loads bitmap into the object

                String recognizedText = baseApi.getUTF8Text();      //get that sweet sweet text

                baseApi.end();                                      //close the object

                //generate a new song
                Song image_song = new Song(recognizedText);
                image_song.change_tempo(text_tempo);                //set tempo, pretty standard

                Log.v(TAG, "STRING: '" + recognizedText + "'\n");
                songEditText.setText(recognizedText.replaceAll("\\s", ""));

                //force the song to give us a midi file
                MidiFile midi = image_song.convert_to_midi();

                ///write that midi file to the sdcard (this will not work on all phones but it works on mine!
                File output = new File("sdcard/SightReaderPro_song.mid");
                try {
                    midi.writeToFile(output);
                } catch (IOException e) {
                    System.err.println(e);
                }

                //play the file.
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
                songEditText.setText(songVal.replaceAll("\\s", ""));

                //blob the bitmap
                BlobCut blobber = new BlobCut();
                Bitmap blobbed_bitmap = blobber.blob_cut(bitmap);
                photoDisplay.setImageBitmap(blobbed_bitmap);
                /*

                */
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


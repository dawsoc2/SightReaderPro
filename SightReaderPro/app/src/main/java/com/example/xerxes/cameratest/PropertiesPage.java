package com.example.xerxes.cameratest;

//android imports
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
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

//android-midi imports
import com.leff.midi.*;

//song imports
import com.example.xerxes.cameratest.Song.*;

//misc. imports


public class PropertiesPage extends AppCompatActivity {

    //WHY IS THERE SO MUCH YELLOW IN THIS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_page);

        String arabesque = "Sn5_ Sn6_ Sn7_ Sn6_ En5_ EnR_ Sn5_ Sn6_ Sn7_ Sn8_ En9_ EnR_ Sn8_ Sn9_ Sn10_ Sn11_ En12_ EnR_ Sn12_ Sn13_ Sn14_ Sn15_ En16_ EnR_" +
                               "EnR_ En9_ En9_ En10_ En8_ EnR_ Qn8_ EnR_ En11_ En8_ En9_ En7_ EnR_ Qn9_";

        //http://assets.sheetmusicplus.com/product/Look-Inside/large/2878334_01.jpg
        final String songVal = "Sn16_ Ss15_ Sn15_ Ss14_ Sn15_ Ss14_ Sn14_ Sn13_ Sn14_ Sn13_ Sf13_ Sn12_ Ss11_ Sn11_ Ss10_ Sn10_" +
                               "Sn9_ Ss8_ Sn8_ Ss7_ Sn8_ Ss7_ Sn7_ Sn6_ Sn7_ Sn6_ Sf6_ Sn5_ Ss4_ Sn4_ Ss3_ Sn3_" +
                               "Sn2_ Ss1_ Sn1_ S#0_ Sn0_ Sn-1_ Sn2_ Ss1_ Sn1_ Ss0_ Sn1_ Ss0_ Sn0_ Sn-1_" +
                               "Sn2_ Ss1_ Sn1_ Ss0_ Sn0_ Sn3_ Sn2_ Ss1_ Sn2_ Ss1_ Sn1_ Ss0_ Sn0_ Sn1_ Ss1_";
        //set variables
        ImageView photoDisplay = (ImageView)findViewById(R.id.prop_imageView);
        final MediaPlayer mp = new MediaPlayer();

        //dummy intent just to get variables passed
        Intent intent = getIntent();
        final String imagePath = intent.getStringExtra("imagePath");

        //load bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        final Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

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
        String keySigArray[] = {"C♯", "F♯", "B", "E", "A", "D", "G", "C", "F", "B♭", "E♭", "A♭", "D♭", "G♭", "C♭"};
        final Spinner spinnerKeySig = (Spinner) findViewById(R.id.spinnerKeySignature);
        ArrayAdapter<String> keySigSpinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, keySigArray);
        keySigSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinnerKeySig.setAdapter(keySigSpinnerArrayAdapter);
        int keySigSpinnerPosition = keySigSpinnerArrayAdapter.getPosition("C");
        spinnerKeySig.setSelection(keySigSpinnerPosition);

        //create instrument key spinner
        String instKeyArray[] = {"A♭", "B♭", "B", "C", "D♭", "D", "E♭", "E", "F", "G"};
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

        //create process button that will read data from the spinners
        final Button procButton = (Button) findViewById(R.id.processButton);
        procButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String tempClefVal = spinnerClef.getSelectedItem().toString();
                char clefVal;
                String keyVal = spinnerKeySig.getSelectedItem().toString();
                String instVal = spinnerInst.getSelectedItem().toString();
                int text_tempo = Integer.parseInt(tempoEditText.getText().toString());

                if (tempClefVal == "Treble") {
                    clefVal = 'T';
                } else {
                    clefVal = 'B';
                }

                //let's actually take that tempo and song and create a test midi file

                //now create a Song object
                Song user_song = new Song(songVal);
                user_song.changeTempo(text_tempo);
                user_song.changeClef(clefVal);
                user_song.changeInstrument(instVal);
                user_song.changeKey(keyVal);

                //force that object into providing a MidiFile for us.

                MidiFile midi = user_song.convertToMidi();

                // Write the MIDI data to a file
                File output = new File("sdcard/SightReaderPro_song.mid");
                try {
                    midi.writeToFile(output);
                } catch (IOException e) {
                    System.err.println(e);
                }

                //create intent to start PlayPage
                Intent toPlayPage = new Intent(PropertiesPage.this, PlayPage.class);
                toPlayPage.putExtra("mpPath", "sdcard/SightReaderPro_song.mid");
                toPlayPage.putExtra("imagePath", imagePath);

                //send us off to the play page
                startActivity(toPlayPage);
            }
        });


        //creates back button that will take it back to the first page
        final Button backButton = (Button) findViewById(R.id.prop_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bitmap.recycle();
                startActivity(new Intent(PropertiesPage.this, MainActivity.class));
            }
        });


            }
        }


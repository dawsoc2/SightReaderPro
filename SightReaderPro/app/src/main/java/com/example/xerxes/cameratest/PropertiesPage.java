package com.example.xerxes.cameratest;

//android imports
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
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

//android-midi imports
import com.googlecode.tesseract.android.TessBaseAPI;
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

        //dummy intent just to get variables passed
        Intent intent = getIntent();
        String image_path = intent.getStringExtra("image_path");

        //load bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        final Bitmap bitmap = BitmapFactory.decodeFile(image_path, options);

        //set variables
        final ImageView photoDisplay = (ImageView)findViewById(R.id.prop_imageView);
        final MediaPlayer mp = new MediaPlayer();
        final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/SightReaderPro/";
        final String lang = "mus";


        //display bitmap
        photoDisplay.setImageBitmap(bitmap);

        //create clef spinner
        String clefArray[] = {"Treble", "Bass"};
        final Spinner spinner_clef = (Spinner) findViewById(R.id.spinnerClef);
        ArrayAdapter<String> clefs_spinner_array_adapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, clefArray);
        clefs_spinner_array_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_clef.setAdapter(clefs_spinner_array_adapter);
        //spinner_clef.setSelection(getIndex(spinner_clef, "Treble"));
        int clef_spinner_position = clefs_spinner_array_adapter.getPosition("Treble");
        spinner_clef.setSelection(clef_spinner_position);

        //create key signature spinner
        String keySigArray[] = {"B/C♭", "E", "A", "D", "G", "C", "F", "B♭", "E♭", "A♭", "D♭/C♯", "G♭/F♯"};
        final Spinner spinner_key_sig = (Spinner) findViewById(R.id.spinnerKeySignature);
        ArrayAdapter<String> key_sig_spinner_array_adapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, keySigArray);
        key_sig_spinner_array_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_key_sig.setAdapter(key_sig_spinner_array_adapter);
        int key_sig_spinner_position = key_sig_spinner_array_adapter.getPosition("C");
        spinner_key_sig.setSelection(key_sig_spinner_position);

        //create instrument key spinner
        String instKeyArray[] = {"B/C♭", "E", "A", "D", "G", "C", "F", "B♭", "E♭", "A♭", "D♭/C♯", "G♭/F♯"};
        final Spinner spinner_inst = (Spinner) findViewById(R.id.spinnerInstrument);
        ArrayAdapter<String> inst_key_spinner_array_adapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, instKeyArray);
        inst_key_spinner_array_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_inst.setAdapter(inst_key_spinner_array_adapter);
        int inst_key_spinner_position = inst_key_spinner_array_adapter.getPosition("C");
        spinner_inst.setSelection(inst_key_spinner_position);

        //create tempo text box
        final EditText tempo_edit_text = (EditText)findViewById(R.id.editTempo);
        tempo_edit_text.setFilters(new InputFilter[]{new InputFilterMinMax("1", "1000")});
        tempo_edit_text.setText("120");

        final EditText song_edit_text = (EditText)findViewById(R.id.editSong);

        //create process button that will read data from the spinners
        final Button proc_button = (Button) findViewById(R.id.processButton);
        proc_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String clefVal = spinner_clef.getSelectedItem().toString();
                String keyVal = spinner_key_sig.getSelectedItem().toString();
                String instVal = spinner_inst.getSelectedItem().toString();
                String songVal = song_edit_text.getText().toString();
                int text_tempo = Integer.parseInt(tempo_edit_text.getText().toString());

                //blob the bitmap
                BlobCut blobber = new BlobCut();
                Bitmap blobbed_bitmap = blobber.blob_cut(bitmap);

                String TAG = "SightReaderPro";
                String[] paths = new String[]{DATA_PATH, DATA_PATH + "tessdata/"};

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

                        AssetManager asset_manager = getAssets();
                        InputStream in = asset_manager.open("tessdata/" + lang + ".traineddata");
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
                baseApi.setImage(blobbed_bitmap);                           //loads bitmap into the object

                String recognized_text = baseApi.getUTF8Text();      //get that sweet sweet text

                baseApi.end();                                      //close the object

                //generate a new song
                Song image_song = new Song(recognized_text);
                image_song.change_tempo(text_tempo);                //set tempo, pretty standard

                Log.v(TAG, "STRING: '" + recognized_text + "'\n");
                song_edit_text.setText(recognized_text.replaceAll("\\s", ""));

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

        //creates back button that will take it back to the first page
        final Button back_button = (Button) findViewById(R.id.prop_back_btn);
        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(PropertiesPage.this, MainActivity.class));
            }
        });



            }
        }


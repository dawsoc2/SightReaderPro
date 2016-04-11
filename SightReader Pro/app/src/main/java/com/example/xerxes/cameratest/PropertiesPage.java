package com.example.xerxes.cameratest;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class PropertiesPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_page);

        //set variables
        ImageView photoDisplay = (ImageView)findViewById(R.id.prop_imageView);
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
                int tempo = Integer.parseInt(tempoEditText.getText().toString());
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

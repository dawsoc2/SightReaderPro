package com.example.xerxes.cameratest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PlayPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //dummy intent just to get variables passed
        Intent intent = getIntent();
        final String mp_string = intent.getStringExtra("mpPath");
        final String view_string = intent.getStringExtra("imagePath");

        //create media player
        final MediaPlayer mp = new MediaPlayer();
        try {
            mp.reset();
            mp.setDataSource(mp_string);
            mp.prepare();
        } catch (Exception e) {
        }

        //set image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        final Bitmap bitmap = BitmapFactory.decodeFile(view_string, options);

        //display bitmap
        final ImageView photoDisplay = (ImageView) findViewById(R.id.play_page_view);
        photoDisplay.setImageBitmap(bitmap);

        final Button backButton = (Button) findViewById(R.id.back_btn_play);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bitmap.recycle();
                startActivity(new Intent(PlayPage.this, MainActivity.class));
            }
        });

        final Button propButton = (Button) findViewById(R.id.back_to_prop);
        propButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bitmap.recycle();
                Intent toPropertiesPage = new Intent(PlayPage.this, PropertiesPage.class);
                toPropertiesPage.putExtra("imagePath", view_string);
                startActivity(toPropertiesPage);
            }
        });

        final Button playButton = (Button) findViewById(R.id.play_btn);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mp.start();
            }
        });

        final Button pauseButton = (Button) findViewById(R.id.pause_btn);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    mp.pause();
                }
            }
        });

        final Button stopButton = (Button) findViewById(R.id.stop_btn);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mp.stop();
                try {
                    mp.prepare();
                }
                catch (Exception e) {}
            }
        });



    }


}

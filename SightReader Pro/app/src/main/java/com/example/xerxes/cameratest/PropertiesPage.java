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
import android.widget.ImageView;

public class PropertiesPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_page);

        //set variables
        ImageView photoDisplay = (ImageView)findViewById(R.id.prop_imageView);

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

    //go back to the first page
    public void btnBack(View v) {
        startActivity(new Intent(PropertiesPage.this, MainActivity.class));
    }
}

package com.example.xerxes.cameratest;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST = 10;
    public static final int PICKER_REQUEST = 11;
    private ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        photoView = (ImageView) findViewById(R.id.photoImageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Function for the "take photo" button.
    public void btnTakePhoto(View v) {
        //Intent to take a photo with the camera
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //actually take the photo
        startActivityForResult(camera_intent, CAMERA_REQUEST);
    }

    //Function for the "Pick Photo" button
    public void btnSelectPhoto(View v) {
        //intent to "pick" something
        Intent select_intent = new Intent (Intent.ACTION_PICK);
        //we're picking an image.
        select_intent.setType("image/*");
        startActivityForResult(select_intent, PICKER_REQUEST);
    }

    public void btnTestProp(View v) {
        startActivity(new Intent(MainActivity.this, PropertiesPage.class));
    }


    @Override
    //override "onActivityRequest" to do something special with the "startActivity"
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent to_properties_page = new Intent(MainActivity.this, PropertiesPage.class);
        if (resultCode == RESULT_OK) {
            //"take photo". Gets a bitmap directly from the camera.
            if (requestCode == CAMERA_REQUEST) {
                Bitmap camera_image = (Bitmap)data.getExtras().get("data");
                Uri temp_uri = getImageUri(getApplicationContext(), camera_image);
                String image_path = getRealPathFromURI(temp_uri);
                to_properties_page.putExtra("image_path", image_path);
                startActivity(to_properties_page);
            }
            //"pick photo" Gets a file path that we have to extract a bitmap from.
            if (requestCode == PICKER_REQUEST) {
                Uri pickerImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(pickerImage, filePath, null, null, null);
                cursor.moveToFirst();
                String image_path = cursor.getString(cursor.getColumnIndex(filePath[0]));
                to_properties_page.putExtra("image_path", image_path);
                startActivity(to_properties_page);
                cursor.close();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String output = cursor.getString(idx);
        cursor.close();
        return output;
    }
}

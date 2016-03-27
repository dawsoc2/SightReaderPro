package com.example.xerxes.cameratest;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

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
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //actually take the photo
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    //Function for the "Pick Photo" button
    public void btnSelectPhoto(View v) {
        //intent to "pick" something
        Intent selectIntent = new Intent (Intent.ACTION_PICK);
        //we're picking an image.
        selectIntent.setType("image/*");
        startActivityForResult(selectIntent, PICKER_REQUEST);
    }

    @Override
    //override "onActivityRequest" to do something special with the "startActivity"
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //"take photo". Gets a bitmap directly from the camera.
            if (requestCode == CAMERA_REQUEST) {
                Bitmap cameraImage = (Bitmap)data.getExtras().get("data");
                photoView.setImageBitmap(cameraImage);
            }
            //"pick photo" Gets a file path that we have to extract a bitmap from.
            if (requestCode == PICKER_REQUEST) {
                Uri pickerImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(pickerImage, filePath, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                photoView.setImageBitmap(bitmap);
                cursor.close();
            }
        }
    }
}

package com.jebora.jebora;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class PhotoGridView extends ActionBarActivity {

    final int TAKE_PHOTO_MODE = 1000;
    final int SELECT_PHOTO_MODE = 1001;

    ImageView viewImage;
    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_grid_view);

        b = (Button) findViewById(R.id.buttonToChoosePhoto);
        viewImage = (ImageView) findViewById(R.id.viewImage);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
    }

    private void selectPhoto() {
        final CharSequence[] options = {
                "Take Photo",
                "Choose from Gallery",
                "Cancel"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoGridView.this);
        builder.setTitle("Add photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, TAKE_PHOTO_MODE);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent i;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                        i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        i.setType("image/*");
                        startActivityForResult(i, SELECT_PHOTO_MODE);
                    } else {
                        i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, SELECT_PHOTO_MODE);
                    }
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_grid_view, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) return;

        if(requestCode == TAKE_PHOTO_MODE){
            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp: f.listFiles())
            {
                if(temp.getName().equals("temp.jpg"))
                {
                    f = temp;
                    break;
                }
            }

            try {
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);

                viewImage.setImageBitmap(bitmap);

                String path = android.os.Environment.getExternalStorageState() + File.separator + "Phoenix" +
                        File.separator + "default";

                f.delete();

                OutputStream outFile;
                File file = new File(path, String.valueOf(System.currentTimeMillis() + ".jpg"));
                try{
                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (Exception e){
                    e.printStackTrace();
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(requestCode == SELECT_PHOTO_MODE){
            Uri origUri = data.getData();
            Bitmap bitmap = null;
            try{
                bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), origUri);
            } catch (Exception e){
                e.printStackTrace();
            }
            viewImage.setImageBitmap(bitmap);
        }
    }
}

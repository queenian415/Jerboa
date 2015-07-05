package com.jebora.jebora;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class ImageEditing extends ActionBarActivity {

    //public static native int[] ImgFun(int[] buf, int w, int h);
    static {
        System.loadLibrary("hello");
    }
    public native int[] hello(int[] buf, int w, int h, double alpha, int beta);
    //public native int[] contrast(int[] buf, int w, int h);

    ImageView imageView;
    Button btnNDK, btnRestore;
    SeekBar contrastBar, brightnessBar;
    TextView contrastText, brightnessText;
    int contrastBarVal = 0, brightnessBarVal = 0;
    double alpha = 1.5;
    int beta = 33;
    int w, h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editing);

        this.setTitle("使用NDK转换灰度图");

        imageView = (ImageView) this.findViewById(R.id.ImageView01);
        contrastBar = (SeekBar) this.findViewById(R.id.contrastBar);
        brightnessBar = (SeekBar) this.findViewById(R.id.brightnessBar);
        contrastText = (TextView) this.findViewById(R.id.contrastText);
        contrastText.setText("Contrast : " + contrastBar.getProgress() + " / " + contrastBar.getMax());
        brightnessText = (TextView) this.findViewById(R.id.brightnessText);
        brightnessText.setText("Brightness : " + brightnessBar.getProgress() + " / " + brightnessBar.getMax());

        contrastBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                contrastBarVal = progress;
                alpha = (((double)contrastBarVal) / contrastBar.getMax()) * 2.0 + 1.0;
                contrastText.setText("Contrast : " + progress + " / " + contrastBar.getMax());
                Toast.makeText(ImageEditing.this, "Contrast changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(ImageEditing.this, "Contrast start tracking", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                contrastText.setText("Contrast : " + contrastBarVal + " / " + contrastBar.getMax());
            }
        });

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                beta = progress;
                brightnessText.setText("Brightness : " + progress + " / " + brightnessBar.getMax());
                Toast.makeText(ImageEditing.this, "Contrast changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(ImageEditing.this, "Brightness start tracking", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                brightnessText.setText("Brightness : " + beta + " / " + brightnessBar.getMax());
            }
        });

        Bitmap img1 = ((BitmapDrawable) getResources().getDrawable(
                R.drawable.logo)).getBitmap();

        w = img1.getWidth();
        h = img1.getHeight();

        imageView.setImageBitmap(img1);

        btnRestore = (Button) this.findViewById(R.id.btnRestore);
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap orig = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.logo)).getBitmap();
                imageView.setImageBitmap(orig);
            }
        });
        btnNDK = (Button) this.findViewById(R.id.btnNDK);
        btnNDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap img = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.logo)).getBitmap();
                int[] pix = new int[w * h];
                img.getPixels(pix, 0, w, 0, 0, w, h);
                int[] resultInt = hello(pix, w, h, alpha, beta);
                Bitmap resultImg = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
                resultImg.setPixels(resultInt, 0, w, 0, 0, w, h);
                imageView.setImageBitmap(resultImg);
            }
        });
    }
/*
    class ClickEvent implements View.OnClickListener {
        public void onClick(View v) {

            try {
                Set<String> libs = new HashSet<String>();
                String mapsFile = "/proc/" + android.os.Process.myPid() + "/maps";
                BufferedReader reader = new BufferedReader(new FileReader(mapsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.endsWith(".so")) {
                        int n = line.lastIndexOf(" ");
                        libs.add(line.substring(n + 1));
                    }
                }
                Log.d("Ldd", libs.size() + " libraries:");
                for (String lib : libs) {
                    Log.d("Ldd", lib);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (v == btnNDK) {
                long current = System.currentTimeMillis();
                Bitmap img1 = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.logo)).getBitmap();
                int w = img1.getWidth(), h = img1.getHeight();
                int[] pix = new int[w * h];
                img1.getPixels(pix, 0, w, 0, 0, w, h);
                int[] resultInt = ImgFun(pix, w, h);
                Bitmap resultImg = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
                resultImg.setPixels(resultInt, 0, w, 0, 0, w, h);
                long performance = System.currentTimeMillis() - current;
                imageView.setImageBitmap(resultImg);
                ImageEditing.this.setTitle("w:" + String.valueOf(img1.getWidth())
                        + ",h:" + String.valueOf(img1.getHeight()) + "NDK耗时"
                        + String.valueOf(performance) + " 毫秒");
            } else if (v == btnRestore) {
                Bitmap img2 = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.whiteshirt)).getBitmap();
                imageView.setImageBitmap(img2);
                ImageEditing.this.setTitle("使用OpenCV进行图像处理");
            }
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_editing, menu);
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
}

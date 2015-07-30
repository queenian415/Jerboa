package com.jebora.jebora;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jebora.jebora.Utils.FileInfo;
import com.jebora.jebora.Utils.ImgProc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;


public class ImageEditing extends Activity {
    static {
        System.loadLibrary("hello");
    }
    public native void doGreyscale(int[] buf, int w, int h);
    public native void doSepia(int[] buf, int w, int h, int depth, double r, double g, double b);
    public native void doContrast(int[] buf, int w, int h, double contrastLvl);
    public native void doBrightness(int[] buf, int w, int h, int brightnessLvl);
    public native void doEmboss(int[] buf, int w, int h);
    public native void doSnow(int[] buf, int w, int h);
    public native void doBlur(int[] buf, int w, int h);
    public native void doSharpen(int[] buf, int w, int h, double weight);
    public native void doEmbossTwo(int[] buf, int w, int h);
    public native void doEdgeDetect(int[] buf, int w, int h);

    ImageView imageView;
    Button btnNDK, btnRestore, btnFrame, btnDone;
    int w, h;
    Bitmap bm;
    int [] pixels;
    int [] originalPixs;
    int brightness = 0;
    int contrast = 0;
    double red = 0, green=0, blue=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editing);

        this.setTitle("ImageEdit");

        imageView = (ImageView) this.findViewById(R.id.ImageView01);

        bm = ((BitmapDrawable) getResources().getDrawable(
                R.drawable.minions)).getBitmap();
        w = bm.getWidth();
        h = bm.getHeight();
        pixels = new int[w * h];
        originalPixs = new int[w *h];
        bm.getPixels(pixels, 0, w, 0, 0, w, h);
        System.arraycopy(pixels, 0, originalPixs, 0, w * h);

        imageView.setImageBitmap(bm);

        btnNDK = (Button) this.findViewById(R.id.btnNDK);
        btnNDK.setOnClickListener(switchListener);
        btnFrame = (Button) this.findViewById(R.id.btnFrame);
        btnFrame.setOnClickListener(switchListener);
        btnRestore = (Button) this.findViewById(R.id.btnRestore);
        btnRestore.setOnClickListener(switchListener);
        btnDone = (Button) this.findViewById(R.id.btnFinish);
        btnDone.setOnClickListener(switchListener);
    }

    private View.OnClickListener switchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final LinearLayout ll = (LinearLayout) findViewById(R.id.LLTabs);
            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final LinearLayout sb_ll = (LinearLayout) findViewById(R.id.LLSeekbars);

            if(ll.getChildCount() > 0){
                ll.removeAllViews();
            }
            if(sb_ll.getChildCount() > 0){
                sb_ll.removeAllViews();
            }

            if(btnNDK == v) {
                final SepiaFilters sf = new SepiaFilters();
                int num_filters = sf.num_filters;

                for(int i_f = 0; i_f < num_filters; ++i_f){
                    final int i = i_f;
                    Button filter_btn = new Button(getApplicationContext());
                    filter_btn.setText("Filter" + i);
                    ll.addView(filter_btn, lp);
                    filter_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doSepia(pixels, w, h, 100, sf.red[i], sf.green[i], sf.blue[i]);
                            updateImageView();
                        }
                    });

                }

                Button customize_btn = new Button(getApplicationContext());
                customize_btn.setText("Customize");
                ll.addView(customize_btn, lp);
                customize_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ll.removeAllViews();
                        Button brightnesBtn = new Button(getApplicationContext());
                        Button contrastBtn = new Button(getApplicationContext());
                        Button sepiaBtn = new Button(getApplicationContext());
                        Button noiseBtn = new Button(getApplicationContext());
                        brightnesBtn.setText("Brightness");
                        contrastBtn.setText("Contrast");
                        sepiaBtn.setText("RBG");
                        noiseBtn.setText("Noise");
                        ll.addView(brightnesBtn, lp);
                        ll.addView(contrastBtn, lp);
                        ll.addView(sepiaBtn, lp);
                        ll.addView(noiseBtn, lp);

                        brightnesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (sb_ll.getChildCount() > 0) {
                                    sb_ll.removeAllViews();
                                }
                                SeekBar bLvl = new SeekBar(getApplicationContext());
                                sb_ll.addView(bLvl, lp);
                                bLvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        int progress = seekBar.getProgress();
                                        doBrightness(pixels, w, h, progress - brightness);
                                        brightness = progress;
                                        updateImageView();
                                    }
                                });
                            }
                        });

                        contrastBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (sb_ll.getChildCount() > 0) {
                                    sb_ll.removeAllViews();
                                }
                                SeekBar cLvl = new SeekBar(getApplicationContext());
                                sb_ll.addView(cLvl, lp);
                                cLvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        int progress = seekBar.getProgress();
                                        doContrast(pixels, w, h, (double) progress - contrast);
                                        contrast = progress;
                                        updateImageView();
                                    }
                                });
                            }
                        });

                        sepiaBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                red = blue = green = 0;
                                if (sb_ll.getChildCount() > 0) {
                                    sb_ll.removeAllViews();
                                }
                                SeekBar redLvl = new SeekBar(getApplicationContext());
                                SeekBar greenLvl = new SeekBar(getApplicationContext());
                                SeekBar blueLvl = new SeekBar(getApplicationContext());
                                sb_ll.addView(redLvl, lp);
                                sb_ll.addView(greenLvl, lp);
                                sb_ll.addView(blueLvl, lp);
                                redLvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        double progress = (double)seekBar.getProgress() / seekBar.getMax();
                                        doSepia(pixels, w, h, 100, progress - red, blue, green);
                                        red = progress;
                                        updateImageView();
                                    }
                                });
                                greenLvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        double progress = (double)seekBar.getProgress() / seekBar.getMax();
                                        doSepia(pixels, w, h, 100, red, progress - green, blue);
                                        green = progress;
                                        updateImageView();
                                    }
                                });
                                blueLvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        double progress = (double)seekBar.getProgress()/seekBar.getMax();
                                        doSepia(pixels, w, h, 100, red, green, progress - blue);
                                        blue = progress;
                                        updateImageView();
                                    }
                                });
                            }
                        });

                        noiseBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doSnow(pixels, w, h);
                                updateImageView();
                            }
                        });
                    }
                });
            }
            else if (btnFrame == v){
                selectImgProc();
            }
            else if (btnRestore == v){
                Bitmap orig = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.minions)).getBitmap();
                imageView.setImageBitmap(orig);
                System.arraycopy(originalPixs, 0, pixels, 0, w * h);
                updateImageView();
                clearAllLL();
            }
            else if (btnDone == v){
                clearAllLL();
                finish();
                File kidDir = FileInfo.getUserKidDirectory(getApplicationContext());
                Date imageEditTime = new Date();
                long unsignedHash = imageEditTime.hashCode() & 0x00000000ffffffffL;
                String fileName = unsignedHash + ".jpg";
                String dstPath = kidDir.toString() + File.separator + fileName;
                try{
                    File dstFile = FileInfo.newFile(dstPath);
                    FileOutputStream fos = new FileOutputStream(dstFile);
                    Bitmap dummy = Bitmap.createBitmap(w, h, bm.getConfig());
                    dummy.setPixels(pixels, 0, w, 0, 0, w, h);
                    if(dummy.compress(Bitmap.CompressFormat.PNG, 100, fos)){
                        int x = 1;
                    }
                    fos.flush();
                    fos.close();
                    Intent i = new Intent(ImageEditing.this, PreviewProduct.class);
                    i.putExtra("imagePath", dstPath);
                    i.putExtra("calling-activity", "ImageEditing");
                    startActivity(i);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };

    private void selectImgProc(){
        final CharSequence[] options = {
                "Brightness",
                "Contrast",
                "Greyscale",
                "Sepia",
                "Noise",
                "Blur",
                "GaussianBlur",
                "Emboss",
                "Sharpen",
                "Emboss2",
                "EdgeDetect",
                "Cancel"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ImageEditing.this);
        builder.setTitle("Select option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Brightness")) {
                    doBrightness(pixels, w, h, 20);
                } else if (options[which].equals("Contrast")) {
                    doContrast(pixels, w, h, 40);
                } else if (options[which].equals("Greyscale")) {
                    doGreyscale(pixels, w, h);
                } else if (options[which].equals("Sepia")) {
                    doSepia(pixels, w, h, 100, red, green, blue);
                } else if (options[which].equals("Noise")) {
                    doSnow(pixels, w, h);
                } else if (options[which].equals("Blur")) {
                    doBlur(pixels, w, h);
                } else if (options[which].equals("GaussianBlur")) {
                    bm = ImgProc.doGaussianBlur(bm);
                } else if (options[which].equals("Emboss")) {
                    doEmboss(pixels, w, h);
                } else if (options[which].equals("Sharpen")) {
                    doSharpen(pixels, w, h, 12.0);
                } else if (options[which].equals("Emboss2")) {
                    doEmbossTwo(pixels, w, h);
                } else if (options[which].equals("EdgeDetect")) {
                    doEdgeDetect(pixels, w, h);
                } else if (options[which].equals("Cancel")) {
                }

                Bitmap dummy = Bitmap.createBitmap(w, h, bm.getConfig());
                dummy.setPixels(pixels, 0, w, 0, 0, w, h);
                imageView.setImageBitmap(dummy);
                dialog.dismiss();
            }
        });
        builder.show();
    }

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

    private void updateImageView(){
        Bitmap dummy = Bitmap.createBitmap(w, h, bm.getConfig());
        dummy.setPixels(pixels, 0, w, 0, 0, w, h);
        imageView.setImageBitmap(dummy);
    }

    private void clearAllLL() {
        brightness = 0;
        contrast = 0;
        red = green = blue = 0;
        LinearLayout ll = (LinearLayout)findViewById(R.id.LLTabs);
        LinearLayout sbll = (LinearLayout) findViewById(R.id.LLSeekbars);
        ll.removeAllViews();
        sbll.removeAllViews();
    }

    private class SepiaFilters{
        public int num_filters = 6;
        double[] red =   {0.4, 0.0, 0.0, 0.5, 0.3, 0.0};
        double[] green = {0.0, 0.4, 0.0, 0.0, 0.45, 0.3};
        double[] blue =  {0.0, 0.0, 0.4, 0.4, 0.0, 0.5};
    }
}



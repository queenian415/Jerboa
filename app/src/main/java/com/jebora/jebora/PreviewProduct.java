package com.jebora.jebora;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class PreviewProduct extends ActionBarActivity implements View.OnTouchListener  {

    // these matrices will be used to move and zoom image
    public static Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private final int GALLERY_REQUEST_CODE = 2222;

    public static Matrix matrixtxt = new Matrix();
    private Matrix savedMatrixtxt = new Matrix();
    // we can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;
    private Button colorButton;
    private Button sizeButton;
    private Button threeDButton;
    private Button photoButton;
    private ImageView img;
    private ImageView txtimg;
    private ImageView shirt;
    private EditText editText;
    private Bitmap textbitmap;
    public static Bitmap pictureObject;
    public static int isPreview = 0;
    int shirtState = 0; //0 白 1 黑
    private String stringInfo;
    private TextView textView;
    private TextView textViewXY;
    private Spinner spinner;
    public Bitmap screenshot;
    public HorizontalScrollView horizontalScrollView;
    /*
    文字框
     */
    private final static int START_DRAGGING = 0;
    private final static int STOP_DRAGGING = 1;
    private SeekBar seekBar;
    private int status;
    int flag=0;
    float xAxis = 0f;
    float yAxis = 0f;
    float lastXAxis = 0f;
    float lastYAxis = 0f;
    final Context context = PreviewProduct.this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_preview_product);
        colorButton = (Button) findViewById(R.id.colorbutton);
        sizeButton = (Button) findViewById(R.id.sizebutton);
        threeDButton = (Button) findViewById(R.id.button3d);
        photoButton = (Button) findViewById(R.id.photogallary);
        shirt = (ImageView) findViewById(R.id.shirt);
        img = (ImageView) findViewById(R.id.logo);

        // Daniel's part
        String callingActivity = getIntent().getStringExtra("calling-activity");
        if(callingActivity != null){
            if(callingActivity.equals("ImageEditing")){
                try{
                    String filePath = getIntent().getStringExtra("imagePath");
                    File f = new File(filePath);
                    FileInputStream fis = new FileInputStream(f);
                    Bitmap bm = BitmapFactory.decodeStream(fis);
                    img.setImageBitmap(bm);
                }  catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        else
        img.setImageResource(R.drawable.logo);
        // Daniel, end
        seekBar = (SeekBar) findViewById(R.id.seekBar);
       // txtimg = (ImageView)findViewById(R.id.textImg);
        textViewXY = (TextView) findViewById(R.id.textView2);
        textViewXY.setVisibility(textViewXY.INVISIBLE);
        textView = (TextView) findViewById(R.id.textView);
        spinner = (Spinner) findViewById(R.id.spinner);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        String[] mItems = getResources().getStringArray(R.array.fronts_array);
// 建立Adapter并且绑定数据源
        ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,R.layout.spinner_item_dropdown, mItems);
//绑定 Adapter到控件
        spinner.setAdapter(_Adapter);
        //font add

        final Typeface billstar = Typeface.createFromAsset(getAssets(), "BillionStars_PersonalUse.ttf");
        final Typeface wedgie = Typeface.createFromAsset(getAssets(), "WedgieRegular.ttf");
        final Typeface zhongxingshu =  Typeface.createFromAsset(getAssets(), "ZhongXing.ttf");
        final Typeface xiongmao =  Typeface.createFromAsset(getAssets(), "xiongmao.ttf");
        final Typeface sans = textView.getTypeface();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String str = parent.getItemAtPosition(position).toString();
                Toast.makeText(PreviewProduct.this, "你点击的是:" + str, Toast.LENGTH_SHORT).show();
                switch (str) {
                    case "Wedgie":
                        textView.setTypeface(wedgie);
                    break;
                    case "Billion Stars":
                        textView.setTypeface(billstar);
                    break;
                    case "钟齐蔡云汉毛笔行书":
                        textView.setTypeface(zhongxingshu);
                    break;
                    case "Android":
                        textView.setTypeface(sans);
                    break;
                    case "熊猫萌萌":
                        textView.setTypeface(xiongmao);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        img.setOnTouchListener(this);
        textView.setOnTouchListener(this);
//        txtimg.setOnTouchListener(this);
        //初始化
        matrix.postTranslate(0, 0);
        img.setImageMatrix(matrix);
        matrixtxt.postTranslate(0, 0);
        seekBar.setVisibility(seekBar.INVISIBLE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                textView.setTextSize(progress);
            }
        });
//        txtimg.setImageMatrix(matrixtxt);
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.logo:
                // handle touch events here
                ImageView view = (ImageView) v;
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        seekBar.setVisibility(seekBar.INVISIBLE);
                        textView.setOnTouchListener(null);
                        savedMatrix.set(matrix);
                        start.set(event.getX(), event.getY());
                        mode = DRAG;
                        lastEvent = null;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        lastEvent = new float[4];
                        lastEvent[0] = event.getX(0);
                        lastEvent[1] = event.getX(1);
                        lastEvent[2] = event.getY(0);
                        lastEvent[3] = event.getY(1);
                        d = rotation(event);
                        break;
                    case MotionEvent.ACTION_UP:

                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        lastEvent = null;
                        textView.setOnTouchListener(this);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            float dx = event.getX() - start.x;
                            float dy = event.getY() - start.y;
                            matrix.postTranslate(dx, dy);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = (newDist / oldDist);
                                matrix.postScale(scale, scale, mid.x, mid.y);
                            }
                            if (lastEvent != null && event.getPointerCount() == 3) {
                                newRot = rotation(event);
                                float r = newRot - d;
                                float[] values = new float[9];
                                matrix.getValues(values);
                                float tx = values[2];
                                float ty = values[5];
                                float sx = values[0];
                                float xc = (view.getWidth() / 2) * sx;
                                float yc = (view.getHeight() / 2) * sx;
                                matrix.postRotate(r, tx + xc, ty + yc);
                            }
                        }
                        break;
                }

                view.setImageMatrix(matrix);
                break;
            case R.id.textView:
                // handle touch events here

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    seekBar.setVisibility(seekBar.VISIBLE);
                    img.setOnTouchListener(null);
                    status = START_DRAGGING;
                    final float x = event.getX();
                    final float y = event.getY();
                    lastXAxis = x;
                    lastYAxis = y;
                    v.setVisibility(View.INVISIBLE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    status = STOP_DRAGGING;
                    flag = 0;
                    v.setVisibility(View.VISIBLE);
                    img.setOnTouchListener(this);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (status == START_DRAGGING) {
                        flag = 1;
                        v.setVisibility(View.VISIBLE);
                        final float x = event.getX();
                        final float y = event.getY();
                        final float dx = x - lastXAxis;
                        final float dy = y - lastYAxis;
                        xAxis += dx;
                        yAxis += dy;
                        textViewXY.setText("x:" + xAxis + "  y:" + yAxis);

                        v.setX((int) xAxis);
                        v.setY((int) yAxis);

                        v.invalidate();

                    }
                }


        break;
    }

        return true;
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public void changeShirtColor(View v) {
        if (shirtState == 0) {
            shirt.setImageResource(R.drawable.blackshirt);
            colorButton.setText("白色");
            shirtState = 1;
        } else if (shirtState == 1) {
            shirt.setImageResource(R.drawable.whiteshirt);
            colorButton.setText("黑色");
            shirtState = 0;
        }
    }

    public void changeTextColor(View v) {
        ColorPickerDialogBuilder
                .with(context)
                .setTitle("选择颜色")
                .initialColor(0xffffffff)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                    textView.setTextColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    public void threeDView(View v) {
        Intent intent = new Intent();

        intent.setClass(PreviewProduct.this, LoadModels.class);

        startActivity(intent);
    }

    public static Bitmap drawText(String text, int textWidth, int textSize) {
// Get text dimensions
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);
        StaticLayout mTextLayout = new StaticLayout(text, textPaint,
                textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

// Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

// Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        c.drawPaint(paint);

// Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }

    public Bitmap textToBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        int width = (int) (paint.measureText(text) + 0.5f); // round
        float baseline = (int) (-paint.ascent() + 0.5f); // ascent() is negative
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    public void setTextImg(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("请输入文字");
        alert.setMessage("   ");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Do something with value!
                stringInfo = input.getText().toString();
                textView.setText(stringInfo);

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public void selectPhoto(View v) {

        final Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);

}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // To Handle Gallery Result
        if (data != null && requestCode == GALLERY_REQUEST_CODE) {

            Uri selectedImageUri = data.getData();
            String[] fileColumn = { MediaStore.Images.Media.DATA };

            Cursor imageCursor = getContentResolver().query(selectedImageUri,
                    fileColumn, null, null, null);
            imageCursor.moveToFirst();

            int fileColumnIndex = imageCursor.getColumnIndex(fileColumn[0]);
            String picturePath = imageCursor.getString(fileColumnIndex);
            File f =new File(picturePath);
            pictureObject = decodeFile(f);
            img.setImageBitmap(pictureObject);
            isPreview = 1;
        }
    }

    public void captureScreen(View view) {
        horizontalScrollView.setVisibility(horizontalScrollView.INVISIBLE);


        //RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.previewrelative);
        //relativeLayout.setDrawingCacheEnabled(true);
        //relativeLayout.buildDrawingCache();

        View v = getWindow().getDecorView();//.getRootView();
        v.setDrawingCacheEnabled(true);
        screenshot = Bitmap.createBitmap(v.getDrawingCache());
       // screenshot = relativeLayout.getDrawingCache();
        v.setDrawingCacheEnabled(false);
        try {
            FileOutputStream fos  = this.openFileOutput("screenshot", Context.MODE_PRIVATE);
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Intent intent = new Intent(this, Checkout.class);
            intent.putExtra("image", "screenshot");
            startActivity(intent);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }


    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=256;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
}
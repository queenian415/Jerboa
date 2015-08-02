package com.jebora.jebora;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jebora.jebora.Utils.FileInfo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailActivity extends ActionBarActivity {

    public static final String EXTRA_URL = "url";
    @InjectView(R.id.image)
    ImageView mImageView;
    Button mEditBtn, mDeleteBtn, mShareBtn, mProductBtn;
    private String mImageLocalPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);
        mImageLocalPath= getIntent().getStringExtra("localImagePath");
        if(mImageLocalPath != null){
            mImageLocalPath = mImageLocalPath.replace("file://", "");
            mImageLocalPath = FileInfo.getOriginalFromCompressed(mImageLocalPath);
            try{
                File f = new File(mImageLocalPath);
                FileInputStream fis = new FileInputStream(f);
                Bitmap bm = BitmapFactory.decodeStream(fis);
                mImageView.setImageBitmap(bm);
            }  catch (Exception e){
                e.printStackTrace();
            }
        }
        else{ // Empty, invalid string passed in
            Toast.makeText(getApplicationContext(), "Invalid file passed in!", Toast.LENGTH_LONG).show();
        }
//        final String imageUrl = getIntent().getExtras().getString(EXTRA_URL);
//        Picasso.with(this).load(imageUrl).into((ImageView) findViewById(R.id.image), new Callback() {
//            @Override
//            public void onSuccess() {
//                //moveBackground();
//            }
//            @Override
//            public void onError() {
//            }
//        });

        mEditBtn = (Button) findViewById(R.id.editBtn);
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageLocalPath != null) {
                    Intent i = new Intent(DetailActivity.this, ImageEditing.class);
                    i.putExtra("image", mImageLocalPath);
                    startActivity(i);
                }
            }
        });

        mDeleteBtn = (Button) findViewById(R.id.delBtn);
        mShareBtn = (Button) findViewById(R.id.shareBtn);
        mProductBtn = (Button) findViewById(R.id.productBtn);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                DetailActivity.this.onBackPressed();
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}



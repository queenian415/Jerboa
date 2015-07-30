package com.jebora.jebora;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailActivity extends ActionBarActivity {

    public static final String EXTRA_URL = "url";
    @InjectView(R.id.image)
    ImageView mImageView;
    Button mEditBtn, mDeleteBtn, mShareBtn, mProductBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.inject(this);
        final String imageUrl = getIntent().getExtras().getString(EXTRA_URL);
        Picasso.with(this).load(imageUrl).into((ImageView) findViewById(R.id.image), new Callback() {
            @Override
            public void onSuccess() {
                //moveBackground();
            }
            @Override
            public void onError() {
            }
        });

        mEditBtn = (Button) findViewById(R.id.editBtn);
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, ImageEditing.class);
                i.putExtra("image", imageUrl);
                startActivity(i);
            }
        });

        mDeleteBtn = (Button) findViewById(R.id.delBtn);
        mShareBtn = (Button) findViewById(R.id.shareBtn);
        mProductBtn = (Button) findViewById(R.id.productBtn);

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}



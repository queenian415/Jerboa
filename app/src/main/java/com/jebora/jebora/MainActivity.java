package com.jebora.jebora;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.ParseObject;
import com.parse.ParseUser;


public class MainActivity extends Activity {

    private Button email_Reg;
    private Button QQ_login;
    private Button Weibo_login;
    private Button login;
    private ImageView mBackGround;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            new UserRecorder(getApplicationContext());
            startActivity(new Intent(this, UserMain.class));
            finish();
        } else {
            setContentView(R.layout.activity_main);

            email_Reg = (Button) findViewById(R.id.email_reg);
            QQ_login = (Button) findViewById(R.id.qq_login);
            Weibo_login = (Button) findViewById(R.id.weibo_login);
            login = (Button) findViewById(R.id.login);
            mBackGround = (ImageView) findViewById(R.id.first_bg);

            setListener();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setListener (){

        email_Reg.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });
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

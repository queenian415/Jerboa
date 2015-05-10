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


public class MainActivity extends Activity {

    private Button email_Reg;
    private Button Third_party_Login;
    private Button login;
    private ImageView mBackGround;

    //private Animation mFadeIn;
    //private Animation mFadeInScale;
    //private Animation mFadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email_Reg = (Button) findViewById(R.id.email_reg);
        Third_party_Login = (Button) findViewById(R.id.third_party_login);
        login = (Button) findViewById(R.id.login);
        mBackGround = (ImageView) findViewById(R.id.first_bg);

        setListener();
        //initAnim();

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
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        });
    }

    /* still having some problems with the animation implementation, comment out for now
    private void initAnim() {
        mFadeIn = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.fade_in);
        mFadeIn.setDuration(1000);
        mFadeInScale = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.fade_in_scale);
        mFadeInScale.setDuration(6000);
        mFadeOut = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.fade_out);
        mFadeOut.setDuration(1000);
    }*/

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

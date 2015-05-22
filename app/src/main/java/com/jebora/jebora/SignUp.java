package com.jebora.jebora;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUp extends ActionBarActivity {

    public static final String PREFIX = "com.jebora.jebora";

    /***
     * Sign up verification strings
     */
    public static final String SIGNUP_EXISTS = "用户名已注册";
    public static final String SIGNUP_EMPTY = "请输入用户名和密码";
    public static final String SIGNUP_SUCCEEDS = "注册成功";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Create user account
        if (id == R.id.Continue) {

            String status = createAccount();
            TextView textView = (TextView)findViewById(R.id.signup_fail);
            if(status.equals(SIGNUP_SUCCEEDS)) {
                startActivity(new Intent(SignUp.this, SignUp_2.class));
                finish();
            } else if (status.equals(SIGNUP_EXISTS)){
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setText(SIGNUP_EMPTY);
                textView.setVisibility(View.VISIBLE);
            }
        }

        if (id == R.id.home){

            return true;
           /* Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)){
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
            }
            else{
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, upIntent);
            }*/
        }

        return super.onOptionsItemSelected(item);
    }

    private String createAccount() {
        String username = ((EditText)findViewById(R.id.username)).getText().toString().trim();
        String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();

        if (username.equals("") || password.equals("")) {
            return SIGNUP_EMPTY;
        }

        ServerAuthentication auth = new ServerAuthentication();
        return auth.signUp(username, password, SignUp.this);
    }
}

package com.jebora.jebora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Iterator;

public class SignUp extends ActionBarActivity {

    /***
     * Sign up verification strings
     */
    public static final String SIGNUP_EXISTS = "用户名已注册";
    public static final String SIGNUP_EMPTY = "请输入用户名和密码";
    public static final String SIGNUP_ERROR = "无法连接到服务器";
    public static final String SIGNUP_NOTAGREE = "请同意使用条款";
    public static final String SIGNUP_NOTEMAIL = "请输入邮箱为用户名";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setElevation(0);
        //set email drop down list
        final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.username);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s != null && s.length() > 0 && s.charAt(s.length() - 1) == '@') {
                    String[] emailDropdown = getResources().getStringArray(R.array.email_dropdown);
                    // Edit drop down list to match user email
                    // For example, if user enters "hello@", drop down list shows "hello@gmail.com" etc.
                    int i = 0;
                    for (String email : emailDropdown) {
                        emailDropdown[i] = s + email;
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, emailDropdown);
                    AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.username);
                    textView.setThreshold(1);
                    textView.setAdapter(adapter);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

            if (status.equals(SIGNUP_EXISTS)){
                textView.setVisibility(View.VISIBLE);
            } else if (status.equals(SIGNUP_NOTAGREE)) {
                textView.setText(status);
                textView.setVisibility(View.VISIBLE);
            } else if (status.equals(SIGNUP_ERROR)) {
                textView.setText(status);
                textView.setVisibility(View.VISIBLE);
            } else if (status.equals(SIGNUP_NOTEMAIL)) {
                textView.setText(status);
                textView.setVisibility(View.VISIBLE);
            } else if (status.equals(SIGNUP_EMPTY)) {
                textView.setText(status);
                textView.setVisibility(View.VISIBLE);
            } else {
                startActivity(new Intent(SignUp.this, SignUp_2.class));
                //finish();
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
        String username = ((AutoCompleteTextView)findViewById(R.id.username)).getText().toString().trim();
        String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
        CheckBox agreement = ((CheckBox)findViewById(R.id.agreement));

        if (username.equals("") || password.equals("")) {
            return SIGNUP_EMPTY;
        }

        if (!agreement.isChecked()) {
            return SIGNUP_NOTAGREE;
        }

        if (!username.contains("@")) {
            return SIGNUP_NOTEMAIL;
        }

        return ServerCommunication.signUp(getApplicationContext(), username, password);
    }

}

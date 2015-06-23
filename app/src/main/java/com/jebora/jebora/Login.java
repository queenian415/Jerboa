package com.jebora.jebora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;


public class Login extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (accountAuthentication()) {
                    startActivity(new Intent(Login.this, UserMain.class));
                    finish();
                } else {
                    TextView textView = (TextView) findViewById(R.id.signin_fail);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private boolean accountAuthentication () {
        String username = ((TextView)findViewById(R.id.username)).getText().toString().trim();
        String password = ((TextView)findViewById(R.id.password)).getText().toString().trim();
        ServerCommunication auth = new ServerCommunication();
        return auth.logIn(username, password);
    }

}

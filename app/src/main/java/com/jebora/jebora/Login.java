package com.jebora.jebora;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.style.URLSpan;

import java.util.logging.Handler;


public class Login extends ActionBarActivity {

    LinearLayout layoutOfPopup;
    PopupWindow popupMessage;
    Button insidePopupButton;
    TextView popupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        // Initialize pop up window for password reset
        // final Button resetButton = (Button)findViewById(R.id.resetpwd);
        TextView popupText = new TextView(this);
        insidePopupButton = new Button(this);
        layoutOfPopup = new LinearLayout(this);
        insidePopupButton.setText("我知道了");
        insidePopupButton.setWidth(ActionBar.LayoutParams.WRAP_CONTENT);
        popupText.setText("重设密码的邮件已发到您的邮箱，请查看邮件重设密码");
        popupText.setPadding(0, 0, 0, 20);
        layoutOfPopup.setOrientation(LinearLayout.VERTICAL);
        layoutOfPopup.addView(popupText);
        layoutOfPopup.addView(insidePopupButton);
        popupMessage = new PopupWindow(layoutOfPopup, ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        popupMessage.setContentView(layoutOfPopup);

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (accountAuthentication()) {
                    if (UserRecorder.isFirstTimeLogIn()) {
                        // we cannot proceed to UserMain until we've downloaded all
                        // the images to local
                        launchRingDialog();
                    } else {
                        startActivity(new Intent(Login.this, UserMain.class));
                        finish();
                    }
                } else {
                    TextView textView = (TextView) findViewById(R.id.signin_fail);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });

        /*
        findViewById(R.id.resetpwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((TextView)findViewById(R.id.username)).getText().toString().trim();
                ServerCommunication.resetPassword(username);
                // Notify user that pwd reset email has been sent
                popupMessage.showAsDropDown(resetButton, 0 ,0);
            }
        });*/

        insidePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMessage.dismiss();
            }
        });

        TextView forgetpw = (TextView) findViewById(R.id.forgetpw);
        SpannableString sp = new SpannableString("忘记密码");
        sp.setSpan(new URLSpan("http://www.baidu.com"), 0, 4,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgetpw.setText(sp);
        forgetpw.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(Login.this, "Please wait ...", "Downloading Images ...", true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerCommunication.syncDownImages(getApplicationContext(), 1000);
                ringProgressDialog.dismiss();
                startActivity(new Intent(Login.this, UserMain.class));
                finish();
            }
        }).start();
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
        return ServerCommunication.logIn(getApplicationContext(), username, password);
    }

}

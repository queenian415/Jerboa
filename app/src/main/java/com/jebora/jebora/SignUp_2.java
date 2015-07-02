package com.jebora.jebora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class SignUp_2 extends ActionBarActivity {

    public static final String SIGNUP2_EMPTY = "请输入全部信息";
    public static final String SIGNUP2_NORELATION = "请输入与孩子的关系";
    public static final String SIGNUP2_ERROR = "无法连接到服务器";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up_2, menu);
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

        if (id == R.id.skip) {
            startActivity(new Intent(SignUp_2.this, UserMain.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickComplete(View v) {
        String status = addKid();
        TextView textView = (TextView)findViewById(R.id.signup2_fail);

        if (status.equals(SIGNUP2_EMPTY)) {
            textView.setText(status);
            textView.setVisibility(v.VISIBLE);
        } else if (status.equals(SIGNUP2_NORELATION)) {
            textView.setText(status);
            textView.setVisibility(v.VISIBLE);
        } else if (status.equals(SIGNUP2_ERROR)) {
            textView.setText(status);
            textView.setVisibility(v.VISIBLE);
        } else {
            // Save the preferred kid
            UserRecorder.setPreferredKid(status);
            startActivity(new Intent(SignUp_2.this, UserMain.class));
            finish();
        }
    }

    public void showBirthdayPickerDialog(View v) {
        DatePickerFragment birthdayPicker = new DatePickerFragment();
        birthdayPicker.show(getSupportFragmentManager(), "datePicker");
    }

    public void showOther(View v) {
        (findViewById(R.id.signup_2_other_relationship)).setVisibility(v.VISIBLE);
    }

    public void hideOther(View v) {
        (findViewById(R.id.signup_2_other_relationship)).setVisibility(v.INVISIBLE);
    }

    private String addKid() {
        String kidName = ((EditText) findViewById(R.id.child_name)).getText().toString();
        String kidBirthday = ((Button) findViewById(R.id.child_birthday)).getText().toString();

        RadioGroup radioGroup1 = ((RadioGroup) findViewById(R.id.child_gender));
        int radioGroup1_id = radioGroup1.getCheckedRadioButtonId();
        if (radioGroup1_id == -1) {
            return SIGNUP2_EMPTY;
        }
        String kidGender = ((RadioButton) findViewById(radioGroup1_id)).getText().toString();

        RadioGroup radioGroup2 = ((RadioGroup) findViewById(R.id.relationship));
        int radioGroup2_id = radioGroup2.getCheckedRadioButtonId();
        if (radioGroup2_id == -1) {
            return SIGNUP2_NORELATION;
        }
        String kidRelation = ((RadioButton) findViewById(radioGroup2_id)).getText().toString();
        if (kidRelation.equals("其他")) {
            kidRelation = ((EditText) findViewById(R.id.signup_2_other_relationship)).getText().toString();
            if (kidRelation.equals("")) {
                return SIGNUP2_NORELATION;
            }
        }

        if (kidName.equals("") || kidBirthday.equals(getString(R.string.child_birthday))) {
            return SIGNUP2_EMPTY;
        }

        return ServerCommunication.addKid(kidName, kidBirthday, kidGender, kidRelation);
    }

}

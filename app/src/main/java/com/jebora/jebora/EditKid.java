package com.jebora.jebora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.provider.MediaStore;
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

import com.jebora.jebora.Utils.KidInfo;
import com.jebora.jebora.Utils.UserMainCheck;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class EditKid extends ActionBarActivity {

    public static final String SIGNUP2_EMPTY = "请输入全部信息";
    public static final String SIGNUP2_NORELATION = "请输入与孩子的关系";
    public static final String SIGNUP2_ERROR = "无法连接到服务器";
    public static final String KID_ID = "ID";

    public static boolean isDeleted = false;
    private KidInfo kidinfo;
    private String get_kidid = "ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kid);
        get_kidid = getIntent().getStringExtra(KID_ID);
        kidinfo = ServerCommunication.getKidObject(get_kidid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        EditText kidName = (EditText) findViewById(R.id.child_name);
        Button kidBirthday = (Button) findViewById(R.id.child_birthday);
        if(kidinfo!=null) {
            kidName.setText(kidinfo.getKidName());
            kidBirthday.setText(kidinfo.getKidBirthday());

            RadioButton gender;
            RadioButton relation;

            if (kidinfo.getKidGender().equals("男孩"))
                gender = (RadioButton) findViewById(R.id.boy);
            else
                gender = (RadioButton) findViewById(R.id.girl);
            gender.setChecked(true);
            switch (kidinfo.getKidRelation()) {
                case ("爸爸"): {
                    relation = (RadioButton) findViewById(R.id.dad);
                    break;
                }
                case ("妈妈"): {
                    relation = (RadioButton) findViewById(R.id.mom);
                    break;
                }
                case ("其他"): {
                    relation = (RadioButton) findViewById(R.id.others);
                    break;
                }
                default:
                    relation = null;
                    break;
            }
            if (relation != null)
                relation.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_kid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                EditKid.this.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onClickDelete (View v){
        ServerCommunication.deleteKid(getApplicationContext(),get_kidid);
        SystemClock.sleep(1000);
        UserMainCheck.setKidNumberUpdated(true);
        isDeleted = true;
        EditKid.this.onBackPressed();
        finish();
    }

    public void onClickComplete(View v) {
        String status = editKid();
        TextView textView = (TextView)findViewById(R.id.editkid_fail);

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
            SystemClock.sleep(1000);
            EditKid.this.onBackPressed();
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

    private String editKid() {
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
        KidInfo kidInfo_updated = new KidInfo(get_kidid, kidBirthday, kidGender, kidName,kidRelation);
        ServerCommunication.editKidObject(kidInfo_updated);
        return "SUCCESS";
    }

}

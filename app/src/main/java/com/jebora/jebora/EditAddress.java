package com.jebora.jebora;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jebora.jebora.Utils.ShippingInfo;


public class EditAddress extends ActionBarActivity {

    public static final String INFO = "INFO";
    public static final String IS_EDIT = "false";
    private static final String NO_NAME = "请输入收件人姓名";
    private static final String NO_ADDRESS = "请输入地址";
    private static final String NO_CITY = "请输入城市";
    private static final String NO_COUNTRY = "请输入国家";
    private static final String NO_POSTALCODE = "请输入邮编";
    private static final String SUCCESS = "成功添加地址";
    private String objID;
    private boolean status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        status = getIntent().getBooleanExtra(IS_EDIT,false);
        if(status){
            EditText name = (EditText)findViewById(R.id.Name);
            EditText address = (EditText)findViewById(R.id.Address);
            EditText city = (EditText)findViewById(R.id.City);
            EditText country = (EditText)findViewById(R.id.Country);
            EditText postalcode = (EditText)findViewById(R.id.PostalCode);

            String[] info = getIntent().getStringArrayExtra(INFO);
            if(info.length == 6){
                objID = info[0];
                name.setText(info[1]);
                address.setText(info[2]);
                city.setText(info[3]);
                country.setText(info[4]);
                postalcode.setText(info[5]);
            }
        }
    }

    public void onClickFinish(View v) {

        String result = addAdress();


        switch (result){
            case(NO_NAME):{
                DisplayToastMessage(NO_NAME);
                return;
            }
            case(NO_COUNTRY):{
                DisplayToastMessage(NO_COUNTRY);
                return;
            }
            case(NO_POSTALCODE):{
                DisplayToastMessage(NO_POSTALCODE);
                return;
            }
            case(NO_CITY):{
                DisplayToastMessage(NO_CITY);
                return;
            }
            case(NO_ADDRESS):{
                DisplayToastMessage(NO_ADDRESS);
                return;
            }
            case(SUCCESS):{
                if(status)
                    DisplayToastMessage("成功修改地址");
                else
                    DisplayToastMessage(SUCCESS);
                EditAddress.this.onBackPressed();
                //startActivity(new Intent(EditAddress.this,ManageAddress.class));
            }
        }

    }

    public void DisplayToastMessage(String message){
        Toast toast = Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private String addAdress(){
        EditText name = (EditText)findViewById(R.id.Name);
        EditText address = (EditText)findViewById(R.id.Address);
        EditText city = (EditText)findViewById(R.id.City);
        EditText country = (EditText)findViewById(R.id.Country);
        EditText postalcode = (EditText)findViewById(R.id.PostalCode);
        if(name.getText().toString().equals(""))
            return NO_NAME;
        if(address.getText().toString().equals(""))
            return NO_ADDRESS;
        if(city.getText().toString().equals(""))
            return NO_CITY;
        if(country.getText().toString().equals(""))
            return NO_COUNTRY;
        if(postalcode.getText().toString().equals(""))
            return NO_POSTALCODE;
        if(status){
            ShippingInfo si = new ShippingInfo(objID, name.getText().toString(),address.getText().toString(),city.getText().toString(),country.getText().toString(),postalcode.getText().toString());
            ServerCommunication.editShippingInfo(si);
        }
        else{
            ShippingInfo si = new ShippingInfo(name.getText().toString(),address.getText().toString(),city.getText().toString(),country.getText().toString(),postalcode.getText().toString());
            ServerCommunication.saveShippingInfo(si);
        }
        return SUCCESS;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_address, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            EditAddress.this.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

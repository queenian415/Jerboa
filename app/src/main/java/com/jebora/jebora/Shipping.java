package com.jebora.jebora;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jebora.jebora.Utils.ShippingInfo;

import java.util.ArrayList;
import java.util.List;


public class Shipping extends ActionBarActivity implements AdapterView.OnItemClickListener{

    private List<AddressItem> mAddressItem = new ArrayList<>();
    private ListView mListView;
    private int item_checked = 0;
    private List<ShippingInfo> mShippingInfo = new ArrayList<>();
    private static ShippingInfoAdapter adapter;
    private int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        mListView = (ListView)findViewById(R.id.shipping_address);
        mShippingInfo = ServerCommunication.getShippingInfoList();

        for(int i = 0;i<mShippingInfo.size();i++){
            AddressItem item = new AddressItem();
            item.address = mShippingInfo.get(i).getAddress()+", "+mShippingInfo.get(i).getCity()+", "+mShippingInfo.get(i).getCountry()+", "+mShippingInfo.get(i).getPostalCode();
            item.receiver = mShippingInfo.get(i).getName();
            mAddressItem.add(item);
        }
        adapter = new ShippingInfoAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        check++;
        Button manageAddress = (Button)findViewById(R.id.manage_address);
        manageAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Shipping.this, ManageAddress.class));
            }
        });

        Button checkout = (Button)findViewById(R.id.checkout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;
            }
        });
    }
    @Override
    protected void onResume(){
        if(check!=1){
            if(mShippingInfo!=null)
                mShippingInfo.clear();
            if(mAddressItem!=null)
                mAddressItem.clear();
            mShippingInfo = ServerCommunication.getShippingInfoList();

            for(int i = 0;i<mShippingInfo.size();i++){
                AddressItem item = new AddressItem();
                item.address = mShippingInfo.get(i).getAddress()+", "+mShippingInfo.get(i).getCity()+", "+mShippingInfo.get(i).getCountry()+", "+mShippingInfo.get(i).getPostalCode();
                item.receiver = mShippingInfo.get(i).getName();
                mAddressItem.add(item);
            }
            adapter.notifyDataSetChanged();
            adapter.notifyDataSetInvalidated();
        }
        check++;
        super.onResume();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if(item_checked!=position)
            mAddressItem.get(item_checked).button.setChecked(false);
        mAddressItem.get(position).button.setChecked(true);
        item_checked = position;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shipping_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {
            startActivity(new Intent(Shipping.this,EditAddress.class));
        }

        return super.onOptionsItemSelected(item);
    }
    private class ShippingInfoAdapter extends BaseAdapter{
        private Context context = getApplicationContext();
        private LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @Override
        public int getCount(){
            return mAddressItem.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder holder;
            RelativeLayout ManageInfo = (RelativeLayout) convertView;
            if(ManageInfo==null){
                View itemView = mInflater.inflate(R.layout.address_items,parent, false);
                ManageInfo = new RelativeLayout(Shipping.this);
                ManageInfo.addView(itemView);
                holder = new ViewHolder(ManageInfo);
                ManageInfo.setTag(holder);
            } else{
                holder = (ViewHolder) ManageInfo.getTag();
            }
            AddressItem item = mAddressItem.get(position);
            holder.receiver.setText(item.receiver);
            holder.address.setText(item.address);
            item.button = holder.select;
            item.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton temp = (RadioButton) v;
                    mAddressItem.get(item_checked).button.setChecked(false);
                    temp.setChecked(true);
                    for(int i=0; i<mAddressItem.size(); i++){
                        if(mAddressItem.get(i).button.isChecked())
                            item_checked=i;
                    }
                }
            });

            return ManageInfo;

        }

        @Override
        public Object getItem(int position) {
            return mAddressItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    private static class ViewHolder {
        public TextView receiver;
        public TextView address;
        public RadioButton select;

        ViewHolder(View view) {
            receiver = (TextView) view.findViewById(R.id.receiver_name);
            address = (TextView) view.findViewById(R.id.shipping_address);
            select = (RadioButton) view.findViewById(R.id.select_address);
        }
    }

    public class AddressItem {
        public String receiver;
        public String address;
        public RadioButton button;
    }
}

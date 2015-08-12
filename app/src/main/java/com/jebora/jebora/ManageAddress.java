package com.jebora.jebora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jebora.jebora.SlideView.OnSlideListener;
import com.jebora.jebora.Utils.ShippingInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

public class ManageAddress extends ActionBarActivity implements OnItemClickListener, OnClickListener,
        OnSlideListener {


    private ListViewAddress mListView;

    private List<MessageItem> mMessageItems = new ArrayList<ManageAddress.MessageItem>();

    private SlideView mLastSlideViewWithStatusOn;

    private static SlideAdapter adapter;

    private List<ShippingInfo> mShippingInfo = new ArrayList<>();

    private String[] address_selected;

    private int check = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        check++;
    }
    @Override
    protected void onResume(){
        if(check!=1) {

            if (mMessageItems != null)
                mMessageItems.clear();
            if (mShippingInfo != null)
                mShippingInfo.clear();

            mShippingInfo = ServerCommunication.getShippingInfoList();

            for (int i = 0; i < mShippingInfo.size(); i++) {
                MessageItem item = new MessageItem();
                item.address = mShippingInfo.get(i).getAddress() + ", " + mShippingInfo.get(i).getCity() + ", " + mShippingInfo.get(i).getCountry() + ", " + mShippingInfo.get(i).getPostalCode();
                item.receiver = mShippingInfo.get(i).getName();
                mMessageItems.add(item);
            }
            this.adapter.notifyDataSetInvalidated();
            this.adapter.notifyDataSetChanged();
        }
        check++;
        super.onResume();
    }
    private void initView() {
        mListView = (ListViewAddress) findViewById(R.id.manage_address);

        mShippingInfo = ServerCommunication.getShippingInfoList();

        for(int i = 0;i<mShippingInfo.size();i++){
            MessageItem item = new MessageItem();
            item.address = mShippingInfo.get(i).getAddress()+", "+mShippingInfo.get(i).getCity()+", "+mShippingInfo.get(i).getCountry()+", "+mShippingInfo.get(i).getPostalCode();
            item.receiver = mShippingInfo.get(i).getName();
            mMessageItems.add(item);
        }

        adapter = new SlideAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_address, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                ManageAddress.this.onBackPressed();
                return true;
            case R.id.add:
                Intent intent = new Intent(ManageAddress.this,EditAddress.class);
                intent.putExtra(EditAddress.IS_EDIT, false);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SlideAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        SlideAdapter() {
            super();
            mInflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mMessageItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mMessageItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            SlideView slideView = (SlideView) convertView;
            if (slideView == null) {
                View itemView = mInflater.inflate(R.layout.manage_address_items, parent, false);

                slideView = new SlideView(ManageAddress.this);
                slideView.setContentView(itemView);

                holder = new ViewHolder(slideView);
                slideView.setOnSlideListener(ManageAddress.this);
                slideView.setTag(holder);
            } else {
                holder = (ViewHolder) slideView.getTag();
            }
            MessageItem item = mMessageItems.get(position);
            item.slideView = slideView;
            item.slideView.shrink();

            holder.address.setText(item.address);
            holder.receiver.setText(item.receiver);
            holder.deleteHolder.setOnClickListener(ManageAddress.this);

            return slideView;
        }

    }

    public class MessageItem {
        public String receiver;
        public String address;
        public SlideView slideView;
    }

    private static class ViewHolder {
        public TextView receiver;
        public TextView address;
        public ViewGroup deleteHolder;

        ViewHolder(View view) {
            receiver = (TextView) view.findViewById(R.id.receiver_name);
            address = (TextView) view.findViewById(R.id.shipping_address);
            deleteHolder = (ViewGroup)view.findViewById(R.id.holder);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if(mLastSlideViewWithStatusOn != null)
            mLastSlideViewWithStatusOn.shrink();
        else{
            Intent intent = new Intent(ManageAddress.this,EditAddress.class);
            address_selected = new String[6];
            address_selected[0] = mShippingInfo.get(position).getObjectId();
            address_selected[1] = mShippingInfo.get(position).getName();
            address_selected[2] = mShippingInfo.get(position).getAddress();
            address_selected[3] = mShippingInfo.get(position).getCity();
            address_selected[4] = mShippingInfo.get(position).getCountry();
            address_selected[5] = mShippingInfo.get(position).getPostalCode();



            intent.putExtra(EditAddress.INFO, address_selected);
            intent.putExtra(EditAddress.IS_EDIT, true);
            startActivity(intent);
        }

    }

    @Override
    public void onSlide(View view, int status) {

        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view)
            mLastSlideViewWithStatusOn.shrink();

        if (status == SLIDE_STATUS_ON)
            mLastSlideViewWithStatusOn = (SlideView) view;

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.holder) {
            for(int i=0;i<mMessageItems.size();i++){
                if(mLastSlideViewWithStatusOn == mMessageItems.get(i).slideView){
                    mMessageItems.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}

package com.jebora.jebora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManageAddress extends ActionBarActivity implements OnItemClickListener, OnClickListener,
        OnSlideListener {


    private ListViewAddress mListView;

    private List<MessageItem> mMessageItems = new ArrayList<ManageAddress.MessageItem>();

    private SlideView mLastSlideViewWithStatusOn;

    private static SlideAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        mListView = (ListViewAddress) findViewById(R.id.manage_address);

        MessageItem item = new MessageItem();
        item.receiver = "Jack";
        item.address = "asdfasdfasdfasdfasdfsaf";
        mMessageItems.add(item);

        MessageItem item1 = new MessageItem();
        item1.receiver = "Mike";
        item1.address = "asdfasdfasdfasdfasdfsaf";
        mMessageItems.add(item1);

        adapter = new SlideAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                ManageAddress.this.onBackPressed();
                return true;
            case R.id.add:
                startActivity(new Intent(ManageAddress.this,EditAddress.class));
                return true;
        }

        return super.onOptionsItemSelected(item);    }

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
            //intent.putExtra(EditKid.KID_ID, mMessageItems.get(position).kidID);
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

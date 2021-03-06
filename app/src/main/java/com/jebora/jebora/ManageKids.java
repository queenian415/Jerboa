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
import com.jebora.jebora.Utils.UserMainCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManageKids extends ActionBarActivity implements OnItemClickListener, OnClickListener,
        OnSlideListener {

    private static final String TAG = "ManageKids";

    private ListViewCompat mListView;

    private List<MessageItem> mMessageItems = new ArrayList<ManageKids.MessageItem>();

    private SlideView mLastSlideViewWithStatusOn;

    private static SlideAdapter adapter;

    private int check = 0;

    private static int last_item_selected = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_kids);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        check++;
    }

    @Override
    protected void onResume(){
        if(check!=1) {
            /*if (mMessageItems != null)
                mMessageItems.clear();
            Map<String, String> kids = UserRecorder.getKidList();
            for (String key : kids.keySet()) {
                MessageItem item = new MessageItem();
                item.iconRes = R.drawable.ic_drawer_explore;
                item.kidname = kids.get(key);
                item.kidID = key;
                mMessageItems.add(item);
            }*/
            if(EditKid.isDeleted==true){
                mMessageItems.remove(last_item_selected);
                EditKid.isDeleted=false;
            }
            adapter.notifyDataSetChanged();
            adapter.notifyDataSetInvalidated();
        }
        check++;
        super.onResume();
    }

    private void initView() {
        mListView = (ListViewCompat) findViewById(R.id.managekids_list);

        Map<String, String> kids = UserRecorder.getKidList();
        for (String key : kids.keySet()) {
            MessageItem item = new MessageItem();
            item.iconRes = R.drawable.ic_drawer_explore;
            item.kidname = kids.get(key);
            item.kidID = key;
            mMessageItems.add(item);
        }
        adapter = new SlideAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                ManageKids.this.onBackPressed();
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
                View itemView = mInflater.inflate(R.layout.managekidslist, null);

                slideView = new SlideView(ManageKids.this);
                slideView.setContentView(itemView);

                holder = new ViewHolder(slideView);
                slideView.setOnSlideListener(ManageKids.this);
                slideView.setTag(holder);
            } else {
                holder = (ViewHolder) slideView.getTag();
            }
            MessageItem item = mMessageItems.get(position);
            item.slideView = slideView;
            item.slideView.shrink();

            holder.icon.setImageResource(item.iconRes);
            holder.kidname.setText(item.kidname);
            holder.deleteHolder.setOnClickListener(ManageKids.this);

            return slideView;
        }

    }

    public class MessageItem {
        public int iconRes;
        public String kidname;
        public String kidID;
        public SlideView slideView;
    }

    private static class ViewHolder {
        public ImageView icon;
        public TextView kidname;
        public ViewGroup deleteHolder;

        ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.icon);
            kidname = (TextView) view.findViewById(R.id.manage_kidname);
            deleteHolder = (ViewGroup)view.findViewById(R.id.holder);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if(mLastSlideViewWithStatusOn != null)
            mLastSlideViewWithStatusOn.shrink();
        else{
            last_item_selected = position;
            Intent intent = new Intent(ManageKids.this,EditKid.class);
            intent.putExtra(EditKid.KID_ID, mMessageItems.get(position).kidID);
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
                    ServerCommunication.deleteKid(getApplicationContext(),mMessageItems.get(i).kidID);
                    mMessageItems.remove(i);
                    UserMainCheck.setKidNumberUpdated(true);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}

package com.jebora.jebora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShoppingCart extends ActionBarActivity implements OnItemClickListener, OnClickListener,
        OnSlideListener {

    //private static final String TAG = "ManageKids";

    private ShoppingCartListView mListView;

    private List<ShoppingCartItem> mShoppingCartItems = new ArrayList<ShoppingCart.ShoppingCartItem>();

    private SlideView mLastSlideViewWithStatusOn;

    private static SlideAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        mListView = (ShoppingCartListView) findViewById(R.id.shopping_cart_items);

        ShoppingCartItem item = new ShoppingCartItem();
        item.iconRes = R.drawable.ic_drawer_explore;
        item.itemName = "Item1";
        item.itemNumber = "1";
        item.itemPrice = "60";
        mShoppingCartItems.add(item);

        ShoppingCartItem item1 = new ShoppingCartItem();
        item1.iconRes = R.drawable.ic_drawer_explore;
        item1.itemName = "Item2";
        item1.itemNumber = "4";
        item1.itemPrice = "20";
        mShoppingCartItems.add(item1);

        TextView subtotal = (TextView) findViewById(R.id.price);
        subtotal.setText("140");

        adapter = new SlideAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_cart, menu);
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
                ShoppingCart.this.onBackPressed();
                return true;
            case R.id.next:
                startActivity(new Intent(ShoppingCart.this, ShippingInfo.class));
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
            return mShoppingCartItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mShoppingCartItems.get(position);
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
                View itemView = mInflater.inflate(R.layout.shopping_cart_items, parent, false);

                slideView = new SlideView(ShoppingCart.this);
                slideView.setContentView(itemView);

                holder = new ViewHolder(slideView);
                slideView.setOnSlideListener(ShoppingCart.this);
                slideView.setTag(holder);
            } else {
                holder = (ViewHolder) slideView.getTag();
            }
            ShoppingCartItem item = mShoppingCartItems.get(position);
            item.slideView = slideView;
            item.slideView.shrink();

            holder.icon.setImageResource(item.iconRes);
            holder.itemName.setText(item.itemName);
            holder.itemPrice.setText(item.itemPrice);
            holder.itemNumber.setText(item.itemNumber);
            holder.deleteHolder.setOnClickListener(ShoppingCart.this);

            return slideView;
        }

    }

    public class ShoppingCartItem {
        public int iconRes;
        public String itemName;
        public String itemNumber;
        public String itemPrice;
        public SlideView slideView;
    }

    private static class ViewHolder {
        public ImageView icon;
        public TextView itemName;
        public TextView itemNumber;
        public TextView itemPrice;
        public ViewGroup deleteHolder;

        ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.item_icon);
            itemName = (TextView) view.findViewById(R.id.item_name);
            itemNumber = (TextView) view.findViewById(R.id.quantity);
            itemPrice = (TextView) view.findViewById(R.id.price);
            deleteHolder = (ViewGroup)view.findViewById(R.id.holder);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if(mLastSlideViewWithStatusOn != null)
            mLastSlideViewWithStatusOn.shrink();
        else
            startActivity(new Intent(ShoppingCart.this,PreviewProduct.class));
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
            for(int i=0;i<mShoppingCartItems.size();i++){
                if(mLastSlideViewWithStatusOn == mShoppingCartItems.get(i).slideView){
                    mShoppingCartItems.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}

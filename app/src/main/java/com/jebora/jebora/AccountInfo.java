package com.jebora.jebora;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jebora.jebora.adapters.ProductAdapter;
import com.jebora.jebora.adapters.RecyclerItemClickListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class AccountInfo extends ActionBarActivity implements AdapterView.OnItemClickListener{

    private List<AccountItem> mAccountItem = new ArrayList<>();
    private ListView mListView;
    private PopupWindow popupWindow;
    private int positionSlected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        mListView = (ListView)findViewById(R.id.personal_info_list);
        String[] temp = getResources().getStringArray(R.array.user_info);
        for(int i=0; i<temp.length; i++){
            AccountItem item = new AccountItem();
            item.info_items = temp[i];
            item.user_items = temp[i];
            mAccountItem.add(item);
        }
        mListView.setAdapter(new AccountInfoAdapter());
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        positionSlected = position;
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.account_info_popupwindow, null);
        popupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);

        final String info = mAccountItem.get(position).user_items.toString().trim();

        // Set up pop up window
        TextView text = (TextView)popupView.findViewById(R.id.account_info_popup_text);
        text.setText("请输入需要更改的信息");

        final EditText userInput = (EditText)popupView.findViewById(R.id.account_info_change);
        userInput.setText(info);
        userInput.setVisibility(View.VISIBLE);
        userInput.setTextColor(getResources().getColor(R.color.grey));
        popupWindow.setFocusable(true);
        popupWindow.update();

        popupView.findViewById(R.id.account_info_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountItem temp = mAccountItem.get(positionSlected);
                temp.user_items = userInput.getText().toString().trim();
                mAccountItem.set(positionSlected, temp);
                popupWindow.dismiss();
            }
        });

        findViewById(R.id.account_info_layout).post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(findViewById(R.id.account_info_layout), Gravity.CENTER, 0, 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_info, menu);
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
    private class AccountInfoAdapter extends BaseAdapter{
        private Context context = getApplicationContext();
        private LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @Override
        public int getCount(){
            return mAccountItem.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder holder;
            LinearLayout ManageInfo = (LinearLayout) convertView;
            if(ManageInfo==null){
                View itemView = mInflater.inflate(R.layout.manage_personal_info,parent, false);
                ManageInfo = new LinearLayout(AccountInfo.this);
                ManageInfo.addView(itemView);
                holder = new ViewHolder(ManageInfo);
                ManageInfo.setTag(holder);
            } else{
                holder = (ViewHolder) ManageInfo.getTag();
            }
            AccountItem item = mAccountItem.get(position);
            holder.info.setText(item.info_items);
            holder.user.setText(item.user_items);
            return ManageInfo;

        }

        @Override
        public Object getItem(int position) {
            return mAccountItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    private static class ViewHolder {
        public TextView info;
        public TextView user;

        ViewHolder(View view) {
            info = (TextView) view.findViewById(R.id.personal_info_items);
            user = (TextView) view.findViewById(R.id.personal_info_edit);
        }
    }

    public class AccountItem {
        public String info_items;
        public String user_items;
    }
}

package com.jebora.jebora;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import com.jebora.jebora.adapters.ProductAdapter;
import com.jebora.jebora.adapters.RecyclerItemClickListener;
import com.jebora.jebora.models.ProductManager;


public class ProductSelect extends TabActivity {

    public RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private TabHost tabhost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_select);
        tabhost = getTabHost();

        tabhost.addTab(tabhost
                //创建新标签one
                .newTabSpec("one")
                        //设置标签标题
                .setIndicator("精选产品")
                        //设置该标签的布局内容
                .setContent(R.id.tab0));
        tabhost.addTab(tabhost.newTabSpec("two").setIndicator("分类").setContent(R.id.tab1));

        mRecyclerView = (RecyclerView)findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new ProductAdapter(ProductManager.getInstance().getProducts(), R.layout.row_product, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(ProductAdapter.mContext, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever

                        Toast.makeText(ProductAdapter.mContext, "The Item Clicked is: " + position, Toast.LENGTH_SHORT).show();

                        switch (position){
                            case 0:
                                    UserMain.checkoutItem.name = "T恤衫";
                                    Intent intent = new Intent();
                                    intent.setClass(ProductSelect.this, PreviewProduct.class);
                                    startActivity(intent);
                                    break;
                        }
                    }
                })
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_select, menu);
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
}

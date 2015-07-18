package com.jebora.jebora.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jebora.jebora.R;
import com.jebora.jebora.models.Product;

import java.util.List;

/**
 * Created by mshzhb on 15/7/3.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> products;
    private int rowLayout;
    public static  Context mContext;


    public ProductAdapter(List<Product> products, int rowLayout, Context context) {
        this.products = products;
        this.rowLayout = rowLayout;
        this.mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Product product = products.get(i);
        viewHolder.productName.setText(product.name);
        Drawable temp = mContext.getResources().getDrawable(product.getImageResourceId(mContext));
        viewHolder.productImage.setImageDrawable(temp);

    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView productName;
        public ImageView productImage;



        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            productName = (TextView) itemView.findViewById(R.id.productName);
            productImage = (ImageView)itemView.findViewById(R.id.productImage);


        }
        public void onClick(View v) {

            Toast.makeText(mContext,"The Item Clicked is: " + getPosition(), Toast.LENGTH_SHORT).show();

        }

    }
}

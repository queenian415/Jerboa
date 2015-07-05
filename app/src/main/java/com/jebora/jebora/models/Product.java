package com.jebora.jebora.models;

import android.content.Context;

/**
 * Created by mshzhb on 15/7/3.
 */
public class Product {
    public String name;
    public String description;
    public String imageName;


    public int getImageResourceId(Context context)
    {
        try {
            return context.getResources().getIdentifier(this.imageName, "drawable", context.getPackageName());

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}

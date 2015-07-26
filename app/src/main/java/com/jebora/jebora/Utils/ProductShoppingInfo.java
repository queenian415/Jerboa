package com.jebora.jebora.Utils;

/**
 * Created by Tiffanie on 15-07-25.
 */
public class ProductShoppingInfo {
    private String productName;
    private int numOfItems;
    private int price;

    public ProductShoppingInfo(String pn, int num, int pri) {
        productName = pn;
        numOfItems = num;
        price = pri;
    }

    public int getNumOfItems() {
        return numOfItems;
    }

    public int getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }
}

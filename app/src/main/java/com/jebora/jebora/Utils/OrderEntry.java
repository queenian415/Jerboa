package com.jebora.jebora.Utils;

/**
 * Created by Tiffanie on 15-07-25.
 */
public class OrderEntry {
    private ProductInfo product;
    private int numOfItems;

     public OrderEntry(ProductInfo product, int numOfItems) {
         this.product = product;
         this.numOfItems = numOfItems;
     }

    public ProductInfo getProduct() {
        return product;
    }

    public int getNumOfItems() {
        return numOfItems;
    }
}

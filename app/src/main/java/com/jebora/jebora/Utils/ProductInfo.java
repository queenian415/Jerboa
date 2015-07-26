package com.jebora.jebora.Utils;

/**
 * Created by Tiffanie on 15-07-25.
 */
public class ProductInfo {
    private String productId;
    private String productName;
    private int price;

    public ProductInfo(String id, String productName, int price) {
        this.productId = id;
        this.productName = productName;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getPrice() {
        return price;
    }
}

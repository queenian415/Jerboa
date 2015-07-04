package com.jebora.jebora.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mshzhb on 15/7/3.
 */
public class ProductManager {

   //private static String[] productArray = {"Cloth", "Cup", "Phone Case", "Japan Import", "UK import", "US import" };
   private static String[] productArray = {"Cloth", "Cup","Phone Case","Japan Import", "UK import", "US import"};
    private static String loremIpsum = "good";

    private static ProductManager mInstance;
    private List<Product> products;

    public static ProductManager getInstance() {
        if (mInstance == null) {
            mInstance = new ProductManager();
        }

        return mInstance;
    }

    public List<Product> getProducts() {
        if (products == null) {
            products = new ArrayList<Product>();

            int i = 0;
            for (String productName : productArray) {
                Product product = new Product();
                product.name = productName;
                product.description = loremIpsum;
                switch (i){
                    case 0: product.imageName = "cloth"; break;
                    case 1: product.imageName = productName.replaceAll("\\s+","").toLowerCase(); break;
                    case 2: product.imageName = "phonecase"; break;
                    case 3: product.imageName = "japanimport"; break;
                    case 4: product.imageName = "ukimport"; break;
                    case 5: product.imageName = "usimport"; break;
                }
                i++;
                products.add(product);
            }
        }

        return  products;
    }
}

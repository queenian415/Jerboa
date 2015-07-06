package com.jebora.jebora.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mshzhb on 15/7/3.
 */
public class ProductManager {

   //private static String[] productArray = {"Cloth", "Cup", "Phone Case", "Japan Import", "UK import", "US import" };
   private static String[] productArray = {"服裝", "茶杯","手機殼","日本進口", "英國進口", "美國進口"};
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
                    case 1: product.imageName = "cup"; break;
                    case 2: product.imageName = "phonecase"; break;
                    case 3: product.imageName = "japan"; break;
                    case 4: product.imageName = "uk"; break;
                    case 5: product.imageName = "us"; break;
                }
                i++;
                products.add(product);
            }
        }

        return  products;
    }
}

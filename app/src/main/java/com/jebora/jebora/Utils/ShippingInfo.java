package com.jebora.jebora.Utils;

/**
 * Created by Tiffanie on 15-07-25.
 */
public class ShippingInfo {
    private String name;
    private String address;
    private String city;
    private String country;
    private String postalCode;

    public ShippingInfo(String name, String address, String city, String country, String postalCode) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getPostalCode() { return postalCode; }
}

package com.jebora.jebora.Utils;

import java.util.List;

/**
 * Created by Tiffanie on 15-07-25.
 */
public class OrderInfo {
    List<OrderEntry> orderEntries;
    ShippingInfo shipping;

    public OrderInfo(List<OrderEntry> list, ShippingInfo shippingInfo) {
        orderEntries = list;
        shipping = shippingInfo;
    }

    public List<OrderEntry> getOrderEntries() {
        return orderEntries;
    }

    public ShippingInfo getShipping() {
        return shipping;
    }

    public void setOrderEntries(List<OrderEntry> orderEntries) {
        this.orderEntries = orderEntries;
    }

    public void setShipping(ShippingInfo shipping) {
        this.shipping = shipping;
    }
}

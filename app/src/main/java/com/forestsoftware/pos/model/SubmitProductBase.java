package com.forestsoftware.pos.model;

/**
 * Created by HP-PC on 5/28/2018.
 */
//
//public class SubmitProductBase
//{

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubmitProductBase {

    @SerializedName("vendorId")
    @Expose
    private Integer vendorId;
    @SerializedName("total")
    @Expose
    private Double total;
    @SerializedName("discount")
    @Expose
    private Double discount;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("paymentType")
    @Expose
    private String paymentType;
    @SerializedName("change")
    @Expose
    private double change;
    @SerializedName("items")
    @Expose
    private List<Product> items = null;

    public SubmitProductBase(Integer vendorId, double total, double discount, double price, String paymentType, double change, List<Product> items) {
        this.vendorId = vendorId;
        this.total = total;
        this.discount = discount;
        this.price = price;
        this.paymentType = paymentType;
        this.change = change;
        this.items = items;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Integer change) {
        this.change = change;
    }

    public List<Product> getItems() {
        return items;
    }

    public void setItems(List<Product> items) {
        this.items = items;
    }
}

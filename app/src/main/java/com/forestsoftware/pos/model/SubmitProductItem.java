package com.forestsoftware.pos.model;

/**
 * Created by HP-PC on 5/28/2018.
 */

//public class SubmitProductItem
//{

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubmitProductItem {

    @SerializedName("productId")
    @Expose
    private Integer productId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}

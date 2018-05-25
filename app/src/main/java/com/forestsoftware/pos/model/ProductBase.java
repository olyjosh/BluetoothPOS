package com.forestsoftware.pos.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by HP-PC on 5/24/2018.
 */

public class ProductBase {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("productCategories")
    @Expose
    private List<ProductCategory> productCategories = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<ProductCategory> getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(List<ProductCategory> productCategories) {
        this.productCategories = productCategories;
    }
}
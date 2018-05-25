package com.forestsoftware.pos.rest;

import com.forestsoftware.pos.model.Product;
import com.forestsoftware.pos.model.ProductBase;
import com.forestsoftware.pos.model.ProductCategory;
import com.forestsoftware.pos.model.User;
import com.forestsoftware.pos.model.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface
{
    @POST("users/login")
    Call<UserResponse> userLogIn(@Body User data);

    @GET("product_category/{vendorId}/list")
    Call<ProductBase> getVendor(@Path("vendorId") String id);

    @GET("product_category/{vendorId}/list")
    Call<ProductCategory> getVendor2(@Path("vendorId") String id);


}

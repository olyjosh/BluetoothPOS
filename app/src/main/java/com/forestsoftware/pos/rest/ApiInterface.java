package com.forestsoftware.pos.rest;

import com.forestsoftware.pos.model.GeneralResponse;
import com.forestsoftware.pos.model.ProductBase;
import com.forestsoftware.pos.model.ProductCategory;
import com.forestsoftware.pos.model.SubmitProductBase;
import com.forestsoftware.pos.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface
{
    @POST("users/login")
    Call<GeneralResponse> userLogIn(@Body User data);

    @GET("product_category/{vendorId}/list")
    Call<ProductBase> getVendor(@Path("vendorId") String id);

    @GET("product_category/{vendorId}/list")
    Call<ProductCategory> getVendor2(@Path("vendorId") String id);


    @POST("sales")
    Call<GeneralResponse> submit(@Body SubmitProductBase submitProductBase);


}

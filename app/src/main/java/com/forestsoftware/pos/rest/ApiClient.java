package com.forestsoftware.pos.rest;

import android.util.Log;

import com.forestsoftware.pos.util.Constant;
import com.forestsoftware.pos.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by HP-PC on 3/23/2018.
 */

public class ApiClient {
    private static Retrofit retrofit = null;

    public static OkHttpClient getHeader() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .addNetworkInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();
//                                okhttp3.Response response = chain.proceed(request);

                                String token = SessionManager.getTOKEN();


                                if (token != null) {
                                    System.out.println("----------------KKKKKKKKKKKKK-->>>" + token);

                                    Log.d("--Authorization-- ", token);

                                    Request original = chain.request();
                                    // Request customization: add request headers

                                    Request.Builder requestBuilder = original.newBuilder()
                                            .addHeader("Authorization", token);

                                    request = requestBuilder.build();
                                }

                                return chain.proceed(request);
                            }


                        })
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        try {
                            Response response = chain.proceed(chain.request());
                            if (!response.isSuccessful()) {
                                Log.e("tag", "Failure central - response code: " + response.code());
                                Log.e("tag", "central server error handling");

                                // Central error handling for error responses here:
                                // e.g. 4XX and 5XX errors
                                switch (response.code()) {
                                    case 401:

                                        Log.wtf("===========401401401401===========", "401 error");
                                        // do something when 401 Unauthorized happened
                                        // e.g. delete credentials and forward to login screen
                                        // ...

                                        break;
                                    case 403:

                                        Log.wtf("===========333333333===========", "403 error");
                                        // do something when 403 Forbidden happened
                                        // e.g. delete credentials and forward to login screen
                                        // ...
                                        break;
                                    default:
                                        Log.e("tag?>?>?||<<<<", "Log error or do something else with error code:" + response.code());

                                        break;

                                }
                            }
                            return response;
                        } catch (IOException e) {
                            Log.e("tag", e.getMessage(), e);
                            Log.e("tag", "central network error handling");

                            throw e;

                        }
                    }
                })
                .build();
        return okClient;

    }


    public static Retrofit getClient() {

        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();


            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .client(getHeader())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
//            Log.wtf("--------'''''''   ", "" + SessionManager.getTOKEN());
        }
        return retrofit;
    }
}

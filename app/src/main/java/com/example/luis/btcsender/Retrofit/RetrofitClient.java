package com.example.luis.btcsender.Retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    private static Retrofit ripioInstance;
    private static Retrofit feeInstance;

    public static Retrofit getRipioInstance() {
        if (ripioInstance == null)
            ripioInstance = new Retrofit.Builder()
                    .baseUrl("https://ripio.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        return ripioInstance;
    }

    public static Retrofit getFeeInstance() {
        if (feeInstance == null)
            feeInstance = new Retrofit.Builder()
                    .baseUrl("https://bitcoinfees.earn.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        return feeInstance;
    }

    private RetrofitClient() {
    }
}
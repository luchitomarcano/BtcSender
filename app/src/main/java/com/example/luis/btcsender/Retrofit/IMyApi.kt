package com.example.luis.btcsender.Retrofit

import com.example.luis.btcsender.model.Fee
import com.example.luis.btcsender.model.RipioPojo

import io.reactivex.Observable
import retrofit2.http.GET

interface IMyAPI {

    @GET("/api/v1/rates/")
    fun getRates(): Observable<RipioPojo>

    @GET("api/v1/fees/recommended")
    fun getFee(): Observable<Fee>
}
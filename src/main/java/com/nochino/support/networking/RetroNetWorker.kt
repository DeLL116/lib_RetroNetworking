package com.nochino.support.networking

import com.google.gson.GsonBuilder
import retrofit2.*
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetroNetWorker(apiBaseUrl: String) {

    // Build Retrofit object with the provided apiBaseUrl for making API calls
    var retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(apiBaseUrl)
        .build()

    fun <T> createWebService(clazz: Class<T>): T {
        return retrofit.create(clazz)
    }
}
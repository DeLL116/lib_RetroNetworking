package com.nochino.support.networking

import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroNetWorker(
    apiBaseUrl: String,
    callAdapterFactory: CallAdapter.Factory,
    converterFactory: GsonConverterFactory
) {

    // Build Retrofit object with the provided apiBaseUrl for making API calls
    var retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(converterFactory)
        .addCallAdapterFactory(callAdapterFactory)
        .baseUrl(apiBaseUrl)
        .build()

    fun <T> createWebService(clazz: Class<T>): T {
        return retrofit.create(clazz)
    }
}
//package com.feedlink.feedlink.api
//
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object ApiClient {
//    private const val BASE_URL = "https://feedlink-210643547921.herokuapp.com/api/"
//
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    fun<T> buildApiClient(apiInterface: Class<T>): T {
//        return retrofit.create(apiInterface)
//    }
//}

package com.feedlink.feedlink.di

import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.repository.ListingsRepository
import com.feedlink.feedlink.viewmodel.ListingsViewModel
import com.feedlink.feedlink.viewmodel.ProductDetailViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://feedlink-210643547921.herokuapp.com/api/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ApiInterface> {
        get<Retrofit>().create(ApiInterface::class.java)
    }

    single { ListingsRepository(get<ApiInterface>()) }

    viewModel { ListingsViewModel(get()) }
    viewModel {
        ProductDetailViewModel(
            get(),

            )
    }
}
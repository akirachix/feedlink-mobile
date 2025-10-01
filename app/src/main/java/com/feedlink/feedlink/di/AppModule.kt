package com.feedlink.feedlink.di

import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.repository.AuthRepository
import com.feedlink.feedlink.repository.ListingsRepository
import com.feedlink.feedlink.viewmodel.ForgotPasswordViewModel
import com.feedlink.feedlink.viewmodel.ListingsViewModel
import com.feedlink.feedlink.viewmodel.ProductDetailViewModel
import com.feedlink.feedlink.viewmodel.SigninViewModel
import com.feedlink.feedlink.viewmodel.SignupViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://feedlink-210643547921.herokuapp.com/api/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ApiInterface> {
        get<Retrofit>().create(ApiInterface::class.java)
    }
}

val repositoryModule = module {
    single { AuthRepository(get()) }
    single { ListingsRepository(get()) }
}

val viewModelModule = module {
    // Auth ViewModels
    viewModel { SignupViewModel(get()) }
    viewModel { SigninViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }

    // Listings ViewModels
    viewModel { ListingsViewModel(get()) }
    viewModel { ProductDetailViewModel(get()) }
}

val appModules = listOf(networkModule, repositoryModule, viewModelModule)
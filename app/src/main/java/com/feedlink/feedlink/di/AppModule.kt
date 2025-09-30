package com.feedlink.feedlink.di

import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.repository.AuthRepository
import com.feedlink.feedlink.viewmodel.ForgotPasswordViewModel
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
            .baseUrl("https://feedlink-210643547921.herokuapp.com/api/") // ✅ YOUR API
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }

    single<ApiInterface> {
        get<Retrofit>().create(ApiInterface::class.java)
    }
}
    val repositoryModule = module {
        single { AuthRepository(get()) }
    }

    val viewModelModule = module {
        viewModel { SignupViewModel(get()) }
        viewModel { SigninViewModel(get()) }
        viewModel { ForgotPasswordViewModel(get()) }
    }

val appModules = listOf(networkModule, repositoryModule, viewModelModule)

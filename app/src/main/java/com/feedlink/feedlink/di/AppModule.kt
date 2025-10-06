package com.feedlink.feedlink.di
import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.api.AuthInterceptor
import com.feedlink.feedlink.repository.ProfileRepository
import com.feedlink.feedlink.viewmodel.ProfileViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val appModule = module {
    single { AuthInterceptor() }
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .addInterceptor(get<AuthInterceptor>())

            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("https://feedlink-210643547921.herokuapp.com/api/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single {
        get<Retrofit>().create(ApiInterface::class.java)
    }
    single { ProfileRepository(get()) }
    viewModel { ProfileViewModel(get()) }
}
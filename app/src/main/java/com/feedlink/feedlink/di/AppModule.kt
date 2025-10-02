package com.feedlink.feedlink.di

import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.repository.ListingRepository
import com.feedlink.feedlink.repository.WasteClaimRepository
import com.feedlink.feedlink.screens.TimerViewModelFactory
import com.feedlink.feedlink.viewModel.ListingViewModel
import com.feedlink.feedlink.viewModel.NotificationViewModel
import com.feedlink.feedlink.viewModel.WasteClaimViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://feedlink-210643547921.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }

    single<ApiInterface> {
        get<Retrofit>().create(ApiInterface::class.java)
    }
}

val repositoryModule = module {
    single { WasteClaimRepository(get()) }
    single { ListingRepository(get()) }
}

val viewModelModule = module {
    viewModel { WasteClaimViewModel(get()) }
    viewModel { ListingViewModel(get(), get()) }
    viewModel { NotificationViewModel() }

    factory { (claimId: Int) -> TimerViewModelFactory(claimId) }
}

val appModules = listOf(networkModule, repositoryModule, viewModelModule)
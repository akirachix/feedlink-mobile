package com.feedlink.feedlink.di

import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.repository.ListingRepository
import com.feedlink.feedlink.repository.WasteClaimRepository
import com.feedlink.feedlink.viewmodel.NotificationViewModel
import com.feedlink.feedlink.viewmodel.WasteClaimViewModel
import com.feedlink.feedlink.repository.AuthRepository
import com.feedlink.feedlink.repository.ListingsRepository
import com.feedlink.feedlink.viewmodel.ForgotPasswordViewModel
import com.feedlink.feedlink.viewmodel.ListingViewModel
import com.feedlink.feedlink.viewmodel.ListingsViewModel
import com.feedlink.feedlink.viewmodel.ProductDetailViewModel
import com.feedlink.feedlink.viewmodel.SigninViewModel
import com.feedlink.feedlink.viewmodel.SignupViewModel
import com.feedlink.feedlink.viewmodel.TimerViewModel
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

            level = HttpLoggingInterceptor.Level.BODY
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
    single { AuthRepository(get<ApiInterface>()) }
    single { WasteClaimRepository(get<ApiInterface>()) }
    single { ListingRepository(get<ApiInterface>()) }
    single { ListingsRepository (get<ApiInterface>())}
}

val viewModelModule = module {
    viewModel { SignupViewModel(get()) }
    viewModel { SigninViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { ListingsViewModel(get()) }
    viewModel { ProductDetailViewModel(get()) }
    viewModel { WasteClaimViewModel(get()) }
    viewModel { ListingViewModel(get(), get()) }
    viewModel { NotificationViewModel() }
    viewModel { (claimId: Int) -> TimerViewModel(get(), claimId) }


}

val appModules = listOf(networkModule, repositoryModule, viewModelModule)
package com.feedlink.feedlink.di

import com.feedlink.feedlink.api.ApiInterface
<<<<<<< HEAD
import com.feedlink.feedlink.repository.ListingRepository
import com.feedlink.feedlink.repository.WasteClaimRepository
import com.feedlink.feedlink.screens.TimerViewModelFactory
import com.feedlink.feedlink.viewModel.ListingViewModel
import com.feedlink.feedlink.viewModel.NotificationViewModel
import com.feedlink.feedlink.viewModel.WasteClaimViewModel
=======
import com.feedlink.feedlink.repository.AuthRepository
import com.feedlink.feedlink.repository.ListingsRepository
import com.feedlink.feedlink.viewmodel.ForgotPasswordViewModel
import com.feedlink.feedlink.viewmodel.ListingsViewModel
import com.feedlink.feedlink.viewmodel.ProductDetailViewModel
import com.feedlink.feedlink.viewmodel.SigninViewModel
import com.feedlink.feedlink.viewmodel.SignupViewModel
>>>>>>> 93200a6b2ef82ae4c36044f694a6b170c8be4d57
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
<<<<<<< HEAD
import java.util.concurrent.TimeUnit
=======
>>>>>>> 93200a6b2ef82ae4c36044f694a6b170c8be4d57

val networkModule = module {
    single {
        HttpLoggingInterceptor().apply {
<<<<<<< HEAD
            level = HttpLoggingInterceptor.Level.BASIC
=======
            level = HttpLoggingInterceptor.Level.BODY
>>>>>>> 93200a6b2ef82ae4c36044f694a6b170c8be4d57
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
<<<<<<< HEAD
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
=======
>>>>>>> 93200a6b2ef82ae4c36044f694a6b170c8be4d57
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://feedlink-210643547921.herokuapp.com/api/")
<<<<<<< HEAD
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
=======
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
>>>>>>> 93200a6b2ef82ae4c36044f694a6b170c8be4d57
            .build()
    }

    single<ApiInterface> {
        get<Retrofit>().create(ApiInterface::class.java)
    }
}

val repositoryModule = module {
<<<<<<< HEAD
    single { WasteClaimRepository(get()) }
    single { ListingRepository(get()) }
}

val viewModelModule = module {
    viewModel { WasteClaimViewModel(get()) }
    viewModel { ListingViewModel(get(), get()) }
    viewModel { NotificationViewModel() }

    factory { (claimId: Int) -> TimerViewModelFactory(claimId) }
=======
    single { AuthRepository(get()) }
    single { ListingsRepository(get()) }
}

val viewModelModule = module {
    viewModel { SignupViewModel(get()) }
    viewModel { SigninViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }

    viewModel { ListingsViewModel(get()) }
    viewModel { ProductDetailViewModel(get()) }
>>>>>>> 93200a6b2ef82ae4c36044f694a6b170c8be4d57
}

val appModules = listOf(networkModule, repositoryModule, viewModelModule)
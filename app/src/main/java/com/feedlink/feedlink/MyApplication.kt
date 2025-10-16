package com.feedlink.feedlink

import android.app.Application
import com.feedlink.feedlink.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.feedlink.feedlink.di.networkModule
import com.feedlink.feedlink.di.repositoryModule
import com.feedlink.feedlink.di.viewModelModule

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(
                networkModule,
                repositoryModule,
                viewModelModule,
                appModule
            )
        }
    }
}
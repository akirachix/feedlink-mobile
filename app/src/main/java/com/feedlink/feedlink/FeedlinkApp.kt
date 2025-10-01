package com.feedlink.feedlink

import android.app.Application
import com.feedlink.feedlink.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FeedlinkApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FeedlinkApp)
            modules(appModule)
        }
    }
}
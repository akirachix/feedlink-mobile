package com.feedlink.feedlink

import android.app.Application
import android.util.Log
import com.feedlink.feedlink.di.appModules
import com.feedlink.feedlink.utils.NotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FeedlinkAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            Log.e("CRASH", "Uncaught exception", throwable)
        }

        try {
            NotificationManager.createNotificationChannel(this)

            startKoin {
                androidContext(this@FeedlinkAppApplication)
                modules(appModules)
            }
            Log.d("APP", "Koin initialized successfully")
        } catch (e: Exception) {
            Log.e("CRASH", "Startup failed", e)
            throw e
        }
    }
}
//package com.feedlink.feedlink
//import android.app.Application
//import com.feedlink.feedlink.di.appModule
//import org.koin.android.ext.koin.androidContext
//import org.koin.core.context.startKoin
//class FeedlinkApplication : Application() {
//    override fun onCreate() {
//        super.onCreate()
//        startKoin {
//            androidContext(this@FeedlinkApplication)
//            modules(appModule)
//        }
//    }
//}
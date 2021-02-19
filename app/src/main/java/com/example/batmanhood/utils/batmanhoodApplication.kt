package com.example.batmanhood.utils

import android.app.Application
import com.example.batmanhood.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ApplicationComponent
import timber.log.Timber

@HiltAndroidApp
class batmanhoodApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
package com.leri.smovbook

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmovBookApp : Application() {

    companion object {
        private var appContext: Application? = null
        fun getContext(): Context {
            return appContext!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

}
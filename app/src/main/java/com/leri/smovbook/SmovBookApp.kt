package com.leri.smovbook

import android.app.Application
import android.content.Context
import com.leri.smovbook.data.AppContainer
import com.leri.smovbook.data.AppContainerImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmovBookApp : Application() {
    lateinit var container: AppContainer

    companion object {
        private var appContext: Application? = null
        fun getContext(): Context {
            return appContext!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        container = AppContainerImpl(this)
    }

}
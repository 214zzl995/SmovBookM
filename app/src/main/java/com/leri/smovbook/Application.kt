package com.leri.smovbook

import android.app.Application
import android.content.Context
import com.leri.smovbook.data.AppContainer
import com.leri.smovbook.data.AppContainerImpl

class Application : Application() {
    lateinit var container: AppContainer

    companion object {
        var _context: Application? = null
        fun getContext(): Context {
            return _context!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }

}
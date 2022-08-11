package com.leri.smovbook.data

import android.content.Context
import com.leri.smovbook.config.SettingsRepository
import com.leri.smovbook.config.impl.SettingsRepositoryImpl
import com.leri.smovbook.data.smov.SmovRepository
import com.leri.smovbook.data.smov.impl.SmovRepositoryImpl

interface AppContainer {
    val smovRepository: SmovRepository
    val settingsRepository: SettingsRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val smovRepository: SmovRepository by lazy {
        SmovRepositoryImpl()
    }
    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl()
    }
}

package com.leri.smovbook.config.impl

import androidx.datastore.core.DataStore
import com.leri.smovbook.Application
import com.leri.smovbook.config.Settings
import com.leri.smovbook.config.SettingsRepository
import com.leri.smovbook.config.settingsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * @Description: Settings接口实现
 * @author DingWei
 * @version 11/8/2022 上午9:43
 */
class SettingsRepositoryImpl : SettingsRepository {
    private val settingsDataStore: DataStore<Settings> = Application.getContext().settingsDataStore

    override suspend fun getServerUrl(): String {
        return settingsDataStore.data
            .map { settings ->
                settings.serverUrl
            }.first().toString()
    }

    override suspend fun changeServerUrl(url: String) {
        settingsDataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setServerUrl(url)
                .build()
        }
    }

}
package com.leri.smovbook.datastore.impl

import android.content.Context
import androidx.datastore.core.DataStore
import com.leri.smovbook.config.Settings
import com.leri.smovbook.datastore.SmovDataStore
import com.leri.smovbook.datastore.serializer.settingsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


/**
 * @Description: Settings接口实现
 * @author DingWei
 * @version 11/8/2022 上午9:43
 */
class SmovDataStoreImpl(context: Context) : SmovDataStore {
    private val settingsDataStore: DataStore<Settings> = context.settingsDataStore

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
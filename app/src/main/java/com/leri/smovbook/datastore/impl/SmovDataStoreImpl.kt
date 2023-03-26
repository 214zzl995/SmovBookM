package com.leri.smovbook.datastore.impl

import android.content.Context
import androidx.datastore.core.DataStore
import com.leri.smovbook.config.Settings
import com.leri.smovbook.datastore.SmovDataStore
import com.leri.smovbook.datastore.serializer.settingsDataStore
import com.leri.smovbook.ui.home.ServerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.HttpUrl.Companion.toHttpUrl


/**
 * @Description: Settings接口实现
 * @author DingWei
 * @version 11/8/2022 上午9:43
 */
class SmovDataStoreImpl(context: Context) : SmovDataStore {
    private val settingsDataStore: DataStore<Settings> = context.settingsDataStore

    override val serverUrl: Flow<String>
        get() =
            settingsDataStore.data.map { settings ->
                settings.serverUrl
            }.flowOn(Dispatchers.IO).onEmpty {
                emit("127.0.0.1")
            }


    override val serverUrlAndPort: Flow<String>
        get() =
            settingsDataStore.data.map { settings ->
                settings.serverUrl + ":" + settings.serverPort
            }.flowOn(Dispatchers.IO)

    override val serverPort: Flow<Int>
        get() =
            settingsDataStore.data.map { settings ->
                settings.serverPort
            }.flowOn(Dispatchers.IO)


    override val historyUrl: Flow<MutableList<String>>
        get() =
            settingsDataStore.data.map { settings ->
                settings.historyUrlList
            }.flowOn(Dispatchers.IO)

    override val serviceState: Flow<ServerState>
        get() = settingsDataStore.data.map { settings ->
            ServerState(settings.serverUrl + ":" + settings.serverPort, settings.historyUrlList)
        }.flowOn(Dispatchers.IO)


    override suspend fun changeServerUrl(url: String) {
        //分隔url 为 base 和 port
        val httpUrl = "http://$url".toHttpUrl()
        settingsDataStore.updateData { currentSettings ->
            val historyUrl = currentSettings.historyUrlList.toMutableList()
            historyUrl.removeIf { it.equals(url) }
            val historyCount =
                if (currentSettings.historyCount == 0) 10 else currentSettings.historyCount

            if (historyUrl.size == historyCount) {
                historyUrl.removeAt(0)
            }
            historyUrl.add(url)

            currentSettings.toBuilder().setServerUrl(httpUrl.host).setServerPort(httpUrl.port)
                .clearHistoryUrl()
                .addAllHistoryUrl(historyUrl).build()
        }
    }

}
package com.leri.smovbook.datastore.impl

import android.content.Context
import androidx.datastore.core.DataStore
import com.leri.smovbook.config.Settings
import com.leri.smovbook.datastore.SmovDataStore
import com.leri.smovbook.datastore.serializer.settingsDataStore
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

    override val thirdPartyPlayer: Flow<String>
        get() = settingsDataStore.data.map { settings ->
            settings.thirdPartyPlayer
        }.flowOn(Dispatchers.IO)


    override suspend fun changeServerUrl(url: String) {
        val httpUrl = "http://$url".toHttpUrl()
        settingsDataStore.updateData { currentSettings ->

            val historyUrl = currentSettings.historyUrlList.toMutableList()
            //判断当前url 是否已经存在
            if (!historyUrl.contains(url)) {
                historyUrl.add(url)
            }

            currentSettings.toBuilder().setServerUrl(httpUrl.host).setServerPort(httpUrl.port)
                .clearHistoryUrl().addAllHistoryUrl(historyUrl).build()
        }
    }

}
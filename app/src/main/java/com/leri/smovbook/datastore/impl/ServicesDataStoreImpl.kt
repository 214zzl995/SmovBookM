package com.leri.smovbook.datastore.impl

import android.content.Context
import androidx.datastore.core.DataStore
import com.leri.smovbook.config.Services
import com.leri.smovbook.datastore.ServicesDataStore

import com.leri.smovbook.datastore.serializer.servicesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.HttpUrl.Companion.toHttpUrl


/**
 * @Description: services接口实现
 * @author DingWei
 * @version 11/8/2022 上午9:43
 */
class ServicesDataStoreImpl(context: Context) : ServicesDataStore {
    private val servicesDataStore: DataStore<Services> = context.servicesDataStore

    override val serverUrl: Flow<String>
        get() =
            servicesDataStore.data.map { services ->
                services.serverUrl
            }.flowOn(Dispatchers.IO).onEmpty {
                emit("127.0.0.1")
            }


    override val serverUrlAndPort: Flow<String>
        get() =
            servicesDataStore.data.map { services ->
                services.serverUrl + ":" + services.serverPort
            }.flowOn(Dispatchers.IO)

    override val serverPort: Flow<Int>
        get() =
            servicesDataStore.data.map { services ->
                services.serverPort
            }.flowOn(Dispatchers.IO)


    override val historyUrl: Flow<MutableList<String>>
        get() =
            servicesDataStore.data.map { services ->
                services.historyUrlList
            }.flowOn(Dispatchers.IO)


    override suspend fun addServerUrl(url: String) {
        val httpUrl = "http://$url".toHttpUrl()
        servicesDataStore.updateData { currentServices ->

            val historyUrl = currentServices.historyUrlList.toMutableList()
            //判断当前url 是否已经存在
            if (!historyUrl.contains(url)) {
                historyUrl.add(url)
            }

            currentServices.toBuilder().setServerUrl(httpUrl.host).setServerPort(httpUrl.port)
                .clearHistoryUrl().addAllHistoryUrl(historyUrl).build()
        }
    }

    override suspend fun removeServerUrl(url: String) {
        servicesDataStore.updateData { currentServices ->
            val historyUrl = currentServices.historyUrlList.toMutableList()
            historyUrl.remove(url)
            currentServices.toBuilder().clearHistoryUrl().addAllHistoryUrl(historyUrl).build()
        }
    }

    override suspend fun changeServerUrl(index: Int, url: String) {
        servicesDataStore.updateData { currentServices ->
            val historyUrl = currentServices.historyUrlList.toMutableList()
            historyUrl[index] = url
            currentServices.toBuilder().clearHistoryUrl().addAllHistoryUrl(historyUrl).build()
        }
    }


}
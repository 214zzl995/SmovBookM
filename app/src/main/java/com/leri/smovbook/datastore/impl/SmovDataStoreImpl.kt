package com.leri.smovbook.datastore.impl

import android.content.Context
import androidx.datastore.core.DataStore
import com.leri.smovbook.config.Settings
import com.leri.smovbook.datastore.SmovDataStore
import com.leri.smovbook.datastore.serializer.settingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.internal.toImmutableList


/**
 * @Description: Settings接口实现
 * @author DingWei
 * @version 11/8/2022 上午9:43
 */
class SmovDataStoreImpl(context: Context) : SmovDataStore {
    private val settingsDataStore: DataStore<Settings> = context.settingsDataStore

    override suspend fun getServerUrl(): Flow<String> {
        return settingsDataStore.data
            .map { settings ->
                settings.serverUrl
            }
    }

    override suspend fun getServerPort(): Flow<Int> {
        return settingsDataStore.data
            .map { settings ->
                settings.serverPort
            }
    }

    override suspend fun getHistoryUrl(): Flow<MutableList<String>> {
        return settingsDataStore.data
            .map { settings ->
                settings.historyUrlList
            }
    }

    override suspend fun changeServerUrl(url: String) {

        //分隔url 为 base 和 port
        val httpUrl = "http://$url".toHttpUrlOrNull()

        if (httpUrl != null) {
            settingsDataStore.updateData { currentSettings ->
                //设置可变修改
                //currentSettings.ensureHistoryUrlIsMutable()
                //获取当前的historyUrl
                //val historyUrl = currentSettings.historyUrlList
                var historyUrl = currentSettings.historyUrlList.toImmutableList()

                //当插入的url在historyUrl中已存在 删除当前存在的项目
                //historyUrl.removeIf {
                //it.contains(url)
                //}
                historyUrl = historyUrl.filterNot { it.equals(url) }


                //当前count
                val historyCount = if (currentSettings.historyCount == 0) 10 else currentSettings.historyCount

                //当档案数量等于count数量时情理一位
                //if (historyUrl.size == historyCount) {
                //historyUrl.removeFirst()
                //}

                if (historyUrl.size == historyCount) {
                    historyUrl = historyUrl.drop(0)
                }

                //插入新的url
                //historyUrl.add(url)
                historyUrl = historyUrl.plus(url)

                //更新物理数据
                currentSettings.toBuilder()
                    .setServerUrl(httpUrl.host)
                    .setServerPort(httpUrl.port)
                    .clearHistoryUrl()
                    .addAllHistoryUrl(historyUrl)
                    .build()
            }
        }
    }

}
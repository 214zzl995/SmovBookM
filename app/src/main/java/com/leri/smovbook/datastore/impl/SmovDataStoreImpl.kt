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
/*        settingsDataStore.updateData { currentSettings ->
            //设置可变修改
            //currentSettings.ensureHistoryUrlIsMutable()
            //获取当前的historyUrl
            val historyUrl = currentSettings.historyUrlList
            //当插入的url在historyUrl中已存在 更新位置
            historyUrl.removeIf {
                it.contains(url)
            }
            //当前count
            val historyCount = if (currentSettings.historyCount == 0) 10 else currentSettings.historyCount

            //当档案数量等于count数量时情理一位
            if (historyUrl.size == historyCount) {
                historyUrl.removeFirst()
            }

            historyUrl.add(url)

            currentSettings.toBuilder()
                .setServerUrl(url)
                .clearHistoryUrl()
                .addAllHistoryUrl(historyUrl)
                .build()
        }*/
    }

}
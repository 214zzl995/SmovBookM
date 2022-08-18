package com.leri.smovbook.config.impl

import androidx.datastore.core.DataStore
import com.leri.smovbook.SmovBookApp
import com.leri.smovbook.config.Settings
import com.leri.smovbook.config.SettingsRepository
import com.leri.smovbook.datastore.serializer.settingsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.internal.toImmutableList

/**
 * @Description: Settings接口实现
 * @author DingWei
 * @version 11/8/2022 上午9:43
 */
class SettingsRepositoryImpl : SettingsRepository {
    private val settingsDataStore: DataStore<Settings> = SmovBookApp.getContext().settingsDataStore

    override suspend fun getServerUrl(): String {
        return settingsDataStore.data
            .map { settings ->
                settings.serverUrl
            }.first().toString()
    }

    override suspend fun getHisUrl(): String {
        return settingsDataStore.data
            .map { settings ->
                println("测试hisUrl" + settings.historyUrlList)
                settings.historyUrlList
            }.toString()
    }

    override suspend fun changeServerUrl(url: String) {

        settingsDataStore.updateData { currentSettings ->
            //currentSettings.ensureIsMutable()
            //获取当前的historyUrl
            /*            val historyUrl = currentSettings.historyUrlList
                        //当插入的url在historyUrl中已存在 更新位置

                        println("测试hisUrl${historyUrl.javaClass}")

                        println("测试hisUrl$historyUrl")

                        historyUrl.removeIf {
                            it.equals(url)
                        }
                        //当前count
                        //当前count
                        val historyCount = if (currentSettings.historyCount == 0) 10 else currentSettings.historyCount

                        //当档案数量等于count数量时情理一位
                        if (historyUrl.size == historyCount) {
                            historyUrl.removeFirst()
                        }

                        historyUrl.add(url)*/

            var historyUrl = currentSettings.historyUrlList.toImmutableList()

            println("测试hisUrl1$historyUrl")

            historyUrl =historyUrl.filterNot { it.equals(url) }

            println("测试hisUrl2$historyUrl")

            val historyCount = if (currentSettings.historyCount == 0) 10 else currentSettings.historyCount

            if (historyUrl.size == historyCount) {
                historyUrl = historyUrl.drop(0)
            }

            historyUrl = historyUrl.plus(url)

            println("测试hisUrl3$historyUrl")

            currentSettings.toBuilder()
                .setServerUrl(url)
                .clearHistoryUrl()
                .addAllHistoryUrl(historyUrl)
                .build()
        }
    }


}
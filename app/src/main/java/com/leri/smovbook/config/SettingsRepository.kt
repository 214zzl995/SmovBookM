package com.leri.smovbook.config

import com.example.jetnews.data.Result
import com.leri.smovbook.model.Smov

/**
 * @Description: Settings接口
 * @author DingWei
 * @version 11/8/2022 上午9:42
 */
interface SettingsRepository {

    suspend fun getServerUrl(): String

    suspend fun changeServerUrl(url: String)

}
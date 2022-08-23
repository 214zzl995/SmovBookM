package com.leri.smovbook.datastore

import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface SmovDataStore {

    val serverUrl: Flow<String>

    val serverUrlAndPort: Flow<String>

    val serverPort: Flow<Int>

    val historyUrl: Flow<MutableList<String>>

    suspend fun changeServerUrl(url: String)


}
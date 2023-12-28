package com.leri.smovbook.datastore

import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface ServicesDataStore {

    val serverUrl: Flow<String>

    val serverUrlAndPort: Flow<String>

    val serverPort: Flow<Int>

    val historyUrl: Flow<MutableList<String>>

    suspend fun addServerUrl(url: String)

    suspend fun removeServerUrl(url: String)

    suspend fun changeServerUrl(index: Int, url: String)


}
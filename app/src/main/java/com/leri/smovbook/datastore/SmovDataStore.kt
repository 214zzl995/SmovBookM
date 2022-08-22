package com.leri.smovbook.datastore

import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface SmovDataStore {

    val serverUrl: Flow<String>

    val serverUrlAndPort: Flow<String>

    suspend fun getServerPort(): Flow<Int>

    suspend fun changeServerUrl(url: String)

    suspend fun getHistoryUrl(): Flow<MutableList<String>>
}
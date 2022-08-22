package com.leri.smovbook.datastore

import kotlinx.coroutines.flow.Flow

interface SmovDataStore {
    suspend fun getServerUrl(): Flow<String>

    suspend fun getServerUrlAndPort():Flow<String>

    suspend fun getServerPort(): Flow<Int>

    suspend fun changeServerUrl(url: String)

    suspend fun getHistoryUrl(): Flow<MutableList<String>>
}
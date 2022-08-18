package com.leri.smovbook.datastore

import kotlinx.coroutines.flow.Flow

interface SmovDataStore {
    suspend fun getServerUrl(): String

    suspend fun changeServerUrl(url: String)

    suspend fun getHistoryUrl(): Flow<MutableList<String>>
}
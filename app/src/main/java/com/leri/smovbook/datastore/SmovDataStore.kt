package com.leri.smovbook.datastore

interface SmovDataStore {
    suspend fun getServerUrl(): String

    suspend fun changeServerUrl(url: String)
}
package com.leri.smovbook.datastore

import com.leri.smovbook.ui.home.ServerState
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface SmovDataStore {

    val serverUrl: Flow<String>

    val serverUrlAndPort: Flow<String>

    val serverPort: Flow<Int>

    val historyUrl: Flow<MutableList<String>>

    val serviceState: Flow<ServerState>

    val thirdPartyPlayer: Flow<String>

    suspend fun changeServerUrl(url: String)


}
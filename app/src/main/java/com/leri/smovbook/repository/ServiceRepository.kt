package com.leri.smovbook.repository

import androidx.annotation.WorkerThread
import com.leri.smovbook.datastore.SmovDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import timber.log.Timber

class ServiceRepository(
    private val smovDataStore: SmovDataStore,
    private val dispatcher: Dispatcher,
) : Repository {

    init {
        Timber.d("ServiceRepository")
    }

    @WorkerThread
    fun getSmovServiceUrl() = smovDataStore.serverUrl

    @WorkerThread
    fun getSmovServiceUrlAndPort() = smovDataStore.serverUrlAndPort

    @WorkerThread
    fun getSmovHistoryUrl() = smovDataStore.historyUrl

    @WorkerThread
    fun changeSmovServiceUrl(url: String) = runBlocking {
        dispatcher.cancelAll()
        smovDataStore.changeServerUrl(url)
    }
}
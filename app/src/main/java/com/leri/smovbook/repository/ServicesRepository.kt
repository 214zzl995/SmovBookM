package com.leri.smovbook.repository

import androidx.annotation.WorkerThread
import com.leri.smovbook.datastore.ServicesDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import timber.log.Timber

class ServicesRepository(
    private val servicesDataStore: ServicesDataStore,
    private val dispatcher: Dispatcher,
) : Repository {

    init {
        Timber.d("ServiceRepository")
    }

    @WorkerThread
    fun getSmovServiceUrl() = servicesDataStore.serverUrl

    @WorkerThread
    fun getSmovServiceUrlAndPort() = servicesDataStore.serverUrlAndPort

    @WorkerThread
    fun getSmovHistoryUrl() = servicesDataStore.historyUrl

    @WorkerThread
    fun changeSmovServiceUrl(url: String) = runBlocking {
        dispatcher.cancelAll()
        servicesDataStore.changeServerUrl(url)
    }
}
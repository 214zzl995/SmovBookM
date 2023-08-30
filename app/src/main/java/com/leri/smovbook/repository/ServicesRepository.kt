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
    fun addServiceUrl(url: String) = runBlocking {
        dispatcher.cancelAll()
        servicesDataStore.addServerUrl(url)
    }

    @WorkerThread
    fun removeServiceUrl(url: String) = runBlocking {
        dispatcher.cancelAll()
        servicesDataStore.removeServerUrl(url)
    }

    @WorkerThread
    fun changeServerUrl(index: Int, url: String) = runBlocking {
        dispatcher.cancelAll()
        servicesDataStore.changeServerUrl(index, url)
    }


}
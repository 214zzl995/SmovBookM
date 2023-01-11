package com.leri.smovbook.repository

import androidx.annotation.WorkerThread
import com.leri.smovbook.datastore.SmovDataStore
import com.leri.smovbook.network.service.SmovService
import com.skydoves.sandwich.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import timber.log.Timber

/**
 * @Description: XXX
 * @author DingWei
 * @version 12/8/2022 下午2:29
 */
class SmovRepository(
    private val smovService: SmovService,
    private val smovDataStore: SmovDataStore,
    private val dispatcher: Dispatcher,
) : Repository {

    init {
        Timber.d("Injection SmovRepository")
    }

    @WorkerThread
    fun getSmovAll() = flow {
        val response = smovService.getAllSmov()
        response.suspendOnSuccess {
            //Timber.d(data.toString())
            emit(data.data)
        }.suspendOnError {
            message()
        }
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun getSmovPagination(pageNum: Int, pageSize: Int, success: () -> Unit, error: () -> Unit) =
        flow {
            val response = smovService.getPaginationSmov(pageNum, pageSize)
            response.suspendOnSuccess {
                //Timber.d(data.toString())
                emit(data.data.list)
                success()
            }.onError {
                error()
            }.onException {
                error()
            }
        }.onCompletion {
            //success()
        }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun getSmovById(id: Long, success: () -> Unit, error: () -> Unit) = flow {
        val response = smovService.getSmovById(id)
        response.suspendOnSuccess {
            Timber.d(data.toString())
            emit(data.data)
        }.onError {
            error()
        }.onException { error() }
    }.onCompletion { success() }.flowOn(Dispatchers.IO)


    @WorkerThread
    fun getSmovServiceUrl() = smovDataStore.serverUrl

    @WorkerThread
    fun getSmovServiceUrlAndPort() = smovDataStore.serverUrlAndPort

    @WorkerThread
    fun getServerState() = smovDataStore.serviceState

    @WorkerThread
    fun getSmovHistoryUrl() = smovDataStore.historyUrl

    @WorkerThread
    fun changeSmovServiceUrl(url: String) = runBlocking {
        smovDataStore.changeServerUrl(url)
    }

    @WorkerThread
    fun cancelAll() = run { dispatcher.cancelAll() }

}
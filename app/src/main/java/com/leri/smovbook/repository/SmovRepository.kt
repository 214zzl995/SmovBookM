package com.leri.smovbook.repository

import androidx.annotation.WorkerThread
import com.leri.smovbook.datastore.SmovDataStore
import com.leri.smovbook.network.service.SmovService
import com.skydoves.sandwich.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.http.Query
import timber.log.Timber

/**
 * @Description: XXX
 * @author DingWei
 * @version 12/8/2022 下午2:29
 */
class SmovRepository constructor(
    private val smovService: SmovService,
    private val smovDataStore: SmovDataStore
) : Repository {

    init {
        Timber.d("Injection SmovRepository")
    }

    @WorkerThread
    fun getSmovAll() = flow {
        val response = smovService.getAllSmov()
        response.suspendOnSuccess {
            Timber.d(data.toString())
            emit(data.data)
        }.suspendOnError {
            message()
        }
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun getSmovPagination(pageNum: Int, pageSize: Int, success: () -> Unit, error: () -> Unit) = flow {
        val response = smovService.getPaginationSmov(pageNum, pageSize)
        response.suspendOnSuccess {
            Timber.d(data.toString())
            emit(data.data.list)
        }.onError {
            error()
        }.onException { error() }
    }.onCompletion { success() }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun getSmovServiceUrl() = flow<String> {
        smovDataStore.getServerUrl()
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun getSmovServiceUrlAndPort() = flow<String> {
        smovDataStore.getServerUrlAndPort().first()
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun getSmovHistoryUrl() = flow<MutableList<String>> {
        smovDataStore.getHistoryUrl()
    }.flowOn(Dispatchers.IO)

    fun changeSmovServiceUrl(url: String) = runBlocking {
        smovDataStore.changeServerUrl(url)
    }

}
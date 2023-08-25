package com.leri.smovbook.repository

import androidx.annotation.WorkerThread
import com.leri.smovbook.network.service.SmovService
import com.skydoves.sandwich.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import timber.log.Timber

/**
 * @Description: XXX
 * @author DingWei
 * @version 12/8/2022 下午2:29
 */
class SmovRepository(
    private val smovService: SmovService,
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
            success()
        }.onError {
            error()
        }.onException { error() }
    }.onCompletion {  }.flowOn(Dispatchers.IO)


}
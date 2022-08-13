package com.leri.smovbook.repository

import androidx.annotation.WorkerThread
import com.leri.smovbook.network.service.SmovService
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

/**
 * @Description: XXX
 * @author DingWei
 * @version 12/8/2022 下午2:29
 */
class SmovRepository constructor(
    private val smovService: SmovService
) : Repository {

    init {
        Timber.d("Injection SmovRepository")
    }

    @WorkerThread
    fun getSmovTest() = flow<String> {

        val response = smovService.getAllSmovTest()
        response.suspendOnSuccess {
            Timber.d(data)
        }

    }.flowOn(Dispatchers.IO)


}
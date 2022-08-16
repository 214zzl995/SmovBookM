package com.leri.smovbook.repository

import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import com.leri.smovbook.datastore.SmovDataStore
import com.leri.smovbook.network.service.SmovService
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

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
    fun getSmovTest() = flow<String> {

        val response = smovService.getAllSmovTest()
        response.suspendOnSuccess {
            Timber.d(data.first())
            emit(data.first())
        }


    }.flowOn(Dispatchers.IO)


}
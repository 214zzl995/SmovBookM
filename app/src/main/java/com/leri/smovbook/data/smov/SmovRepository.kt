package com.leri.smovbook.data.smov

import com.leri.smovbook.model.Smov
import com.leri.smovbook.data.Result

interface SmovRepository {
    suspend fun getSmovsAsync(serverUrl: String): Result<Smov>

    suspend fun getSmovs(serverUrl: String): Result<Smov>

}
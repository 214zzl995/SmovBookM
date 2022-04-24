package com.leri.smovbook.data.smov

import com.example.jetnews.data.Result
import com.leri.smovbook.model.Smov

interface SmovRepository {
    suspend fun getSmovsAsync(serverUrl: String): Result<Smov>

    suspend fun getSmovs(serverUrl: String): Result<Smov>

}
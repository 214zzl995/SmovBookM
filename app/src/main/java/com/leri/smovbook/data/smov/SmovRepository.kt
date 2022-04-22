package com.leri.smovbook.data.smov

import com.example.jetnews.data.Result
import com.leri.smovbook.model.Smov

interface SmovRepository {
    suspend fun getSmovs(serverUrl: String): Result<Smov>

}
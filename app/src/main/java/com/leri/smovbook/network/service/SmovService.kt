package com.leri.smovbook.network.service

import com.leri.smovbook.models.ServerResult
import com.leri.smovbook.models.network.SmovResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET

/**
 * @Description: XXX
 * @author DingWei
 * @version 12/8/2022 下午1:39
 */
interface SmovService {

    @GET("/data")
    suspend fun getAllSmov(): ApiResponse<ServerResult<SmovResponse>>

    @GET("/214zzl995/e5d0c48057c3f9474972d3515c6ccc6c/raw/2bb3088dbaaf6a09fb294e34bcb3b01575339fb1")
    suspend fun getAllSmovTest(): ApiResponse<Array<String>>

}
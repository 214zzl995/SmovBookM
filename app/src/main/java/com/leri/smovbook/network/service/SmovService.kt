package com.leri.smovbook.network.service

import com.leri.smovbook.models.ServerResult
import com.leri.smovbook.models.network.SmovResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * @Description: XXX
 * @author DingWei
 * @version 12/8/2022 下午1:39
 */
interface SmovService {

    @GET("/data")
    suspend fun getAllSmov(): ApiResponse<ServerResult<SmovResponse>>

    @GET("/data")
    suspend fun getAllSmovTest(): ApiResponse<Array<String>>

}
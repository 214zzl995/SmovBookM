package com.leri.smovbook.network.service

import com.leri.smovbook.models.ServerResult
import com.leri.smovbook.models.network.SmovPaginationResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * @Description: XXX
 * @author DingWei
 * @version 12/8/2022 下午1:39
 */
interface SmovService {

    @GET("/smovbook/data/pagination")
    suspend fun getAllSmov(): ApiResponse<ServerResult<SmovPaginationResponse>>

    @GET("/smovbook/data/pagination")
    suspend fun getPaginationSmov(
        @Query("page_num") pageNum: Int,
        @Query("page_size") pageSize: Int
    ): ApiResponse<ServerResult<SmovPaginationResponse>>

}
package com.leri.smovbook.models.network

import androidx.compose.runtime.Immutable
import com.leri.smovbook.models.NetworkResponseModel
import com.leri.smovbook.models.entities.Smov

/**
 * @Description: 主页数据返回
 * @author DingWei
 * @version 12/8/2022 上午11:49
 * @param page 页码
 * @param results 结果集
 * @param total_results 总结果数
 * @param total_pages 总页数
 */
@Immutable
data class SmovResponse(
    val page: Int,
    val results: List<Smov>,
    val total_results: Int,
    val total_pages: Int
) : NetworkResponseModel

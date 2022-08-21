package com.leri.smovbook.models.network

import androidx.compose.runtime.Immutable
import com.leri.smovbook.models.NetworkResponseModel
import com.leri.smovbook.models.entities.Smov

/**
 * @Description: 主页数据返回
 * @author DingWei
 * @version 12/8/2022 上午11:49
 * @param page_num 页码
 * @param total 总结果数
 * @param total_pages 总页数
 * @param list 结果集
 */
@Immutable
data class SmovPaginationResponse(
    val page_num: Int,
    val total: Int,
    val total_pages: Int,
    val list: List<Smov>
) : NetworkResponseModel

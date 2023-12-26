package com.leri.smovbook.models.entities

import androidx.compose.runtime.Immutable

/**
 * @Description: Actor实体类
 * @author DingWei
 * @version 12/8/2022 上午11:30
 */
@Immutable
data class SmovAttr(
    var id: Int,
    var name: String
)
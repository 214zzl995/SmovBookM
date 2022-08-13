package com.leri.smovbook.models.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity

/**
 * @Description: Actor实体类
 * @author DingWei
 * @version 12/8/2022 上午11:30
 */
@Immutable
@Entity(primaryKeys = [("id")])
data class Actor(
    var id: Int,
    var name: String,
)
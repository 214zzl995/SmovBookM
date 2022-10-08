package com.leri.smovbook.models.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity

/**
 * @Description: Tag实体类
 * @author DingWei
 * @version 12/8/2022 上午11:29
 */
@Immutable
@Entity(primaryKeys = [("id")])
data class Tag(
    override var id: Int,
    override var name: String,
) : DetailModel {
    override fun getType(): Type {
        return Type.TAG
    }
}
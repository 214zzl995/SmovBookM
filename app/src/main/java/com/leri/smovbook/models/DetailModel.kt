package com.leri.smovbook.models.entities

import androidx.compose.runtime.Immutable

@Immutable
interface DetailModel {
    val id: Int
    val name: String
    fun getType(): Type
}


enum class Type(val index: Int, val Name: String) {
    ACTOR(0, "Actor"),
    TAG(1, "Tag")
}


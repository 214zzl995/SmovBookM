package com.leri.smovbook.models.entities

data class ImageFile(
    val path: String,
    val size: Size,
)

data class Size(
    val width: Int,
    val height: Int,
)
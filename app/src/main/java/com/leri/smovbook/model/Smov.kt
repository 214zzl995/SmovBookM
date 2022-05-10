package com.leri.smovbook.model

data class SmovItem(
    var id: Int? = 0,
    var name: String,
    var title: String,
    var path: String,
    var realname: String,
    var len: Long,
    var created: Long,
    var modified: Long,
    var extension: String,
    var format: String,
    var release_time: String,
    var duration: Int,
    var maker: String,
    var maker_id: Int,
    var publisher: String,
    var publisher_id: Int,
    var serie: String,
    var serie_id: Int,
    var director: String,
    var director_id: Int,
    var tags: List<Tag> = listOf(),
    var actors: List<Actor> = listOf(),
    var isch: Boolean,
    var thumbs_img: String,
    var main_img: String,
    var detail_img: List<String> = listOf()
)

data class Smov(
    var smovList: List<SmovItem>,
    var highlightedSmovItem: SmovItem
)

data class Tag(
    var id: Int,
    var name: String,
)

data class Actor(
    var id: Int,
    var name: String,
)

data class ServerResult(
    var code: Int,
    var msg: String,
    var data: List<SmovItem> = listOf()
)

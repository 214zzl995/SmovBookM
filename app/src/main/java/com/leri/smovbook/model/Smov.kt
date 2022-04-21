package com.leri.smovbook.model

data class Smov(
    var id: Int? = 0,
    var name: String,  //云端
    var title: String, //标题
    var path: String,  //路径
    var realname: String,
    var len: Int,             //大小
    var created: Int,         //本地创建时间
    var modified: Int,        //本地修改时间
    var extension: String,    //拓展名
    var format: String,       //格式化后名称
    var release_time: String, //发行时间
    var duration: Int,        //时长
    var maker: String,        //商
    var maker_id: Int,        //商
    var publisher: String,    //方
    var publisher_id: Int,    //方
    var serie: String,        //系列
    var serie_id: Int,        //系列
    var director: String,     //导演
    var director_id: Int,     //导演
    var tags: List<Tag>,       //标签
    var actors: List<Actor>,   //演员
    var isch: Boolean,
    var thumbs_img: String,
    var main_img: String,
    var detail_img: List<String>,
)

data class Tag(
    var id: Int,
    var name: String,
)

data class Actor(
    var id: Int,
    var name: String,
)

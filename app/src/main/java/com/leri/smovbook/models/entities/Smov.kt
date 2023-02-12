package com.leri.smovbook.models.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity

/**
 * @Description: Smov实体类
 * @author DingWei
 * @version 12/8/2022 上午11:29
 * @param id 主键
 * @param name 名称
 * @param title 标题
 * @param path 路径
 * @param realname 文件名
 * @param len 大小
 * @param created 创建时间
 * @param modified 修改时间
 * @param extension 后缀名
 * @param format 格式化名称
 * @param release_time 发行时间
 * @param duration 影片时间
 * @param maker 制作者
 * @param maker_id 导演id
 * @param publisher 出版社
 * @param publisher_id 出版社id
 * @param serie 系列
 * @param serie_id 系列id
 * @param director 导演
 * @param director_id 导演id
 * @param tags 标签
 * @param actors 演员
 * @param isch 是否字幕
 * @param thumbs_img 缩略图
 * @param main_img 主图
 * @param detail_img 详情图
 *
 */
@Immutable
@Entity(primaryKeys = [("id")])
data class Smov(
    var id: Int = 0,
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
    var thumbs_img: ImageFile,
    var main_img: String,
    var detail_img: List<String> = listOf(),
    var sub_title: List<String> = listOf()
) {

    fun getThumbsImg(url: String): String {
        return "http://$url/smovbook/file/${this.realname}/img/thumbs_${this.name}.jpg"
    }

    fun getVideoUrl(url: String): String {
        return "http://$url/smovbook/file/${this.realname}/${this.realname}.${this.extension}"
    }

    fun getDefaultSub(url: String): String? {
        return if (this.sub_title.isEmpty()) {
            null
        } else {
            "http://$url/smovbook/file/${this.realname}/${this.sub_title.get(0)}"
        }
    }

}

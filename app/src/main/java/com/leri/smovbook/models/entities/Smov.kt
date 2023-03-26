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
 * @param filename 文件名
 * @param size 大小
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
    var filename: String,
    var seekname: String,
    var title: String,
    var path: String,
    var size: Long,
    var created: Long,
    var modified: Long,
    var extension: String,
    var release_time: String,
    var duration: Int,
    var makers: SmovAttr,
    var publisher: SmovAttr,
    var series: SmovAttr,
    var director: SmovAttr,
    var tags: List<Tag> = listOf(),
    var actors: List<Actor> = listOf(),
    var is_ch_sub: Boolean,
    var thumbs_img: ImageFile,
    var main_img: String,
    var detail_img: List<String> = listOf(),
    var subtitle: List<String> = listOf()
) {

    fun getThumbsImg(url: String): String {
        return "http://$url/smovbook/file/${this.filename}/img/thumbs_${this.name}.jpg"
    }

    fun getVideoUrl(url: String): String {
        return "http://$url/smovbook/file/${this.filename}/${this.filename}.${this.extension}"
    }

    fun getDefaultSub(url: String): String? {
        return if (this.subtitle.isEmpty()) {
            null
        } else {
            "http://$url/smovbook/file/${this.filename}/${this.subtitle.get(0)}"
        }
    }

}

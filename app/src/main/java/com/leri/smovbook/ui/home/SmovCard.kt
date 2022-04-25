package com.leri.smovbook.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leri.smovbook.R
import com.leri.smovbook.model.Actor
import com.leri.smovbook.model.SmovItem
import com.leri.smovbook.model.Tag
import com.leri.smovbook.ui.theme.ExtraLightBorder
import com.leri.smovbook.ui.theme.SmovBookMTheme
import com.skydoves.landscapist.glide.GlideImage


@Composable
fun SmovCard(
    smov: SmovItem
) {
    Box(
        modifier = Modifier
            .padding(3.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        val padding = 6.dp
        val density = LocalDensity.current
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .padding(padding)
        ) {
            Column {
                //暂时先使用这个控件 当前的使用不知道会不会对应用造成影响 多次嵌套的GlideImage 是否会造成内存溢出的情况也不知道 重写一个图片显示对我的挑战有点巨大
                GlideImage(
                    imageModel = smov.thumbs_img,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(170.dp) //.fillMaxWidth() 代表宽度占满布局
                        .height(230.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    previewPlaceholder = R.drawable.smov_ico,
                    loading = {
                        Box(modifier = Modifier.matchParentSize()) {
                            GlideImage(
                                imageModel = R.drawable.mobile_gif
                            )
                        }
                    }, failure = {
                        Box(modifier = Modifier.matchParentSize()) {
                            GlideImage(
                                imageModel = R.drawable.ic_error
                            )
                        }
                    }
                )
                Column(
                    Modifier
                        .padding(horizontal = 20.dp)
                        .height(60.dp),//.align(CenterHorizontally)  //这里的 align 代表当前元素在父元素的位置
                    horizontalAlignment = Alignment.CenterHorizontally,  //这里代表当前元素下的子元素所在的位置
                    verticalArrangement = Arrangement.Center  //同理
                ) {
                    Text(
                        text = smov.name,
                        modifier = Modifier,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun SmovItemPreview() {
    SmovBookMTheme() {
        Surface {
            Column {
                SmovCard(
                    smov = SmovItem(
                        0,
                        "test",
                        "test",
                        "test",
                        "test",
                        10010,
                        4564541,
                        4564654,
                        "mp4",
                        "test",
                        "test",
                        123,
                        "test",
                        3,
                        "test",
                        1,
                        "test",
                        1,
                        "test",
                        1,
                        listOf(Tag(1, "test")),
                        listOf(Actor(1, "test")),
                        isch = false,
                        "https://z4a.net/images/2022/04/16/wallhaven-1kq1jg.jpg",
                        "https://pc-index-skin.cdn.bcebos.com/616ca57261c714fceccb1717f6911516.jpg",
                        listOf("https://pc-index-skin.cdn.bcebos.com/616ca57261c714fceccb1717f6911516.jpg")

                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun CoilImagePreview() {

}
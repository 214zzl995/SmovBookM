package com.leri.smovbook.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.leri.smovbook.R
import com.leri.smovbook.model.SmovItem
import com.leri.smovbook.ui.data.testDataSin
import com.leri.smovbook.ui.theme.SmovBookMTheme
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

import android.app.ActivityOptions
import android.content.ActivityNotFoundException


@Composable
fun SmovCard(
    smov: SmovItem,
    mainUrl: String
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(3.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        val padding = 6.dp
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .padding(padding)
                .clickable {
                    coroutineScope.launch {
                        println("http://$mainUrl/SmovStatic/${smov.realname}/${smov.realname}.${smov.extension}")
                        val options: ActivityOptions = ActivityOptions.makeBasic()
                        val intent = Intent(Intent.ACTION_VIEW)
                        val type = "video/*"
                        val uri: Uri =
                            Uri.parse("http://$mainUrl/SmovStatic/${smov.realname}/${smov.realname}.${smov.extension}")
                        intent.setDataAndType(uri, type)

                        val title = "打开视频"
                        val chooser = Intent.createChooser(intent, title)
                        try {
                            startActivity(context, chooser, options.toBundle())
                        } catch (e: ActivityNotFoundException) {
                            // Define what your app should do if no activity can handle the intent.
                        }
                    }

                }
        ) {
            Column {
                //暂时先使用这个控件 当前的使用不知道会不会对应用造成影响 多次嵌套的GlideImage 是否会造成内存溢出的情况也不知道 重写一个图片显示对我的挑战有点巨大
                GlideImage(
                    imageModel = "http://$mainUrl/SmovStatic/${smov.realname}/img/thumbs_${smov.name}.jpg",
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
                    smov = testDataSin,
                    mainUrl = "127.0.0.1"
                )
            }
        }
    }
}

@Preview
@Composable
fun CoilImagePreview() {

}
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
import com.leri.smovbook.ui.data.testDataSin
import com.leri.smovbook.ui.theme.SmovBookMTheme
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import android.app.ActivityOptions
import android.content.ActivityNotFoundException
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.CircularProgressIndicator
import com.google.accompanist.flowlayout.FlowRow
import com.leri.smovbook.models.entities.Smov
import com.skydoves.landscapist.CircularReveal

//修改SmovCard为 https://developer.android.google.cn/reference/kotlin/androidx/compose/material3/package-summary#ElevatedCard(androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Shape,androidx.compose.material3.CardColors,androidx.compose.material3.CardElevation,kotlin.Function1)
@Composable
fun SmovCard(
    smov: Smov,
    mainUrl: String
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(15.dp, 0.dp, 15.dp, 0.dp) //21
            .clip(RoundedCornerShape(8.dp))
    ) {
        val padding = 6.dp
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            shadowElevation = 6.dp,
            modifier = Modifier
                .padding(padding)
        ) {
            //不定高会出现 页面图片消失 块突然从大到小 而且高度变低 回到顶部变快
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .clickable {
                    coroutineScope.launch {
                        println("http://$mainUrl/smovbook/file${smov.realname}/${smov.realname}.${smov.extension}")
                        val options: ActivityOptions = ActivityOptions.makeBasic()

                        val intent = Intent(Intent.ACTION_VIEW)
                        val type = "video/${smov.extension}"

                        intent.setPackage("com.mxtech.videoplayer.ad")
                        intent.putExtra("decode_mode", "4")
                        intent.putExtra("title", smov.name)
                        println(type)
                        val uri: Uri =
                            Uri.parse("http://$mainUrl/smovbook/file/${smov.realname}/${smov.realname}.${smov.extension}")
                        intent.setDataAndType(uri, type)

                        val title = "打开视频"
                        val chooser = Intent.createChooser(intent, title)

                        try {
                            startActivity(context, intent, options.toBundle())
                        } catch (e: ActivityNotFoundException) {
                            // Define what your app should do if no activity can handle the intent.
                        }
                    }

                }) {

                GlideImage(
                    imageModel = "http://$mainUrl/smovbook/file/${smov.realname}/img/thumbs_${smov.name}.jpg",
                    contentScale = ContentScale.FillWidth, //这个参数代表这张图像优先满足哪条边
                    //circularReveal = CircularReveal(duration = 250),
                    modifier = Modifier
                        .fillMaxWidth(0.7F)
                        .defaultMinSize(minHeight = 100.dp)
                        .animateContentSize()
                        .clip(RoundedCornerShape(3.dp, 3.dp, 3.dp, 3.dp)),
                    previewPlaceholder = R.drawable.smov_ico,
                    loading = {
                        Box(modifier = Modifier.matchParentSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }, 
                    failure = {
                        Box(modifier = Modifier.matchParentSize()) {
                            GlideImage(
                                imageModel = R.drawable.ic_error
                            )
                        }
                    }
                )
                Column(
                    Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    //.fillMaxSize()//.align(CenterHorizontally)  //这里的 align 代表当前元素在父元素的位置
                    horizontalAlignment = Alignment.Start,  //这里代表当前元素下的子元素所在的位置
                    verticalArrangement = Arrangement.Top  //同理
                ) {
                    Text(
                        text = smov.name,
                        modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Left
                    )

                    FlowRow {
                        for (actor in smov.actors) {
                            Text(
                                text = actor.name + " ",
                                modifier = Modifier,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )

                        }
                    }

                    Text(
                        text = smov.release_time,
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Left
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
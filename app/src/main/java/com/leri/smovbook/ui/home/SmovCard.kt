package com.leri.smovbook.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leri.smovbook.R
import com.leri.smovbook.ui.data.testDataSin
import com.leri.smovbook.ui.theme.SmovBookMTheme
import kotlinx.coroutines.launch
import android.app.ActivityOptions
import android.content.ActivityNotFoundException
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.max
import androidx.core.content.ContextCompat.startActivity
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.google.accompanist.flowlayout.FlowRow
import com.leri.smovbook.config.ThirdPartyPlayer
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.ui.LocalOkHttpClient

@Composable
fun SmovCard(
    smov: Smov,
    mainUrl: String,
    openSmovDetail: (Long, String) -> Unit,
    thirdPartyPlayer: ThirdPartyPlayer,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var imageSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(15.dp, 0.dp, 15.dp, 0.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        val padding = 8.dp
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            shadowElevation = 5.dp,
            modifier = Modifier.padding(padding)
        ) {
            //不定高会出现 页面图片消失 块突然从大到小 而且高度变低 回到顶部变快
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .clickable {
                    coroutineScope.launch {
                        openSmovDetail(smov.id.toLong(), smov.name)
                    }

                }) {

                val url =
                    "http://127.0.0.1/smovbook/file/${smov.filename}/img/thumbs_${smov.name}.jpg"

                //这里要取巨gb顶部的一个值 我麻了
                val imageLoader =
                    ImageLoader.Builder(context).okHttpClient(LocalOkHttpClient.current).build()
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth(0.7F)
                        .aspectRatio(smov.thumbs_img.size.width.toFloat() / smov.thumbs_img.size.height)
                        .defaultMinSize(minHeight = 200.dp)
                        .animateContentSize()
                        .clip(RoundedCornerShape(3.dp, 3.dp, 3.dp, 3.dp)),
                    model = ImageRequest
                        .Builder(LocalContext.current).crossfade(true).data(url)
                        .scale(Scale.FILL)
                        .precision(Precision.EXACT)
                        .build(),
                    imageLoader = imageLoader,
                    contentScale = ContentScale.FillWidth,
                    loading = {
                        Box(modifier = Modifier.matchParentSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    },
                    error = {
                        Box(modifier = Modifier.matchParentSize()) {
                            AsyncImage(
                                model = R.drawable.ic_error, contentDescription = null
                            )
                        }
                    },
                    onSuccess = {
                        //触发下面column的重组
                        imageSuccess = true
                    },
                    contentDescription = stringResource(R.string.content_description)
                )


                // 这里真正的问题是没有触发重组
                Column(
                    Modifier
                        .aspectRatio(((smov.thumbs_img.size.width.toFloat() * (0.3 / 0.7)) / smov.thumbs_img.size.height).toFloat())
                        .padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = smov.name,
                            modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Left
                        )

                        Column {
                            for (actor in smov.actors.subList(0, minOf(smov.actors.size, 3))) {
                                Text(
                                    text = actor.name,
                                    modifier = Modifier,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )

                            }

                            if (smov.actors.size > 3) {
                                Text(
                                    text = "...",
                                    modifier = Modifier,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    Column {
                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                coroutineScope.launch {
                                    val options: ActivityOptions = ActivityOptions.makeBasic()
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    val type = "video/${smov.extension}"
                                    val uri: Uri =
                                        Uri.parse("http://$mainUrl/smovbook/file/${smov.filename}/${smov.filename}.${smov.extension}")
                                    intent.setDataAndType(uri, type)
                                    intent.addCategory(Intent.CATEGORY_BROWSABLE)
                                    if (thirdPartyPlayer.thirdPartyPlayerName == "") {
                                        val title = "打开视频"
                                        val chooser = Intent.createChooser(intent, title)

                                        try {
                                            startActivity(context, chooser, options.toBundle())
                                        } catch (e: ActivityNotFoundException) {
                                            // Define what your app should do if no activity can handle the intent.
                                        }
                                    } else {
                                        intent.setPackage(thirdPartyPlayer.thirdPartyPlayerPackage)
                                        intent.putExtra("title", smov.name)
                                        startActivity(context, intent, options.toBundle())
                                    }


                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PlayCircleFilled,
                                contentDescription = "Quick PLayer",
                                tint = MaterialTheme.colorScheme.primary
                            )

                        }

                        Box(modifier = Modifier) {
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
                    mainUrl = "127.0.0.1",
                    openSmovDetail = { _, _ -> },
                    thirdPartyPlayer = ThirdPartyPlayer.getDefaultInstance()
                )
            }
        }
    }
}

@Preview
@Composable
fun CoilImagePreview() {

}
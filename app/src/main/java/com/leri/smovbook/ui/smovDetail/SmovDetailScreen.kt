package com.leri.smovbook.ui.smovDetail

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.flowlayout.FlowRow
import com.leri.smovbook.models.entities.DetailModel
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.models.network.NetworkState
import com.leri.smovbook.ui.components.MainPage
import com.leri.smovbook.ui.data.testDataSin
import com.leri.smovbook.ui.player.SmovVideoState
import com.leri.smovbook.ui.player.SmovVideoView


//实现图片轮播 暂时还没有理想的方案
@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmovDetailScreen(
    smov: Smov,
    smovName: String,
    serverUrl: String,
    onBack: () -> Unit,
    pageState: NetworkState,
    modifier: Modifier = Modifier,
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val subTitle = smov.getDefaultSub(serverUrl)
    val url = smov.getVideoUrl(serverUrl)
    val cover = smov.getThumbsImg(serverUrl)

    val smovVideoView = rememberVideoPlayerState(title = "", url = url, subTitle)

    //当url发生更改 且smov不为空时触发初始化
    LaunchedEffect(key1 = url) {
        if (smov.id != 0) {
            smovVideoView.smovInit(
                url = url, title = smovName, subTitle = subTitle, cover = cover
            )
        }
    }

    BackHandler {
        if (smovVideoView.isIfCurrentIsFullscreen) {
            smovVideoView.onBackFullscreen()
        } else {
            onBack()
            smovVideoView.release()
        }
    }

    Scaffold(modifier = modifier
        .fillMaxSize()
        .windowInsetsPadding(
            WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
        ).nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SmovDetailAppBar(
                scrollBehavior = scrollBehavior,
                title = smovName,
                onBack = onBack,
                modifier = Modifier
            )
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopStart
        ) {
            MainPage(pageState) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .verticalScroll(scrollState)
                        .fillMaxSize()
                ) {
                    VideoPlayer(smovVideoView = smovVideoView)
                    VideoDetail(smov = smov)
                }
            }

        }
    }
}

@Composable
fun VideoPlayer(smovVideoView: SmovVideoView) {
    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(220.dp)
        .animateContentSize()
        .background(Color.White),
        factory = {
            smovVideoView
        })
}

@Composable
fun VideoDetail(smov: Smov) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NameValueAlone(name = "标题", value = smov.title)
            }
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NameValueMultiple(name = "演员", values = smov.actors)
            }
        }


        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NameValueMultiple(name = "标签", values = smov.tags)
            }
        }


        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 20.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                NameValueAlone(name = "发行时间", value = smov.release_time)
                NameValueAlone(name = "制作", value = smov.makers.name)
                NameValueAlone(name = "导演", value = smov.director.name)
                NameValueAlone(name = "出版社", value = smov.publisher.name)
                NameValueAlone(name = "系列", value = smov.series.name)
            }
        }
    }

}

@Composable
fun NameValueAlone(name: String, value: String) {
    if (value != "") {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = name,
                modifier = Modifier.fillMaxWidth(),
                style = nameStyle
            )
            Text(
                text = value,
                modifier = Modifier.fillMaxWidth(),
                style = valueStyle
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameValueMultiple(name: String, values: List<DetailModel>) {
    if (values.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = name,
                modifier = Modifier.fillMaxWidth(),
                style = nameStyle
            )
            FlowRow(mainAxisSpacing = 6.dp, crossAxisSpacing = (-10).dp) {
                for (value in values) {
                    ElevatedSuggestionChip(
                        onClick = { /* Do something! */ },
                        label = { Text(text = value.name, style = valueStyle) }
                    )
                }
            }

        }
    }

}


private val nameStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.W400,
    fontSize = 11.sp,
    lineHeight = 11.0.sp,
    letterSpacing = 0.4.sp,
)

private val valueStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.W500,
    fontSize = 16.sp,
    lineHeight = 19.0.sp,
    letterSpacing = 0.4.sp,
)


@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun rememberVideoPlayerState(
    title: String, url: String, subTitle: String?,
): SmovVideoView {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    return rememberSaveable(context,
        coroutineScope,
        saver = object : Saver<SmovVideoView, SmovVideoState> {
            @RequiresApi(Build.VERSION_CODES.R)
            override fun restore(value: SmovVideoState): SmovVideoView {
                return SmovVideoView(
                    context = context,
                    title = value.title,
                    url = value.url,
                    subTitle = value.subTitle
                )
            }

            override fun SaverScope.save(value: SmovVideoView): SmovVideoState {
                return SmovVideoState(
                    isIfCurrentIsFullscreen = value.isIfCurrentIsFullscreen,
                    title = value.title,
                    url = value.url,
                    subTitle = value.subTitle
                )
            }
        },
        init = {
            SmovVideoView(
                context = context, title = title, url = url, subTitle = subTitle
            )
        })
}


fun Context.getActivity(): AppCompatActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is AppCompatActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

@Preview
@Composable
fun SmovDetailPreview() {
    VideoDetail(smov = testDataSin)
}






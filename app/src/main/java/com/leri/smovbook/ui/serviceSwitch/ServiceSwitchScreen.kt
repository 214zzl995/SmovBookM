package com.leri.smovbook.ui.serviceSwitch

import android.view.MotionEvent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AllInclusive
import androidx.compose.material.icons.outlined.Input
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip


import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.R
import com.leri.smovbook.ui.theme.SmovBookMTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ServiceSwitchScreen(
    modifier: Modifier = Modifier,
    serviceUrl: String,
    historyUrl: List<String>,
    openBarScann: () -> Unit,
    changeServiceUrl: (String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var addDialogVisible by remember { mutableStateOf(false) }

    val changeAddDialogVisible = {
        addDialogVisible = !addDialogVisible
    }

    val editDialogVisible = remember { mutableStateOf(false) }

    val changeEditDialogVisible = {
        editDialogVisible.value = !editDialogVisible.value
    }

    if (editDialogVisible.value) {
        var addUrl by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }

        var addPort by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }

        val interactionSource = remember { MutableInteractionSource() }

        val (urlFocus, portFocus) = FocusRequester.createRefs()

        LaunchedEffect(Unit) {
            urlFocus.requestFocus()
        }
        AlertDialog(
            onDismissRequest = changeEditDialogVisible,
            confirmButton = {
                TextButton(
                    onClick = {
                        changeEditDialogVisible()
                        val url = addUrl.text + ":" + if (addPort.text == "") "80" else addPort.text
                        changeServiceUrl(url)
                    },
                    enabled = (addUrl.text.isValidURL() || addUrl.text.isValidIPAddress()) && addPort.text.isValidPort()
                )
                {
                    Text(text = "确认")
                }
            },
            dismissButton = {
                TextButton(onClick = changeEditDialogVisible) {
                    Text(text = "取消")
                }
            },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    //输入框
                    OutlinedTextField(
                        modifier = Modifier
                            .focusRequester(urlFocus)
                            .focusProperties {
                                next = portFocus
                            }
                            .fillMaxWidth(0.65f),
                        value = addUrl,
                        onValueChange = { addUrl = it },
                        //判断url是否合法
                        isError = addUrl.text.isEmpty() || !(addUrl.text.isValidURL() || addUrl.text.isValidIPAddress()),
                        label = {
                            Row {
                                Text(
                                    "地址",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                Box(modifier = Modifier.width(3.dp)) {}
                                Icon(
                                    imageVector = Icons.Outlined.Link,
                                    contentDescription = "Localized description",
                                )
                            }

                        },
                    )

                    //判断端口是否合法
                    Text(
                        text = ":",
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Box(modifier = Modifier.padding(top = 9.dp)) {
                        OutlinedTextField(value = addPort,
                            interactionSource = interactionSource,
                            onValueChange = { addPort = it },
                            isError = !addPort.text.isValidPort(),
                            placeholder = { Text("80") },
                            modifier = Modifier
                                .focusRequester(portFocus)
                                .focusProperties {
                                    next = urlFocus
                                    canFocus =
                                        addUrl.text.isNotEmpty() && (addUrl.text.isValidURL() || addUrl.text.isValidIPAddress())
                                })
                    }
                }
            },
            title = {
                Text(text = "添加Url")
            },
        )

    }


    Scaffold(modifier = modifier
        .nestedScroll(scrollBehavior.nestedScrollConnection)
        .clickable(indication = null, interactionSource = remember {
            MutableInteractionSource()
        }) {
            if (addDialogVisible) {
                changeAddDialogVisible()
            }
        }, topBar = {
        MediumTopAppBar(title = {
            Text(
                "服务",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }, navigationIcon = {
            Row(modifier = Modifier.padding(start = 13.dp)) {
                Icon(
                    modifier = Modifier.height(24.dp),
                    imageVector = Icons.Outlined.AllInclusive,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = stringResource(id = R.string.info)
                )
            }
        }, actions = {}, scrollBehavior = scrollBehavior
        )
    }, content = { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(all = 16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(count = historyUrl.size) {
                Url(
                    url = historyUrl[it],
                    check = serviceUrl == checkPortJoin(historyUrl[it]),
                    modifier = Modifier.clip(
                        MaterialTheme.shapes.medium
                    ).combinedClickable(
                        onClick = { changeServiceUrl(historyUrl[it]) },
                        onLongClick = {
                            changeServiceUrl(historyUrl[it])
                        },
                    ),
                )
            }

        }
    }, floatingActionButton = {
        AddUrlFloatingActionButton(
            openBarScann = openBarScann,
            openEditInput = changeEditDialogVisible,
            addDialogVisible,
        ) {
            changeAddDialogVisible()
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlEditSheet(
    openSheet: Boolean,
    closeSheet: () -> Unit,
    openEditInput: () -> Unit,
    url:String
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberSheetState(
        skipHalfExpanded = true
    )

    if (openSheet) {
        ModalBottomSheet(
            onDismissRequest = closeSheet,
            sheetState = bottomSheetState,
        ) {
            //操作栏 包括修改和删除
        }

    }
}


fun String.isValidIPAddress(): Boolean {
    // IP 地址的正则表达式
    val ipAddressRegex =
        """^([1-9]|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3}${'$'}""".toRegex()

    return matches(ipAddressRegex)
}

fun String.isValidURL(): Boolean {
    // URL 的正则表达式
    val urlRegex =
        """^([a-zA-Z0-9]([a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])?\.)+[a-zA-Z]{2,6}${'$'}""".toRegex()

    return matches(urlRegex)
}

fun String.isValidPort(): Boolean {
    //判断端口大小大于 0 小于 65535
    if (this == "") {
        return true
    }
    return toInt() in 0..65535
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Url(
    url: String,
    check: Boolean,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (check) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    RichTooltipBox(
        text = { Text(text = "点击切换服务")},
    ){
        Box(
            contentAlignment = Alignment.Center,

            modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, borderColor, MaterialTheme.shapes.medium)
                .padding(vertical = 16.dp)
        ) {

            Text(
                text = url,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            //尾部显示确认图标
            if (check) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = stringResource(id = R.string.content_description),
                    modifier = Modifier
                        .height(21.dp)
                        .padding(end = 16.dp)
                        .align(Alignment.CenterEnd),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

    }

}

@Composable
private fun AddUrlFloatingActionButton(
    openBarScann: () -> Unit,
    openEditInput: () -> Unit,
    addDialogVisible: Boolean,
    changeAddDialogVisible: () -> Unit,
) {
    AnimatedContent(addDialogVisible, label = "") {
        if (!it) {

            return@AnimatedContent FloatingActionButton(
                onClick = changeAddDialogVisible,
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
                    .height(48.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    modifier = Modifier
                        .height(24.dp)
                        .rotate(180f),
                    contentDescription = "Localized description",
                )
            }
        } else {
            return@AnimatedContent Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp,
                    ),
                    modifier = Modifier
                        .width(180.dp)
                        .pointerInput(Unit) { detectTapGestures { } },
                ) {

                    Column(modifier = Modifier) {
                        AddUrlFloatingType(icon = Icons.Outlined.QrCode, text = "扫描二维码") {
                            changeAddDialogVisible()
                            openBarScann()
                        }
                        AddUrlFloatingType(icon = Icons.Outlined.Input, text = "输入地址") {
                            changeAddDialogVisible()
                            openEditInput()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddUrlFloatingType(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(60.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = R.string.content_description),
            modifier = Modifier.align(Alignment.CenterVertically),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp)
        )
    }
}


fun checkPortJoin(url: String): String {
    val urlList = url.split(":")
    if (urlList.size == 1) {
        return urlList[0] + ":80"
    }
    return url
}

@Preview
@Composable
fun UrlPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        Url(
            url = "https://www.baidu.com", check = false
        )
    }
}

@Preview
@Composable
fun CheckUrlPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        Url(
            url = "https://www.baidu.com", check = true
        )
    }
}

@Preview
@Composable
fun AddUrlFloatingActionButtonPreview() {
    SmovBookMTheme() {
        AddUrlFloatingActionButton(openBarScann = {},
            openEditInput = {},
            false,
            changeAddDialogVisible = {})
    }
}

@Preview
@Composable
fun AddUrlFloatingActionDialogPreview() {
    SmovBookMTheme() {
        AddUrlFloatingActionButton(openBarScann = {},
            openEditInput = {},
            true,
            changeAddDialogVisible = {})
    }
}

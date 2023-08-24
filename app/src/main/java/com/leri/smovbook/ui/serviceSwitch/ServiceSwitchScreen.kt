package com.leri.smovbook.ui.serviceSwitch

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AllInclusive
import androidx.compose.material.icons.outlined.Input
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.R
import com.leri.smovbook.ui.theme.SmovBookMTheme

@OptIn(ExperimentalMaterial3Api::class)
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
        var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }

        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        AlertDialog(
            onDismissRequest = changeEditDialogVisible,
            confirmButton = {
                TextButton(onClick = {
                    changeEditDialogVisible()
                    changeServiceUrl(text.text)
                }) {
                    Text(text = "确认")
                }
            },
            dismissButton = {
                TextButton(onClick = changeEditDialogVisible) {
                    Text(text = "取消")
                }
            },
            text = {
                //输入框
                OutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("地址") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Link,
                            contentDescription = "Localized description"
                        )
                    }
                )
            },
            title = {
                Text(text = "添加Url")
            },
        )

    }


    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }) {
                if (addDialogVisible) {
                    changeAddDialogVisible()
                }
            },
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "服务",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    Row(modifier = Modifier.padding(start = 13.dp)) {
                        Icon(
                            modifier = Modifier.height(24.dp),
                            imageVector = Icons.Outlined.AllInclusive,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = stringResource(id = R.string.info)
                        )
                    }
                },
                actions = {},
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(all = 16.dp),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(count = historyUrl.size) {
                    Url(
                        url = historyUrl[it],
                        check = serviceUrl == checkPortJoin(historyUrl[it]),
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember {
                                MutableInteractionSource()
                            }) {
                            changeServiceUrl(historyUrl[it])
                        },
                    )
                }

            }
        },
        floatingActionButton = {
            AddUrlFloatingActionButton(
                openBarScann = openBarScann,
                openEditInput = changeEditDialogVisible,
                addDialogVisible,
            ) {
                changeAddDialogVisible()
            }
        }
    )
}

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
                onClick = { changeAddDialogVisible() },
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp),
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Localized description",
                )
            }
        } else {
            return@AnimatedContent Card(
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
            modifier = Modifier
                .align(Alignment.CenterVertically),
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
            url = "https://www.baidu.com",
            check = false
        )
    }
}

@Preview
@Composable
fun CheckUrlPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        Url(
            url = "https://www.baidu.com",
            check = true
        )
    }
}

@Preview
@Composable
fun AddUrlFloatingActionButtonPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        AddUrlFloatingActionButton(
            openBarScann = {},
            openEditInput = {},
            false,
            changeAddDialogVisible = {}
        )
    }
}

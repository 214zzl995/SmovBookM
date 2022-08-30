package com.leri.smovbook.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.R
import com.leri.smovbook.ui.clearFocusOnKeyboardDismiss
import com.leri.smovbook.ui.theme.changeServerUrlBak


@Composable
fun ChangeServerUrlDialog(
    openBarScann: () -> Unit,
    visible: Boolean,
    close: () -> Unit,
    changeServerUrl: (String) -> Unit
) {
    var editInputVisible by remember { mutableStateOf(false) }
    var url by rememberSaveable { mutableStateOf("") }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(), exit = fadeOut()
    ) {
        Surface(color = changeServerUrlBak) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(0.7f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(modifier = Modifier.fillMaxHeight(0.8f)) {
                        AddUrlFloatingActionButton(
                            hide = editInputVisible,
                            openBarScann = openBarScann,
                            openEditInput = { editInputVisible = true })

                        AddUrlInput(
                            editInputVisible = editInputVisible,
                            url = url,
                            changeUrlInComposable = { url = it }
                        )
                    }

                    Box {
                        AddUrlDialogOperate(
                            editInputVisible = editInputVisible,
                            close = close,
                            closeEditInput = { editInputVisible = false },
                            url = url,
                            changeServerUrl = changeServerUrl
                        )

                    }
                }

            }
        }
    }
}

@Composable
private fun AddUrlFloatingActionButton(
    hide: Boolean,
    openBarScann: () -> Unit,
    openEditInput: () -> Unit
) {
    AnimatedVisibility(
        visible = !hide,
        enter = slideInVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    openBarScann()
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_qr_scan_line),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                            .height(21.dp),
                        contentDescription = stringResource(id = R.string.search)
                    )
                },
                text = { Text(text = "扫描二维码") },
            )

            ExtendedFloatingActionButton(
                onClick = { openEditInput() },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_enter_the_keyboard),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                            .height(21.dp),
                        contentDescription = stringResource(id = R.string.search)
                    )
                },
                text = { Text(text = "输入框输入") },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddUrlDialogOperateButtonTwo(
    editInputVisible: Boolean,
    close: () -> Unit,
    closeEditInput: () -> Unit
) {
    var height by remember {
        mutableStateOf(0.dp)
    }
    val localDensity = LocalDensity.current
    val cancelOffset by animateDpAsState(
        targetValue = if (editInputVisible) 0.dp else -height
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                height = with(localDensity) { coordinates.size.height.toDp() }
            }
            .offset(x = cancelOffset),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .width(height),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = editInputVisible,
                enter = /*slideInHorizontally() +*/ fadeIn(),
                exit = /*slideOutHorizontally() +*/ fadeOut()
            ) {
                FilledIconButton(onClick = { closeEditInput() }, enabled = editInputVisible) {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = stringResource(id = R.string.search)
                    )
                }
            }
        }

        FilledIconButton(modifier = Modifier,
            onClick = { close() }) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = stringResource(id = R.string.search)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddUrlDialogOperate(
    editInputVisible: Boolean,
    close: () -> Unit,
    closeEditInput: () -> Unit,
    changeServerUrl: (String) -> Unit,
    url: String
) {

    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        AnimatedVisibility(
            visible = editInputVisible,
            enter = slideInVertically() + fadeIn(), exit = slideOutVertically() + fadeOut()
        ) {
            FilledIconButton(onClick = { closeEditInput() }, enabled = editInputVisible) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(id = R.string.search)
                )
            }
        }

        FilledIconButton(modifier = Modifier,
            onClick = { close() }) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = stringResource(id = R.string.search)
            )
        }

        AnimatedVisibility(
            visible = editInputVisible,
            enter = slideInVertically() + fadeIn(), exit = slideOutVertically() + fadeOut()
        ) {
            FilledIconButton(modifier = Modifier, enabled = url != "",
                onClick = { changeServerUrl(url) }) {
                Icon(
                    Icons.Outlined.Check,
                    contentDescription = stringResource(id = R.string.search)
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AddUrlInput(
    editInputVisible: Boolean,
    changeUrlInComposable: (String) -> Unit,
    url: String
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    AnimatedVisibility(
        visible = editInputVisible,
        enter = slideInVertically() + fadeIn(), exit = slideOutVertically() + fadeOut()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //此处 keyboardActions keyboardOptions的意思是 当ime发出 enter的指令后 隐藏键盘
            OutlinedTextField(
                value = url,
                onValueChange = { changeUrlInComposable(it) },
                label = { Text("输入新的url") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clearFocusOnKeyboardDismiss(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AddUrlInputNoneCheck(
    editInputVisible: Boolean,
    changeServerUrl: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    AnimatedVisibility(
        visible = editInputVisible,
        enter = slideInVertically() + fadeIn(), exit = slideOutVertically() + fadeOut()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //此处 keyboardActions keyboardOptions的意思是 当ime发出 enter的指令后 隐藏键盘
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("输入新的url") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clearFocusOnKeyboardDismiss(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            changeServerUrl(text)
                            text = ""
                            keyboardController?.hide()
                        },
                        enabled = text != ""
                    ) {
                        val visibilityIcon = Icons.TwoTone.Check

                        Icon(imageVector = visibilityIcon, contentDescription = "缓存")
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun ChangeServerUrlDialogPrev() {
    ChangeServerUrlDialog(close = {}, openBarScann = {}, visible = true, changeServerUrl = {})
}

@Preview
@Composable
fun AddUrlDialogOperatePrev() {
    AddUrlDialogOperate(
        editInputVisible = true,
        close = {},
        closeEditInput = {},
        changeServerUrl = {},
        url = "127"
    )
}
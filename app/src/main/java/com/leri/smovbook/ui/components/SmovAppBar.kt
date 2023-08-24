package com.leri.smovbook.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.R
import com.leri.smovbook.ui.FunctionalityNotAvailablePopup
import com.leri.smovbook.ui.theme.Shapes
import com.leri.smovbook.ui.theme.SmovBookMTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelNameBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    onRefreshSmovData: () -> Unit = { },
    serviceUrl: String,
) {
    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }

    var serverSelectShow by remember { mutableStateOf(false) }

    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer
    )

    Column(modifier = Modifier) {

        MediumTopAppBar(modifier = modifier, actions = {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = { onRefreshSmovData() })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.info)
            )
        }, title = {
            Row(
                modifier = Modifier.clickable(indication = null, interactionSource = remember {
                    MutableInteractionSource()
                }) {
                    //打开地址选择栏
                    serverSelectShow = !serverSelectShow
                },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = serviceUrl, style = MaterialTheme.typography.titleMedium
                )
            }
        }, scrollBehavior = scrollBehavior, colors = foregroundColors, navigationIcon = {


        })

        BackHandler(
            onBack = {
                serverSelectShow = !serverSelectShow
            }, enabled = serverSelectShow
        )


    }
}


@OptIn(InternalAnimationApi::class, ExperimentalAnimationApi::class)
@Composable
private fun <T> Transition<T>.targetEnterExit(
    visible: (T) -> Boolean,
    targetState: T,
): EnterExitState = key(this) {

    if (this.isSeeking) {
        if (visible(targetState)) {
            EnterExitState.Visible
        } else {
            if (visible(this.currentState)) {
                EnterExitState.PostExit
            } else {
                EnterExitState.PreEnter
            }
        }
    } else {
        val hasBeenVisible = remember { mutableStateOf(false) }
        if (visible(currentState)) {
            hasBeenVisible.value = true
        }
        if (visible(targetState)) {
            EnterExitState.Visible
        } else {
            // If never been visible, visible = false means PreEnter, otherwise PostExit
            if (hasBeenVisible.value) {
                EnterExitState.PostExit
            } else {
                EnterExitState.PreEnter
            }
        }
    }
}

@Composable
fun SmovUrl(
    modifier: Modifier = Modifier,
    url: String,
    textDecoration: TextDecoration? = null,
    changeServerUrl: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 2.dp)
            .height(35.dp)
            .clip(Shapes.small)
            .background(MaterialTheme.colorScheme.inversePrimary)
            .clickable { changeServerUrl() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            url, modifier = Modifier, textDecoration = textDecoration
        )

    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppBarPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        ChannelNameBar(serviceUrl = "127.0.0.1")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppBarPreviewDark() {
    SmovBookMTheme(isDarkTheme = true) {
        ChannelNameBar(serviceUrl = "127.0.0.1")
    }
}

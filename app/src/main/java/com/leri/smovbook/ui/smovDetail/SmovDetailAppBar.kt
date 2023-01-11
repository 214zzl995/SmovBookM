package com.leri.smovbook.ui.smovDetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.ui.theme.SmovBookMTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmovDetailAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    title: String,
    onBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {

    val foregroundColors = TopAppBarDefaults.smallTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.inverseOnSurface
    )



    Box(modifier = Modifier) {
        TopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            modifier = modifier,
            navigationIcon = {
                Row(modifier = Modifier
                    .padding(7.dp)
                    .clickable(indication = null, interactionSource = remember {
                        MutableInteractionSource()
                    }) {
                        onBack()
                    }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Localized description"
                    )
                }
            },
            actions = actions,
            colors = foregroundColors,
            scrollBehavior = scrollBehavior
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmovDetailMediumAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String,
    onBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {

    Box(modifier = Modifier) {
        MediumTopAppBar(
            modifier = modifier,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                Row(modifier = Modifier
                    .padding(7.dp)
                    .clickable(indication = null, interactionSource = remember {
                        MutableInteractionSource()
                    }) {
                        onBack()
                    }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Localized description"
                    )
                }

            },
            actions = actions,
            scrollBehavior = scrollBehavior
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SmovDetailMediumAppBarPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        SmovDetailMediumAppBar(title = "SmovBook")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppBarPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        SmovDetailAppBar(title = "SmovBook")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppBarPreviewDark() {
    SmovBookMTheme(isDarkTheme = true) {
        SmovDetailAppBar(title = "SmovBook")
    }
}

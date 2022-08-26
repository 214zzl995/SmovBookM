package com.leri.smovbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.R
import com.leri.smovbook.ui.theme.SmovBookMTheme

@Composable
fun SmovAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val backgroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors()
    val backgroundColor = backgroundColors.containerColor(
        scrollFraction = scrollBehavior?.scrollFraction ?: 0f
    ).value
    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent
    )
    Box(modifier = Modifier.background(backgroundColor)) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            actions = actions,
            title = title,
            scrollBehavior = scrollBehavior,
            colors = foregroundColors,
            navigationIcon = {
                BarIcon(
                    contentDescription = stringResource(id = R.string.navigation_drawer_open),
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(onClick = onNavIconPressed)
                        .padding(21.dp)
                )
            }
        )

    }
}

@Preview
@Composable
fun AppBarPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        SmovAppBar(title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SmovBook",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "127.0.0.1:8080",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        })
    }
}

@Preview
@Composable
fun AppBarPreviewDark() {
    SmovBookMTheme(isDarkTheme = true) {
        SmovAppBar(title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SmovBook",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "127.0.0.1:8080",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        })
    }
}

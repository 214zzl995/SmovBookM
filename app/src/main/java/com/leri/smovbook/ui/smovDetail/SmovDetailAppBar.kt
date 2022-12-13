package com.leri.smovbook.ui.smovDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.leri.smovbook.ui.theme.SmovBookMTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmovDetailAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String,
    onBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {

    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor =  MaterialTheme.colorScheme.inverseOnSurface,
        scrolledContainerColor = Color.Transparent
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
                IconButton(onClick = onBack) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Localized description"
                    )
                }
            },
            actions = actions,
            colors = foregroundColors
        )

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

package com.leri.smovbook.ui.smovDetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    val foregroundColors = TopAppBarDefaults.smallTopAppBarColors(
        containerColor =  MaterialTheme.colorScheme.surface,
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
                IconButton(onClick = onBack) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
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
